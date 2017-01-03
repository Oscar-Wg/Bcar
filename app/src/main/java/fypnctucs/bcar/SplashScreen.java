package fypnctucs.bcar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by kamfu.wong on 29/9/2016.
 */

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timerThread = new Thread(){
            public void run() {
                final Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally {
                    startActivity(intent);
                }
            }
        };

        timerThread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
