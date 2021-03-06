
package wearpower.Wearpower;

/**
 * Created by emir on 6/4/15.
 */

public class Definitions {

    final static public boolean DBG = true;
    final static public boolean DOTRACE = false;

    final static public String LOG_FILE_NAME = "AllLogs.log";

    final static public String UPLOAD_FILE_NAME = "AllLogsUpload.log";
    final static public String UPLOAD_FILE_NAME_GZ = "AllLogsUpload.log.gz";

    final static public String DEVICE_UNIQUE_ID = "DEVICE_UNIQUE_ID";
    final static public String DEVICE_ID = "DEVICE_ID";

    final static public String APP_STARTED = "APP_STARTED";
    final static public String APP_STARTING_TIME = "APP_STARTING_TIME";



     final static public long TRAINING_SIZE_THRESHOLD =  2 * 1024; // it is approximately 1 day data // like 700KB
  // final static public long TRAINING_SIZE_THRESHOLD =  60 * 1024;
    final static public long ARFF_FILE_SIZE_THRESHOLD =  20 * 1024;
    final static public long ARFF_FILE_US_SIZE_THRESHOLD =  80 * 1024;  // 80 KB  //

    final static public long FOUR_HOURS = 14400000; // 4 hours in milisecs
    final static public long THREE_MIN = 180000; // 3 min in milisecs
    final static public long TENSEC = 10000; // 3 min in milisecs


  //  final static public long TRAINING_SIZE_THRESHOLD =  17 * 1024;
//    final static public String file_name = "/data/data/wearpower.phonewatchcpuscreen/files/CpuConfLogs.log";
    final static public String file_name = "/data/data/wearpower.displayfilter2/files/AllLogs.log";
    final static public String file_name_upload = "/data/data/wearpower.displayfilter2/files/AllLogsUpload.log";
    final static public String file_name_upload_gz = "/data/data/wearpower.displayfilter2/files/AllLogsUpload.log.gz";
  //  final static public String file_name_upload = "/data/data/wearpower.displayfilter2/files/AllLogsUploads.gz";

    final static public String US_MODEL = "/data/data/wearpower.Wearpower/files/screenStudyUsModel.arff";

     static public String ARFF_FILE_NAME = "/data/data/wearpower.Wearpower/files/screenStudy.arff";
    //static public String ARFF_FILE_NAME = Context.getFilesDir().getPath();
    final static public String MODEL_NAMES = "/data/data/wearpower.Wearpower/files/model.log";
    final static public String DAYS = "/data/data/wearpower.displayfilter2/files/day.log";

    final static public String FPS_CHECKING = "FPS.log";
    final static public String USER_RATING_TIME ="satisfaction.log";


    final static public String MODEL_DAY_FILE_NAME = "/data/data/wearpower.displayfilter2/files/day.log";

    final  static public String USER_SPECIFIC_MODEL = "/data/data/wearpower.displayfilter2/files/usModel.model";

//    final static public String file_name = "/data/data/com.cpuconf/files/CpuConfLogs.arff";


    final static public String ACTION_FILE_UPLOAD_RESULT = "wearpower.displayfilter2.FILE_UPLOAD_RESULT";

    public static final String SHARED_PREFS_NAME = ServiceClassPhone.class.getPackage().getName();


   // final static public String UPLOAD_URL = "http://valhalla.eecs.northwestern.edu/fff.php";
    final static public String UPLOAD_URL = "http://35.185.9.112/test.php";

 //   final static public long ONE_DAY_FILE_SIZE =  125 * 1024; // it is approx 2000 logs and 2 hours // like 700KB
   final static public long ONE_DAY_FILE_SIZE =  10 * 1024; // it is approx 50 logs and 20 min // like 700KB
   final static public long SIX_HOURS_FILE_UPLOAD =  80000;
    final static public long FOUR_HOURS_FILE_UPLOAD =  500000; // used to send data ro backend
    final static public long FILE_SENT_THRESHOLD =  1400000;  // used to put a threshold so that it will not send more than that size
  //  final static public long ONE_DAY_IN_MILISEC = 86400000;  // one day in msec
 //   final static public long ONE_DAY_IN_MILISEC = 3600000;  // 10  min in msec
   final static public long ONE_DAY_IN_MILISEC = 86400000; // 1 day in milisec
    final static public long FIFTEEN_MIN = 900000; // 15 min


