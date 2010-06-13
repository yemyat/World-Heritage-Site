package rp.edu.sg.jeff;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class LocationList extends ListActivity {
	TextView label;
	TextView info;
	
	ListView listLocation;
	ListAdapter adapterLocation;
	
	ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	
	ConnectivityManager myConManager;
	
	String[] locations;
	String[] countries;
	
	//Variables for menus
	static final private int VIEW_MAP = Menu.FIRST ;
	static final private int VIEW_WIKI = Menu.FIRST+1;
	static final private int GIVE_FEEDBACK = Menu.FIRST + 2;
	static final private int DELETE = Menu.FIRST + 3;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Initialization
		myConManager =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);//To check whether the device is connected to the Interenet
		label = (TextView)findViewById(R.id.label);
		info  = (TextView)findViewById(R.id.info);
		
		listLocation = getListView();
		
		locations = getResources().getStringArray(R.array.locations);
		countries = getResources().getStringArray(R.array.countries);
		
		//Iterate one of the arrays (sharing common index) and map them into a hash then put that hash into a Hashmap
		for(int i = 0; i<locations.length;i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("location", locations[i]);
			map.put("country", countries[i]);
			list.add(map);
		}
		//Use simple adapter to provide both location and coutry information in the list view\
		adapterLocation = new SimpleAdapter(this,list,R.layout.row,new String[]{"location","country"},new int[] {R.id.label,R.id.info});
		listLocation.setAdapter(adapterLocation);
		registerForContextMenu(listLocation);
	}	
	public void onListItemClick(ListView parent, View v,int position, long id) {  
		if(myConManager.getNetworkInfo(1).isConnected() || myConManager.getNetworkInfo(0).isConnected()) {
			goToMap(position,list.get(position).get("location"));
		} else {
			showAlertBox("This application requires Internet connection");
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu,View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Options");
		MenuItem view_map = menu.add(0,VIEW_MAP,Menu.NONE,R.string.view_map);
		MenuItem view_wiki = menu.add(0,VIEW_WIKI,Menu.NONE,R.string.view_wiki);
		MenuItem feedback = menu.add(0,GIVE_FEEDBACK,Menu.NONE,R.string.give_feedback);
		MenuItem delete = menu.add(0,DELETE,Menu.NONE,R.string.delete);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		AdapterView.AdapterContextMenuInfo menuInfo;
		menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();//get menu info to get the index later
		int index = menuInfo.position;
		switch(item.getItemId()){
		case(VIEW_MAP): {
			if(myConManager.getNetworkInfo(1).isConnected() || myConManager.getNetworkInfo(0).isConnected()) {//redirect to map only if the device is connected to the net
				goToMap(index,list.get(index).get("location"));
				return true;
			} else {
				showAlertBox("This application requires Internet connection");
				return false;
			}
		}
		case(VIEW_WIKI):{
			searchWiki(list.get(index).get("location"));
			
			return true;
		}
		case(GIVE_FEEDBACK):{
			giveFeedback(list.get(index).get("location"));
			return true;
		}
		case(DELETE):{
			list.remove(index);
			adapterLocation = new SimpleAdapter(this,list,R.layout.row,new String[]{"location","country"},new int[] {R.id.label,R.id.info});
			listLocation.setAdapter(adapterLocation);
		}
		}
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
	public void goToMap(int index, String searchLocation) {
		Bundle myLocation = new Bundle();
		Intent newIntentMap = new Intent(getBaseContext(),Map.class);
		myLocation.putString("location",searchLocation);
		myLocation.putInt("index", index);
		newIntentMap.putExtras(myLocation);
		startActivity(newIntentMap);	
	}
	//A global alert box
	public void showAlertBox(String myMessage) {
		AlertDialog.Builder myAlertBoxBuilder =new AlertDialog.Builder(this);
		myAlertBoxBuilder.setMessage(myMessage).setCancelable(true);
		myAlertBoxBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alertBox = myAlertBoxBuilder.create();
		alertBox.show();
	}

}