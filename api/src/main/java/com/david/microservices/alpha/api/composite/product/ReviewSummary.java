package com.david.microservices.alpha.api.composite.product;

public class ReviewSummary {
	private final int reviewId;
	private final String author;
	private final String subject;
	private final String content;
	
	public ReviewSummary() {
		this.reviewId = 0;
		this.author = null;
		this.subject = null;
		this.content = null;
	}
	
	/**
	 * 
	 * @param reviewId
	 * @param author
	 * @param subject
	 */
	public ReviewSummary(int reviewId, String author, String subject) {
		this.reviewId = reviewId;
		this.author = author;
		this.subject = subject;
		this.content = "";
	}

	public String getContent() {
		return content;
	}

	/*
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
	*/
	
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
