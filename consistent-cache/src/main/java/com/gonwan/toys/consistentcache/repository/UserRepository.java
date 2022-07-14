package com.gonwan.toys.consistentcache.repository;

import com.gonwan.toys.consistentcache.model.UserDO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserDO, Long> {

}
