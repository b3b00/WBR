package com.beboo.wifibackupandrestore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.beboo.wifibackupandrestore.backupmanagement.Network;
import com.beboo.wifibackupandrestore.backupmanagement.WIFIConfigurationManager;

public class BackupedFragment extends NetworkListFragment {

	private WIFIConfigurationManager confManager;

	private SimpleAdapter contentAdapter;
	
	public BackupedFragment() {
		Log.d("WBR","################ new BackupedFragment");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
			
		Log.d("WBR","initialisation of  backuped networks fragment");		
		


	}
	
	public void onViewCreated(View view, Bundle savedInstanceState) {		
		confManager = WIFIConfigurationManager.getInstance();
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
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.contextmenu_backuped, menu);

		// On récupére le nom pour l'entête du menu
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
            case R.id.edit_backup_network : {

                Log.d("WBR","contextual menu ["+getString(R.string.backup)+"] clicked :: was on "+ssid+" / "+net);
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.backup);

                String msg = getString(R.string.msg_set_alias_net) + " " + ssid.getText().toString() + " :";
                alert.setMessage(msg);

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                alert.setView(input);

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (id == DialogInterface.BUTTON_POSITIVE) {
                            String alias = input.getText().toString();
                            Log.d("WBR","setting alias ["+alias+"] to network ["+net.getSsid()+"]");
                            try {
                                confManager.renameNetwork(net,alias);
                            }
                            catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };

                alert.setPositiveButton(getString(R.string.ok), listener);
                alert.setNegativeButton(getString(R.string.cancel), listener);

                alert.show();

                break;
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
		TextView ssid = (TextView)getActivity().findViewById(R.id.ssid);
		Log.d("WBR","contextual menu ["+getString(R.string.backup)+"] clicked :: was on "+ssid);
		
		//final Network net = confManager.getBackupedNetworks().get(position);
		final Network net = confManager.getConfiguredNetworkBySsid(ssid.getText().toString());
		viewNetwork(net);
	}


	/*********************************************
	 * 
	 *             mise � jour de la liste
	 *     
	 **********************************************/

	public void onNetworkDataChanged() {

		Log.d("WBR","BackupedActivity : backuped networks updated");
		contentAdapter = initList(confManager.getBackupedNetworks());
		//initList(confManager.getBackupedNetworks());
	}

}