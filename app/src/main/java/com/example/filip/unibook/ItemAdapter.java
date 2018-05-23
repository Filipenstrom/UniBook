package com.example.filip.unibook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.filip.unibook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ludvig on 2018-03-12.
 */

public class ItemAdapter extends BaseAdapter {

    LayoutInflater mInflator;
    String[] items;
    String[] prices;
    String[] id;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public ItemAdapter(Context c, String[] i, String[] p, String[] id){
        items = i;
        prices = p;
        this.id = id;
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

    //Fyller listview med data om annonser, körs en gång för varje listitem.
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = mInflator.inflate(R.layout.my_listview_ad, null);
        TextView nameTextView = v.findViewById(R.id.txtAdTitle);
        TextView priceTextView =  v.findViewById(R.id.txtAdPris);
        TextView idsTextView = v.findViewById(R.id.txtAdID);
        final ImageView imageView = v.findViewById(R.id.ivAdsListPicture);

        String name = items[i];
        String cost = prices[i] + ":-";
        String adid = id[i];

        nameTextView.setText(name);
        priceTextView.setText(cost);
        idsTextView.setText(adid);

        final DocumentReference docRef = rootRef.collection("Ads").document(adid);
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
