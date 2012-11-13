package com.beboo.wifibackupandrestore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.beboo.wifibackupandrestore.backupmanagement.Network;
import com.beboo.wifibackupandrestore.backupmanagement.WIFIConfigurationManager;
import android.widget.SimpleAdapter;

public class BackupedActivity extends NetworkListActivity {

	private WIFIConfigurationManager confManager;

	private SimpleAdapter contentAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("WBR","initialisation of  backuped networks activity");
		setContentView(R.layout.networklist);
		confManager = WIFIConfigurationManager.getInstance();
		contentAdapter = initList(confManager.getBackupedNetworks());

		ListView lv = getListView();

		// Listener : on envoie un intent avec l'id du contact
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {				
				//TODO : display network details
			}
		});

		confManager.setBackupedNetworkChangedListener(this);

	}



	/*********************************************
	 * 
	 *             menu contextuel
	 * 
	 **********************************************/

	// Menu Contextuel
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		// On crée notre menu
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contextmenu_backuped, menu);

		// On récupère le nom pour l'entête du menu
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int position =  info.position;
		Network net = confManager.getBackupedNetworks().get(position);

	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		Log.d("WBR","contextual menu ["+getString(R.string.backup)+"] clicked");
		TextView ssid = (TextView)info.targetView.findViewById(R.id.ssid);
		Log.d("WBR","contextual menu ["+getString(R.string.backup)+"] clicked :: was on "+ssid.getText()+" / @"+info.position);
		final Network net = confManager.getBackupedNetworkBySsid(ssid.getText().toString());
		
		if (net != null) {
			switch (item.getItemId()) {
			case R.id.restore_network: {
				restoreNetwork(net);
				break;
			}		
			case R.id.view_backup_network : {
				viewNetwork(net);
				break;
			}
			case R.id.delete_backup_network : {
				confManager.deleteBackupedNetwork(net); 
			}
			}
		}
		return true;
	}
	
	
	private void restoreNetwork(Network net) {
		confManager.restoreNetwork(net);
	}



	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("WBR","contextual menu ["+getString(R.string.backup)+"] clicked");
		TextView ssid = (TextView)findViewById(R.id.ssid);
		Log.d("WBR","contextual menu ["+getString(R.string.backup)+"] clicked :: was on "+ssid);
		
		//final Network net = confManager.getBackupedNetworks().get(position);
		final Network net = confManager.getConfiguredNetworkBySsid(ssid.getText().toString());
		viewNetwork(net);
	}


	/*********************************************
	 * 
	 *             mise à jour de la liste
	 *     
	 **********************************************/

	public void onNetworkDataChanged() {

		Log.d("WBR","BackupedActivity : backuped networks updated");
		initList(confManager.getBackupedNetworks());
	}

}
