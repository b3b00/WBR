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
import android.view.MenuInflater;
import android.view.MenuItem;
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

		actionBar.addTab(actionBar.newTab().setText(getResources().getString(R.string.configureds)).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(getResources().getString(R.string.backupeds)).setTabListener(this));


	}
	

		public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_refresh:
				// app icon in action bar clicked; refresh 
				WIFIConfigurationManager.getInstance().refresh();				
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
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
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main_activity, menu);
		return true;
	}

    


    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        
		int tabIndex = tab.getPosition();
		
		NetworkListFragment fragment = fragments.get(tabIndex);
		
		if (fragment == null) {
			if (tabIndex == 0) {
				fragment = new ConfiguredFragment();				
				fragments.put(tabIndex,fragment);
			}
			else if (tabIndex == 1) {
				fragment = new BackupedFragment();			
				fragments.put(tabIndex,fragment);
			}
		}
		
		getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
     
    }

    
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    
}