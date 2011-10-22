package local.bin;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
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
	LocationManager myPositionManager;
	LocationListener myPositionListener;
	Bundle extras ;
	GeoPoint myLocation = null;
	OverlayItem currentPosition = null;
	List<Overlay> mapOverlays;
	boolean displayLocation = false;
	Drawable mapPin;
	boolean added = false;
	MapController myMapController;
	
	ImageButton b1 = null;
	
	 HelloItemizedOverlay layer1;
	 HelloItemizedOverlay layer2 = null;
	 
	public class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location arg0) {
			
			// TODO Auto-generated method stub
			int la = -1;
			int lo =  -1;
			while(la ==-1 || lo == -1){
				la = (int)(arg0.getLatitude()*1E6);
				lo = (int)(arg0.getLongitude()*1E6);
			}
			Toast a= Toast.makeText(mapview.this, ""+la+" "+lo, 1);
			a.show();
			
			myLocation = new GeoPoint(la,lo);

			if(!myLocation.equals(null)){
				currentPosition = new OverlayItem(myLocation,"Current Location","");
		     }
			
			if(!added){
		        layer2 = new HelloItemizedOverlay(mapPin,map);
				mapOverlays.add(layer2);
				added = true;
			}
			 layer2.addOverlay(currentPosition);
		     myMapController.setZoom(16);
		     myMapController.animateTo(myLocation);
		       
					
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
		
	}
	 public void onStop(Bundle savedInstanceState) {
		 super.onStop();
		 displayLocation = !displayLocation;
		 myPositionManager.removeUpdates(myPositionListener);
	 
	 }
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);
        fromMain = getIntent();
        
        b1 = (ImageButton)findViewById(R.id.button1);
        b1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				
				// TODO Auto-generated method stub
				displayLocation = !displayLocation;
				if(displayLocation){
			
					myPositionManager =	(LocationManager)getSystemService(Context.LOCATION_SERVICE);
			        myPositionListener = new MyLocationListener();
			        myPositionManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, myPositionListener);
			        
			        
				}
				else{
					//mapOverlays.remove(layer2);
					myPositionManager.removeUpdates(myPositionListener);
					//added = false;
				}
				
			}
        	
        	
        });
               
        extras = getIntent().getExtras();
   	
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
        myMapController = map.getController();
        
        map.setDrawingCacheEnabled(true);
        map.setDrawingCacheQuality(MapView.DRAWING_CACHE_QUALITY_AUTO);

      
        //myMapController.animateTo(purdue);
        myMapController.animateTo(lab);
        myMapController.setZoom(18);
       
        
        mapOverlays = map.getOverlays();
        mapPin = this.getResources().getDrawable(R.drawable.map_pin);
        
        layer1 = new HelloItemizedOverlay(mapPin,map);
        
        OverlayItem overlayitem3 = new OverlayItem(lab, name, status);
        //OverlayItem overlayitem4 = new OverlayItem(purdue, "", "a point on 2nd layer");
        
        layer1.addOverlay(overlayitem3);
        //layer2.addOverlay(overlayitem4);
        
        mapOverlays.add(layer1);
       
     
        
       
       
    }

    @Override
    protected boolean isRouteDisplayed() { return false; }
    
    
}
