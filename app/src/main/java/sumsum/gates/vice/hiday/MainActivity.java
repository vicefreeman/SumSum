package sumsum.gates.vice.hiday;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import sumsum.gates.vice.hiday.Modules.Gate;
import sumsum.gates.vice.hiday.geofence.GeofenceErrorMessages;
import sumsum.gates.vice.hiday.geofence.GeofenceTransitionsIntentService;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnCompleteListener<Void>{


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    Activity activity;
    private enum PendingGeofenceTask {ADD, REMOVE, NONE}
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.ADD;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String uid;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref  = getSharedPreferences("shred" ,Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity = this;


        getSupportActionBar().setIcon(R.drawable.barlogo);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                checkCurrentUser();
            }
        };


    }

    public void moveToContact(MenuItem item) {
        getSupportFragmentManager().
                beginTransaction().
                replace(sumsum.gates.vice.hiday.R.id.mainContainer, new ContactUsFragment()).
                commit();

    }

    public void showSplash(MenuItem item) {
        SharedPreferences preferences = getSharedPreferences("shred" , Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean("show" , true).commit();
        Intent intent = new Intent(this , IntroActivity.class);
        startActivity(intent);
    }

    public void showOurInfo(MenuItem item) {
        getSupportFragmentManager().
                beginTransaction().
                replace(sumsum.gates.vice.hiday.R.id.mainContainer, new AboutUsFragment()).
                commit();

    }

    public void returnToPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }



    private void checkCurrentUser() {
        if (FirebaseAuth.getInstance().equals(null) || FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            //___________________Geofence_____________________
            initWithUser();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        addGeofences();
    }

    private void initWithUser() {

        // Empty list for storing geofences.
        mGeofenceList = populateGeofenceList();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;


//        // Get the geofences used. Geofence data is hard coded in this sample.
//        populateGeofenceList();

        mGeofencingClient = LocationServices.getGeofencingClient(this);

        checkIfUserHaveGatesList();

        //Add Main gates to the DB

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Gates");
//        Gate g = new Gate(32.1932321,34.9529492 ,200, "0543063923", "NirEliyahu East Gate", "NirEliyahu East Gate");
//        Gate g2 = new Gate(32.1957065,34.9473632 ,200, "0543063921", "NirEliyahu West Gate", "NirEliyahu West Gate");
//        Gate g3 = new Gate(32.0844269,34.8029073 ,1200, "0505978532", "HackerUוו", "HackerU test");
//        Gate g4 = new Gate(32.2956391, 34.8762752 ,200, "0543532447", "Home", "Home Test");
//        Gate g5 = new Gate(32.149635, 35.108194 ,200, "0543582460", "Nofim Main Gate", "Nofim Main Gate");
//
//        myRef.child(g.getName()).setValue(g);
//        myRef.child(g2.getName()).setValue(g2);
//        myRef.child(g3.getName()).setValue(g3);
//        myRef.child(g4.getName()).setValue(g4);
//        myRef.child(g5.getName()).setValue(g5);

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }





    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }


    private void addGeofences() {
       if (!checkPermissions()) {
            requestPermissions();
        }else if (!checkCallPermission()){
            requestCallPermission();
        }
        else {
            mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent()).addOnCompleteListener(this);
//            Toast.makeText(this, "Geofences added", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeGeofences() {
            mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }


    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.ADD;
        if (task.isSuccessful()) {
            updateGeofencesAdded(true);

            int messageId;
            if (getGeofencesAdded()) messageId = R.string.geofences_added;
            else messageId = R.string.geofences_removed;
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        intent.putExtra("uid", uid);
        //intent.putExtra("number", )
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    private ArrayList populateGeofenceList() {
        final ArrayList<Geofence> geofensList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("UserGatesList")
                .child(mAuth.getCurrentUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Double lang = Double.valueOf(postSnapshot.child("lang").getValue().toString());
                    Double lat = Double.valueOf(postSnapshot.child("lat").getValue().toString());
                    String phoneNumber = postSnapshot.child("phone").getValue().toString();
                    Float distance = Float.valueOf(postSnapshot.child("distance").getValue().toString());
                    Geofence geofence = new Geofence.Builder().setRequestId(postSnapshot.child("name").getValue().toString())
                            .setCircularRegion(lat, lang, distance)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build();
                    geofensList.add(geofence);

                    editor.putString(postSnapshot.child("name").getValue().toString().toLowerCase(), phoneNumber);

                    editor.apply();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return geofensList;
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Returns true if geofences were added, otherwise false.
     */
    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false);
    }

    /**
     * Stores whether geofences were added ore removed in {@link SharedPreferences};
     *
     * @param added Whether geofences were added or removed.
     */
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.
     */
    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
            Toast.makeText(this, "Got the fence from Pending Intent", Toast.LENGTH_SHORT).show();
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        //int callPermissionState = ActivityCompat.checkSelfPermission(this , Manifest.permission.CALL_PHONE);

        if (permissionState == PackageManager.PERMISSION_GRANTED)

        return true;

        else return false;


    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private boolean checkCallPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            return false;
        }
        else return true;

    }

    private void requestCallPermission(){

        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.CALL_PHONE}, 1
        );

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
                performPendingGeofenceTask();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                mPendingGeofenceTask = PendingGeofenceTask.NONE;
            }
        }
    }

    public void checkIfUserHaveGatesList(){

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                DatabaseReference userId = FirebaseDatabase.getInstance().getReference("UserGatesList").child(uid);
                userId.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {

                            FragmentTransaction replace = getSupportFragmentManager().
                                    beginTransaction().
                                    replace(R.id.topContainer, new GreetingFragment()).
                                    replace(R.id.bottomContainer, new UserGateListFragment());
                            addGeofences();
                            replace.commit();

                        }else {

                            getSupportFragmentManager().
                                    beginTransaction().
                                    replace(R.id.topContainer, new GreetingFragment()).
                                    replace(R.id.bottomContainer, new NoGateFragment()).
                                    commit();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }
}
