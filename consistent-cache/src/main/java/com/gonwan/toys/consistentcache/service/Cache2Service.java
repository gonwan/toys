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
        /*
         * This example only shows the usage of rockscache-java. Therefore,
         * it directly calls the tagAsDeleted method after modifying the database
         * and does not consider that the tagAsDeleted method itself may throw exception.
         *
         * In actual projects, please ensure that tagAsDeleted will be executed successfully.
         * It is recommended to use reliable messages.
         *
         * For example, you can use the 2-phase message of the DTM framework to ensure that
         * tagAsDeleted will be retried and retried until it succeeds.
         *
         * Please see https://en.dtm.pub/practice/msg.html to know more
         */
        userCache.tagAsDeleted(userDO.getId());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        /*
         * This example only shows the usage of rockscache-java. Therefore,
         * it directly calls the tagAsDeleted method after modifying the database
         * and does not consider that the tagAsDeleted method itself may throw exception.
         *
         * In actual projects, please ensure that tagAsDeleted will be executed successfully.
         * It is recommended to use reliable messages.
         *
         * For example, you can use the 2-phase message of the DTM framework to ensure that
         * tagAsDeleted will be retried and retried until it succeeds.
         *
         * Please see https://en.dtm.pub/practice/msg.html to know more
         */
        userCache.tagAsDeleted(id);
    }

}
