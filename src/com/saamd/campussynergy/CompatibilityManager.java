package com.saamd.campussynergy;

import android.os.Build;

public class CompatibilityManager {
	
	/**
	 * Get the current android API version
	 */
	public static int getSdkVersion(){
		return android.os.Build.VERSION.SDK_INT;
	}
	
	/**
	 * Checks if android version running is equal or higher than honeycomb 4.0
	 */
	public static boolean isHoneycomb(){
		return getSdkVersion() >= Build.VERSION_CODES.HONEYCOMB;
	}
	
	/**
	 * Checks if android version running is equal or higher than ice cream sandwich 4.1
	 */
	public static boolean isIceCreamSandwich(){
		return getSdkVersion() >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}
}
