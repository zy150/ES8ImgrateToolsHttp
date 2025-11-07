# ES8ImgrateTools - HTTP数据迁移工具

## 项目简介

这是一个多线程HTTP数据迁移工具，用于从HTTP接口获取文章数据，并通过多线程方式发送到目标API。支持高并发处理，提高数据迁移效率。

## 功能特性

- ✅ 多线程从HTTP接口获取文章数据
- ✅ 多线程发送数据到目标API
- ✅ 线程安全的队列管理
- ✅ 自动记录已处理的最大ID，支持断点续传
- ✅ 实时统计成功/失败数量和成功率
- ✅ 完整的日志记录

## 配置说明

### 配置文件位置

配置文件位于：`src/main/resources/application.properties`

### 必需配置项

#### 1. HTTP文章数据接口配置

```properties
# HTTP文章数据接口URL（必需）
# 示例：http://127.0.0.1:8080/api/articles
config.http.article.api.url=http://127.0.0.1:8080/api/articles

# HTTP接口超时时间（毫秒，可选，默认30000）
config.http.article.api.timeout=30000

# HTTP接口Token（可选，从HTTP取数据通常不需要token）
# 如果需要token，取消注释并填写实际token
# config.http.article.api.token=your-api-token-here
```

#### 2. 目标API配置（发送数据的目标）

```properties
# 目标API的基础URL
config.ai.sync.url=http://222.31.96.15:9382

# 数据集ID
config.ai.sync.datasetId=597996acabdd11f08c690242ac130006

# 文档ID
config.ai.sync.documentId=6ebf9738abe111f084970242ac130006

# API Token（发送数据需要token）
config.ai.sync.apiToken=ragflow-NlMDM2YWRlYWJlMjExZjA4YzU2MDI0Mm
```

#### 3. 队列和线程配置

```properties
# 队列最大大小（建议设置为线程数的10-20倍）
config.queue.max.size=100

# 发送线程数量（多线程发送，建议设置为5-10）
config.app.thread.size=5
```

#### 4. 文件保存路径配置

```properties
# 文件保存根目录
config.file.save.root.path=cdnewsfiles
```

**注意**：程序会在该目录下创建 `ES8ImgrateTools` 子目录，用于存储：
- `maxProcessedArticleId.txt` - 记录已处理的最大文章ID（用于断点续传）

### 配置示例

完整的配置示例：

```properties
# HTTP文章数据接口配置
config.http.article.api.url=http://127.0.0.1:8080/api/articles
config.http.article.api.timeout=30000

# 目标API配置
config.ai.sync.url=http://222.31.96.15:9382
config.ai.sync.datasetId=597996acabdd11f08c690242ac130006
config.ai.sync.documentId=6ebf9738abe111f084970242ac130006
config.ai.sync.apiToken=ragflow-NlMDM2YWRlYWJlMjExZjA4YzU2MDI0Mm

# 队列和线程配置
config.queue.max.size=100
config.app.thread.size=5

# 文件保存路径
config.file.save.root.path=cdnewsfiles
```

## 使用方法

### 1. 运行程序

直接运行 `App2` 类的 `main` 方法：

```java
cuc.cdnews.ui.App2.main(String[] args)
```

### 2. 程序运行流程

1. **启动阶段**：
   - 读取配置文件
   - 初始化Spring上下文
   - 打印配置信息

2. **线程启动**：
   - 启动1个HTTP数据加载线程（`HTTP-Load-Thread`）
   - 启动N个HTTP数据发送线程（`HTTP-Send-Thread-1` 到 `HTTP-Send-Thread-N`）

3. **运行阶段**：
   - 加载线程持续从HTTP接口获取数据并放入队列
   - 发送线程从队列取出数据并发送到目标API
   - 主线程每60秒输出一次统计信息

4. **统计信息**：
   - 当前队列大小
   - 成功发送数量
   - 失败数量
   - 总处理数量
   - 成功率

### 3. 停止程序

使用 `Ctrl+C` 或关闭程序窗口，程序会自动：
- 关闭所有线程
- 保存已处理的最大ID
- 关闭Spring上下文

## 如何修改拉取逻辑

### 修改HTTP接口URL格式

编辑文件：`src/main/java/cuc/cdnews/data/HttpArticleService.java`

找到 `buildRequestUrl` 方法（约第105行）：

```java
private String buildRequestUrl(int minId, int limit) {
    String baseUrl = RootConfiguration.getHttpArticleApiUrl();
    
    // 修改这里的URL构建逻辑
    // 当前格式：http://127.0.0.1:8080/api/articles?minId=1000&limit=100
    StringBuilder urlBuilder = new StringBuilder(baseUrl);
    
    if (baseUrl.contains("?")) {
        urlBuilder.append("&");
    } else {
        urlBuilder.append("?");
    }
    
    // 根据实际接口调整参数名称
    urlBuilder.append("minId=").append(minId)
              .append("&limit=").append(limit);
    
    return urlBuilder.toString();
}
```

**示例**：如果接口使用 `page` 和 `pageSize` 参数：

