package cuc.cdnews.domain;

public class HotWord {
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getPartsOfSpeech() {
		return partsOfSpeech;
	}
	public void setPartsOfSpeech(int partsOfSpeech) {
		this.partsOfSpeech = partsOfSpeech;
	}
	private String name;
	private int value;
	private int partsOfSpeech;
}
