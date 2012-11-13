package com.beboo.wifibackupandrestore;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.net.wifi.WifiManager;
import com.beboo.wifibackupandrestore.backupmanagement.WIFIConfigurationManager;

class InitializerTask extends AsyncTask<Void, Void, Void> {
	private Context context;
	
	private ProgressDialog progress;
	
	public InitializerTask(Context context) {
		this.context = context;
	}
	
     protected Void doInBackground(Void... nothing) {
		 WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
		WIFIConfigurationManager confManager = WIFIConfigurationManager.getInstance();
		confManager.init(this.context, wifiManager);
		return null;
	
	}

	protected void onPreExecute() {
		this.progress = new ProgressDialog(context);
		this.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.progress.setCancelable(false);
		this.progress.setIndeterminate(true);
		this.progress.show();
	}
	
     protected void onProgressUpdate(Void... progress) {
		 // NOP
     }

     protected void onPostExecute(Void result) {		
		WIFIConfigurationManager.getInstance().NotifyListeners();
		this.progress.hide();
         
     }
 }