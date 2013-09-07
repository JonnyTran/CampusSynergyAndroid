package com.saamd.campussynergy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class ScreenManager {
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	static public double getHeight(Context mContext)
	{
		double height=0;
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if(Build.VERSION.SDK_INT>12){               
	        Point size = new Point();
	        display.getSize(size);
	        height = size.y;
	    }else{          
	        height = display.getHeight();  // deprecated
	    }
	    return height;
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	static public double getWidth(Context mContext)
	{
		double width=0;
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if(Build.VERSION.SDK_INT>12){               
	        Point size = new Point();
	        display.getSize(size);
	        width = size.x;
	    }else{          
	        width = display.getWidth();  // deprecated
	    }
	    return width;
	}
}
