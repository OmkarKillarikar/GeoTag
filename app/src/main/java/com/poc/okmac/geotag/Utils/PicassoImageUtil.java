package com.poc.okmac.geotag.Utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class PicassoImageUtil {

    private Context context;


    public PicassoImageUtil(Context mContext) {
        this.context = mContext;
    }

    public void setImageWithDefaultAndError(File image, ImageView imageView, int defaultImageId, int errorImageId) {
        if (context != null) {
            Picasso.with(context)
                    .load(image)
                    .placeholder(defaultImageId)
                    .error(errorImageId)
                    .into(imageView);
        }
    }
}