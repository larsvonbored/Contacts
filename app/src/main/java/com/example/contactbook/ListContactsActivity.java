package com.example.contactbook;

import java.util.Collections;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class ListContactsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final int RESULT_CONTACT = 100;

    private ContactDataSource datasource;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> values;

    private GoogleApiClient mGoogleApiClient;
    public static String authCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        TextView textView = (TextView) findViewById(R.id.noContacts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.contacts);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createContact();
            }
        });

        authCode = getIntent().getStringExtra("authCode");
        datasource = new ContactDataSource(this, authCode);
        datasource.open();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        values = datasource.getAllContacts();
        Collections.sort(values);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (values.isEmpty()) {
            textView.setText(R.string.no_contacts_yet);
            recyclerView.setVisibility(View.GONE);
        } else {
            contactAdapter = new ContactAdapter(values);
            recyclerView.setAdapter(contactAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.add_contact:
                createContact();
                return true;
            case R.id.action_logout:
                signOut();
                finish();
        }
        return true;
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        datasource.open();

        values = datasource.getAllContacts();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setVisibility(View.VISIBLE);
        contactAdapter = new ContactAdapter(values);
        recyclerView.setAdapter(contactAdapter);
        contactAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    public void createContact() {
        Intent intent = new Intent(getApplicationContext(), ContactDetailsActivity.class);
        startActivityForResult(intent, RESULT_CONTACT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_DENIED ) {
            Toast.makeText(this, R.string.you_need_this_permission, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            TextView textView = (TextView) findViewById(R.id.noContacts);
            datasource = new ContactDataSource(this, authCode);
            datasource.open();

            List<Contact> values = datasource.getAllContacts();

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);

            contactAdapter = new ContactAdapter(values);
            recyclerView.setAdapter(contactAdapter);

            contactAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
