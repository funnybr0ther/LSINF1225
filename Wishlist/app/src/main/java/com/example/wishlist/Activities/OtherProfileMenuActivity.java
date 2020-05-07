package com.example.wishlist.Activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wishlist.Class.User;
import com.example.wishlist.Class.UserDatabaseHelper;
import com.example.wishlist.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherProfileMenuActivity extends AppCompatActivity {

    private int userID;
    private int otherUserID;
    private User otherUser;
    CircleImageView profilePhoto;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.app", Context.MODE_PRIVATE);
        int tmpUserID = prefs.getInt("userID", -1);
        if (tmpUserID != -1) {
            userID = tmpUserID;
        } else {//If no userID go back to LoginActivity
            Toast toast = Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT);
            toast.show();
            Intent backToLogin = new Intent(this, LoginActivity.class);
        }
        //Get the other userID then compare with the actual user ID (-> send user to MyProfile if it's the same)
        Intent intent = getIntent();
        otherUserID=intent.getIntExtra("otherUserID",-1);
        if(otherUserID==userID){
            Intent myProfileIntent=new Intent(this,MyProfileActivity.class);
            startActivity(myProfileIntent);
        }

        setContentView(R.layout.other_profile_menu);
        UserDatabaseHelper dbHelper = new UserDatabaseHelper(getApplicationContext());
        otherUser = dbHelper.getUserFromID(otherUserID);

        profilePhoto = findViewById(R.id.profilePhoto);
        visibleMode();
    }

    @TargetApi(21)
    public void visibleMode() {
        if (otherUser.getProfilePhoto() != null) {
            profilePhoto.setImageBitmap(otherUser.getProfilePhoto());
        } else {
            profilePhoto.setImageDrawable(getDrawable(R.drawable.ic_default_photo));
        }
    }

    public void seeDetails(View view){
        Intent seeProfileIntent = new Intent(this,OtherProfile.class);
        seeProfileIntent.putExtra("otherUserID",otherUserID);
        startActivity(seeProfileIntent);
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }
}