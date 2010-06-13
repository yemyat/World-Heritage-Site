package rp.edu.sg.jeff;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;


public class Map extends MapActivity {
	static final private int DISTANCE = Menu.FIRST;
	static final private int NORMAL_VIEW = Menu.FIRST + 1;
	static final private int SATELLITE_VIEW = Menu.FIRST + 2;
	static final private int WIKI = Menu.FIRST + 3;
	static final private int FEEDBACK = Menu.FIRST + 4;

	TextView textTitle;
	TextView textDistance;
	TextView textBearing;

	MapView mapView;
	MapController mapController;

	Double lat;
	Double log;
	
	GeoPoint targetLocation;
	GeoPoint myCurrentGeoPoint;

	Drawable marker;
	MyItemizedOverlay itemizedOverlay;
	List<Overlay> mapOverlays;

	Drawable current;
	MyItemizedOverlay currentOverlay;

	String[] temp;
	String[] coordinates;
	String location;

	int index;
	int myDistance=0;
	int tempInt;	
	int distance;

	ProgressDialog progDialog;

	Location currentLocation;
	String[] locationProviders = {LocationManager.NETWORK_PROVIDER,LocationManager.GPS_PROVIDER}; //provide two options for location service

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		//get bundles attached
		location = getIntent().getStringExtra("location");
		index = getIntent().getIntExtra("index", 0);
		
		//initializing text views
		textDistance = (TextView)findViewById(R.id.textDistance);
		textBearing = (TextView)findViewById(R.id.textBearing);
		textTitle = (TextView)findViewById(R.id.textTitle);
		
		//info on current location
		textTitle.setText(location);
		Toast.makeText(getBaseContext(),location, Toast.LENGTH_LONG).show();

		//get coordinates
		temp = getResources().getStringArray(R.array.coordinates);
		coordinates = temp[index].split(",");
		lat = Double.parseDouble(coordinates[0]);
		log = Double.parseDouble(coordinates[1]);

		targetLocation = new GeoPoint((int)(lat*1E6), (int)(log*1E6));

		//Map initialization
		mapView = (MapView)findViewById(R.id.mapView);
		mapController = mapView.getController();
		mapOverlays = mapView.getOverlays();
		
		//target location
		marker = this.getResources().getDrawable(R.drawable.marker);
		itemizedOverlay = new MyItemizedOverlay(marker,this,location); 
		
		//current location
		current = this.getResources().getDrawable(R.drawable.current);
		currentOverlay = new MyItemizedOverlay(current,this,null);

		//Map settings
		mapView.setBuiltInZoomControls(true);
		mapController.setCenter(targetLocation);
		mapController.setZoom(13);

		//Map controls
		OverlayItem overlayItem = new OverlayItem(targetLocation,location,"");
		itemizedOverlay.addOverlay(overlayItem);
		mapOverlays.add(itemizedOverlay);
		mapController.animateTo(targetLocation);

		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);//initialize the location manager
		for(String provider : locationProviders) {//loop through each provider
			LocationProvider myProvider = locationManager.getProvider(provider);
			if(myProvider != null && locationManager.isProviderEnabled(myProvider.getName())) {
			
				currentLocation = locationManager.getLastKnownLocation(provider);//use last known location at first so that the user can see infos faster
				updateNewLocation(currentLocation);

				locationManager.requestLocationUpdates(provider, 1000, 5, new LocationListener() {
					public void onLocationChanged(Location newLocation) {
						updateNewLocation(newLocation);
						
					}
					public void onProviderDisabled(String provider) {
						// TODO Auto-generated method stub
					}
					public void onProviderEnabled(String provider) {
						// TODO Auto-generated method stub
					}
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
						// TODO Auto-generated method stub
					}
				});
			}
		}
	}
	
	public void updateNewLocation(Location location) {
		//Add overlay to current location
		GeoPoint currentGP = new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6));
		OverlayItem currentO = new OverlayItem(currentGP,"","");
		currentOverlay.addOverlay(currentO);
		mapOverlays.add(currentOverlay);
		
		//update distance
		final float[] results = new float[3];
		double lat = location.getLatitude();
		double log = location.getLongitude();
		Location.distanceBetween(targetLocation.getLatitudeE6()/1E6, targetLocation.getLongitudeE6()/1E6, location.getLatitude(), location.getLongitude(), results);//remember to divide by 1E6
		textDistance.setText("Distance between : "+Math.floor(results[0]/1000)+" km");
		textBearing.setText("Bearing : "+((results[1]*(-1))+90)+"°N");
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuItem itemNormal = menu.add(0,NORMAL_VIEW,Menu.FIRST,R.string.normal_view);
		MenuItem itemSatellite  = menu.add(0,SATELLITE_VIEW,Menu.FIRST,R.string.satellite_view);
		MenuItem itemWiki = menu.add(1,WIKI,Menu.NONE,R.string.wiki);
		MenuItem itemComment = menu.add(1,FEEDBACK,Menu.NONE,R.string.comment);
		itemWiki.setIcon(R.drawable.wikipedia);
		itemComment.setIcon(R.drawable.feedback);
		itemSatellite.setIcon(R.drawable.satellite_view);
		itemNormal.setIcon(R.drawable.map_view);
		
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()) {
		case(NORMAL_VIEW):{
			mapView.setSatellite(false);
			return true;
		}
		case(SATELLITE_VIEW):{
			mapView.setSatellite(true);
			return true;
		}
		
		case(WIKI):{
			searchWiki(location);
			return true;
		}
		case(FEEDBACK):{
			giveFeedback(location);
			return true;
		}
		}
		return false;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	public void giveFeedback(String location) {
		Intent newIntent = new Intent(getBaseContext(),Feedback.class);
		newIntent.putExtra("location", location);
		startActivity(newIntent);	
	}
	public void searchWiki(String queryWord) {
		String query = queryWord.replace(' ','_');
		String url = "http://en.wikipedia.org/wiki/";
		Uri uri = Uri.parse(url+query);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	public void goToMap(int index, String[] locations) {
		String searchLocation = locations[index];
		Bundle targetLocation = new Bundle();
		Intent newIntentMap = new Intent(getBaseContext(),Map.class);
		targetLocation.putString("location",searchLocation);
		targetLocation.putInt("index", index);
		newIntentMap.putExtras(targetLocation);
		startActivity(newIntentMap);	
	}
}
