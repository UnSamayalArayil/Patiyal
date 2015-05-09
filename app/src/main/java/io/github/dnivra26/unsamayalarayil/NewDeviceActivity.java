package io.github.dnivra26.unsamayalarayil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class NewDeviceActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "location";

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connection failed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection failed");
    }

    Button submitButton;
    EditText name;
    EditText alertPercentage;
    EditText phoneNumber;
    String deviceIdFromIntent;
    TextView deviceId;
    Spinner reminderActionsSpinner;
    LinearLayout locationLayout;
    EditText lattitude;
    EditText longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);








        deviceIdFromIntent = getIntent().getStringExtra(GcmIntentService.device_name);
        deviceId = (TextView) findViewById(R.id.deviceId);
        deviceId.append(" "+ deviceIdFromIntent);
        name = (EditText) findViewById(R.id.deviceName);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        alertPercentage = (EditText) findViewById(R.id.alertPercentage);
        submitButton = (Button) findViewById(R.id.deviceSubmitButton);
        reminderActionsSpinner = (Spinner) findViewById(R.id.actions_spinner);
        locationLayout = (LinearLayout) findViewById(R.id.locationLayout);
        lattitude = (EditText) findViewById(R.id.lattitude);
        longitude = (EditText) findViewById(R.id.longitude);


        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderActionsSpinner.setAdapter(adapter);
        reminderActionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getItemAtPosition(i).toString().equals("SMS")){
                    locationLayout.setVisibility(View.GONE);
                    phoneNumber.setVisibility(View.VISIBLE);
                    phoneNumber.requestFocus();
                }
                else if(adapterView.getItemAtPosition(i).toString().equals("location")){
                    phoneNumber.setVisibility(View.GONE);
                    locationLayout.setVisibility(View.VISIBLE);
                    lattitude.requestFocus();
                }
                else{
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
                GeoFencingService geoFencingService = new GeoFencingService(NewDeviceActivity.this);
                geoFencingService.addLocationReminder("Orange",12.986258, 80.245402);
//                saveAction(name.getText().toString(), reminderActionsSpinner.getSelectedItem().toString(),
//                        phoneNumber.getText().toString(), lattitude.getText().toString(), longitude.getText().toString());
//
//                NewDevice newDevice = new NewDevice(getUserId(),deviceIdFromIntent, name.getText().toString(), Integer.parseInt(alertPercentage.getText().toString()));
//                RestAdapter restAdapter = new RestAdapter.Builder()
//                        .setEndpoint(RegisterDeviceFragment.BASE_URL)
//                        .build();
//                RetrofitInterface apiService =
//                        restAdapter.create(RetrofitInterface.class);
//                apiService.addItem(newDevice, new Callback<RegistrationResponse>() {
//                    @Override
//                    public void success(RegistrationResponse registrationResponse, Response response) {
//                        Toast.makeText(NewDeviceActivity.this, "Item successfully added", Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(NewDeviceActivity.this,AllItemsActivity.class));
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        Toast.makeText(NewDeviceActivity.this, "Failed to add item", Toast.LENGTH_LONG).show();
//                    }
//                });

            }
        });
    }

    private void sendMessage(String item, String phoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, "Please send some amount of " + item, null, null);

    }

    private void saveAction(String itemName, String action, String phoneNumber, String lattitude, String longitude) {
        try {
            DB snappydb = DBFactory.open(this);
            snappydb.put(itemName, new ItemAction(itemName, action, phoneNumber, lattitude, longitude));
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed");
    }
}
