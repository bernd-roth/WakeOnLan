package at.co.netconsulting.wakeonlan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import at.co.netconsulting.wakeonlan.database.DBHelper;
import at.co.netconsulting.wakeonlan.general.BaseActivity;
import at.co.netconsulting.wakeonlan.general.SharedPreferenceModel;
import at.co.netconsulting.wakeonlan.poj.EntryPoj;

public class MainActivity extends BaseActivity {

    private DBHelper dbHelper;
//    private ImageView imageView;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView entryListView;
    private EntryAdapter entryAdapter;
    private SharedPreferences sharedPreferences;
    private Button delete;

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
               entryAdapter.clear();
               getEntriesFromDb();
               swipeRefreshLayout.setRefreshing(false);
           }
       });
        //imageView = (ImageView) findViewById(R.id.entry_icon);
        initializeObjects();

        // Populate the list, through the adapter
        getEntriesFromDb();

        entryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {
                openDialog(entryAdapter.getItem(position), position);
                return true;
            }
        });

        entryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SharedPreferenceModel sharedPreferenceModel = new SharedPreferenceModel(getApplicationContext());
                int savedRadioIndex = sharedPreferenceModel.getIntSharedPreference("Server_Or_Group");

                sharedPreferences = getSharedPreferences("PREFS_PORT", 0);
                int port = sharedPreferences.getInt("PREFS_PORT", 9);

                sharedPreferences = getSharedPreferences("PREFS_CHECKBOX_CSV",0);
                boolean checkboxCsvEvaluation = sharedPreferences.getBoolean("PREFS_CHECKBOX_CSV", false);

                EntryPoj entryPoj;
                if(checkboxCsvEvaluation && savedRadioIndex == 1) {
                    entryPoj = entryAdapter.getItem(position);
                    String ip = entryPoj.getIp_address();
                    String broadcast = entryPoj.getBroadcast();
                    String nicMac = entryPoj.getNic_mac();
                    String groupName = entryPoj.getGroup_name();
                    String hostname = entryPoj.getHostname();
                    for(int i = 0; i<entryAdapter.getCount();i++) {
                        entryPoj = entryAdapter.getItem(i);
                        String groupNamePosition = entryPoj.getGroup_name();
                        if(groupNamePosition.equals(groupName)) {
                            AsyncTask<Object, Object, Object> send = new MagicPacket(broadcast, nicMac, port).execute();
                            Toast.makeText(getApplicationContext(), "Magic Packet to hostname " +entryPoj.getHostname()+ " was sent", Toast.LENGTH_LONG).show();
                        }
                    }
                } else
                //Only for one server/client to start
                if(savedRadioIndex == 0){
                    entryPoj = entryAdapter.getItem(position);
                    String ip = entryPoj.getIp_address();
                    String broadcast = entryPoj.getBroadcast();
                    String nicMac = entryPoj.getNic_mac();
                    String hostname = entryPoj.getHostname();
                    AsyncTask<Object, Object, Object> send = new MagicPacket(broadcast, nicMac, port).execute();
                    Toast.makeText(getApplicationContext(), "Magic Packet to hostname " +hostname+ " was sent", Toast.LENGTH_LONG).show();
                //Start more than 1 server/client
                }else if(savedRadioIndex == 1 && !checkboxCsvEvaluation){
                    entryPoj = entryAdapter.getItem(position);
                    List<EntryPoj> listPoj = dbHelper.getAllEntriesByGroupName(entryPoj.getGroup_name());
                    for(int i = 0; i<listPoj.size(); i++) {
                        String ip = listPoj.get(i).getIp_address();
                        String broadcast = listPoj.get(i).getBroadcast();
                        String nicMac = listPoj.get(i).getNic_mac();
                        String group = listPoj.get(i).getGroup_name();
                        AsyncTask<Object, Object, Object> send = new MagicPacket(broadcast, nicMac, port).execute();
                        Toast.makeText(getApplicationContext(), "Magic Packet to group " +group+ " was sent", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void getEntriesFromDb() {
        //CSV or DB
        sharedPreferences = getSharedPreferences("PREFS_FILENAME",0);
        String checkboxEvaluation = sharedPreferences.getString("PREFS_FILENAME", "");

        sharedPreferences = getSharedPreferences("PREFS_CHECKBOX_CSV",0);
        boolean checkboxCsvEvaluation = sharedPreferences.getBoolean("PREFS_CHECKBOX_CSV", false);

        if(checkboxCsvEvaluation==false){
            List<EntryPoj> listEntryPoj = dbHelper.getAllEntries();
            entryAdapter.addAll(listEntryPoj);
        }else if(checkboxCsvEvaluation && !checkboxEvaluation.equals("")){
            importCSV();
        }
    }

    private void openDialog(EntryPoj entryPoj, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_hosts, null);
        EditText editTextHostname = (EditText) dialogView.findViewById(R.id.hostname);
        EditText editTextGroupname = (EditText) dialogView.findViewById(R.id.groupname);
        EditText editTextIpaddress = (EditText) dialogView.findViewById(R.id.ipaddress);
        EditText editTextBroadcast = (EditText) dialogView.findViewById(R.id.broadcast);
        EditText editTextNicMac = (EditText) dialogView.findViewById(R.id.nicmac);
        EditText editTextComment = (EditText) dialogView.findViewById(R.id.comment);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_hosts, null))
                // Add action buttons
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String hostname = editTextHostname.getText().toString();
                        String groupName = editTextGroupname.getText().toString();
                        String ip = editTextIpaddress.getText().toString();
                        String broadcast = editTextBroadcast.getText().toString();
                        String nicmac = editTextNicMac.getText().toString();
                        String comment = editTextComment.getText().toString();
                        dbHelper.update(hostname, groupName, ip, broadcast, nicmac, comment, position+1);
                        entryAdapter.clear();
                        getEntriesFromDb();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String hostname = editTextHostname.getText().toString();
                        String groupName = editTextGroupname.getText().toString();
                        String ip = editTextIpaddress.getText().toString();
                        String broadcast = editTextBroadcast.getText().toString();
                        String nicmac = editTextNicMac.getText().toString();
                        String comment = editTextComment.getText().toString();

                        EntryPoj entryPoj2 = new EntryPoj(hostname, groupName, ip, broadcast, nicmac, comment);
                        dbHelper.deleteSelectedEntry(entryPoj2);
                        entryAdapter.clear();
                        getEntriesFromDb();
                    }
                });

        builder.setView(dialogView);

        editTextHostname.setText(entryPoj.getHostname());
        editTextGroupname.setText(entryPoj.getGroup_name());
        editTextIpaddress.setText(entryPoj.getIp_address());
        editTextBroadcast.setText(entryPoj.getIp_address());
        editTextNicMac.setText(entryPoj.getNic_mac());
        editTextComment.setText(entryPoj.getComment());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Initializing objects
    private void initializeObjects() {
        dbHelper = new DBHelper(getApplicationContext());
         // Setup the list view
        entryListView = (ListView) findViewById(R.id.list);
        entryAdapter = new EntryAdapter(this, R.layout.entry_list_item);
        entryListView.setAdapter(entryAdapter);
        sharedPreferences = getSharedPreferences("PREFS_RADIOGROUP_SETTINGS",0);
     }

    public void showMenu(MenuItem item) {
        onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        entryAdapter.clear();
        getEntriesFromDb();
    }

    public void importCSV() {
        try {
            sharedPreferences = getSharedPreferences("PREFS_FILENAME", 0);
            String csvFileName = sharedPreferences.getString("PREFS_FILENAME", "/server_client.txt");

            String hostname = null, groupName, ip, broadcast, mac, comment;
            List<EntryPoj> listEntryPoj = new ArrayList<EntryPoj>();
            File csvfile = new File(Environment.getExternalStorageDirectory() + "/" + csvFileName);
            CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String[] splitLine = nextLine[0].split(";");
                hostname = splitLine[0];
                groupName = splitLine[1];
                ip = splitLine[2];
                broadcast = splitLine[3];
                mac = splitLine[4];
                comment = splitLine[5];
                EntryPoj poj = new EntryPoj(hostname, groupName, ip, broadcast, mac, comment);
                listEntryPoj.add(poj);
            }
            entryAdapter.addAll(listEntryPoj);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
    }
}