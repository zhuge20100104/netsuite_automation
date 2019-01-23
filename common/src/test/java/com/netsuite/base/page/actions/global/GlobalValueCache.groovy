package com.netsuite.base.page.actions.global

class GlobalValueCache {

    private static ThreadLocal<HashMap<String,Object>> cacheThreadLocal = new ThreadLocal<>()

    public static HashMap<String,Object> getCache() {
        if(cacheThreadLocal.get()==null) {
            cacheThreadLocal.set(new HashMap<String, Object>())
        }
        return cacheThreadLocal.get()
    }

    public static void putValue(String key,Object value) {
        getCache().put(key,value)
    }

    public static Object getValue(String key) {
        HashMap<String,Object> cache = getCache()

        if(cache.containsKey(key)) {
            return cache.get(key)
        }

        return null
    }

    public static void clearCache() {
        HashMap<String,Object> cache = getCache()
        cache.clear()
    }


    public static boolean containsKey(String key) {
        HashMap<String,Object> cache = getCache()
        return cache.containsKey(key)
    }

}
