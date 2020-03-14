package com.engineerskasa.safetapp.Adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alespero.expandablecardview.ExpandableCardView;
import com.engineerskasa.safetapp.Interfaces.ItemClickListener;
import com.engineerskasa.safetapp.R;

public class ShoppingListAdapter extends RecyclerView.ViewHolder {
    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public ExpandableCardView shop_list_name;
    public RecyclerView my_shop_recycler;
    public CardView item_layout;
    public ShoppingListAdapter(@NonNull View view) {
        super(view);

        shop_list_name = (ExpandableCardView) view.findViewById(R.id.momoExpandable);
        my_shop_recycler = (RecyclerView) view.findViewById(R.id.my_pantry_list);
        item_layout = (CardView) view.findViewById(R.id.item_view);



        /*view.setOnClickListener(new View.OnClickListener() {
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
        });*/

    }
}
