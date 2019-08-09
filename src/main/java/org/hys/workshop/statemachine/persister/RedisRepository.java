package org.hys.workshop.statemachine.persister;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.statemachine.redis.RedisStateMachineContextRepository;

public class RedisRepository extends RedisStateMachineContextRepository {

    public RedisRepository(RedisConnectionFactory redisConnectionFactory) {
        super(redisConnectionFactory);
    }
}
