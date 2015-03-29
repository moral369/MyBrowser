package browser.dnm.cross.mybrowser;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * Created by User on 13. 7. 30.
 */
public class Preferences implements SharedPreferences {

    private final String PREF_NAME = "com.example.h5bak_webview";
    public Context mContext;
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    public Preferences(Context c) {
        mContext = c;
    }

    public void putString(String key, String value) {

        pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(Context c, String key){
        pref = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(key, "http://www.google.com");
    }

    @Override
    public Map<String, ?> getAll() {
        return null;
    }

    @Override
    public String getString(String s, String s2) {
        return null;
    }

    @Override
    public Set<String> getStringSet(String s, Set<String> strings) {
        return null;
    }

    @Override
    public int getInt(String s, int i) {
        return 0;
    }

    @Override
    public long getLong(String s, long l) {
        return 0;
    }

    @Override
    public float getFloat(String s, float v) {
        return 0;
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        return false;
    }

    @Override
    public boolean contains(String s) {
        return false;
    }

    @Override
    public Editor edit() {
        return null;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {

    }
}
