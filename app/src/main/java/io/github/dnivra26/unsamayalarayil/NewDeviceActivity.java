package io.github.dnivra26.unsamayalarayil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class NewDeviceActivity extends Activity {
    private static final String TAG = "location";
    private static final int PICK_CONTACT = 321;

    Button submitButton;
    EditText name;
    EditText phoneNumber;
    String deviceIdFromIntent;
    Spinner reminderActionsSpinner;
    LinearLayout locationLayout;
    TextView latLng;
    ImageButton pickLocation;
    Spinner levelSpinner;
    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);
        deviceIdFromIntent = getIntent().getStringExtra(GcmIntentService.device_name);
        name = (EditText) findViewById(R.id.deviceName);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        levelSpinner = (Spinner) findViewById(R.id.levels_spinner);
        submitButton = (Button) findViewById(R.id.deviceSubmitButton);
        reminderActionsSpinner = (Spinner) findViewById(R.id.actions_spinner);
        locationLayout = (LinearLayout) findViewById(R.id.locationLayout);
        latLng = (TextView) findViewById(R.id.latLng);
        pickLocation = (ImageButton) findViewById(R.id.pickLocation);

        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(NewDeviceActivity.this, MapActivity.class), 111);
            }
        });

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(this,
                R.array.level_types, android.R.layout.simple_spinner_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(levelAdapter);

        reminderActionsSpinner.setAdapter(adapter);
        reminderActionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).toString().equals("SMS")) {
                    locationLayout.setVisibility(View.GONE);
                    phoneNumber.setVisibility(View.VISIBLE);
                    phoneNumber.requestFocus();
                } else if (adapterView.getItemAtPosition(i).toString().equals("location")) {
                    phoneNumber.setVisibility(View.GONE);
                    locationLayout.setVisibility(View.VISIBLE);
                } else {
                    phoneNumber.setVisibility(View.GONE);
                    locationLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lat = "", lng = "";
                String[] latlng = new String[]{
                        "123","345"
                };
                if(reminderActionsSpinner.getSelectedItem().toString().equals("location") && location!=null) {
                    //latlng = latLng.getText().toString().split(",");
                    lat = String.valueOf(location.getLatitude());
                    lng = String.valueOf(location.getLongitude());
                }
                saveAction(name.getText().toString(), reminderActionsSpinner.getSelectedItem().toString(),
                        phoneNumber.getText().toString(), lat, lng);

                NewDevice newDevice = new NewDevice(getUserId(), deviceIdFromIntent, name.getText().toString(),
                        levelMapper.get(levelSpinner.getSelectedItem().toString()));
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(RegisterDeviceFragment.BASE_URL)
                        .build();
                RetrofitInterface apiService =
                        restAdapter.create(RetrofitInterface.class);
                apiService.addItem(newDevice, new Callback<RegistrationResponse>() {
                    @Override
                    public void success(RegistrationResponse registrationResponse, Response response) {
                        Toast.makeText(NewDeviceActivity.this, "Item successfully added", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(NewDeviceActivity.this, AllItemsActivity.class));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(NewDeviceActivity.this, "Failed to add item", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }


    private void saveAction(String itemName, String action, String phoneNumber, String lattitude, String longitude) {

            SharedPreferences sharedPreferences = getSharedPreferences(RegisterDeviceFragment.class.getSimpleName(),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(itemName, new Gson().toJson(new ItemAction(itemName, action, phoneNumber, lattitude, longitude)));
            editor.commit();


    }


    private String getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences(RegisterDeviceFragment.class.getSimpleName(),
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(RegisterDeviceFragment.USER_ID, "");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_new_device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String cNumber;
        if (requestCode == 111) {
            if(data != null) {

                location = data.getParcelableExtra("location");
                String address = data.getStringExtra("address");
                latLng.setText("Location: "+address);
            }
        }

        if (requestCode == PICK_CONTACT) {
            Uri contactData = data.getData();
            Cursor c = managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {


                String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equalsIgnoreCase("1")) {
                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, null);
                    phones.moveToFirst();
                    cNumber = phones.getString(phones.getColumnIndex("data1"));
                    phoneNumber.setText(cNumber);
                }
            }
        }
    }

    public Map<String,Integer> levelMapper = new HashMap<String, Integer>(){{
        put("Half",50);
        put("Nearing empty",30);
        put("Almost empty", 10);
    }

    };
}
