package com.moganstar.randomtalk.GlobalChat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moganstar.randomtalk.ChatRoom.ChatRoom;
import com.moganstar.randomtalk.ChatRoom.MainActivity;
import com.moganstar.randomtalk.GlobalChat.Adapter.MessageAdapter;
import com.moganstar.randomtalk.GlobalChat.Model.Chat;
import com.moganstar.randomtalk.GlobalChat.Model.User;
import com.moganstar.randomtalk.R;
import com.moganstar.randomtalk.profile.OthersProfile;
import com.moganstar.randomtalk.util.PersistentUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    private String deviceId = "";
    private String toDeviceId = "";
    DatabaseReference reference;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;

    // Facebook Ads
    private InterstitialAd interstitialAd;
    private InterstitialAdListener interstitialAdListener;
    private int adscount = 0;
    private int maxadscount = 0;

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gc_activity_message);

        // Facebook Ads
        adscount = PersistentUser.getAdsCount(this.getApplicationContext());
        maxadscount = getResources().getInteger(R.integer.maxadscount);

        AudienceNetworkAds.initialize(this);
        interstitialAd = new InterstitialAd(this,getResources().getString(R.string.fb_interstitial_ad1));
        interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                loadAds();
                Intent intent = new Intent(MessageActivity.this, OthersProfile.class);
                intent.putExtra("userId",toDeviceId);
                startActivity(intent);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
            }

            @Override
            public void onAdLoaded(Ad ad) {
            }

            @Override
            public void onAdClicked(Ad ad) {
                adscount++;
                PersistentUser.setAdsCount(MessageActivity.this,adscount);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };
        loadAds();
        //

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        String userid = intent.getStringExtra("userid");
        toDeviceId = userid;
        deviceId = PersistentUser.getDeviceId(this.getApplicationContext());

        // Sending message button
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString().trim();
                if(!TextUtils.isEmpty(msg)){
                    sendMessage(deviceId,userid,msg);
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Type a message to send it",
                            Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });
        //

        reference = FirebaseDatabase.getInstance().getReference().child("globalchat").child("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.default_user);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                readMessages(deviceId,userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);

        profile_image.setOnClickListener(v->{
            openOthersProfile();
        });
        username.setOnClickListener(v->{
            openOthersProfile();
        });
    }

    private void loadAds(){
        if(adscount<=maxadscount){
            interstitialAd.loadAd(
                    interstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
        }
    }

    private void openOthersProfile(){
        if(interstitialAd==null || interstitialAd.isAdInvalidated() || !interstitialAd.isAdLoaded()){
            loadAds();
            Intent intent = new Intent(MessageActivity.this, OthersProfile.class);
            intent.putExtra("userId",toDeviceId);
            startActivity(intent);
        }else{
            interstitialAd.show();
        }
    }

    private void seenMessage(String userid){
        reference = FirebaseDatabase.getInstance().getReference().child("globalchat").child("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(deviceId) && chat.getSender().equals(userid)){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // sendMessage
    private void sendMessage(String sender,String receiver,String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);

        reference.child("globalchat").child("Chats").push().setValue(hashMap);
    }
    //

    // readMessages
    private void readMessages(final String myid,final String userid,final String imageurl){
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child("globalchat").child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this,mChat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //

    // status of user
    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference().child("globalchat").child("Users").child(deviceId);

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
    }

    //

}