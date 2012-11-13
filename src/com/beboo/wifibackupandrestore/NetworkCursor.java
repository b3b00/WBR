package com.beboo.wifibackupandrestore;

import java.util.List;

import android.database.AbstractCursor;

import com.beboo.wifibackupandrestore.backupmanagement.Network;
import com.beboo.wifibackupandrestore.backupmanagement.WIFIConfigurationManager;

public class NetworkCursor extends AbstractCursor {

	
	int position = 0;
	List<Network> content = null;
	
	
	
	
	@Override
	public String[] getColumnNames() {
		// TODO Auto-generated method stub
		return new String[]{"network"};
	}

	@Override
	public int getCount() {		
		return WIFIConfigurationManager.getInstance().getBackupedNetworks().size();
	}

	@Override
	public double getDouble(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getFloat(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInt(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLong(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getShort(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getString(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNull(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
