package com.beboo.wifibackupandrestore.backupmanagement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Network {


	private static final String NETWORK_START_LINE="network={";
	private static final String NETWORK_END_LINE="}";

	private static final String SSID_PROP_NAME = "ssid";

	private static final String KEY_MGMT_PROP_NAME = "key_mgmt";

	private static final String ALIAS_PROP_NAME = "alias";

	private static final String PSK_PROP_NAME = "psk";


	public static final String KEYMGMT_IEE8021X = "IEEE8021X";
	public static final String KEYMGMT_WPA_PSK = "WPA-PSK";
	public static final String KEYMGMT_WPA_EAP = "WPA-EAP";
	public static final String KEYMGMT_NONE = "NONE";

	private Map<String,String> properties;
	private String state;

	public Network() {
		properties = new HashMap<String,String>();
	}

	public String getSsid() {
		return properties.get(SSID_PROP_NAME);
	}

	public void setSsid(String ssid) {
		this.properties.put(SSID_PROP_NAME, ssid);
	}

	public String getKeyManagment() {
		return properties.get(KEY_MGMT_PROP_NAME);
	}

	public void setKeyManagment(String keyMgmt) {
		this.properties.put(KEY_MGMT_PROP_NAME, keyMgmt);
	}

	public String getShatedKey() {
		return properties.get(PSK_PROP_NAME);
	}

	public void setSharedKey(String psk) {
		this.properties.put(PSK_PROP_NAME, psk);
	}

	public String getAlias() {
		return properties.get(ALIAS_PROP_NAME);
	}

	public void setAlias(String alias) {
		this.properties.put(ALIAS_PROP_NAME, alias);
	}

	public String getState() {
		// TODO Auto-generated method stub
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	private String unQuote(String value) {
		if (value.startsWith("\"")) {
			value = value.substring(1);
		}
		if (value.endsWith("\"")) {
			value = value.substring(0,value.length()-1);
		}
		return value;
	}

	public void addProperty(String key , String value) {
		this.properties.put(key, unQuote(value));
	}

	public void addProperty(KeyPairValue keyPair) {

		addProperty(keyPair.getKey(),keyPair.getValue());

	}
	
	
	private Map<String,String> cloneProperties() {
		Map<String, String> clone = new HashMap<String, String>();
		for (String k : properties.keySet()) {
			String v = properties.get(k);
			;
			clone.put(String.copyValueOf(k.toCharArray()), String.copyValueOf(v.toCharArray()));
		}
		return clone;
	}
	
	@Override
	public Network clone() {
		Network clone = new Network();
		clone.properties = cloneProperties();
		return clone;
	}

	public String toString() {
		return getAlias()+" : "+getSsid()+" / "+getKeyManagment();
	}

	public String formatForWIFIBackupFile() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(NETWORK_START_LINE+"\n");
		Iterator<Entry<String,String>> entries = properties.entrySet().iterator();  
		while (entries.hasNext()) {
			Entry<String,String> entry = entries.next();
			String value = entry.getValue();
			String key = entry.getKey();
			if (key.equalsIgnoreCase(PSK_PROP_NAME)) {
				value ="\""+value+"\"";
			}
			if (key.equalsIgnoreCase(SSID_PROP_NAME)) {
				value ="\""+value+"\"";
			}
			buffer.append(key+"="+value+"\n");

		}
		buffer.append(NETWORK_END_LINE+"\n");
		return buffer.toString();
	}






}