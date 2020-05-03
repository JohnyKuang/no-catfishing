package com.example.johny.nocatfishing.Chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.johny.nocatfishing.R;

//this class will call every single ID we have under matches
public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mMessage;
    public LinearLayout mContainer;

    public ChatViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMessage = (TextView)itemView.findViewById(R.id.message);
        mContainer = (LinearLayout)itemView.findViewById(R.id.container);

    }
    @Override
    public void onClick(View v) {
    }
}
