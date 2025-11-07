package cuc.cdnews.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EsSentiment {

		@JsonProperty("class")
	    private int cla;
		private String claName;
	    private double neu_weight;
	    private double neg_weight;
	    private double pos_weight;
	    
	    public void setCla(int cla) {
	         this.cla = cla;
	     }
	     public int getCla() {
	         return cla;
	     }

	    public void setNeu_weight(double neu_weight) {
	         this.neu_weight = neu_weight;
	     }
	     public double getNeu_weight() {
	         return neu_weight;
	     }

	    public void setNeg_weight(double neg_weight) {
	         this.neg_weight = neg_weight;
	     }
	     public double getNeg_weight() {
	         return neg_weight;
	     }

	    public void setPos_weight(double pos_weight) {
	         this.pos_weight = pos_weight;
	     }
	     public double getPos_weight() {
	         return pos_weight;
	     }
		public String getClaName() {
			return claName;
		}
		public void setClaName(String claName) {
			this.claName = claName;
		}
}
