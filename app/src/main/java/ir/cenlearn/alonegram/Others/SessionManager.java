package ir.cenlearn.alonegram.Others;

import android.content.Context;
import android.content.SharedPreferences;


public class SessionManager {
    Context context;
    SharedPreferences preferences;
    String pr_name = "prefs";

    boolean loggedIn = false;

    String prl = "loggedin";

    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(pr_name,Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(prl,false);
    }

    public void setLoggedIn(boolean loggedIn) {
        preferences.edit().putBoolean(prl,loggedIn).apply();
    }
}
