package com.nao.sabina.projectnao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Sabina on 26.06.2016.
 */
public class ImageAdapterServerAction extends BaseAdapter {
    private Context mContext;

    // references to our images
    private ActionFile[] files;

    public ImageAdapterServerAction(Context c) {
        mContext = c;
    }

    public int getCount() {
        if (files == null)
            return 0;
        return files.length;
    }

    public void setFiles(ActionFile[] f){
        this.files = f;
    }

    public Object getItem(int position) {
        return this.files[position];
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(350, 350));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(15, 15, 15, 15);
        } else {
            imageView = (ImageView) convertView;
        }
        byte[] imageBytes = files[position].getImageBytes();
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
        //Picasso.with(mContext).load(mThumbIds[position]).fit().centerInside().into(imageView);

        imageView.setContentDescription(extractFileName(files[position].getFileName()));
        return imageView;
    }

    private String extractFileName(String fileName){
        String[] parts = fileName.split(".");
        return parts[0];
    }
}
