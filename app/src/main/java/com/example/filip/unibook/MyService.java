package com.example.filip.unibook;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    String[] chats;

    private Timer mTimer;
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.e("Log", "Running");
            checkForNewMessage();
            getNotification();
        }
    };

    @Override
    public void onCreate(){
        super.onCreate();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 2000, 2 * 8000);

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

    //Metod som kollar efter nya annonser som sedan visas som notifikationer
    public void checkForNewAds(final List<String> notifications, final List<String> adIds, final List<String> notisIds){
        DocumentReference adsRef = rootRef.collection("Ads").document("latest");
        adsRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }
                List<String> notiser = notifications;
                DocumentSnapshot doc = snapshot;
                String notisProgram = doc.getString("program");
                String notisKurs = doc.getString("course");
                String adId = doc.getId();

                for (int i = 0; i < notiser.size(); i++) {
                    if (!user.getUid().equals(doc.getString("sellerId"))) {

                        if (notisProgram.equals(notiser.get(i)) && !adId.equals(adIds.get(i))) {
                            Notification notification = new Notification(getApplicationContext(), "En ny bok tillhörande programmet " + notisProgram , "och tillhörande kursen " + notisKurs + " har lagts upp. Tryck för att öppna UniBook");
                            notification.notificationManagerCompat.notify(2, notification.mBuilder.build());
                            updateNotification(adId, notisIds.get(i));

                        } else if (notisKurs.equals(notiser.get(i))) {
                            Notification notification = new Notification(getApplicationContext(), "En ny bok tillhörande programmet " + notisProgram, " och tillhörande kursen "+ notisKurs + " har lagts upp. Tryck för att öppna UniBook");
                            notification.notificationManagerCompat.notify(2, notification.mBuilder.build());
                        }
                    }
                }
            }
        });
    }

    //Metod som gör att en notifikation endast visas en gång.
    public void updateNotification(String notification, String notisId){
        DocumentReference usersRef = rootRef.collection("Users").document(user.getUid().toString()).collection("Notifications").document(notisId);
        Map<String, Object> mapOne = new HashMap<>();
        mapOne.put("AdId", notification);

        usersRef.set(mapOne)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });
    }

    public List<String> getNotification(){
        CollectionReference usersRef =  rootRef.collection("Users").document(user.getUid().toString()).collection("Notifications");
        final List<String> notifications = new ArrayList<>();
        final List<String>adIds = new ArrayList<>();
        final List<String> notisIds = new ArrayList<>();

        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int size = task.getResult().size();

                    List<DocumentSnapshot> list = task.getResult().getDocuments();
                    for (int i = 0; i < task.getResult().size(); i++) {
                        DocumentSnapshot doc = list.get(i);
                        String notis = doc.getString("Notification");
                        notifications.add(notis);
                        adIds.add(doc.getString("AdId"));
                        notisIds.add(doc.getId().toString());

                    }

                    checkForNewAds(notifications, adIds, notisIds);
                }

                else{
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
        return notifications;
    }

    public void checkForNewMessage() {
        //Uppdatera meddelandelistan när ett nytt meddelande lagts till i databasen.
        final CollectionReference colRef = rootRef.collection("Chat");
        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> list = task.getResult().getDocuments();

                            if (list.size() > 0) {

                                chats = new String[task.getResult().size()];

                                for (int i = 0; i < list.size(); i++) {
                                    DocumentSnapshot documentSnapshot = list.get(i);
                                    if (documentSnapshot.getString("User1").equals(user.getUid().toString())) {
                                        chats[i] = documentSnapshot.getId().toString();
                                    } else if (documentSnapshot.getString("User2").equals(user.getUid().toString())) {
                                        chats[i] = documentSnapshot.getId().toString();
                                    }

                                    if (chats[i] != null) {
                                        checkLatest(chats[i]);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    public void checkLatest(String chatsWithUser){
        final DocumentReference colRef = rootRef.collection("Chat").document(chatsWithUser).collection("Messages").document("latest");
        colRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if(documentSnapshot.getString("NotiSent") != null) {
                    if (!documentSnapshot.getString("UserId").equals(user.getUid().toString()) && documentSnapshot.getString("NotiSent").equals("Not Sent")) {
                        colRef.update("NotiSent", "Sent");
                        try {
                            Uri urinotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), urinotification);
                            String name = documentSnapshot.getString("Name");
                            String message = documentSnapshot.getString("Message");
                            sendNoti(name, message);
                            r.play();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void sendNoti(String name, String message){
        String notimeg = name + " har skickat ett meddelande: " + message;
        Notification notification = new Notification(this, notimeg, "Tryck för att öppna UniBook");
        notification.notificationManagerCompat.notify(2, notification.mBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}