package cuc.cdnews.ui;

import okhttp3.*; // 导入OkHttp的所有相关类

import java.io.IOException; // 导入IO异常处理
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cuc.cdnews.domain.HotWord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import cuc.cdnews.config.AppConfg;
import cuc.cdnews.config.RootConfiguration;
import cuc.cdnews.data.HttpArticleService;
import cuc.cdnews.domain.ESArticleFullBean;
import cuc.cdnews.domain.ESArticleQueueManager;
import cuc.cdnews.thread.HttpLoadProcessThread;
import cuc.cdnews.thread.HttpSendProcessThread;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Component
public class App2 {
    public static Logger logger = LogManager.getLogger(App2.class.getName());

    @SuppressWarnings("resource")

    /**
     * 将单个文章数据通过HTTP POST请求发送到指定的API接口。
     * 此方法是同步阻塞的，直到收到服务器响应为止。
     *
     * @param bean       要发送的文章数据对象
     * @param client     复用的OkHttpClient实例
     * @param baseUrl    API的基础URL, 例如 "http://127.0.0.1:8000"
     * @param datasetId  数据集ID
     * @param documentId 文档ID
     * @param apiToken   用于Authorization头部的Bearer Token
     * @return 服务器返回的HTTP状态码 (例如 200 表示成功, -1 表示代码内部异常)
     */
    public static int sendArticleToApi(ESArticleFullBean bean, OkHttpClient client, String baseUrl,
            String datasetId, String documentId, String apiToken) {

        // 1. 调用之前的函数，将Bean转换为JSON字符串
        String jsonBody = convertArticleToJson(bean);

        // 2. 构建请求体 (Request Body)
        MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, jsonBody);

        // 3. 构建完整的URL
        String url = String.format("%s/api/v1/datasets/%s/documents/%s/article/chunks",
                baseUrl, datasetId, documentId);

