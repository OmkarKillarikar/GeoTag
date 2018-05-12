package com.poc.okmac.geotag.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.poc.okmac.geotag.R;
import com.poc.okmac.geotag.Utils.AppFileManager;
import com.poc.okmac.geotag.Utils.PicassoImageUtil;
import com.poc.okmac.geotag.fragments.GeoTag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public abstract class GeoTagsAdapter extends RecyclerView.Adapter<GeoTagsAdapter.GeoTagHolder> {
    public ArrayList<GeoTag> geoTags;
    private Context context;
    private PicassoImageUtil picassoImageUtil;
    private AppFileManager appFileManager;

    public GeoTagsAdapter(Context context) {
        this.context = context;
        geoTags = new ArrayList<>();
        picassoImageUtil = new PicassoImageUtil(context);
        appFileManager = new AppFileManager(context);
    }

    public abstract void onTap(GeoTag geoTag);

    @NonNull
    @Override
    public GeoTagHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GeoTagHolder(LayoutInflater.from(context).inflate(R.layout.row_geo_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GeoTagHolder holder, int position) {
        final GeoTag geoTag = geoTags.get(holder.getAdapterPosition());
        if (geoTag != null) {
            File image = appFileManager.getExistingFile(geoTag.getImageName());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(image), null, options);
            } catch (Exception e) {
                holder.ivTag.setImageResource(R.drawable.ic_placeholder);
            }
            holder.ivTag.setImageBitmap(bitmap);
            StringBuilder strLatLng = new StringBuilder("Co-ordinates: ");
            if (geoTag.getLatitude() != null) {
                strLatLng.append(geoTag.getLatitude()).append(", ");
            }
            if (geoTag.getLongitude() != null) {
                strLatLng.append(geoTag.getLongitude());
            }

            holder.tvLatlng.setText(strLatLng);

            holder.cvTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTap(geoTag);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (geoTags == null) {
            return 0;
        }
        return geoTags.size();
    }

    class GeoTagHolder extends RecyclerView.ViewHolder {
        ImageView ivTag;
        TextView tvAddress, tvLatlng;
        CardView cvTag;

        GeoTagHolder(View itemView) {
            super(itemView);
            ivTag = itemView.findViewById(R.id.iv_geo_tag);
            tvAddress = itemView.findViewById(R.id.tv_tag_address);
            tvLatlng = itemView.findViewById(R.id.tv_tag_latlng);
            cvTag = itemView.findViewById(R.id.cv_tag);
        }
    }
}
