package io.github.dnivra26.unsamayalarayil;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {
    protected ResultReceiver mReceiver;
    public FetchAddressIntentService() {
        super("FetchAddress");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);
        List<Address> addresses = null;
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
            String result = addresses.get(0).getFeatureName();
            if(result == null){
                result = addresses.get(0).getCountryCode();
            }
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    result);
        } catch (IOException ioException) {

        } catch (IllegalArgumentException illegalArgumentException) {

        }
    }

    private void deliverResultToReceiver(int successResult, String locality) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, locality);
        mReceiver.send(successResult, bundle);
    }
}
