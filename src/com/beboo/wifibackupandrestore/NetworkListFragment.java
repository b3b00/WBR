package com.beboo.wifibackupandrestore;



import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.beboo.wifibackupandrestore.backupmanagement.Network;
import com.beboo.wifibackupandrestore.backupmanagement.NetworkDataChangedListener;

/**
* Simple class for showing List of elements in Fragment
* @author Artem Zinnatullin
* @see http://android.artemzin.ru/?p=7
*
*/
public abstract class NetworkListFragment extends ListFragment  implements NetworkDataChangedListener, OnItemClickListener {


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
		ListView lv= (ListView)getActivity().findViewById(android.R.id.list);

		// create the grid item mapping
		String[] from = new String[] {ALIAS_ITEM, SSID_ITEM, KEYMGMT_ITEM, STATE_ITEM};
		int[] to = new int[] {  R.id.alias, R.id.ssid, R.id.keymgmt,R.id.state};

		// prepare the list of all records
		List<Map<String, String>> fillMaps = new ArrayList<Map<String, String>>();
		for(Network network : networks){        	
			fillMaps.add(createItem(network));
		}

		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.onelinelist2, from, to);
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
		ListView lv= (ListView)getActivity().findViewById(android.R.id.list);

		// create the grid item mapping
		String[] from = new String[] {ALIAS_ITEM, SSID_ITEM, KEYMGMT_ITEM, STATE_ITEM};
		int[] to = new int[] {  R.id.alias, R.id.ssid, R.id.keymgmt,R.id.state};

		// prepare the list of all records
		List<Map<String, String>> fillMaps = new ArrayList<Map<String, String>>();
		for(Network network : networks){        	
			fillMaps.add(createItem(network));
		}

		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.onelinelist2, from, to);
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
				return;				
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
		alert.setPositiveButton(getString(R.string.ok), null);
		alert.show();
		
	}

}