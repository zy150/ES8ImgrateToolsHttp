package cuc.cdnews.thread;
// 2. 通用线程包装类，适配不同类型的Runnable任务
public class ThreadWrapper implements ManagedThreadInterface {
    private Thread thread;
    private final String threadName;
    private final String threadType;
    private final Runnable task;

    public ThreadWrapper(String threadName, String threadType, Runnable task) {
        this.threadName = threadName;
        this.threadType = threadType;
        this.task = task;
        this.thread = createThread();
    }

    private Thread createThread() {
        return new Thread(task, threadName);
    }

    @Override
    public Thread getThread() {
        return thread;
    }

    @Override
    public String getThreadType() {
        return threadType;
    }

    @Override
    public Runnable getTask() {
        return task;
    }

    @Override
    public void start() {
        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        } else {
            // 线程已终止，创建新实例并启动
            thread = createThread();
            thread.start();
        }
        System.out.printf("启动线程 [类型: %s, 名称: %s]%n", threadType, threadName);
    }
    
 // 关键修复：创建全新的线程包装实例
    @Override
    public ManagedThreadInterface createNewInstance(String threadName) {
        // 可以在名称后添加时间戳，确保唯一性
        //String newName = threadName + "-" + System.currentTimeMillis();
        return new ThreadWrapper(threadName, threadType, task);
    }
}
