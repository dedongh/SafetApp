package com.engineerskasa.safetapp.Adapter;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.engineerskasa.safetapp.R;

public class ShopperRecyclerAdapter extends RecyclerView.ViewHolder {
    public TextView txtItemName;
    public TextView txtUnits;
    public EditText edt_quantity;
    public EditText edt_price;
    public ImageButton save_to_cart;
    public ShopperRecyclerAdapter(@NonNull View view) {
        super(view);
        edt_quantity = (EditText) view.findViewById(R.id.shp_qty_buy);
        edt_price = (EditText) view.findViewById(R.id.edt_price);
        save_to_cart = (ImageButton) view.findViewById(R.id.btn_save_cart);
        txtItemName = (TextView) view.findViewById(R.id.txt_item_name);
        txtUnits = (TextView) view.findViewById(R.id.spn_units);
    }
}
