package com.navercorp.pinpoint.plugin.redisson.interceptor;

import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.redisson.EndPointAccessor;
import org.redisson.Redisson;
import org.redisson.api.Node;
import org.redisson.api.NodeType;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * redisson cache interceptor
 */
public class RedissonCacheConstructorInterceptor implements AroundInterceptor {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    @Override
    public void before(Object target, Object[] args) {
        if (isDebug) {
            logger.beforeInterceptor(target, args);
        }
        try {
            if (!validate(target, args)) {
                return;
            }
            RedissonClient client = (RedissonClient) args[0];
            Collection<Node> nodes = client.getNodesGroup().getNodes(NodeType.MASTER);
            Node master = nodes.iterator().next();
            InetSocketAddress addr = master.getAddr();
            ((EndPointAccessor) target)._$PINPOINT$_setEndPoint(addr.toString());
        } catch (Throwable t) {
            logger.warn("Failed to BEFORE process. {}", t.getMessage(), t);
        }
    }

    private boolean validate(Object target, Object[] args) {
        if (args == null || args.length <= 1) {
            logger.debug("Invalid arguments. Null or not found args({}).", args);
            return false;
        }

        if (!(args[0] instanceof RedissonClient)) {
            logger.debug("Invalid arguments. Expect String but args[0]({}).", args[0]);
            return false;
        }
        return true;
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {

    }
}
