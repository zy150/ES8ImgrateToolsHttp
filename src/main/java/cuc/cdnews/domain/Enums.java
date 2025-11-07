package cuc.cdnews.domain;

import java.util.HashMap;
import java.util.Map;



public class Enums {
	public enum Status {
		Enable(0, "启用"), Disable(1, "停用"), Deleted(2, "删除");

		private int value;
		private String displayName;

		private Status(int value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}

		public Integer getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
	};
	
	public enum HttpStatus {
		OK(200), PasswordError(301), Lock(300), Error(500), PasswordNeed(302), CheckError(600); 

		private int value = 0;

		private HttpStatus(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	}
	public enum SentimentArticle implements BaseEnum<Enum<SentimentArticle>, Integer>{
		Positive(3, "正面"), Negative(2, "负面"), Neutral(1, "中立"), Unknow(-1, "暂无");
		private int value;
		private String displayName;

		static Map<Integer, SentimentArticle> enumMap = new HashMap<>();
		static {
			for(SentimentArticle name: SentimentArticle.values()){
				enumMap.put(name.value, name);
			}
		}

		static Map<String, SentimentArticle> enumMap2 = new HashMap<>();

		static {
			for(SentimentArticle name: SentimentArticle.values()){
				enumMap2.put(name.displayName, name);
			}
		}

		SentimentArticle(int value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}

		@Override
		public Integer getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		@Override
		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public static SentimentArticle getEnumMap(int value) {
			return enumMap.get(value);
		}

		public static SentimentArticle getEnumMap2(String displayName) {
			return enumMap2.get(displayName);
		}

	}
	// implements BaseEnum<Enum<Sentiment>, Integer>
	public enum Sentiment implements BaseEnum<Enum<Sentiment>, Integer>{

		Positive(3,"正面"),Neutral(1,"客观"),Negative(2,"负面"),UnKnown(-1,"暂无");  
	      
		private int value;
		private String displayName;
		
		static Map<Integer, Sentiment> enumMap = new HashMap<Integer, Sentiment>();
		static {
			for (Sentiment type : Sentiment.values()) {
				enumMap.put(type.getValue(), type);
			}
		}		
		
		static Map<Integer, String> enumMap2 = new HashMap<Integer, String>();
		static {
			for (Sentiment type : Sentiment.values()) {
				enumMap2.put(type.getValue(), type.getDisplayName());
			}
		}
		
		static Map<String, Sentiment> enumMap3 = new HashMap<String, Sentiment>();
		static {
			for (Sentiment type : Sentiment.values()) {
				enumMap3.put(type.getDisplayName(), type);
			}
		}		
		private Sentiment(int value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}
		public Integer getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		public static Sentiment getEnum(int value) {
			return enumMap.get(value);
		}
		public static Map<Integer, String> getEnums2(){
			return enumMap2;
		}
		public static Sentiment getEnums3(String displayName){
			return enumMap3.get(displayName);
		}
	}
	
	public enum QuoteArticleClassification{

		Politics(0,"政治"),Culture(1,"文化"),Diplomacy(2,"外交"),Society(3,"社会"),Science(4,"科技"),
		Military(5,"军事"),Economics(6,"经济"),Environment(7,"环保"),HMT(8,"港澳台"),International(9,"国际"),
		Religion(10,"宗教"),AntiCorruption(11,"反腐"),Sport(12,"体育"),AntiTerrorism(13,"反恐"),Catoon(14,"卡通"),UnKnown(-1,"暂无");  
	      
		private int value;
		private String displayName;
		
		static Map<Integer, QuoteArticleClassification> enumMap = new HashMap<Integer, QuoteArticleClassification>();
		static {
			for (QuoteArticleClassification type : QuoteArticleClassification.values()) {
				enumMap.put(type.getValue(), type);
			}
		}
		
		static Map<String, QuoteArticleClassification> enumMap3 = new HashMap<String, QuoteArticleClassification>();
		static {
			for (QuoteArticleClassification type : QuoteArticleClassification.values()) {
				enumMap3.put(type.getDisplayName(), type);
			}
		}
		
		static Map<Integer, String> enumMap2 = new HashMap<Integer, String>();
		static {
			for (QuoteArticleClassification type : QuoteArticleClassification.values()) {
				enumMap2.put(type.getValue(), type.getDisplayName());
			}
		}

