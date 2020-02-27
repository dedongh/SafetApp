package com.engineerskasa.safetapp.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.engineerskasa.safetapp.Interfaces.ItemClickListener;
import com.engineerskasa.safetapp.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class CategoryAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView categoryName;
    public TextView initialsTextView;

    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CategoryAdapter(@NonNull View itemView) {
        super(itemView);
        categoryName = (TextView) itemView.findViewById(R.id.txt_cat_name);
        initialsTextView = (TextView) itemView.findViewById(R.id.initialsTextView);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition());
    }
}
