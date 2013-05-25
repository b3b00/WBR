package com.beboo.wifibackupandrestore.backupmanagement;

import java.io.File;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.ResourceBundle;

import com.beboo.wifibackupandrestore.R;
import com.beboo.wifibackupandrestore.WIFIBackupAndRestoreActivity;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WIFIConfigurationManager {

    public static final String WPA_SUPPLICANT_CONF_FILENAME = "wpa_supplicant.conf";
    public static final String BCM_SUPP_CONF_FILENAME = "bcm_supp.conf";
    public static final String BACKUP_FILE_NAME = "wifi.conf";
    public static final String BACKUP_PATH = "/mnt/sdcard/WIFI_Backup_Restore";
    public static final String BACKUP_HISTORY_PATH = BACKUP_PATH+"/history";
    public static final String WIFI_CONFIG_PATH = "/data/misc/wifi";

    List<NetworkDataChangedListener> configuredNetworksListener;
    List<NetworkDataChangedListener> backupedNetworksListener;

    private WifiManager wifiManager;

    private Context context;

    private File backupFile;
    private File configFile;

    private Map<String, Network> configured;
    private Map<String, Network> backuped;
    private ConfReader confReader;

    private static  WIFIConfigurationManager instance;

    protected WIFIConfigurationManager() {
        confReader = new ConfReader();
        configured = new HashMap<String, Network>();
        backuped = new HashMap<String, Network>();
        backupedNetworksListener = new ArrayList<NetworkDataChangedListener>();
        configuredNetworksListener = new ArrayList<NetworkDataChangedListener>();
    }


    private String getConfigurationFile() {
        try {


            String infile = "";
            String outfile = "";

            Log.i("WBR","searching wifi configuration file");



            String wpaCmd = "cp "+WIFIConfigurationManager.WIFI_CONFIG_PATH+"/"+WIFIConfigurationManager.WPA_SUPPLICANT_CONF_FILENAME +
                    "  " + WIFIConfigurationManager.BACKUP_PATH+"/"+WIFIConfigurationManager.WPA_SUPPLICANT_CONF_FILENAME + "\n";

            String bcmCmd = "cp "+WIFIConfigurationManager.WIFI_CONFIG_PATH+"/"+WIFIConfigurationManager.BCM_SUPP_CONF_FILENAME +
                    "  " + WIFIConfigurationManager.BACKUP_PATH+"/"+WIFIConfigurationManager.BCM_SUPP_CONF_FILENAME + "\n";



            Log.i("WBR","trying to get superuser perm");
            Process suProcess = Runtime.getRuntime().exec("su");
            Log.i("WBR","su done");
            DataOutputStream input  = new DataOutputStream(suProcess.getOutputStream());

            Log.i("WBR","copying wpa conf file :: "+wpaCmd);
            input.writeBytes(wpaCmd);
            input.flush();

            Log.i("WBR","copying bcm conf file :: "+bcmCmd);
            input.writeBytes(bcmCmd);
            input.flush();

            input.writeBytes("exit\n");
            input.flush();
            suProcess.waitFor();
            Thread.sleep(1500);

            Log.i("WBR","conf files copy done");

            File wpaFile = new File(WIFIConfigurationManager.BACKUP_PATH+"/"+WIFIConfigurationManager.WPA_SUPPLICANT_CONF_FILENAME);
            File bcmFile = new File(WIFIConfigurationManager.BACKUP_PATH+"/"+WIFIConfigurationManager.BCM_SUPP_CONF_FILENAME);
            File file = null;
            if(wpaFile.exists()) {
                Log.i("WBR","found wpa file");
                file = wpaFile;
            }
            else {
                Log.i("WBR","wpa file does not exists");
                if(bcmFile.exists()) {
                    Log.i("WBR","found bcm file");
                    file = bcmFile;
                }
                else {
                    Log.i("WBR","bcm file does not exists");
                }
            }
            Log.i("WBR","find conf file :: "+file.getAbsolutePath());

            return file.getAbsolutePath();
        }
        catch (Exception e) {
            Log.e("WBR","failed to get superuser perm :: " +e.getMessage());
            return null;
        }


    }

    private void initContent() {
        File backupFile = new File(WIFIConfigurationManager.BACKUP_PATH,WIFIConfigurationManager.BACKUP_FILE_NAME);
        String configFilename = getConfigurationFile();
        loadBackuped(backupFile.getAbsolutePath());
        loadConfigured(configFilename);
        if (configFilename.contains("sdcard")) {
            File config = new File(configFilename);
            config.delete();
        }
    }

    public void NotifyListeners() {
        notifyBackupedNetworkListener();
        notifyConfiguredNetworkListener();
    }

    public void refresh() {
        initContent();
        //NotifyListeners();
    }

    public void init(Context context, WifiManager  wifiManager) {
        this.context = context;
        this.wifiManager = wifiManager;
        initContent();
    }


    public boolean isConfigured(String ssid) {
        return configured.get(ssid) != null;
    }

    public boolean isBackuped(String ssid) {
        Network net = backuped.get(ssid);
        Log.d("WBR","testing backuped state for ["+ssid+"] :: "+net+" / "+(net != null));
        return net != null;

    }

    public void setConfiguredNetworkChangedListener(NetworkDataChangedListener listener) {
        configuredNetworksListener.add(listener);
        if (configured != null && configured.size() > 0) {
            listener.onNetworkDataChanged();
        }
    }

    public void setBackupedNetworkChangedListener(NetworkDataChangedListener listener) {
        backupedNetworksListener.add(listener);
        if (backuped != null && backuped.size() > 0) {
            listener.onNetworkDataChanged();
        }
    }

    private void notifyBackupedNetworkListener() {
        for (NetworkDataChangedListener listener : backupedNetworksListener) {
            listener.onNetworkDataChanged();
        }
    }

    private void notifyConfiguredNetworkListener() {
        for (NetworkDataChangedListener listener : configuredNetworksListener) {
            listener.onNetworkDataChanged();
        }
    }

    public void deleteBackupedNetwork(Network net) {
        backuped.remove(net.getSsid());
        try {
            reGenerateBackupFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        notifyBackupedNetworkListener();
//		notifyConfiguredNetworkListener();

    }


    public void addConfiguredNetwork(Network net) {
        configured.put(net.getSsid(),net);
        //notifyConfiguredNetworkListener();
    }

    public void addBackupedNetwork(Network net) {
        backuped.put(net.getSsid(), net);
        //notifyBackupedNetworkListener();
    }

    public Network getConfiguredNetworkBySsid(String ssid) {
        return configured.get(ssid);
    }

    public Network getBackupedNetworkBySsid(String ssid) {
        return backuped.get(ssid);
    }

    public List<Network> getConfiguredNetworks() {
        List<Network> networks = new ArrayList<Network>();
        for (Network network : configured.values()) {
            if (isBackuped(network.getSsid())) {
                network.setState(context.getString(R.string.backuped));
            }
            else {
                network.setState("");
            }
            networks.add(network);
        }
        return networks;
    }

    public List<Network> getBackupedNetworks() {
        List<Network> networks = new ArrayList<Network>();
        for (Network network : backuped.values()) {
            if (isConfigured(network.getSsid())) {
                network.setState(context.getString(R.string.configured));
            }
            Log.d("WBR"," add network to backuped list : "+network.formatForWIFIBackupFile());
            networks.add(network);
        }
        return networks;
    }

    public static WIFIConfigurationManager getInstance() {
        if (instance == null) {
            instance = new WIFIConfigurationManager();
        }
        return instance;
    }


    private void chmod(File file, String rights) {
        try {
            Runtime.getRuntime().exec("chmod "+rights+" "+file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadBackuped(String filename) {
        try {
            backupFile = new File(filename);
            backuped = confReader.getNetworks(filename);
            debugNetworks("backupeds", backuped);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (RuntimeException re) {
            re.printStackTrace();
        }
    }

    private void debugNetworks(String name, Map<String, Network> networks) {
        Log.d("WBR","======================");
        Log.d("WBR","===     "+name+" - "+networks.size()+"      ===");
        Log.d("WBR","======================");
        for (Network n : networks.values()) {
            Log.d("WBR","-----------------");
            Log.d("WBR",n.formatForWIFIBackupFile());
            Log.d("WBR","-----------------");
        }
        Log.d("WBR","======================");
    }

    public void loadConfigured(String filename) {
        try {
            Log.d("WBR","reading file "+filename);
            File confDir = new File(WIFI_CONFIG_PATH);
            chmod(confDir,"777");
            configFile = new File(filename);
            chmod(configFile,"777");
            configured = confReader.getNetworks(filename);
            debugNetworks("configureds", configured);

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (RuntimeException re) {
            re.printStackTrace();
        }
    }


    private int decodeKeyMgmt(String keyMgmt) {
        if (keyMgmt.equalsIgnoreCase(Network.KEYMGMT_WPA_PSK)) {
            return WifiConfiguration.KeyMgmt.WPA_PSK;
        }
        else if (keyMgmt.equalsIgnoreCase(Network.KEYMGMT_WPA_EAP)) {
            return WifiConfiguration.KeyMgmt.WPA_EAP;
        }
        else if (keyMgmt.equalsIgnoreCase(Network.KEYMGMT_IEE8021X)) {
            return WifiConfiguration.KeyMgmt.IEEE8021X;
        }
        else {
            return WifiConfiguration.KeyMgmt.NONE;
        }
    }

    public void restoreNetwork(Network network)
    {

        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\""+network.getSsid()+"\""; //IMP! This should be in Quotes!!
        //wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.DISABLED;
        wc.priority = 40;
        wc.allowedKeyManagement.set(decodeKeyMgmt(network.getKeyManagment()));
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        //wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        //wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        //wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        //wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wc.preSharedKey="\""+network.getShatedKey()+"\"";


        boolean res1 = wifiManager.setWifiEnabled(true);
        int res = wifiManager.addNetwork(wc);
        Log.d("WifiPreference", "add Network returned " + res );
        boolean es = wifiManager.saveConfiguration();
        Log.d("WifiPreference", "saveConfiguration returned " + es );
        boolean b = wifiManager.enableNetwork(res, true);
        Log.d("WifiPreference", "enableNetwork returned " + b );

        Log.d("WBR","restore of ["+network.getAlias()+"] ended");
        addConfiguredNetwork(network);
        Network backupedNetwork = getBackupedNetworkBySsid(network.getSsid());
        backupedNetwork.setState(context.getString(R.string.configured));
        addBackupedNetwork(backupedNetwork);



        notifyBackupedNetworkListener();

    }

    public void reGenerateBackupFile() throws IOException {

        if (backupFile.exists()) {
            backupFile.delete();
        }
        FileWriter writer = new FileWriter(backupFile,true);
        for (Network network : backuped.values()) {
            writer.append("\n");
            writer.append(network.formatForWIFIBackupFile());
            writer.append("\n");
        }
        writer.flush();
        writer.close();
    }

    public void renameNetwork(Network network,String alias) throws IOException {
        network.setAlias(alias);
        reGenerateBackupFile();
    }

    public void backupNetwork(Network network) throws IOException {
        if (backuped.get(network.getSsid()) == null) {
            Log.d("WBR","starting backup of network "+network);
            // if AP is not yet backuped
            // append it to the end of backup file
            FileWriter writer = new FileWriter(backupFile,true);
            writer.append("\n");
            writer.append(network.formatForWIFIBackupFile());
            writer.append("\n");
            writer.flush();
            writer.close();
            Log.d("WBR","backup of ["+network.getAlias()+"] ended");
            addBackupedNetwork(network);
            Network configured = getConfiguredNetworkBySsid(network.getSsid());
            configured.setState(context.getString(R.string.backuped));
            addConfiguredNetwork(configured);
            notifyConfiguredNetworkListener();
        }
        else  {
            Log.d("WBR","network already backuped ... not saving");
            reGenerateBackupFile();
        }
    }



}
