package www.coralinnovations.cyc.Application;

import android.app.Application;

import com.parse.Parse;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("HDWZuTLZargUnThgoTFtaqVI3mCRNKv13o1eBie5")
                .clientKey("IevHkyrg1gxAlGbAXE5oT5Jpc3EAL0Ir89XMT609")
                .server("https://parseapi.back4app.com")
                .build()
        );
//        Parse.Buddy.initialize();
    }
}
