/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.channel.cache.core.core.duplicate;

import org.channel.cache.core.core.NetEaseCacheHandler;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * A base component for invoking {@link Cache} operations and using a
 * configurable {@link CacheErrorHandler} when an exception occurs.
 *
 * @author Stephane Nicoll
 * @see CacheErrorHandler
 * @since 4.1
 */
public abstract class AbstractCacheInvoker {

    private CacheErrorHandler errorHandler;
    private List<NetEaseCacheHandler> netEaseCacheHandlers = new ArrayList<>();


    protected AbstractCacheInvoker() {
        this.errorHandler = new SimpleCacheErrorHandler();
    }

    protected AbstractCacheInvoker(CacheErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public List<NetEaseCacheHandler> getNetEaseCacheHandlers() {
        return netEaseCacheHandlers;
    }

    public void addHandler(NetEaseCacheHandler netEaseCacheHandler) {
        netEaseCacheHandlers.add(netEaseCacheHandler);
    }

    /**
     * Set the {@link CacheErrorHandler} instance to use to handle errors
     * thrown by the cache provider. By default, a {@link SimpleCacheErrorHandler}
     * is used who throws any exception as is.
     */
    public void setErrorHandler(CacheErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "CacheErrorHandler must not be null");
        this.errorHandler = errorHandler;
    }

    /**
     * Return the {@link CacheErrorHandler} to use.
     */
    public CacheErrorHandler getErrorHandler() {
        return this.errorHandler;
    }


    /**
     * Execute {@link Cache#get(Object)} on the specified {@link Cache} and
     * invoke the error handler if an exception occurs. Return {@code null}
     * if the handler does not throw any exception, which simulates a cache
     * miss in case of error.
     *
     * @see Cache#get(Object)
     */
    @Nullable
    protected Cache.ValueWrapper doGet(Cache cache, Object key, CacheAspectSupport.CacheOperationContext context) {
        try {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            for (NetEaseCacheHandler netEaseCacheHandler : getNetEaseCacheHandlers()) {

                netEaseCacheHandler.afterCacheGet(cache, key, context);
            }
            return valueWrapper;
        } catch (RuntimeException ex) {
            getErrorHandler().handleCacheGetError(ex, cache, key);
            return null;  // If the exception is handled, return a cache miss
        }
    }

    /**
     * Execute {@link Cache#put(Object, Object)} on the specified {@link Cache}
     * and invoke the error handler if an exception occurs.
     */
    protected void doPut(Cache cache, Object key, @Nullable Object result, CacheAspectSupport.CacheOperationContext context) {
        try {
            cache.put(key, result);
            for (NetEaseCacheHandler netEaseCacheHandler : getNetEaseCacheHandlers()) {
                netEaseCacheHandler.afterCachePut(cache, key, result, context);
            }
        } catch (RuntimeException ex) {
            getErrorHandler().handleCachePutError(ex, cache, key, result);
        }
    }

    /**
     * Execute {@link Cache#evict(Object)} on the specified {@link Cache} and
     * invoke the error handler if an exception occurs.
     */
    protected void doEvict(Cache cache, Object key, CacheAspectSupport.CacheOperationContext context) {
        try {
            cache.evict(key);
            for (NetEaseCacheHandler netEaseCacheHandler : getNetEaseCacheHandlers()) {
                netEaseCacheHandler.afterCacheEvict(cache, key, context);
            }
        } catch (RuntimeException ex) {
            getErrorHandler().handleCacheEvictError(ex, cache, key);
        }
    }

    /**
     * Execute {@link Cache#clear()} on the specified {@link Cache} and
     * invoke the error handler if an exception occurs.
     */
    protected void doClear(Cache cache, CacheAspectSupport.CacheOperationContext context) {
        try {
            cache.clear();
            for (NetEaseCacheHandler netEaseCacheHandler : getNetEaseCacheHandlers()) {
                netEaseCacheHandler.afterClear(cache, context);
            }
        } catch (RuntimeException ex) {
            getErrorHandler().handleCacheClearError(ex, cache);
        }
    }

}
