package www.coral.innovations.cyc.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import www.coral.innovations.cyc.R;
import www.coral.innovations.cyc.Storage.Constants;
import www.coral.innovations.cyc.Storage.Storage;

public class SplashScreen extends AppCompatActivity {
    Storage storage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_sreen);
        storage = new Storage(SplashScreen.this);
        loadLogoAnimation();
        isValidating();
    }

    private void loadLogoAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.logo_animation_in);
        ((LinearLayout) findViewById(R.id.splash)).startAnimation(a);
    }

    void isValidating() {
        if (storage.getValue(Constants.IS_LOGIN).equals("LoggedInn")) {
            new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, HomeScreen.class));
                }
            }, 2000);
        } else {
            new Handler().postDelayed(new Runnable() {
                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                }
            }, 2000);

        }
    }

}
