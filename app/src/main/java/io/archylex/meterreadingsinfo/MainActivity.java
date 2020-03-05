package io.archylex.meterreadingsinfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.archylex.meterreadingsinfo.services.Krymenergo;
import io.archylex.meterreadingsinfo.services.Krymgazseti;
import io.archylex.meterreadingsinfo.services.VodaKryma;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton fab;
    private TextView tvHelp;
    private TextView tvInfo;
    private ListView serviceListView;
    private ListView infoListView;
    private DatabaseHelper db;
    private List<MeterService> msList = new ArrayList<>();
    private MeterServiceAdapter msAdapter;
    private List<InfoService> infoList = new ArrayList<>();
    private InfoServiceAdapter infoAdapter;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceDialog asd = new ServiceDialog(MainActivity.this, DialogView.CREATE, null, -1);
                if (asd.getValues() != null)
                    createService(asd.getValues());
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        tvInfo = findViewById(R.id.infoTextView);
        tvInfo.setText(getString(R.string.empty));

        tvHelp = findViewById(R.id.helpTextView);
        tvHelp.setText(Html.fromHtml(readHelpHtml()));
        tvHelp.setMovementMethod(new ScrollingMovementMethod());
        tvHelp.setVisibility(View.GONE);

        db = new DatabaseHelper(this);

        msList.addAll(db.getMeterServices());

        msAdapter = new MeterServiceAdapter(this, msList);

        serviceListView = (ListView) findViewById(R.id.serviceListView);
        serviceListView.setAdapter(msAdapter);
        serviceListView.setLongClickable(true);
        serviceListView.setVisibility(View.GONE);

        serviceListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showActionsDialog(i);
                return true;
            }
        });

        infoAdapter = new InfoServiceAdapter(this, infoList);

        infoListView = (ListView) findViewById(R.id.infoListView);
        infoListView.setAdapter(infoAdapter);
        infoListView.setLongClickable(true);

        infoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ServiceDialog sd = new ServiceDialog(MainActivity.this, DialogView.READING, msList.get(i), i);
                if (sd.getMeterReading() != null) {
                    if (isNetworkAvailable()) {
                        switch (msList.get(i).getOrganization()) {
                            case "ГУП РК «Крымэнерго»":
                                Krymenergo ke = new Krymenergo(msList.get(i).getLogin(), msList.get(i).getPassword());
                                if (ke.submitReading(sd.getMeterReading())) {
                                    Toast.makeText(MainActivity.this, R.string.ok_sent_reading, Toast.LENGTH_LONG).show();
                                    updateInfoList();
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.error_send_reading, Toast.LENGTH_LONG).show();
                                }
                                break;
                            case "ГУП РК «Крымгазсети»":
                                Krymgazseti kgs = new Krymgazseti(msList.get(i).getLogin(), msList.get(i).getPassword());
                                if (kgs.meterReading(sd.getMeterReading())) {
                                    Toast.makeText(MainActivity.this, R.string.ok_sent_reading, Toast.LENGTH_LONG).show();
                                    updateInfoList();
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.error_send_reading, Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                    } else {
                        Toast.makeText(MainActivity.this, R.string.error_send_reading_offline, Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateInfoList();
    }

    private String readHelpHtml() {
        String htmlFilename = "help.html";
        AssetManager mgr = getBaseContext().getAssets();
        String result = null;

        try {
            InputStream in = mgr.open(htmlFilename, AssetManager.ACCESS_BUFFER);
            Scanner s = new Scanner(in).useDelimiter("\\A");
            result = s.hasNext() ? s.next() : "";
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void updateInfoList() {
        if (db.getServicesCount() > 0) {
            msAdapter.notifyDataSetChanged();
            db.setOfflineInfo();
        }

        if (isNetworkAvailable()) {
            Toast.makeText(this, R.string.internet_ok, Toast.LENGTH_LONG).show();
            updateDBbyInternet();
        } else {
            Toast.makeText(this, R.string.internet_err, Toast.LENGTH_LONG).show();
        }

        if (db.getInfoCount() > 0) {
            infoList.clear();
            infoList.addAll(db.getInfoServices());
            infoAdapter.notifyDataSetChanged();
            tvInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_info) {
            updateInfoList();
            fab.setVisibility(View.GONE);
            serviceListView.setVisibility(View.GONE);
            tvHelp.setVisibility(View.GONE);
            infoListView.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_services) {
            infoListView.setVisibility(View.GONE);
            serviceListView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            tvHelp.setVisibility(View.GONE);
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {
            infoListView.setVisibility(View.GONE);
            serviceListView.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            tvHelp.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_exit) {
            finish();
            System.exit(0);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void createService(MeterService ms) {
        long id = db.insertService(ms);

        MeterService service = db.getMeterService(id);

        if (service != null) {
            msList.add(service);
            msAdapter.notifyDataSetChanged();
            toggleEmptyList();
        }
    }

    private void updateService(MeterService ms, int pos) {
        db.updateService(ms);

        msList.set(pos, ms);
        msAdapter.notifyDataSetChanged();

        toggleEmptyList();
    }

    private void deleteService(int pos) {
        db.deleteService(msList.get(pos));
        db.deleteInfo(msList.get(pos));

        msList.remove(pos);
        msAdapter.notifyDataSetChanged();

        toggleEmptyList();
    }

    private void toggleEmptyList() {
        if (db.getServicesCount() > 0) {
            tvInfo.setVisibility(View.GONE);
        } else {
            tvInfo.setVisibility(View.VISIBLE);
            tvInfo.setText(getString(R.string.empty));
        }
    }

    private void updateDBbyInternet() {
        for (MeterService ms : msList) {
            if (Integer.valueOf(ms.getEnabled()).equals(1)) {
                InfoService is = new InfoService();
                is.setServiceId(ms.getId());

                switch (ms.getOrganization()) {
                    case "ГУП РК «Вода Крыма»":
                        VodaKryma vk = new VodaKryma(ms.getLogin(), ms.getPassword());
                        if (!vk.getSubscriberNumber().equalsIgnoreCase("")) {
                            is.setOrganization(vk.getOrganization());
                            is.setSubscriberName("");
                            is.setSubscriberAddress(vk.getAddress());
                            is.setSubscriberNumber(vk.getSubscriberNumber());
                            is.setSaldo(vk.getSaldo().toString());
                        }
                        is.setImageId(0);
                        break;
                    case "ГУП РК «Крымгазсети»":
                        Krymgazseti kgs = new Krymgazseti(ms.getLogin(), ms.getPassword());
                        if (!kgs.getSubscriberNumber().equalsIgnoreCase("")) {
                            is.setOrganization(kgs.getOrganization());
                            is.setSubscriberName(kgs.getOwner());
                            is.setSubscriberAddress(kgs.getAddress());
                            is.setSubscriberNumber(kgs.getSubscriberNumber());
                            is.setSaldo(kgs.getBill().toString());
                            is.setLastReading(kgs.getRecentMeterReading().toString());
                            is.setDate(kgs.getLastReadingDate());
                        }
                        is.setImageId(1);
                        break;
                    case "ГУП РК «Крымэнерго»":
                        Krymenergo ke = new Krymenergo(ms.getLogin(), ms.getPassword());
                        if (!ke.getSubscriber().equalsIgnoreCase("")) {
                            is.setOrganization(ke.getOrganization());
                            is.setSubscriberName(ke.getOwner());
                            is.setSubscriberAddress(ke.getAddress());
                            is.setSubscriberNumber(ke.getSubscriber());
                            is.setSaldo(ke.getSaldo());
                            is.setLastReading(ke.getLastReading());
                            is.setDate(ke.getReadingDate());
                        }
                        is.setImageId(2);
                        break;
                }

                if (is.getSubscriberNumber() != null) {
                    is.setOnline(true);

                    if (db.isInfoExists(is.getServiceId()))
                        db.updateInfo(is);
                    else
                        db.insertInfo(is);
                }
            }
        }
    }


    // Action Dialog
    private void showActionsDialog(final int position) {
        CharSequence options[] = new CharSequence[]{getString(R.string.action_edit), getString(R.string.action_delete)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.action_title));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    ServiceDialog sd = new ServiceDialog(MainActivity.this,DialogView.UPDATE, msList.get(position), position);
                    if (sd.getValues() != null)
                        updateService(sd.getValues(), position);
                } else {
                    deleteService(position);
                }
            }
        });
        builder.show();
    }
}
