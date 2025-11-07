package cuc.cdnews.config;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

import org.apache.http.Header;
//import jakarta.annotation.PostConstruct;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class ElasticSearchConfig {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchConfig.class);

//	@Value("${spring.elasticsearch.rest.host}")
//    private String host;
//	
//    @Value("${spring.elasticsearch.rest.enable}")
//    private boolean enable;
//    
//    @Value("${spring.elasticsearch.rest.port}")
//    private int port;
//    
//    @Value("${spring.elasticsearch.rest.username}")
//    private String userName;
//    
//    @Value("${spring.elasticsearch.rest.password}")
//    private String passWord;
//    
//    @Value("${spring.elasticsearch.rest.crtName}")
//    private String tempCrtName;
//
//    private static String crtName;

//    @PostConstruct
//    private void init() {
//    	System.out.print(tempCrtName);
//        crtName = tempCrtName;
//    }

    /**
     * 解析配置的字符串，转为HttpHost对象数组
     *
     * @return
     */
    private HttpHost toHttpHost() {
        HttpHost httpHost = new HttpHost(RootConfiguration.getEsHost(), RootConfiguration.getEsPort(), "http");
        return httpHost;
    }

    @Bean
    public ElasticsearchClient clientByPasswd() throws Exception {
        ElasticsearchTransport transport = getElasticsearchTransport(RootConfiguration.getEsUserName(), RootConfiguration.getEsPpassWord(), toHttpHost());
        return new ElasticsearchClient(transport);
    }

    private static SSLContext buildSSLContext() {
        ClassPathResource resource = new ClassPathResource(RootConfiguration.getEsTempCrtName());
        SSLContext sslContext = null;
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate trustedCa;
            try (InputStream is = resource.getInputStream()) {
                trustedCa = factory.generateCertificate(is);
            }
            KeyStore trustStore = KeyStore.getInstance("pkcs12");
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", trustedCa);
            SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null);
            sslContext = sslContextBuilder.build();
        } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException |
                 KeyManagementException e) {
        	LOGGER.error("ES连接认证失败", e);
        }

        return sslContext;
    }

    private static ElasticsearchTransport getElasticsearchTransport(String username, String passwd, HttpHost... hosts) {
        // 账号密码的配置
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, passwd));

        List<Header> headers = new ArrayList<>(2);
        headers.add(new BasicHeader("Connection", "keep-alive"));
        headers.add(new BasicHeader("Keep-Alive", "720"));

        // 自签证书的设置，并且还包含了账号密码
        RestClientBuilder.HttpClientConfigCallback callback = httpAsyncClientBuilder -> httpAsyncClientBuilder
                .setSSLContext(buildSSLContext())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setDefaultCredentialsProvider(credentialsProvider)
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(50)
                .setDefaultHeaders(headers)
                .setKeepAliveStrategy(CustomKeepAliveStrategy.INSTANCE);

        // 用builder创建RestClient对象
        RestClient client = RestClient
                .builder(hosts)
                .setHttpClientConfigCallback(callback)
                .setRequestConfigCallback(requestConfigBuilder -> {
                    // 从connect Manager（连接池）获取链接的超时时间，单位毫秒
                    requestConfigBuilder.setConnectionRequestTimeout(30000);
                    // 连接目标url的超时时间
                    requestConfigBuilder.setConnectTimeout(30000);
                    // 请求获取数据的超时时间
                    requestConfigBuilder.setSocketTimeout(60_000);
                    return requestConfigBuilder;
                })
                .build();

        
        return new RestClientTransport(client, new JacksonJsonpMapper());
    }
    private static class CustomKeepAliveStrategy extends DefaultConnectionKeepAliveStrategy {
        public static final CustomKeepAliveStrategy INSTANCE = new CustomKeepAliveStrategy();
 
        private CustomKeepAliveStrategy() {
            super();
        }
 
        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            long keepAliveDuration = super.getKeepAliveDuration(response, context);
            // <0 无限期keepalive
            // 讲无限期替换成一个默认时间
            if (keepAliveDuration < 0) {
                /**
                 * 最大keep alive的时间（分钟）
                 * 这里默认为10分钟，可以根据实际情况设置。可以观察客户端机器状态为TIME_WAIT的TCP连接数，如果太多，可以增大此值。
                 */
                long maxKeepAliveSeconds = 600;
                return TimeUnit.SECONDS.toMillis(maxKeepAliveSeconds);
            }
            return keepAliveDuration;
        }
    }


}
