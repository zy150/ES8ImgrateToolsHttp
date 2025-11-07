package cuc.cdnews.thread;

//1. 定义线程包装接口，统一不同类型线程的管理方式
public interface ManagedThreadInterface {
    Thread getThread();       // 获取线程实例
    String getThreadType();   // 获取线程类型
    Runnable getTask();       // 获取任务实例
    void start();             // 启动线程
    ManagedThreadInterface createNewInstance(String threadName); // 创建新实例
}
