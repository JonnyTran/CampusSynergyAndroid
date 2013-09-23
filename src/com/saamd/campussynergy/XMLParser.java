package com.saamd.campussynergy;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

public class XMLParser {
	
		public ArrayList<Double> getCoordinatesOfBuilding(Activity activity, String buildingName) throws XmlPullParserException, IOException
		{
			ArrayList<Double> valuesList = new ArrayList<Double>();
			
			Resources res = activity.getResources();
			XmlResourceParser xpp = res.getXml(R.xml.buildings);
			xpp.next();
			
			int eventType = xpp.getEventType();
			int i=1;
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				if (eventType == XmlPullParser.START_TAG)
				{
					if(xpp.getAttributeValue(null, "name") != null)
					{
						if(xpp.getAttributeValue(null, "name").equals(buildingName))
						{
							//stringBuffer.append(xpp.getAttributeValue(null, "name") + "   " + xpp.getName() +'\n');
							eventType = xpp.next();
							
							while(xpp.getAttributeValue(null, "name") != null)
							{
								//arrive to <point>
								eventType = xpp.next();		//arrive to text
								if (MainActivity.debug)
								{
									Log.d("USER", xpp.getText() + "\n");
								}
								valuesList.add(Double.parseDouble(xpp.getText()));
								eventType = xpp.next();		//arrive to </point>
								eventType = xpp.next();		//arrive to the next <point>
							}
							//eventType = XmlPullParser.END_DOCUMENT;
						}
					}
				}
				eventType = xpp.next();
			}
			
			
			return valuesList;
		}
		
		public ArrayList<String> getBuildingList(Activity activity) throws XmlPullParserException, IOException
		{
			ArrayList<String> valuesList = new ArrayList<String>();
			
			Resources res = activity.getResources();
			XmlResourceParser xpp = res.getXml(R.xml.buildings);
			xpp.next();
			
			int eventType = xpp.getEventType();
			int i=1;
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				if (eventType == XmlPullParser.START_TAG)
				{
					if(xpp.getName().equals("Building"))
					{
						if (MainActivity.debug)
						{
							Log.d("USER", xpp.getAttributeValue(null, "name") + "\n");
						}
						valuesList.add(xpp.getAttributeValue(null, "name"));	
					}
					
				}
				eventType = xpp.next();
			}
			
			return valuesList;
		}
		
}