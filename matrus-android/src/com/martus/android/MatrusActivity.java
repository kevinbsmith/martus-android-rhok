package com.martus.android;

import java.net.URI;
import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.martus.clientside.ClientSideNetworkHandlerUsingXmlRpcForNonSSL;
//import org.xmlrpc.android.XMLRPCClient;
//import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MatrusActivity extends Activity {

	String serverURL = "http://66.201.46.82:988/RPC2";
	XmlRpcClient client = new XmlRpcClient();
//	ClientSideNetworkHandlerUsingXmlRpcForNonSSL client = new ClientSideNetworkHandlerUsingXmlRpcForNonSSL("66.201.46.82");
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
            	try {
            		Log.v("xmlrpc", "calling: "+serverURL);
            		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            		config.setServerURL(new URL(serverURL));
            		XmlRpcClient client = new XmlRpcClient();
            		client.setConfig(config);
            		Vector params = new Vector();
            		String result = (String) client.execute("MartusServer.ping", params);
                  	textview.setText("response: " +result);
            		Log.v("xmlrpc", "response: "+result);
				} catch (Exception e) {
					Log.e("error", "xmlrpc call failed");
					e.printStackTrace();
				}
            }
        });
    }
    
}