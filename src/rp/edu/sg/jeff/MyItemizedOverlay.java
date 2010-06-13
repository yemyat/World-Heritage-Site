package rp.edu.sg.jeff;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private String mLocation;
	public MyItemizedOverlay(Drawable defaultMarker,Context context,String location) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		mLocation = location;
	}

	public void addOverlay(OverlayItem myOverlay) {
		mOverlays.add(myOverlay);
		populate();
	}
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	@Override
	public boolean onTap(int i) {
		if(mLocation != null) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(R.string.feedback).setMessage(R.string.askIfFeedback).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

					((Map) mContext).giveFeedback(mLocation);
				}
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
			return true;
		}
		return false;
	}
}
