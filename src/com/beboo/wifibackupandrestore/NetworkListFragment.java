package com.beboo.wifibackupandrestore;



import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import com.beboo.wifibackupandrestore.backupmanagement.Network;
import com.beboo.wifibackupandrestore.backupmanagement.NetworkDataChangedListener;
import com.beboo.wifibackupandrestore.backupmanagement.WIFIConfigurationManager;


public abstract class NetworkListFragment extends ListFragment  implements NetworkDataChangedListener, OnItemClickListener, ActionMode.Callback {


private static final String KEYMGMT_ITEM = "keymgmt";
private static final String SSID_ITEM = "ssid";
private static final String ALIAS_ITEM = "alias";
private static final String STATE_ITEM = "state";

public NetworkListFragment() {

}

@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.network_list_fragment, container, false);
	}

	

	public SimpleAdapter initList(List<Network> networks) {
		ListView lv= getListView();

		// create the grid item mapping
		String[] from = new String[] {ALIAS_ITEM, SSID_ITEM, KEYMGMT_ITEM, STATE_ITEM};
		int[] to = new int[] {  R.id.alias, R.id.ssid, R.id.keymgmt,R.id.state};

		// prepare the list of all records
		List<Map<String, String>> fillMaps = new ArrayList<Map<String, String>>();
		for(Network network : networks){        	
			fillMaps.add(createItem(network));
		}

		// fill in the grid_item layout
		SimpleAdapter adapter = new NetworkListAdapter(getActivity(), fillMaps, R.layout.onelinelist2, from, to);
		lv.setAdapter(adapter);


		registerForContextMenu(lv);
		
		lv.setOnItemClickListener(this);

		return adapter;


	}
	
	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		// TODO
	}
	
	public SimpleAdapter refresh(List<Network> networks) {
		ListView lv= getListView();

		// create the grid item mapping
		String[] from = new String[] {ALIAS_ITEM, SSID_ITEM, KEYMGMT_ITEM, STATE_ITEM};
		int[] to = new int[] {  R.id.alias, R.id.ssid, R.id.keymgmt,R.id.state};

		// prepare the list of all records
		List<Map<String, String>> fillMaps = new ArrayList<Map<String, String>>();
		for(Network network : networks){        	
			fillMaps.add(createItem(network));
		}

		// fill in the grid_item layout
		SimpleAdapter adapter = new NetworkListAdapter(getActivity(), fillMaps, R.layout.onelinelist2, from, to);
		lv.setAdapter(adapter);


		registerForContextMenu(lv);

		return adapter;
	}

	Map<String, String> createItem(Network network) {
		Map<String,String> item = new HashMap<String, String>();
		String alias = network.getAlias();
		if (alias != null && alias.length() > 0) {
			item.put(ALIAS_ITEM, alias );
			item.put(SSID_ITEM,network.getSsid());
			item.put(KEYMGMT_ITEM, network.getKeyManagment());
			
		}
		else {
			item.put(ALIAS_ITEM, network.getSsid() );
			item.put(SSID_ITEM,network.getKeyManagment());
		}
		item.put(STATE_ITEM, network.getState());
		return item;
	}
	
	protected void messageDialog(String message) { 
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create(); 
		alertDialog.setTitle("");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		alertDialog.setMessage(message);
		alertDialog.show();
		Log.i("WBR"," MESSAGE :: "+message);

	}
	
	public void viewNetwork(Network net) {		
		
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("d\u00e9tails du r\u00e9seau");
		alert.setMessage("Alias : "+net.getAlias()+" \nSSID : "+net.getSsid()+" \nkeymgmt : "+net.getKeyManagment()+" \nPSK : "+net.getShatedKey());

		// Set an EditText view to get user input 		
		alert.setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        unSelectRow(selectedView,selectedPosition);
                                    }
                                });
		alert.show();
		
	}


    /************************************************
     *
     * actionMode
     *
     ************************************************/

    protected ActionMode actionMode;

    protected Network selectedNetwork;

    protected View selectedView;

    protected int selectedPosition = -1;

    protected WIFIConfigurationManager confManager;

    public abstract Network getNetworkByView(View view);

    protected void selectRow(View view, int position) {
        Resources res = getActivity().getResources();
        if (position % 2 == 0) {
            //@android:color/holo_blue_light;
            view.setBackgroundColor(res.getColor(R.color.selected_even_line));
        }
        else {
            view.setBackgroundColor(res.getColor(R.color.selected_odd_line));
        }
    }

    protected void unSelectRow(View view, int position) {
        Resources res = getActivity().getResources();
        if (position % 2 == 0) {
            view.setBackgroundColor(res.getColor(R.color.even_line));
        }
        else {
            view.setBackgroundColor(res.getColor(R.color.odd_line));
        }
    }




    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        final Network net = getNetworkByView(view);



        Log.d("WBR","state : selectepos::"+selectedPosition+" / selectedNetwork ::"+selectedNetwork);

        if (position == selectedPosition) {
            //
            // deselection d'une ligne
            //
            selectedNetwork = null;
            selectedPosition = -1;
            selectedView = null;
            Log.d("WBR"," new state :: selectepos::"+selectedPosition+" / selectedNetwork ::"+selectedNetwork);
            Log.d("WBR","position is the same => selection @"+position);
            unSelectRow(view,position);

            Log.d("WBR","click on same position => hiding action mode ... "+(actionMode != null));
            if (actionMode != null) {
                Log.d("WBR","hiding action mode");
                actionMode.finish();
            }
        }
        else {
            //
            // selection d'une ligne (differente ou non)
            //
            Log.d("WBR","position is not the same => select @"+position+" && unselect @"+selectedPosition);
            if (selectedPosition >= 0) {
                Log.d("WBR","selectedPos["+selectedPosition+"] >= 0 ==> unselecting");
                unSelectRow(selectedView,selectedPosition);
            }
            Log.d("WBR","selecting @"+position);
            selectRow(view,position);

            selectedNetwork = net;
            selectedPosition = position;
            selectedView = view;
            Log.d("WBR"," new state :: selectepos::"+selectedPosition+" / selectedNetwork ::"+selectedNetwork);
            this.getActivity().startActionMode(this);
        }
        Log.d("WBR","########################################");

/*
        if (selectedNetwork == null) {
            selectRow(net,view,position);
            this.getActivity().startActionMode(this);
        }
        else {
            unSelectRow(view,position);

            if (actionMode != null) {
                actionMode.finish();
            }
        }
*/



    }

}