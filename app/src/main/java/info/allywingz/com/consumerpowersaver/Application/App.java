package info.allywingz.com.consumerpowersaver.Application;

import com.parse.Parse;
import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("5ce5bd84-bca5-4e86-a7d6-af9e7ed93cc5\n")
                .clientKey("CYC2018")
                .server("https://api.parse.buddy.com/parse/")
                .build()
        );
    }
}
