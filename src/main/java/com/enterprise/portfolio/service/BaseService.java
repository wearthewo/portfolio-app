package com.enterprise.portfolio.service;

import com.enterprise.portfolio.dto.BaseDto;
import com.enterprise.portfolio.model.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

public interface BaseService<D extends BaseDto, E extends BaseEntity, ID extends Serializable> {
    
    D findById(ID id);
    
    List<D> findAll();
    
    Page<D> findAll(Pageable pageable);
    
    D create(D dto);
    
    D update(ID id, D dto);
    
    void delete(ID id);
    
    boolean existsById(ID id);
    
    D toDto(E entity);
    
    E toEntity(D dto);
}
