package com.enterprise.portfolio.service.impl;

import com.enterprise.portfolio.dto.BaseDto;
import com.enterprise.portfolio.exception.ResourceNotFoundException;
import com.enterprise.portfolio.model.BaseEntity;
import com.enterprise.portfolio.service.BaseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseServiceImpl<D extends BaseDto, E extends BaseEntity, ID extends Serializable, R extends JpaRepository<E, ID>> 
        implements BaseService<D, E, ID> {

    protected final R repository;
    protected final Class<E> entityClass;
    protected final Class<D> dtoClass;

    @Autowired
    protected ModelMapper modelMapper;

    protected BaseServiceImpl(R repository, Class<E> entityClass, Class<D> dtoClass) {
        this.repository = repository;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    @Override
    public D findById(ID id) {
        E entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        entityClass.getSimpleName() + " not found with id: " + id));
        return toDto(entity);
    }

    @Override
    public List<D> findAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<D> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDto);
    }

    @Override
    public D create(D dto) {
        E entity = toEntity(dto);
        E savedEntity = repository.save(entity);
        return toDto(savedEntity);
    }

    @Override
    public D update(ID id, D dto) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(entityClass.getSimpleName() + " not found with id: " + id);
        }
        dto.setId((Long) id);
        E entity = toEntity(dto);
        E updatedEntity = repository.save(entity);
        return toDto(updatedEntity);
    }

    @Override
    public void delete(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(entityClass.getSimpleName() + " not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public D toDto(E entity) {
        return modelMapper.map(entity, dtoClass);
    }

    @Override
    public E toEntity(D dto) {
        return modelMapper.map(dto, entityClass);
    }
}
