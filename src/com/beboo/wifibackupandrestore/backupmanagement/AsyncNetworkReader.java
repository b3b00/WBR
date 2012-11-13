package com.beboo.wifibackupandrestore.backupmanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import android.os.AsyncTask;


class AsyncNetworkReader extends AsyncTask<String, Void, Map<String, Network>> {
	Map<String, Network> networks;

	private  KeyPairValue getkeyPairValue(String line) {

		String[] keyvalue =  line.split("=");
		if (keyvalue.length == 2) {
			String key = keyvalue[0].trim();			
			String val = keyvalue[1].trim();			
			return new KeyPairValue(key, val);
		}

		return null;
	}

	protected Map<String, Network> doInBackground(String... filenames) {
		String filename = filenames[0];
		Map<String, Network> networks = new HashMap<String, Network>();
		try {
			if (filename != null && filename.length() > 0) {
				FileReader fileStream = new FileReader(filename);
				BufferedReader buff = new BufferedReader(fileStream);

				int state = ConfReader.STATE_WAITING_NETWORK;

				String line = buff.readLine();
				Network currentNetwork = null; 
				while (line != null) {
					//System.out.println("state is "+(state==STATE_WAITING_NETWORK ? "waiting net":"reading net" ));
					//System.out.println(line);
					switch (state) {
					case ConfReader.STATE_WAITING_NETWORK : {
						if (line.trim().startsWith(ConfReader.NETWORK_START_LINE)) {
							currentNetwork = new Network();
							state = ConfReader.STATE_READING_NETWORK;
						}
						break;
					}
					case ConfReader.STATE_READING_NETWORK : {


						if (line.trim().toUpperCase().startsWith(ConfReader.CLOSE_NETWORK)) {
							System.out.println(networks.size()+" - adding network after read : "+currentNetwork.formatForWIFIBackupFile());
							System.out.println("puttin net "+currentNetwork.getSsid()+" -> "+currentNetwork);
							networks.put(currentNetwork.getSsid(),currentNetwork);
							currentNetwork = null;
							state = ConfReader.STATE_WAITING_NETWORK;
						}
						else {
							KeyPairValue keyPair = getkeyPairValue(line);
							if (keyPair != null) {
								currentNetwork.addProperty(keyPair);
							}
						}
					}
					}
					line = buff.readLine();
				}

			}
		}
		catch (IOException e) {
		}
		finally {
			return networks;
		}


	}

	protected void onProgressUpdate(Void... progress) {

	}
/*
	protected void onPostExecute(Map<String, Network> networks) {		
		// WIFIConfigurationManager.getInstance().setBackupedNetworks(networks);	
		// DO SOMETHING

	}
	
*/	
}