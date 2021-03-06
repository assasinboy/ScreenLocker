package wearpower.Wearpower;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;

/**
 * Created by emir on 5/25/17.
 */

public class TouchReading {

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
    private static Logger mLogger = new Logger();
    private Process process;
    Vibrator v;

    public TouchReading(Context c){
        v = (Vibrator) c.getSystemService(c.VIBRATOR_SERVICE);

    }
    private boolean mFilesTested;
    private boolean mFilesExist;


    // Version 2: active su session
    private Process mProcess;
    // Byte Writer
    private OutputStream mStdIn;
    private DataOutputStream outputStream;
    private BufferedReader br;
    // Char Writer
    //private OutputStreamWriter mStdIn;
    {
        try {
            // mProcess = Runtime.getRuntime().exec("sh");
            mProcess = Runtime.getRuntime().exec("sh");
            // Byte Writer
           // mStdIn = mProcess.getOutputStream();
            // Char Writer
            //mStdIn = new OutputStreamWriter(mProcess.getOutputStream());
            outputStream = new DataOutputStream(mProcess.getOutputStream());
            String comm1 = "getevent -l";
            String comm2 = "logcat -c";
            String close = "^C";
            String newLine= "\n";

            outputStream.writeBytes(comm1);
            outputStream.writeBytes(newLine);
           // outputStream.writeBytes(close);
           // outputStream.writeBytes(newLine);
           // outputStream.flush();

          //  mStdIn.write(comm1);
           // mStdIn.flush();

            Log.d(TAG,"touchLogs: " + "su has been started and flushed");
            //   mReader = new InputStreamReader(mProcess.getInputStream());
            //  mErrorReader = new InputStreamReader(mProcess.getErrorStream());
        } catch (IOException e) {
            Log.d(TAG, "Could not spawn su process");
            e.printStackTrace();
        }
    }


/*
    {
        try {
            // mProcess = Runtime.getRuntime().exec("sh");
            mProcess = Runtime.getRuntime().exec("su");
            // Byte Writer
            mStdIn = mProcess.getOutputStream();
            // Char Writer
            //mStdIn = new OutputStreamWriter(mProcess.getOutputStream());
            mReader = new InputStreamReader(mProcess.getInputStream());
            mErrorReader = new InputStreamReader(mProcess.getErrorStream());
        } catch (IOException e) {
            Log.d(TAG, "Could not spawn su process");
            e.printStackTrace();
        }
    }

*/