		private QuoteArticleClassification(int value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}

		public Integer getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public static QuoteArticleClassification getEnum(int value) {
			return enumMap.get(value);
		}
		
		public static Map<Integer, QuoteArticleClassification> getEnums(){
			return enumMap;
		}
		public static Map<Integer, String> getEnums2(){
			return enumMap2;
		}	
		public static QuoteArticleClassification getEnums3(String displayName){
			return enumMap3.get(displayName);
		}
	}
	
	public enum QuoteArticlChannel{

		NewsPaper(0,"报纸"),WebSite(1,"网站"),Video(2,"视频"),Sns(3,"社交平台"),UnKnown(-1,"暂无");//,Enable(100,"测试用");  
	      
		private int value;
		private String displayName;

		private QuoteArticlChannel(int value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}

		public Integer getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
	}
	public enum MediaType implements BaseEnum<Enum<Sentiment>, Integer>{

		Agency(0,"通讯社"),Newspaper(1,"报纸"),Journal(2,"期刊杂志"),Broadcast(3,"广播电视"),Thinktank(4,"智库"),Website(5,"网站"),Others(6,"暂定其他");  
	      
		private int value;
		private String displayName;
		
		static Map<Integer, MediaType> enumMap = new HashMap<Integer, MediaType>();
		static {
			for (MediaType type : MediaType.values()) {
				enumMap.put(type.getValue(), type);
			}
		}
		
		static Map<Integer, String> enumMap2 = new HashMap<Integer, String>();
		static {
			for (MediaType type : MediaType.values()) {
				enumMap2.put(type.getValue(), type.getDisplayName());
			}
		}

		private MediaType(int value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}

		public Integer getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public static MediaType getEnum(int value) {
			return enumMap.get(value);
		}
		
		public static Map<Integer, String> getEnums2(){
			return enumMap2;
		}
	}
	public enum EsSyncStatus implements BaseEnum<Enum<EsSyncStatus>, Integer>{
		NotReady(0,"不可同步"),Ready(1,"可同步"),Done(2,"已经同步");

		private int value;
		private String displayName;

		static Map<Integer, EsSyncStatus> enumMap = new HashMap<Integer, EsSyncStatus>();
		public static Map<Integer, EsSyncStatus> getEnumMap() {
			return enumMap;
		}

		public static void setEnumMap(Map<Integer, EsSyncStatus> enumMap) {
			EsSyncStatus.enumMap = enumMap;
		}

		static {
			for (EsSyncStatus type : EsSyncStatus.values()) {
				enumMap.put(type.getValue(), type);
			}
		}

		private EsSyncStatus(int value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}

		public Integer getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public static EsSyncStatus getEnum(int value) {
			return enumMap.get(value);
		}
		static Map<Integer, String> enumDisplayNameMap = new HashMap<Integer, String>();
		static {
			for (EsSyncStatus type : EsSyncStatus.values()) {
				enumDisplayNameMap.put(type.getValue(), type.getDisplayName());
			}
		}
		public static Map<Integer, String> getEnumDisplayNameMap(){
			return enumDisplayNameMap;
		}
		static Map<Integer, String> enumNameMap = new HashMap<Integer, String>();
		static {
			for (EsSyncStatus type : EsSyncStatus.values()) {
				enumNameMap.put(type.getValue(), type.name());
			}
		}
		public static Map<Integer, String> getEnumNameMap(){
			return enumNameMap;
		}
	}
	public enum PartOfSpeechTypes implements BaseEnum<Enum<PartOfSpeechTypes>, Integer>{
		Unkouwn(0,"未知"),Verb(1,"动词"),Adv(2,"副词");

		private int value;
		private String displayName;

		static Map<Integer, PartOfSpeechTypes> enumMap = new HashMap<Integer, PartOfSpeechTypes>();
		public static Map<Integer, PartOfSpeechTypes> getEnumMap() {
			return enumMap;
		}

		public static void setEnumMap(Map<Integer, PartOfSpeechTypes> enumMap) {
			PartOfSpeechTypes.enumMap = enumMap;
		}

		static {
			for (PartOfSpeechTypes type : PartOfSpeechTypes.values()) {
				enumMap.put(type.getValue(), type);
			}
		}

		private PartOfSpeechTypes(int value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}

