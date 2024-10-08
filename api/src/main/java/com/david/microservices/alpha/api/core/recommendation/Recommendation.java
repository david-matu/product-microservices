package com.david.microservices.alpha.api.core.recommendation;

public class Recommendation {
	private int productId;
	private int recommendationId;
	private String author;
	private int rate;		// Rating -+- Stars
	private String content;
	private String serviceAddress;	//Determine which instance responded to a request
	
	public Recommendation(int productId, int recommendationId,  String author, int rate, String content, String serviceAddress) {
		this.productId = productId;
		this.recommendationId = recommendationId;
		this.author = author;
		this.rate = 0;
		this.content = content;
		this.serviceAddress = serviceAddress;
	}
	
	public void setProductId(int productId) {
		this.productId = productId;
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

	public void setContent(String content) {
		this.content = content;
	}

	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

	public Recommendation() {
		this.productId = 0;
		this.recommendationId = 0;
		this.author = null;
		this.rate = 0;
		this.content = null;
		this.serviceAddress = null;
	}
	
	public int getProductId() {
		return productId;
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

	public String getContent() {
		return content;
	}

	public String getServiceAddress() {
		return serviceAddress;
	}
}
