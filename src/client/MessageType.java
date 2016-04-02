package client;

/**
 * Created by mdl94 on 16/03/2016.
 */
public class MessageType
{
    public static final int ERROR = -1;
    public static final int READ_FILE = 0;
    public static final int INSERT_FILE = 1;
    public static final int MONITOR_FILE = 2;
    public static final int DELETE_FILE = 3;
    public static final int DUPLICATE_FILE = 4;
    public static final int RESPONSE_MSG = 5;
    public static final int RESPONSE_BYTES = 6;
    public static final int CALLBACK = 7;
    public static final int RESPONSE_PATH = 8;
    public static final int RESPONSE_SUCCESS = 9;
    public static final int AT_LEAST_ONCE_DEMO_INSERT_FILE = 51;

    //Command
    public static final int READ_COMMAND = 1;
    public  static final int INSERT_COMMAND = 2;
    public static final int MONITOR_COMMAND = 3;
    public static final int FAIL_AT_LEAST_ONCE = 4;
    public static final int FAIL_AT_MOST_ONCE = 5;


}
