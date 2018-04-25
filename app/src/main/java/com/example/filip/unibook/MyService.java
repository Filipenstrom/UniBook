package com.example.filip.unibook;


import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.Reference;
import java.sql.Ref;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ludvig on 2018-03-20.
 */

public class MyService extends Service {
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    String chatId;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    private Timer mTimer;
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.e("Log", "Running");
            checkForNotis();
            checkForNewMessage();
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

    public void checkForNewMessage() {
        //Uppdatera meddelandelistan när ett nytt meddelande lagts till i databasen.
        final CollectionReference colRef = rootRef.collection("Chat");
        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> chatLista = task.getResult().getDocuments();
                            int size = task.getResult().size();
                            String[] chatlista = new String[size];

                            for (int i = 0; i < size; i++) {
                                DocumentSnapshot doc = chatLista.get(i);
                                if (doc.getString("User1").equals(user.getUid().toString())) {
                                    chatlista[i] = doc.getString(doc.getId().toString());
                                } else {
                                    chatlista[i] = doc.getString(doc.getId().toString());
                                }
                            }

                            for(int i = 0; i<chatlista.length;i++){
                                final DocumentReference chatRef = rootRef.collection("Chat").document(chatlista[i]).collection("Messages").document("latest");
                                chatRef.get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    if(!documentSnapshot.getString("UserId").equals(user.getUid())) {
                                                        try {
                                                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                                            r.play();
                                                        } catch (Exception ex) {
                                                            ex.printStackTrace();
                                                        }
                                                    }

                                                }
                                            }
                                        });
                        }

                    }
                }
    });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

