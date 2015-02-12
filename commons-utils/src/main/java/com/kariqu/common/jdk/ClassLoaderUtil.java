package com.kariqu.common.jdk;

import net.sf.ehcache.CacheException;

/**
 * 加载器工具
 * User: Asion
 * Date: 12-4-19
 * Time: 上午2:15
 */
public class ClassLoaderUtil {

    private ClassLoaderUtil() {
    }

    public static ClassLoader getStandardClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static ClassLoader getFallbackClassLoader() {
        return ClassLoaderUtil.class.getClassLoader();
    }

    public static Object createNewInstance(String className) throws CacheException {
        Class clazz;
        Object newInstance;
        try {
            clazz = Class.forName(className, true, getStandardClassLoader());
        } catch (ClassNotFoundException e) {
            //try fallback
            try {
                clazz = Class.forName(className, true, getFallbackClassLoader());
            } catch (ClassNotFoundException ex) {
                throw new CacheException("Unable to load class " + className +
                        ". Initial cause was " + e.getMessage(), e);
            }
        }
        try {
            newInstance = clazz.newInstance();
        } catch (IllegalAccessException e) {
            throw new CacheException("Unable to load class " + className +
                    ". Initial cause was " + e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new CacheException("Unable to load class " + className +
                    ". Initial cause was " + e.getMessage(), e);
        }
        return newInstance;
    }
}
