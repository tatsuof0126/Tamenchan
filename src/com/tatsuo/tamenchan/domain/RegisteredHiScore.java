package com.tatsuo.tamenchan.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RegisteredHiScore {
	
	private int registeredId;
	private String name;
	private String devId;
	private int rank;
	private int score;
	private Date achievedDate;
	
	public int getRegisteredId() {
		return registeredId;
	}
	public void setRegisteredId(int registeredId) {
		this.registeredId = registeredId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDevId() {
		return devId;
	}
	public void setDevId(String devId) {
		this.devId = devId;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public Date getAchievedDate() {
		return achievedDate;
	}
	public void setAchievedDate(Date achievedDate) {
		this.achievedDate = achievedDate;
	}
	
	public void setAchievedDate(String achievedDateStr) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		
		// UTC時刻で来るのでタイムゾーンをUTCにセット
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		try {
			this.achievedDate = dateFormat.parse(achievedDateStr);
		} catch (ParseException pe){
			pe.printStackTrace();
		}
	}

}
