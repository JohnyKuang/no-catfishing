package com.example.johny.nocatfishing.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.johny.nocatfishing.Chat.ChatActivity;
import com.example.johny.nocatfishing.R;

//this class will call every single ID we have under matches
public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mMatchId, mMatchName;
    public ImageView mMatchImage;

    public MatchesViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId = (TextView)itemView.findViewById(R.id.Matchid);
        mMatchName = (TextView)itemView.findViewById(R.id.MatchName);
        mMatchImage = (ImageView)itemView.findViewById(R.id.MatchImage);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), ChatActivity.class);
        Bundle b = new Bundle();//bundle to move info to new activity
        b.putString("matchId", mMatchId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }
}
