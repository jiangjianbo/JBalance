package org.github.jbalance;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * User: jjb
 * DateTime: 2013-05-01 20:39
 */
public class Utils {
    public static<T> T newInstance(Class<T> aClass) throws IllegalAccessException, InstantiationException {
        if(aClass == null )
            throw new IllegalArgumentException("aClass");

        return (T)aClass.newInstance();
    }

    public static<T> T newInstance(Class<T> aClass, T defaultValue){
        try {
            return newInstance(aClass);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static<T> T[] newInstance(Class<T>[] aClass) throws IllegalAccessException, InstantiationException {
        if(aClass == null )
            throw new IllegalArgumentException("aClass");

        ArrayList<T> values = new ArrayList<T>();
        for(Class<T> clz : aClass)
            values.add(clz.newInstance());
        return (T[])values.toArray();
    }

    public static<T> T[] newInstance(Class<T>[] aClass, boolean skipError, T[] defaultValue){
        ArrayList<T> values = new ArrayList<T>();
        for(Class<T> clz : aClass)
            try {
                values.add(clz.newInstance());
            } catch (Exception e) {
                if( !skipError ) return defaultValue;
            }
        return (T[])values.toArray();
    }

    public static InetAddress parseInetAddress(String addressAndPort) {

        return null;
    }
}
