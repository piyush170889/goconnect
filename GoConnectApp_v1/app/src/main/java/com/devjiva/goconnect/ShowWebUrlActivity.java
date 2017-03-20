package com.devjiva.goconnect;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.goconnect.util.SingletonUtil;

/**
 * @author Ravi
 * 
 */
public class ShowWebUrlActivity extends GoconnectActivity {

	
	WebView webview;
    ProgressDialog dialog;
	boolean loadingFinished = true;
	boolean redirect = false;
	private String urlData="";
	
	
	@SuppressLint("SetJavaScriptEnabled") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showweb);
//		SingletonUtil.getSingletonConfigInstance().isConnectedToInternet(this);
		 Bundle extras = getIntent().getExtras();
			if ( extras != null ) {
				
			   urlData = extras.getString("url");
			}


		dialog=new ProgressDialog(ShowWebUrlActivity.this);
		dialog.setMessage("Page is loading...");
		dialog.show();
	    

		webview = (WebView) findViewById(R.id.webview);
	    webview.getSettings().setJavaScriptEnabled(true);
	    webview.getSettings().setAllowFileAccess(true); 
	    
	    webview.setWebViewClient(new MyWebViewClient());
	    
	    webview.loadUrl(Uri.parse(urlData).toString());
	}
		 
		 
		 
		 private class MyWebViewClient extends WebViewClient { 
			  
			 
		         
		        //show the web page in webview but not in web browser
		        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
		        	
		        	
		        	
		        	if (!loadingFinished) {
		                redirect = true;
		             }

		            view.loadUrl (url); 
		            return true;
		        } 
		       
		        
		       

		        public void onPageFinished(WebView view, String url) {
		            if(!redirect){
		               loadingFinished = true;
		            }

		            if(loadingFinished && !redirect){
		              //HIDE LOADING IT HAS FINISHED
		            	if(dialog!=null) {
		            		if(dialog.isShowing()==true)
		            			dialog.dismiss();
		            	}
		            } else{
		               redirect = false; 
		            }

		    }
		  }
}