package com.example.filip.unibook;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.sql.Blob;
import java.util.Date;

/**
 * Created by Ludvig on 2018-03-12.
 */

public class MyMessagesAdapter extends BaseAdapter {

    LayoutInflater mInflator;
    String[] items;
    String[] chatids;
    String[] boktitel;
    Blob[] blobs;
    String[] userid;
    Context context;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();


    public MyMessagesAdapter(Context c, String[] items, String[] chatids, String[] userid, String[] boktitel, Blob[] blobs) {
        this.items = items;
        this.chatids = chatids;
        this.userid = userid;
        this.boktitel = boktitel;
        if(blobs != null) {
            this.blobs = blobs;
        }
        this.context = c;
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int i) {
        return items;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = mInflator.inflate(R.layout.mymessages_listview, null);
        TextView nameTxt = v.findViewById(R.id.txtUserTalkingTo);
        TextView chatId = v.findViewById(R.id.txtChatId);
        TextView boktitlar = v.findViewById(R.id.txtChatForAd);
        final ImageView imageView = v.findViewById(R.id.ivMessages);

        String name = items[i];
        String ids = chatids[i];
        String user = userid[i];
        String titel = boktitel[i];

        nameTxt.setText(name);
        chatId.setText(ids);
        boktitlar.setText("Ämne: " + titel);

        final DocumentReference docRef = rootRef.collection("Users").document(user);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    String imageId = doc.getString("imageId");

                    //Hämta profilbild.
                    StorageReference storageRef = storage.getReferenceFromUrl(imageId);

                    final long ONE_MEGABYTE = 1024 * 1024;

                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Data for "images/island.jpg" is returns, use this as needed
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            }
        });

        return v;
    }
}