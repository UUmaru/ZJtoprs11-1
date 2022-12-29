package com.kyee.iwis.nana.patient.card.action;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kyee.iwis.nana.MainActivity;
import com.kyee.iwis.nana.ParamsConstants;
import com.kyee.iwis.nana.StatusConstants;
import com.kyee.iwis.nana.base.action.PanelListAction;
import com.kyee.iwis.nana.base.net.NetApi;
import com.kyee.iwis.nana.base.net.ResponseCallBack;
import com.kyee.iwis.nana.bedmanage.bedsetting.bedtype.BedTypeBed;
import com.kyee.iwis.nana.bedmanage.bedsetting.bedtype.BedTypeInfoMap;
import com.kyee.iwis.nana.bedmanage.rangebed.BedRange;
import com.kyee.iwis.nana.bedmanage.rangebed.BedRangeInfoDao;
import com.kyee.iwis.nana.common.ConfigSingleton;
import com.kyee.iwis.nana.common.Constant;
import com.kyee.iwis.nana.model.ConfigInfo;
import com.kyee.iwis.nana.model.DeptBean;
import com.kyee.iwis.nana.model.MyPatientsModel;
import com.kyee.iwis.nana.model.PatientInfo;
import com.kyee.iwis.nana.model.PatientStatusInfo;
import com.kyee.iwis.nana.patient.card.bean.PatientCardPanelData;
import com.kyee.iwis.nana.patient.card.bean.StatusData;
import com.kyee.iwis.nana.patient.card.panel.PatientCardPanel;
import com.kyee.iwis.nana.patient.card.view.PatientCardFragment;
import com.kyee.iwis.nana.services.ACacheService;
import com.kyee.iwis.nana.utils.ACache;
import com.kyee.iwis.nana.utils.CollectionUtils;
import com.kyee.iwis.nana.utils.DateUtil;
import com.kyee.iwis.nana.utils.Predicate;
import com.kyee.iwis.nana.utils.ThreadUtils;
import com.kyee.iwis.nana.utils.remind.MessageUtil;
import com.kyee.iwis.nana.widget.timepicker.ColorUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import java8.util.stream.StreamSupport;

import static com.kyee.iwis.nana.widget.timepicker.ColorUtil.zoomImg;

public class PatientCardPanelListAction extends PanelListAction<PatientCardPanel> {
    private static final String TAG = PatientCardPanelListAction.class.getSimpleName();
    private final PatientCardFragment patientCardFragment;

    private Context mContext;
    private Map<String, Drawable> statusBitMapMap =  new HashMap<>();
    private Map<String, GradientDrawable> statusColorMap =  new HashMap<>();
    private Map<String,String> textColorMap = new HashMap<>();
    private String deptCode;
    private List<DeptBean> wardNameList=new ArrayList<>();
    /**
     * 用于患者标签判断
     */
    private final List<Predicate<PatientInfo>> patientTagPredicateList;
    /**
     * 患者个数
     */
    private final List<Long> sizeList = new ArrayList<>();


    public PatientCardPanelListAction(PatientCardFragment patientCardFragment) {
        this.patientCardFragment = patientCardFragment;

    }

