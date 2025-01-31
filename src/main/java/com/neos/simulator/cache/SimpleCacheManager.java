package com.neos.simulator.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleCacheManager<K, V> {
    private final Map<K, CacheObject<V>> cacheMap = new ConcurrentHashMap<>();
    private final long defaultTtlMillis;
    private final ScheduledExecutorService cleanerService = Executors.newSingleThreadScheduledExecutor();

    public SimpleCacheManager(long defaultTtlMillis, long cleanUpIntervalMillis) {
        this.defaultTtlMillis = defaultTtlMillis;

        // Schedule periodic cleanup
        cleanerService.scheduleAtFixedRate(this::cleanUp, cleanUpIntervalMillis, cleanUpIntervalMillis, TimeUnit.MILLISECONDS);
    }

    public void put(K key, V value) {
        put(key, value, defaultTtlMillis);
    }

    public void put(K key, V value, long ttlMillis) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or Value cannot be null");
        }
        long expiryTime = System.currentTimeMillis() + ttlMillis;
        cacheMap.put(key, new CacheObject<>(value, expiryTime));
    }

    public V get(K key) {
        CacheObject<V> cacheObject = cacheMap.get(key);
        if (cacheObject == null || cacheObject.isExpired()) {
            cacheMap.remove(key); // Remove expired entry
            return null;
        }
        return cacheObject.value;
    }

    public boolean containsKey(K key) {
        return get(key) != null; // Triggers expiration check
    }

    public void remove(K key) {
        cacheMap.remove(key);
    }

    public int size() {
        return cacheMap.size();
    }

    public void clear() {
        cacheMap.clear();
    }

    public void shutdown() {
        cleanerService.shutdown();
    }

    private void cleanUp() {
        long currentTime = System.currentTimeMillis();
        cacheMap.entrySet().removeIf(entry -> entry.getValue().isExpired(currentTime));
    }

    private static class CacheObject<V> {
        final V value;
        final long expiryTime;

        CacheObject(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }

        boolean isExpired(long currentTime) {
            return currentTime > expiryTime;
        }
    }
}
