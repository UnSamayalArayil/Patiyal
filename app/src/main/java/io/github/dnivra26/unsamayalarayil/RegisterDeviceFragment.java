package io.github.dnivra26.unsamayalarayil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RegisterDeviceFragment extends Fragment {

    String SENDER_ID = "419810287584";
    GoogleCloudMessaging gcm;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String BASE_URL = "http://demo1047698.mockable.io";



    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = RegisterDeviceFragment.class.getSimpleName();
    Button registerButton;
    Button button;
    String regid;
    private RestAdapter restAdapter;

    public static RegisterDeviceFragment newInstance() {
        RegisterDeviceFragment fragment = new RegisterDeviceFragment();
        return fragment;
    }

    public RegisterDeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register_device, container, false);
        registerButton = (Button) rootView.findViewById(R.id.registerButton);
        button = (Button) rootView.findViewById(R.id.button);
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .build();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (checkPlayServices()) {
            Toast.makeText(getActivity(), "Google play services available",Toast.LENGTH_SHORT).show();
            gcm = GoogleCloudMessaging.getInstance(getActivity());
            registerButton.setEnabled(true);
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regid = getRegistrationId(getActivity());
                if (regid.isEmpty()) {
                    registerInBackground();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRegistrationIdToBackend("1234");
            }
        });
    }

    private void sendGetRequest() {
        RetrofitInterface apiService =
                restAdapter.create(RetrofitInterface.class);
        apiService.getName(new Callback<RegistrationMessage>() {
            @Override
            public void success(RegistrationMessage registrationMessage, Response response) {
                Toast.makeText(getActivity(), "success: "+registrationMessage.msg,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "failure",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerInBackground() {
        new AsyncTask<Void,String,String>(){


            @Override
            protected String doInBackground(Void... voids) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getActivity());
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    sendRegistrationIdToBackend(regid);

                    storeRegistrationId(getActivity(), regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getActivity(), "Device successfully registered: "+msg,Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void sendRegistrationIdToBackend(String regid) {
        RetrofitInterface apiService =
                restAdapter.create(RetrofitInterface.class);
        apiService.registerDevice(regid, new Callback<RegistrationMessage>() {
            @Override
            public void success(RegistrationMessage registrationMessage, Response response) {
                Toast.makeText(getActivity(), "success post: "+registrationMessage.msg,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "failure post: ",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                Toast.makeText(getActivity(), "Your device is not supported",Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getActivity().getSharedPreferences(RegisterDeviceFragment.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

}
