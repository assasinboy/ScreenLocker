package wearpower.screenlocker;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created by emir on 8/24/17.
 */

public class TouchRecorder {



    final private static boolean DBG = true;
    final StringBuilder log =new StringBuilder();
    private static final String TAG = "TouchReading";


    private static Vector<Double> touch_log = new Vector<Double>();
    private static Vector<String> touch_name = new Vector<String>();
    private static Vector<Double> touch_value = new Vector<Double>();

    private static Vector<Long> touch_x = new Vector<Long>();
    private static Vector<Long> touch_y = new Vector<Long>();

    private static Vector<Long> touch_major = new Vector<Long>();
    private static Vector<Long> touch_minor = new Vector<Long>();

    private int fingerCount=0;

    private boolean startingAgain=false;
    final private static Object mLogLock = new Object();
    private Process process;

    private double touch1;
    private double touch2;
    private double touch3;
    private double touch4;
    private double touch5;
    private double touch6;

    private double heart_rate;


   // public double[] voices = new double[20];
    public int voice_counter=0;
    public double voice_sum=0;

    private static Logger mLogger = new Logger();

    private TouchRecorder mRecorder = null;
    Timer timer;

    // Version 2: active su session
    private Process mProcess;
    // Byte Writer
    private OutputStream mStdIn;
    private DataOutputStream outputStream;
    private BufferedReader br;


    {
        try {

            mProcess = Runtime.getRuntime().exec("su");
           // mProcess = Runtime.getRuntime().exec(new String[]{"getevent", "-l"});

            Log.d(TAG, "Touch process is: " + mProcess);
            outputStream = new DataOutputStream(mProcess.getOutputStream());

            String comm1 = "getevent "+ "-l";
            String comm2 = "logcat -c";
            String close = "^C";
            String newLine= "\n";


            outputStream.writeBytes(comm1);
            outputStream.writeBytes(newLine);

            Log.d(TAG,"touchLogs: " + "sh has been started and flushed");
            //   mReader = new InputStreamReader(mProcess.getInputStream());
            //  mErrorReader = new InputStreamReader(mProcess.getErrorStream());


        } catch (IOException e) {
            if (DBG) Log.d(TAG, "Could not spawn su process");
            e.printStackTrace();
        }
    }