    public  void readTouchsAvgLogs(){
        // Process logcat;


            try {

                /*
                String comm1 = "getevent -l";
                String comm2 = "logcat -c";
                String close = "^C";
                String newLine= "\n";

                outputStream.writeBytes(comm1);
                outputStream.writeBytes(newLine);
                outputStream.writeBytes(close);
                outputStream.writeBytes(newLine);
               */
              //  outputStream.flush();


                String comm1 = "getevent -l";
                String comm2 = "logcat -c";
                String close = "^C";
                byte[] newLine= "\n".getBytes();

              //  outputStream.writeBytes(comm1);
              //  outputStream.writeBytes(newLine);
                 outputStream.writeBytes(close);
                // outputStream.writeBytes(newLine);
                 outputStream.flush();

             //   mStdIn.write(close);
                //mStdIn.write(newLine);
             //   mStdIn.flush();


                br = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
                boolean cont=true;
                String line;
                Log.d(TAG,"touchLogs BufferedReader for touch reading... " );
                if (br!=null ) {
                    Log.d(TAG,"touchLogs BufferedReader is not null checking for readiness... ");
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


                Log.d(TAG,"process is null: ");


            } catch (Exception d) {
                Log.d(TAG, "Error occured: " + d);
                d.printStackTrace();

            }

    }

    private void logTouchData(){
        double[] touchStats = new double[6];
        touchStats = getTouchData();
        mLogger.logStringEntry("Touches: "+touchStats[0] +" " +touchStats[1] +" " +touchStats[2] +" " +touchStats[3]
                +" " +touchStats[4]+" " +touchStats[5]);

    }



    public double[] getTouchData(){
        synchronized (mLogLock) {
        // return values are:
        // min,max,avg touch size from major, minor values (3)
        // x and y pixels travelled (2) --> swipe or not can be extracted from here as well! I can add it later!
        // touch finger count (1)
        double[] returnValues = new double[6];

        long avg_touch_major, avg_touch_minor, max_touch_major,max_touch_minor, min_touch_major, min_touch_minor, sum_major,sum_minor ;
        avg_touch_major = avg_touch_minor = max_touch_major= max_touch_minor =  min_touch_major = min_touch_minor=sum_major=sum_minor =0;
        long touch_x_tra, touch_y_tra , sum_x, sum_y;
        touch_x_tra = touch_y_tra = sum_x = sum_y =0;

       // if (DBG)Log.d(TAG, "framePerSec log length is: " + touch_log.size());


        // touch_major avg, min, max
        if (touch_major.size()>0){
            min_touch_major=touch_major.get(0);
        }
        for (int i = 0; i < touch_major.size(); i++) {
            sum_major+=touch_major.get(i);
            if (touch_major.get(i)>max_touch_major){
                max_touch_major=touch_major.get(i);
            }
            if (touch_major.get(i)<min_touch_major){
                min_touch_major=touch_major.get(i);
            }
        }
         if (touch_major.size()>0) {
             avg_touch_major = sum_major / touch_major.size();
         }


            // touch_minor avg, min, max
        if (touch_minor.size()>0){
            min_touch_major=touch_minor.get(0);
        }
        for (int i = 0; i < touch_minor.size(); i++) {
            sum_minor+=touch_minor.get(i);
            if (touch_minor.get(i)>max_touch_minor){
                max_touch_minor=touch_minor.get(i);
            }
            if (touch_minor.get(i)<min_touch_minor){
                min_touch_minor=touch_minor.get(i);
            }
        }
        if (touch_minor.size()>0) {
            avg_touch_minor = sum_minor / touch_minor.size();
        }

        if (DBG) Log.d(TAG,"touches avg minor/major : " + avg_touch_minor + " " + avg_touch_major );

        // x and y travelled
        for (int i = 1; i < touch_x.size(); i++) {
            long diff = touch_x.get(i)-touch_x.get(i-1);
            if (diff<0)  diff=-diff;
            sum_x+=diff;
        }
        // x and y travelled
        for (int i = 1; i < touch_y.size(); i++) {
            long diff = touch_y.get(i)-touch_y.get(i-1);
            if (diff<0)  diff=-diff;
            sum_y+=diff;
        }



        // calculating the size
        if (touch_major.size()==0){
            avg_touch_major=avg_touch_minor;
            max_touch_major=max_touch_minor;
            min_touch_major=min_touch_minor;
        }
        if (touch_minor.size()==0){
            avg_touch_minor=avg_touch_major;
            max_touch_minor=max_touch_major;
            min_touch_minor=min_touch_major;
        }
        double avg_size = (3.14 * avg_touch_minor * avg_touch_major) / 4;
        double max_size = (3.14 * max_touch_minor * max_touch_major) / 4;
        double min_size = (3.14 * min_touch_minor * min_touch_major) / 4;


        returnValues[0]= avg_size ;
        returnValues[1]=max_size;
        returnValues[2]=min_size;
        returnValues[3]=sum_x;
        returnValues[4]=sum_y;
        returnValues[5]=fingerCount;
        if (DBG) Log.d(TAG,"touches Returning as array: " + returnValues[0] +
                " " + returnValues[1] + " " + returnValues[2] + " " + returnValues[3]);

        touch_x.clear();
        touch_y.clear();
        touch_major.clear();
        touch_minor.clear();
        fingerCount=0;

        return returnValues;
    }


    }
}
