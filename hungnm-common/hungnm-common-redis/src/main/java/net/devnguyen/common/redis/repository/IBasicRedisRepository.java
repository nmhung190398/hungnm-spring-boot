package net.devnguyen.common.redis.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IBasicRedisRepository<V> {
    List<V> findAllById(Collection<String> strings);
    void save(V v);
    void saveAll(List<V> list);
    void deletedById(String id);
    Optional<V> findById(String id);
    List<V> findAll();
    long count();
    boolean existsById(String id);
    void deleteAll();
}
