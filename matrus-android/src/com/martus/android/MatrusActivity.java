package com.martus.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MatrusActivity extends Activity {
	    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle("Mardus Android");
 	    
        final Button button = (Button) findViewById(R.id.gotoPing);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	try {
            		Intent intent = new Intent(MatrusActivity.this, pingme.class);
                    startActivity(intent);
                    } catch (Exception e) {
					Log.e("error", "Failed starting pingme activity");
					e.printStackTrace();
				}
            }
        });
    }
    
}