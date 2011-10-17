package local.bin;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MapActivity;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class mapview extends MapActivity {

	MapView map; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);
      
        map = (MapView) findViewById(R.id.mlayout2);
        map.setBuiltInZoomControls(true);
        
        List<Overlay> mapOverlays = map.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.icon);
        HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(drawable);
        
        GeoPoint point = new GeoPoint(19240000,-99120000);
        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
        
        GeoPoint point2 = new GeoPoint(35410000, 139460000);
        OverlayItem overlayitem2 = new OverlayItem(point2, "Sekai, konichiwa!", "I'm in Japan!");
       
        itemizedoverlay.addOverlay(overlayitem);
        itemizedoverlay.addOverlay(overlayitem2);
        mapOverlays.add(itemizedoverlay);
        
    }

    @Override
    protected boolean isRouteDisplayed() { return false; }
}
