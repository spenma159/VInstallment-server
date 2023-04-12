package com.example.vinstallment.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinstallment.PunishmentItem;
import com.example.vinstallment.R;

import java.util.ArrayList;

public class RecylerViewPunishmentItemAdapter extends RecyclerView.Adapter<RecylerViewPunishmentItemAdapter.RecylerViewPunishmentItemHolder> {
    private ArrayList<PunishmentItem> punishmentItems;
    public static class RecylerViewPunishmentItemHolder extends RecyclerView.ViewHolder{
        public TextView idPunishment, textTitlePunishment, textDesPunishment, punishmentEnabled;
        public RecylerViewPunishmentItemHolder(@NonNull View itemView) {
            super(itemView);
            idPunishment = itemView.findViewById(R.id.id_punishment);
            textTitlePunishment = itemView.findViewById(R.id.text_title_punishment);
            textDesPunishment = itemView.findViewById(R.id.text_description_punishment);
            punishmentEnabled = itemView.findViewById(R.id.text_punishment_enabled);
        }
    }

    @NonNull
    @Override
    public RecylerViewPunishmentItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.punishment_item, parent, false);
        return new RecylerViewPunishmentItemHolder(view);
    }

    public RecylerViewPunishmentItemAdapter(ArrayList<PunishmentItem> punishmentItemArrayList){
        punishmentItems = punishmentItemArrayList;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecylerViewPunishmentItemHolder holder, int position) {
        PunishmentItem punishmentItem =  punishmentItems.get(position);
        holder.idPunishment.setText(String.valueOf(punishmentItem.getId()));
        holder.textTitlePunishment.setText(punishmentItem.getTitlePunishmnet());
        holder.textDesPunishment.setText(punishmentItem.getDesPunisment());
        if(punishmentItem.isEnabled()) holder.punishmentEnabled.setText("Aktif");
        else holder.punishmentEnabled.setText("Tidak Aktif");
    }

    @Override
    public int getItemCount() {
        return punishmentItems.size();
    }
}
