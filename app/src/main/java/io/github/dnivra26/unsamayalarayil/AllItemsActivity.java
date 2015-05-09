package io.github.dnivra26.unsamayalarayil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class AllItemsActivity extends Activity {

    ListView allItemsList;
    private RestAdapter restAdapter;
    TextView noItemsHelperMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);
        allItemsList = (ListView) findViewById(R.id.allItemsList);
        noItemsHelperMessage = (TextView) findViewById(R.id.noItemsMessage);
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(RegisterDeviceFragment.BASE_URL)
                .build();
        RetrofitInterface apiService =
                restAdapter.create(RetrofitInterface.class);
        apiService.getAllItems(new ListMessage(getUserId()), new Callback<ListResponse>() {
            @Override
            public void success(ListResponse listResponse, Response response) {
                AllItemsAdapter allItemsAdapter = new AllItemsAdapter(AllItemsActivity.this, listResponse.items);
                allItemsList.setAdapter(allItemsAdapter);
                allItemsAdapter.notifyDataSetChanged();
                if(listResponse.items.size() == 0){
                    noItemsHelperMessage.setVisibility(View.VISIBLE);
                }else{
                    noItemsHelperMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(AllItemsActivity.this,"Fetch failed",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_items, menu);
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

    private String getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences(RegisterDeviceFragment.class.getSimpleName(),
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(RegisterDeviceFragment.USER_ID, "");

    }
}
