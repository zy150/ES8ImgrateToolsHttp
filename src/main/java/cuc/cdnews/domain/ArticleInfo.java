package cuc.cdnews.domain;

import java.util.Date;

public class ArticleInfo {
	public ArticleInfo() {

	}

//	private int id;
//	private String title;
//	private String content;
//	private String author;
//	private String publisherId;
//	private String docType;
//	private String mediaSourceId;
//	private Date inclusionTime;
//	private String inclusionTimeText;
//	private Date publishTime;
//	private String publishTimeText;
//	private String publisherCountry;
//	private int publisherType;
//	private EsClassification classification;
//	private EsSentiment sentiment;
//	private String reserveAtt1;
//	private int reserveAtt2;
//	private String abstractEN;
//	private String abstractCN;
//	private String titleCN;
//	private String contentCN;
//	private Double[] articleVector;
//	private String wordSeqJson;
//	private String nerList;
//	private Double searchScore;
//	private int isKeyArticle;
//    private String url;
    private int id;
    private String title;
    private String content;
    private String author;
    private String publisherId;
    
    private String docType;
    private String mediaSourceId;
    private Date inclusionTime;
    private String inclusionTimeText;
    private Date publishTime;
    
    private String publishTimeText;
    private String publisherCountry;
    private int publisherType;
    private EsClassification classification;
    private EsSentiment sentiment;
    
    private String reserveAtt1;
    private int reserveAtt2;
	private String abstractEN;
	private String abstractCN;
    private String titleCN;
    
    private String contentCN;
    private Double[] articleVector;
    private String wordSeqJson;
	private String nerList;
	private int isKeyArticle=0;

	private String url;

    private Double searchScore;
	
	public EsClassification getClassification() {
		return classification;
	}

	public void setClassification(EsClassification classification) {
		this.classification = classification;
	}

	public EsSentiment getSentiment() {
		return sentiment;
	}

	public void setSentiment(EsSentiment sentiment) {
		this.sentiment = sentiment;
	}



	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAuthor() {
		return author;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}
	public String getPublisherId() {
		return publisherId;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getDocType() {
		return docType;
	}

	public void setMediaSourceId(String mediaSourceId) {
		this.mediaSourceId = mediaSourceId;
	}
	public String getMediaSourceId() {
		return mediaSourceId;
	}

	public void setInclusionTime(Date inclusionTime) {
		this.inclusionTime = inclusionTime;
	}
	public Date getInclusionTime() {
		return inclusionTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}
	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTimeText(String publishTimeText) {
		this.publishTimeText = publishTimeText;
	}
	public String getPublishTimeText() {
		return publishTimeText;
	}

	public void setPublisherCountry(String publisherCountry) {
		this.publisherCountry = publisherCountry;
	}
	public String getPublisherCountry() {
		return publisherCountry;
	}

	public void setPublisherType(int publisherType) {
		this.publisherType = publisherType;
	}
	public int getPublisherType() {
		return publisherType;
	}

	public String getInclusionTimeText() {
		return inclusionTimeText;
	}
	public void setInclusionTimeText(String inclusionTimeText) {
		this.inclusionTimeText = inclusionTimeText;
	}
	public String getReserveAtt1() {
		return reserveAtt1;
	}
	public void setReserveAtt1(String reserveAtt1) {
		this.reserveAtt1 = reserveAtt1;
	}
	public int getReserveAtt2() {
		return reserveAtt2;
	}
	public void setReserveAtt2(int reserveAtt2) {
		this.reserveAtt2 = reserveAtt2;
	}
	public String getTitleCN() {
		return titleCN;
	}
	public void setTitleCN(String titleCN) {
		this.titleCN = titleCN;
	}
	public String getContentCN() {
		return contentCN;
	}
	public void setContentCN(String contentCN) {
		this.contentCN = contentCN;
	}
	public Double[] getArticleVector() {
		return articleVector;
	}
	public void setArticleVector(Double[] articleVector) {
		this.articleVector = articleVector;
	}
	public String getAbstractEN() {
		return abstractEN;
	}

	public void setAbstractEN(String abstractEN) {
		this.abstractEN = abstractEN;
	}

	public String getAbstractCN() {
		return abstractCN;
	}

	public void setAbstractCN(String abstractCN) {
		this.abstractCN = abstractCN;
	}
	public String getWordSeqJson() {
		return wordSeqJson;
	}
	public void setWordSeqJson(String wordSeqJson) {
		this.wordSeqJson = wordSeqJson;
	}
	public String getNerList() {
		return nerList;
	}
	public void setNerList(String nerList) {
		this.nerList = nerList;
	}

	public int getIsKeyArticle() {
		return isKeyArticle;
	}

	public void setIsKeyArticle(int isKeyArticle) {
		this.isKeyArticle = isKeyArticle;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Double getSearchScore() {
		return searchScore;
	}

	public void setSearchScore(Double searchScore) {
		this.searchScore = searchScore;
	}
}
