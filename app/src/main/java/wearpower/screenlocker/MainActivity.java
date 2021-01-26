package wearpower.screenlocker;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//import com.transitionseverywhere.*;

public class MainActivity extends AppCompatActivity{

    final public static String TAG = "ScreenLockerMainActiviy";
    final private static boolean DBG = Definitions.DBG;

    private static Logger mLogger = new Logger();
    ServiceClassPhone myService;
    boolean mBound = false;

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1234;

    Random r;

    Button set_button;
    Button test_button;
    Button stop_button;
    LinearLayout linear_bg;
    TextView display_text;

    private boolean set_state = true; // true -> movement is recorded!

    int repeat_count = 4;
    String[] movements = new String[]{"INITIAL", "Square", "Oval", "U", "Infinite", "Triangle", "Z"};
    int movement_count = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG , "Getting all permissions");
            getAllPermissions();
        }

        set_button = findViewById(R.id.set_button);
        test_button = findViewById(R.id.test_button);
        stop_button = findViewById(R.id.stop_button);
        display_text = findViewById(R.id.dispaly_text);
        linear_bg = findViewById(R.id.linear_bg);

        Intent i=new Intent(MainActivity.this, ServiceClassPhone.class);
        startService(i); // it is needed since service should run after activity is destroyed.
        bindService(i, connection, Context.BIND_AUTO_CREATE);

        // it is unique id to add uploading file name



        set_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (set_state){ // set state is false
                    set_button.setText(" DONE! ");
                    set_button.setBackground(getDrawable(R.drawable.done_bg));
                    linear_bg.setBackgroundColor(Color.GREEN);
                    //myService.SETTING = 0;
                    DataHolder.getInstance().setSetCount(0);
                    Log.d(TAG, "RECORDING started");
                    set_state = false;
                }
                else{ // set_state = false meaning it is done!
                    set_button.setText("  GO!  ");
                    set_button.setBackground(getDrawable(R.drawable.ready_bg));
                    linear_bg.setBackgroundColor(Color.parseColor("#191970"));
                    //myService.SETTING = 2;
                    DataHolder.getInstance().setSetCount(2);
                    Log.d(TAG, "RECORDING done!");
                    set_state = true;
                }


            }
        });

        test_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              //  display_text.setText("Testing..." +
              //          "\nPlease do the same 3D movement to unlock the screen");
            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // display_text.setText("Stopping...");
                stopStudy();
            }
        });

        try{
            makeNotification();
        }catch (Exception e){

        }

    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getAllPermissions(){
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!Settings.canDrawOverlays(this))
            permissionsNeeded.add("System Alert");
        if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
            permissionsNeeded.add("Audio Record");
        if (!Settings.System.canWrite(this))
            permissionsNeeded.add("Write Settings");
        if (!addPermission(permissionsList, Manifest.permission.BODY_SENSORS))
            permissionsNeeded.add("Read Storage");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read External Storage");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write External Storage");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Access Fine Location");
        if (!addPermission(permissionsList, Manifest.permission.PACKAGE_USAGE_STATS))
            permissionsNeeded.add("Package Usage Stats");
       // if (!addPermission(permissionsList, Manifest.permission.DATA_USAGE))
       //     permissionsNeeded.add("Package Usage Stats");
        getDeviceUniqueId();

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.SYSTEM_ALERT_WINDOW, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.BODY_SENSORS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.PACKAGE_USAGE_STATS, PackageManager.PERMISSION_GRANTED);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    }
                }

                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    boolean granted = false;
                    AppOpsManager appOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
                    int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                            android.os.Process.myUid(), this.getPackageName());

                    if (mode == AppOpsManager.MODE_DEFAULT) {
                        granted = (this.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
                    } else {
                        granted = (mode == AppOpsManager.MODE_ALLOWED);
                    }
                    if (!granted) {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    }
                }



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(getApplicationContext())) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

                    }
                }

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (Settings.canDrawOverlays(this)
                        && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && Settings.System.canWrite(getApplicationContext())){
                    // All Permissions Granted
                    DataHolder.getInstance().setVoicePermission(true);

                }

            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean allPermissionsAreGranted(){
        Map<String, Integer> perms = new HashMap<String, Integer>();
        // Initial
        perms.put(Manifest.permission.SYSTEM_ALERT_WINDOW, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.BODY_SENSORS, PackageManager.PERMISSION_GRANTED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
            else{
                requestOverlayPermission();
                return false;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

            }
            else{
                requestSystemWritePermission();
                return false;
            }
        }
        if ( perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && perms.get(Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED){
            // All Permissions Granted
            DataHolder.getInstance().setVoicePermission(true);
            return true;
        }
        //getAllPermissions();
        return false;
    }

    private void getDeviceUniqueId(){
        try{
            //TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            //String uid = tManager.getDeviceId();
            SharedPreferences prefs;
            prefs=this.getSharedPreferences(Definitions.DEVICE_UNIQUE_ID, Context.MODE_PRIVATE);
            int unique_id = prefs.getInt(Definitions.DEVICE_ID, -1);
            if (unique_id==-1){
                r = new Random();
                int ran = r.nextInt(10000 - 1) + 1;
                prefs.edit().putInt(Definitions.DEVICE_ID , ran).commit();
                if (DBG) Log.d(TAG , "Device unique id is created: " + ran);
            }
        } catch (Exception e){
            if (DBG) Log.d(TAG , "Device unique id is not created: " + e);
        }


    }




    private void stopServiceIfActive(){
        if(ScreenService.STATE == ScreenService.ACTIVE){
            Intent i=new Intent(MainActivity.this, ScreenService.class);
            stopService(i);
        }
    }



    public void stopStudy(){

        Intent i=new Intent(MainActivity.this, ScreenService.class);
        stopService(i);

        Intent j=new Intent(MainActivity.this, ServiceClassPhone.class);
        stopService(j);

        finish();
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // allPermissionsAreGranted();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission();
            requestSystemWritePermission();
            getAllPermissions();
            return false;
        }



        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // explain what this application about!
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("About");

            final TextView empty = new TextView(MainActivity.this);

            // Set up the input

            final TextView timePlace = new TextView(MainActivity.this);
            timePlace.setText("This application is developed for research purposes by NU EECS Department Micro Architecture Lab. " +
                    "Research aims to detect optimal screen features on Android smartphones. " +
                    "Please give all requested permission and try to give input when requested. Thank you very much for your support."
                    + " (for any suggestion or report please email emirhan@u.northwestern.edu)");


            LinearLayout lila1= new LinearLayout(MainActivity.this);
            lila1.setOrientation(LinearLayout.VERTICAL); //1 is for vertical orientation

            lila1.addView(empty);
            lila1.addView(timePlace);

            builder.setView(lila1);

            builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    dialog.dismiss();

                }
            });

            AlertDialog dialog = builder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            // dialog.getWindow().getAttributes().privateFlags |= WindowManager.LayoutParams.PRIVATE_FLAG_SHOW_FOR_ALL_USERS;
            dialog.show();

        }

        return super.onOptionsItemSelected(item);
    }



    private void requestSystemWritePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

            }
        }
    }

    private void requestOverlayPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
        }
    }


    private void makeNotification() {
        try {
            NotificationCompat.Builder nb = new NotificationCompat.Builder(this);
            nb.setSmallIcon(R.drawable.nu_icon_two);
            nb.setContentTitle("Colorful Screen Filter");
            nb.setContentText("Active");
            nb.setAutoCancel(true);
            Intent resultIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            nb.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0x355, nb.build());
        } catch (Exception e) {
            if (DBG) Log.d(TAG, "ERROR in notification: " + e);
        }
    }


    @Override
    public void onDestroy(){
        stopServiceIfActive();
        super.onDestroy();
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ServiceClassPhone.LocalBinder binder = (ServiceClassPhone.LocalBinder) service;
            myService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


}

