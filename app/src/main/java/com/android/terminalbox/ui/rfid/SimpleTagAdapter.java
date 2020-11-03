package com.android.terminalbox.ui.rfid;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.terminalbox.R;

import java.util.List;

public class SimpleTagAdapter extends RecyclerView.Adapter <SimpleTagAdapter.ViewHolder>{
    private static final String TAG = "TagAdapter";
    private List<String> currentTags;
    private Context mContext;
    public SimpleTagAdapter(List datas) {
        currentTags=datas;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int i) {
//        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_tag,viewGroup,false));
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_epc,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, final int i) {
        String epc = currentTags.get(i);
        viewHolder.mTagEpc.setText(epc);
    }


    @Override
    public int getItemCount() {
        return currentTags == null ? 0 :currentTags.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTagEpc;
        ViewHolder(View view){
            super(view);
            mTagEpc =  view.findViewById(R.id.item_tagepc_epc);
        }
    }
}
