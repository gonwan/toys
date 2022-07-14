package com.gonwan.toys.consistentcache.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gonwan.toys.consistentcache.model.UserDO;
import com.gonwan.toys.consistentcache.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CacheService {

    private static final String CACHE_PREFIX = "cachetest:u:";

    private static final Duration CACHE_DEFAULT_DURATION = Duration.ofHours(1);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /* double delete */
    public void setUser(UserDO userDO) {
        /* first delete */
        stringRedisTemplate.delete(CACHE_PREFIX + userDO.getId());
        int r = jdbcTemplate.update("insert ignore into t_user(`id`, `username`, `password`) " +
                        "values (?, ?, ?) " +
                        "on duplicate key update `username` = ?, `password` = ?",
                ps -> {
                    ps.setLong(1, userDO.getId());
                    ps.setString(2, userDO.getUsername());
                    ps.setString(3, userDO.getPassword());
                    ps.setString(4, userDO.getUsername());
                    ps.setString(5, userDO.getPassword());
                });
        if (r == 1) {
            /* second delete */
            /* FIXME: mark-delete or use version number strategy to improve consistency */
            stringRedisTemplate.delete(CACHE_PREFIX + userDO.getId());
        }
    }

    public UserDO getUser(Long id) {
        /* read from cache */
        String rstr = stringRedisTemplate.opsForValue().get(CACHE_PREFIX + id);
        if (rstr != null) {
            return JsonUtils.str2Obj(objectMapper, rstr, UserDO.class);
        }
        /* read from db */
        UserDO userDO = jdbcTemplate.query("select * from t_user where `id` = " + id, rs -> {
            /* add dummy empty value, to prevent penetration(穿透) */
            UserDO u = new UserDO();
            if (rs.next()) {
                u.setId(id);
                u.setUsername(rs.getString(2));
                u.setPassword(rs.getString(3));
            }
            return u;
        });
        /* write to cache, set expiration, also fill non-exist values */
        /* FIXME: add lock to avoid breakdown(击穿): large number of requests when a hotspot is deleted/evicted */
        String wstr = JsonUtils.obj2Str(objectMapper, userDO);
        stringRedisTemplate.executePipelined(new SessionCallback<Void>() {
            @Override
            public <K, V> Void execute(RedisOperations<K, V> operations) throws DataAccessException {
                StringRedisTemplate template = (StringRedisTemplate) operations;
                template.opsForValue().set(CACHE_PREFIX + id, wstr);
                /* no need to handle avalanche(雪崩), since we are handling per-user requests */
                template.expire(CACHE_PREFIX + id, CACHE_DEFAULT_DURATION);
                return null;
            }
        });
        return userDO;
    }

}
