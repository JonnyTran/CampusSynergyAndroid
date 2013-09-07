package com.saamd.campussynergy;

import java.util.Date;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Event implements Serializable, Comparable<Event>{
	private String title = "";
	private String description = "";
	private String buildingName = "";
	private String publisher = "";
	private int roomNumber = 0;
	private Date date;
	private double startingTime = 0;
	private double duration = 0;

	public Event (String title, String building, String description, int room,
			double startingTime, double duration, Date date, String publisher) {
		setTitle(title);
		setBuildingName(building);
		setDescription(description);
		setRoomNumber(room);
		setStartingTime(startingTime);
		setDuration(duration);
		setDate(date);
		setPublisher(publisher);
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		if (buildingName != null)
			this.buildingName = buildingName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description != null)
			this.description = description;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	public double getStartingTime() {
		return startingTime;
	}

	public void setStartingTime(double startingTime) {
		this.startingTime = startingTime;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	@Override
	public int compareTo(Event another) {
		// TODO Auto-generated method stub
		return this.getDate().compareTo(another.getDate());
		//return 0;
	}

}
