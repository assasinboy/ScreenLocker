package wearpower.screenlocker;


import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * Created by emir on 2/3/18.
 */

public class ScreenService extends Service implements View.OnTouchListener {

    LinearLayout mView;
    WindowManager wm ;

    final public static String TAG = "ScreenClassPhone";
    final private static boolean DBG = Definitions.DBG;

    private View mSeekBarView;


    public static int STATE;

    private static Logger mLogger = new Logger();

    public static final int INACTIVE=0;
    public static final int ACTIVE=1;

    static{
        STATE=INACTIVE;
    }

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mView = new LinearLayout(this);

        mSeekBarView = LayoutInflater.from(this).inflate(R.layout.brightness_seekbar, null);
        //mView.setOnTouchListener((View.OnTouchListener) gestureDetector);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }



        final WindowManager.LayoutParams params1 = new WindowManager.LayoutParams(
                10, 10,
               // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
               // WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);


        /*
        final WindowManager.LayoutParams params1 = new WindowManager.LayoutParams();
        params1.height = 10;
        params1.width = 10;
        params1.type = WindowManager.LayoutParams.TYPE_PHONE;
        params1.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
              //  | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
               // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        */

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
               // WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                LAYOUT_FLAG,
                0| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        params1.gravity = Gravity.LEFT | Gravity.TOP;
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        //params.x = getScreenWidth() - 50;
        //params.y = getScreenHeight() - 100 ;
        final int height_lower = getScreenHeight()-700;
        final int height_upper = 350;

        final int brightness_scale = height_lower - height_upper;


        params.x = getScreenWidth() - 50;
        params.y = getScreenHeight() - 700 ;


        if (DBG) Log.d(TAG , "screen x and y: " + params.x + " " + params.y + " " + getScreenWidth()  + " " + getScreenHeight() );

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

       // wm.addView(mView, params1);
        wm.addView(mView, params1);
        wm.addView(mSeekBarView, params);
        //STATE = ACTIVE;


        mView.setOnTouchListener(this);


      //  final ImageView brightness_seekbar = (ImageView) mSeekBarView.findViewById(R.id.bar_iv);
        final ImageView brightness_thumb = (ImageView) mSeekBarView.findViewById(R.id.thumb_iv);


        brightness_thumb.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (DBG) Log.d(TAG, "screen_bar touch pos: " + params.x + " " + params.y);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            //Open the chat conversation click.
                            Intent intent = new Intent(ScreenService.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            //close the service and remove the chat heads
                            stopSelf();
                        }
                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (DBG) Log.d(TAG, "screen_bar move orientation " + getResources().getConfiguration().orientation);
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        int height_lower_loc = height_lower;
                        int height_upper_loc = height_upper;
                        int brightness_scale_loc = brightness_scale;

                        if (getResources().getConfiguration().orientation ==2 ){
                            Log.d(TAG , "screen move params y Land: " + params.y + " " + getScreenWidth() + " " + getScreenHeight());
                            height_lower_loc = getScreenHeight()-50;
                            height_upper_loc = 50;
                            brightness_scale_loc = height_lower_loc - height_upper_loc;
                        }

                        if (params.y > height_lower_loc)  params.y = height_lower_loc;
                        if (params.y < height_upper_loc)  params.y = height_upper_loc;

                        try {
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) ((height_lower_loc - params.y) * 255 / brightness_scale_loc));

                        }catch (Exception e){
                            Log.d(TAG , "Error in screen brightness setting: " + e);
                        }
                        mLogger.logStringEntry("UserScr: " + (height_lower_loc-params.y)*255/brightness_scale_loc);
                        DataHolder.getInstance().setUserInput(1);
                        DataHolder.getInstance().setUserInputTime(System.currentTimeMillis());


                        //Update the layout with new X & Y coordinate
                        wm.updateViewLayout(mSeekBarView, params);
                        lastAction = event.getAction();
                        return true;
                }
                return false;
            }
        });


    }



    public  int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public  int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
      //  STATE=INACTIVE;
        if(mView!=null){
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.removeView(mView);
            wm.removeView(mSeekBarView);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (DBG) Log.d(TAG , "Screen touch: " + event.getRawX() + " " + event.getRawY() + " " +"Action down");
                mLogger.logStringEntry("ToucF: " + event.getRawX() + " " + event.getRawY()  );
                return true;
            case MotionEvent.ACTION_UP:
                if (DBG) Log.d(TAG , "Screen touch: " + event.getRawX() + " " + event.getRawY() + " " +"Action up");
                mLogger.logStringEntry("ToucF: " + event.getRawX() + " " + event.getRawY()  );
                return true;
            case MotionEvent.ACTION_MOVE:
                if (DBG) Log.d(TAG , "Screen touch: " + event.getRawX() + " " + event.getRawY() + " " +"Action move");
                mLogger.logStringEntry("ToucF: " + event.getRawX() + " " + event.getRawY()  );
                return true;
            case MotionEvent.ACTION_OUTSIDE:
                if (DBG) Log.d(TAG , "Screen touch: " + event.getRawX() + " " + event.getRawY() + " " +"Action move");
                mLogger.logStringEntry("ToucF: " + event.getRawX() + " " + event.getRawY()  );
                return true;
        }

        return false;
    }



}
