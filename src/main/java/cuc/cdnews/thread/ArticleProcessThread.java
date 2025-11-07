package cuc.cdnews.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cuc.cdnews.config.RootConfiguration;
import cuc.cdnews.data.EsArticleService;
import cuc.cdnews.domain.ArticleInfo;
import cuc.cdnews.domain.ArticleLinkQueueManager;
import cuc.cdnews.domain.ESArticleFullBean;
import cuc.cdnews.utils.StanfordNlpTools;

/*
 * 使用了新的bean定义 ESArticleBean nerList和wordSeqJson类型由String修改成JSONObject
 */
@Component
public class ArticleProcessThread implements Runnable {
	public static Logger logger = LogManager.getLogger(ArticleProcessThread.class.getName());

	public static int isHandledCount = 0;

	public static int getIsHandledCount() {
		return isHandledCount;
	}

	public static void setIsHandledCount(int isHandledCount) {
		ArticleProcessThread.isHandledCount = isHandledCount;
	}

	@Autowired
	private EsArticleService esArticleService;

	public ArticleProcessThread(EsArticleService esArticleService) {
		this.esArticleService = esArticleService;
	}

	public void run() {
		// TODO Auto-generated method stub
		logger.info("ArticleProcessThread start");

		StanfordNlpTools stanfordNlpTools = new StanfordNlpTools();
		while (true) {
				// 判断队列是否为空，然后取出一个进行处理
				logger.info("the number of unhandled articleQueue:" + ArticleLinkQueueManager.getSize());
				ArticleInfo bean = ArticleLinkQueueManager.deQueue();
				if (bean != null) {
					try {
					ESArticleFullBean eSArticleFullBean = new ESArticleFullBean();

					eSArticleFullBean.setId(bean.getId());
					eSArticleFullBean.setAbstractCN(bean.getAbstractCN());
					eSArticleFullBean.setAbstractEN(bean.getAbstractEN());
					eSArticleFullBean.setArticleVector(bean.getArticleVector());
					eSArticleFullBean.setAuthor(bean.getAuthor());

					eSArticleFullBean.setClassification(bean.getClassification());
					eSArticleFullBean.setContent(bean.getContent());
					eSArticleFullBean.setContentCN(bean.getContentCN());
					eSArticleFullBean.setDocType(bean.getDocType());
					eSArticleFullBean.setInclusionTime(bean.getInclusionTime());

					eSArticleFullBean.setInclusionTimeText(bean.getInclusionTimeText());
					eSArticleFullBean.setIsKeyArticle(bean.getIsKeyArticle());
					eSArticleFullBean.setMediaSourceId(bean.getMediaSourceId());

					if (bean.getNerList() != null && !bean.getNerList().equalsIgnoreCase("")) {
						eSArticleFullBean.setNerList(esArticleService.list2HotWordList(bean.getNerList()));
					} else {
						eSArticleFullBean.setNerList(esArticleService.nerList2NerListObj(
								stanfordNlpTools.nerRegnation(bean.getTitle() + ". " + bean.getContent())));
					}

					eSArticleFullBean.setPublisherCountry(bean.getPublisherCountry());

					eSArticleFullBean.setPublisherId(bean.getPublisherId());
					eSArticleFullBean.setPublisherType(bean.getPublisherType());
					eSArticleFullBean.setPublishTime(bean.getPublishTime());
					eSArticleFullBean.setPublishTimeText(bean.getPublishTimeText());
					eSArticleFullBean.setReserveAtt1(bean.getReserveAtt1());

					eSArticleFullBean.setReserveAtt2(bean.getReserveAtt2());
					eSArticleFullBean.setSentiment(bean.getSentiment());
					eSArticleFullBean.setTitle(bean.getTitle());
					eSArticleFullBean.setTitleCN(bean.getTitleCN());
					eSArticleFullBean.setUrl(bean.getUrl());

					eSArticleFullBean
							.setWordSeqJson(stanfordNlpTools.partOfSpeech(bean.getTitle() + ". " + bean.getContent()));

					esArticleService.imgrateData2NewIndex(eSArticleFullBean, RootConfiguration.getCdnewsESToIndex());
					isHandledCount ++;
					} catch (Exception e) {
						// TODO: handle exception
					}
				} 
				else 
				{
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					logger.info("the number of unhandled articleQueue:" + 0);
				}
			
		}
	}
}
