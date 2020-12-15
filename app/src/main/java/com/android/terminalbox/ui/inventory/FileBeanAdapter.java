package com.android.terminalbox.ui.inventory;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.terminalbox.R;
import com.android.terminalbox.core.bean.user.EpcFile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileBeanAdapter extends RecyclerView.Adapter <FileBeanAdapter.ViewHolder>{
    private static final String TAG = "TagAdapter";
    private List<EpcFile> currentTags;
    private Context mContext;

    public FileBeanAdapter(List<EpcFile> currentTags, Context mContext) {
        this.currentTags = currentTags;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.inv_item_layout, parent, false);
        return new FileBeanAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, final int i) {
        EpcFile epcFile = currentTags.get(i);
        viewHolder.tvFileName.setText(epcFile.getName());
        viewHolder.tvFileCode.setText(epcFile.getEpcCode());
        if(i % 2 != 0){
            viewHolder.mLayout.setBackgroundColor(mContext.getColor(R.color.inv_item_back));
        }else {
            viewHolder.mLayout.setBackgroundColor(mContext.getColor(R.color.transparent));
        }
    }


    @Override
    public int getItemCount() {
        return currentTags == null ? 0 :currentTags.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_filename)
        TextView tvFileName;
        @BindView(R.id.tv_filecode)
        TextView tvFileCode;
        @BindView(R.id.ll_layout)
        LinearLayout mLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
