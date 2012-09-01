package com.tatsuo.tamenchan.domain;

import java.io.Serializable;

public class TamenchanScore implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int question;
	private int score;
	
	public TamenchanScore(){
		question = 0;
		score = 0;
	}
	
	public int getQuestion() {
		return question;
	}
	public void setQuestion(int question) {
		this.question = question;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
		
}
