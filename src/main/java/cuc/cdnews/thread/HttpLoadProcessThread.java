package cuc.cdnews.thread;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cuc.cdnews.config.RootConfiguration;
import cuc.cdnews.data.HttpArticleService;
import cuc.cdnews.domain.ESArticleFullBean;
import cuc.cdnews.domain.ESArticleQueueManager;
import cuc.cdnews.utils.TextProcess;

/**
 * HTTP数据加载线程
 * 从HTTP接口获取文章数据并放入队列，供发送线程处理
 */
@Component
public class HttpLoadProcessThread implements Runnable {
  public static Logger logger = LogManager.getLogger(HttpLoadProcessThread.class.getName());

  @Autowired
  private HttpArticleService httpArticleService;

  public HttpLoadProcessThread() {
  }

  public HttpLoadProcessThread(HttpArticleService httpArticleService) {
    this.httpArticleService = httpArticleService;
  }

  @Override
  public void run() {
    logger.info("HttpLoadProcessThread 启动");

    while (true) {
      try {
        logger.info("待处理文章队列大小: " + ESArticleQueueManager.getSize());

        // 如果队列未满，则从HTTP接口加载数据
        if (ESArticleQueueManager.getSize() <= RootConfiguration.getQueueMaxSize()) {
          // 读取已处理的最大ID
          String maxIdPath = RootConfiguration.getFileSaveRootPath() + File.separator
              + "ES8ImgrateTools" + File.separator + "maxProcessedArticleId.txt";
          logger.info("maxIdPath: " + maxIdPath);

          String maxIdString = TextProcess.readFile(maxIdPath, null);
          logger.info("已处理的最大ArticleID: " + maxIdString);

          int maxid = 0;
          if (maxIdString != null && !maxIdString.trim().isEmpty()) {
            try {
              maxid = Integer.parseInt(maxIdString.trim());
            } catch (NumberFormatException e) {
              logger.warn("解析maxId失败，使用默认值0: " + e.getMessage());
              maxid = 0;
            }
          }

          // 从HTTP接口获取文章列表（每次获取1000条）
          java.util.List<ESArticleFullBean> beans = httpArticleService.getArticleInfoListByMaxId(maxid, 1000);

          if (beans != null && !beans.isEmpty()) {
            // 将获取的文章放入队列
            for (ESArticleFullBean bean : beans) {
              ESArticleQueueManager.enQueue(bean);
              maxid = bean.getId(); // 更新最大ID
            }

            // 更新已处理的最大ID到文件
            TextProcess.writeOverideFile(maxIdPath, String.valueOf(maxid));
            logger.info("从HTTP接口获取新文章列表并加入队列成功！当前队列大小: " + ESArticleQueueManager.getSize());
          } else {
            logger.info("从HTTP接口未获取到新数据，队列大小: " + ESArticleQueueManager.getSize());
            // 如果没有新数据，等待一段时间再重试
            Thread.sleep(30000);
          }
        } else {
          logger.info("文章队列已满，等待处理中... 当前队列大小: " + ESArticleQueueManager.getSize());
          Thread.sleep(10000); // 队列满时等待10秒
        }

      } catch (Exception e) {
        logger.error("HttpLoadProcessThread 发生异常: " + e.getMessage(), e);
        try {
          Thread.sleep(30000); // 发生异常时等待30秒再重试
        } catch (InterruptedException ie) {
          logger.error("线程被中断", ie);
          break;
        }
      }
    }
  }
}
