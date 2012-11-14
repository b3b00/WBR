package com.beboo.wifibackupandrestore.backupmanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;







public class ConfReader {

	private static final String CLOSE_NETWORK = "}";

	private static final String NETWORK_START_LINE = "network";




	private static final String NETWORK_END_LINE = "}";

	private static final int STATE_WAITING_NETWORK = 0;

	private static final int STATE_READING_NETWORK = 1;


	private  KeyPairValue getkeyPairValue(String line) {

		String[] keyvalue =  line.split("=");
		if (keyvalue.length == 2) {
			String key = keyvalue[0].trim();			
			String val = keyvalue[1].trim();			
			return new KeyPairValue(key, val);
		}

		return null;
	}


	public  Map<String, Network> getNetworks(String filename) throws IOException {
		Map<String, Network> networks = new HashMap<String, Network>();
		if (filename != null && filename.length() > 0) {
			FileReader fileStream = new FileReader(filename);
			BufferedReader buff = new BufferedReader(fileStream);

			int state = STATE_WAITING_NETWORK;

			String line = buff.readLine();
			Network currentNetwork = null; 
			while (line != null) {
				//System.out.println("state is "+(state==STATE_WAITING_NETWORK ? "waiting net":"reading net" ));
				//System.out.println(line);
				switch (state) {
				case STATE_WAITING_NETWORK : {
					if (line.trim().startsWith(NETWORK_START_LINE)) {
						currentNetwork = new Network();
						state = STATE_READING_NETWORK;
					}
					break;
				}
				case STATE_READING_NETWORK : {


					if (line.trim().toUpperCase().startsWith(CLOSE_NETWORK)) {
						System.out.println(networks.size()+" - adding network after read : "+currentNetwork.formatForWIFIBackupFile());
						System.out.println("puttin net "+currentNetwork.getSsid()+" -> "+currentNetwork);
						networks.put(currentNetwork.getSsid(),currentNetwork);
						currentNetwork = null;
						state = STATE_WAITING_NETWORK;
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
		return networks;
	}


	public static void main(String[] args) {
		System.out.println(args[0]);

		try {
			ConfReader reader = new ConfReader();




			Map<String,Network> networks = reader.getNetworks(args[0]);
			System.out.println("found "+networks.size()+" networks");
			for (Network n : networks.values()) {
				System.out.println("============================" );
				System.out.println(n.formatForWIFIBackupFile());
				System.out.println();
				System.out.println("---------");
				System.out.println();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
