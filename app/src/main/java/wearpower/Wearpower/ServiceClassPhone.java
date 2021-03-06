
package wearpower.Wearpower;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
//import androidx.core.app.TaskStackBuilder;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Vector;

import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.REPTree;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import weka.core.Instance;

import android.app.AlertDialog;


// * Created by emir on 5/7/17.
// *
// *
// * In this project, we are trying to lock/unlock screen with 3d movements of the phone
// *
// *


@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class ServiceClassPhone extends Service implements SensorEventListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, FirebaseListener {

    final private static boolean DBG = Definitions.DBG;
    final public static String TAG = "ServiceClassPhone";

    private static Logger mLogger = new Logger();

    private FirebaseListener listener = this;
    private StorageReference mStorageRef;
    private StorageReference mStorageRefUS;
    private StorageMetadata metadata;
    private int mScreenBrightness = -1;
    private int sett = 0;
    //  private UStats uStats;
    //  private Applications applications;
    private VoiceRecorder voiceRecorder;
    private TouchRecorder touchRecorder;


    private int cam_counter = 0;

    private boolean firstTime = true;
    private String fpsline;

    //  private TouchReading touch;

    private static long appStartTime = System.currentTimeMillis();
    private long unlock_notification = System.currentTimeMillis();

    private Instances data;
    private Instances dataUS;

    Button mButton;

    private LibSVM svm;
    private REPTree repTreeStatic;
    private REPTree repTreeUserSpecific;
    private SimpleKMeans kmeans;
    private DTW dtw;

    final private static Object mLogLock = new Object();

    private boolean screenIsOn = true;
    private boolean camOn = false;

    private int sendToServerNeeded = 0;

    private int user_input = 0;
    private int count = 0;
    public boolean bool = false;

    private boolean userModelIsCreated = false;
    private boolean usModel = true;
    private boolean staticModel = false;
    private boolean stdModel = false;
    private boolean userSpecificModelIsSafe = false;
    private boolean staticModelisCreated = false;
    private String logOfModel = null;


    public static final String INTENT_EXTRA_OUTCOME = "outcome";
    public static final String INTENT_EXTRA_TIMESTAMP = "timestamp";
    public static final String INTENT_EXTRA_NOTE = "note";
    public static final String INTENT_EXTRA_VERSION = "version";

    final private static Object mFileUploadLock = new Object();
    public static final String SHARED_PREFS_NAME = ServiceClassPhone.class.getPackage().getName();
    private static final String LASTUPLOAD_SP_KEY = "LastUpload";
    private static final String DISSATISFACTION_ARRAY = "DissatisfactionArray";
    private static final String USERMODELTRAINING = "UserModelTraining";


    //   private static final byte[] FPS_DATA = FileRepeatReader.generateReadfileCommand(FPS_PATH);
    private static Vector<Double> fpsLogs = new Vector<Double>();

    private static long cpu_conf_time_interval = 100; // 30 sec to change cpu configuration

    private int counter_for_arff = 0;

    private int order = 0;
    private double power = 0.0;
    private int counter_for_power = 0;
    private int counter_for_day = 0;
    private double current_now = 0;
    private double voltage = 0;
    private double current_avg = 0;
    private double voltage_avg = 0;
    private double voltage_sum = 0;
    private double current_sum = 0;
    private double power_sum = 0;
    private double power_avg = 0;

    private double avg_voice = 0;
    private double min_voice = 0;
    private double max_voice = 0;

    private int batt_level = 50;
    private double cpu_usage = 0.0;
    private double check_brt = 0.0;
    private boolean write = false;
    double curr_time = 0.0;
    double Server_refresh = 0.0;
    double Server_refresh_2 = 0.0;
    double Server_refresh_3 = 0.0;
    public int temp = 0;
    public int SETTING = -1;
    public boolean change_brightness = false;

    public String UUID = "";
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    //private String morning_night_now = "noon";
    private String morning_night_now = "";
    private double time_hour_now = -1;

    IntentFilter filter;
    IntentFilter powerfilter;

    private boolean screen_clicked = false;

    private long lastModelStartTime = 0;

    private long lastTimeCpu = System.currentTimeMillis();

    private LinkedList accell_p_x;
    private LinkedList accell_p_y;
    private LinkedList accell_p_z;
    private LinkedList gyro_p_x;
    private LinkedList gyro_p_y;
    private LinkedList gyro_p_z;
    private LinkedList ambient_;
    private LinkedList pressure_;
    private LinkedList touch_binary;
    private LinkedList power_;
    private LinkedList current_;
    private LinkedList voltage_;
    private LinkedList avg_voice_;
    private LinkedList min_voice_;
    private LinkedList max_voice_;
    private LinkedList time_hour;
    private LinkedList day_night;
    private LinkedList batt_level_;
    private LinkedList scrBrigtness;
    private LinkedList app_name_;

    // ArrayList<Double> accell_p_x;
    //  ArrayList<Double> accell_p_y;
    //  ArrayList<Double> accell_p_z;
    ArrayList<Double> accell_p_x_set;
    ArrayList<Double> accell_p_y_set;
    ArrayList<Double> accell_p_z_set;
    ArrayList<Double> accell_p_x_test;
    ArrayList<Double> accell_p_y_test;
    ArrayList<Double> accell_p_z_test;

    // creating arrayLists of ArrayLists
    ArrayList<Double>[] accell_p_x_t;
    ArrayList<Double>[] accell_p_y_t;
    ArrayList<Double>[] accell_p_z_t;
    ArrayList<Double>[] gyro_p_x_t;
    ArrayList<Double>[] gyro_p_y_t;
    ArrayList<Double>[] gyro_p_z_t;
    ArrayList<Double>[] ambient_t;
    ArrayList<Double>[] pressure_t;
    ArrayList<Double>[] touch_binary_t;
    ArrayList<Double>[] power_t;
    ArrayList<Double>[] current_t;
    ArrayList<Double>[] voltage_t;
    ArrayList<Double>[] avg_voice_t;
    ArrayList<Double>[] min_voice_t;
    ArrayList<Double>[] max_voice_t;
    ArrayList<Double>[] time_hour_t;
    ArrayList<Double>[] batt_level_t;
    ArrayList<Double>[] scrBrigtness_t;


    private SensorManager mSensorManager; // sensor manager and sensor event listener objects

    private boolean isPhoneLocked = false;

    private PowerManager powerManager;                  // wake lock to make this app runs even in screen off
    public PowerManager.WakeLock wakeLock;

    //private AlertDialog dialog=null;

    private double sec = 0;
    private double min = 0;

    private int counter = 0;
    private String accellerometerSensor = "";
    private String heartRateSensor = "";
    private String stepCounterSensor = "";
    private String gyroscopeSensor = "";
    private int accellerometerEvent = 0;
    private int heartRateEvent = 0;
    private int stepCounterEvent = 0;
    private String touchEvents = "";
    private int gyroscopeEvent = 0;
    private int proximityEvent = 0;
    private String proximitySensor = "";
    private int pressureEvent = 0;
    private String pressureSensor = "";
    private boolean pressure_available = false;
    private int ambientEvent = 0;
    private String ambientSensor = "";
    private double accell_phone_x = 0;
    private double accell_phone_y = 0;
    private double accell_phone_z = 0;
    private double gyro_phone_x = 0;
    private double gyro_phone_y = 0;
    private double gyro_phone_z = 0;
    private double pressure = 0;
    private double ambient = 0;
    private double DISTANCE_X = 50;
    private double DISTANCE_Y = 50;
    private double DISTANCE_Z = 80;
    private String app_name = "";

    private int input_counter = 1800;
    private long lastTimeActivity = 0;

    private long lastUploadTime = System.currentTimeMillis();

    private long screen_brightness_changed_time = System.currentTimeMillis();

    private boolean sets_collected = false;
    private boolean locked = false;
    private long lastToastTime = 0;

    private long lastTime = System.currentTimeMillis();
    private long lastFileSize = System.currentTimeMillis();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /*
    Screen Study --> Features with no pressure sensor
    Attributes:   16
    accel_p_x
    accel_p_y
    accel_p_z
    gyro_p_x
    gyro_p_y
    gyro_p_z
    ambient
    pressure
    power
    current
    voltage
    avg_voice
    min_voice
    max_voice
    time_hour
    day_night
    y  =  screen_bright
    app_name

    */

    // TODO: features names should be same with the models created!
    private static String[] featureList = {"accel_p_x", "accel_p_y", "accel_p_z",
            "gyro_p_x", "gyro_p_y", "gyro_p_z", "ambient", "pressure", "avg_voice", "min_voice", "max_voice",
            "time_hour", "day_night", "batt_lev", "screen_bright", "app_name"};

    // "power" , "current" , "voltage",


    public static int STATE;

    public static final int INACTIVE = 0;
    public static final int ACTIVE = 1;

    static {
        STATE = INACTIVE;
    }


    public ActivityManager mActivityManager;

    private final IBinder mBinder = new LocalBinder();


    /*
    GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(ServiceClassPhone.this)
            .addConnectionCallbacks(ServiceClassPhone.this)
            .addOnConnectionFailedListener(ServiceClassPhone.this)
            .addApi(ActivityRecognition.API)
            .build();
    */
    GoogleApiClient mGoogleApiClient = null;

    public ServiceClassPhone() throws Settings.SettingNotFoundException {
    }


    @Override
    public void onCreate() {

        try {

            try {
                createInitiateLogFiles();
            } catch (Exception e) {
                if (DBG) Log.d(TAG, "Error in file creation in onCreate");
            }

            try {
                initiateArrayLists();
                initiateLinkedLists();
            } catch (Exception e) {
                if (DBG) Log.d(TAG, "Error in initiating linked lists in onCreate");
            }


            if (DBG) Log.d(TAG, "Touch/Voice are formed: ");

            mActivityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
          //  ActivityManager.RunningTaskInfo foregroundTaskInfo = mActivityManager.getRunningTasks(1).get(0);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = mActivityManager.getRunningAppProcesses();
            if (DBG) Log.d(TAG, "Touch/Voice are formed2: ");

            // get device manufacturer name
            mLogger.logStringEntry("P_Dev: " + getDeviceName());

            if (DBG) Log.d(TAG, "Registers are formed: ");

              voiceRecorder = new VoiceRecorder();
            // touchRecorder = new TouchRecorder();

            //WHITE LIST FOR POWER OPTIMIZATION
           //
            UUID = id(getApplicationContext());
            mStorageRef = FirebaseStorage.getInstance().getReference();

            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            curr_time = System.currentTimeMillis();
            Server_refresh = System.currentTimeMillis();
            Server_refresh_2 = System.currentTimeMillis();
            Server_refresh_3 = System.currentTimeMillis();
            //whitelist(getApplicationContext());

            registerAll();
            startSensorListeners();
            mCollectorHandler.postDelayed(mCollectorRefresh, 2000);
            //  mTouchHandler.postDelayed(mTouchRefresh, 2000);
            powerHandler.postDelayed(powerRefresh, 2000);
            sensorHandler.postDelayed(sensorRefresh, 2000);
            cpuHandler.postDelayed(cpuRefresh, 2000);
            check_brt = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);

            if (DataHolder.getInstance().getVoicePermission()) {
                voiceRecorder.start();
                //  touchRecorder.start();
            }

            if (DBG) Log.d(TAG, "service class after voice recorder ");
            STATE = ACTIVE;

        } catch (Exception e) {
            if (DBG)
                Log.d(TAG, "Error Occured in ServiceClass1: " + e.toString() + " " + e.getStackTrace());
            if (DBG) {
                if (DBG) Log.e(TAG, "Brightness setting not found " + e);
            }
            STATE = INACTIVE;
        } catch (Throwable t) {
            if (DBG)
                Log.d(TAG, "Error Occured in ServiceClass2: " + t.toString() + " " + t.getStackTrace());
            STATE = INACTIVE;
        }
    }

    public void createInitiateLogFiles() {
        try {

            // create and initiate files if they are already not there
            mLogger.createLogFile(this);
            // Logger.createLogFileToUpload(this);
            mLogger.logStringEntry("Logger On");
            final PackageManager pm = getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            mLogger.createArffFile(this);  // for weka ml, arff file format to use in instant predictions

            Logger.createArffFileUsModel(this);

            File arff_file = new File(Definitions.ARFF_FILE_NAME);

            if (DBG) Log.d(TAG, "Arff File length is: " + arff_file.length());

            if (arff_file.length() <= 20) {
                //Logger.InitiateArffFileUsModel(featureList);
                Logger.InitiateArffFile(featureList,packages);
            }

            // if there is a problem and app crashes bc of data in it
            SharedPreferences prefs = getSharedPreferences(Definitions.APP_STARTED, Context.MODE_PRIVATE);
            long lastTimeAppStarted = prefs.getLong(Definitions.APP_STARTING_TIME, 0);
            if (System.currentTimeMillis() - lastTimeAppStarted <= Definitions.THREE_MIN) {
                mLogger.deleteUploadedFiles(this);
                mLogger.createLogFile(this);
                mLogger.logStringEntry("Logger On");
                //System.out.println("here!");
            }
            prefs.edit().putLong(Definitions.APP_STARTING_TIME, System.currentTimeMillis()).commit();
            Log.d(TAG, String.valueOf(this.getFilesDir()));
            if (DBG) Log.d(TAG, "Shared pref app start time is written");


        } catch (Exception e) {
            Log.d(TAG, "Error in creating files: " + e);
        }
    }


    private void initiateArrayLists() {
        accell_p_x_set = new ArrayList<>();
        accell_p_y_set = new ArrayList<>();
        accell_p_z_set = new ArrayList<>();

        accell_p_x_test = new ArrayList<>();
        accell_p_y_test = new ArrayList<>();
        accell_p_z_test = new ArrayList<>();

        // creating arrayLists of ArrayLists


    }

    private void initiateLinkedLists() {
        accell_p_x = new LinkedList();
        accell_p_y = new LinkedList();
        accell_p_z = new LinkedList();
        gyro_p_x = new LinkedList();
        gyro_p_y = new LinkedList();
        gyro_p_z = new LinkedList();
        ambient_ = new LinkedList();
        pressure_ = new LinkedList();
        touch_binary = new LinkedList();
        power_ = new LinkedList();
        current_ = new LinkedList();
        voltage_ = new LinkedList();
        avg_voice_ = new LinkedList();
        min_voice_ = new LinkedList();
        max_voice_ = new LinkedList();
        time_hour = new LinkedList();
        day_night = new LinkedList();
        batt_level_ = new LinkedList();
        scrBrigtness = new LinkedList();
        app_name_ = new LinkedList();
    }


    public void registerAll() {
        // screen on/off + wifi manager rssi + airplane mode + date + timezone + headset plugged +
        // applications = all of them (music, browser, game?, texting, radio)
        // how much each app is used, optimal configuration...
        // current app info and current cpu conf? also

        IntentFilter filter = new IntentFilter();
        //powerfilter = new IntentFilter();

        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        /*
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(Intent.ACTION_CAMERA_BUTTON);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_DEVICE_STORAGE_LOW);
        filter.addAction(Intent.ACTION_DEVICE_STORAGE_OK);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(Intent.ACTION_MANAGE_PACKAGE_STORAGE);
        // PUT INTO PHONE STUFF? - skipping for now

        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_UMS_CONNECTED);
        filter.addAction(Intent.ACTION_UMS_DISCONNECTED);

        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        //powerfilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        filter.addAction(AudioManager.VIBRATE_SETTING_CHANGED_ACTION);
        filter.addAction(String.valueOf(BatteryManager.BATTERY_STATUS_CHARGING));
        filter.addAction(String.valueOf(BatteryManager.BATTERY_STATUS_FULL));
        filter.addAction(String.valueOf(BatteryManager.BATTERY_PLUGGED_USB));
        filter.addAction(String.valueOf(BatteryManager.BATTERY_PLUGGED_AC));
        filter.addAction(String.valueOf(BatteryManager.BATTERY_STATUS_NOT_CHARGING));

        registerReceiver(mBroadcastIntentReceiver, filter);
       // registerReceiver(powerBroadcastreceiver, powerfilter);

      */
        filter.addAction(String.valueOf(BatteryManager.BATTERY_STATUS_CHARGING));
        filter.addAction(String.valueOf(BatteryManager.BATTERY_STATUS_FULL));
        filter.addAction(String.valueOf(BatteryManager.BATTERY_PLUGGED_USB));
        filter.addAction(String.valueOf(BatteryManager.BATTERY_PLUGGED_AC));
        filter.addAction(String.valueOf(BatteryManager.BATTERY_STATUS_NOT_CHARGING));
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(mBroadcastIntentReceiver, filter);
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        int ver = Build.VERSION.SDK_INT;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model) + "-" + ver;
        } else {
            return capitalize(manufacturer) + "-" + model + "-" + ver;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "None";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    public static Logger getLogger() {
        return mLogger;
    }


    BroadcastReceiver mBroadcastIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {

                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;

                //    How are we charging?
                //   int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                //    boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                //    boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

                if (isCharging) {
                    mLogger.logStringEntry("Usb_charging: " + isCharging);
                }
                //  Log.d(TAG,"Battery status usb and ac: " + usbCharge + " "+acCharge);
                if (DBG) Log.d(TAG, "Battery status is charging: " + isCharging);

                if (DBG) {
                    Log.i(TAG, ">>>>>>>>>> caught USB_CHARGING <<<<<<<<<<<");
                }
                //  mLogger.logEntry("USB_CHARGING");
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                mLogger.logStringEntry("Batt: " + level);
                batt_level = level;

            } else if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                if (DBG) {
                    Log.i(TAG, ">>>>>>>>>> caught ACTION_POWER_CONNECTED <<<<<<<<<<<");
                }
                mLogger.logStringEntry("Power_Connected");
            } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
                if (DBG) {
                    Log.i(TAG, ">>>>>>>>>> caught ACTION_POWER_DISCONNECTED <<<<<<<<<<<");
                }
                mLogger.logStringEntry("Power_DisConnected");
            } else if (intent.getAction().equals(String.valueOf(BatteryManager.BATTERY_PLUGGED_USB))) {
                if (DBG) {
                    Log.i(TAG, ">>>>>>>>>> caught BATTERY_PLUGGED_USB <<<<<<<<<<<");
                }
                mLogger.logStringEntry("BATTERY_PLUGGED_USB");
            } else if (intent.getAction().equals(String.valueOf(BatteryManager.BATTERY_STATUS_CHARGING))) {
                if (DBG) {
                    Log.i(TAG, ">>>>>>>>>> caught BATTERY_STATUS_CHARGING <<<<<<<<<<<");
                }
                mLogger.logStringEntry("BATTERY_STATUS_CHARGING");
            } else if (intent.getAction().equals(String.valueOf(BatteryManager.BATTERY_STATUS_NOT_CHARGING))) {
                if (DBG) {
                    Log.i(TAG, ">>>>>>>>>> caught BATTERY_STATUS_NOT_CHARGING <<<<<<<<<<<");
                }
                mLogger.logStringEntry("BATTERY_STATUS_NOT_CHARGING");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                if (DBG) {
                    Log.i(TAG, ">>>>>>>>>> caught ACTION_SCREEN_OFF <<<<<<<<<<<");
                }
                //mLogger.logSimpleEntry(Logger.EntryType.SCREEN_OFF);
                mLogger.logStringEntry("ScreenOff");
                screenIsOn = false;
                //  if(wakeLock==null)wakeLock.acquire();

            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if (DBG) {
                    Log.i(TAG, ">>>>>>>>>> caught ACTION_SCREEN_ON <<<<<<<<<<<");
                }
                //mLogger.logSimpleEntry(Logger.EntryType.SCREEN_ON);
                if (screenIsOn == false) {
                    //  moodModule.getCurrentMood();
                }
                mLogger.logStringEntry("ScreenOn");
                screenIsOn = true;

                //   if (wakeLock!=null ||  wakeLockisOn) wakeLock.release();
                //    wakeLock=null;

            } else if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                if (DBG) {
                    Log.i(TAG, ">>>>>>>>>> caught ACTION_BATTERY_CHANGED <<<<<<<<<<<");
                }

            } else if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
                Bundle extra = intent.getExtras();
                int strength = extra.getInt(WifiManager.EXTRA_NEW_RSSI);

                if (DBG) {
                    Log.i(TAG, "Wifi signal strength: " + strength);
                }
                //mLogger.logIntEntry(Logger.EntryType.WIFI_SIGNAL_STRENGTH, strength);
                mLogger.logStringEntry("Wifi signal strength: " + strength);
            } else if (intent.getAction().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
                Bundle extra = intent.getExtras();
                if (true == (Boolean) extra.get("state")) {
                    if (DBG) {
                        Log.i(TAG, ">>>>>>>>>> AIRPLANE MODE: on <<<<<<<<<<<");
                    }
                    //mLogger.logSimpleEntry(Logger.EntryType.AIRPLANE_MODE_ON);
                    mLogger.logStringEntry("Airplane_On");
                } else {
                    if (DBG) {
                        Log.i(TAG, ">>>>>>>>>> AIRPLANE MODE: off <<<<<<<<<<<");
                    }
                    //mLogger.logSimpleEntry(Logger.EntryType.AIRPLANE_MODE_OFF);
                    mLogger.logStringEntry("Airplane_Off");
                }
            } else if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
                if (DBG) {
                    Log.i(TAG, "Date changed");
                }
                //mLogger.logSimpleEntry(Logger.EntryType.DATE_CHANGED);
                mLogger.logStringEntry("Date_changed");
            } else if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                Bundle extra = intent.getExtras();
                String state = "";
                switch ((Integer) extra.get("state")) {
                    case 0:
                        state = "unplugged";
                        break;
                    case 1:
                        state = "plugged";
                        break;
                    default:
                        state = "invalid";
                        break;
                }
                if (DBG) {
                    Log.i(TAG, "Headset Plug: " + state);
                }
                //mLogger.logStringEntry(Logger.EntryType.HEADSET_PLUG, state);
                mLogger.logStringEntry("Headset_Plug: " + state);
            } else if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
                if (DBG) {
                    Log.i(TAG, "Time changed");
                }
                //mLogger.logSimpleEntry(Logger.EntryType.TIME_CHANGED);
                mLogger.logStringEntry("Time_Changed");
            } else if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                Bundle extra = intent.getExtras();
                String timezone = (String) extra.get("time-zone");
                TimeZone tz = TimeZone.getTimeZone(timezone);
                int tz_offset = tz.getRawOffset();
                if (DBG) {
                    Log.i(TAG, "Timezone Change: " + tz_offset);
                }
                //mLogger.logIntEntry(Logger.EntryType.TIMEZONE_CHANGED, tz_offset);
                mLogger.logStringEntry("Timezone_Changed: " + tz_offset);


                // TODO: POSSIBLY STOP LOGGING WHEN STORAGE IS LOW
            } else if (intent.getAction().equals(Intent.ACTION_DEVICE_STORAGE_LOW)) {
                if (DBG) {
                    Log.i(TAG, "Device Storage Low");
                }
                //mLogger.logSimpleEntry(Logger.EntryType.DEVICE_STORAGE_LOW);
                mLogger.logStringEntry("Device_Storage_Low");
            } else if (intent.getAction().equals(Intent.ACTION_DEVICE_STORAGE_OK)) {
                if (DBG) {
                    Log.i(TAG, "Device Storage Ok");
                }
                //mLogger.logSimpleEntry(Logger.EntryType.DEVICE_STORAGE_OK);
                mLogger.logStringEntry("Device_Storage_Ok");
            }

            // low memory condition acknowledged by user - start package management
            else if (intent.getAction().equals(Intent.ACTION_MANAGE_PACKAGE_STORAGE)) {
                if (DBG) {
                    Log.i(TAG, "Manage Package Storage");
                }
                //mLogger.logSimpleEntry(Logger.EntryType.MANAGE_PACKAGE_STORAGE);
                mLogger.logStringEntry("Manage_Package_Storage");
            } else if (intent.getAction().equals(Intent.ACTION_UMS_CONNECTED)) {
                if (DBG) {
                    Log.i(TAG, "UMS Connected");
                }
                mLogger.logStringEntry("UMS_Connected");
            } else if (intent.getAction().equals(Intent.ACTION_UMS_DISCONNECTED)) {
                if (DBG) {
                    Log.i(TAG, "UMS Connected");
                }
                mLogger.logStringEntry("UMS_Disconnected");
            }

            // TODO ensure this vibrate settings thing works
            else if (intent.getAction().equals(AudioManager.VIBRATE_SETTING_CHANGED_ACTION)) {
                if (DBG) {
                    Log.i(TAG, "Vibrate Setting changed");
                }

            }
        }
    };

    @Override
    public void onDestroy() {
        try {
            Toast.makeText(this, "App is closing", Toast.LENGTH_LONG).show();
            try {
                unregisterReceiver(mBroadcastIntentReceiver);
            } catch (Exception e) {
                Log.d(TAG, "ERROR while destroy: " + e);
            }
            try {
                mCollectorHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                Log.d(TAG, "ERROR while destroy: " + e);
            }

            try {
                sensorHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                Log.d(TAG, "ERROR while destroy: " + e);
            }
            try {
                cpuHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                Log.d(TAG, "ERROR while destroy: " + e);
            }
            try {
                unregisterReceiver(powerBroadcastreceiver);
                powerHandler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                Log.d(TAG, "ERROR while destroy: " + e);
            }
            try {
                //
                 voiceRecorder.stop();
                stopSensorListeners();
            } catch (Exception e) {
                Log.d(TAG, "ERROR while destroy: " + e);
            }

            //Intent service = new Intent(this, ServiceClassPhone.class);
            Intent service = new Intent(this, ScreenService.class);
            Intent intent = new Intent(ServiceClassPhone.this, DetectedActivitiesIntentService.class);
            ServiceClassPhone.this.stopService(intent);
            //   MyWakefulReceiver.completeWakefulIntent(service);
            //sendToServer();
            STATE = INACTIVE;
            stopSelf();
        } catch (Exception e) {
            if (DBG) Log.d(TAG, "ERROR on destroy: " + e);
        }
    }

    public boolean writeDataToArrayList1(int set_for) {
        try {
            accell_p_x_t[set_for].add(accell_phone_x);
            accell_p_y_t[set_for].add(accell_phone_y);
            accell_p_z_t[set_for].add(accell_phone_z);
            gyro_p_x_t[set_for].add(gyro_phone_x);
            gyro_p_y_t[set_for].add(gyro_phone_y);
            gyro_p_z_t[set_for].add(gyro_phone_z);
            ambient_t[set_for].add(ambient);
            pressure_t[set_for].add(pressure);
            touch_binary_t[set_for].add((double) DataHolder.getInstance().getUserInput());
            power_t[set_for].add(power_avg);
            current_t[set_for].add(current_avg);
            voltage_t[set_for].add(voltage_avg);
            avg_voice_t[set_for].add(avg_voice);
            min_voice_t[set_for].add(min_voice);
            max_voice_t[set_for].add(max_voice);
            time_hour_t[set_for].add(time_hour_now);
            batt_level_t[set_for].add((double) batt_level);
            scrBrigtness_t[set_for].add((double) mScreenBrightness);
            return true;
        } catch (Exception e) {
            if (DBG) Log.d(TAG, "ERROR in setting to arraylist: " + set_for);
        }
        return false;
    }

    public boolean writeDataToArrayList() {
        try {
            accell_p_x_set.add(accell_phone_x);
            accell_p_y_set.add(accell_phone_y);
            accell_p_z_set.add(accell_phone_z);
            return true;
        } catch (Exception e) {
            if (DBG) Log.d(TAG, "ERROR in setting to arraylist: ");
        }
        return false;
    }

    public double[] formArraysFromArrayLists(ArrayList<Double> arrayList) {
        double[] target = new double[arrayList.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = arrayList.get(i).doubleValue();  // java 1.4 style
            // or:
            target[i] = arrayList.get(i);                // java 1.5+ style (outboxing)
        }
        return target;
    }

    private double[][] getDoubleArray(ArrayList<Double> arrayList, ArrayList<Double> arrayList_t) {
        //int min_size = Math.min(arrayList.size() , arrayList_t.size());

        int min_size = arrayList_t.size();

        if (DBG)
            Log.d(TAG, "min size is: " + min_size + " " + arrayList.size() + " " + arrayList_t.size());

        double[][] lists = new double[][]{new double[min_size], new double[min_size]};

        for (int i = arrayList.size() - min_size; i < arrayList.size(); i++) {
            lists[0][i - (arrayList.size() - min_size)] = arrayList.get(i);                // java 1.5+ style (outboxing)
        }
        for (int j = arrayList_t.size() - min_size; j < arrayList_t.size(); j++) {
            lists[1][j] = arrayList_t.get(j);                // java 1.5+ style (outboxing)
        }
        return lists;
    }

    public boolean writeDataToArrayListTest() {
        accell_p_x_test.add(accell_phone_x);
        accell_p_y_test.add(accell_phone_y);
        accell_p_z_test.add(accell_phone_z);

        if (accell_p_x_test.size() > accell_p_x_set.size()) {
            accell_p_x_test.remove(0);
            accell_p_y_test.remove(0);
            accell_p_z_test.remove(0);

            if (DBG)
                Log.d(TAG, "DISTANCES test: " + accell_p_x_test.get(accell_p_x_test.size() - 1) + " "
                        + accell_p_x_test.get(accell_p_x_test.size() - 2) + " "
                        + accell_p_x_test.get(accell_p_x_test.size() - 3) + " ");

            if (DBG)
                Log.d(TAG, "DISTANCES set: " + accell_p_x_set.get(accell_p_x_set.size() - 1) + " "
                        + accell_p_x_set.get(accell_p_x_set.size() - 2) + " "
                        + accell_p_x_set.get(accell_p_x_set.size() - 3) + " ");

        }
        return true;
    }


    public boolean keepTrackAndCheck() {
        try {

            if (accell_p_x_test.size() < accell_p_x_set.size()) {
                return false;
            }

            double d1 = dtw.dd(accell_p_x_set, accell_p_x_test, 10);
            double d2 = dtw.dd(accell_p_y_set, accell_p_y_test, 10);
            double d3 = dtw.dd(accell_p_z_set, accell_p_z_test, 10);
            if (DBG) Log.d(TAG, "DISTANCES: " + d1 + " " + d2 + " " + d3);
            return d1 < DISTANCE_X && d2 < DISTANCE_Y && d3 < DISTANCE_Z;

        } catch (Exception e) {
            if (DBG) Log.d(TAG, "ERROR in keeping track and checking: " + e);
        }
        return false;
    }


    Handler mCollectorHandler = new Handler();   // handler is more convenient for massage passing between objects and also UI friendly.
    // so if we need to put some info or even in notifications we may need handler instead of thread.
    Runnable mCollectorRefresh = new Runnable() {


        @Override
        public void run() {


            try {
                //ScreenService.ACCESSIBILITY_SERVICE;

                try {
                    determineTimeDay();
                } catch (Exception e) {
                    Log.d(TAG, "Error in time/day");
                }

                if (DataHolder.getInstance().getVoiceStarted() == false) {
                     voiceRecorder.start();
                }

                // if input is received reset input counter
                if (screen_clicked) input_counter = 0;

                // check if screen is locked so set screen brightness to 0

                KeyguardManager myKM = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
                isPhoneLocked = myKM.isKeyguardLocked();
                if (isPhoneLocked) {
                    mScreenBrightness = 0;
                    input_counter = 1800;
                } else {
                    mScreenBrightness = Settings.System.getInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS);
                }

                /*
                make sure screen brightness is not in adaptive! or it can be adaptive if its user's choice
                 */
                if (!isPhoneLocked || mScreenBrightness > 0) {
                    //         writeToArffFileTemp();
                    //          writeToArffFileForUS(accell_p_x.size()-1);
                }

                if (screenIsOn == false) mScreenBrightness = 0;


                power_avg = power_sum / counter_for_power;
                current_avg = current_sum / counter_for_power;
                voltage_avg = voltage_sum / counter_for_power;

                avg_voice = DataHolder.getInstance().getAvgVoice();
                min_voice = DataHolder.getInstance().getMinVoice();
                max_voice = DataHolder.getInstance().getMaxVoice();

                String print = printForegroundTask();
                SharedPreferences prefsUserInput = getSharedPreferences(Definitions.USER_RATING_TIME,
                        Context.MODE_PRIVATE);
                long lastTimeUserRating = prefsUserInput.getLong(Definitions.USER_RATING_TIME, 0);
                //build_model();
               /* if (System.currentTimeMillis() - lastTimeUserRating >= Definitions.TENSEC
                        || (input_counter >= 2000 && screen_clicked==false)) {
                    input_counter = 0;
                    screen_clicked=true;
                    try{
                        build_model();
                        prefsUserInput.edit().putLong(Definitions.USER_RATING_TIME,
                                System.currentTimeMillis()).commit();
                    }catch (Exception e){
                        Log.d(TAG , "Error in alert dialog: " + e );
                    }
                }

                */
                // System.out.println(print);
                mLogger.logStringEntry("PowCV: " + power_avg + " " + current_avg + " " + voltage_avg);
                // mLogger.logStringEntry(print);
                // System.out.println("sending to server?");
                // sendToServer();
                // check model and if its been one day change it

                // write to temp file to make predictions based on updated data


                if (System.currentTimeMillis() - DataHolder.getInstance().getUserInputTime() > 2000) { // 2 sec since usr in continue with manual
                    DataHolder.getInstance().setUserInput(0);
                }


                power_sum = 0.0;
                current_sum = 0.0;
                voltage_sum = 0.0;
                counter_for_power = 0;

                VoiceRecorder.getInstance().setAvgVoice(1,0);
                VoiceRecorder.getInstance().setMinVoice(0.0);
                VoiceRecorder.getInstance().setMaxVoice(0.0);
                if (DBG) Log.d(TAG, "Collector Handler");
                if (System.currentTimeMillis() - lastUploadTime > Definitions.FOUR_HOURS) {
                    //   sendToServer();
                } else if (mLogger.logFileSize(ServiceClassPhone.this) > 200000 * 5) {
                    //   sendToServer();
                }

                mCollectorHandler.postDelayed(mCollectorRefresh, 1000); // in every 3 sec

            } catch (Exception e) {
                if (DBG) Log.i(TAG, "Error occured in collector: " + e);
                mCollectorHandler.postDelayed(mCollectorRefresh, 200000);
            }
        }
    };


    private void determineTimeDay() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-HH-mm-ss");
        String current_time = dateFormat.format(date);
        String[] parts = current_time.split("-");
        time_hour_now = Double.parseDouble(parts[2]);
        min = Double.parseDouble(parts[3]);
        sec = Double.parseDouble(parts[4]);
        if (DBG) Log.d(TAG, "time hour: " + time_hour_now);
        //time_hour_now = min;
        //morning_night_now = sec;
        if (time_hour_now>=6 && time_hour_now<10)
            morning_night_now = "morning";
        else if (time_hour_now>=10 && time_hour_now<16)
            morning_night_now = "noon";
        else if (time_hour_now>=16 && time_hour_now<20)
            morning_night_now = "evening";
        else
            morning_night_now = "night";


    }


    Handler sensorHandler = new Handler();
    Runnable sensorRefresh = new Runnable() {

        @Override
        public void run() {

            try {

                //  mLogger.logStringEntry("Accel: " + accellerometerSensor);
                //  Log.d(TAG, "Accel: " + accellerometerSensor);
                //  mLogger.logStringEntry("Gyro: " + gyroscopeSensor);
                //  Log.d(TAG, "Gyro: " + gyroscopeSensor);
                //  mLogger.logStringEntry("Time_of_Day: " + morning_night_now);
                //  Log.d(TAG, "Time_of_Day: " + morning_night_now);

                //   if (mScreenBrightness >= 170) {
                //    setBrightness(10);
                //    }
                //    else if (mScreenBrightness <= 70) {
                //    setBrightness(240);
                //    }


                //mLogger.logStringEntry("PowCV: " + power_avg  + " " + current_avg + " " + voltage_avg );
                mScreenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                //mLogger.logStringEntry("Brightness: " + mScreenBrightness);
                // Log.d(TAG, "Brightness: " + mScreenBrightness);
                SETTING = DataHolder.getInstance().getSetCount();
                if (DBG) Log.d(TAG, "SETTING: " + SETTING);
                count += 1;
                /*
                if (temp == 0) {
                    if (printForegroundTask().equals("com.google.android.youtube")) {
                        if (change_brightness()) {
                            temp += 1;
                        }
                    }
                }
                if (temp == 1) {
                    curr_time = System.currentTimeMillis();
                    while (System.currentTimeMillis() - curr_time < 6000) {
                        write = false;
                    }
                    temp += 1;
                }
                if (temp == 2) {
                    curr_time = System.currentTimeMillis();
                    while (System.currentTimeMillis() - curr_time < 3000) {
                        write = true;
                    }

                }
                */
                //60 secs into the past data structure
                app_name = printForegroundTask();
                if (temp == 0) {
                    if (System.currentTimeMillis() - curr_time > 60000) {
                        //clear the linked list last entry
                        removeFirstElements(1);
                        //   curr_time = curr_time+400;
                    }
                    //add to the linked list
                    writeToLinkedList();
                }


                //PRINT CURR_TIME AND SYSM.CURRTIME and check

               // System.out.println(DataHolder.getInstance().getUserInput());
               // System.out.println(min_voice);

                if (temp == 0) {
                    if (Settings.System.getInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS) != check_brt) {
                        check_brt = Settings.System.getInt(getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS);
                        change_brightness = true;
                    } else
                        change_brightness = false;
                    System.out.println("!!!!!!!!!!");
                    if (change_brightness) {
                        LinkedListToArff();
                        curr_time = System.currentTimeMillis();
                        temp += 1;
                    }
                }
                        /*
                        if (temp == 1) {
                            System.out.println("@@@@@@@@@@");
                            if (System.currentTimeMillis() - curr_time > 60000) {
                                curr_time = System.currentTimeMillis();
                                temp = 2;
                            }
                            System.out.println(System.currentTimeMillis() + " : system time");
                            System.out.println(curr_time+ " : curr_time");
                        }

                         */
                if (temp == 1) {
                    System.out.println("##########");
                    if (System.currentTimeMillis() - curr_time < 60000) {
                        //writeToLinkedList();
                        writeToArffFileTemp();
                    } else {
                        temp = 2;
                        curr_time = System.currentTimeMillis();
                    }
                }
                        /*
                        if (temp == 3) {
                            if (System.currentTimeMillis() - curr_time > 60000) {
                                curr_time = System.currentTimeMillis();
                                temp = 4;
                            }
                        }
                         */
                if (temp == 2) {
                    System.out.println("@@@@@@@@@@");
                    if (System.currentTimeMillis() - curr_time < 60000) {
                        //writeToLinkedList();
                        writeToArffFileTemp();
                    } else {
                        // writeToArffFileTemp();
                        //LinkedListToArff();
                        curr_time = System.currentTimeMillis();
                        temp = 0;
                    }
                }
                //System.out.println(System.currentTimeMillis() - Server_refresh);
                if (System.currentTimeMillis() - Server_refresh > 14400000) {

                    //System.out.println(Server_refresh);
                     sendToServer(listener);
                    Server_refresh = System.currentTimeMillis();


                }
                if (bool) {
                    //refresh the data structures
                    mLogger.deleteArffFile(getApplicationContext());
                    mLogger.createArffFile(getApplicationContext());
                    bool = false;
                    //delete and recreate the log file
                }
/*
                if (System.currentTimeMillis() - Server_refresh_2 > 10000) {
                  //  System.out.println("////////////////////////");

                    if (getFromServer()) {
                       // System.out.println("+++++++++++++++++++++++");
                        //refresh the data structures
                        Server_refresh_2 = System.currentTimeMillis();

                       // mLogger.deleteArffFile(getApplicationContext());
                       // mLogger.createArffFile(getApplicationContext());
                        //delete and recreate the log file
                        // mLogger.createArffFile(this);
                    }
                }

                if (System.currentTimeMillis() - Server_refresh_3 > 10000) {
                   // System.out.println("////////////////////////");

                    predictScreenBrightness();
                   // System.out.println("+++++++++++++++++++++++");
                        //refresh the data structures
                    Server_refresh_3 = System.currentTimeMillis();

                }

*?
              /*   if (SETTING == 0){
                    initiateArrayLists();
                    DataHolder.getInstance().setSetCount(1);
                    SETTING += 1;
                }
                if(SETTING == 1){
                    writeDataToArrayList();
                }
                if (SETTING == 2){
                    writeDataToArrayListTest();
                    if (keepTrackAndCheck()){
                        if(DBG) Log.d(TAG, "\n\n\n<<<<<<<Unlocked!>>>>\n\n\n");
                        if (System.currentTimeMillis() - unlock_notification >= 4000) {
                            UNCLOCKED();
                            unlock_notification = System.currentTimeMillis();
                        }
                    }
                    count = 0;

                }

              */

                //collectSensors();


                sensorHandler.postDelayed(sensorRefresh, 90); // 20 Hertz

            } catch (Exception e) {
                if (DBG) Log.i(TAG, "Error occured in linked list writing probably" + e);
                sensorHandler.postDelayed(sensorRefresh, 90); // 20 Hertz
            }
        }
    };

    Handler cpuHandler = new Handler();
    Runnable cpuRefresh = new Runnable() {

        @Override
        public void run() {

            try {
                double power_now = -1.0;

                try {
                    //CPUUSAGEINFO IS PRIVATE???
                    //CpuUsageInfo cpu_usage = new CpuUsageInfo(0.0,0.0);
                    //System.out.println("HELLO???!$!");
                    //getActivity(Context.);
                    //if (getApplicationContext() == null)
                    // if (getActivity(getApplicationContext()) == null)
                    //    System.out.println("yo!");
                    //verifyStoragePermissions(getActivity(getApplicationContext()));
                    //System.out.println("yo!");
                    // readUsage();

                } catch (Exception e) {
                    Log.d(TAG, "CPU Usage Error: " + e);
                }


                cpuHandler.postDelayed(cpuRefresh, cpu_conf_time_interval * 5); // in every 0.5 sec

            } catch (Exception e) {
                Log.i(TAG, "Error occured " + e);
                cpuHandler.postDelayed(cpuRefresh, cpu_conf_time_interval * 5); // in every 0.5 sec
            }
        }
    };

    private void UNCLOCKED() {
        Toast.makeText(this, "Unlocked", Toast.LENGTH_SHORT).show();
    }


    BroadcastReceiver powerBroadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            BatteryManager mBatteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            current_now = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);

            if (DBG) Log.d(TAG, "current and voltage: " + current_now + " " + voltage);

        }
    };

    private void setBrightness(double brightness) {
        boolean set = false;
        if (mScreenBrightness - 8 >= brightness) {
            brightness = mScreenBrightness - 3;
            // System.out.println("hrerereere1");
            set = true;
        }
        //if (ambient/5>brightness) brightness+=5;
        else if (mScreenBrightness < brightness - 15) {
            brightness = mScreenBrightness + 2;
            // System.out.println("hrerereere2");
            set = true;
        } else {
            brightness = mScreenBrightness;
            //  System.out.println("hrerereere");
        }
        if (set) {
            try {
                int auto = Settings.System.getInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE);
                if (auto == 1) {
                    Settings.System.putInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                }
                Settings.System.putInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, (int) brightness);

                if (DBG) Log.d(TAG, "setBrightness Brightness should be set: " + brightness + " " +
                        Settings.System.getInt(getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS));

                mScreenBrightness = Settings.System.getInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS);
            } catch (Exception e) {
                Log.e(TAG, "Error on reading brightness settings: " + e.toString());
            }
        }
        if (DBG) Log.d(TAG, "setBrightness: " + brightness);
    }


    Handler powerHandler = new Handler();   // handler is more convenient for massage passing between objects and also UI friendly.
    // so if we need to put some info or even in notifications we may need handler instead of thread.
    Runnable powerRefresh = new Runnable() {


        @Override
        public void run() {

            try {
                double power_now = -1.0;

                try {
                    IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    registerReceiver(powerBroadcastreceiver, filter);
                    ServiceClassPhone.this.registerReceiver(powerBroadcastreceiver, filter);

                    // if (DBG)
                    //   Log.d(TAG, "Power current and voltage : " + current_now + " " + voltage);

                    // voltage = voltageNow.readVoltage();
                    //current_now = voltageNow.readCurrent();

                    current_now = current_now / 1000; // from micro ampher to milli ampher voltage is alread milivolt

                    power_now = current_now * voltage / 1000; // mW

                    power_sum = power_sum + power_now;
                    current_sum = current_sum + current_now;
                    voltage_sum = voltage_sum + voltage;
                    counter_for_power++;
                    if (DBG)
                        Log.d(TAG, "Power avereage: " + power_sum / counter_for_power);
                    // power_avg = power_sum / counter_for_power;
                    // current_avg = current_sum /counter_for_power;
                    // voltage_avg = voltage_sum /counter_for_power;


                } catch (Exception e) {
                    Log.d(TAG, "Voltage Error: " + e);
                }


                powerHandler.postDelayed(powerRefresh, cpu_conf_time_interval * 5); // in every 0.5 sec

            } catch (Exception e) {
                Log.i(TAG, "Error occured " + e);
                powerHandler.postDelayed(powerRefresh, cpu_conf_time_interval * 5); // in every 0.5 sec
            }
        }
    };


    private PendingIntent getActivityDetectionPendingIntent() {

        Intent intent = new Intent(getApplicationContext(), DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );


        }
        System.out.println("atleast this??");
    }

    public Activity getActivity(Context context) {
        if (context == null) {
            System.out.println("here?");
            return null;
        } else {
            if (context instanceof Activity) {
                return (Activity) context;
            } else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }
        //System.out.println("def here");
        //return null;
    }

    private boolean writeToArffFileForUS(int index) {
        try {
            for (int i = index; i < accell_p_x.size(); i++) {
                mLogger.arffEntryDoubleUs(Double.parseDouble(accell_p_x.get(i).toString()));
                mLogger.arffEntryDoubleUs(Double.parseDouble(accell_p_y.get(i).toString()));
                mLogger.arffEntryDoubleUs(Double.parseDouble(accell_p_z.get(i).toString()));
                mLogger.arffEntryDoubleUs(Double.parseDouble(gyro_p_x.get(i).toString()));
                mLogger.arffEntryDoubleUs(Double.parseDouble(gyro_p_y.get(i).toString()));
                mLogger.arffEntryDoubleUs(Double.parseDouble(gyro_p_z.get(i).toString()));
                mLogger.arffEntryDoubleUs(Double.parseDouble(pressure_.get(i).toString())); // if there is no pressure, it will be zero
                mLogger.arffEntryDoubleUs(Double.parseDouble(ambient_.get(i).toString()));
                //mLogger.arffEntryIntegerUs(Integer.parseInt(touch_binary.get(i).toString())); // touch_filter
                //mLogger.arffEntryDoubleUs(Double.parseDouble(power_.get(i).toString()));
                //mLogger.arffEntryDoubleUs(Double.parseDouble(current_.get(i).toString()));
                //mLogger.arffEntryDoubleUs(Double.parseDouble(voltage_.get(i).toString()));
                mLogger.arffEntryDoubleUs(Double.parseDouble(avg_voice_.get(i).toString()));
                mLogger.arffEntryDoubleUs(Double.parseDouble(min_voice_.get(i).toString()));
                mLogger.arffEntryDoubleUs(Double.parseDouble(max_voice_.get(i).toString()));
                mLogger.arffEntryIntegerUs(Integer.parseInt(time_hour.get(i).toString()));
                mLogger.arffEntryStringUs(day_night.get(i).toString());
                mLogger.arffEntryIntegerUs(Integer.parseInt(batt_level_.get(i).toString()));
                mLogger.arffEntryLong(Long.parseLong(scrBrigtness.get(i).toString()));
                mLogger.arffEntryStringLastUs(app_name_.get(i).toString());
                mLogger.arffEntryNewInstanceUs();
            }
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Error in writing history linked list: " + e.getStackTrace());
        }
        return false;
    }

    private void writeToArffFileTemp() {
        mLogger.arffEntryDouble(accell_phone_x);
        mLogger.arffEntryDouble(accell_phone_y);
        mLogger.arffEntryDouble(accell_phone_z);
        mLogger.arffEntryDouble(gyro_phone_x);
        mLogger.arffEntryDouble(gyro_phone_y);
        mLogger.arffEntryDouble(gyro_phone_z);
        mLogger.arffEntryDouble(pressure); // if there is no pressure, it will be zero
        mLogger.arffEntryDouble(ambient);
        //mLogger.arffEntryInteger(DataHolder.getInstance().getUserInput()); // touch_filter
        // mLogger.arffEntryDouble(power_avg); // without pressure
        // mLogger.arffEntryDouble(current_avg);
        // mLogger.arffEntryDouble(voltage_avg);
        mLogger.arffEntryDouble(avg_voice);
        mLogger.arffEntryDouble(min_voice);
        mLogger.arffEntryDouble(max_voice);
        mLogger.arffEntryDouble(time_hour_now);
        mLogger.arffEntryString(morning_night_now);
        //mLogger.arffEntryDouble(morning_night_now);
        mLogger.arffEntryInteger(batt_level);
        mLogger.arffEntryLong(mScreenBrightness);
        mLogger.arffEntryStringLast(app_name);
        mLogger.arffEntryNewInstance();
    }

    private void LinkedListToArff() {
        try {
            for (int i = 0; i < accell_p_x.size(); i++) {
                mLogger.arffEntryDouble(Double.parseDouble(accell_p_x.get(i).toString()));
                mLogger.arffEntryDouble(Double.parseDouble(accell_p_y.get(i).toString()));
                mLogger.arffEntryDouble(Double.parseDouble(accell_p_z.get(i).toString()));
                mLogger.arffEntryDouble(Double.parseDouble(gyro_p_x.get(i).toString()));
                mLogger.arffEntryDouble(Double.parseDouble(gyro_p_y.get(i).toString()));
                mLogger.arffEntryDouble(Double.parseDouble(gyro_p_z.get(i).toString()));
                mLogger.arffEntryDouble(Double.parseDouble(pressure_.get(i).toString())); // if there is no pressure, it will be zero
                mLogger.arffEntryDouble(Double.parseDouble(ambient_.get(i).toString()));
               // mLogger.arffEntryIntegerUs(Integer.parseInt(touch_binary.get(i).toString())); // touch_filter
                //mLogger.arffEntryDoubleUs(Double.parseDouble(power_.get(i).toString()));
                //mLogger.arffEntryDoubleUs(Double.parseDouble(current_.get(i).toString()));
                //mLogger.arffEntryDoubleUs(Double.parseDouble(voltage_.get(i).toString()));
                mLogger.arffEntryDouble(Double.parseDouble(avg_voice_.get(i).toString()));
                mLogger.arffEntryDouble(Double.parseDouble(min_voice_.get(i).toString()));
                mLogger.arffEntryDouble(Double.parseDouble(max_voice_.get(i).toString()));
                mLogger.arffEntryDouble(Double.parseDouble(time_hour.get(i).toString()));
                mLogger.arffEntryString(day_night.get(i).toString());
                mLogger.arffEntryInteger(Integer.parseInt(batt_level_.get(i).toString()));
                mLogger.arffEntryLong(Long.parseLong(scrBrigtness.get(i).toString()));
                mLogger.arffEntryStringLast(app_name_.get(i).toString());
                mLogger.arffEntryNewInstance();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error in writing history linked list: " + e.getStackTrace());
        }
    }

    private void writeToLinkedList() {

        //if (DBG) Log.d(TAG , "Writing to linked list: " + accell_phone_x + " " +accell_phone_y);
        System.out.println(morning_night_now);
        accell_p_x.add(accell_phone_x);
        accell_p_y.add(accell_phone_y);
        accell_p_z.add(accell_phone_z);
        gyro_p_x.add(gyro_phone_x);
        gyro_p_y.add(gyro_phone_y);
        gyro_p_z.add(gyro_phone_z);
        ambient_.add(ambient);
        pressure_.add(pressure);
        //touch_binary.add(DataHolder.getInstance().getUserInput());
        //power_.add(power_avg);
        //current_.add(current_avg);
        //voltage_.add(voltage_avg);
        avg_voice_.add(avg_voice);
        min_voice_.add(min_voice);
        max_voice_.add(max_voice);
        time_hour.add(time_hour_now);
        day_night.add(morning_night_now);
        batt_level_.add(batt_level);
        scrBrigtness.add(mScreenBrightness);
        app_name_.add(app_name);
        //if (DBG) Log.d(TAG , "Linked list appended to last: " + touch_binary.getLast().toString() + " " + accell_p_x.getLast().toString());

    }

    private void removeFirstElements(int n) {
        for (int i = 0; i < n; i++) {
            accell_p_x.removeFirst();
            accell_p_y.removeFirst();
            accell_p_z.removeFirst();
            gyro_p_x.removeFirst();
            gyro_p_y.removeFirst();
            gyro_p_z.removeFirst();
            ambient_.removeFirst();
            pressure_.removeFirst();
            //touch_binary.removeFirst();
            //power_.removeFirst();
            //current_.removeFirst();
            //voltage_.removeFirst();
            avg_voice_.removeFirst();
            min_voice_.removeFirst();
            max_voice_.removeFirst();
            time_hour.removeFirst();
            day_night.removeFirst();
            batt_level_.removeFirst();
            scrBrigtness.removeFirst();
            app_name_.removeFirst();
        }
    }

    public void determineCurrentModel() {

        try {
            // if user specific model is created to predict screen, then put it into random order
            // random order is, is it time to change the model and cont with the prev one
            // check the last model from model.log
            File model_file = new File(Definitions.MODEL_NAMES);
            if (model_file.exists()) {
                Scanner modelInput = new Scanner(new File(Definitions.MODEL_NAMES));
                String last_line = " ";
                while (modelInput.hasNextLine()) {
                    last_line = modelInput.nextLine();
                }
                if (DBG) Log.d(TAG, "last_line: " + last_line);
                if (last_line.isEmpty() || last_line.equalsIgnoreCase(" ") || last_line == null) {
                    double mathRandom1 = Math.random();
                    if (mathRandom1 >= 0.50) {
                        stdModel = true;
                        staticModel = false;
                        usModel = false;
                        logOfModel = "stdGov";

                    } else {
                        staticModel = true;
                        stdModel = false;
                        usModel = false;
                        logOfModel = "staticModel";
                    }
                    if (DBG) Log.d(TAG, "LogOfModel: " + logOfModel);
                    if (DBG) Log.d(TAG, "LogOfModel random: " + mathRandom1);
                    mLogger.modelLogEntry(logOfModel);
                } else if (last_line.contains("stdGov")) {
                    logOfModel = last_line;
                    stdModel = true;
                    staticModel = false;
                    usModel = false;
                } else if (last_line.contains("staticModel")) {
                    logOfModel = last_line;
                    stdModel = false;
                    staticModel = true;
                    usModel = false;
                } else if (last_line.contains("usModel")) {
                    logOfModel = last_line;
                    stdModel = false;
                    staticModel = false;
                    usModel = true;
                } else {
                    stdModel = true;
                }
            } else {
                Log.e(TAG, "Model file is not created!");
            }

            if (DBG) Log.d(TAG, "Current Model: " + logOfModel);
        } catch (Exception e) {
            Log.e(TAG, "Exception in determining current model");
        }
    }


    private void predictScreenBrightness() {
        if (staticModel) {
            if (!staticModelisCreated) {
                readStaticModel();
            }
            if (staticModelisCreated) {
                makePredictions(0);
            }
        } else if (usModel) {
            if (!userModelIsCreated) {
                createAndReadUserSpecificModel();
            }
            if (userModelIsCreated) {
                makePredictions(1);
            }
        } else {
            usModel = false;
            staticModel = false;
            stdModel = true;
        }

    }


    private void readStaticModel() {
        try {
            if (DBG) Log.d(TAG, "weka static model reading");
            repTreeStatic = null;
            InputStream is = null;

            if (pressure_available) is = getAssets().open("staticScreenModelPress3.model");
            else is = getAssets().open("staticScreenModelNoPress3.model");

            repTreeStatic = (REPTree) SerializationHelper.read(is);
            if (DBG) Log.d(TAG, "weka static model reading success");
            // Classifier cls = (Classifier) SerializationHelper.read("/some/where/j48.model");
            lastModelStartTime = System.currentTimeMillis();
            staticModelisCreated = true;
        } catch (Exception e) {
            if (DBG) Log.d(TAG, "Error in reading static model: " + e);
            staticModelisCreated = false;
        }
    }

    // if mo == 0 its static, if mo==1 its user specific
    private void makePredictions(int mo) {
        try {
            ConverterUtils.DataSource dataSource1 = new ConverterUtils.DataSource(Definitions.ARFF_FILE_NAME);

            Instances testData = new Instances(dataSource1.getDataSet());

            testData.setClassIndex(testData.numAttributes() - 1);

            Instance newInst = testData.instance(14);

            if (DBG) Log.d(TAG, "Weka last Instance: " + newInst);

            double pred = 0;
            if (mo == 0) {
                pred = repTreeStatic.classifyInstance(newInst);
                if (DBG) Log.d(TAG, "Weka pred static : " + pred);
                setBrightness(pred);

            } else if (mo == 1) {
                pred = repTreeUserSpecific.classifyInstance(newInst);
                if (DBG) Log.d(TAG, "Weka pred user : " + pred);
                setBrightness(pred);
            }

        } catch (Exception e) {
            Log.d(TAG, "Weka , error: " + e + " " + mo);

        }
    }


    private double readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {
            }

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
            cpu_usage = (double) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

            Log.d(TAG, "CPU INFO: ");
            mLogger.logStringEntry("CPU_Usage: " + cpu_usage);
            return cpu_usage;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0.0;
    }

    private boolean createAndReadUserSpecificModel() {
        synchronized (mLogLock) {
            try {

                ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(Definitions.US_MODEL);

                if (DBG) Log.d(TAG, "weka create user model rev: " + dataSource.getRevision());
                Instances dataUS = dataSource.getDataSet();

                dataUS.setClassIndex(dataUS.numAttributes() - 1);

                if (DBG) Log.d(TAG, "Weka create user Model11: " + dataUS.numInstances());

                repTreeUserSpecific = new REPTree();
                repTreeUserSpecific.buildClassifier(dataUS);

                if (DBG) Log.d(TAG, "Weka create user Model12: ");

                Evaluation eTest = new Evaluation(dataUS);
                eTest.evaluateModel(repTreeUserSpecific, dataUS);

                if (DBG) Log.d(TAG, "Weka createModel13: " + repTreeUserSpecific.getMinNum());

                if (DBG) Log.d(TAG, "Results: " + eTest.correct());
                userModelIsCreated = true;

                return true;
            } catch (Exception e) {
                Log.e(TAG, "Weka there is an error in model: " + e);
                mLogger.logStringEntry("Error on creating user model: " + e);
                return false;
            }
        }
    }

    public boolean change_brightness() throws Settings.SettingNotFoundException {
        //put this in case 0 to check vs screen brightness and change bool there
        check_brt = mScreenBrightness;
        if (Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS) != check_brt) {
            check_brt = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
            return true;
        } else
            return false;
    }

    private void askForSatisfaction() {

        final ArrayList mSatisfactionItems = new ArrayList();
        final ArrayList mScreenItems = new ArrayList();

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle(R.string.Satisfaction_Title)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.satisfactions, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSatisfactionItems.add(which);
                                    if (DBG) Log.d(TAG, "User satisfactions: " + which);
                                } else if (mSatisfactionItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSatisfactionItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        for (int i = 0; i < mSatisfactionItems.size(); i++) {
                            if (DBG) Log.d(TAG, "User satisfactions: " + mSatisfactionItems.get(i));
                            mLogger.logStringEntry("Satis: " + mSatisfactionItems.get(i));
                        }
                        if (mSatisfactionItems.size() == 0) {
                            mLogger.logStringEntry("Satis: " + 0);
                        }
                        screen_clicked = false;
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });


        AlertDialog dialog = builder.create();
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        dialog.getWindow().setType(LAYOUT_FLAG);
        // dialog.getWindow().getAttributes().privateFlags |= WindowManager.LayoutParams.PRIVATE_FLAG_SHOW_FOR_ALL_USERS;
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        if (DBG) Log.d(TAG, "Created a AlertBuilder");
    }

    public void build_model() throws Settings.SettingNotFoundException {
        //Loop doesnt work!! Look at sensor handler
        if (printForegroundTask().equals("com.google.android.youtube")) {
            if (change_brightness()) {
                askForSatisfaction();
                for (int i = 0; i < 2; i++) {
                    curr_time = System.currentTimeMillis();
                    while (System.currentTimeMillis() - curr_time < 6000) {
                        write = false;
                    }
                    curr_time = System.currentTimeMillis();
                    while (System.currentTimeMillis() - curr_time < 3000) {
                        write = true;
                    }
                }
            }
        }

    }

    @SuppressWarnings("ResourceType")
    private String printForegroundTask() {
        String currentApp = "NULL";
        System.out.println("first");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            System.out.println("second");
            if (appList == null)
                System.out.println("applist is null");
            if (appList.size() == 0) {
                // System.out.println(appList.get(0));
                System.out.println("applist size is 0");
            }
            if (appList != null && appList.size() > 0) {
                System.out.println("second--");
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                System.out.println("????");
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            System.out.println("third");
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        Log.e("adapter", "Current App in foreground is: " + currentApp);
        return currentApp;
    }

    private boolean getFromServer() throws FileNotFoundException {

        //     final BufferedReader finalDataInputStream =dataInputStreamLog;
        //try {
        long data = 200000;
        mStorageRefUS = FirebaseStorage.getInstance().getReferenceFromUrl("gs://new-project-5973e.appspot.com/Arff.log/563b9224-3bbd-47cb-893a-3986f82f2274/upload.arff.txt");
        File download = new File(getFilesDir(),"screenStudyUsModel.arff");
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        final StorageReference store = mStorageRefUS.child("/data/data/wearpower.Wearpower/files/screenStudyUsModel.arff");
      //  getExternalFilesDir()
        mStorageRefUS.getFile(download);

        return true;
    }


    @Override
    public void onSuccess(boolean flag) {
        if (flag) {
            bool = true;
        }
        else {
            bool = false;
        }
    }

    private void sendToServer(final FirebaseListener listener) throws FileNotFoundException {

        //     final BufferedReader finalDataInputStream =dataInputStreamLog;
        //try {
        Random rand = new Random();
        float float_random = rand.nextFloat();
        //Uri file = Uri.fromFile(new File("/data/data/wearpower.displayfilter2/files/AllLogs.log"));
        InputStream stream = new FileInputStream(new File("/data/data/wearpower.Wearpower/files/screenStudy.arff"));
        //StorageReference storageRef;
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-HH-mm-ss");
        String current_time = dateFormat.format(date);
        String[] parts = current_time.split("-");
        final StorageReference log_store = mStorageRef.child("Arff.log").child(UUID).child(UUID+current_time);
        metadata = new StorageMetadata.Builder()
                .setContentType("text/plain")
                .setCustomMetadata("timestamp", current_time)
                .build();
       // System.out.println(log_store.getMetadata());
        UploadTask uploadTask = log_store.putStream(stream,metadata);
        uploadTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                listener.onSuccess(true);
            }
        });

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onSuccess(false);
            }
        });

        //log_store.updateMetadata(metadata);
    /*    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("///////////////////////////////");
                // Get a URL to the uploaded content
                Task<Uri> downloadUrl = log_store.getDownloadUrl();

            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                // ...
                // return;
            }
        });

     */
       // return val;
    }

        /*
            Intent intent;
            Boolean outcome = false;
            String note = "";
            String current_version = "";

            if (haveNetworkConnection()) {
                if(DBG) Log.d(TAG, "connectivity services checking");
                if(DBG) Log.i(TAG, " There is network");

                // get unique device id from context in service class and pass it throught the as task
                SharedPreferences pref = this.getSharedPreferences(Definitions.DEVICE_UNIQUE_ID, Context.MODE_PRIVATE);
                String uni_id = String.valueOf(pref.getInt(Definitions.DEVICE_ID, 0));


                int counter = 0;

                mLogger.prepareForUpload(ServiceClassPhone.this);
                new Task().execute(Definitions.file_name_upload_gz, Definitions.UPLOAD_URL, uni_id);


                //new AsyncTask().execute(Definitions.file_name_upload_gz, Definitions.UPLOAD_URL, uni_id);

                if (DBG) {
                    Log.v(TAG, "Done with file upload.");
                }

                // Broadcast FILE_UPLOAD_RESULT intent of the outcome and time stamp if it worked
                Long time = System.currentTimeMillis();
                if (outcome == true) {
                    writeSuccessfulUploadFile(time);
                }
                intent = new Intent(Definitions.ACTION_FILE_UPLOAD_RESULT);

                intent.putExtra(INTENT_EXTRA_OUTCOME, (Boolean) outcome);
                intent.putExtra(INTENT_EXTRA_TIMESTAMP, (Long) time);
                intent.putExtra(INTENT_EXTRA_NOTE, (String) note);
                intent.putExtra(INTENT_EXTRA_VERSION, (String) current_version);
                sendBroadcast(intent);

                return true;
            } else {
                return false;
            }

         */

    //     } catch (Exception e) {
    //         Log.d(TAG , "ERROR in file sending: " + e);
    //     }
    //      return false;


    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = java.util.UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }

    public void whitelist(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
               // intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
               // intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                //Intent intent = new Intent(getApplicationContext(), Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);

           // Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
            //startActivityForResult(intent, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
        }
    }


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void writeSuccessfulUploadFile(long time) {
        synchronized (mFileUploadLock) {
            SharedPreferences mSP = getSharedPreferences(
                    SHARED_PREFS_NAME,
                    Context.MODE_WORLD_READABLE);
            SharedPreferences.Editor mEditor = mSP.edit();
            mEditor.putLong(LASTUPLOAD_SP_KEY, time);

            if (!mEditor.commit()) {
                Log.e(TAG, "Could not commit upload time!");
            }
        }
    }


    private void startSensorListeners() {
        try {
            Log.d(TAG, "startSensorListeners");

            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);

            Log.d(TAG, "Sensor started: " + mSensorManager);

        } catch (Exception e) {
            if (DBG) Log.d(TAG, "ERROR in sensor registering: " + e);
        }
    }

    private void stopSensorListeners() {
        try {
            Log.d(TAG, "stopSensorListeners");
            mSensorManager.unregisterListener(ServiceClassPhone.this);
            Log.d(TAG, "Sensor stoppped: " + mSensorManager);
//        wakeLock.release();

        } catch (Exception e) {
            if (DBG) Log.d(TAG, "ERROR in sensor unregistering: " + e);
        }
    }


    @Override
    // in each sensor changes this override function keeps the everything avout the sensor. And we get 2 of these info
    public void onSensorChanged(SensorEvent event) {
        String key = event.sensor.getName();
        double values = event.values[0];
        Sensor sensor = event.sensor;

        try {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accellerometerEvent++;                  // this is event counts when sensor changes, this counts all the events changings. It shouldnt be necessary though
                accellerometerSensor = values + " " + event.values[1] + " " + event.values[2] + " " + accellerometerEvent; // This is the string for value and seen event count.
                //   accel_x=accel_x+values;
                //   accel_y=accel_y+event.values[1];
                //   accel_z=accel_z+event.values[2];
                accell_phone_x = values;
                accell_phone_y = event.values[1];
                accell_phone_z = event.values[2];
                //if (DBG) Log.d(TAG , "Sensors accel: " + accell_phone_x+" " +accell_phone_y);
            }
            if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                gyroscopeEvent++;                  // this is event counts when sensor changes, this counts all the events changings. It shouldnt be necessary though
                gyroscopeSensor = values + " " + event.values[1] + " " + event.values[2] + " " + gyroscopeEvent; // This is the string for value and seen event count.
                //   accel_x=accel_x+values;
                //   accel_y=accel_y+event.values[1];
                //   accel_z=accel_z+event.values[2];
                gyro_phone_x = values;
                gyro_phone_y = event.values[1];
                gyro_phone_z = event.values[2];
                //if (DBG) Log.d(TAG , "Sensors accel: " + accell_phone_x+" " +accell_phone_y);
            }
            if (sensor.getType() == Sensor.TYPE_PRESSURE) {
                pressureSensor = String.valueOf(values);
                pressure = values;
                //pressure = 0;
                if (pressure>0) pressure_available = true;//
                else pressure_available = false;
                //if(DBG) Log.d(TAG , "Pressure available: " + pressure_available);

            }

            if (sensor.getType() == Sensor.TYPE_LIGHT) {
                ambientSensor = String.valueOf(values);
                ambient = values;
                //    pres=pres+values;
            }


        } catch (Exception e) {
            Log.d(TAG, "Error in sensor reading");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnectionFailed: " + i);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnectionFailed: " + connectionResult);
        }
    }


    public class LocalBinder extends Binder {
        ServiceClassPhone getService() {
            // Return this instance of LocalService so clients can call public methods
            return ServiceClassPhone.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


}



