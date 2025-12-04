package com.enterprise.portfolio.service;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToDoubleFunction;

@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, Counter> counters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Timer> timers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> gauges = new ConcurrentHashMap<>();

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Increment a counter with the given name and tags
     */
    public void incrementCounter(String name, String... tags) {
        String key = name + String.join("_", tags);
        counters.computeIfAbsent(key, k -> 
            Counter.builder(name)
                .tags(tags)
                .register(meterRegistry)
        ).increment();
    }

    /**
     * Record the time taken for an operation
     */
    public void recordTime(String name, long time, TimeUnit unit, String... tags) {
        String key = name + String.join("_", tags);
        timers.computeIfAbsent(key, k ->
            Timer.builder(name)
                .tags(tags)
                .publishPercentiles(0.5, 0.95, 0.99) // median, 95th percentile, 99th percentile
                .publishPercentileHistogram()
                .register(meterRegistry)
        ).record(time, unit);
    }

    /**
     * Set a gauge value
     */
    /**
     * Set a gauge value with the given name, value, and tags
     * @param name The name of the gauge
     * @param value The value to set
     * @param tags Key-value pairs of tags (must be even number of arguments)
     */
    public void setGauge(String name, int value, String... tags) {
        if (tags.length % 2 != 0) {
            throw new IllegalArgumentException("Tags must be provided as key-value pairs");
        }
        
        String key = name + String.join("_", tags);
        
        // Get or create the gauge
        AtomicInteger gaugeValue = gauges.computeIfAbsent(key, k -> {
            // Create tags list
            List<Tag> tagList = new ArrayList<>();
            for (int i = 0; i < tags.length; i += 2) {
                tagList.add(Tag.of(tags[i], tags[i + 1]));
            }
            
            // Create a new AtomicInteger to hold the gauge value
            AtomicInteger atomicInt = new AtomicInteger(0);
            
            // Register the gauge
            Gauge.builder(name, atomicInt, new ToDoubleFunction<AtomicInteger>() {
                @Override
                public double applyAsDouble(AtomicInteger value) {
                    return value.doubleValue();
                }
            })
            .tags(tagList)
            .description(name + " gauge")
            .register(meterRegistry);
            
            return atomicInt;
        });
        
        // Update the gauge value
        gaugeValue.set(value);
    }

    /**
     * Get the current value of a counter
     */
    public double getCounterValue(String name, String... tags) {
        String key = name + String.join("_", tags);
        Counter counter = counters.get(key);
        return counter != null ? counter.count() : 0.0;
    }

    /**
     * Get the mean time for a timer
     */
    public double getMeanTime(String name, String... tags) {
        String key = name + String.join("_", tags);
        Timer timer = timers.get(key);
        return timer != null ? timer.mean(TimeUnit.MILLISECONDS) : 0.0;
    }

    /**
     * Get the current value of a gauge
     */
    public int getGaugeValue(String name, String... tags) {
        String key = name + String.join("_", tags);
        AtomicInteger gauge = gauges.get(key);
        return gauge != null ? gauge.get() : 0;
    }

    /**
     * Create a timer and return a sample to be stopped when the operation completes
     */
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * Stop a timer and record the duration
     */
    public void stopTimer(Timer.Sample sample, String name, String... tags) {
        if (sample != null) {
            String key = name + String.join("_", tags);
            Timer timer = timers.computeIfAbsent(key, k ->
                Timer.builder(name)
                    .tags(tags)
                    .publishPercentiles(0.5, 0.95, 0.99)
                    .publishPercentileHistogram()
                    .register(meterRegistry)
            );
            sample.stop(timer);
        }
    }
}
