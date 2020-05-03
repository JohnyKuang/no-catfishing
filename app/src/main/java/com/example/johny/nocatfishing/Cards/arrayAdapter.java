package com.example.johny.nocatfishing.Cards;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.johny.nocatfishing.R;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<cards>{
    Context context;
    //constructor
    public arrayAdapter(Context context, int resourceId, List<cards> items){
        super(context, resourceId, items); //super(argument1) calls the parent constructor with argument1
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {//will populate the card
        cards card_item = getItem(position); //give us everything within card at this position
        if (convertView == null){ //if the convertView is empty then we will inflate it
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);//inflate the convertView
        }
        //all the items we will populate on card
        TextView name = (TextView) convertView.findViewById(R.id.profile_pic_text); //inflated convertView is the layout now
        ImageView image = (ImageView) convertView.findViewById(R.id.image_view);
        //set values of the items in the cardView
        name.setText(card_item.getName());
        //display image on card (either default or other)
        if (card_item.getProfileImageUrl().equals("default")){
            image.setImageResource(R.drawable.cf);
        } else{
            //Glide is for displaying images off of Urls
            Glide.with(getContext()).load(card_item.getProfileImageUrl()).into(image);
        }


        return convertView;
    }
}
