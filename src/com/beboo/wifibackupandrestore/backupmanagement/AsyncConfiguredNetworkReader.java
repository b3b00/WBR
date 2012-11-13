package com.beboo.wifibackupandrestore.backupmanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import android.os.AsyncTask;


class AsyncConfiguredNetworkReader extends AsyncNetworkReader {

	protected void onPostExecute(Map<String, Network> networks) {		
		WIFIConfigurationManager.getInstance().setBackupedNetworks(networks);	
		// DO SOMETHING

	}

}