```java
urlBuilder.append("page=").append(pageNumber)
          .append("&pageSize=").append(limit);
```

### 修改HTTP请求方法（GET/POST）

编辑文件：`src/main/java/cuc/cdnews/data/HttpArticleService.java`

找到 `buildRequest` 方法（约第127行）：

```java
private Request buildRequest(String url) {
    Request.Builder requestBuilder = new Request.Builder()
            .url(url)
            .get(); // 改为 .post(body) 如果需要POST请求
    
    // 如果需要POST，需要构建RequestBody
    // MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    // RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, jsonBody);
    // requestBuilder.post(body);
    
    return requestBuilder.build();
}
```

### 修改JSON响应解析逻辑

编辑文件：`src/main/java/cuc/cdnews/data/HttpArticleService.java`

找到 `parseResponse` 方法（约第153行）：

```java
private List<ESArticleFullBean> parseResponse(String responseBody) {
    // 根据实际接口响应格式修改解析逻辑
    
    JSONObject jsonResponse = JSON.parseObject(responseBody);
    
    // 如果响应格式不同，修改这里的解析逻辑
    // 例如：如果响应直接是数组，使用 JSON.parseArray(responseBody)
    
    JSONArray dataArray = jsonResponse.getJSONArray("data");
    // ...
}
```

**常见响应格式**：

1. **标准格式**（带code和data）：
```json
{
  "code": 200,
  "data": [...],
  "total": 100
}
```

2. **直接数组格式**：
```json
[...]
```

3. **自定义格式**：
```json
{
  "articles": [...],
  "count": 100
}
```

### 修改字段映射逻辑

编辑文件：`src/main/java/cuc/cdnews/data/HttpArticleService.java`

找到 `convertJsonToBean` 方法（约第220行），根据实际接口返回的字段名称修改映射：

```java
private ESArticleFullBean convertJsonToBean(JSONObject articleJson) {
    ESArticleFullBean bean = new ESArticleFullBean();
    
    // 根据实际接口字段名称修改
    bean.setId(articleJson.getIntValue("id"));  // 如果接口返回的是 articleId，改为 "articleId"
    bean.setTitle(articleJson.getString("title"));
    // ...
}
```

### 修改每次获取的数据量

编辑文件：`src/main/java/cuc/cdnews/thread/HttpLoadProcessThread.java`

找到 `run` 方法中的这一行（约第57行）：

```java
// 从HTTP接口获取文章列表（每次获取1000条）
List<ESArticleFullBean> beans = httpArticleService.getArticleInfoListByMaxId(maxid, 1000);
```

修改 `1000` 为你想要的数量。

## 如何修改发送逻辑

### 修改发送的API地址

编辑文件：`src/main/java/cuc/cdnews/ui/App2.java`

找到 `sendArticleToApi` 方法（约第57行），修改URL构建逻辑：

```java
// 构建完整的URL
String url = String.format("%s/api/v1/datasets/%s/documents/%s/article/chunks",
        baseUrl, datasetId, documentId);
```

### 修改请求体格式

编辑文件：`src/main/java/cuc/cdnews/ui/App2.java`

找到 `convertArticleToJson` 方法（约第220行），修改JSON转换逻辑：

```java
public static String convertArticleToJson(ESArticleFullBean bean) {
    JSONObject targetJson = new JSONObject();
    
    // 修改字段映射和格式
    targetJson.put("articleId", bean.getId());
    targetJson.put("title", bean.getTitle());
    // ...
}
```

### 修改请求头

编辑文件：`src/main/java/cuc/cdnews/ui/App2.java`

找到 `sendArticleToApi` 方法中的请求构建部分（约第72行）：

```java
Request request = new Request.Builder()
        .url(url)
        .post(body)
        .addHeader("Authorization", "Bearer " + apiToken)
        .addHeader("Content-Type", "application/json")
        // 添加其他请求头
        .addHeader("X-Custom-Header", "value")
        .build();
```

### 修改发送线程数量

在配置文件中修改：

```properties
config.app.thread.size=10  # 改为你想要的线程数
```

或者在代码中修改：`src/main/java/cuc/cdnews/ui/App2.java` 的 `main` 方法中（约第130行）：

```java
int threadSize = RootConfiguration.getThreadSize();
if (threadSize <= 0) {
    threadSize = 10; // 修改默认值
}
```

## 项目结构

```
src/main/java/cuc/cdnews/
├── config/
│   ├── RootConfiguration.java      # 配置管理类
│   └── ...
├── data/
│   ├── HttpArticleService.java     # HTTP数据获取服务
│   └── ...
├── domain/
│   ├── ESArticleFullBean.java      # 文章数据模型
│   ├── ESArticleQueueManager.java  # 队列管理器
│   └── ...
├── thread/
│   ├── HttpLoadProcessThread.java  # HTTP数据加载线程
│   ├── HttpSendProcessThread.java  # HTTP数据发送线程
│   └── ...
└── ui/
    └── App2.java                    # 主程序入口
```



