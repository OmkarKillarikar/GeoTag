package com.poc.okmac.geotag.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.poc.okmac.geotag.R;
import com.poc.okmac.geotag.Utils.PicassoImageUtil;
import com.poc.okmac.geotag.fragments.GeoTag;

import java.util.ArrayList;
import java.util.List;

public class GeoTagsAdapter extends RecyclerView.Adapter<GeoTagsAdapter.GeoTagHolder> {
    public ArrayList<GeoTag> geoTags;
    private Context context;
    private PicassoImageUtil picassoImageUtil;

    public GeoTagsAdapter(Context context) {
        this.context = context;
        geoTags = new ArrayList<>();
        picassoImageUtil = new PicassoImageUtil(context);
    }

    @NonNull
    @Override
    public GeoTagHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GeoTagHolder(LayoutInflater.from(context).inflate(R.layout.row_geo_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GeoTagHolder holder, int position) {
        GeoTag geoTag = geoTags.get(holder.getAdapterPosition());
        if (geoTag != null) {

            picassoImageUtil.setImageWithDefaultAndError();
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
        GeoTagHolder(View itemView) {
            super(itemView);
            ivTag = itemView.findViewWithTag(R.id.iv_geo_tag);
            tvAddress = itemView.findViewById(R.id.tv_tag_address);
            tvLatlng = itemView.findViewById(R.id.tv_tag_latlng);
        }
    }
}
