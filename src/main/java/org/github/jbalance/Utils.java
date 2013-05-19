package org.github.jbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: jjb
 * DateTime: 2013-05-01 20:39
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static<T> T newInstance(Class<T> aClass) throws IllegalAccessException, InstantiationException {
        if(aClass == null )
            throw new IllegalArgumentException("aClass");

        return (T)aClass.newInstance();
    }

    public static<T> T newInstance(String clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return newInstance((Class<T>) Class.forName(clazz));
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

    public static InetSocketAddress parseInetAddress(String addressAndPort) {
        String ipPattern = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})?:(\\d+)";
        String ipV6Pattern = "\\[([a-zA-Z0-9:]+)\\]:(\\d+)";
        String hostPattern = "([\\w\\.\\-]+)?:(\\d+)";  // note will allow _ in host name
        String portPattern = "(\\d{1,5})";
        Pattern p = Pattern.compile( ipPattern + "|" + ipV6Pattern + "|" + hostPattern + "|" + portPattern );
        Matcher m = p.matcher( addressAndPort );
        if( m.matches() ) {
            if( m.group(2) != null ) {
                // group(1) IP address, group(2) is port
                int port = Integer.parseInt(m.group(2));
                return m.group(1) == null ? new InetSocketAddress(port): new InetSocketAddress(m.group(1), port);
            } else if( m.group(4) != null ) {
                // group(3) is IPv6 address, group(4) is port
                int port = Integer.parseInt(m.group(4));
                return m.group(3) == null ? new InetSocketAddress(port): new InetSocketAddress(m.group(3), port);
            } else if( m.group(6) != null ) {
                // group(5) is hostname, group(6) is port
                int port = Integer.parseInt(m.group(6));
                return m.group(5) == null ? new InetSocketAddress(port): new InetSocketAddress(m.group(5), port);
            } else if( m.group(7) != null ) {
                // group(7) is port
                int port = Integer.parseInt(m.group(7));
                return new InetSocketAddress(port);
            } else {
                // Not a valid address
            }
        }
        return null;
    }

    public static void copy(InputStream input, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024*8];
        int size = input.read(buffer);
        while( size >= 0 ){
            out.write(buffer, 0, size);
            size = input.read(buffer);
        }
    }


    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            logger.error("close error", e);
        }
    }

    // copy from jdk
    public static void shutdownAndAwaitTermination(ExecutorService pool, int waitSeconds) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (waitSeconds > 0 && !pool.awaitTermination(waitSeconds, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(waitSeconds, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            } else
                while(!pool.awaitTermination(60, TimeUnit.SECONDS));
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public static void background(String threadName, Runnable runner){
        Thread daemon = new Thread(runner);

        daemon.setName(threadName);
        daemon.setDaemon(true);
        daemon.start();
    }

    public static void locationFromSina(){
        //"http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=" + ip;
        // {"ret":1,"start":"123.112.0.0","end":"123.125.66.255","country":"\u4e2d\u56fd","province":"\u5317\u4eac","city":"\u5317\u4eac","district":"","isp":"\u8054\u901a","type":"","desc":""}

    }

    public static void locationFromTaobao(){
        //"http://ip.taobao.com/service/getIpInfo.php?ip=" + ip;
        // {"code":0,"data":{"country":"\u4e2d\u56fd","country_id":"CN","area":"\u534e\u5357","area_id":"800000","region":"\u5e7f\u4e1c\u7701","region_id":"440000","city":"\u6df1\u5733\u5e02","city_id":"440300","county":"","county_id":"-1","isp":"\u7535\u4fe1","isp_id":"100017","ip":"202.96.154.6"}}
    }

    public static void locationFromTencent(){
        //"http://ip.qq.com/cgi-bin/searchip?searchip1=" + ip;
        // <p>该IP所在地为：<span>中国广东省深圳市&nbsp;电信</span></p>
    }

    public static void copyStream(InputStream instream, OutputStream ostream) {
        try {
            byte[] buffer = new byte[1024*8];
            int size = instream.read(buffer);
            while( size > 0){
                ostream.write(buffer, 0, size);
                size = instream.read(buffer);
            }
            ostream.flush();
        } catch (IOException e) {
            logger.error("copy stream error", e);
        }
    }

    public static void flush(OutputStream stream) {
        try {
            stream.flush();
        } catch (IOException e) {
            logger.error("flush error", e);
        }
    }
}
