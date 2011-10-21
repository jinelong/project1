package local.bin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Jin
 *
 */

public class about extends Activity{
		
	TextView text = null;
	Button ok_about = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		
		text = (TextView)findViewById(R.id.about);
		text.setTextSize(15);
		
		ok_about = (Button)findViewById(R.id.ok_about);
		ok_about.setText("OK");
		
		ok_about.setOnClickListener(new onClick());
		
	
		
		 
	}
	
	
class onClick implements OnClickListener{
		
		@Override
		public void onClick(View v){
			finish();
				
		}
	}
		
}
	