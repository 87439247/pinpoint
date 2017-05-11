package com.navercorp.pinpoint.plugin.redis;

import com.navercorp.pinpoint.common.server.bo.SpanEventBo;
import com.navercorp.pinpoint.test.junit4.BasePinpointTest;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonCache;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.Cache;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class RedissonPluginTest extends BasePinpointTest {

    private static final String HOST = "localhost";
    private static final int PORT = 6379;

    @Test
    public void redisson() {
        RedissonClient client = Redisson.create();
        RedissonSpringCacheManager manager = new RedissonSpringCacheManager(client);
        RedissonCache redissonCache = (RedissonCache) manager.getCache("asdf");

        Cache.ValueWrapper adfasfd = redissonCache.get("adfasfd");
        final List<SpanEventBo> events = getCurrentSpanEvents();
        assertEquals(1, events.size());

        final SpanEventBo eventBo = events.get(0);
        assertEquals("/127.0.0.1:6379", eventBo.getEndPoint());
    }

    @Test
    public void binaryJedis() {
//        JedisMock jedis = new JedisMock("localhost", 6379);
//        try {
//            jedis.get("foo".getBytes());
//        } finally {
//            if(jedis != null) {
//                jedis.close();
//            }
//        }
        final List<SpanEventBo> events = getCurrentSpanEvents();
        assertEquals(1, events.size());

        final SpanEventBo eventBo = events.get(0);
        assertEquals(HOST + ":" + PORT, eventBo.getEndPoint());
        assertEquals("REDIS", eventBo.getDestinationId());
    }


    @Test
    public void pipeline() {
//        JedisMock jedis = new JedisMock("localhost", 6379);
//        try {
//            Pipeline pipeline = jedis.pipelined();
//            pipeline.get("foo");
//        } finally {
//            if(jedis != null) {
//                jedis.close();
//            }
//        }

        final List<SpanEventBo> events = getCurrentSpanEvents();
        assertEquals(1, events.size());
    }


    public class RedissonSpringCacheManagerMock extends RedissonSpringCacheManager {
        public RedissonSpringCacheManagerMock(RedissonClient redisson) {
            super(redisson);
        }
//        public JedisMock(String host, int port) {
//            super(host, port);
//
//            client = mock(Client.class);
//
//            // for 'get' command
//            when(client.isInMulti()).thenReturn(false);
//            when(client.getBulkReply()).thenReturn("bar");
//            when(client.getBinaryBulkReply()).thenReturn("bar".getBytes());
//        }
    }

}
