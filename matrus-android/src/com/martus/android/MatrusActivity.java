package com.martus.android;

import org.martus.clientside.ClientSideNetworkGateway;
import org.martus.common.crypto.MockMartusSecurity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MatrusActivity extends Activity {
	    
	String serverIP = "66.201.46.82";
	String serverIPNew = "50.112.118.184";
	
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
        
        final Button buttonServerInfo = (Button) findViewById(R.id.serverInfo);
        buttonServerInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	try {
//        			MockMartusSecurity security = new MockMartusSecurity();
//        			security.createKeyPair();
                	ClientSideNetworkGateway gateway = ClientSideNetworkGateway.buildGateway(serverIPNew, "6228.1113.7277.8184.2116");
                	gateway.getServerInfo();
            	} catch (Exception e) {
        			Log.e("error", "Failed starting MockMartusSecurity");
					e.printStackTrace();
				}
            }
        });
    }
    
}