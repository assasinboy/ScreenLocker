package wearpower.screenlocker;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;


/**
 * Created by emir on 1/28/18.
 */

public class DetectedActivitiesIntentService extends IntentService {

    private static final boolean DGB = Definitions.DBG;
    private final static String TAG = "DetectedActivitiesIS";
    private static final String ACTIVITIES="Activities";

    private static Logger mLogger = new Logger();

    private Context context;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param
     */
    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
        //   this.context=c;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            Intent localIntent = new Intent("BROADCAST_ACTION");

            // Get the list of the probable activities associated with the current state of the
            // device. Each activity is associated with a confidence level, which is an int between
            // 0 and 100.
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

            String mostActivity = getActivityString(getApplicationContext(), result.getMostProbableActivity().getType());

            String act = "";
            String mostAct = "";

            //  DataHolder.getInstance().setActivity(mostActivity);


            boolean determined = false;

            // Log each activity.
            if (DGB) Log.i(TAG, "activities detected");
            for (DetectedActivity da : detectedActivities) {

                act = getActivityString(getApplicationContext(), da.getType());
                int confCurAct = da.getConfidence();

                if (DGB) Log.i(TAG, act + " " + confCurAct + "%");

                if (mostActivity.contains("Foot") || mostActivity.contains("foot") || mostActivity.contains("known")) {
                    if (act.contains("Walking") || act.contains("walking")) {
                        if (confCurAct > 40) mostActivity = act;
                    } else if (act.contains("Running") || act.contains("running")) {
                        if (confCurAct > 40) mostActivity = act;
                    } else if (act.contains("Bicycle") || act.contains("bicycle")) {
                        if (confCurAct > 40) mostActivity = act;
                    }
                }

            }

            if (mostActivity!=null && !mostActivity.equalsIgnoreCase(null))
                mLogger.logStringEntry("Cur_activ: " + mostActivity);

            if (DGB) Log.i(TAG, "activities detected the most: " + mostActivity);
        }catch (Exception e){
            Log.d(TAG, "Error in Activity Recog: " + e);
        }

    }

    public String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity);
        }
    }
}
