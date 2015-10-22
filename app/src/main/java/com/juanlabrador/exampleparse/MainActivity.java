package com.juanlabrador.exampleparse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private Button btnNew;
    private ListView mList;
    private CustomAdapter mAdapter;


    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNew = (Button) findViewById(R.id.btnNew);
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, NewActivity.class), 2);
            }
        });


        mList = (ListView) findViewById(R.id.listPlayer);

        // ListView Item Click Listener
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item value
                Player player = (Player) mList.getItemAtPosition(position);

                Intent i = new Intent(MainActivity.this, DetailActivity.class);
                i.putExtra("ID", player.getUUID());
                startActivityForResult(i, 1);

            }

        });

        mAdapter = new CustomAdapter(this);
        mList.setAdapter(mAdapter);

        findLocalData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (isNetworkAvailable())
                syncData();
            return true;
        }
        if (id == R.id.action_logout) {
            if (isNetworkAvailable()) {
                ParseUser.logOut();
                currentUser = ParseUser.getCurrentUser();
                ParseObject.unpinAllInBackground("players");
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    /**
     * Cuando entra la primera vez y me carga los datos desde la nube
     */
    private void findCloudData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        ParseQuery<Player> query = ParseQuery.getQuery(Player.class);
        query.orderByAscending("name");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Player>() {
            @Override
            public void done(List<Player> objects, ParseException e) {
                Log.i(TAG, "Size from cloud: " + objects.size());
                progressDialog.dismiss();
                if (!objects.isEmpty()) {
                    if (e == null) {
                        //for (Player o : objects) {
                        //    o.pinInBackground();
                        //}
                        ParseObject.pinAllInBackground("players", objects);
                        mAdapter.addAll(objects);
                        mAdapter.notifyDataSetChanged();

                        Log.i(TAG, "Download Finish!");
                    } else {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    /**
     * Cuando entra la primera vez y me carga los datos desde la nube
     */
    private void findLocalData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        ParseQuery<Player> query = Player.getQuery();
        query.fromLocalDatastore();
        query.orderByAscending("name");
        query.findInBackground(new FindCallback<Player>() {
            @Override
            public void done(List<Player> objects, ParseException e) {
                Log.i(TAG, "Size from Local: " + objects.size());
                progressDialog.dismiss();
                if (!objects.isEmpty()) {
                    if (e == null) {
                        mAdapter.addAll(objects);
                        mAdapter.notifyDataSetChanged();

                        Log.i(TAG, "Get from Local Database finish!");
                    } else {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (isNetworkAvailable()) {
                        findCloudData();
                        Log.i(TAG, "Cloud sync!");
                    }
                }
            }
        });
    }

    /**
     * Cuando quiero que los datos locales se sincronizen con la nube
     */
    private void syncData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        ParseQuery<Player> query = ParseQuery.getQuery(Player.class);
        query.fromLocalDatastore();
        query.orderByAscending("name");
        query.findInBackground(new FindCallback<Player>() {
            @Override
            public void done(List<Player> objects, ParseException e) {
                if (e == null) {
                    for (Player o : objects) {
                        o.saveEventually();
                        o.unpinInBackground();
                    }
                    findCloudData();
                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            findLocalData();
            Log.i(TAG, "Local sync!");
        }
    }
}
