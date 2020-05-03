package com.example.johny.nocatfishing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.johny.nocatfishing.Cards.arrayAdapter;
import com.example.johny.nocatfishing.Cards.cards;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    //private ArrayList<String> al;
    private cards cards_data[];
    //private ArrayAdapter<String> arrayAdapter;
    private com.example.johny.nocatfishing.Cards.arrayAdapter arrayAdapter; //this is OUR arrayAdapter class
    private int i;

    private FirebaseAuth mAuth;
    private String currentUid;

    ListView listView;
    List<cards> rowItems;

    private DatabaseReference usersDb; //to reference users in DB
    private String  oppositeUserSex;
    public static String userSex; //global across activities

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users"); //referencing all users

        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();


        checkUserSex(); //checks and sets current user sex

        /////////CODE FOR SWIPE CARDS START////////////////
        //set array
        rowItems = new ArrayList<cards>();

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView)findViewById(R.id.frame);
        //fling listener is the thing that does the swiping action
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0); //remove from array
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                cards obj = (cards)dataObject;
                String userID = obj.getUserId();
                //create a nope or yep child on a obj in DB with the current UID when left card is swiped on one of the users
                usersDb.child(oppositeUserSex).child(userID).child("connections").child("nope").child(currentUid).setValue(true);
                Toast.makeText(Home.this, "Left",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                cards obj = (cards)dataObject;
                String userID = obj.getUserId();
                //create a nope or yep child on a obj in DB with the current UID when right card is swiped on one of the users
                usersDb.child(oppositeUserSex).child(userID).child("connections").child("yeps").child(currentUid).setValue(true);
                isConnectionMatch(userID);
                Toast.makeText(Home.this, "Right",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) { //when deck is empty
                // Ask for more data here
//                al.add("XML ".concat(String.valueOf(i)));
//                arrayAdapter.notifyDataSetChanged();
//                Log.d("LIST", "notified");
//                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                //View view = flingContainer.getSelectedView();
                //view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                //view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });
        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                //makeToast(MyActivity.this, "Clicked!");
                Toast.makeText(Home.this, "click",Toast.LENGTH_SHORT).show();
            }
        });
        /////////CODE FOR SWIPE CARDS END////////////////
    }

    private void isConnectionMatch(String userID) { //here we are checking if userID exists under our "yeps" (if they already swiped right on us)
        DatabaseReference currentUserConnectionsDb = usersDb.child(userSex).child(currentUid).child("connections").child("yeps").child(userID);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() { //when we try accessing UID
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){ //if there is a obj. under connection for current user
                    //then we add UIDs under child "matches" for both users involved
                    Toast.makeText(Home.this,"New Connection",Toast.LENGTH_SHORT).show();
                    String connectionUID = dataSnapshot.getKey();
                    usersDb.child(oppositeUserSex).child(connectionUID).child("connections").child("matches").child(currentUid).setValue(true);
                    usersDb.child(userSex).child(currentUid).child("connections").child("matches").child(connectionUID).setValue(true);
                    addChatIDs(connectionUID);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addChatIDs(String connectionUID){
        //creates unique key for UID; doesn't create it though
        String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
        usersDb.child(oppositeUserSex).child(connectionUID).child("connections").child("matches").child(currentUid).child("ChatId").setValue(key);
        usersDb.child(userSex).child(currentUid).child("connections").child("matches").child(connectionUID).child("ChatId").setValue(key);
    }

    public void checkUserSex(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //check if male first
        DatabaseReference maleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("male");
        maleDb.addChildEventListener(new ChildEventListener() {//called when something happens under "male"
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(user.getUid())){//checks if it exists under maleDB; getKey() == UID
                    userSex = "male";//if so then the userSex is male
                    oppositeUserSex = "female";
                    getOppositeSexUsers();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //now for female
        DatabaseReference femaleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("female");
        femaleDb.addChildEventListener(new ChildEventListener() {//called when child added under "female"
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals(user.getUid())){
                    userSex = "female";
                    oppositeUserSex = "male";
                    getOppositeSexUsers();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getOppositeSexUsers() {
        DatabaseReference oppositeSexDb = FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeUserSex);
        oppositeSexDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Boolean beenSwipedLeft = dataSnapshot.child("connections").child("nope").hasChild(currentUid);
                Boolean beenSwipedRight = dataSnapshot.child("connections").child("yeps").hasChild(currentUid);
                if (dataSnapshot.exists() && !beenSwipedLeft && !beenSwipedRight ) {
                    String profileImageUrl = "default";
                    //check if opposing user has image or not, if not, set default Sep.4.2019
                    if (!dataSnapshot.child("profileImageUrl").getValue().equals("default")){
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    };
                    //
                    cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl);
                    rowItems.add(item);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //runs when profile button is clicked
    public void goToProfile(View view){
        Intent intent = new Intent(this,ProfileActivity.class);
        intent.putExtra("userSex", userSex); //using extras to pass a variable into another activity (intent)
        startActivity(intent);
    }

    //runs when matches button is clicked
    public void goToMatches(View view){
        Intent intent = new Intent(this, MatchesActivity.class);
        intent.putExtra("userSex", userSex);
        intent.putExtra("oppositeUserSex", oppositeUserSex);
        startActivity(intent);
    }

    //runs when signout button is clicked
    public void signOut(View view){
        //FirebaseAuth.getInstance().signOut();
        mAuth.signOut(); //need to actually sign out of database
        Intent intent = new Intent(this,MainActivity.class);//take us back to main page
        startActivity(intent);
    }

}

