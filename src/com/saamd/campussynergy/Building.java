package com.saamd.campussynergy;

import java.util.ArrayList;
import java.util.Collections;

public class Building implements Comparable<Building> {
	private String name ="";
	private ArrayList<Double> latitude = new ArrayList<Double>();
	private ArrayList<Double> longitude = new ArrayList<Double>();
	
	public Building()
	{
	}
	public Building(String name)
	{
		this.setName(name);
	}
	
	public String getName() {
		return name;
	}
	public Building setName(String nam) {
		this.name = nam;
		return this;
	}
	public boolean addLatitude(Double lat)
	{
		return this.latitude.add(lat);
	}
	public boolean addLongitude(Double lng)
	{
		return this.longitude.add(lng);
	}
	/**
	 * @return the minimum latitude list assumed to be sorted
	 */
	public Double getMinLatitude()
	{
		if(latitude.size()>0)
		{
			return this.latitude.get(0);
		}
		return null;
	}
	/**
	 * @return the minimum longitude list assumed to be sorted
	 */
	public Double getMinLongitude()
	{
		if(longitude.size() > 0)
		{
			return this.longitude.get(0);
		}
		return null;
	}
	
	
	/**
	 * @return the maximum latitude list assumed to be sorted
	 */
	public Double getMaxLatitude()
	{
		return this.latitude.get( this.latitude.size() - 1);
	}
	/**
	 * @return the maximum longitude list assumned to be sorted
	 */
	public Double getMaxLongitude()
	{
		return this.longitude.get( this.longitude.size() - 1);
	}
	/**
	 * @return the latitude
	 */
	public ArrayList<Double> getLatitude() {
		return latitude;
	}
	/**
	 * @return the longitude
	 */
	public ArrayList<Double> getLongitude() {
		return longitude;
	}
	/**
	 * this method sorts all the latitude and longitude by increasing order
	 */
	public void prepObject()
	{
		Collections.sort(this.latitude);
		Collections.sort(this.longitude);
	}
	
	@Override
	public int compareTo(Building another) {
		
		return this.getName().compareTo(another.getName());
	}
}
