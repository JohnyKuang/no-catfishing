package com.example.johny.nocatfishing.Chat;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.johny.nocatfishing.R;

import java.util.List;

//this adapter is responsible for populating every recycler view item with info
public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders>{
    private List<ChatObject> chatList;
    private Context context;

    //constructor
    public ChatAdapter(List<ChatObject> chatList, Context context){
        this.chatList = chatList;
        this.context = context;
    }

    @Override
    public ChatViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolders rcv = new ChatViewHolders((layoutView));
        return rcv;
    }

    @Override
    public void onBindViewHolder(ChatViewHolders holder, int position) {
        holder.mMessage.setText(chatList.get(position).getMessage());
        if (chatList.get(position).getCurrentUser()){ //if message is from current user
            holder.mMessage.setGravity(Gravity.END); //makes message go to the right end
            holder.mMessage.setTextColor(Color.parseColor("#404040"));//color of message
            holder.mContainer.setBackgroundColor(Color.parseColor("#F4F4F4"));//color of container
        } else { //for other user
            holder.mMessage.setGravity(Gravity.START); //makes message go to the left
            holder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));
            holder.mContainer.setBackgroundColor(Color.parseColor("#2DB4C8"));
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
