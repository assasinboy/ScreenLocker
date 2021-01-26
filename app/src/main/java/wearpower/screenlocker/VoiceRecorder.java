package wearpower.screenlocker;

import android.media.MediaRecorder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by emir on 7/22/17.
 */

public class VoiceRecorder {

    final private static boolean DBG = true;
    final private static String TAG = "VoiceRecorder";

    public double[] voices = new double[9];
    public int voice_counter=0;
    public double voice_sum=0;

    private double avg_voice=0;
    private double min_voice=0;
    private double max_voice=0;

    private static Logger mLogger = new Logger();

    private MediaRecorder mRecorder = null;
    Timer timer;

    public void start()  {
        if (mRecorder == null) {
            try {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                timer = new Timer();
                timer.scheduleAtFixedRate(new RecorderTask(mRecorder), 0, 500);

                mRecorder.setOutputFile("/dev/null");
                mRecorder.prepare();
                mRecorder.start();
                DataHolder.getInstance().setVoiceStarted(true);
            } catch (Exception e) {
                Log.d(TAG, "Error Exception: " + e);
                DataHolder.getInstance().setVoiceStarted(false);
                mRecorder = null;
            }
        }
    }

    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            timer.cancel();
        }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return mRecorder.getMaxAmplitude();
        else
            return 0;

    }


    /*
    public double getAvgVoice(){
        if (voice_counter==0){
            return -1;
        }
        return voice_sum/voice_counter;
    }

    public double getMinVoice(){
        if (voice_counter==0){
            return -1;
        }
        double minn=1000;
        int h=0;
        while (h<=voice_counter-1) {
            if (voices[h]<minn) {
                minn=voices[h];
            }
            h=h+1;
        }

        return minn;
    }

    public double getMaxVoice(){
        if (voice_counter==0){
            return -1;
        }
        double maxx=-1;
        int h=0;
        while (h<=voice_counter-1) {
            if (voices[h]>maxx) {
                maxx=voices[h];
            }
            h=h+1;
        }
        voices = new double[20];
        voice_counter=0;
        voice_sum=0;
        return maxx;
    }
    */

    public void setAvgVoice(double voice_counter,double voice_sum){
        this.avg_voice=voice_sum/voice_counter;
    }
    public double getAvgVoice(){
        return avg_voice;
    }

    public void setMinVoice(double value){
        this.min_voice=value;
    }
    public double getMinVoice(){
        return min_voice;
    }

    public void setMaxVoice(double value){
        this.max_voice=value;
    }
    public double getMaxVoice(){
        return max_voice;
    }



    private static final VoiceRecorder voiceRecorderHolder = new VoiceRecorder();
    public static VoiceRecorder getInstance() {return voiceRecorderHolder;}



    private class RecorderTask extends TimerTask {
        private MediaRecorder recorder;

        public RecorderTask(MediaRecorder recorder) {
            this.recorder = recorder;
        }

        public void run(Runnable runnable) {
            run(new Runnable() {
                @Override
                public void run() {
                    int amplitude = recorder.getMaxAmplitude();
                    double amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude));
                    if(DBG) Log.d(TAG, "Voice recorder: " + amplitudeDb);


                    // sound.setText("" + amplitudeDb);
                }
            });
        }

        @Override
        public void run() {
            int amplitude = recorder.getMaxAmplitude();
            double amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude));
            //if(DBG) Log.d(TAG, "Voice recorder1: " + amplitudeDb);
            mLogger.logStringEntry("Voice: "+ String.format("%.2f", amplitudeDb) );

            try {
                voice_sum = voice_sum + amplitudeDb;
                if (amplitudeDb<min_voice){
                    min_voice=amplitudeDb;
                }
                if (amplitudeDb>max_voice){
                    max_voice=amplitudeDb;
                }
                voice_counter++;
                if (voice_counter>=9){
                    VoiceRecorder.getInstance().setAvgVoice(voice_counter,voice_sum);
                    VoiceRecorder.getInstance().setMinVoice(min_voice);
                    VoiceRecorder.getInstance().setMaxVoice(max_voice);

                    DataHolder.getInstance().setAvgVoice((voice_sum/voice_counter)/100);
                    DataHolder.getInstance().setMinVoice(min_voice/100);
                    DataHolder.getInstance().setMaxVoice(max_voice/100);
                    if (DBG) Log.d(TAG , "Voice updated! " + min_voice + " " +max_voice);

                    voice_counter=0;
                    max_voice=0;
                    min_voice=100;
                    voice_sum=0;


                }
            }catch (Exception e){
                if(DBG) Log.d(TAG, "Voice recorder error: " + e);
            }
        }
    }


}
