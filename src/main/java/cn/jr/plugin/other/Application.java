package cn.jr.plugin.other;

import org.apache.maven.plugin.logging.Log;

public class Application {
    private static  ThreadLocal<Log> logThreadLocal = new ThreadLocal<Log>();
    public static void setLog(Log log){
        logThreadLocal.remove();
        logThreadLocal.set(log);
    }
    public static Log getLog(){
        return logThreadLocal.get();
    }

}
