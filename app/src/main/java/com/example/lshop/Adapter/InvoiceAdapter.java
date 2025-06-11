package com.example.lshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lshop.Domain.ItemModel;
import com.example.lshop.R;

import java.util.ArrayList;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {

    private final ArrayList<ItemModel> items;
    private final Context context;

    public InvoiceAdapter(ArrayList<ItemModel> items, Context context) {
        this.items = items;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, quantity, price;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            quantity = itemView.findViewById(R.id.itemQty);
            price = itemView.findViewById(R.id.itemPrice);
        }
    }

    @Override
    public InvoiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invoice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InvoiceAdapter.ViewHolder holder, int position) {
        ItemModel item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.quantity.setText("x" + item.getNumberinCart());
        holder.price.setText(String.format("$%.2f", item.getPrice() * item.getNumberinCart()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
