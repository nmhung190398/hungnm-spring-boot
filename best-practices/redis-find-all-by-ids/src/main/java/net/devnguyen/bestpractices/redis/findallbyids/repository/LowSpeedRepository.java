package net.devnguyen.bestpractices.redis.findallbyids.repository;

import net.devnguyen.bestpractices.redis.findallbyids.domain.LowSpeedDomain;
import org.springframework.data.repository.CrudRepository;

public interface LowSpeedRepository extends CrudRepository<LowSpeedDomain,String> {
    @Override
    Iterable<LowSpeedDomain> findAllById(Iterable<String> strings);
}