		public Integer getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public static PartOfSpeechTypes getEnum(int value) {
			return enumMap.get(value);
		}
		static Map<Integer, String> enumDisplayNameMap = new HashMap<Integer, String>();
		static {
			for (PartOfSpeechTypes type : PartOfSpeechTypes.values()) {
				enumDisplayNameMap.put(type.getValue(), type.getDisplayName());
			}
		}
		public static Map<Integer, String> getEnumDisplayNameMap(){
			return enumDisplayNameMap;
		}
		static Map<Integer, String> enumNameMap = new HashMap<Integer, String>();
		static {
			for (PartOfSpeechTypes type : PartOfSpeechTypes.values()) {
				enumNameMap.put(type.getValue(), type.name());
			}
		}
		public static Map<Integer, String> getEnumNameMap(){
			return enumNameMap;
		}
	}
	public enum ClassificationName implements BaseEnum<Enum<Sentiment>, Integer>{
		Unknown(0,"未知"),Military(1,"军事"),Politics(2,"政治"),Culture(3,"文化"),HMT(4,"港澳台"),Environment(5,"环境"),Society(6,"社会"),Science(7,"科技"),
		Economics(8,"经济"),Diplomacy(9,"外交");
		private int value;
		private String displayName;

		static Map<Integer, ClassificationName> enumMap = new HashMap<>();
		static {
			for(ClassificationName name: ClassificationName.values()){
				enumMap.put(name.value, name);
			}
		}

		static Map<String, ClassificationName> enumMap2 = new HashMap<>();
		static {
			for(ClassificationName name: ClassificationName.values()){
				enumMap2.put(name.displayName, name);
			}
		}

		ClassificationName(int value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}

		@Override
		public Integer getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		@Override
		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public static ClassificationName getEnumMapByIndex(Integer value){
			return enumMap.get(value);
		}

		public static ClassificationName getEnumMapByName(String displayName){
			return enumMap2.get(displayName);
		}
	}
	public enum ProcessStatus implements BaseEnum<Enum<ProcessStatus>, Integer>{

		Untreated(0, "未处理"), Processed(1, "已处理"),Analsysed(2,"已分析");

		private int value;
		private String displayName;

		static Map<Integer, ProcessStatus> enumMap = new HashMap<Integer, ProcessStatus>();
		static {
			for (ProcessStatus type : ProcessStatus.values()) {
				enumMap.put(type.getValue(), type);
			}
		}

		private ProcessStatus(int value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}

		public Integer getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public static ProcessStatus getEnum(int value) {
			return enumMap.get(value);
		}
		static Map<Integer, String> enumDisplayNameMap = new HashMap<Integer, String>();
		static {
			for (ProcessStatus type : ProcessStatus.values()) {
				enumDisplayNameMap.put(type.getValue(), type.getDisplayName());
			}
		}
		public static Map<Integer, String> getEnumDisplayNameMap() { return enumDisplayNameMap; }

		
		static Map<Integer, String> enumNameMap = new HashMap<Integer, String>();
		static {
			for (ProcessStatus type : ProcessStatus.values()) {
				enumNameMap.put(type.getValue(), type.name());
			}
		}
		public static Map<Integer, String> getEnumNameMap(){	return enumNameMap;	}
	}
	public enum TextType implements BaseEnum<Enum<TextType>, Integer>{

		LongText(0, "长文本"), ShortText(1, "短文本");

		private int value;
		private String displayName;

		static Map<Integer, TextType> enumMap = new HashMap<Integer, TextType>();
		static {
			for (TextType type : TextType.values()) {
				enumMap.put(type.getValue(), type);
			}
		}

		private TextType(int value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}

		public Integer getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public static TextType getEnum(int value) {
			return enumMap.get(value);
		}
		static Map<Integer, String> enumDisplayNameMap = new HashMap<Integer, String>();
		static {
			for (TextType type : TextType.values()) {
				enumDisplayNameMap.put(type.getValue(), type.getDisplayName());
			}
		}
		public static Map<Integer, String> getEnumDisplayNameMap() { return enumDisplayNameMap; }

		
		static Map<Integer, String> enumNameMap = new HashMap<Integer, String>();
		static {
			for (TextType type : TextType.values()) {
				enumNameMap.put(type.getValue(), type.name());
			}
		}
		public static Map<Integer, String> getEnumNameMap(){	return enumNameMap;	}
	}
	
}
