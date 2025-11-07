package cuc.cdnews.domain;


import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 队列，保存将要访问的URL
 */
/**
 * 文章链接队列管理器，用于保存和处理待访问的文章信息
 * 线程安全设计，支持多线程并发操作
 */
public class ArticleLinkQueueManager {
    // 使用线程安全的ConcurrentLinkedQueue替代LinkedList，支持高并发场景
    private static final Queue<ArticleInfo> queue = new ConcurrentLinkedQueue<>();
    
    /**
     * 入队列操作
     * @param articleInfo 待加入队列的文章信息对象
     */
    public static void enQueue(ArticleInfo articleInfo) {
        if (articleInfo != null) { // 避免添加空对象
            queue.add(articleInfo);
        }
    }
    
    /**
     * 出队列操作
     * @return 队列头部的文章信息对象，队列为空时返回null
     */
    public static ArticleInfo deQueue() {
        // poll()方法是线程安全的，空队列时返回null，不会抛出异常
        return queue.poll();
    }
    
    /**
     * 判断队列是否为空
     * @return 队列空返回true，否则返回false
     */
    public static boolean isEmpty() {
        return queue.isEmpty();
    }
    
    /**
     * 判断队列是否包含指定对象
     * @param object 要检查的对象
     * @return 包含返回true，否则返回false
     */
    public static boolean contains(Object object) {
        return queue.contains(object);
    }
    
    /**
     * 获取队列中元素的数量
     * @return 队列中元素的数量
     */
    public static int getSize() {
        return queue.size();
    }
    
    /**
     * 清空队列中所有元素
     */
    public static void clear() {
        queue.clear();
    }
}
