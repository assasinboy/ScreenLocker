package wearpower.screenlocker;

/**
 * Created by emir on 7/24/17.
 */

public class DataHolder {



    private long lastTime;
    private boolean del_safe;
    private long userInputTime;
    private int userInput;
    private boolean voice_permission = false;
    private boolean voice_started = false;

    private double avgVoice = 0.0;
    private double minVoice = 0.0;
    private double maxVoice = 0.0;

    private int set_count = 0;

    public int getSetCount(){return set_count;}
    public void setSetCount(int set_count) {this.set_count = set_count;}


    public double getAvgVoice(){return  avgVoice;}
    public void setAvgVoice(double avgVoice){this.avgVoice = avgVoice;}

    public double getMinVoice(){return  minVoice;}
    public void setMinVoice(double minVoice){this.minVoice = minVoice;}

    public double getMaxVoice(){return  maxVoice;}
    public void setMaxVoice(double maxVoice){this.maxVoice = maxVoice;}


    public long getLastTimeComingLog(){return lastTime;}
    public void setLastTimeComingLog(){
        this.lastTime = System.currentTimeMillis();
    }


    public boolean getDeleteSafe(){return  del_safe;}
    public void setDeleteSafe(boolean safe){this.del_safe = safe;}

    public boolean getVoicePermission(){return  voice_permission;}
    public void setVoicePermission(boolean voice_permission){this.voice_permission = voice_permission;}

    public boolean getVoiceStarted(){return  voice_started;}
    public void setVoiceStarted(boolean voice_started){this.voice_started = voice_started;}

    public long getUserInputTime(){return  userInputTime;}
    public void setUserInputTime(long userInputTime){this.userInputTime = userInputTime;}


    public int getUserInput(){return  userInput;}
    public void setUserInput(int userInput){this.userInput = userInput;}

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}
