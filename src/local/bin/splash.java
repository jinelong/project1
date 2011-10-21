package local.bin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;



public class splash extends Activity {
	private TextView t = null;
	final String PATH = "/mnt/sdcard/lab";
	final String ADDR = "https://www.purdue.edu/apps/ics/LabMap";
	
	public boolean DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
        try {
                URL url = new URL(imageURL); //you can write here any link
                File file = new File(fileName);

                long startTime = System.currentTimeMillis();
                Log.d("downloader", "download begining");
                Log.d("downloader", "download url:" + url);
                Log.d("downloader", "downloaded file name:" + fileName);
                /* Open a connection to that URL. */
                URLConnection ucon = url.openConnection();

                /*
                 * Define InputStreams to read from the URLConnection.
                 */
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                /*
                 * Read bytes to the Buffer until there is nothing more to read(-1).
                 */
                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                        baf.append((byte) current);
                }

                /* Convert the Bytes read to a String. */
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baf.toByteArray());
                fos.close();
                Log.d("downloader", "download ready in"
                                + ((System.currentTimeMillis() - startTime) / 1000)
                                + " sec");
                return true;

        } catch (IOException e) {
                Log.d("downloader", "Error: " + e);
                return false;
        }

}
	
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	Runnable a = new Runnable(){
		
		public void run(){
			/*
			 * 1. check internet connection
			 * 2. if no internet connection, inform
			 * 3. otherwise, try to downlaod the webpage
			 * 
			 */
			
			//if(isOnline() && DownloadFromUrl(ADDR, PATH)){
			if(isOnline()){
				startActivity(new Intent().setClass(splash.this,LabCheckActivity.class));
				finish();
			}else{
				runOnUiThread(new Runnable(){
					public void run() {
						Toast.makeText(splash.this,"you are not online", Toast.LENGTH_LONG).show();
						t.setText("Connection fail, will exit in 2 seconds");
						welcome.postDelayed(b, 2000);
					}
					});
			}
			
		}
		
	};
	
Runnable b = new Runnable(){
		
		public void run(){finish();}
	};
	
	Handler welcome = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		t = (TextView)findViewById(R.id.progress);
			
		welcome.postDelayed(a, 2000);
		//welcome.removeCallbacks(a);
		
		}
}
