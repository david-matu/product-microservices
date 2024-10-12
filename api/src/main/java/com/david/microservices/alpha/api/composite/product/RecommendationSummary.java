package com.david.microservices.alpha.api.composite.product;

public class RecommendationSummary {
	private final int recommendationId;
	private final String author;
	private final int rate;
	private final String content;
	
	public RecommendationSummary() {
		this.recommendationId = 0;
		this.author = null;
		this.rate = 0;
		this.content = null;
	}
	
	public RecommendationSummary(int recommendationId, String author, int rate, String content) {
		this.recommendationId = recommendationId;
		this.author = author;
		this.rate = rate;
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	/*
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
	*/
	
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
