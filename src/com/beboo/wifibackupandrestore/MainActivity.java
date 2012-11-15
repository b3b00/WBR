package com.beboo.wifibackupandrestore;

import android.util.Log;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Map;
import java.util.HashMap;
import com.beboo.wifibackupandrestore.InitializerTask;
import com.beboo.wifibackupandrestore.backupmanagement.WIFIConfigurationManager;
import java.io.File;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private HashMap<Integer, NetworkListFragment> fragments =new HashMap<Integer,NetworkListFragment>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
	
		initComponents();
		
		initEnv();

	
       
	
	
		
        
    }
	
	
	

	public void initEnv() {
	Log.i("WBR","enter initenv()");
		
		
		Log.i("WBR","setting backup dir "+WIFIConfigurationManager.BACKUP_PATH);
		File backupDir = new File(WIFIConfigurationManager.BACKUP_PATH);
		if (backupDir == null) {
			Log.e("WBR","ERROR !!!! :: backupdir file ("+WIFIConfigurationManager.BACKUP_PATH+") is null"); 
		}
		if ( !backupDir.exists()) {
			if (backupDir.mkdirs()) {
				Log.i("WBR", WIFIConfigurationManager.BACKUP_PATH+" folder created");
			}
			else {
				Log.e("WBR", WIFIConfigurationManager.BACKUP_PATH+" folder creation failed");
			}

		}
		Log.i("WBR","setting backup dir done ");
		
		Log.i("WBR","setting backup histo dir");
		File backupHistoDir = new File(WIFIConfigurationManager.BACKUP_HISTORY_PATH);
		if ( !backupHistoDir.exists()) {
			if (backupHistoDir.mkdirs()) {
				Log.i("WBR", WIFIConfigurationManager.BACKUP_HISTORY_PATH+" folder created");
			}
			else {
				Log.e("WBR", WIFIConfigurationManager.BACKUP_HISTORY_PATH+" folder creation failed");
			}
		}
		Log.i("WBR","setting backup histo dir");
		

		new InitializerTask(this).execute();
		
	}

	private void initComponents() {
		// Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		fragments.put(0,new BackupedFragment());
		fragments.put(1,new BackupedFragment());
		 // For each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText("Config --").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Backup").setTabListener(this));


	}
	

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    


    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        
		getSupportFragmentManager().beginTransaction().replace(R.id.container, fragments.get(tab.getPosition())).commit();
     
    }

    
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
* A dummy fragment representing a section of the app, but that simply displays dummy text.
*/
    public static class DummySectionFragment extends Fragment {
        public DummySectionFragment() {
        }

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            TextView textView = new TextView(getActivity());
            textView.setGravity(Gravity.CENTER);
            Bundle args = getArguments();
            textView.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)));
            return textView;
        }
    }
}