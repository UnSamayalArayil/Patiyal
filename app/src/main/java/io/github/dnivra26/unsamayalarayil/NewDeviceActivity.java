package io.github.dnivra26.unsamayalarayil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class NewDeviceActivity extends Activity {

    Button submitButton;
    EditText name;
    EditText alertPercentage;
    String deviceIdFromIntent;
    TextView deviceId;
    Spinner reminderActionsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);
        deviceIdFromIntent = getIntent().getStringExtra(GcmIntentService.device_name);
        deviceId = (TextView) findViewById(R.id.deviceId);
        deviceId.append(" "+ deviceIdFromIntent);
        name = (EditText) findViewById(R.id.deviceName);
        alertPercentage = (EditText) findViewById(R.id.alertPercentage);
        submitButton = (Button) findViewById(R.id.deviceSubmitButton);
        reminderActionsSpinner = (Spinner) findViewById(R.id.actions_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderActionsSpinner.setAdapter(adapter);
        reminderActionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewDevice newDevice = new NewDevice(getUserId(),deviceIdFromIntent, name.getText().toString(), Integer.parseInt(alertPercentage.getText().toString()));
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(RegisterDeviceFragment.BASE_URL)
                        .build();
                RetrofitInterface apiService =
                        restAdapter.create(RetrofitInterface.class);
                apiService.addItem(newDevice, new Callback<RegistrationResponse>() {
                    @Override
                    public void success(RegistrationResponse registrationResponse, Response response) {
                        Toast.makeText(NewDeviceActivity.this, "Item successfully added", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(NewDeviceActivity.this,AllItemsActivity.class));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(NewDeviceActivity.this, "Failed to add item", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
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
}
