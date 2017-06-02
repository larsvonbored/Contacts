package com.example.contactbook;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.contactbook.ListContactsActivity.authCode;

public class ViewContactActivity extends AppCompatActivity {

    private static final int RESULT_CONTACT = 102;
    private ImageView avatar;
    private TextView name, lastName, number, email;
    private long _id = 0;
    private Uri selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        avatar = (ImageView) findViewById(R.id.avatar_choosen);
        name = (TextView) findViewById(R.id.name_text);
        lastName = (TextView) findViewById(R.id.last_name_text);
        email = (TextView) findViewById(R.id.email_text);
        number = (TextView) findViewById(R.id.number_text);

        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parse = "tel:" + number.getText().toString();
                Uri number = Uri.parse(parse);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent( android.content.Intent.ACTION_SEND);

                emailIntent.setType("plain/text");

                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                        new String[] { email.getText().toString() });

                startActivity(Intent.createChooser(
                        emailIntent, "Send mail..."));
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Просмотр");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editContact();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if((selectedImage = Uri.parse(bundle.getString("image"))) == null) {
                selectedImage = DbBitmapUtility.resourceToUri(getApplicationContext(), R.drawable.account);
            }
            selectedImage = Uri.parse(bundle.getString("image"));
            avatar.setImageURI(selectedImage);
            name.setText(bundle.getString("name", ""));
            lastName.setText(bundle.getString("last_name", ""));
            number.setText(bundle.getString("number", ""));
            email.setText(bundle.getString("email", ""));
            _id = bundle.getLong("_id");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.edit_contact:
                editContact();
                return true;
            case R.id.delete_contact:
                deleteContact();
                return true;
        }
        return true;
    }

    public void editContact() {
        Intent intent = new Intent(getApplicationContext(), ContactDetailsActivity.class);
        //intent.putExtra("image", DbBitmapUtility.getBytes(((BitmapDrawable) avatar.getDrawable()).getBitmap()));
        intent.putExtra("image", selectedImage.toString());
        intent.putExtra("name", name.getText().toString());
        intent.putExtra("last_name", lastName.getText().toString());
        intent.putExtra("number", number.getText().toString());
        intent.putExtra("email", email.getText().toString());
        intent.putExtra("_id", _id);
        startActivityForResult(intent, RESULT_CONTACT);
    }

    public void deleteContact() {
        ContactDataSource datasource = new ContactDataSource(getApplicationContext(), authCode);
        datasource.open();
        datasource.deleteContact(_id);
        datasource.close();
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CONTACT && resultCode == RESULT_OK && null != data) {
            Bundle bundle = data.getExtras();
            /*Bitmap bitmap;
            if((bitmap = DbBitmapUtility.getImage(bundle.getByteArray("image"))) != null) {
                avatar.setImageBitmap(bitmap);
                }*/
            selectedImage = Uri.parse(bundle.getString("image"));
            avatar.setImageURI(selectedImage);
            name.setText(bundle.getString("name", ""));
            lastName.setText(bundle.getString("last_name", ""));
            number.setText(bundle.getString("number", ""));
            email.setText(bundle.getString("email", ""));
            _id = bundle.getLong("_id");
        }
    }
}
