package com.martus.android;

import org.martus.clientside.ClientSideNetworkHandlerUsingXmlRpcForNonSSL;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MatrusActivity extends Activity {

	ClientSideNetworkHandlerUsingXmlRpcForNonSSL client = new ClientSideNetworkHandlerUsingXmlRpcForNonSSL("66.201.46.82");
	String response;
	TextView textview;

	    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textview = new TextView(this); 
    	textview=(TextView)findViewById(R.id.response); 
	    
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	response = client.ping();
            	textview.setText("response:" +response);
            	
            }
        });
    }
    
}