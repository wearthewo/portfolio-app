package com.enterprise.portfolio.filter;

import com.enterprise.portfolio.service.MetricsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String[] EXCLUDE_URLS = {
        "/actuator/health",
        "/actuator/prometheus",
        "/v3/api-docs",
        "/swagger-ui"
    };

    private final MetricsService metricsService;

    public RequestResponseLoggingFilter(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String excludeUrl : EXCLUDE_URLS) {
            if (path.startsWith(excludeUrl)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Generate or get request ID
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        // Wrap request and response to allow multiple reads
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        // Add request ID to response headers
        wrappedResponse.addHeader(REQUEST_ID_HEADER, requestId);

        long startTime = System.currentTimeMillis();
        
        try {
            // Log request
            if (log.isDebugEnabled()) {
                logRequest(wrappedRequest, requestId);
            }
            
            // Process the request
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            
        } finally {
            // Calculate processing time
            long duration = System.currentTimeMillis() - startTime;
            
            // Log response
            if (log.isDebugEnabled()) {
                logResponse(wrappedResponse, requestId, duration);
            }
            
            // Record metrics
            recordMetrics(request, response, duration);
            
            // Ensure response is written back to the client
            wrappedResponse.copyBodyToResponse();
        }
    }
    
    private void logRequest(ContentCachingRequestWrapper request, String requestId) throws IOException {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.append(headerName).append(": ").append(headerValue).append("\n");
        }
        
        String requestBody = "";
        if (request.getContentLength() > 0) {
            requestBody = IOUtils.toString(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        }
        
        log.debug("\n=== Request [{}] ===\n{} {} {}\n{}\n{}",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                request.getProtocol(),
                headers.toString().trim(),
                requestBody);
    }
    
    private void logResponse(ContentCachingResponseWrapper response, String requestId, long duration) throws IOException {
        String responseBody = "";
        if (response.getContentSize() > 0) {
            responseBody = IOUtils.toString(response.getContentAsByteArray(), StandardCharsets.UTF_8);
            // Reset response content after reading
            response.copyBodyToResponse();
        }
        
        log.debug("\n=== Response [{}] ({} ms) ===\nStatus: {}\n{}\n",
                requestId,
                duration,
                response.getStatus(),
                responseBody);
    }
    
    private void recordMetrics(HttpServletRequest request, HttpServletResponse response, long duration) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        int status = response.getStatus();
        
        // Record request count
        metricsService.incrementCounter("http.requests.total", 
            "method", method, 
            "path", path, 
            "status", String.valueOf(status));
        
        // Record request duration
        metricsService.recordTime("http.request.duration", 
            duration, 
            java.util.concurrent.TimeUnit.MILLISECONDS,
            "method", method,
            "path", path);
            
        // Record response status
        String statusSeries = String.valueOf(status / 100) + "xx";
        metricsService.incrementCounter("http.responses", 
            "status", statusSeries);
    }
}
