package com.example.helloworld;

import android.content.Context;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;


public class ImageAdapter extends BaseAdapter {  
    private Context mContext;  
    private Integer[] mps = {  
            R.drawable.icon1,  
            R.drawable.icon2,  
            R.drawable.icon3,  
            R.drawable.icon4,  
            R.drawable.icon5,
            R.drawable.icon6
    }; 
    public ImageAdapter(Context context) {  
        mContext = context;  
    }  
  
    public int getCount() {   
        return mps.length;  
    }  
  
    public Object getItem(int position) {  
        return position;  
    }  
  
    public long getItemId(int position) {  
        return position;  
    }  
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {  
        ImageView image = new ImageView(mContext);  
        image.setImageResource(mps[position]);  
        image.setAdjustViewBounds(true);  
        image.setLayoutParams(new Gallery.LayoutParams(  
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));  
        return image;  
    }

	//@Override
	//public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//return null;
	//}  
}  