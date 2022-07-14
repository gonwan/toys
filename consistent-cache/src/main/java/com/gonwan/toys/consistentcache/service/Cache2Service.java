package com.gonwan.toys.consistentcache.service;

import com.gonwan.toys.consistentcache.model.UserDO;
import com.gonwan.toys.consistentcache.repository.UserRepository;
import io.github.dtm.cache.Cache;
import io.github.dtm.cache.Consistency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class Cache2Service {

    @Autowired
    private UserRepository userRepository;

    @Autowired @Qualifier("userCache")
    private Cache<Long, UserDO> userCache;

    public UserDO findUser(Long id, Consistency consistency) {
        return userCache.fetch(id, consistency);
    }

    public void saveUser(UserDO userDO) {
        userRepository.save(userDO);
        userCache.tagAsDeleted(userDO.getId());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        userCache.tagAsDeleted(id);
    }

}
