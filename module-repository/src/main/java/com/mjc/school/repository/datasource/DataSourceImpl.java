package com.mjc.school.repository.datasource;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjc.school.repository.exception.*;
import com.mjc.school.repository.model.Entity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class DataSourceImpl<T extends Entity> implements DataSource<T> {
    private final Class<T> entityClass;
    private final Map<Long, T> values = new LinkedHashMap<>();
    private final ReadWriteLock entityLock = new ReentrantReadWriteLock();
    private final AtomicLong nextId = new AtomicLong(1L);

    protected DataSourceImpl(String dataFileName, Class<T> entityClass) {
        this.entityClass = entityClass;
        List<T> items = readDataFromFile(dataFileName, entityClass);
        indexItems(items);
    }

    private void indexItems(List<T> items) {
        entityLock.writeLock().lock();
        try {
            for (T item: items) {
                this.values.put(item.getId(), item);
                if (item.getId() >= nextId.get()) {
                    setNextId(item.getId() + 1);
                }
            }
        } finally {
            entityLock.writeLock().unlock();
        }
    }

    private List<T> readDataFromFile(String dataFileName, Class<T> entityClass) {
        ClassLoader classLoader = AuthorDataSource.class.getClassLoader();

        try (InputStream is = classLoader.getResourceAsStream(dataFileName)) {
            if (is == null) {
                throw new DataFileNotFoundException(dataFileName);
            }

            ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
            JavaType mapperType = mapper.getTypeFactory().constructCollectionType(List.class, entityClass);
            return mapper.readValue(is, mapperType);
        } catch (IOException e) {
            throw new DataFileReadException(dataFileName, e);
        }
    }

    @Override
    public T findById(Long id) throws KeyNullReferenceException, EntityNotFoundException {
        if (id == null) {
            throw new KeyNullReferenceException();
        }

        entityLock.readLock().lock();
        try {
            T entity = this.values.get(id);
            if (entity == null) {
                throw new EntityNotFoundException(id, entityClass);
            }
            return cloneEntity(entity);
        } finally {
            entityLock.readLock().unlock();
        }
    }

    @Override
    public List<T> findAll() {
        entityLock.readLock().lock();
        try {
            return this.values.values().stream()
                    .map(this::cloneEntity)
                    .toList();
        } finally {
            entityLock.readLock().unlock();
        }
    }

    @Override
    public List<T> findAll(long offset, long limit) {
        if (offset < this.values.size()) {
            entityLock.readLock().lock();
            try {
                return this.values.values().stream()
                        .skip(offset)
                        .limit(limit == -1 ? values.size() : limit)
                        .map(this::cloneEntity)
                        .toList();
            } finally {
                entityLock.readLock().unlock();
            }
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public T save(T value) throws EntityNullReferenceException {
        if (value == null) {
            throw new EntityNullReferenceException();
        }

        if (value.getId() == null || value.getId() == 0) {
            value.setId(getNextId());
        } else if (value.getId() >= this.nextId.get()) {
            setNextId(value.getId() + 1);
        }

        T entityToSave = cloneEntity(value);

        entityLock.writeLock().lock();
        try {
            this.values.put(entityToSave.getId(), entityToSave);
        } finally {
            entityLock.writeLock().unlock();
        }

        return value;
    }

    @Override
    public boolean delete(Long id) throws KeyNullReferenceException, EntityNotFoundException {
        if (id == null) {
            throw new KeyNullReferenceException();
        }

        entityLock.writeLock().lock();
        try {
            if (this.values.containsKey(id)) {
                return this.values.remove(id) != null;
            } else {
                throw new EntityNotFoundException(id, entityClass);
            }
        } finally {
            entityLock.writeLock().unlock();
        }
    }

    @Override
    public long count() {
        entityLock.readLock().lock();
        try {
            return this.values.size();
        } finally {
            entityLock.readLock().unlock();
        }
    }

    @Override
    public boolean existsById(Long id) throws KeyNullReferenceException {
        if (id == null) {
            throw new KeyNullReferenceException();
        }
        return this.values.containsKey(id);
    }

    private Long getNextId() {
        return this.nextId.incrementAndGet();
    }

    private void setNextId(Long value) {
        this.nextId.set(value);
    }

    private T cloneEntity(T entity) {
        return entity.copy();
    }
}
