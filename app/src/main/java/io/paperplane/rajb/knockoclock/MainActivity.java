package io.paperplane.rajb.knockoclock;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ListView list;
    ArrayList<Model> modelList;
    private static TextToSpeech t1;
    public static SensorManager sm;
    public static AudioManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView myTextView=(TextView)findViewById(R.id.textView);
        Typeface typeFace= Typeface.createFromAsset(getAssets(),"RobotoCondensed-Light.ttf");
        myTextView.setTypeface(typeFace);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }

    });
    }

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");


            Context remotePackageContext = null;
            try {
                byte[] byteArray =intent.getByteArrayExtra("icon");
                Bitmap bmp = null;
                if(byteArray !=null) {
                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                }
                Model model = new Model();
                model.setName(title +" " +text);
                model.setImage(bmp);
                t1.speak("Message from" + " " + title + " saying " + " " + text, TextToSpeech.QUEUE_FLUSH, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void sendMediaButton(Context context, int keyCode) {


        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendOrderedBroadcast(intent, null);

        keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        context.sendOrderedBroadcast(intent, null);
    }


    @Override
    public void onPause(){
        super.onPause();
       // dk.stopAccSensing();
    }

    @Override
    public void onResume(){
        super.onResume();
      //  dk.resumeAccSensing();
    }

    public void run(View v){

        sendMediaButton(getApplicationContext(), KeyEvent.KEYCODE_MEDIA_PAUSE);

        Intent intent = new Intent(this, ListenService.class);
        startService(intent);
    }

    public void stop(View v){
        sendMediaButton(getApplicationContext(), KeyEvent.KEYCODE_MEDIA_PAUSE);
        Intent intent = new Intent(this, ListenService.class);
        stopService(intent);
    }

    public void notifPerms(View v){
        Intent intent = new Intent(
                "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);
    }


}




