package com.kyee.iwis.nana.task.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyee.iwis.nana.R;
import com.kyee.iwis.nana.task.bean.DoctorInfoModel;
import com.kyee.iwis.nana.utils.CollectionUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskDoctorItemAdapter extends RecyclerView.Adapter {

    private Context mContext;

    private List<DoctorInfoModel> mDoctorList;

    public TaskDoctorItemAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_doctor_item, null);
        return new TaskDoctorItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskDoctorItemHolder itemHolder = (TaskDoctorItemHolder) holder;
        DoctorInfoModel doctorInfoModel = mDoctorList.get(position);
        if (doctorInfoModel != null) {
            itemHolder.tvDoctorName.setText(doctorInfoModel.getName());
            itemHolder.tvDoctorBeds.setText(doctorInfoModel.getBed());
        }
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.isEmpty(mDoctorList) ? 0 : mDoctorList.size();
    }

    public void setData(List<DoctorInfoModel> infoModels) {
        this.mDoctorList = infoModels;
        notifyDataSetChanged();
    }

    class TaskDoctorItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_doctor_name)
        TextView tvDoctorName;
        @BindView(R.id.tv_doctor_beds)
        TextView tvDoctorBeds;

        public TaskDoctorItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
