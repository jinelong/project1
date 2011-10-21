package local.bin;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapActivity;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class mapview extends MapActivity {

	MapView map; 
	GeoPoint purdue ;
	Intent fromMain = null;
	String status;
	String name;
	String la;
	String lo;
	GeoPoint lab;
	
	Bundle extras ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);
        fromMain = getIntent();
        
        
        int a = (int) (40.42656*1E6);
        int b = (int) (-86.92048*1E6);
        purdue = new GeoPoint(a,b);
        
        extras = getIntent().getExtras();
     /*
        if(extras.get("latitude").equals(null)){
        	
        	runOnUiThread(new Runnable(){
				public void run() {
					Toast.makeText(mapview.this,"empty", Toast.LENGTH_SHORT).show();
				}
				});
        	
        }else{
        	
        	runOnUiThread(new Runnable(){
				public void run() {
					Toast.makeText(mapview.this,extras.get("la").toString(), Toast.LENGTH_SHORT).show();
				}
				});
        }
        */
        		
		
        //get the info for point 
        
       
           
        la = extras.get("la").toString();
        lo = extras.get("lo").toString();
        
       
        name = extras.get("name").toString();
        status = extras.get("status").toString();
          
        int lat =	(int) (Double.parseDouble(la)*1E6); 
        int longi = (int) (Double.parseDouble(lo)*1E6);
        
        
        lab = new GeoPoint( lat, longi);
        	 
        Log.d("geo", String.valueOf(lat));
        Log.d("geo", String.valueOf(longi));
        
        
       
    
        map = (MapView) findViewById(R.id.mlayout2);
        map.setBuiltInZoomControls(true);
        MapController myMapController = map.getController();
        map.setDrawingCacheEnabled(true);
        map.setDrawingCacheQuality(MapView.DRAWING_CACHE_QUALITY_AUTO);

      
        //myMapController.animateTo(purdue);
        myMapController.animateTo(lab);
        
        myMapController.setZoom(16);
       
        
        List<Overlay> mapOverlays = map.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.map_pin);
        
        HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(drawable,this);
        HelloItemizedOverlay itemizedoverlay2 = new HelloItemizedOverlay(drawable,this);
       
        
        
        
        GeoPoint point = new GeoPoint(19240000,-99120000);
        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
        
        GeoPoint point2 = new GeoPoint(35410000, 139460000);
        OverlayItem overlayitem2 = new OverlayItem(point2, "Sekai, konichiwa!", "I'm in Japan!");
        
        OverlayItem overlayitem3 = new OverlayItem(lab, name, status);
        
        OverlayItem overlayitem4 = new OverlayItem(purdue, "", "a point on 2nd layer");

        
       
        itemizedoverlay.addOverlay(overlayitem);
        itemizedoverlay.addOverlay(overlayitem2);
        itemizedoverlay.addOverlay(overlayitem3);
        itemizedoverlay2.addOverlay(overlayitem4);
        
        mapOverlays.add(itemizedoverlay);
        mapOverlays.add(itemizedoverlay2);
        
       
        
       
    }

    @Override
    protected boolean isRouteDisplayed() { return false; }
}
