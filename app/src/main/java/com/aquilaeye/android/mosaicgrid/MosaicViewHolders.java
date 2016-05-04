package com.aquilaeye.android.mosaicgrid;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MosaicViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {


    public ImageView countryPhoto;

    public MosaicViewHolders(View itemView) {
        super(itemView);
        countryPhoto = (ImageView) itemView.findViewById(R.id.photo);
    }

    @Override
    public void onClick(View view) {

    }
}
