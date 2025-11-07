package cuc.cdnews.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cuc.cdnews.config.RootConfiguration;
import cuc.cdnews.domain.ESArticleFullBean;
import cuc.cdnews.domain.EsClassification;
import cuc.cdnews.domain.EsSentiment;
import cuc.cdnews.domain.HotWord;
import cuc.cdnews.domain.NerListObj;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * HTTP文章数据服务类
 * 用于从HTTP接口获取文章数据，替代原有的Elasticsearch数据源
 * 
 * @author system
 */
@Service
public class HttpArticleService {

    public static Logger logger = LogManager.getLogger(HttpArticleService.class.getName());

    // HTTP客户端，复用连接以提高性能
    private OkHttpClient httpClient;

    /**
     * 构造函数，初始化HTTP客户端
     */
    public HttpArticleService() {
        // 从配置文件读取超时时间，默认30秒
        int timeout = RootConfiguration.getHttpArticleApiTimeout();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * 根据最大文章ID和数量从HTTP接口获取文章列表
     * 此方法模拟了EsArticleService.getArticleInfoListByMaxIdNew的功能
     * 
     * @param articleMaxId 最大文章ID，用于分页查询（获取ID大于此值的文章）
     * @param retCount     返回的文章数量
     * @return 文章列表，如果请求失败则返回空列表
     */
    public List<ESArticleFullBean> getArticleInfoListByMaxId(int articleMaxId, int retCount) {
        List<ESArticleFullBean> articleList = new ArrayList<>();

        try {
            // 构建HTTP请求URL，包含分页参数
            // 假设接口支持以下参数：
            // - minId: 最小ID（用于分页）
            // - limit: 返回数量
            String url = buildRequestUrl(articleMaxId, retCount);

            logger.info("从HTTP接口获取文章数据，URL: " + url + ", 最小ID: " + articleMaxId + ", 数量: " + retCount);

            // 构建HTTP请求
            Request request = buildRequest(url);

            // 发送HTTP请求
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("HTTP请求失败，状态码: " + response.code() + ", URL: " + url);
                    return articleList;
                }

                // 解析响应体
                String responseBody = response.body().string();
                articleList = parseResponse(responseBody);

                logger.info("成功从HTTP接口获取 " + articleList.size() + " 条文章数据");
            }

        } catch (IOException e) {
            logger.error("HTTP请求发生IO异常: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("解析HTTP响应时发生异常: " + e.getMessage(), e);
        }

        return articleList;
    }

    /**
     * 构建HTTP请求URL
     * 根据实际接口规范调整URL和参数格式
     * 
     * @param minId 最小文章ID（用于分页）
     * @param limit 返回数量
     * @return 完整的请求URL
     */
    private String buildRequestUrl(int minId, int limit) {
        String baseUrl = RootConfiguration.getHttpArticleApiUrl();

        // 构建查询参数
        // 示例URL格式: http://127.0.0.1:8080/api/articles?minId=1000&limit=100
        // 根据实际接口规范调整参数名称和格式
        StringBuilder urlBuilder = new StringBuilder(baseUrl);

        // 如果URL中已包含参数，使用&连接，否则使用?连接
        if (baseUrl.contains("?")) {
            urlBuilder.append("&");
        } else {
            urlBuilder.append("?");
        }

        urlBuilder.append("minId=").append(minId)
                .append("&limit=").append(limit);

        return urlBuilder.toString();
    }

    /**
     * 构建HTTP请求对象
     * 添加必要的请求头（不包含Authorization，因为从HTTP取数据不需要token）
     * 
     * @param url 请求URL
     * @return Request对象
     */
    private Request buildRequest(String url) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .get(); // 使用GET方法，如果接口需要POST，可以修改为POST

        // 注意：从HTTP取数据不需要token，所以不添加Authorization头
        // 如果将来需要token，可以取消下面的注释
        // String token = RootConfiguration.getHttpArticleApiToken();
        // if (token != null && !token.isEmpty() &&
        // !token.equals("your-api-token-here")) {
        // requestBuilder.addHeader("Authorization", "Bearer " + token);
        // }

        // 添加其他必要的请求头
        requestBuilder.addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json");