    {
        wardNameList=ACacheService.getDeptBeans();
        patientTagPredicateList = new ArrayList<>();
        if(wardNameList.size()>0) {
            patientTagPredicateList.add(patientInfo ->{
                if(wardNameList.get(0).getName().equals(patientInfo.getDeptName())&&TextUtils.isEmpty(patientInfo.getMotherId())){
                    return true;
                }else {
                    return false;
                }
            } );
        }else {
            patientTagPredicateList.add(patientInfo ->{
                return false;
            } );
        }
        if(wardNameList.size()>1) {
            patientTagPredicateList.add(patientInfo -> {
                if(wardNameList.get(1).getName().equals(patientInfo.getDeptName())&&TextUtils.isEmpty(patientInfo.getMotherId())){
                    return true;
                }else {
                    return false;
                }
            });
        }else {
            patientTagPredicateList.add(patientInfo ->{
                return false;
            } );
        }
        if(wardNameList.size()>2) {
            patientTagPredicateList.add(patientInfo -> {
                if(wardNameList.get(2).getName().equals(patientInfo.getDeptName())&&TextUtils.isEmpty(patientInfo.getMotherId())){
                    return true;
                }else {
                    return false;
                }
            });
        }else {
            patientTagPredicateList.add(patientInfo ->{
                return false;
            } );
        }
        if(wardNameList.size()>3) {
            patientTagPredicateList.add(patientInfo ->{
                if(wardNameList.get(3).getName().equals(patientInfo.getDeptName())&&TextUtils.isEmpty(patientInfo.getMotherId())){
                    return true;
                }else {
                    return false;
                }
            } );
        }else {
            patientTagPredicateList.add(patientInfo ->{
                return false;
            } );
        }
        if(wardNameList.size()>4) {
            patientTagPredicateList.add(patientInfo -> {
                if(wardNameList.get(4).getName().equals(patientInfo.getDeptName())&&TextUtils.isEmpty(patientInfo.getMotherId())){
                    return true;
                }else {
                    return false;
                }
            });
        }else {
            patientTagPredicateList.add(patientInfo ->{
                return false;
            } );
        }
        if(wardNameList.size()>5) {
            patientTagPredicateList.add(patientInfo -> {
                if(wardNameList.get(5).getName().equals(patientInfo.getDeptName())&&TextUtils.isEmpty(patientInfo.getMotherId())){
                    return true;
                }else {
                    return false;
                }
            });
        }else {
            patientTagPredicateList.add(patientInfo ->{
                return false;
            } );
        }
        if(wardNameList.size()>6) {
            patientTagPredicateList.add(patientInfo ->{
                if(wardNameList.get(6).getName().equals(patientInfo.getDeptName())&&TextUtils.isEmpty(patientInfo.getMotherId())){
                    return true;
                }else {
                    return false;
                }
            } );
        }else {
            patientTagPredicateList.add(patientInfo ->{
                return false;
            } );
        }
        if(wardNameList.size()>7) {
            patientTagPredicateList.add(patientInfo -> {
                if(wardNameList.get(7).getName().equals(patientInfo.getDeptName())&&TextUtils.isEmpty(patientInfo.getMotherId())){
                    return true;
                }else {
                    return false;
                }
            });
        }else {
            patientTagPredicateList.add(patientInfo ->{
                return false;
            } );
        }
        if(wardNameList.size()>8) {
            patientTagPredicateList.add(patientInfo -> {
                if(wardNameList.get(8).getName().equals(patientInfo.getDeptName())&&TextUtils.isEmpty(patientInfo.getMotherId())){
                    return true;
                }else {
                    return false;
                }
            });
        }else {
            patientTagPredicateList.add(patientInfo ->{
                return false;
            } );
        }
        if(wardNameList.size()>9) {
            patientTagPredicateList.add(patientInfo -> {
                if(wardNameList.get(9).getName().equals(patientInfo.getDeptName())&&TextUtils.isEmpty(patientInfo.getMotherId())){
                    return true;
                }else {
                    return false;
                }
            });
        }else {
            patientTagPredicateList.add(patientInfo ->{
                return false;
            } );
        }
        // 非空床
        patientTagPredicateList.add(new Predicate<PatientInfo>() {
            @Override
            public boolean evaluate(PatientInfo patientInfo1) {
                return !patientInfo1.isEmpty()&&TextUtils.isEmpty(patientInfo1.getMotherId());
            }
        });

        //孕产妇
        patientTagPredicateList.add(patientInfo -> {
                if("1".equals(patientInfo.getIsPregnantWomen())){
                    return true;
                }
            return false;
        });
        //新生儿
        patientTagPredicateList.add(patientInfo -> {
            if(Constant.OBSTETRICS_SHARE_CARD.equals(ConfigInfo.get(ConfigInfo.WARD_FEATURE))) {
                if (CollectionUtils.isEmpty(patientInfo.getBabyInfo())) {
                    return false;
                }
                return true;
            }
            else if(Constant.OBSTETRICS_UNSHARE_CARD.equals(ConfigInfo.get(ConfigInfo.WARD_FEATURE))) {
                if(TextUtils.isEmpty( patientInfo.getMotherId())){
                    return false;
                }
                return true;
            }
            return false;
        });
        //今日入院
        patientTagPredicateList.add(patientInfo -> {
            if (TextUtils.isEmpty(patientInfo.getAdmissionTime()) || "未知".equals(patientInfo.getAdmissionTime())) {
                return false;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            String admissonTime = DateUtil.getTimeString(Long.valueOf(patientInfo.getAdmissionTime()), "yyyy/MM/dd");
            String todayTime = format.format(new Date());
            if (admissonTime.equals(todayTime)&&TextUtils.isEmpty(patientInfo.getMotherId())) {
                return true;
            }
            return false;
        });
        //入院多天
        patientTagPredicateList.add(patientInfo -> {
            if (TextUtils.isEmpty(patientInfo.getAdmissionTime()) || "未知".equals(patientInfo.getAdmissionTime()) || "0".equals(patientInfo.getAdmissionTime())) {
                return false;
            }
            int days = DateUtil.timeSpanDays(patientInfo.getAdmissionTime());
            if (days >= 8&&TextUtils.isEmpty(patientInfo.getMotherId())) {
                return true;
            }
            return false;
        });
        //vip患者
        patientTagPredicateList.add(patientInfo -> "0".equals(patientInfo.getNurseLevel())&&TextUtils.isEmpty(patientInfo.getMotherId()));
        //一级患者
        patientTagPredicateList.add(patientInfo -> "1".equals(patientInfo.getNurseLevel())&&TextUtils.isEmpty(patientInfo.getMotherId()));
        //二级患者
        patientTagPredicateList.add(patientInfo -> "2".equals(patientInfo.getNurseLevel())&&TextUtils.isEmpty(patientInfo.getMotherId()));
        //三级患者
        patientTagPredicateList.add(patientInfo -> "3".equals(patientInfo.getNurseLevel())&&TextUtils.isEmpty(patientInfo.getMotherId()));
        //危险患者
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("危".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        // 严重患者
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("重".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        //今日生日
        patientTagPredicateList.add(this::selectPatientBirth );
        //空床
        patientTagPredicateList.add(new Predicate<PatientInfo>() {
            @Override
            public boolean evaluate(PatientInfo patientInfo1) {
                return patientInfo1.isEmpty();
            }
        });
        // 术 敏 压 跌 食 高 热 危
        patientTagPredicateList.add(patientInfo -> {
            String daysAfterOperation = patientInfo.getDaysAfterOperation();
            int daysOperation = -1;
            if (!TextUtils.isEmpty(daysAfterOperation)) {
                int spotIndex = patientInfo.getDaysAfterOperation().indexOf(".");
                if (spotIndex > -1) {
                    daysAfterOperation = daysAfterOperation.substring(0, spotIndex);
                }
                daysOperation = Integer.parseInt(daysAfterOperation);
            }
            if (daysOperation >= 0 && daysOperation <= 15) {
                return true;
            }
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("术".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    String operationTime = patientInfo.getStatusData().get(i).getOperTimeMill();
                    daysOperation = -1;
                    if (!TextUtils.isEmpty(operationTime)) {
                        daysOperation = (int) ((ConfigSingleton.getInstance().getCurrentTime().getTime() - Long.valueOf(operationTime)) / (1000 * 3600 * 24));
                    }
                    if (daysOperation >= 0 && daysOperation <= 15) {
                        return true;
                    }
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("敏".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("压".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("跌".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("食".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("热".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("高".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("自".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("隔".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });

        patientTagPredicateList.add(patientInfo -> {
            if (!TextUtils.isEmpty(patientInfo.getDaysAfterAdmission())&&TextUtils.isEmpty(patientInfo.getMotherId())) {
                int days = Integer.parseInt(patientInfo.getDaysAfterAdmission());
                if (days > 1 && (days - 1) % 7 == 0) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("栓".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("MDR".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        // ZHBFDEVIMP-223     20210415
        //------------------------ZHBFDEVIMP-223 Start----------------------------
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("陪".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("密".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        //------------------------ZHBFDEVIMP-223 End----------------------------
        //优势病种
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("优".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        //导管滑脱
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("导".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });

        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("暴".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("伤".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("噎".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("离".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        patientTagPredicateList.add(patientInfo -> {
            if (patientInfo.getStatusData() == null) {
                return false;
            }
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                if ("塞".equals(patientInfo.getStatusData().get(i).getStatusName())) {
                    return true;
                }
            }
            return false;
        });
        //出院
        patientTagPredicateList.add(null);
        //转入
        patientTagPredicateList.add(null);
        //转出
        patientTagPredicateList.add(null);
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void doAction() {
        Log.d(TAG, "doAction");
        ThreadUtils.Task<List<PatientInfo>> task = new ThreadUtils.SimpleTask<List<PatientInfo>>() {
            @Nullable
            @Override
            public List<PatientInfo> doInBackground() {
                refreshData();
                initPanelList();
                finish(StatusConstants.STATUS_REFRESH_ALL);
                return transformData(ConfigSingleton.getInstance().getIsMagic());
            }

            @Override
            public void onSuccess(@Nullable List<PatientInfo> result) {
                showPatientList(result);
                MessageUtil.sendMessage(MainActivity.handler, ParamsConstants.CLOSE_SCREEN_PHOTO, null, 0, 0);
            }
        };
        ThreadUtils.executeByCached(task);
    }

    private void refreshData() {
        Log.d(TAG, "refreshData");
        if(!ConfigSingleton.getInstance().getCurrentWardCode().equals(deptCode)){
            statusColorMap.clear();
            statusBitMapMap.clear();
            textColorMap.clear();
        }
        deptCode = ConfigSingleton.getInstance().getCurrentWardCode();
        CountDownLatch countDownLatch = new CountDownLatch(8);
        NetApi.getPatients(ConfigSingleton.getInstance().getDeptCode(), new ResponseCallBack<List<PatientInfo>>(countDownLatch) {
            @Override
            protected void onSuccess(String code, List<PatientInfo> patientInfos) {
                List<BedRange> bedRanges = new BedRangeInfoDao(mContext).loadAll();
                if (CollectionUtils.isNotEmpty(bedRanges)) {
                    for (PatientInfo patientInfo : patientInfos) {
                        for (BedRange bedRange : bedRanges) {
                            if (bedRange.getBedLabel().equals(patientInfo.getBedLabel())) {
                                patientInfo.setOrder(bedRange.getOrder());
                            }
                        }
                    }
                }
                Collections.sort(patientInfos);
                ACacheService.putList("GET_PATIENTS", patientInfos);
                MessageUtil.sendMessage(MainActivity.handler,ParamsConstants.BIRTHDAY_PAT,patientInfos,0,0);
            }

            @Override
            protected void onFail(String code, String msg) {
                Log.e(TAG, "getPatients Fail");
            }

            @Override
            protected void onError(Throwable t) {
                Log.e(TAG, "getPatients Fail");
            }
        });

        NetApi.getLeavePatients(ConfigSingleton.getInstance().getDeptCode(), new ResponseCallBack<List<PatientInfo>>(countDownLatch) {
            @Override
            protected void onSuccess(String code, List<PatientInfo> patientInfos) {
                Collections.sort(patientInfos);
                ACacheService.putList("GET_LEAVE_PATIENTS", patientInfos);
            }

            @Override
            protected void onFail(String code, String msg) {
                Log.e(TAG, "getLeavePatients Fail");
            }

            @Override
            protected void onError(Throwable t) {
                Log.e(TAG, "getLeavePatients Fail");
            }
        });

        NetApi.getInPatients(ConfigSingleton.getInstance().getDeptCode(), new ResponseCallBack<List<PatientInfo>>(countDownLatch) {
            @Override
            protected void onSuccess(String code, List<PatientInfo> patientInfos) {
                Collections.sort(patientInfos);
                ACacheService.putList("GET_IN_PATIENTS", patientInfos);
            }

            @Override
            protected void onFail(String code, String msg) {
                Log.e(TAG, "getInPatients Fail");
            }

            @Override
            protected void onError(Throwable t) {
                Log.e(TAG, "getInPatients Fail");
            }
        });

        NetApi.getOutPatients(ConfigSingleton.getInstance().getDeptCode(), new ResponseCallBack<List<PatientInfo>>(countDownLatch) {
            @Override
            protected void onSuccess(String code, List<PatientInfo> patientInfos) {
                Collections.sort(patientInfos);
                ACacheService.putList("GET_OUT_PATIENTS", patientInfos);
            }

            @Override
            protected void onFail(String code, String msg) {
                Log.e(TAG, "getOutPatients Fail");
            }

            @Override
            protected void onError(Throwable t) {
                Log.e(TAG, "getOutPatients Fail");
            }
        });

        //只有有医护登录时才查询关注患者
        if("科室PIN解锁".equals(ConfigSingleton.getInstance().getCurrentNurseModel().getUserName())){
            countDownLatch.countDown();
        }else {
            NetApi.getAllMyPatients(ConfigSingleton.getInstance().getCurrentNurseModel().getUserId(), new ResponseCallBack<List<MyPatientsModel>>(countDownLatch) {
                @Override
                protected void onSuccess(String code, List<MyPatientsModel> myPatientsModels) {
                    ACacheService.putList("GET_ALLMYPATIENTS", myPatientsModels);
                    initMyPatientList();
                }

                @Override
                protected void onFail(String code, String msg) {
                    Log.e(TAG, "getAllMyPatients Fail");
                }

                @Override
                protected void onError(Throwable t) {
                    Log.e(TAG, "getAllMyPatients Fail");
                }
            });
        }
        if(statusBitMapMap.size() >0 || statusColorMap.size()>0){
            countDownLatch.countDown();
        }else {
            NetApi.getStatusData(ConfigSingleton.getInstance().getCurrentWardCode(), new ResponseCallBack<List<StatusData>>(countDownLatch) {
                @Override
                protected void onSuccess(String code, List<StatusData> statusDatas) {
                    if(CollectionUtils.isNotEmpty(statusDatas)){
                        ConfigSingleton.getInstance().setShowImage(false);
                        for(StatusData statusData:statusDatas){
                            if(!TextUtils.isEmpty(statusData.getImg())){
                                ConfigSingleton.getInstance().setShowImage(true);
                                //有图片则展示图片
                                Bitmap img = ColorUtil.base64ToBitmap(statusData.getImg());
                                if(img != null){
                                    if("男".equals(statusData.getShortName()) || "女".equals(statusData.getShortName())){
                                        statusBitMapMap.put(statusData.getShortName(),new BitmapDrawable(zoomImg(img,100,100,false)));
                                    }else {
                                        statusBitMapMap.put(statusData.getShortName(),new BitmapDrawable(zoomImg(img,100,100,true)));
                                    }
                                }
                            }else {
                                textColorMap.put(statusData.getShortName(),"#"+statusData.getColorCode());
                                if(statusData.getPosition() == 1){
                                    GradientDrawable gradientDrawable = new GradientDrawable();
                                    gradientDrawable.setShape(GradientDrawable.OVAL);
                                    gradientDrawable.setColor(Color.parseColor("#"+statusData.getColorCode()));
                                    statusColorMap.put(statusData.getShortName(),gradientDrawable);
                                }else {
                                    GradientDrawable gradientDrawable = new GradientDrawable();
                                    gradientDrawable.setShape(GradientDrawable.OVAL);
                                    gradientDrawable.setStroke(2,Color.parseColor("#"+statusData.getColorCode()));
                                    statusColorMap.put(statusData.getShortName(),gradientDrawable);
                                }
                            }
                        }
                        ConfigSingleton.textColor = textColorMap;
                    }
                }

                @Override
                protected void onFail(String code, String msg) {

                }

                @Override
                protected void onError(Throwable t) {

                }
            });
        }
        NetApi.getPatientsStatus(new ResponseCallBack<List<PatientInfo>>(countDownLatch) {
            @Override
            protected void onSuccess(String code, List<PatientInfo> patientInfos) {

                ACacheService.putList("GET_PATIENTSSTATUS", patientInfos);
            }

            @Override
            protected void onFail(String code, String msg) {
                Log.e(TAG, "getPatientsStatus Fail");
            }

            @Override
            protected void onError(Throwable t) {
                Log.e(TAG, "getPatientsStatus Fail");
            }
        });
            {
            NetApi.getBedInfos(ConfigSingleton.getInstance().getCurrentWardCode(), new ResponseCallBack<List<BedTypeBed>>(countDownLatch) {
                @Override
                protected void onSuccess(String code, List<BedTypeBed> bedTypeBeds) {
                    BedTypeInfoMap.getInstance().setBedTypeMap(bedTypeBeds);
                }

                @Override
                protected void onFail(String code, String msg) {

                }

                @Override
                protected void onError(Throwable t) {

                }
            });
        }
        waitForRequest(countDownLatch);
    }



    private void waitForRequest(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException", e);
            Thread.currentThread().interrupt();
        }
    }


    private void initMyPatientList() {
        List<PatientInfo> myPatientsList = new ArrayList<>();
        List<MyPatientsModel> allMyPatientList1 = ACacheService.getAllMyPatientList();
        for (int i = 0; i < allMyPatientList1.size(); i++) {
            if (allMyPatientList1.get(i).getNurseId().equals(ConfigSingleton.getInstance().getCurrentNurseModel().getUserId())) {
                myPatientsList = allMyPatientList1.get(i).getPatients();
                break;
            }
        }
        List<PatientInfo> patientList = ACacheService.getPatients();
        for (int i = 0; i < myPatientsList.size(); i++) {
            if (myPatientsList.get(i) != null) {
                for (int j = 0; j < patientList.size(); j++) {
                    if (patientList.get(j).getPatientId() != null && patientList.get(j).getPatientId() != "" && patientList.get(j).getPatientId().equals(myPatientsList.get(i).getPatientId())) {
                        myPatientsList.set(i, patientList.get(j));
                        break;
                    //任务ZHBFDEVIMP-165修改
                    }else if(patientList.get(j).getBedLabel().equals(myPatientsList.get(i).getBedLabel())){
                        myPatientsList.set(i, patientList.get(j));
                        break;
                    }
                }
            } else {
                myPatientsList.remove(i);
            }
        }
        Collections.sort(myPatientsList);
        PatientInfo[] array_mine_patientInfo = new PatientInfo[myPatientsList.size()];
        myPatientsList.toArray(array_mine_patientInfo);

        ACache.get(mContext).put("MYPATIENTLIST", array_mine_patientInfo);
    }

    public List<Long> getSizeList() {
        return sizeList;
    }

    private void initPanelList() {
        int size = patientTagPredicateList.size();
        //    List<PatientCardPanel> patientCardPanels = new ArrayList<>();
        int totalPanelSize = 0;
        //全部
        totalPanelSize++;
        //patientCardPanels.add(new PatientCardPanel(patientCardFragment));
        //患者分类
        for (int i = 0; i < size; i++) {
            totalPanelSize++;
        }
        //转出
        totalPanelSize++;
        //我的关注
        totalPanelSize++;
        if (getPanelList() == null) {
            setPanelList(new ArrayList<>());
        }
        int reduce = getPanelList().size() - totalPanelSize;
        if (reduce > 0) {
            while (reduce > 0) {
                int curSize = getPanelList().size();
                getPanelList().remove(curSize - 1);
                reduce--;
            }
        } else {
            while (reduce < 0) {
                getPanelList().add(new PatientCardPanel(patientCardFragment));
                reduce++;
            }
        }
    }

    private List<PatientInfo> transformData(boolean isMagic) {
        List<PatientInfo> patientList = ACacheService.getPatients();
        List<PatientInfo> patientStatusList = ACacheService.getPatientStatusList();
        patientList = reBuildPatientData(patientList, patientStatusList);
        //我的关注
        List<PatientInfo> minePatientList;
        if (!ParamsConstants.DEPARTMENT_PIN_UNLOCK.equals(ConfigSingleton.getInstance().getCurrentNurseModel().getUserName())) {
            minePatientList = ACacheService.getMyPatientList();
        } else {
            minePatientList = new ArrayList<>();
        }
        if (isMagic) {
            //魔术棒选中只显示我关注的患者
            patientList = (List<PatientInfo>) CollectionUtils.select(patientList, minePatientList::contains);
        }
        //去除bedlabel为空的数据
        List<PatientInfo> tempList = new ArrayList<>();
        CollectionUtils.selectRejected(patientList, patientInfo -> "0".equals(patientInfo.getBedLabel()), tempList);
        return tempList;
    }


    /**
     * 重新构建病人信息
     * todo Refactor this method to reduce its Cognitive Complexity from 22 to the 15 allowed.
     */
    private List<PatientInfo> reBuildPatientData(List<PatientInfo> patientList, List<PatientInfo> patientStatusList) {
        List<PatientInfo> resultPatList2 = new ArrayList<>();
        if (null != patientList) {
            for (PatientInfo tempPat : patientList) {
                tempPat.setStatusData(new ArrayList<>());
                if (1 == ConfigSingleton.getInstance().getPatientCardMode()
                        || 3 == ConfigSingleton.getInstance().getPatientCardMode()
                        || 2 == ConfigSingleton.getInstance().getPatientCardMode()
                        || ConfigSingleton.CARD_MODE_VAS==ConfigSingleton.getInstance().getPatientCardMode()
                        || ConfigSingleton.CARD_STYLE_SAMPLE == ConfigSingleton.getInstance().getPatientCardMode()) {
                    for (PatientInfo tempStu : patientStatusList) {
                        if (tempPat.getPatientId().equals(tempStu.getPatientId()) && tempPat.getVisitId().equals(tempStu.getVisitId())) {
                            tempPat.setStatusData(tempStu.getStatusData());
                            getPatientOperationName(tempPat);
                            break;
                        }
                    }
                }
                resultPatList2.add(tempPat);
            }
        }
        return resultPatList2;
    }

    /**
     * 用病人状态中查询出来的手术信息， 替换病人信息中的手术信息
     */
    private void getPatientOperationName(PatientInfo patientInfo) {
        if (null != patientInfo && null != patientInfo.getStatusData()) {
            List<PatientStatusInfo> statusArray = new ArrayList<>();
            for (int i = 0; i < patientInfo.getStatusData().size(); i++) {
                PatientStatusInfo info = patientInfo.getStatusData().get(i);
                //如果这个状态没有名称也没有数组数据，则进行删除，在界面上也不需要显示
                if ("".equals(info.getOperName()) && CollectionUtils.isEmpty(info.getSubArray())) {
                    continue;
                } else if ("术".equals(info.getStatusName()) && !"".equals(info.getOperName())) {
                    patientInfo.setOperationName(info.getOperName());
                }
                statusArray.add(info);
            }
            patientInfo.setStatusData(statusArray);
        }
    }

    private void showPatientList(List<PatientInfo> tempList) {
        sizeList.clear();
        long sizePatientInfo = StreamSupport.stream(tempList).filter(patientInfo -> !patientInfo.isEmpty()).count();
        sizeList.add(sizePatientInfo);
        //全部患者
        int size = patientTagPredicateList.size();
        getPanelList().get(0).setData(mContext, PatientCardPanelData.builder()
                .patientInfoList(tempList)
                .bitmapMap(statusBitMapMap)
                .colorMap(statusColorMap)
                .textColorMap(textColorMap)
                .build());
        //患者分类
        for (int i = 0; i < size; i++) {
            if (i == 44) {
                //出院患者
                List<PatientInfo> leavePatientInfos = ACacheService.getLeavePatients();
                getPanelList().get(i + 1).setData(mContext, PatientCardPanelData.builder()
                        .patientInfoList(leavePatientInfos)
                        .bitmapMap(statusBitMapMap)
                        .colorMap(statusColorMap)
                        .textColorMap(textColorMap)
                        .build());
                sizeList.add((long) CollectionUtils.size(leavePatientInfos));
            } else if (i == 45) {
                //转入
                List<PatientInfo> inPatientInfos = ACacheService.getInPatients();
                getPanelList().get(i + 1).setData(mContext, PatientCardPanelData.builder()
                        .patientInfoList(inPatientInfos)
                        .bitmapMap(statusBitMapMap)
                        .colorMap(statusColorMap)
                        .textColorMap(textColorMap)
                        .build());
                sizeList.add((long) CollectionUtils.size(inPatientInfos));
            } else if (i == 46) {
                //转出
                List<PatientInfo> outPatientInfos = ACacheService.getOutPatients();
                getPanelList().get(i + 1).setData(mContext, PatientCardPanelData.builder()
                        .patientInfoList(outPatientInfos)
                        .bitmapMap(statusBitMapMap)
                        .colorMap(statusColorMap)
                        .textColorMap(textColorMap)
                        .build());
                sizeList.add((long) CollectionUtils.size(outPatientInfos));
            } else {
                List<PatientInfo> patientInfoList = (List<PatientInfo>) CollectionUtils.select(tempList, patientTagPredicateList.get(i));
                getPanelList().get(i + 1).setData(mContext, PatientCardPanelData.builder()
                        .patientInfoList(patientInfoList)
                        .bitmapMap(statusBitMapMap)
                        .colorMap(statusColorMap)
                        .textColorMap(textColorMap)
                        .build());
                sizeList.add((long) CollectionUtils.size(patientInfoList));
            }

        }

        getPanelLayout().onLayout(getPanelList());
    }

    private void magicStickChange(boolean isMagic) {
        ThreadUtils.Task<List<PatientInfo>> task = new ThreadUtils.SimpleTask<List<PatientInfo>>() {
            @Nullable
            @Override
            public List<PatientInfo> doInBackground() {
                initPanelList();
                return transformData(isMagic);
            }

            @Override
            public void onSuccess(@Nullable List<PatientInfo> result) {
                showPatientList(result);
            }
        };
        ThreadUtils.executeByCached(task);
    }

    /**
     * 筛选今天患者生日
     */
    private boolean selectPatientBirth(PatientInfo patientInfo) {
        //查询患者生日信息
        Date date = ConfigSingleton.getInstance().getCurrentTime();
        String today = new SimpleDateFormat("MM-dd", Locale.getDefault()).format(date);
        if (!TextUtils.isEmpty(patientInfo.getDateOfBirth())&&TextUtils.isEmpty(patientInfo.getMotherId())) {
            String birthday = DateUtil.millisecondToDate(patientInfo.getDateOfBirth(), new SimpleDateFormat("MM-dd", Locale.getDefault()));
            String todayDay = new SimpleDateFormat("yyMMdd", Locale.getDefault()).format(ConfigSingleton.getInstance().getCurrentTime());
            String patBirDay = DateUtil.millisecondToDate(patientInfo.getDateOfBirth(), new SimpleDateFormat("yyMMdd", Locale.getDefault()));
            //当天出生的患者不计入生日提醒统计
            if (todayDay.equals(patBirDay)) {
                return false;
            }
            return birthday.equals(today);
        }
        return false;
    }

    @Override
    public void onChange(String status, int value) {
        if (StatusConstants.STATUS_REFRESH_ALL.equals(status) && value != 0) {
            doAction();
        } else if (StatusConstants.STATUS_ISMAGIC.equals(status)) {
            magicStickChange(value == 1);
        } else if (StatusConstants.STATUS_CLOSE_ALL_DIALOG.equals(status)) {
            patientCardFragment.closeDialog();
        }
    }
}
