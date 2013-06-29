package com.beboo.wifibackupandrestore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.view.ActionMode;
import android.view.ActionMode.Callback;

import com.beboo.wifibackupandrestore.backupmanagement.Network;
import com.beboo.wifibackupandrestore.backupmanagement.WIFIConfigurationManager;

public class BackupedFragment extends NetworkListFragment implements ActionMode.Callback {

    private ActionMode actionMode;



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



	public void restoreNetwork(Network net) {
		confManager.restoreNetwork(net);
	}


    public void editNetwork(final Network  net) {
        Log.d("WBR","contextual menu ["+getString(R.string.backup)+"] clicked :: was on "+net.getSsid()+" / "+net);
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.backup);

        String msg = getString(R.string.msg_set_alias_net) + " " + net.getSsid() + " :";
        alert.setMessage(msg);

        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        alert.setView(input);



        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (id == DialogInterface.BUTTON_POSITIVE) {
                    String alias = input.getText().toString();
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

    }




    @Override
    public Network getNetworkByView(View view) {
        TextView  ssidText = (TextView)view.findViewById(R.id.ssid);
        String ssid = ssidText.getText().toString();
        return confManager.getBackupedNetworkBySsid(ssid);
    }



	/*********************************************
	 * 
	 *             mise à jour de la liste
	 *     
	 **********************************************/

	public void onNetworkDataChanged() {

		Log.d("WBR","BackupedActivity : backuped networks updated");
		contentAdapter = initList(confManager.getBackupedNetworks());

		//initList(confManager.getBackupedNetworks());
	}


    /************************************************
     *
     * actionMode
     *
     ************************************************/





    // Called when the action mode is created; startActionMode() was called
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.contextmode_backuped, menu);
        return true;
    }

    // Called each time the action mode is shown. Always called after onCreateActionMode, but
// may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        actionMode = mode;
        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    // Called when the user selects a contextual menu item
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        Context ctx = getActivity();
        switch (item.getItemId()) {
            case R.id.restore_network_action: {
                restoreNetwork(selectedNetwork);
                break;
            }
            case R.id.view_backup_network_action : {
                viewNetwork(selectedNetwork);
                break;
            }
            case R.id.delete_backup_network_action : {
                confManager.deleteBackupedNetwork(selectedNetwork);
                break;
            }
            case R.id.edit_backup_network_action : {
                editNetwork(selectedNetwork);
                break;
            }
        }



        if (mode != null) {
            mode.finish();
        }
        return true;
    }

    // Called when the user exits the action mode
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.actionMode = null;
    }

}