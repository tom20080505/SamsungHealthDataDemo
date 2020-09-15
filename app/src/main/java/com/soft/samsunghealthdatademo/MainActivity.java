package com.soft.samsunghealthdatademo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
public static final String APP_TAG = "SimpleHealth";
private static  MainActivity mInstance = null;

private HealthDataStore mStore;
private HealthConnectionErrorResult mConnError;
private Set<HealthPermissionManager.PermissionKey> mKeySet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initControl();
    }



    void initView()
    {

    }

    void initControl()
    {
        samsungHealthInit();
    }

    void samsungHealthInit()
    {
        mInstance = this;
        mKeySet = new HashSet<HealthPermissionManager.PermissionKey>();
        mKeySet.add(new HealthPermissionManager.PermissionKey(
                            HealthConstants.StepCount.HEALTH_DATA_TYPE,
                            HealthPermissionManager.PermissionType.READ));
        mStore = new HealthDataStore(this, mConnectionListener);
        mStore.connectService();
    }

    private void showConnecttonFailureDialog(HealthConnectionErrorResult error)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        mConnError = error;
        String message = "Connection with Samsung Health is not available";

        if (mConnError.hasResolution())
        {
            switch (error.getErrorCode())
            {
                case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                    message = "Please install Samsung Health";
                    break;

                case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                    message = "Please upgrade Samsung Health";
                    break;

                case HealthConnectionErrorResult.PLATFORM_DISABLED:
                    message = "Please enable Samsung Health";
                    break;

                case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                    message = "Please agree with Samsung Health policy";
                    break;

                default:
                    message = "Please make Samsung Health available";
                    break;
            }
        }

        alert.setMessage(message)
             .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                     if (mConnError.hasResolution()) {
                            mConnError.resolve(mInstance);
                     }
                 }
             });

        if (error.hasResolution())
        {
            alert.setNegativeButton("Cancel", null);
        }

        alert.show();
    }

    private final HealthDataStore.ConnectionListener mConnectionListener =
            new HealthDataStore.ConnectionListener() {
                @Override
                public void onConnected() {
                    Log.w(APP_TAG, "Health data service is connected.");
                    HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);

                    try {

                    }
                    catch (Exception e)
                    {
                        Log.e(APP_TAG, e.getClass().getName() + " - " + e.getMessage());
                        Log.e(APP_TAG, "Permission setting fails.");

                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionFailed(HealthConnectionErrorResult error) {
                    Log.d(APP_TAG, "Health data service is not available.");
                    showConnecttonFailureDialog(error);

                }

                @Override
                public void onDisconnected() {
                    Log.d(APP_TAG, "Health data service is disconnected.");
                }
            };
}