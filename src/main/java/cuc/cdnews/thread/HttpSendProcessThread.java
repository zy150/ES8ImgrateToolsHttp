package cuc.cdnews.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import cuc.cdnews.config.RootConfiguration;
import cuc.cdnews.domain.ESArticleFullBean;
import cuc.cdnews.domain.ESArticleQueueManager;
import cuc.cdnews.ui.App2;
import okhttp3.OkHttpClient;

/**
 * HTTP数据发送线程
 * 从队列中取出文章数据，通过HTTP POST请求发送到目标API
 * 使用App2中的sendArticleToApi方法
 */
@Component
public class HttpSendProcessThread implements Runnable {
  public static Logger logger = LogManager.getLogger(HttpSendProcessThread.class.getName());

  // 成功和失败计数器（线程安全）
  private static volatile int successCount = 0;
  private static volatile int failCount = 0;

  // HTTP客户端，每个线程使用独立的客户端实例
  private OkHttpClient httpClient;

  public HttpSendProcessThread() {
    // 初始化HTTP客户端
    this.httpClient = new OkHttpClient();
  }

  @Override
  public void run() {
    logger.info("HttpSendProcessThread [" + Thread.currentThread().getName() + "] 启动");

    while (true) {
      try {
        // 从队列中取出文章
        ESArticleFullBean bean = ESArticleQueueManager.deQueue();

        if (bean != null) {
          try {
            // 使用App2中的方法发送文章到API
            int statusCode = App2.sendArticleToApi(
                bean,
                httpClient,
                RootConfiguration.getCdnewsAiSyncUrl(),
                RootConfiguration.getCdnewsAiSyncDatasetId(),
                RootConfiguration.getCdnewsAiSyncDocumentId(),
                RootConfiguration.getCdnewsAiSyncApiToken());

            // 根据响应码更新计数器
            if (statusCode == 200) {
              synchronized (HttpSendProcessThread.class) {
                successCount++;
              }
              logger.debug("线程 [" + Thread.currentThread().getName() + "] 成功发送文章 ID: " + bean.getId());
            } else {
              synchronized (HttpSendProcessThread.class) {
                failCount++;
              }
              logger.error(
                  "线程 [" + Thread.currentThread().getName() + "] 发送文章 ID: " + bean.getId() + " 失败，响应码: " + statusCode);
            }

          } catch (Exception e) {
            synchronized (HttpSendProcessThread.class) {
              failCount++;
            }
            logger.error(
                "线程 [" + Thread.currentThread().getName() + "] 发送文章 ID: " + bean.getId() + " 时发生异常: " + e.getMessage(),
                e);
          }
        } else {
          // 队列为空，等待一段时间
          logger.debug("线程 [" + Thread.currentThread().getName() + "] 队列为空，等待中...");
          Thread.sleep(5000); // 等待5秒
        }

      } catch (InterruptedException e) {
        logger.info("线程 [" + Thread.currentThread().getName() + "] 被中断");
        break;
      } catch (Exception e) {
        logger.error("线程 [" + Thread.currentThread().getName() + "] 发生异常: " + e.getMessage(), e);
        try {
          Thread.sleep(10000); // 发生异常时等待10秒
        } catch (InterruptedException ie) {
          logger.error("线程被中断", ie);
          break;
        }
      }
    }
  }

  /**
   * 获取成功发送的数量
   */
  public static int getSuccessCount() {
    return successCount;
  }

  /**
   * 获取失败的数量
   */
  public static int getFailCount() {
    return failCount;
  }

  /**
   * 重置计数器
   */
  public static void resetCounters() {
    successCount = 0;
    failCount = 0;
  }
}
