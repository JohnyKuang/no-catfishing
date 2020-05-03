package com.example.johny.nocatfishing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.johny.nocatfishing.Matches.MatchesAdapter;
import com.example.johny.nocatfishing.Matches.MatchesObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView; //import the recycler view object
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    public String currentUserID;
    public String currentUserSex;
    public String oppositeUserSex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);
        //get current user sex passed from Home activity
        currentUserSex = getIntent().getExtras().getString("userSex");
        oppositeUserSex = getIntent().getExtras().getString("oppositeUserSex");
        getUserMatchId();

        /*
        //for testing; add random objects
        for (int i = 0; i<100; i++){
            MatchesObject obj = new MatchesObject(Integer.toString(i));
            resultsMatches.add(obj);
        }
        mMatchesAdapter.notifyDataSetChanged();
        */
    }

    private void getUserMatchId() {
        DatabaseReference matchDB = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserSex).child(currentUserID).child("connections").child("matches");
        matchDB.addListenerForSingleValueEvent(new ValueEventListener() { //go through the matches
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot match : dataSnapshot.getChildren()){ //fetch info of matches
                        FetchMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FetchMatchInformation(String key) { //getting info for the match
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeUserSex).child(key);
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String userID = dataSnapshot.getKey();
                    String name = dataSnapshot.child("name").getValue().toString();;
                    String profileImageUrl = profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    //make and add obj to list
                    MatchesObject obj = new MatchesObject(userID, name, profileImageUrl);
                    resultsMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private ArrayList<MatchesObject> resultsMatches= new ArrayList<MatchesObject>();
    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }
}
