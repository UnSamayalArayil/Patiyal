package io.github.dnivra26.unsamayalarayil;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class NewDeviceActivity extends Activity {

    Button submitButton;
    EditText name;
    EditText alertPercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);
        name = (EditText) findViewById(R.id.deviceName);
        alertPercentage = (EditText) findViewById(R.id.alertPercentage);
        submitButton = (Button) findViewById(R.id.deviceSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewDevice newDevice = new NewDevice(name.getText().toString(), Integer.parseInt(alertPercentage.getText().toString()));
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(RegisterDeviceFragment.BASE_URL)
                        .build();
                RetrofitInterface apiService =
                        restAdapter.create(RetrofitInterface.class);
                apiService.addItem(newDevice, new Callback<RegistrationResponse>() {
                    @Override
                    public void success(RegistrationResponse registrationResponse, Response response) {
                        Toast.makeText(NewDeviceActivity.this,"Item successfully added",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(NewDeviceActivity.this,"Failed to add item",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
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
