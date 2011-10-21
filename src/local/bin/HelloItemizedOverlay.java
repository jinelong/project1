package local.bin;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.OverlayItem;

public class HelloItemizedOverlay extends ItemizedOverlay {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	Context mContext = null;
	AlertDialog.Builder dialog= null;
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	public HelloItemizedOverlay(Drawable defaultMarker) {
		
		  super(boundCenterBottom(defaultMarker));
		}
	public HelloItemizedOverlay(Drawable defaultMarker, Context context) {
		 super(boundCenterBottom(defaultMarker));
		 mContext = context;
		
		// super(boundCenterBottom(defaultMarker));
		  
		}
	
	

	@Override
	protected OverlayItem createItem(int arg0) {
		// TODO Auto-generated method stub
		return mOverlays.get(arg0);
	}
	@Override
	protected boolean onTap(int index) {
		
	  if(mContext==null)
		  	return false;
		
	  OverlayItem item = mOverlays.get(index);
	  dialog = new AlertDialog.Builder(mContext);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  return true;
	}

	@Override
	public int size() {
		  return mOverlays.size();
		}

	
}
