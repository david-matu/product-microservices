package com.david.microservices.alpha.api.composite.product;

public class RecommendationSummary {
	private int recommendationId;
	private String author;
	private int rate;
	private String content;
	
	public RecommendationSummary(int recommendationId, String author, int rate, String content) {
		this.recommendationId = recommendationId;
		this.author = author;
		this.rate = rate;
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setRecommendationId(int recommendationId) {
		this.recommendationId = recommendationId;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getRecommendationId() {
		return recommendationId;
	}

	public String getAuthor() {
		return author;
	}

	public int getRate() {
		return rate;
	}
}
