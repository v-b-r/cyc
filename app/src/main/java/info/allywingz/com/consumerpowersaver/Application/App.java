package info.allywingz.com.consumerpowersaver.Application;

import com.parse.Parse;
import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("99147be1-2898-46f9-9648-855d15e57620")
                .clientKey("ocHoUCaq4oSoiXjEhGAJdrvNsDqPGicI")
                .server("https://api.parse.buddy.com/parse/")
                .build()
        );
        Parse.Buddy.initialize();
    }
}
