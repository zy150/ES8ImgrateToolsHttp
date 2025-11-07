package cuc.cdnews.thread;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cuc.cdnews.config.RootConfiguration;
import cuc.cdnews.data.EsArticleService;
import cuc.cdnews.domain.ArticleInfo;
import cuc.cdnews.domain.ArticleLinkQueueManager;
import cuc.cdnews.utils.TextProcess;


// 3. 多类型线程管理器
public class MultiThreadManager {
	
	public static Logger logger = LogManager.getLogger(MultiThreadManager.class.getName());
    private final List<ManagedThreadInterface> managedThreads = new ArrayList<>();

    // 添加线程到管理器
    public void addThread(ManagedThreadInterface thread) {
        managedThreads.add(thread);
    }

    // 启动所有线程
    public void startAll() {
        for (ManagedThreadInterface thread : managedThreads) {
            thread.start();
        }
    }

    // 监控并重启已结束的线程
    public void monitorAndRestart(EsArticleService articleService) {
        new Thread(() -> {
            while (true) {
                try {
                	 logger.info("已处理文章数量：" + ArticleProcessThread.isHandledCount);
                	 loaderData(articleService);
                	 for (int i = 0; i < managedThreads.size(); i++) {
                		ManagedThreadInterface managedThread = managedThreads.get(i);
                        Thread thread = managedThread.getThread();
                        Thread.State state = thread.getState();
                        
                        // 打印线程信息（包含类型和名称）
                        logger.info("[类型: "+ managedThread.getThreadType() +", 名称: "+ thread.getName()+",] 状态: " + state.name());
                        // 线程终止时重启
                        if (state == Thread.State.TERMINATED) {
                        	logger.info("[类型: "+ managedThread.getThreadType() +", 名称: "+ thread.getName()+",] 已结束，准备重启启动..... ");
                        	ManagedThreadInterface newThread = managedThread.createNewInstance(thread.getName());
                            managedThreads.set(i, newThread);
                            newThread.start(); // 启动全新的线程
                            managedThread.start(); // 调用包装类的start方法（自动处理重启）
                        }
                    }
                    TimeUnit.SECONDS.sleep(30); // 每2秒检查一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }, "Multi-Thread-Monitor").start();
    }
    public void loaderData(EsArticleService esArticleService)
    {
	    if (ArticleLinkQueueManager.getSize()<= RootConfiguration.getQueueMaxSize()) {
			
			String maxIdPath = RootConfiguration.getFileSaveRootPath() +"ES8ImgrateTools"+File.separator+ "maxProcessedArticleId.txt";
			logger.info("maxIdPath:" + maxIdPath);
			
			String maxIdString = TextProcess.readFile(maxIdPath,null);
			logger.info("已处理的最大ArticleID：" + maxIdString);
			int maxid = Integer.parseInt(maxIdString);
			// System.out.println("已处理的最大ArticleID：" + maxIdString);
			//EsArticleService esArticleService = new EsArticleService();
			
			List<ArticleInfo> beans = esArticleService.getArticleInfoListByMaxId(maxid, 1000);
			
			for(ArticleInfo b: beans)
			{
				ArticleLinkQueueManager.enQueue(b);
				maxid = b.getId();
			}
			
			TextProcess.writeOverideFile(maxIdPath,String.valueOf(maxid));
			logger.info("get new article list....add to article queue......Success! Total count is " + ArticleLinkQueueManager.getSize());
		} else {
			logger.info("size of unhandled article:" + ArticleLinkQueueManager.getSize());
		}
    }
}
    