package at.co.netconsulting.wakeonlan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import at.co.netconsulting.wakeonlan.database.DBHelper;
import at.co.netconsulting.wakeonlan.general.BaseActivity;
import at.co.netconsulting.wakeonlan.poj.EntryPoj;

public class MainActivity extends BaseActivity {

    private static final String TAG = "CHECKHOSTS";
    private DBHelper dbHelper;
    private List<EntryPoj> allEntries;
    private List<EntryPoj> entries;
//    private ImageView imageView;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView entryListView;
    private EntryAdapter entryAdapter;
    private ArrayList<EntryPoj> mIpAddressesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set the toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {
               ArrayList<EntryPoj> arrayList = pingIpAddresses();
               //Adapter is being added up by every refresh. Therefore, clearing the content first.
               entryAdapter.clear();

               for(int i = 0; i<arrayList.size(); i++) {
                   String hostname = arrayList.get(i).getHostname();
                   String groupName = arrayList.get(i).getGroup_name();
                   String ipAddress = arrayList.get(i).getIp_address();
                   String nicMac = arrayList.get(i).getNic_mac();
                   String comment = arrayList.get(i).getComment();
                   entryAdapter.add(new EntryPoj("Add hostname", "Add groupname", ipAddress, nicMac, "Add comment"));
               }
               swipeRefreshLayout.setRefreshing(false);
           }
       });
        //imageView = (ImageView) findViewById(R.id.entry_icon);
        initializeObjects();

        // Populate the list, through the adapter
        for(final EntryPoj entry : getEntries()) {
        	entryAdapter.add(entry);
        }

        entryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {
                openDialog(entryAdapter.getItem(position), position);
                return true;
            }
        });
    }

    private void openDialog(EntryPoj entryPoj, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_hosts, null);
        EditText editTextHostname = (EditText) dialogView.findViewById(R.id.hostname);
        EditText editTextGroupname = (EditText) dialogView.findViewById(R.id.groupname);
        EditText editTextIpaddress = (EditText) dialogView.findViewById(R.id.ipaddress);
        EditText editTextNicMac = (EditText) dialogView.findViewById(R.id.nicmac);
        EditText editTextComment = (EditText) dialogView.findViewById(R.id.comment);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_hosts, null))
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String hostname = editTextHostname.getText().toString();
                        dbHelper.update(hostname, position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.setView(dialogView);

        editTextHostname.setText(entryPoj.getHostname());
        editTextGroupname.setText(entryPoj.getGroup_name());
        editTextIpaddress.setText(entryPoj.getIp_address());
        editTextNicMac.setText(entryPoj.getNic_mac());
        editTextComment.setText(entryPoj.getComment());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private List<EntryPoj> getEntries() {
    	allEntries = dbHelper.getAllEntries();

//        if(dbHelper.numberOfRows()==0) {
//            allEntries.add(new EntryPoj("TV-Server", "Server", "192.168.178.23", "00:00", "Nur ein Testeintrag"));
//            allEntries.add(new EntryPoj("TV-Client", "Server", "192.168.178.23", "00:00", "Nur ein Testeintrag"));
//        } else {
//            for (int i = 0; i <= dbHelper.numberOfRows(); i++) {
//                entries.add(allEntries.get(i));
//            }
//        }
    	return allEntries;
    }

    //Initializing objects
    private void initializeObjects() {
        dbHelper = new DBHelper(getApplicationContext());
        entries = new ArrayList<EntryPoj>();
        mIpAddressesList = new ArrayList<EntryPoj>();
        // Setup the list view
        entryListView = (ListView) findViewById(R.id.list);
        entryAdapter = new EntryAdapter(this, R.layout.entry_list_item);
        entryListView.setAdapter(entryAdapter);
    }

    public void showMenu(MenuItem item) {
        onOptionsItemSelected(item);
    }

    public ArrayList<EntryPoj> pingIpAddresses() {
        WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        String subnet = getSubnetAddress(mWifiManager.getDhcpInfo().gateway);

        checkHosts(subnet);
        //Google changed the access permission to /proc/net/arp; its not allowed to access it any
        //longer therefore, this is a workaround to get all the mac addresses. besides, in gradle
        //version has been set to 28
        return getIpFromArpCache();
    }

    private void checkHosts(String subnet) {
        try {
            int timeout=5;
            for (int i=1;i<255;i++) {
                String host=subnet + "." + i;
                if (InetAddress.getByName(host).isReachable(timeout)) {
                    Log.d(TAG, "checkHosts() :: "+host + " is reachable");
                }
            }
        }
        catch (UnknownHostException e) {
            Log.d(TAG, "checkHosts() :: UnknownHostException e : "+e);
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d(TAG, "checkHosts() :: IOException e : "+e);
            e.printStackTrace();
        }
    }

    private ArrayList<EntryPoj> getIpFromArpCache() {
        BufferedReader reader = null;
        mIpAddressesList.clear();
        try {
            Process ipProc = Runtime.getRuntime().exec("ip neighbor");
            ipProc.waitFor();
            if (ipProc.exitValue() != 0) {
                throw new Exception("Unable to access ARP entries");
            }

            reader = new BufferedReader(new InputStreamReader(ipProc.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] neighborLine = line.split("\\s+");

                // We don't have a validated ARP entry for this case.
                if (neighborLine.length <= 4) {
                    continue;
                }

                String ipaddr = neighborLine[0];
                InetAddress addr = InetAddress.getByName(ipaddr);

                if (addr.isLinkLocalAddress() || addr.isLoopbackAddress()) {
                    continue;
                }
                String macAddress = neighborLine[4];
                EntryPoj entryPoj = new EntryPoj("", "", ipaddr, macAddress, "");
                mIpAddressesList.add(entryPoj);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIpAddressesList;
    }

    private String getSubnetAddress(int address) {
        String ipString = String.format(
                "%d.%d.%d",
                (address & 0xff),
                (address >> 8 & 0xff),
                (address >> 16 & 0xff));
        return ipString;
    }
}