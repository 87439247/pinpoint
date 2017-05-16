/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.redisson;

import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.instrument.MethodFilters;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplate;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplateAware;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;
import io.netty.channel.socket.SocketChannel;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;

import static com.navercorp.pinpoint.common.util.VarArgs.va;

/**
 * @author jaehong.kim
 */
public class RedissonPlugin implements ProfilerPlugin, TransformTemplateAware {
    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private TransformTemplate transformTemplate;

    @Override
    public void setup(ProfilerPluginSetupContext context) {
        boolean enable = context.getConfig().readBoolean("profiler.redisson", true);
        if (enable) {
            addRedissonClassEditors();
        }
    }

    // Jedis & BinaryJedis
    private void addRedissonClassEditors() {

        //org.redisson.config.Config
        transformTemplate.transform("org.redisson.spring.cache.RedissonCache", new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                target.addField(RedissonConstants.END_POINT_ACCESSOR);
                //constructor
                InstrumentMethod constructor1 = target.getConstructor("org.redisson.api.RedissonClient", "org.redisson.api.RMap");
                if (constructor1 != null) {
                    constructor1.addInterceptor("com.navercorp.pinpoint.plugin.redisson.interceptor.RedissonCacheConstructorInterceptor");
                }
                InstrumentMethod constructor2 = target.getConstructor("org.redisson.api.RedissonClient", "org.redisson.api.RMapCache", "org.redisson.spring.cache.CacheConfig");
                if (constructor2 != null) {
                    constructor2.addInterceptor("com.navercorp.pinpoint.plugin.redisson.interceptor.RedissonCacheConstructorInterceptor");
                }
                // get method
                final InstrumentMethod getMethod = target.getDeclaredMethod("get", "java.lang.Object");
                if (getMethod != null) {
                    getMethod.addInterceptor("com.navercorp.pinpoint.plugin.redisson.interceptor.RedissonCacheMethodInterceptor");
                }
                return target.toBytecode();
            }
        });
    }

    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
}