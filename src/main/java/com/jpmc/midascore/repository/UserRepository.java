package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.UserRecord;
import org.springframework.data.repository.CrudRepository;

//knows how to store and retrieve that data.
public interface UserRepository extends CrudRepository<UserRecord, Long> {
    UserRecord findById(long id);
}
