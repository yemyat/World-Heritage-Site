package rp.edu.sg.jeff;

import java.util.ArrayList;

import java.util.List;
import com.google.android.maps.GeoPoint;

public class Bearing {
	private static int EARTH_RADIUS_KM = 6371;
	
	public static double bearing(GeoPoint p1, GeoPoint p2) {
		double lat1 = p1.getLatitudeE6() / 1E6;
		double lon1 = p1.getLongitudeE6() / 1E6;
		double lat2 = p2.getLatitudeE6() / 1E6;
		double lon2 = p2.getLongitudeE6() / 1E6;
		return getBearing(lat1, lon1, lat2, lon2);
	}

	public static double getBearing(double lat1, double lon1, double lat2, double lon2) {
		double lat1Rad = Math.toRadians(lat1);
		double lat2Rad = Math.toRadians(lat2);
		double deltaLonRad = Math.toRadians(lon2 - lon1);
		double y = Math.sin(deltaLonRad) * Math.cos(lat2Rad);
		double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad);
		return radToBearing(Math.atan2(y, x));
	}


	public static double radToBearing(double rad) {
		return (Math.toDegrees(rad) + 360) % 360;
	}

}
