package com.example.johny.nocatfishing.Profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.johny.nocatfishing.R;

import java.util.ArrayList;

/**
 * Created by Johny on 2018-08-28.
 */

public class CustomImageSlideAdapter extends PagerAdapter{

    //initialize values bro
    //private int[] image_resources;
    //private Bitmap[] image_resources;
    ArrayList<Bitmap> image_resources = new ArrayList<Bitmap>();
    private Context ctx; //need some context object
    private LayoutInflater layout_inflater;

    //Here is constructor
    public  CustomImageSlideAdapter(Context ctx, ArrayList<Bitmap> image_resources){
        this.ctx = ctx;
        this.image_resources = image_resources;
    }
    @Override
    public int getCount() {
        return image_resources.size();
    }

    @Override
    //verify if object is a linear layout
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //need to initialize layout inflater object here
        layout_inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //must feed the layout inflater a XML layout
        View item_view = layout_inflater.inflate(R.layout.image_slider_layout,container,false);
        ImageView imageView = (ImageView)item_view.findViewById(R.id.image_slider_iv);
        //TextView textView = (TextView)item_view.findViewById(R.id.image_slider_count);
        //now to set resources for the imageView
        //imageView.setImageResource(image_resources[position]);
        imageView.setImageBitmap(image_resources.get(position));
        //finally add item_view to container
        container.addView(item_view);
        return item_view;
    }

    @Override
    //this method will destroy the previous slide when you move from one slide to another
    //this frees up heap memory
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}
