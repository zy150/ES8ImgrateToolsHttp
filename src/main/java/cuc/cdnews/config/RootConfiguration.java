package cuc.cdnews.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RootConfiguration {

    private static String fileSaveRootPath;

    private static String baseEsSyncUrl;
    
    private static int queueMaxSize;

    private static String cdnewsESfromIndex;
    
    private static String cdnewsESToIndex;
    
    private static int threadSize;

	private static String cdnewsAiSyncUrl;
	private static String cdnewsAiSyncDatasetId;
	private static String cdnewsAiSyncDocumentId;
	private static String cdnewsAiSyncApiToken;
	public static String getCdnewsAiSyncUrl() {
		return cdnewsAiSyncUrl;
	}
	@Value("${config.ai.sync.url}")
	public void setCdnewsAiSyncUrl(String cdnewsAiSyncUrl) {
		RootConfiguration.cdnewsAiSyncUrl = cdnewsAiSyncUrl;
	}

	public static String getCdnewsAiSyncDatasetId() {
		return cdnewsAiSyncDatasetId;
	}
	@Value("${config.ai.sync.datasetId}")
	public void setCdnewsAiSyncDatasetId(String cdnewsAiSyncDatasetId) {
		RootConfiguration.cdnewsAiSyncDatasetId = cdnewsAiSyncDatasetId;
	}

	public static String getCdnewsAiSyncDocumentId() {
		return cdnewsAiSyncDocumentId;
	}
	@Value("${config.ai.sync.documentId}")
	public void setCdnewsAiSyncDocumentId(String cdnewsAiSyncDocumentId) {
		RootConfiguration.cdnewsAiSyncDocumentId = cdnewsAiSyncDocumentId;
	}

	public static String getCdnewsAiSyncApiToken() {
		return cdnewsAiSyncApiToken;
	}
	@Value("${config.ai.sync.apiToken}")
	public void setCdnewsAiSyncApiToken(String cdnewsAiSyncApiToken) {
		RootConfiguration.cdnewsAiSyncApiToken = cdnewsAiSyncApiToken;
	}
	// ==================================
    private static String esHost;
	

    private static boolean esNnable;
    
    private static int esPort;
    
    
    private static String esUserName;
    
    
    private static String esPpassWord;

    private static String esTempCrtName;
//    
//    @Value("${file.save.root.path}")
//	public static  void setFileSaveRootPath(String fileSaveRootPath) {
//		RootConfiguration.fileSaveRootPath = fileSaveRootPath;
//	}
//
//	@Value("${config.queue.max.size}")
//	public static void setQueueMaxSize(int queueMaxSize) {
//		RootConfiguration.queueMaxSize = queueMaxSize;
//	}

	public static String getEsHost() {
		return esHost;
	}
	@Value("${spring.elasticsearch.rest.host}")
	public void setEsHost(String esHost) {
		RootConfiguration.esHost = esHost;
	}

	public static boolean isEsNnable() {
		return esNnable;
	}
    @Value("${spring.elasticsearch.rest.enable}")
	public void setEsNnable(boolean esNnable) {
    	RootConfiguration.esNnable = esNnable;
	}

	public static int getEsPort() {
		return esPort;
	}
	@Value("${spring.elasticsearch.rest.port}")
	public void setEsPort(int esPort) {
		RootConfiguration.esPort = esPort;
	}

	public static String getEsUserName() {
		return esUserName;
	}
	@Value("${spring.elasticsearch.rest.username}")
	public void setEsUserName(String esUserName) {
		RootConfiguration.esUserName = esUserName;
	}

	public static String getEsPpassWord() {
		return esPpassWord;
	}
	@Value("${spring.elasticsearch.rest.password}")
	public void setEsPpassWord(String esPpassWord) {
		RootConfiguration.esPpassWord = esPpassWord;
	}

	public static String getEsTempCrtName() {
		return esTempCrtName;
	}
	@Value("${spring.elasticsearch.rest.crtName}")
	public void setEsTempCrtName(String esTempCrtName) {
		RootConfiguration.esTempCrtName = esTempCrtName;
	}

	public static String getBaseEsSyncUrl() {
		return baseEsSyncUrl;
	}
	
	@Value("${config.base.es.sync.url}")
	public  void setBaseEsSyncUrl(String baseEsSyncUrl) {
		RootConfiguration.baseEsSyncUrl = baseEsSyncUrl;
	}

	public static String getFileSaveRootPath() {
		return fileSaveRootPath;
	}
	@Value("${config.file.save.root.path}")
	public  void setFileSaveRootPath(String fileSaveRootPath) {
		RootConfiguration.fileSaveRootPath = fileSaveRootPath;
	}

	public static int getQueueMaxSize() {
		return queueMaxSize;
	}
	@Value("${config.queue.max.size}")
	public  void setQueueMaxSize(int queueMaxSize) {
		RootConfiguration.queueMaxSize = queueMaxSize;
	}


	public static String getCdnewsESfromIndex() {
		return cdnewsESfromIndex;
	}
	@Value("${config.es.from.index}")
	public void setCdnewsESfromIndex(String cdnewsESfromIndex) {
		RootConfiguration.cdnewsESfromIndex = cdnewsESfromIndex;
	}

	public static int getThreadSize() {
		return threadSize;
	}
	@Value("${config.app.thread.size}")
	public  void setThreadSize(int threadSize) {
		RootConfiguration.threadSize = threadSize;
	}

	public static String getCdnewsESToIndex() {
		return cdnewsESToIndex;
	}

	@Value("${config.es.to.index}")
	public  void setCdnewsESToIndex(String cdnewsESToIndex) {
		RootConfiguration.cdnewsESToIndex = cdnewsESToIndex;
	}

	// ==================================
	// HTTP文章数据接口配置
	// ==================================
	private static String httpArticleApiUrl;
	private static String httpArticleApiToken;
	private static int httpArticleApiTimeout;

	public static String getHttpArticleApiUrl() {
		return httpArticleApiUrl;
	}

	@Value("${config.http.article.api.url}")
	public void setHttpArticleApiUrl(String httpArticleApiUrl) {
		RootConfiguration.httpArticleApiUrl = httpArticleApiUrl;
	}

	public static String getHttpArticleApiToken() {
		return httpArticleApiToken;
	}

	@Value("${config.http.article.api.token}")
	public void setHttpArticleApiToken(String httpArticleApiToken) {
		RootConfiguration.httpArticleApiToken = httpArticleApiToken;
	}

	public static int getHttpArticleApiTimeout() {
		return httpArticleApiTimeout;
	}

	@Value("${config.http.article.api.timeout:30000}")
	public void setHttpArticleApiTimeout(int httpArticleApiTimeout) {
		RootConfiguration.httpArticleApiTimeout = httpArticleApiTimeout;
	}
}
