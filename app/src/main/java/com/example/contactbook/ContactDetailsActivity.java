package com.example.contactbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.URI;

import static com.example.contactbook.ListContactsActivity.authCode;

public class ContactDetailsActivity extends AppCompatActivity {

    private final static int RESULT_LOAD_IMAGE = 101;

    private ImageView avatar;
    private EditText name, lastName, email, number;
    private long _id = -1;
    private ContactDataSource datasource;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        avatar = (ImageView) findViewById(R.id.avatar_chooser);
        name = (EditText) findViewById(R.id.name_chooser);
        lastName = (EditText) findViewById(R.id.last_name_chooser);
        email = (EditText) findViewById(R.id.email_chooser);
        number = (EditText) findViewById(R.id.number_chooser);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Редактирование");

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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
           /* Bitmap bitmap;
            if((bitmap = DbBitmapUtility.getImage(bundle.getByteArray("image"))) != null) {
                avatar.setImageBitmap(bitmap);
            }*/
           avatar.setImageURI(Uri.parse(bundle.getString("image")));
            name.setText(bundle.getString("name", ""));
            lastName.setText(bundle.getString("last_name", ""));
            number.setText(bundle.getString("number", ""));
            email.setText(bundle.getString("email", ""));
            _id = bundle.getLong("_id");
        }
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            avatar.setImageURI(selectedImage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.done:
                datasource = new ContactDataSource(getApplicationContext(), authCode);
                datasource.open();
                if (_id < 0) {
                    datasource.createContact(name.getText().toString(), lastName.getText().toString(),
                            number.getText().toString(), email.getText().toString(), selectedImage.toString());
                            //DbBitmapUtility.getBytes(((BitmapDrawable) avatar.getDrawable()).getBitmap()));
                } else {
                    datasource.updateContact(name.getText().toString(), lastName.getText().toString(),
                            number.getText().toString(), email.getText().toString(), selectedImage.toString(), _id);
                            //DbBitmapUtility.getBytes(((BitmapDrawable) avatar.getDrawable()).getBitmap()), _id);
                }
                Intent intent = new Intent();
                intent.putExtra("name", name.getText().toString());
                intent.putExtra("last_name", lastName.getText().toString());
                intent.putExtra("number", number.getText().toString());
                intent.putExtra("email", email.getText().toString());
                intent.putExtra("_id", _id);
                intent.putExtra("image", selectedImage.toString());
                //intent.putExtra("image", DbBitmapUtility.getBytes(((BitmapDrawable) avatar.getDrawable()).getBitmap()));
                setResult(RESULT_OK, intent);

                finish();
                return true;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (datasource != null)
            datasource.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
