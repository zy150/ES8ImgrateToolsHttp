package cuc.cdnews.data;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.json.JsonData;
import cuc.cdnews.config.RootConfiguration;

public class ESRequestBuilderTools {
	private static final Logger LOGGER = LoggerFactory.getLogger(ESRequestBuilderTools.class);

	/**
	 * 创建根据根据最大Id获取文章的查询请求
	 */
	public static SearchRequest getArticleListByIdRangeBuilder(int articleId, List<String> retFields,int retCount) {
    	SearchRequest searchRequest = new SearchRequest.Builder().index(RootConfiguration.getCdnewsESfromIndex()).query(q -> q.bool(b -> {
            // 搜索文本过滤
            Optional.ofNullable(articleId)
                    .filter(id -> id != -1)
                    .ifPresent(maxArticleId -> b.must(r -> r.range(f -> f.field("id").gt(JsonData.of(maxArticleId)))));

            return b;
		})).sort(so -> so.field(f -> f.field("id").order(SortOrder.Asc)))
    			.source(sour -> sour.filter(ss -> ss.includes(retFields)))
				.size(retCount).scroll(Time.of(t -> t.time("2m")))
				.trackScores(true).build();
    	
    	LOGGER.debug(searchRequest.toString());
		return searchRequest;
	}
	/**
	 * 创建根据根据最大Id获取文章的查询请求
	 */
	public static SearchRequest getArticleListByIdRangeBuilderNew(int articleId, List<String> retFields,int retCount) {
    	SearchRequest searchRequest = new SearchRequest.Builder().index(RootConfiguration.getCdnewsESToIndex()).query(q -> q.bool(b -> {
            // 搜索文本过滤
            Optional.ofNullable(articleId)
                    .filter(id -> id != -1)
                    .ifPresent(maxArticleId -> b.must(r -> r.range(f -> f.field("id").gt(JsonData.of(maxArticleId)))));

            return b;
		})).sort(so -> so.field(f -> f.field("id").order(SortOrder.Asc)))
    			.source(sour -> sour.filter(ss -> ss.includes(retFields)))
				.size(retCount).scroll(Time.of(t -> t.time("2m")))
				.trackScores(true).build();
    	
    	LOGGER.debug(searchRequest.toString());
		return searchRequest;
	}
	
}
