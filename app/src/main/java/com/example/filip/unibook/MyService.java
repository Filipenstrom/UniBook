package com.example.filip.unibook;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ludvig on 2018-03-20.
 */

public class MyService extends Service {

    private Timer mTimer;
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.e("Log", "Running");
            checkForNotis();
        }
    };

    @Override
    public void onCreate(){
        super.onCreate();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 20000, 2 * 8000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // START YOUR TASKS
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // STOP YOUR TASKS
        super.onDestroy();
        try{
            mTimer.cancel();
            timerTask.cancel();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void checkForNotis() {
        DatabaseHelper db = new DatabaseHelper(this);
        SharedPreferences sp = new SharedPreferences(this);
        User user = db.getUser(sp.getusername());
        List<String> notis = db.getNotis(user.getId());
        List<Ad> ads = db.getAllAdsNotis("");

        try {
            //Loopa igenom alla ads.
            for (int i = 0; i < ads.size(); i++) {
                //Loopa igenom alla notiser på en ad.
                for (int i2 = 0; i2 < notis.size(); i2++) {
                    Ad ad;
                    if (ads.size() == 1) {
                        ad = ads.get(i);
                    } else {
                        //Gör bara kollen på de senast tillagda böckerna
                        ad = ads.get(db.getNotisCounter(notis.get(i2)));
                    }
                    //Kolla om det lagts till nya böcker sen senaste notisen visades, om inte visa ingen ny notis.
                    if (ads.size() > db.getNotisCounter(ad.getProgram())) {
                        String program = ad.getProgram();
                        String course = ad.getCourse();
                        //Om notisen matchar ett program på en ny bok, visa notis.
                        if (notis.get(i2).trim().equals(program) || notis.get(i2).trim().equals(course)) {
                            Notification notification = new Notification(this, program + " i kursen " + course, "Tryck för att öppna UniBook");
                            notification.notificationManagerCompat.notify(2, notification.mBuilder.build());

                            //Ändra notiscounter på rätt notis.
                            if(notis.get(i2).equals(program)) {
                                db.setNotisCounter(ad.getProgram(), ads.size());
                                break;
                            }
                            else if(notis.get(i2).equals(course)){
                                db.setNotisCounter(ad.getCourse(), ads.size());
                                break;
                            }
                        }
                        //Om boken inte matchar notisen gå vidare till nästa bok.
                        else {
                            int counter = db.getNotisCounter(notis.get(i)) + 1;
                            db.setNotisCounter(notis.get(i2), counter);
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            Log.d("Broken", "Index out of bounds");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
