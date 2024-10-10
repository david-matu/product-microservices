package com.david.microservices.alpha.api.composite.product;

public class ReviewSummary {
	private int reviewId;
	private String author;
	private String subject;
	private String content;
	
	public ReviewSummary(int reviewId, String author, String subject) {
		this.reviewId = reviewId;
		this.author = author;
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getReviewId() {
		return reviewId;
	}

	public String getAuthor() {
		return author;
	}

	public String getSubject() {
		return subject;
	}
}