        return requestBuilder.build();
    }

    /**
     * 解析HTTP响应体，将JSON数据转换为ESArticleFullBean对象列表
     * 
     * @param responseBody HTTP响应体的JSON字符串
     * @return 文章对象列表
     */
    private List<ESArticleFullBean> parseResponse(String responseBody) {
        List<ESArticleFullBean> articleList = new ArrayList<>();

        try {
            // 解析JSON响应
            // 假设响应格式为：
            // {
            // "code": 200,
            // "data": [
            // { "id": 1, "title": "...", ... },
            // ...
            // ],
            // "total": 100
            // }
            // 根据实际接口响应格式调整解析逻辑

            JSONObject jsonResponse = JSON.parseObject(responseBody);

            // 检查响应状态码（如果接口有返回）
            if (jsonResponse.containsKey("code")) {
                int code = jsonResponse.getIntValue("code");
                if (code != 200) {
                    logger.warn("接口返回错误码: " + code + ", 消息: " + jsonResponse.getString("message"));
                    return articleList;
                }
            }

            // 获取文章数据数组
            JSONArray dataArray = null;
            if (jsonResponse.containsKey("data")) {
                Object dataObj = jsonResponse.get("data");
                if (dataObj instanceof JSONArray) {
                    dataArray = (JSONArray) dataObj;
                }
            } else if (jsonResponse.containsKey("articles")) {
                Object articlesObj = jsonResponse.get("articles");
                if (articlesObj instanceof JSONArray) {
                    dataArray = (JSONArray) articlesObj;
                }
            } else {
                // 尝试将整个响应作为单个对象处理
                dataArray = new JSONArray();
                dataArray.add(jsonResponse);
            }

            if (dataArray == null || dataArray.isEmpty()) {
                logger.info("接口返回数据为空");
                return articleList;
            }

            // 遍历数组，将每个JSON对象转换为ESArticleFullBean
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject articleJson = dataArray.getJSONObject(i);
                ESArticleFullBean bean = convertJsonToBean(articleJson);
                if (bean != null) {
                    articleList.add(bean);
                }
            }

        } catch (Exception e) {
            logger.error("解析JSON响应时发生异常: " + e.getMessage(), e);
            logger.error("响应内容: " + responseBody);
        }

        return articleList;
    }

    /**
     * 将JSON对象转换为ESArticleFullBean对象
     * 根据实际接口返回的字段名称进行映射
     * 
     * @param articleJson JSON对象
     * @return ESArticleFullBean对象
     */
    private ESArticleFullBean convertJsonToBean(JSONObject articleJson) {
        if (articleJson == null) {
            return null;
        }

        try {
            ESArticleFullBean bean = new ESArticleFullBean();

            // 基本字段映射
            bean.setId(articleJson.getIntValue("id"));
            bean.setTitle(articleJson.getString("title"));
            bean.setContent(articleJson.getString("content"));
            bean.setAuthor(articleJson.getString("author"));
            bean.setPublisherId(articleJson.getString("publisherId"));
            bean.setDocType(articleJson.getString("docType"));
            bean.setMediaSourceId(articleJson.getString("mediaSourceId"));
            bean.setPublisherCountry(articleJson.getString("publisherCountry"));
            bean.setPublisherType(articleJson.getIntValue("publisherType"));
            bean.setReserveAtt1(articleJson.getString("reserveAtt1"));
            bean.setReserveAtt2(articleJson.getIntValue("reserveAtt2"));
            bean.setAbstractEN(articleJson.getString("abstractEN"));
            bean.setAbstractCN(articleJson.getString("abstractCN"));
            bean.setTitleCN(articleJson.getString("titleCN"));
            bean.setContentCN(articleJson.getString("contentCN"));
            bean.setIsKeyArticle(articleJson.getIntValue("isKeyArticle"));
            bean.setUrl(articleJson.getString("url"));
            bean.setInclusionTimeText(articleJson.getString("inclusionTimeText"));
            bean.setPublishTimeText(articleJson.getString("publishTimeText"));

            // 日期字段解析
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

            String inclusionTimeStr = articleJson.getString("inclusionTime");
            if (inclusionTimeStr != null && !inclusionTimeStr.isEmpty()) {
                try {
                    Date inclusionTime = parseDate(inclusionTimeStr, sdf, sdfDate);
                    bean.setInclusionTime(inclusionTime);
                } catch (ParseException e) {
                    logger.warn("解析inclusionTime失败: " + inclusionTimeStr);
                }
            }

            String publishTimeStr = articleJson.getString("publishTime");
            if (publishTimeStr != null && !publishTimeStr.isEmpty()) {
                try {
                    Date publishTime = parseDate(publishTimeStr, sdf, sdfDate);
                    bean.setPublishTime(publishTime);
                } catch (ParseException e) {
                    logger.warn("解析publishTime失败: " + publishTimeStr);
                }
            }

            // 解析classification对象
            if (articleJson.containsKey("classification")) {
                JSONObject classificationJson = articleJson.getJSONObject("classification");
                if (classificationJson != null) {
                    EsClassification classification = new EsClassification();
                    classification.setClassificationId(classificationJson.getIntValue("classificationId"));
                    classification.setName(classificationJson.getString("name"));
                    bean.setClassification(classification);
                }
            }

            // 解析sentiment对象
            if (articleJson.containsKey("sentiment")) {
                JSONObject sentimentJson = articleJson.getJSONObject("sentiment");
                if (sentimentJson != null) {
                    EsSentiment sentiment = new EsSentiment();
                    sentiment.setCla(sentimentJson.getIntValue("class"));
                    sentiment.setClaName(sentimentJson.getString("claName"));
                    sentiment.setNeu_weight(sentimentJson.getDoubleValue("neu_weight"));
                    sentiment.setNeg_weight(sentimentJson.getDoubleValue("neg_weight"));
                    sentiment.setPos_weight(sentimentJson.getDoubleValue("pos_weight"));
                    bean.setSentiment(sentiment);
                }
            }

            // 解析wordSeqJson数组
            if (articleJson.containsKey("wordSeqJson")) {
                Object wordSeqObj = articleJson.get("wordSeqJson");
                if (wordSeqObj instanceof JSONArray) {
                    JSONArray wordSeqArray = (JSONArray) wordSeqObj;
                    List<HotWord> wordSeqList = new ArrayList<>();
                    for (int i = 0; i < wordSeqArray.size(); i++) {
                        JSONObject wordJson = wordSeqArray.getJSONObject(i);
                        HotWord hotWord = new HotWord();
                        hotWord.setName(wordJson.getString("name"));
                        hotWord.setValue(wordJson.getIntValue("value"));
                        hotWord.setPartsOfSpeech(wordJson.getIntValue("partsOfSpeech"));
                        wordSeqList.add(hotWord);
                    }
                    bean.setWordSeqJson(wordSeqList);
                }
            }

            // 解析nerList对象
            if (articleJson.containsKey("nerList")) {
                Object nerListObj = articleJson.get("nerList");
                if (nerListObj instanceof JSONObject) {
                    JSONObject nerListJson = (JSONObject) nerListObj;
                    NerListObj nerList = new NerListObj();

                    // 解析location列表
                    if (nerListJson.containsKey("location")) {
                        JSONArray locationArray = nerListJson.getJSONArray("location");
                        if (locationArray != null) {
                            nerList.setLocation(parseHotWordList(locationArray));
                        }
                    }

                    // 解析organization列表
                    if (nerListJson.containsKey("organization")) {
                        JSONArray orgArray = nerListJson.getJSONArray("organization");
                        if (orgArray != null) {
                            nerList.setOrganization(parseHotWordList(orgArray));
                        }
                    }

                    // 解析person列表
                    if (nerListJson.containsKey("person")) {
                        JSONArray personArray = nerListJson.getJSONArray("person");
                        if (personArray != null) {
                            nerList.setPerson(parseHotWordList(personArray));
                        }
                    }

                    bean.setNerList(nerList);
                }
            }

            // 解析articleVector数组
            if (articleJson.containsKey("articleVector")) {
                JSONArray vectorArray = articleJson.getJSONArray("articleVector");
                if (vectorArray != null && !vectorArray.isEmpty()) {
                    Double[] vector = new Double[vectorArray.size()];
                    for (int i = 0; i < vectorArray.size(); i++) {
                        vector[i] = vectorArray.getDouble(i);
                    }
                    bean.setArticleVector(vector);
                }
            }

            return bean;

        } catch (Exception e) {
            logger.error("转换JSON对象为Bean时发生异常: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解析日期字符串，支持多种格式
     * 
     * @param dateStr 日期字符串
     * @param sdf     完整日期时间格式
     * @param sdfDate 日期格式
     * @return Date对象
     * @throws ParseException 解析异常
     */
    private Date parseDate(String dateStr, SimpleDateFormat sdf, SimpleDateFormat sdfDate) throws ParseException {
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return sdfDate.parse(dateStr);
        }
    }

    /**
     * 解析HotWord列表
     * 
     * @param wordArray JSON数组
     * @return HotWord列表
     */
    private List<HotWord> parseHotWordList(JSONArray wordArray) {
        List<HotWord> wordList = new ArrayList<>();
        if (wordArray == null || wordArray.isEmpty()) {
            return wordList;
        }

        for (int i = 0; i < wordArray.size(); i++) {
            JSONObject wordJson = wordArray.getJSONObject(i);
            if (wordJson != null) {
                HotWord hotWord = new HotWord();
                hotWord.setName(wordJson.getString("name"));
                hotWord.setValue(wordJson.getIntValue("value"));
                hotWord.setPartsOfSpeech(wordJson.getIntValue("partsOfSpeech"));
                wordList.add(hotWord);
            }
        }

        return wordList;
    }
}
