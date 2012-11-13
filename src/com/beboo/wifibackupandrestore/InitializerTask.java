package com.beboo.wifibackupandrestore;

import android.content.Context;
import android.os.AsyncTask;
import android.net.wifi.WifiManager;
import com.beboo.wifibackupandrestore.backupmanagement.WIFIConfigurationManager;

class InitializerTask extends AsyncTask<Context, Void, Void> {
	private Context context;
     protected Void doInBackground(Context... contexts) {
         this.context = contexts[0];
		 
		 WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
		WIFIConfigurationManager confManager = WIFIConfigurationManager.getInstance();
		confManager.init(this.context, wifiManager);
		return null;
	
	}

     protected void onProgressUpdate(Void... progress) {
		 // NOP
     }

     protected void onPostExecute(Void result) {		
		WIFIConfigurationManager.getInstance().NotifyListeners();
		// NOP	
         
     }
 }