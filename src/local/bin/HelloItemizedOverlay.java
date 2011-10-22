package local.bin;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class HelloItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	private Context c;

	public HelloItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		c = mapView.getContext();
	}

	public void addOverlay(OverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		
		Toast.makeText(c, item.getTitle(),
				Toast.LENGTH_LONG).show();
		return true;
	}
	
}
