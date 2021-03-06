package com.example.wishlist.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wishlist.Class.FollowDatabaseHelper;
import com.example.wishlist.Class.User;
import com.example.wishlist.Class.UserDatabaseHelper;
import com.example.wishlist.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherProfileMenuActivity extends AppCompatActivity {

    private int userID;
    private int receiverID;
    private User otherUser;
    private CircleImageView profilePhoto;
    private TextView titleToolbar;
    private Button followButton;
    private Button unfollowButton;

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
        receiverID =intent.getIntExtra("receiverID",-1);
        setContentView(R.layout.other_profile_menu);

        UserDatabaseHelper dbHelperU = new UserDatabaseHelper(getApplicationContext());
        profilePhoto = findViewById(R.id.profilePhoto);
        otherUser = dbHelperU.getUserFromID(receiverID);

        followButton = findViewById(R.id.followButton);
        unfollowButton = findViewById(R.id.unfollowButton);

        actualiseButtons();
        visibleMode();
    }

    public void actualiseButtons(){
        //Makes unfollow button visible and hides follow button id user already followed and vice versa
        FollowDatabaseHelper dbHelperF = new FollowDatabaseHelper(getApplicationContext());

        if(dbHelperF.checkIfFollows(userID, receiverID)){
            followButton.setVisibility(View.GONE);
            unfollowButton.setVisibility(View.VISIBLE);
        }else{
            unfollowButton.setVisibility(View.GONE);
            followButton.setVisibility(View.VISIBLE);
        }
    }

    @TargetApi(21)
    public void visibleMode() {
        if (otherUser.getProfilePhoto() != null) {
            profilePhoto.setImageBitmap(otherUser.getProfilePhoto());
        } else {
            profilePhoto.setImageDrawable(getDrawable(R.drawable.ic_default_photo));
        }
        titleToolbar = findViewById(R.id.TitleOtherProfileToolbar);
        String title= otherUser.getFirstName()+" " + otherUser.getLastName();
        titleToolbar.setText(title);
    }

    public void seeDetails(View view){
        Intent seeProfileIntent = new Intent(this,OtherProfile.class);
        seeProfileIntent.putExtra("otherUserID", receiverID);
        startActivity(seeProfileIntent);
    }

    public void onBackPressed(View view) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void goToFriendWishlist(View view){
        Log.d("SUPEER", "goToFriendWishlist: " + receiverID);
        Intent intent = new Intent(this,ListWishlistActivity.class);
        intent.putExtra("receiverID", receiverID); //id de celui a qui appartient les wishlist
        intent.putExtra("userID", userID); //id de celui qui consulte les wishlist de ses amis
        intent.putExtra("isMyWishlist",false);
        startActivity(intent);
    }


    public void followCurrentUser(View view){
        String[] relationships = {"Friend","Family","Colleague"};
        final ArrayAdapter<String> adp = new ArrayAdapter<String>(OtherProfileMenuActivity.this,
                android.R.layout.simple_spinner_item, relationships);

        final Spinner sp = new Spinner(OtherProfileMenuActivity.this);
        sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sp.setAdapter(adp);
        sp.setPadding(100,0,110,0);


        AlertDialog.Builder builder = new AlertDialog.Builder(OtherProfileMenuActivity.this);
        builder.setView(sp);
        builder.setTitle("Choose relationship");
        builder.setMessage("What is your relationship with "+otherUser.getFirstName()+"?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                FollowDatabaseHelper helper = new FollowDatabaseHelper(getApplicationContext());
                String rel = sp.getSelectedItem().toString();
                helper.addFollow(userID,receiverID,rel);
                actualiseButtons();
            }
        });
        builder.create().show();
    }

    public void unfollowCurrentUser(View view){
        FollowDatabaseHelper helper = new FollowDatabaseHelper(getApplicationContext());
        helper.unfollow(userID,receiverID);
        actualiseButtons();
    }
}
