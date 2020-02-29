package com.engineerskasa.safetapp.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.engineerskasa.safetapp.Interfaces.ItemClickListener;
import com.engineerskasa.safetapp.R;

public class PantryAdapter extends RecyclerView.ViewHolder {

    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ImageView imageCover;
    public TextView txtReadingPercentage;
    public TextView txtItemName;
    public TextView txtCategory;
    public LinearLayout layoutBook;
    public ProgressBar progressBar;
    public RelativeLayout delete_layout;
    public LinearLayout btnDelete;
    public LinearLayout btnClose;
    public TextView initialsTextView;
    public PantryAdapter(@NonNull View view) {
        super(view);
        txtItemName = view.findViewById(R.id.txt_item_name);
        txtCategory = view.findViewById(R.id.txt_category);
        progressBar = view.findViewById(R.id.progress);
        imageCover = (ImageView) view.findViewById(R.id.image_cover);
        txtReadingPercentage = view.findViewById(R.id.txt_reading_percentage);
        layoutBook = view.findViewById(R.id.layout_book);
        delete_layout = (RelativeLayout) view.findViewById(R.id.layout_delete);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnClose = view.findViewById(R.id.btn_close);
        initialsTextView = (TextView) view.findViewById(R.id.initialsTextView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, getAdapterPosition());
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemClickListener.onLongClick(v, getAdapterPosition());
                return false;
            }
        });
    }
}
