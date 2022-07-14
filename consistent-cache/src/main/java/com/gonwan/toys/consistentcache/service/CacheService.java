package com.gonwan.toys.consistentcache.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gonwan.toys.consistentcache.util.JsonUtils;
import com.gonwan.toys.consistentcache.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    private static final String CACHE_PREFIX = "cachetest:u:";

    private static final Duration CACHE_DEFAULT_DURATION = Duration.ofHours(1);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /* double delete */
    public void setUser(UserVO userVO) {
        /* first delete */
        stringRedisTemplate.delete(CACHE_PREFIX + userVO.getId());
        int r = jdbcTemplate.update("insert ignore into t_user(`id`, `username`, `password`) " +
                        "values (?, ?, ?) " +
                        "on duplicate key update `username` = ?, `password` = ?",
                ps -> {
                    ps.setInt(1, userVO.getId());
                    ps.setString(2, userVO.getUsername());
                    ps.setString(3, userVO.getPassword());
                    ps.setString(4, userVO.getUsername());
                    ps.setString(5, userVO.getPassword());
                });
        if (r == 1) {
            /* second delete */
            /* FIXME: mark-delete or use version number strategy to improve consistency */
            stringRedisTemplate.delete(CACHE_PREFIX + userVO.getId());
        }
    }

    public UserVO getUser(Integer id) {
        /* read from cache */
        String rstr = stringRedisTemplate.opsForValue().get(CACHE_PREFIX + id);
        if (rstr != null) {
            return JsonUtils.str2Obj(objectMapper, rstr, UserVO.class);
        }
        /* read from db */
        UserVO userVO = jdbcTemplate.query("select * from t_user where `id` = " + id, rs -> {
            /* add dummy empty value, to prevent penetration(穿透) */
            UserVO u = new UserVO();
            if (rs.next()) {
                u.setId(id);
                u.setUsername(rs.getString(2));
                u.setPassword(rs.getString(3));
            }
            return u;
        });
        /* write to cache, set expiration, also fill non-exist values */
        /* FIXME: add lock to avoid breakdown(击穿): large number of requests when a hotspot is deleted/evicted */
        String wstr = JsonUtils.obj2Str(objectMapper, userVO);
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
        return userVO;
    }

}
