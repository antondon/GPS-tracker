package com.antondon.gpstracker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private GoogleApiClient googleApiClient;
    private static final int INTERVAL = 1000;
    private static final int FASTEST_INTERVAL = 1000;
    private static final int MAX_ACCURACY = 15;
    private static final int POSITION_OFFSET = 5;
    private static final float scaleLatitude = 110574.61f;
    private static final float scaleLongitude = 111302.62f;

    private boolean requestingLocationUpdate = false;
    private Location previousLocation = null;
    private GraphView graphView;
    private Button btnStartStop;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graphView = (GraphView) findViewById(R.id.graphView);
        btnStartStop = (Button) findViewById(R.id.btnStartStop);
        btnStartStop.setOnClickListener(this);
        findViewById(R.id.btnClear).setOnClickListener(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (requestingLocationUpdate) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (previousLocation == null) {
            previousLocation = location;
            return;
        }
        float[] results = new float[3];
        Location.distanceBetween(previousLocation.getLatitude(), previousLocation.getLongitude(),
                location.getLatitude(), location.getLongitude(), results);
        float distance = results[0];
        if (distance > POSITION_OFFSET && location.getAccuracy() < MAX_ACCURACY) {
            graphView.setCoordinate(location.getLatitude() * scaleLatitude, location.getLongitude() * scaleLongitude);
            previousLocation = location;
        } else if (location.getAccuracy() > MAX_ACCURACY) {
            Toast.makeText(this, "Warning, low accuracy!", Toast.LENGTH_LONG).show();
        }
    }

    private Location createLocation(double latitude, double longtitude) {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longtitude);
        return location;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                googleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }


    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartStop:
                //start tracking
                if (!requestingLocationUpdate) {
                    requestingLocationUpdate = true;
                    startLocationUpdates();
                    btnStartStop.setText(getString(R.string.stop_tracking));
                }
                //stop tracking
                else {
                    requestingLocationUpdate = false;
                    stopLocationUpdates();
                    btnStartStop.setText(getString(R.string.start_tracking));
                }
                break;
            case R.id.btnClear:
                graphView.clear();
                break;
        }
    }

    private void addLocations() {
        Location location0 = createLocation(50.430293, 30.522567);
        Location location1 = createLocation(50.430474, 30.523353);
        Location location2 = createLocation(50.430204, 30.523181);
        Location location3 = createLocation(50.430267, 30.522728);
        Location location4 = createLocation(50.430110, 30.522672);

        graphView.setCoordinate((float) (location0.getLatitude() * scaleLatitude),
                (float) (location0.getLongitude() * scaleLongitude));
        graphView.setCoordinate((float) (location1.getLatitude() * scaleLatitude),
                (float) (location1.getLongitude() * scaleLongitude));
        graphView.setCoordinate((float) (location2.getLatitude() * scaleLatitude),
                (float) (location2.getLongitude() * scaleLongitude));
        graphView.setCoordinate((float) (location3.getLatitude() * scaleLatitude),
                (float) (location3.getLongitude() * scaleLongitude));
        graphView.setCoordinate((float) (location4.getLatitude() * scaleLatitude),
                (float) (location4.getLongitude() * scaleLongitude));
    }


    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }
}