    public void start()  {

            try {
                timer = new Timer();
                timer.scheduleAtFixedRate(new TouchRecorder.RecorderTask(), 0, 1000);
            } catch (Exception e) {
                Log.d(TAG, "Exception: " + e);
            }

    }

    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder = null;
            timer.cancel();
        }
    }



    public void logTouchData(){
        double[] touchStats = new double[6];
        touchStats = getTouchData();

        mLogger.logStringEntry("Touches: "+touchStats[0] +" " +touchStats[1] +" " +touchStats[2] +" " +touchStats[3]
                +" " +touchStats[4]+" " +touchStats[5]);
        Log.d(TAG,"TouchLogs were written: " +"Touches: "+touchStats[0] +" " +touchStats[1] +" " +touchStats[2] +" " +touchStats[3]
                +" " +touchStats[4]+" " +touchStats[5] );

        TouchRecorder.getInstance().setTouch1(touchStats[0]);
        TouchRecorder.getInstance().setTouch2(touchStats[1]);
        TouchRecorder.getInstance().setTouch3(touchStats[2]);
        TouchRecorder.getInstance().setTouch4(touchStats[3]);
        TouchRecorder.getInstance().setTouch5(touchStats[4]);
        TouchRecorder.getInstance().setTouch6(touchStats[5]);

    }





    public double[] getTouchData() {
        synchronized (mLogLock) {
            // return values are:
            // min,max,avg touch size from major, minor values (3)
            // x and y pixels travelled (2) --> swipe or not can be extracted from here as well! I can add it later!
            // touch finger count (1)
            double[] returnValues = new double[6];

            long avg_touch_major, avg_touch_minor, max_touch_major, max_touch_minor, min_touch_major, min_touch_minor, sum_major, sum_minor;
            avg_touch_major = avg_touch_minor = max_touch_major = max_touch_minor = min_touch_major = min_touch_minor = sum_major = sum_minor = 0;
            long touch_x_tra, touch_y_tra, sum_x, sum_y;
            touch_x_tra = touch_y_tra = sum_x = sum_y = 0;

            // if (DBG)Log.d(TAG, "framePerSec log length is: " + touch_log.size());


            // touch_major avg, min, max
            if (touch_major.size() > 0) {
                min_touch_major = touch_major.get(0);
            }
            for (int i = 0; i < touch_major.size(); i++) {
                sum_major += touch_major.get(i);
                if (touch_major.get(i) > max_touch_major) {
                    max_touch_major = touch_major.get(i);
                }
                if (touch_major.get(i) < min_touch_major) {
                    min_touch_major = touch_major.get(i);
                }
            }
            if (touch_major.size() > 0) {
                avg_touch_major = sum_major / touch_major.size();
            }


            // touch_minor avg, min, max
            if (touch_minor.size() > 0) {
                min_touch_major = touch_minor.get(0);
            }
            for (int i = 0; i < touch_minor.size(); i++) {
                sum_minor += touch_minor.get(i);
                if (touch_minor.get(i) > max_touch_minor) {
                    max_touch_minor = touch_minor.get(i);
                }
                if (touch_minor.get(i) < min_touch_minor) {
                    min_touch_minor = touch_minor.get(i);
                }
            }
            if (touch_minor.size() > 0) {
                avg_touch_minor = sum_minor / touch_minor.size();
            }

            if (DBG)
                Log.d(TAG, "touches avg minor/major : " + avg_touch_minor + " " + avg_touch_major);

            // x and y travelled
            for (int i = 1; i < touch_x.size(); i++) {
                long diff = touch_x.get(i) - touch_x.get(i - 1);
                if (diff < 0) diff = -diff;
                sum_x += diff;
            }
            // x and y travelled
            for (int i = 1; i < touch_y.size(); i++) {
                long diff = touch_y.get(i) - touch_y.get(i - 1);
                if (diff < 0) diff = -diff;
                sum_y += diff;
            }


            // calculating the size
            if (touch_major.size() == 0) {
                avg_touch_major = avg_touch_minor;
                max_touch_major = max_touch_minor;
                min_touch_major = min_touch_minor;
            }
            if (touch_minor.size() == 0) {
                avg_touch_minor = avg_touch_major;
                max_touch_minor = max_touch_major;
                min_touch_minor = min_touch_major;
            }
            double avg_size = (3.14 * avg_touch_minor * avg_touch_major) / 4;
            double max_size = (3.14 * max_touch_minor * max_touch_major) / 4;
            double min_size = (3.14 * min_touch_minor * min_touch_major) / 4;


            returnValues[0] = avg_size;
            returnValues[1] = max_size;
            returnValues[2] = min_size;
            returnValues[3] = sum_x;
            returnValues[4] = sum_y;
            returnValues[5] = fingerCount;

            if (DBG) Log.d(TAG, "touches Returning as array: " + returnValues[0] +
                    " " + returnValues[1] + " " + returnValues[2] + " " + returnValues[3]);

            touch_x.clear();
            touch_y.clear();
            touch_major.clear();
            touch_minor.clear();
            fingerCount = 0;

            return returnValues;
        }
    }

    public void setTouch1(double value){
        this.touch1=value;
    }
    public double getTouch1(){
        return touch1;
    }

    public void setTouch2(double value){
        this.touch2=value;
    }
    public double getTouch2(){
        return touch2;
    }

    public void setTouch3(double value){
        this.touch3=value;
    }
    public double getTouch3(){
        return touch3;
    }

    public void setTouch4(double value){
        this.touch4=value;
    }
    public double getTouch4(){
        return touch4;
    }

    public void setTouch5(double value){
        this.touch5=value;
    }
    public double getTouch5(){
        return touch5;
    }

    public void setTouch6(double value){
        this.touch6=value;
    }
    public double getTouch6(){
        return touch6;
    }

    public void resetTouches(){
        TouchRecorder.getInstance().setTouch1(0);
        TouchRecorder.getInstance().setTouch2(0);
        TouchRecorder.getInstance().setTouch3(0);
        TouchRecorder.getInstance().setTouch4(0);
        TouchRecorder.getInstance().setTouch5(0);
        TouchRecorder.getInstance().setTouch6(0);
    }


    private static final TouchRecorder touchRecorderHolder = new TouchRecorder();
    public static TouchRecorder getInstance() {return touchRecorderHolder;}


        private class RecorderTask extends TimerTask {


        public RecorderTask() {}


        @Override
        public void run() {

            try {

                String comm1 = "getevent -l";
                String comm2 = "logcat -c";
                String close = "^C";
                byte[] newLine = "\n".getBytes();

                outputStream.writeBytes(close);
                outputStream.flush();
                // Get input and output streams
               // out = child.getOutputStream();
               // in = child.getInputStream();
                //Input stream can return anything


                br = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
                boolean cont=true;
                String line;
                Log.d(TAG,"touchLogs BufferedReader for touch reading... " );
                if (br!=null ) {
                    Log.d(TAG,"touchLogs BufferedReader is not null checking for readiness... " );
                    if (br.ready()) {
                        if(DBG) Log.d(TAG, "BufferedReader for touch is not null and ready");

                        // LIMIT the reading until you see UP or 0000000 or SYN_REPORT so that we know user touched and moved his/her finger
                        //
                        String separator = System.getProperty("line.separator");
                        while ((line = br.readLine()) != null && line.contains("event0:")
                                && !line.contains("BufferedReader") ) {
                            // extract the other components in the line, major, minor, x, y
                            String[] linesNamesValues = line.split(" +");
                            // is it the end of the touch event?
                            if (line.contains("BTN_TOOL_FINGER") && linesNamesValues[3].contains("UP")){
                                logTouchData();
                            }
                            else if (line.contains("ABS_MT_POSITION_X")){
                                Log.d(TAG, "touchLogs ABS_MT_POSITION_X: " +linesNamesValues[3]+" "+ Long.parseLong(linesNamesValues[3], 16));
                                touch_x.add(Long.parseLong(linesNamesValues[3], 16));
                            }
                            else if (line.contains("ABS_MT_POSITION_Y")){
                                Log.d(TAG, "touchLogs ABS_MT_POSITION_Y: " +linesNamesValues[3]+" "+ Long.parseLong(linesNamesValues[3], 16));
                                touch_y.add(Long.parseLong(linesNamesValues[3], 16));
                            }
                            else if (line.contains("ABS_MT_TOUCH_MAJOR")){
                                Log.d(TAG, "touchLogs ABS_MT_TOUCH_MAJOR: " +linesNamesValues[3]+" "+ Long.parseLong(linesNamesValues[3], 16));
                                touch_major.add(Long.parseLong(linesNamesValues[3], 16));
                            }
                            else if (line.contains("ABS_MT_TOUCH_MINOR")){
                                Log.d(TAG, "touchLogs ABS_MT_TOUCH_MINOR: " +linesNamesValues[3]+" "+ Long.parseLong(linesNamesValues[3], 16));
                                touch_minor.add(Long.parseLong(linesNamesValues[3], 16));
                            }
                            else if (line.contains("ABS_MT_TRACKING_ID")){
                                fingerCount=fingerCount+1;
                            }

                            // vibrate to get pressure from gyro
                            //  v.vibrate(200);
                            // Log.d(TAG, "touchLogs added: " + linesNamesValues[3]);
                        }
                    }else{
                        Log.d(TAG,"BufferedReader is not ready");
                    }

                }else{
                    Log.d(TAG,"BufferedReader is null");
                }

               // child.getInputStream().close();
               // child.getOutputStream().flush();
                Log.d(TAG,"process is null: ");


            }catch (Exception e) {
                if (DBG) Log.d(TAG, "Touch recorder error: " + e);
            }
        }
    }


}

