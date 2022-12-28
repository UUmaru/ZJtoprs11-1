package com.kyee.iwis.nana.task.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyee.iwis.nana.R;
import com.kyee.iwis.nana.task.bean.TaskDoctorModel;
import com.kyee.iwis.nana.utils.CollectionUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskDoctorAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<TaskDoctorModel> mTaskDoctorList;

    public TaskDoctorAdapter(Context context, List<TaskDoctorModel> taskDoctorList) {
        this.mContext = context;
        this.mTaskDoctorList = taskDoctorList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_task_doctor, null);
        return new TaskDoctorHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskDoctorHolder doctorHolder = (TaskDoctorHolder) holder;
        TaskDoctorModel doctorModel = mTaskDoctorList.get(position);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        doctorHolder.recyclerViewDoctor.setLayoutManager(layoutManager);
        TaskDoctorItemAdapter itemAdapter = new TaskDoctorItemAdapter(mContext);
        doctorHolder.recyclerViewDoctor.setAdapter(itemAdapter);
        if (doctorModel != null) {
            doctorHolder.tvDoctorGroup.setText(doctorModel.getGroupName());
            itemAdapter.setData(doctorModel.getDoctor());
        } else {
            doctorHolder.tvDoctorGroup.setText("");
            itemAdapter.setData(null);
        }
        if (position == mTaskDoctorList.size() - 1) {
            doctorHolder.tvDoctorLine.setVisibility(View.GONE);
        } else {
            doctorHolder.tvDoctorLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.isEmpty(mTaskDoctorList) ? 0 : mTaskDoctorList.size();
    }

    class TaskDoctorHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_doctor_group)
        TextView tvDoctorGroup;
        @BindView(R.id.recycler_item_doctor)
        RecyclerView recyclerViewDoctor;
        @BindView(R.id.tv_doctor_line)
        TextView tvDoctorLine;

        public TaskDoctorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
