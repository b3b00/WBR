package com.beboo.wifibackupandrestore;

import android.content.Context;
import android.os.Handler;
import java.io.File;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.Toast;
import android.util.Log;
import com.beboo.wifibackupandrestore.InitializerTask;

import com.beboo.wifibackupandrestore.backupmanagement.WIFIConfigurationManager;

public class WIFIBackupAndRestoreActivity extends TabActivity  implements OnClickListener {


	File wpaFile = null;
	File bcmFile = null;

	boolean wpaExists = false;
	boolean bcmExists = false;

	
	
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("WBR","starting WBR");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		initComponents();
		
		initEnv();

	}
	

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}*/
	
	private String getMessage(InputStream is) {
		try {
			int len = is.available();
			byte[] buf = new byte[len];
			is.read(buf,0,len);
			return new String(buf);
		}
		catch(Exception e) {			
			return "error "+e.getMessage()+" while reading stream";
		}
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main_activity, menu);
		return true;
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
		

		new InitializerTask().execute(this);
		
	}

	private void initComponents() {

		setContentView(R.layout.main);
		TabHost tabHost = getTabHost(); // On recupere l'activite
		TabHost.TabSpec spec; // Tabspec réutilisable pour chaque onglet
		// Intent réutilisable pour chaque onglet

		// Creation d'un intent pour un onglet
		Intent configIntent = new Intent().setClass(this, ConfiguredActivity.class);

		//Initialisation d'une spec puis ajout au tabHost
		String indicator = getResources().getString(R.string.configureds);
		spec = tabHost.newTabSpec("config").setIndicator(indicator).setContent(configIntent);
		tabHost.addTab(spec);

		// de meme pour le second onglet
		Intent backupIntent = new Intent().setClass(this, BackupedActivity.class);
		indicator = getResources().getString(R.string.backupeds);
		spec = tabHost.newTabSpec("backup").setIndicator(indicator).setContent(backupIntent);

		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);


	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_refresh:
				// app icon in action bar clicked; refresh 
				//Toast.makeText(this,"rafraichir",Toast.LENGTH_LONG).show();
				WIFIConfigurationManager.getInstance().refresh();				
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}



	private void messageDialog(String message) { 
		AlertDialog alertDialog = new AlertDialog.Builder(this).create(); 
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










	public void onClick(View v) {
		Log.i("WBR","onClick");

	}


}
