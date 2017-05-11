package com.navercorp.pinpoint.plugin.redisson.interceptor;

import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.SpanEventSimpleAroundInterceptorForPlugin;
import com.navercorp.pinpoint.bootstrap.interceptor.annotation.Scope;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScope;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScopeInvocation;
import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.plugin.redisson.CommandContext;
import com.navercorp.pinpoint.plugin.redisson.CommandContextFactory;
import com.navercorp.pinpoint.plugin.redisson.EndPointAccessor;
import com.navercorp.pinpoint.plugin.redisson.RedissonConstants;

/**
 * redisson cache interceptor
 */
@Scope(value = RedissonConstants.REDISSON_SCOPE)
public class RedissonCacheMethodInterceptor extends SpanEventSimpleAroundInterceptorForPlugin {

    private InterceptorScope interceptorScope;

    public RedissonCacheMethodInterceptor(TraceContext traceContext, MethodDescriptor methodDescriptor, InterceptorScope interceptorScope) {
        super(traceContext, methodDescriptor);
        this.interceptorScope = interceptorScope;
    }

    @Override
    protected void doInBeforeTrace(SpanEventRecorder recorder, Object target, Object[] args) {
        final InterceptorScopeInvocation invocation = interceptorScope.getCurrentInvocation();
        if (invocation != null) {
            CommandContext commandContext = (CommandContext) invocation.getOrCreateAttachment(CommandContextFactory.COMMAND_CONTEXT_FACTORY);
            commandContext.setBeginTime(System.currentTimeMillis());
        }
    }

    @Override
    protected void doInAfterTrace(SpanEventRecorder recorder, Object target, Object[] args, Object result, Throwable throwable) {
        if (target instanceof EndPointAccessor) {
            String endPoint = ((EndPointAccessor) target)._$PINPOINT$_getEndPoint();
            recorder.recordEndPoint(endPoint);
        }

        final InterceptorScopeInvocation invocation = interceptorScope.getCurrentInvocation();
        if (invocation != null && invocation.getAttachment() != null && invocation.getAttachment() instanceof CommandContext) {
            final CommandContext commandContext = (CommandContext) invocation.getAttachment();
            commandContext.setEndTime(System.currentTimeMillis());
            recorder.recordAttribute(AnnotationKey.ARGS1, "elapsed=" + commandContext.getElapsedTime());
            if (args != null && args.length > 0) {
                recorder.recordAttribute(AnnotationKey.ARGS2, "key=" + args[0]);
            }
            recorder.recordTime(true);
            invocation.removeAttachment();// clear
        }
        recorder.recordApi(getMethodDescriptor());
        recorder.recordDestinationId(RedissonConstants.REDISSON.getName());
        recorder.recordServiceType(RedissonConstants.REDISSON);
        recorder.recordException(throwable);
    }
}
