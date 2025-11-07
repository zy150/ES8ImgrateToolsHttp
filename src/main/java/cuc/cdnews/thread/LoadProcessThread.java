package cuc.cdnews.thread;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cuc.cdnews.config.RootConfiguration;
import cuc.cdnews.data.EsArticleService;
import cuc.cdnews.domain.ArticleInfo;
import cuc.cdnews.domain.ArticleLinkQueueManager;
import cuc.cdnews.utils.TextProcess;

@Component
public class LoadProcessThread implements Runnable {
	public static Logger logger = LogManager.getLogger(LoadProcessThread.class.getName());
	public static int currentPorcessingId = 0;

	@Autowired
	private EsArticleService esArticleService;

	public LoadProcessThread(EsArticleService esArticleService) {
		this.esArticleService = esArticleService;
	}

	public void run() {
		// TODO Auto-generated method stub

		logger.info("MultiTargetProcessThread start");

//		LLMsServerHelper bLLMsServerHelper = new LLMsServerHelper();
//
//		OnlineDataHelper onlineDataHelper = new OnlineDataHelper();
//
//		String syncFailedFolder = RootConfiguration.getFileSaveRootPath()+ "cdnewsAISync"+ File.separator  + "syncfailedfiles";
//		String llmsfaiiledFolder = RootConfiguration.getFileSaveRootPath()+ "cdnewsAISync" +File.separator + "llmsfaiiledfiles";
		
		while (true) 
		{
			try 
			{
				logger.info("the number of unhandled articleQueue:" + ArticleLinkQueueManager.getSize());
				if (ArticleLinkQueueManager.getSize()<= RootConfiguration.getQueueMaxSize()) {
					
					String maxIdPath = RootConfiguration.getFileSaveRootPath() +"ES8ImgrateTools"+File.separator+ "maxProcessedArticleId.txt";
					logger.info("maxIdPath:" + maxIdPath);
					
					String maxIdString = TextProcess.readFile(maxIdPath,null);
					System.out.println("已处理的最大ArticleID：" + maxIdString);
					int maxid = Integer.parseInt(maxIdString);
					// System.out.println("已处理的最大ArticleID：" + maxIdString);
					//EsArticleService esArticleService = new EsArticleService();
					
					List<ArticleInfo> beans = esArticleService.getArticleInfoListByMaxId(maxid, 100);
					
					for(ArticleInfo b: beans)
					{
						ArticleLinkQueueManager.enQueue(b);
						maxid = b.getId();
					}
					
					TextProcess.writeOverideFile(maxIdPath,String.valueOf(maxid));
					logger.info("get new article list....add to article queue......Success! Total count is " + ArticleLinkQueueManager.getSize());
				} else {
					logger.info("articles of queueManager is still in process......");
				}
				Thread.sleep(30000);
				
			} catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}
}
