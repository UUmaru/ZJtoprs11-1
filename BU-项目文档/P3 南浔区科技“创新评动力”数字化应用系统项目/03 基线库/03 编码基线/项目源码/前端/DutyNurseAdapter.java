package com.kyee.iwis.nana.dutynurse.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyee.iwis.nana.R;
import com.kyee.iwis.nana.nurseassign.bean.BedDutyNurse;
import com.kyee.iwis.nana.utils.BedNumUtil;
import com.kyee.iwis.nana.utils.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import cn.feng.skin.manager.loader.SkinManager;

public class DutyNurseAdapter extends RecyclerView.Adapter {

    private final List<BedDutyNurse> mNurseList;
    private OnEditClickListener mOnEditClickListener;

    public DutyNurseAdapter(List<BedDutyNurse> nurseList) {
        mNurseList = nurseList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_duty_nurse_setting, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return new DutyNurseHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // 交替颜色
        if (position % 2 == 0) {

            ((DutyNurseHolder) holder).itemView.setBackgroundColor(SkinManager.getInstance().getColor(R.color.duty_nurse_edit_page_bind_color));
        } else {

            ((DutyNurseHolder) holder).itemView.setBackgroundColor(SkinManager.getInstance().getColor(R.color.duty_nurse_edit_page_bind_two_color));
        }

        ((DutyNurseHolder) holder).mTvNurseName.setText(mNurseList.get(position).getDutyNurseName());
        ((DutyNurseHolder) holder).mTvNurseId.setText(mNurseList.get(position).getDutyNurseId());
        String bedLabels = String.format("%s%s",
                mNurseList.get(position).getNight() == 1 ? ItemBedAdapter.WAN_BED_LABEL + "，" : "",
                getBedLabelString(mNurseList.get(position).getBedLabelList()));
        if (bedLabels.endsWith("，")) {
            bedLabels = bedLabels.substring(0, bedLabels.length() - 1);
        }
        ((DutyNurseHolder) holder).mTvBedNos.setText(bedLabels);
        ((DutyNurseHolder) holder).mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnEditClickListener.onDeleteClick(v, position);
            }
        });
        ((DutyNurseHolder) holder).mTvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnEditClickListener.onEditClick(v, position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return CollectionUtils.isEmpty(mNurseList) ? 0 : mNurseList.size();
    }

    public void setOnEditClickListener(OnEditClickListener clickListener) {
        mOnEditClickListener = clickListener;
    }


    /**
     * 床号列表
     *
     * @param bedLabelList
     * @return
     */
    private String getBedLabelString(List<String> bedLabelList) {
        if (CollectionUtils.isEmpty(bedLabelList)) {
            return "";
                }
        Collections.sort(bedLabelList, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return BedNumUtil.compareBedLabel(o1, o2);
                    }
                });
        String[] ints = new String[bedLabelList.size()];
        ints = bedLabelList.toArray(ints);
        String bedString = BedNumUtil.convert(ints, 0, "，");
        return bedString.substring(0, bedString.length() - 1);
    }

    class DutyNurseHolder extends RecyclerView.ViewHolder {
        TextView mTvNurseName;
        TextView mTvNurseId;
        TextView mTvBedNos;
        TextView mTvEdit;
        TextView mTvDelete;


        public DutyNurseHolder(View convertView) {
            super(convertView);
            mTvNurseName = (TextView) convertView.findViewById(R.id.tv_nurse_name);
            mTvNurseId = (TextView) convertView.findViewById(R.id.tv_nurse_id);
            mTvBedNos = (TextView) convertView.findViewById(R.id.tv_bed_nos);
            mTvEdit = (TextView) convertView.findViewById(R.id.tv_edit);
            mTvDelete = (TextView) convertView.findViewById(R.id.tv_delete);

        }
    }
}
