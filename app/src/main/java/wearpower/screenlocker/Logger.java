package wearpower.screenlocker;

/**
 * Created by emir on 9/15/15.
 */

import android.content.Context;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class Logger extends LogListener {


    final private static boolean DBG = true;
    final static public String TAG = "Logger";

    private static final String LOG_FILE_NAME = "AllLogs.log";
    private static final String UPLOAD_FILE_NAME = "AllLogsUpload.log";
    final static public String UPLOAD_FILE_NAME_GZ = "AllLogsUpload.log.gz";

    private static final String ARFF_FILE_NAME_US_MODEL = "screenStudyUsModel.arff";
    private static final String ARFF_FILE_NAME = "screenStudy.arff";



    private static final String HEART_FILE_NAME = "HeartRateLogs.log";
    private static final String HEART_UPLOAD_FILE_NAME = "HeartRateUpLogs.log";

    private static final String STEPS_FILE_NAME = "StepsLogs.log";
    private static final String STEPS_UPLOAD_FILE_NAME = "StepsUpLogs.log";

    private static final String CALORIES_FILE_NAME = "CaloriesLogs.log";
    private static final String CALORIES_UPLOAD_FILE_NAME = "CaloriesUpLogs.log";

    private static final String USER_SPECIFIC_MODEL = "user_specific_model.model";


    private static final String MODEL_FILE = "model.log";
    private static final String DAY_FILE = "day.log";

    private static final String LOG_FILE_NAME_PHONE = "CpuConfLogs2";

    private static FileOutputStream mOutputStreamModel = null;

    private static FileOutputStream mOutputStream = null;
    private static FileOutputStream mOutputStreamArff = null;
    private static FileOutputStream mOutputStreamArffUsModel = null;
    private static FileOutputStream mOutputStreamDay = null;

    private static FileOutputStream mOutputStreamHeart = null;
    private static FileOutputStream mOutputStreamSteps = null;
    private static FileOutputStream mOutputStreamCal = null;
    private static FileOutputStream mOutputStreamLog = null;





    final private static Object mLogLock = new Object();

    private static final byte[] SPACE  = " ".getBytes();
    private static final byte[] NEWLINE= "\n".getBytes();
    private static final byte[] COMMA= ",".getBytes();


    public static void createLogFile(Context c) {
        synchronized (mLogLock) {
            try {
                mOutputStreamLog = c.openFileOutput(LOG_FILE_NAME, Context.MODE_APPEND);
            } catch (Exception e) {
                Log.e(TAG, "Can't open file " + LOG_FILE_NAME + ":" + e);

            }
        }
    }



    public static void createArffFile(Context c) {
        synchronized (mLogLock) {
            try {
                mOutputStreamArff = c.openFileOutput(ARFF_FILE_NAME, Context.MODE_APPEND);
            } catch (Exception e) {
                Log.e(TAG, "Can't open file " + ARFF_FILE_NAME + ":" + e);

            }
        }
    }

    public static void createArffFileUsModel(Context c) {
        synchronized (mLogLock) {
            try {
                mOutputStreamArffUsModel = c.openFileOutput(ARFF_FILE_NAME_US_MODEL, Context.MODE_APPEND);
            } catch (Exception e) {
                Log.e(TAG, "Can't open file " + ARFF_FILE_NAME_US_MODEL + ":" + e);

            }
        }
    }

    public static void createModelFile(Context c) {
        synchronized (mLogLock) {
            try {
                mOutputStreamModel = c.openFileOutput(MODEL_FILE, Context.MODE_APPEND);
            } catch (Exception e) {
                Log.e(TAG, "Can't open file " + MODEL_FILE + ":" + e);

            }
        }
    }

    public static void createDayFile(Context c) {
        synchronized (mLogLock) {
            try {
                mOutputStreamDay = c.openFileOutput(DAY_FILE, Context.MODE_APPEND);
            } catch (Exception e) {
                Log.e(TAG, "Can't open file " + DAY_FILE + ":" + e);

            }
        }
    }


    public void dayLogEntry(String s){
        try {
            mOutputStreamDay.write(s.getBytes());
            mOutputStreamDay.write(NEWLINE);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }

/*
    public static void createLogFileToUpload(Context c) {
        synchronized (mLogLock) {
            try {
                mOutputStreamPhone = c.openFileOutput(LOG_FILE_NAME_PHONE, Context.MODE_APPEND);
            } catch (Exception e) {
                Log.e(TAG, "Can't open file " + LOG_FILE_NAME_PHONE + ":" + e);

            }
        }
    }
*/


    @Override
    public void logStringStringEntry(String name, String value ) {
        synchronized (mLogLock) {
            try {

                //   Calendar c = Calendar.getInstance();
                //   SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                //   String formattedDate = df.format(c.getTime());

                mOutputStreamLog.write(String.valueOf(System.currentTimeMillis()).getBytes());
                mOutputStreamLog.write("-".getBytes());
                mOutputStreamLog.write(name.getBytes());
                mOutputStreamLog.write("-".getBytes());
                mOutputStreamLog.write(value.getBytes());
                mOutputStreamLog.write(NEWLINE);
            } catch (IOException ioe) {
                Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
            }
        }
    }


    @Override
    public void logStringEntry(String name) {
        synchronized (mLogLock) {
            try {
                if (name!=null || !name.equalsIgnoreCase(null)) {

                    //   Calendar c = Calendar.getInstance();
                    //   SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                    //   String formattedDate = df.format(c.getTime());

                    mOutputStreamLog.write(String.valueOf(System.currentTimeMillis()).getBytes());
                    mOutputStreamLog.write("-".getBytes());
                    mOutputStreamLog.write(name.getBytes());
                    mOutputStreamLog.write(NEWLINE);
                }
            } catch (IOException ioe) {
                Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
            }
        }
    }





    public static void InitiateArffFile(String[] s) { // feature list in arff format
        synchronized (mLogLock) {
            try {
                mOutputStreamArff.write("@relation".getBytes());
                mOutputStreamArff.write(SPACE);
                mOutputStreamArff.write("userLogArff".getBytes());
                mOutputStreamArff.write(NEWLINE);
                mOutputStreamArff.write(NEWLINE);

                for (int i = 0; i < s.length; i++) {  // for all attributes

                    if (s[i].equalsIgnoreCase("day_night")){ // cpuconfs in string
                        mOutputStreamArff.write("@attribute".getBytes());
                        mOutputStreamArff.write(SPACE);
                        mOutputStreamArff.write(s[i].getBytes());
                        mOutputStreamArff.write(SPACE);
                        mOutputStreamArff.write("{morning".getBytes());
                        mOutputStreamArff.write(COMMA);
                        mOutputStreamArff.write("noon".getBytes());
                        mOutputStreamArff.write(COMMA);
                        mOutputStreamArff.write("evening".getBytes());
                        mOutputStreamArff.write(COMMA);
                        mOutputStreamArff.write("night}".getBytes());
                        mOutputStreamArff.write(NEWLINE);
                    }
                    else {

                        mOutputStreamArff.write("@attribute".getBytes());
                        mOutputStreamArff.write(SPACE);
                        mOutputStreamArff.write(s[i].getBytes());
                        mOutputStreamArff.write(SPACE);
                        mOutputStreamArff.write("numeric".getBytes());
                        mOutputStreamArff.write(NEWLINE);
                    }

                }

                mOutputStreamArff.write(NEWLINE);
                mOutputStreamArff.write("@data".getBytes());
                mOutputStreamArff.write(NEWLINE);
                mOutputStreamArff.write("-0.277727,3.801992,9.050082,0.001222,0.012217,0.008552,0,61,1,0.57625,0.57,0.59,3,night,50,5".getBytes());
                mOutputStreamArff.write(NEWLINE);
                mOutputStreamArff.write("-0.124498,3.792415,8.983045,0.002443,0.010996,0.006109,0,10,1,0.57625,0.57,0.59,3,night,50,5".getBytes());
                mOutputStreamArff.write(NEWLINE);
                mOutputStreamArff.write("-0.038307,3.754108,8.963891,0.002443,0.009774,0.004887,0,10,1,0.57625,0.57,0.59,3,night,50,5".getBytes());
                mOutputStreamArff.write(NEWLINE);


            } catch (IOException ioe) {
                Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
            }
        }
    }

    public static void InitiateArffFileUsModel(String[] s) { // feature list in arff format
        synchronized (mLogLock) {
            try {
                mOutputStreamArffUsModel.write("@relation".getBytes());
                mOutputStreamArffUsModel.write(SPACE);
                mOutputStreamArffUsModel.write("userLogUsArff".getBytes());
                mOutputStreamArffUsModel.write(NEWLINE);
                mOutputStreamArffUsModel.write(NEWLINE);

                for (int i = 0; i < s.length; i++) {  // for all attributes

                    if (s[i].equalsIgnoreCase("day_night")){ // cpuconfs in string
                        mOutputStreamArffUsModel.write("@attribute".getBytes());
                        mOutputStreamArffUsModel.write(SPACE);
                        mOutputStreamArffUsModel.write(s[i].getBytes());
                        mOutputStreamArffUsModel.write(SPACE);
                        mOutputStreamArffUsModel.write("{morning".getBytes());
                        mOutputStreamArffUsModel.write(COMMA);
                        mOutputStreamArffUsModel.write("noon".getBytes());
                        mOutputStreamArffUsModel.write(COMMA);
                        mOutputStreamArffUsModel.write("evening".getBytes());
                        mOutputStreamArffUsModel.write(COMMA);
                        mOutputStreamArffUsModel.write("night}".getBytes());
                        mOutputStreamArffUsModel.write(NEWLINE);
                    }
                    else {

                        mOutputStreamArffUsModel.write("@attribute".getBytes());
                        mOutputStreamArffUsModel.write(SPACE);
                        mOutputStreamArffUsModel.write(s[i].getBytes());
                        mOutputStreamArffUsModel.write(SPACE);
                        mOutputStreamArffUsModel.write("numeric".getBytes());
                        mOutputStreamArffUsModel.write(NEWLINE);
                    }

                }
/*
                mOutputStreamArffUsModel.write(NEWLINE);
                mOutputStreamArffUsModel.write("@data".getBytes());
                mOutputStreamArffUsModel.write(NEWLINE);
                mOutputStreamArffUsModel.write("-0.277727,3.801992,9.050082,0.001222,0.012217,0.008552,0,61,1,0.57625,0.57,0.59,3,night,50,5".getBytes());
                mOutputStreamArffUsModel.write(NEWLINE);
                mOutputStreamArffUsModel.write("-0.124498,3.792415,8.983045,0.002443,0.010996,0.006109,0,10,1,0.57625,0.57,0.59,3,night,50,5".getBytes());
                mOutputStreamArffUsModel.write(NEWLINE);
                mOutputStreamArffUsModel.write("-0.038307,3.754108,8.963891,0.002443,0.009774,0.004887,0,10,1,0.57625,0.57,0.59,3,night,50,5".getBytes());
                mOutputStreamArffUsModel.write(NEWLINE);

*/
            } catch (IOException ioe) {
                Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
            }
        }
    }



    public void modelLogEntry(String s){
        try {
            mOutputStreamModel.write(s.getBytes());
            mOutputStreamModel.write(NEWLINE);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }





    @Override
    public void heartEntry(String name ){
        try {

            mOutputStreamHeart.write(String.valueOf(System.currentTimeMillis()).getBytes());
            mOutputStreamHeart.write(":".getBytes());
            mOutputStreamHeart.write(name.getBytes());
            mOutputStreamHeart.write(NEWLINE);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }


    @Override
    public void stepEntry(String s){
        try {
            mOutputStreamSteps.write(String.valueOf(System.currentTimeMillis()).getBytes());
            mOutputStreamCal.write(":".getBytes());
            mOutputStreamSteps.write(s.getBytes());
            mOutputStreamSteps.write(NEWLINE);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }

    @Override
    public void calorieEntry(String s){
        try {
            mOutputStreamCal.write(String.valueOf(System.currentTimeMillis()).getBytes());
            mOutputStreamCal.write(":".getBytes());
            mOutputStreamCal.write(s.getBytes());
            mOutputStreamCal.write(NEWLINE);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }


    public void arffEntryLong(long s){
        try {
            mOutputStreamArff.write(ToByteString.getBytes(s));
            mOutputStreamArff.write(COMMA);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }

    public void arffEntryString(String s){
        try {
            mOutputStreamArff.write(s.getBytes());
            mOutputStreamArff.write(COMMA);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }
    public void arffEntryStringLast(String s){
        try {
            mOutputStreamArff.write(s.getBytes());
           // mOutputStreamArff.write(COMMA);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }

    public void arffEntryStringUs(String s){
        try {
            mOutputStreamArffUsModel.write(s.getBytes());
            mOutputStreamArffUsModel.write(COMMA);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }

    public void arffEntryStringLastUs(String s){
        try {
            mOutputStreamArffUsModel.write(s.getBytes());
            //mOutputStreamArffUsModel.write(COMMA);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }


    public void arffEntryLongLast(long s){
        try {
            mOutputStreamArff.write(ToByteString.getBytes(s));
            //  mOutputStreamArff.write(COMMA);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }

    public void arffEntryLongUs(long s){
        try {
            mOutputStreamArffUsModel.write(ToByteString.getBytes(s));
            mOutputStreamArffUsModel.write(COMMA);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }

    public void arffEntryLongLastUs(long s){
        try {
            mOutputStreamArffUsModel.write(ToByteString.getBytes(s));
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }

    public void arffEntryDoubleLast(double i) {
        // int count = ToByteString.putBytes(i, dbuf, 0);
        try {
            mOutputStreamArff.write(String.format("%.6f", i).getBytes());
            //  mOutputStreamArff.write(dbuf, 0, count);
        } catch (IOException ioe) {
            if (DBG) {
                Log.e(TAG, "ERROR: Can't write double to file: " + ioe);}
        }
    }




    public void arffEntryDouble(double i) {
       // int count = ToByteString.putBytes(i, dbuf, 0);
        try {
            mOutputStreamArff.write(String.format("%.6f", i).getBytes());
          //  mOutputStreamArff.write(dbuf, 0, count);
            mOutputStreamArff.write(COMMA);
        } catch (IOException ioe) {
            if (DBG) {
                Log.e(TAG, "ERROR: Can't write double to file: " + ioe);}
        }
    }

    public void arffEntryDoubleUs(double i) {
        // int count = ToByteString.putBytes(i, dbuf, 0);
        try {
            mOutputStreamArffUsModel.write(String.format("%.6f", i).getBytes());
            //  mOutputStreamArff.write(dbuf, 0, count);
            mOutputStreamArffUsModel.write(COMMA);
        } catch (IOException ioe) {
            if (DBG) {
                Log.e(TAG, "ERROR: Can't write double to file: " + ioe);}
        }
    }


    public void arffEntryInteger(int i) {
        int count = ToByteString.putBytes(i, dbuf, 0);
        try {

            mOutputStreamArff.write(dbuf, 0, count);
            mOutputStreamArff.write(COMMA);
        } catch (IOException ioe) {
            if (DBG) {
                Log.e(TAG, "ERROR: Can't write double to file: " + ioe);}
        }
    }

    public void arffEntryIntegerLast(int i) {
        int count = ToByteString.putBytes(i, dbuf, 0);
        try {

            mOutputStreamArff.write(dbuf, 0, count);
        } catch (IOException ioe) {
            if (DBG) {
                Log.e(TAG, "ERROR: Can't write double to file: " + ioe);}
        }
    }

    public void arffEntryIntegerUs(int i) {
        int count = ToByteString.putBytes(i, dbuf, 0);
        try {

            mOutputStreamArffUsModel.write(dbuf, 0, count);
            mOutputStreamArffUsModel.write(COMMA);
        } catch (IOException ioe) {
            if (DBG) {
                Log.e(TAG, "ERROR: Can't write double to file: " + ioe);}
        }
    }

    public void arffEntryIntegerUsLast(int i) {
        int count = ToByteString.putBytes(i, dbuf, 0);
        try {

            mOutputStreamArffUsModel.write(dbuf, 0, count);
        } catch (IOException ioe) {
            if (DBG) {
                Log.e(TAG, "ERROR: Can't write double to file: " + ioe);}
        }
    }

    public void arffEntryNewInstance(){
        try {
            mOutputStreamArff.write(NEWLINE);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }

    public void arffEntryNewInstanceUs(){
        try {
            mOutputStreamArffUsModel.write(NEWLINE);
        } catch (IOException ioe) {
            Log.e(TAG, "ERROR: Can't write string to file: " + ioe);
        }
    }

    public static void deleteArffFile(Context c) {
        synchronized(mLogLock) {
            c.deleteFile(ARFF_FILE_NAME);
        }
    }


    private static final int DBUFSIZE = 16;
    private static final byte[] dbuf = new byte[DBUFSIZE];







    public static void prepareForUpload(Context c) {
        synchronized(mLogLock) {
            if (DBG) {
                Log.i(TAG, "Preparing for upload: append logfile to upload file");}

            // append the log file to the upload file
            try {
                FileInputStream in_stream = c.openFileInput(Definitions.LOG_FILE_NAME);
                DataInputStream in = new DataInputStream(in_stream);
                FileOutputStream out_stream = c.openFileOutput(UPLOAD_FILE_NAME, Context.MODE_APPEND);
                DataOutputStream out = new DataOutputStream(out_stream);

                byte[] buffer = new byte[2048];
                int bytes_read;

                while (-1 != (bytes_read = in.read(buffer, 0, 2048))) {
                    out.write(buffer, 0, bytes_read);
                }
            } catch (Exception e) {
                //   ServiceClass.getLogger().errorOccurred(e);
                if (DBG) {
                    Log.e(TAG, "Can't open input file stream in preparation: " + e);}
            }

            // try to gzip file here

            if (DBG) { Log.i(TAG, ">>>>>> TRY GZIP NOW"); }
            try {
                FileInputStream in = c.openFileInput(UPLOAD_FILE_NAME);
                FileOutputStream out = c.openFileOutput(UPLOAD_FILE_NAME + ".gz", Context.MODE_PRIVATE);
                GZIPOutputStream gz_out = new GZIPOutputStream(out);

                byte[] buffer = new byte[2048];
                int bytes_read;

                while (-1 != (bytes_read = in.read(buffer, 0, 2048))) {
                    gz_out.write(buffer, 0, bytes_read);
                }
                gz_out.finish();
            } catch (Exception e) {
                // JamLoggerService.getLogger().errorOccurred(e);
                if (DBG) { Log.e(TAG, "ERROR GZIPPING");}
            }


            // Then delete the log file and reinitialize it
            // From here on out, the upload file has what needs to be uploaded.

        }
    }


    @Override
    public void deleteUploadedFiles(Context c) {
        synchronized(mLogLock) {
           try {
               c.deleteFile(LOG_FILE_NAME);
           }catch (Exception e){
               Log.d(TAG,"Exception occurred: " + e);
           }
            try {
                c.deleteFile(UPLOAD_FILE_NAME);
            }catch (Exception e){
                Log.d(TAG,"Exception occurred: " + e);
            }
            try {
                c.deleteFile(UPLOAD_FILE_NAME_GZ);
            }catch (Exception e) {
                Log.d(TAG, "Exception occurred: " + e);
            }
        }
    }




    public static void deleteGzipUploadFile(Context c) {
        synchronized(mLogLock) {
            c.deleteFile(HEART_FILE_NAME + ".gz");
        }
    }

    public long logFileSize(Context c) {
        synchronized (mLogLock) {
            File log_file = new File(c.getFilesDir(), LOG_FILE_NAME);
            if (!log_file.exists()) {
                return 0;
            }
            return log_file.length();
        }
    }

    public long arffFileSize(Context c) {
        synchronized (mLogLock) {
            File log_file = new File(c.getFilesDir(), ARFF_FILE_NAME);
            if (!log_file.exists()) {
                return 0;
            }
            return log_file.length();
        }
    }

    public long arffUsFileSize(Context c) {
        synchronized (mLogLock) {
            File log_file = new File(c.getFilesDir(), ARFF_FILE_NAME_US_MODEL);
            if (!log_file.exists()) {
                return 0;
            }
            return log_file.length();
        }
    }


    public static long heartFileSize(Context c) {
        File log_file = new File(c.getFilesDir(), HEART_FILE_NAME);
        if (!log_file.exists()) {
            return 0;
        }
        return log_file.length();
    }

    public static long stepsFileSize(Context c) {
        File log_file = new File(c.getFilesDir(), STEPS_FILE_NAME);
        if (!log_file.exists()) {
            return 0;
        }
        return log_file.length();
    }


    public static long caloriesFileSize(Context c) {
        File log_file = new File(c.getFilesDir(), CALORIES_FILE_NAME);
        if (!log_file.exists()) {
            return 0;
        }
        return log_file.length();
    }


    public static class ToByteString {
        private ToByteString() {
            throw new AssertionError();
        }

        private static final byte[] ZERO = "0".getBytes();

        public static byte[] getBytes(int i) {
            //int init = i;
            boolean neg = (i<0);
            int inc = neg ? 1 : 0;
            i= Math.abs(i);

            int length;
            if (i == 0) {
                return ZERO;
            } else {
                length = ((int) Math.log10(i))+1;
            }

            byte[] output;
            if (neg) {
                output = new byte[length+1];
                output[0]='-';
            } else {
                output = new byte[length];
            }

            putBytes(i, output, inc);

			/*
			int rem;
			for (int j=0; j<(length); j++) {
				rem = i % 10;
				i = i / 10;
				output[length+inc-j-1]=(byte)(rem+'0');
			}
			*/
			/*
			// Compare
			String s = Integer.toString(init);
			boolean error = false;
			byte[] comp = s.getBytes();
			for (int j=0; j<output.length; j++) {
				if (output[j] != comp[j]) {
					Log.e(TAG, "ERROR: input: "+i+" , byte "+j+", \""+output[j]+"\" != \""+comp[j]+"\"");
					error = true;
				}
			}
			if (! error) {
				Log.d(TAG, "Conversion OK");
			}*/

            return output;
        }
        public static byte[] getBytes(long l) {
            boolean neg = (l<0);
            int inc = neg ? 1 : 0;
            l= Math.abs(l);
            //long init = l;

            int length;
            if (l == 0) {
                return ZERO;
            } else {
                length = ((int) Math.log10(l))+1;
            }

            byte[] output;
            if (neg) {
                output = new byte[length+1];
                output[0]='-';
            } else {
                output = new byte[length];
            }

            long rem;
            for (int j=0; j<(length); j++) {
                rem = l % 10;
                l = l / 10;
                output[length+inc-j-1]=(byte)(rem+'0');
            }



            return output;
        }


        /**
         * Put the bytestring representation of integer 'i' into the buffer 'buf'
         * starting at 'offset'.
         *
         * @param i
         * @param buf
         * @param offset
         * @return number of bytes written
         */
        public static int putBytes(int i, byte[] buf, int offset) {
            boolean neg = (i<0);

            i= Math.abs(i);
            //int init = i;

            // length is the number of bytes to represent the number WITHOUT the sign
            int length;
            if (i == 0) {
                buf[offset] = ZERO[0];
                return 1;
            } else {
                length = ((int) Math.log10(i))+1;
            }

            // output is the number of bytes to represent the number WITH the sign
            int output = length;
            if (neg) {
                output++;
                buf[offset]='-';
            }

            int rem;
            for (int j=0; j<(length); j++) {
                rem = i % 10;
                i = i / 10;
                buf[offset+output-j-1]=(byte)(rem+'0');
            }

			/*
			// Compare
			String s = Integer.toString(init);
			boolean error = false;
			byte[] comp = s.getBytes();
			for (int j=0; j<output.length; j++) {
				if (output[j] != comp[j]) {
					Log.e(TAG, "ERROR: input: "+i+" , byte "+j+", \""+output[j]+"\" != \""+comp[j]+"\"");
					error = true;
				}
			}
			if (! error) {
				Log.d(TAG, "Conversion OK");
			}
			*/

            return output;
        }

        /**
         * Put the bytestring representation of double 'd' into the buffer 'buf'
         * starting at 'offset'.
         *
         * The format limits to 2 places after the decimal point
         *
         * @param d
         * @param buf
         * @param offset
         * @return number of bytes written
         */
        public static int putBytes(double d, byte[] buf, int offset) {
            //if (DBG) Log.d(TAG, "Input: \""+mDoubleFmt.format(d)+"\"");
            int intPart = (int)d;
            d = d - intPart;
            int fracPart = (int) Math.round(d*100.0);

            // integer part
            int count = putBytes(intPart, buf, offset);
            // decimal point
            buf[offset+count] = '.';
            count += 1;
            // fractional part
            count += putBytes(fracPart, buf, offset+count);

            //if (DBG) Log.d(TAG, "Output: \""+(new String(buf, offset, count))+"\"");

            return count;
        }
    }
};