    //   final static public long SIX_HOURS_FILE_UPLOAD =  10 * 1024;


}

/*

public class Definitions {

    final static public boolean DBG = true;
    final static public boolean DOTRACE = false;

    final static public String LOG_FILE_NAME = "AllLogs.log";

    final static public String UPLOAD_FILE_NAME = "AllLogsUpload.log";
    final static public String UPLOAD_FILE_NAME_GZ = "AllLogsUpload.log.gz";

    final static public String DEVICE_UNIQUE_ID = "DEVICE_UNIQUE_ID";
    final static public String DEVICE_ID = "DEVICE_ID";

    final static public String APP_STARTED = "APP_STARTED";
    final static public String APP_STARTING_TIME = "APP_STARTING_TIME";



    final static public long TRAINING_SIZE_THRESHOLD =  2 * 1024; // it is approximately 1 day data // like 700KB
    // final static public long TRAINING_SIZE_THRESHOLD =  60 * 1024;
    final static public long ARFF_FILE_SIZE_THRESHOLD =  20 * 1024;
    final static public long ARFF_FILE_US_SIZE_THRESHOLD =  80 * 1024;  // 80 KB  //

    final static public long FOUR_HOURS = 14400000; // 4 hours in milisecs
    final static public long THREE_MIN = 180000; // 3 min in milisecs
    final static public long TENSEC = 10000; // 3 min in milisecs


    //  final static public long TRAINING_SIZE_THRESHOLD =  17 * 1024;
//    final static public String file_name = "/data/data/wearpower.phonewatchcpuscreen/files/CpuConfLogs.log";
    final static public String file_name = "/data/data/wearpower.screenlocker/files/AllLogs.log";
    final static public String file_name_upload = "/data/data/wearpower.screenlocker/files/AllLogsUpload.log";
    final static public String file_name_upload_gz = "/data/data/wearpower.screenlocker/files/AllLogsUpload.log.gz";
    //  final static public String file_name_upload = "/data/data/wearpower.screenlocker/files/AllLogsUploads.gz";

    final static public String US_MODEL = "/data/data/wearpower.screenlocker/files/screenStudyUsModel.arff";

    final static public String ARFF_FILE_NAME = "/data/data/wearpower.screenlocker/files/screenStudy.arff";
    final static public String MODEL_NAMES = "/data/data/wearpower.screenlocker/files/model.log";
    final static public String DAYS = "/data/data/wearpower.screenlocker/files/day.log";

    final static public String FPS_CHECKING = "FPS.log";


    final static public String MODEL_DAY_FILE_NAME = "/data/data/wearpower.screenlocker/files/day.log";

    final  static public String USER_SPECIFIC_MODEL = "/data/data/wearpower.screenlocker/files/usModel.model";

//    final static public String file_name = "/data/data/com.cpuconf/files/CpuConfLogs.arff";


    final static public String ACTION_FILE_UPLOAD_RESULT = "wearpower.screenlocker.FILE_UPLOAD_RESULT";

    public static final String SHARED_PREFS_NAME = ServiceClassPhone.class.getPackage().getName();

    // final static public String UPLOAD_URL = "http://valhalla.eecs.northwestern.edu/fff.php";
    final static public String UPLOAD_URL = "http://35.185.9.112/test.php";

    //   final static public long ONE_DAY_FILE_SIZE =  125 * 1024; // it is approx 2000 logs and 2 hours // like 700KB
    final static public long ONE_DAY_FILE_SIZE =  10 * 1024; // it is approx 50 logs and 20 min // like 700KB
    final static public long SIX_HOURS_FILE_UPLOAD =  80000;
    final static public long FOUR_HOURS_FILE_UPLOAD =  500000; // used to send data ro backend
    final static public long FILE_SENT_THRESHOLD =  1400000;  // used to put a threshold so that it will not send more than that size
    //  final static public long ONE_DAY_IN_MILISEC = 86400000;  // one day in msec
    //   final static public long ONE_DAY_IN_MILISEC = 3600000;  // 10  min in msec
    final static public long ONE_DAY_IN_MILISEC = 86400000; // 1 day in milisec
    final static public long FIFTEEN_MIN = 900000; // 15 min


    //   final static public long SIX_HOURS_FILE_UPLOAD =  10 * 1024;


}

 */