package info.allywingz.com.consumerpowersaver.Storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

public class Storage {
    private final SharedPreferences settings;

    public Storage(Context context )
    {
        settings = PreferenceManager.getDefaultSharedPreferences( context );
    }

    public void saveSecure(String key, String value )
    {
        settings.edit().putString( key, value ).apply();
    }
    public void saveSecureBitmap(String key, Bitmap value )
    {
        settings.edit().putString( key, String.valueOf(value)).apply();
    }
    public void saveSecureBoolean(String key, boolean value )
    {
        settings.edit().putBoolean( key, value ).apply();
    }

    public String getValue(String key )
    {
        return settings.getString( key, "" );
    }
    public boolean getBooleanValue( String key )
    {
        return settings.getBoolean( key, false );
    }

    public void clearAll()
    {
        settings.edit().
                apply();
    }
}
