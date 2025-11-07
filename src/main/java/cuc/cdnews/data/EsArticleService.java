package cuc.cdnews.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import cuc.cdnews.domain.ArticleInfo;
import cuc.cdnews.domain.ESArticleFullBean;
import cuc.cdnews.domain.HotWord;
import cuc.cdnews.domain.NerList;
import cuc.cdnews.domain.NerListObj;

@Service
public class EsArticleService {

	public static Logger logger = LogManager.getLogger(EsArticleService.class.getName());
	@Autowired
	private ElasticsearchClient elasticsearchClient;

	/**
	 * 根据获取文章列表
	 */
	public List<ArticleInfo> getArticleInfoListByMaxId(int articleMaxId, int retCount) {
		List<ArticleInfo> esArticleList = new ArrayList<ArticleInfo>();
		try {

			List<String> retFields = new ArrayList<String>();
			retFields.add("id");
			retFields.add("title");
			retFields.add("content");
			retFields.add("author");
			retFields.add("publisherId");

			retFields.add("docType");
			retFields.add("mediaSourceId");
			retFields.add("inclusionTime");
			retFields.add("inclusionTimeText");
			retFields.add("publishTime");

			retFields.add("publishTimeText");
			retFields.add("publisherCountry");
			retFields.add("publisherType");
			retFields.add("classification");
			retFields.add("sentiment");

			retFields.add("reserveAtt1");
			retFields.add("reserveAtt2");
			retFields.add("abstractEN");
			retFields.add("abstractCN");
			retFields.add("titleCN");

			retFields.add("contentCN");
			retFields.add("wordSeqJson");
			retFields.add("nerList");
			retFields.add("isKeyArticle");
			retFields.add("url");

			retFields.add("articleVector");
			// 使用 elasticsearchClient.search 方法替换原有的 restHighLevelClient.search

			// ElasticSearchConfig elasticSearchConfig = new ElasticSearchConfig();

			SearchRequest searchRequest = ESRequestBuilderTools.getArticleListByIdRangeBuilder(articleMaxId, retFields,
					retCount);
			SearchResponse<ArticleInfo> searchResponse = elasticsearchClient.search(searchRequest, ArticleInfo.class);

			for (Hit<ArticleInfo> hit : searchResponse.hits().hits()) {
				ArticleInfo esArticleBean = (ArticleInfo) hit.source();
				esArticleBean.setSearchScore(hit.score());
				esArticleList.add(esArticleBean);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return esArticleList;
	}

	/**
	 * 根据获取文章列表
	 */
	public List<ESArticleFullBean> getArticleInfoListByMaxIdNew(int articleMaxId, int retCount) {
		List<ESArticleFullBean> esArticleList = new ArrayList<ESArticleFullBean>();
		try {

			List<String> retFields = new ArrayList<String>();
			retFields.add("id");
			retFields.add("title");
			retFields.add("content");
			retFields.add("author");
			retFields.add("publisherId");

			retFields.add("docType");
			retFields.add("mediaSourceId");
			retFields.add("inclusionTime");
			retFields.add("inclusionTimeText");
			retFields.add("publishTime");

			retFields.add("publishTimeText");
			retFields.add("publisherCountry");
			retFields.add("publisherType");
			retFields.add("classification");
			retFields.add("sentiment");

			retFields.add("reserveAtt1");
			retFields.add("reserveAtt2");
			retFields.add("abstractEN");
			retFields.add("abstractCN");
			retFields.add("titleCN");

			retFields.add("contentCN");
			retFields.add("wordSeqJson");
			retFields.add("nerList");
			retFields.add("isKeyArticle");
			retFields.add("url");

			retFields.add("articleVector");
			// 使用 elasticsearchClient.search 方法替换原有的 restHighLevelClient.search

			// ElasticSearchConfig elasticSearchConfig = new ElasticSearchConfig();

			SearchRequest searchRequest = ESRequestBuilderTools.getArticleListByIdRangeBuilderNew(articleMaxId, retFields,
					retCount);
			SearchResponse<ESArticleFullBean> searchResponse = elasticsearchClient.search(searchRequest,
					ESArticleFullBean.class);

			for (Hit<ESArticleFullBean> hit : searchResponse.hits().hits()) {
				ESArticleFullBean esArticleBean = (ESArticleFullBean) hit.source();
				esArticleBean.setSearchScore(hit.score());
				esArticleList.add(esArticleBean);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return esArticleList;
	}

	/**
	 * This method is used to synchronize the results processed by the large
	 * language model to the ES server
	 * 
	 * @param bean
	 */
	public Boolean imgrateData2NewIndex(ESArticleFullBean bean, String newIndexName) {

		boolean ret = false;
		try {

			IndexRequest.Builder<ESArticleFullBean> indexReqBuilder = new IndexRequest.Builder<>();
			indexReqBuilder.index(newIndexName);
			indexReqBuilder.id(String.valueOf(bean.getId()));
			indexReqBuilder.document(bean);

			IndexResponse response = elasticsearchClient.index(indexReqBuilder.build());
			logger.info("Article Indexed with version " + bean.getId() + "_" + response.version());

			ret = true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Article Indexed failed:" + bean.getId());
		}
		return ret;
	}

	public NerListObj list2HotWordList(String ss) {

		// 第一步：去除所有反斜杠转义符
		String unescaped = ss.replaceAll("\\\\", "");

		// 第二步：去除首尾的引号（如果需要）
		unescaped = unescaped.replaceAll("^\"|\"$", "");

		// System.out.println("处理后的字符串:");
		// System.out.println(unescaped);

		NerList berObj = JSON.parseObject(unescaped, NerList.class);

		NerListObj nerListObj = new NerListObj();

		nerListObj.setLocation(str2List(berObj.getLocation()));
		nerListObj.setOrganization(str2List(berObj.getOrganization()));
		nerListObj.setPerson(str2List(berObj.getPerson()));

		return nerListObj;

	}

	public NerListObj nerList2NerListObj(NerList berObj) {

		NerListObj nerListObj = new NerListObj();

		nerListObj.setLocation(str2List(berObj.getLocation()));
		nerListObj.setOrganization(str2List(berObj.getOrganization()));
		nerListObj.setPerson(str2List(berObj.getPerson()));

		return nerListObj;

	}

	public List<HotWord> str2List(List<String> ss) {
		List<HotWord> wcbs = new ArrayList<HotWord>();
		for (String s : ss) {
			try {
				String[] analysis_content = s.split("_");

				HotWord b = new HotWord();
				b.setName(analysis_content[0]);
				b.setValue(Integer.parseInt(analysis_content[1]));
				b.setPartsOfSpeech(-1);

				wcbs.add(b);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return wcbs;
	}
}
