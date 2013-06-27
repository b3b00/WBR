package com.beboo.wifibackupandrestore;

import java.io.IOException;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleAdapter;
import android.content.res.Resources;

import com.beboo.wifibackupandrestore.backupmanagement.Network;
import com.beboo.wifibackupandrestore.backupmanagement.WIFIConfigurationManager;

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
import java.io.IOException;
import com.beboo.wifibackupandrestore.backupmanagement.Network;
import com.beboo.wifibackupandrestore.backupmanagement.WIFIConfigurationManager;
import android.widget.SimpleAdapter;


public class ConfiguredFragment extends NetworkListFragment implements ActionMode.Callback {

	private WIFIConfigurationManager confManager;

	private SimpleAdapter contentAdapter;
	
	public ConfiguredFragment() {
		Log.d("WBR","################ new ConfiguredFragment");
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("WBR","initialisation of  configured networks fragment");		
		
	
		

	}
	
	public void onViewCreated(View view, Bundle savedInstanceState) {		
		confManager = WIFIConfigurationManager.getInstance();
		confManager.setConfiguredNetworkChangedListener(this);
	}


// Menu Contextuel
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		// On crée notre menu
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.contextmenu_configured, menu);

		// On récupére le nom pour l'entête du menu
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		menu.setHeaderTitle(" ");

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		

		Log.d("WBR","contextual menu ["+getString(R.string.backup)+"] clicked");

		TextView ssid = (TextView)info.targetView.findViewById(R.id.alias);
		Log.d("WBR","contextual menu ["+getString(R.string.backup)+"] clicked :: was on "+ssid.getText()+" / @"+info.position);
		
		//final Network net = confManager.getBackupedNetworks().get(position);
		final Network net = confManager.getConfiguredNetworkBySsid(ssid.getText().toString());
		if (net != null) {
			switch (item.getItemId()) {
			case R.id.save_network: {

                Log.d("WBR","contextual menu ["+getString(R.string.backup)+"] clicked :: was on "+ssid+" / "+net);
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.backup);


                String msg = getString(R.string.msg_rename_net) + " " +ssid.getText().toString() + " :";
                alert.setMessage(msg);

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                alert.setView(input);

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (id == DialogInterface.BUTTON_POSITIVE) {
                            // TODO Auto-generated method stub
                            String alias = input.getText().toString();
                            Log.d("WBR","setting alias ["+alias+"] to network ["+net.getSsid()+"]");
                            backupNetwork(net, alias);
                        }
                    }
                };

                alert.setPositiveButton(getString(R.string.ok), listener);
                alert.setNegativeButton(getString(R.string.cancel), listener);

                alert.show();
			}
			case R.id.view_conf_network : {
				viewNetwork(net);
				break;
			}


			default:
				return super.onContextItemSelected(item);
			}
		}
		return true;
	}

	
	
	private void backupNetwork(Network network, String alias) {

		Network backupedNetwork = network.clone();
		backupedNetwork.setAlias(alias);

		//ConfiguredActivity.this.contentAdapter.notifyDataSetChanged();
		try {
			WIFIConfigurationManager.getInstance().backupNetwork(backupedNetwork);
		} catch (IOException e) {
			Log.e("WBR","error backuping network : "+e.getMessage());
			e.printStackTrace();
			messageDialog(getString(R.string.backup_failure)+" : "+e.getLocalizedMessage());
		}	
		// TODO half worked
		getListView().invalidate(); 
	}

	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("WBR","contextual menu ["+getString(R.string.backup)+"] clicked");
		TextView ssid = (TextView)getActivity().findViewById(R.id.alias);
		Log.d("WBR","contextual menu ["+getString(R.string.backup)+"] clicked :: was on "+ssid);
		
		//final Network net = confManager.getBackupedNetworks().get(position);
		final Network net = confManager.getConfiguredNetworkBySsid(ssid.getText().toString());

        Resources res = parent.getResources();
        selectRow(net,view,position);

        this.getActivity().startActionMode(this);


	}




	
	/*********************************************
	 * 
	 * 
	 *             mise à jour de la liste
	 * 
	 **********************************************/




	public void onNetworkDataChanged() {

		Log.d("WBR","ConfiguredActivity : configured networks updated");		
		contentAdapter = initList(confManager.getConfiguredNetworks());
		//initList(confManager.getConfiguredNetworks());
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
        inflater.inflate(R.menu.contextmode_configured, menu);
        return true;
    }

    // Called each time the action mode is shown. Always called after onCreateActionMode, but
// may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; // Return false if nothing is done
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
            case R.id.save_network_action: {
                Log.d("WBR","contextual menu ["+getString(R.string.backup)+"] clicked :: was on "+selectedNetwork.getSsid()+" / "+selectedNetwork);
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.backup);


                String msg = getString(R.string.msg_rename_net) + " " +selectedNetwork.getSsid()+ " :";
                alert.setMessage(msg);

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                alert.setView(input);

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (id == DialogInterface.BUTTON_POSITIVE) {
                            // TODO Auto-generated method stub
                            String alias = input.getText().toString();
                            Log.d("WBR","setting alias ["+alias+"] to network ["+selectedNetwork.getSsid()+"]");
                            backupNetwork(selectedNetwork, alias);
                            unSelectRow(selectedView,selectedPosition);
                        }
                    }
                };

                alert.setPositiveButton(getString(R.string.ok), listener);
                alert.setNegativeButton(getString(R.string.cancel), listener);

                alert.show();
                break;
            }
            case R.id.view_conf_network_action : {
                viewNetwork(selectedNetwork);

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
        mode = null;
    }

}