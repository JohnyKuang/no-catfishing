package com.example.johny.nocatfishing.Chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.johny.nocatfishing.Home;
import com.example.johny.nocatfishing.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String currentUserID, matchId, chatId;
    private RecyclerView mRecyclerView; //import the recycler view object
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private EditText mSendEditText; //the message line
    private Button mSendButton;

    DatabaseReference mDatabaseUser, mDatabaseChat; //references the ChatID value; and ChatID in "Chat" parent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        matchId = getIntent().getExtras().getString("matchId");//ID for your match -- passed from MatchesViewHolders

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(Home.userSex).child(currentUserID).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        getChatId(); //find chat ID and the messages in the chat

        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendEditText = (EditText)findViewById(R.id.message);
        mSendButton = (Button)findViewById(R.id.send);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();
        if(!sendMessageText.isEmpty()){ //if message line isn't blank
            DatabaseReference newMessageDb = mDatabaseChat.push(); //creates child with unique ID for message (under chatID)

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserID);
            newMessage.put("text", sendMessageText);

            newMessageDb.setValue(newMessage);
        }
        mSendEditText.setText(null);
    }

    private void getChatId(){
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() { //checks if chatId is there
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId); //adds (or goes to) chatID under "Chat"
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    String message = null;
                    String createdByUser = null;
                    if (dataSnapshot.child("text").getValue() != null){
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if (dataSnapshot.child("createdByUser").getValue() != null){
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if ((message != null) && (createdByUser != null)){// check if message is by current user
                        Boolean currentUserBoolean = false;
                        if (createdByUser.equals(currentUserID)){
                            currentUserBoolean = true;
                        }
                        ChatObject newMessage = new ChatObject(message, currentUserBoolean);
                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                    }
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

    private ArrayList<ChatObject> resultsChat= new ArrayList<ChatObject>();
    private List<ChatObject> getDataSetChat() {
        return resultsChat;
    }
}
