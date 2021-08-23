package at.co.netconsulting.wakeonlan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.List;
import at.co.netconsulting.wakeonlan.database.DBHelper;
import at.co.netconsulting.wakeonlan.general.BaseActivity;
import at.co.netconsulting.wakeonlan.poj.EntryPoj;

public class MainActivity extends BaseActivity {

    private DBHelper dbHelper;
//    private ImageView imageView;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView entryListView;
    private EntryAdapter entryAdapter;
    private SharedPreferences sharedPreferences;

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
                int radioButtonId = sharedPreferences.getInt("PREFS_RADIOGROUP_SETTINGS", 0);
                sharedPreferences = getSharedPreferences("PREFS_PORT", 0);
                int port = sharedPreferences.getInt("PREFS_PORT", 9);

                if(radioButtonId == R.id.radioButtonStartAllFromHostname){
                    EntryPoj entryPoj = entryAdapter.getItem(position);
                    String ip = entryPoj.getIp_address();
                    String nicMac = entryPoj.getNic_mac();
                    AsyncTask<Object, Object, Object> send = new MagicPacket(ip, nicMac, port).execute();
                }else if(radioButtonId == R.id.radioButtonStartAllFromGroupname){
                    EntryPoj entryPoj = entryAdapter.getItem(position);
                    List<EntryPoj> listPoj = dbHelper.getAllEntriesByGroupName(entryPoj.getGroup_name());
                    for(int i = 0; i<listPoj.size(); i++) {
                        String ip = listPoj.get(position).getIp_address();
                        String nicMac = entryPoj.getNic_mac();
                        AsyncTask<Object, Object, Object> send = new MagicPacket(ip, nicMac, port).execute();
                    }
                }
            }
        });
    }

    private void getEntriesFromDb() {
        List<EntryPoj> listEntryPoj = dbHelper.getAllEntries();
        entryAdapter.addAll(listEntryPoj);
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

    public void delete(View view) {
        dbHelper.deleteAllEntries();
        entryAdapter.clear();
        getEntriesFromDb();
    }

    @Override
    public void onResume() {
        super.onResume();
        entryAdapter.clear();
        getEntriesFromDb();
    }
}