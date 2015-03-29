package browser.dnm.cross.mybrowser;

import android.util.Log;


public class AvLog {

	public static String TAG = "DNMBrowser";
    private static boolean LOG_ON = true;
    private static final int REAL_METHOD_POS = 2;
    private static StackTraceElement[] ste;
    private static StackTraceElement realMethod;

    private static String prefix() {
        ste = new Throwable().getStackTrace();
        realMethod = ste[REAL_METHOD_POS];
        //String thread = (UI_HASH_CODE == Thread.currentThread().hashCode()) ? "UI" : "Other";
        return realMethod.getFileName()+"."+realMethod.getMethodName()
                + "("+realMethod.getLineNumber() +")"+" :: "/*+ "()-" + "[Thread:" + thread + "] "*/;
    }
    private static void printCallerLink(String tag) {
        try {
            StackTraceElement[] stack = new Throwable().getStackTrace();
            StackTraceElement caller = stack[3];
            Log.d(tag, "caller from :: " + caller);
        } catch (ArrayIndexOutOfBoundsException e)  {
            Log.d(tag, "ArrayIndexOutOfBoundsException");
        }
    }

    private static int UI_HASH_CODE = 0;
    public static void setUiThreadHashCode (int uiHashCode) {
        UI_HASH_CODE = uiHashCode;
    }

    public static void setLogOn(boolean enable){
        LOG_ON = enable;
    }

    public static boolean getLogOn(){
        return LOG_ON;
    }

    public static void d(String tag, String msg) {
        if (LOG_ON) {
            Log.d(tag, prefix() + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (LOG_ON) {
            Log.i(tag, prefix() + msg + " :: at " + realMethod);
            printCallerLink(tag);
        }
    }

    public static void w(String tag, String msg) {
        if (LOG_ON) {
            Log.w(tag, prefix() + msg + " at " + realMethod);
            printCallerLink(tag);
        }
    }

    public static void i(String msg) {
        if (LOG_ON) {
            Log.i(TAG, prefix() + msg + " :: at " + realMethod);
            printCallerLink(TAG);
        }
    }

    public static void w(String msg) {
        if (LOG_ON) {
            Log.w(TAG, prefix() + msg + " at " + realMethod);
            printCallerLink(TAG);
        }
    }


    public static void e(String tag, String msg) {
        //if (LOG_ON) {
        Log.e(tag, prefix() + msg);
        //}
    }

    public static void v(String tag, String msg) {
        if (LOG_ON) {
            Log.v(tag, prefix() + msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (LOG_ON) {
            Log.d(tag, prefix() + msg, tr);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (LOG_ON) {
            Log.i(tag, prefix() + msg, tr);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (LOG_ON) {
            Log.e(tag, prefix() + msg, tr);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (LOG_ON) {
            Log.v(tag, prefix() + msg, tr);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (LOG_ON) {
            Log.w(tag, prefix() + msg, tr);
        }
    }
}