        // 4. 构建HTTP请求 (Request)
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiToken)
                .addHeader("Content-Type", "application/json") // 明确指定Content-Type
                .build();

        // 5. 发送请求并获取响应
        try (Response response = client.newCall(request).execute()) {
            int statusCode = response.code();
            // 读取响应体（即使不使用，也需要读取以释放资源）
            if (response.body() != null) {
                response.body().string(); // 读取响应体，以便调试
            }
            // System.out.println(responseBodyString);
            // if (response.isSuccessful()) {
            // System.out.println("成功发送 Article ID: " + bean.getId() + "，服务器响应码: " +
            // statusCode);
            // } else {
            // System.err.println("发送 Article ID: " + bean.getId() + " 失败，服务器响应码: " +
            // statusCode);
            // System.err.println("服务器响应内容: " + responseBodyString);
            // }
            return statusCode; // 返回服务器的HTTP状态码

        } catch (IOException e) {
            System.err.println("发送 Article ID: " + bean.getId() + " 时发生网络或IO异常。");
            e.printStackTrace();
            return -1; // 返回-1表示代码层面发生异常
        }
    }

    public static void main(String args[]) throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfg.class);

        // 注册关闭钩子，确保程序退出时正确关闭context
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("正在关闭应用程序...");
            context.close();
        }));

        try {
            // 打印配置信息
            logger.info("========== 多线程HTTP数据迁移工具启动 ==========");
            logger.info("HTTP文章数据接口URL: " + RootConfiguration.getHttpArticleApiUrl());
            logger.info("目标API URL: " + RootConfiguration.getCdnewsAiSyncUrl());
            logger.info("队列最大大小: " + RootConfiguration.getQueueMaxSize());
            logger.info("发送线程数量: " + RootConfiguration.getThreadSize());
            logger.info("文件保存根目录: " + RootConfiguration.getFileSaveRootPath());

            // 获取HTTP文章服务
            HttpArticleService httpArticleService = context.getBean(HttpArticleService.class);

            // 重置计数器
            HttpSendProcessThread.resetCounters();

            // 1. 启动HTTP数据加载线程（从HTTP接口获取数据）
            logger.info("启动HTTP数据加载线程...");
            HttpLoadProcessThread loadThread = new HttpLoadProcessThread(httpArticleService);
            Thread loadThreadWrapper = new Thread(loadThread, "HTTP-Load-Thread");
            loadThreadWrapper.setDaemon(false);
            loadThreadWrapper.start();

            // 2. 启动多个HTTP数据发送线程（发送数据到目标API）
            int threadSize = RootConfiguration.getThreadSize();
            if (threadSize <= 0) {
                threadSize = 5; // 默认5个线程
                logger.warn("线程数配置无效，使用默认值: " + threadSize);
            }

            logger.info("启动 " + threadSize + " 个HTTP数据发送线程...");
            Thread[] sendThreads = new Thread[threadSize];
            for (int i = 0; i < threadSize; i++) {
                HttpSendProcessThread sendThread = new HttpSendProcessThread();
                sendThreads[i] = new Thread(sendThread, "HTTP-Send-Thread-" + (i + 1));
                sendThreads[i].setDaemon(false);
                sendThreads[i].start();
                logger.info("HTTP发送线程 " + (i + 1) + " 已启动");
            }

            // 3. 主线程定期输出统计信息
            logger.info("========== 所有线程已启动，开始处理数据 ==========");
            while (true) {
                Thread.sleep(60000); // 每60秒输出一次统计信息

                int queueSize = ESArticleQueueManager.getSize();
                int successCount = HttpSendProcessThread.getSuccessCount();
                int failCount = HttpSendProcessThread.getFailCount();
                int totalProcessed = successCount + failCount;

                logger.info("========== 运行统计 ==========");
                logger.info("当前队列大小: " + queueSize);
                logger.info("成功发送数量: " + successCount);
                logger.info("失败数量: " + failCount);
                logger.info("总处理数量: " + totalProcessed);
                if (totalProcessed > 0) {
                    double successRate = (double) successCount / totalProcessed * 100;
                    logger.info("成功率: " + String.format("%.2f", successRate) + "%");
                }
                logger.info("=============================");

                // 检查线程状态
                boolean allAlive = loadThreadWrapper.isAlive();
                for (Thread t : sendThreads) {
                    if (!t.isAlive()) {
                        allAlive = false;
                        logger.error("发现线程已停止: " + t.getName());
                    }
                }

                if (!allAlive) {
                    logger.error("检测到有线程已停止，请检查日志");
                }
            }

        } catch (Exception ex) {
            logger.error("程序发生异常: " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    public static void appendArticlesToJsonFile(List<ESArticleFullBean> articles, String filePath) throws IOException {
        // 如果传入的列表为空或null，则直接返回，不执行任何操作
        if (articles == null || articles.isEmpty()) {
            return;
        }

        // 1. 创建一个列表，用于存放要写入文件的每一行文本
        List<String> lines = new ArrayList<>();
        for (ESArticleFullBean bean : articles) {
            // 2. 将每个Java对象转换为一个紧凑的JSON字符串
            String jsonLine = JSON.toJSONString(bean);
            lines.add(jsonLine);
        }

        // 3. 使用Java NIO，将所有行一次性地追加写入到文件末尾
        // StandardOpenOption.CREATE: 如果文件不存在，则创建它
        // StandardOpenOption.APPEND: 如果文件已存在，则在末尾追加内容
        Files.write(Paths.get(filePath), lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    /**
     * 【最终正确版】将 ESArticleFullBean 对象转换为与最终版Python脚本逻辑完全一致的JSON字符串。
     * 本函数的转换逻辑严格、精确地遵循了用户提供的、已验证成功的Python代码，不进行任何更改。
     *
     * @param bean 从ES请求得到的源数据对象
     * @return 符合最终Python脚本逻辑的JSON字符串
     */
    public static String convertArticleToJson(ESArticleFullBean bean) {
        if (bean == null) {
            return "{}";
        }

        JSONObject targetJson = new JSONObject();

        // 1. 处理 wordSeq (与Python逻辑完全一致)
        JSONObject wordSeq = new JSONObject();
        if (bean.getWordSeqJson() != null && !bean.getWordSeqJson().isEmpty()) {
            JSONArray names = new JSONArray();
            JSONArray partsOfSpeeches = new JSONArray();
            JSONArray values = new JSONArray();

            for (HotWord hw : bean.getWordSeqJson()) {
                names.add(hw.getName());
                partsOfSpeeches.add(hw.getPartsOfSpeech());
                values.add(hw.getValue());
            }
            wordSeq.put("name", names);
            wordSeq.put("partsOfSpeech", partsOfSpeeches);
            wordSeq.put("value", values);
        }

        // 2. 填充顶层字段
        targetJson.put("articleId", bean.getId());
        targetJson.put("title", bean.getTitle());
        targetJson.put("content", bean.getContent());
        targetJson.put("author", bean.getAuthor());

        // 3. 处理 publisher (与Python逻辑完全一致)
        JSONObject publisher = new JSONObject();
        // 假设 ESArticleFullBean 中有 getPublisherId(), getPublisherCountry(),
        // getPublisherType() 方法
        publisher.put("p_id_kwd", bean.getPublisherId());
        publisher.put("p_country_kwd", bean.getPublisherCountry() != null ? bean.getPublisherCountry() : "");
        publisher.put("p_type_int", bean.getPublisherType());
        targetJson.put("publisher", publisher);

        targetJson.put("docType", bean.getDocType());
        targetJson.put("mediaSourceId", bean.getMediaSourceId());

        // 4. 处理时间格式 (与Python逻辑完全一致: YYYY-MM-DD)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (bean.getInclusionTime() != null) {
            targetJson.put("inclusionTime", sdf.format(bean.getInclusionTime()));
        }
        if (bean.getPublishTime() != null) {
            targetJson.put("publishTime", sdf.format(bean.getPublishTime()));
        }

        targetJson.put("inclusionTimeText", bean.getInclusionTimeText() != null ? bean.getInclusionTimeText() : "");
        targetJson.put("publishTimeText", bean.getPublishTimeText() != null ? bean.getPublishTimeText() : "");
        targetJson.put("publisherId", bean.getPublisherId() != null ? bean.getPublisherId() : "");
        targetJson.put("publisherCountry", bean.getPublisherCountry() != null ? bean.getPublisherCountry() : "");
        targetJson.put("publisherType", bean.getPublisherType());
        targetJson.put("reserveAtt1", bean.getReserveAtt1() != null ? bean.getReserveAtt1() : "");
        targetJson.put("reserveAtt2", bean.getReserveAtt2() );
        // 5. 处理 classification (与Python逻辑完全一致)
        if (bean.getClassification() != null) {
            JSONObject classification = new JSONObject();
            // 假设 EsClassification 类有 getClassificationId() 和 getName() 方法
            classification.put("class_int", bean.getClassification().getClassificationId());
            classification.put("name_kwd", bean.getClassification().getName());
            targetJson.put("classification", classification);
        }

        // 6. 处理 sentiment (与Python逻辑完全一致)
        if (bean.getSentiment() != null) {
            JSONObject sentiment = new JSONObject();
            // 假设 EsSentiment 类有 getClaName(), getNeu_weight(), getPos_weight(), getCla() 方法
            sentiment.put("claName_kwd",
                    bean.getSentiment().getClaName() != null ? bean.getSentiment().getClaName() : "");
            sentiment.put("neu_weight_flt", bean.getSentiment().getNeu_weight());
            sentiment.put("neg_weight_flt", bean.getSentiment().getNeu_weight()); // 严格遵从Python逻辑
            sentiment.put("pos_weight_flt", bean.getSentiment().getPos_weight());
            sentiment.put("class_int", bean.getSentiment().getCla() != 0 ? bean.getSentiment().getCla() : ""); // 假设getCla()返回int
            targetJson.put("sentiment", sentiment);
        }

        // 7. 处理其他可能不存在的字段 (与Python逻辑完全一致)
        targetJson.put("titleCN", bean.getTitleCN() != null ? bean.getTitleCN() : "");
        targetJson.put("contentCN", bean.getContentCN() != null ? bean.getContentCN() : "");
        targetJson.put("summaryCN", bean.getAbstractCN() != null ? bean.getAbstractCN() : ""); // 对应 abstractCN
        targetJson.put("summaryEN", bean.getAbstractEN() != null ? bean.getAbstractEN() : ""); // 对应 abstractEN
        targetJson.put("isKeyArticle", bean.getIsKeyArticle());
        targetJson.put("url", bean.getUrl());

        targetJson.put("wordSeq", wordSeq);

        // 8. 处理 nerList (与Python逻辑完全一致，这是成功的关键)
        // 关键点：将nerList对象转换为一个JSON字符串，而不是一个JSON对象。
        if (bean.getNerList() != null) {
            targetJson.put("nerList", bean.getNerList());
        } else {
            // Python中 `json.dumps({})` 是 "{}"
            targetJson.put("nerList", "{}");
        }

        // 返回最终的JSON字符串 (不进行美化，以匹配Python脚本默认的紧凑输出)
        return targetJson.toJSONString();
    }

}
