package com.example.johny.nocatfishing.Profile;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.johny.nocatfishing.Profile.UserProfile;
import com.example.johny.nocatfishing.R;

/**
 * Created by Johny on 2018-06-07.
 */

public class ImageAdapter extends PagerAdapter {

    private Context mContext;
    public UserProfile testProfile;
    //ArrayList<Bitmap> imagesArray = testProfile.imagesArray;
    private int[] mImageIds = new int[]{R.drawable.cf, R.drawable.ocean};

    ImageAdapter(Context context){
        mContext = context;
    }
    @Override
    public int getCount() {
        //return imagesArray.size();
        return mImageIds.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(mImageIds[position]);
        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView)object);
    }
}
