package com.kyee.iwis.nana.base.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kyee.iwis.nana.advicesearch.bean.TotalOrderBean;
import com.kyee.iwis.nana.ai.advice.bean.AdviceBean;
import com.kyee.iwis.nana.anniversary.AnniversaryStaffDto;
import com.kyee.iwis.nana.bedmanage.bedsetting.bedtype.BedType;
import com.kyee.iwis.nana.bedmanage.bedsetting.bedtype.BedTypeBed;
import com.kyee.iwis.nana.billboard.BillboardItemBean;
import com.kyee.iwis.nana.callremind.bean.InfusionMessage;
import com.kyee.iwis.nana.callremind.bean.OriginCallInfo;
import com.kyee.iwis.nana.common.ConfigSingleton;
import com.kyee.iwis.nana.common.bean.SubTaskBean;
import com.kyee.iwis.nana.config.bean.SystemConfig;
import com.kyee.iwis.nana.controller.widget.remind.model.AIAdvicePatient;
import com.kyee.iwis.nana.controller.widget.task.bean.TaskBean;
import com.kyee.iwis.nana.dutynurse.bean.DutyNurseUpdateModel;
import com.kyee.iwis.nana.dynamic.bean.DutyInfoModel;
import com.kyee.iwis.nana.dynamic.bean.DutyInfoPersonModel;
import com.kyee.iwis.nana.dynamic.bean.ItemPhone;
import com.kyee.iwis.nana.dynamic.bean.RemarkLabelBean;
import com.kyee.iwis.nana.dynamic.bean.WritingBoardInsertBean;
import com.kyee.iwis.nana.dynamic.bean.WritingBoardOutputBean;
import com.kyee.iwis.nana.exchange.bean.ExchangeInfo;
import com.kyee.iwis.nana.faceland.FaceLandModel;
import com.kyee.iwis.nana.faceland.FacePhotoModel;
import com.kyee.iwis.nana.generalsigns.bean.SignsData;
import com.kyee.iwis.nana.infusion.bean.InfusionDto;
import com.kyee.iwis.nana.infusion.bean.InfusionSetting;
import com.kyee.iwis.nana.infusion.bean.InfusionSettingBean;
import com.kyee.iwis.nana.infusion.bean.XSAInfusionDto;
import com.kyee.iwis.nana.inspectionarrange.bean.InspectionArrangeModel;
import com.kyee.iwis.nana.model.AddMemos;
import com.kyee.iwis.nana.model.AddOutModel;
import com.kyee.iwis.nana.model.AdviceModel;
import com.kyee.iwis.nana.model.CancelFixedModel;
import com.kyee.iwis.nana.model.ConfigInfo;
import com.kyee.iwis.nana.model.ConfigModel;
import com.kyee.iwis.nana.model.ConfigOption;
import com.kyee.iwis.nana.model.ConfigViewModel;
import com.kyee.iwis.nana.model.DeptBean;
import com.kyee.iwis.nana.model.HospitalModel;
import com.kyee.iwis.nana.model.IgnoreDetailModel;
import com.kyee.iwis.nana.model.ItemCountsModel;
import com.kyee.iwis.nana.model.MemoModel;
import com.kyee.iwis.nana.model.MyPatientsModel;
import com.kyee.iwis.nana.model.NurseModel;
import com.kyee.iwis.nana.model.PatIgnoreModel;
import com.kyee.iwis.nana.model.PatientInfo;
import com.kyee.iwis.nana.model.ProjectConfigModel;
import com.kyee.iwis.nana.model.RemarkPhoneModel;
import com.kyee.iwis.nana.model.RetrofitModel;
import com.kyee.iwis.nana.model.SyncOrderModel;
import com.kyee.iwis.nana.model.UnifyModel;
import com.kyee.iwis.nana.nurseassign.bean.AllDutyNurse;
import com.kyee.iwis.nana.nurseassign.bean.DutyNurseBed;
import com.kyee.iwis.nana.nurseassign.bean.NurseGroup;
import com.kyee.iwis.nana.nurseassign.bean.NurseScheduleList;
import com.kyee.iwis.nana.nursefile.bean.EduTheme;
import com.kyee.iwis.nana.patient.card.bean.PatientOrderRequestDTO;
import com.kyee.iwis.nana.patient.card.bean.StatusData;
import com.kyee.iwis.nana.scheduled.ScheduledTaskUtil;
import com.kyee.iwis.nana.services.RetrofitApi;
import com.kyee.iwis.nana.skin.SkinBean;
import com.kyee.iwis.nana.statistics.DatePageTaskModel;
import com.kyee.iwis.nana.statistics.NurseTaskItemModel;
import com.kyee.iwis.nana.statistics.StatisticsNurseModel;
import com.kyee.iwis.nana.statistics.TaskDetailModel;
import com.kyee.iwis.nana.surgicalArrangement.bean.SurgicalBean;
import com.kyee.iwis.nana.surgicalArrangement.bean.SurgicalLearningBean;
import com.kyee.iwis.nana.task.bean.BedOrderModel;
import com.kyee.iwis.nana.task.bean.TaskBedModel;
import com.kyee.iwis.nana.task.bean.TaskDoctorModel;
import com.kyee.iwis.nana.utils.L;
import com.kyee.iwis.nana.utils.MacUtil;
import com.kyee.iwis.nana.utils.ThreadUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetApi {
    public static final String TAG = NetApi.class.getSimpleName();
    private static QCNetService qcNetService;

    private static XinShangAnInfusionNetService xsaInfusionService;

    public static QCNetService getQCNetService() {
        if (qcNetService == null) {
            OkHttpClient client = new OkHttpClient.Builder().
                    addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).
                    connectTimeout(5, TimeUnit.SECONDS).
                    readTimeout(20, TimeUnit.SECONDS).
                    writeTimeout(20, TimeUnit.SECONDS).build();
            Retrofit.Builder qcBuilder;
            if (!TextUtils.isEmpty(ConfigSingleton.getInstance().getQcServerUrl())) {
                ConfigSingleton.getInstance().setQcServerUrl(ConfigSingleton.getInstance().getQcServerUrl().startsWith("http://") ?
                        ConfigSingleton.getInstance().getQcServerUrl() : "http://" + ConfigSingleton.getInstance().getQcServerUrl());
                ConfigSingleton.getInstance().setQcServerUrl(ConfigSingleton.getInstance().getQcServerUrl().endsWith("/") ?
                        ConfigSingleton.getInstance().getQcServerUrl() : ConfigSingleton.getInstance().getQcServerUrl() + "/");
            }
            try {
                qcBuilder = new Retrofit.Builder()
                        .baseUrl(ConfigSingleton.getInstance().getQcServerUrl());
            } catch (IllegalArgumentException | NullPointerException e) {
                //????????????????????????????????????
                ConfigSingleton.getInstance().setQcServerUrl("");
                return null;
            }
            qcNetService = qcBuilder
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(QCNetService.class);
        }
        return qcNetService;
    }

    public static void refrshQCNetService() {
        qcNetService = null;
        getQCNetService();
    }

    /**
     * ??????RetrofitService
     */
    private static RetrofitApi retrofitApi;
    /**
     * ????????????????????????
     */
    private static final String PAGE_TASK = "2";
    /**
     * ????????????????????????
     */
    private static final String PAGE_DYNAMIC = "3";

    /**
     * ?????????????????????
     *
     * @param callBack
     */
    public static void getSettings(BaseResponseCallBack<RetrofitModel<List<ConfigModel>>> callBack) {
        getRetrofitApi().getSettings(ConfigSingleton.getInstance().getCurrentWardCode() + ":::" + ConfigSingleton.getInstance().getHospitalCode()).enqueue(callBack);
    }

    /**
     * ?????????????????????????????????
     *
     * @param preSurgicalPrepare ?????????????????????
     */
    public static void updatePreSurgicalPrepare(String surgicalCode,String preSurgicalPrepare, ResponseCallBack<String> callBack) {
        getRetrofitApi().updatePreSurgicalPrepare(surgicalCode,preSurgicalPrepare).enqueue(callBack);
    }

    /**
     * ?????????????????????
     *
     * @param callBack
     */
    public static void getTaskProjects(int value, final ResponseCallBack<List<UnifyModel>> callBack) {
        getRetrofitApi().getProjects(ConfigSingleton.getInstance().getCurrentWardCode(), value).enqueue(callBack);
    }

    public static void getTaskBedList(final ResponseCallBack<TaskBedModel> callBack) {
        getRetrofitApi().getTaskBedList(ConfigSingleton.getInstance().getCurrentWardCode(), ConfigSingleton.getInstance().getCurrentNurseModel().getUserId()).enqueue(callBack);
    }

    public static void getTaskBedOrder(int recId, final ResponseCallBack<BedOrderModel> callBack) {
        getRetrofitApi().getTaskBedOrder(recId).enqueue(callBack);
    }

    public static void saveOrderRemark(int recId, String remarkVal, final ResponseCallBack callBack) {
        getRetrofitApi().saveOrderRemark(recId, remarkVal).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param callBack
     */
    public static void getTaskProjectConfig(final ResponseCallBack<ProjectConfigModel> callBack) {
        getRetrofitApi().getProjectConfig(ConfigSingleton.getInstance().getCurrentWardCode(), PAGE_TASK).enqueue(callBack);
    }

    /**
     * ????????????????????????
     *
     * @param callBack
     */
    public static void getProjectCounts(final ResponseCallBack<List<ItemCountsModel>> callBack) {
        getRetrofitApi().getProjectCounts(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }


    //??????????????????????????????
    public static void getAiAdvices(ResponseCallBack<AdviceBean> callBack) {
        Call call = getRetrofitApi().getAiAdvices(ConfigSingleton.getInstance().getCurrentWardCode());
        call.enqueue(callBack);
    }

    /**
     * ????????????????????????
     *
     * @param callBack
     */
    public static void getAiAdvicesTag(ResponseCallBack<List<List<AdviceModel>>> callBack) {
        getRetrofitApi().getAiAdvicesTag(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param deptCode
     * @param callback
     */
    public static void getAiAdviceFromTask(String deptCode, String recId, ResponseCallBack<AdviceBean> callback) {
        getRetrofitApi().getAiAdviceFromTask(deptCode, recId).enqueue(callback);
    }


    /**
     * ????????????????????????
     *
     * @param userId
     * @param password
     * @param deptCode
     * @param callBack
     */
    public static void loginByPassword(final String userId, final String password, final String deptCode,
                                       final ResponseCallBack<NurseModel> callBack) {
        getRetrofitApi().loginByPassword(userId, password, deptCode).enqueue(callBack);
    }

    /**
     * ???????????????
     *
     * @param deptCode
     * @param callBack
     */
    public static void getQrcode(final String deptCode, final ResponseCallBack<String> callBack) {
        getRetrofitApi().getQrcode(deptCode).enqueue(callBack);
    }


    /**
     * ????????????????????????
     *
     * @param callBack
     */
    public static void getFiexdProjects(final ResponseCallBack<List<UnifyModel>> callBack) {
        getRetrofitApi().getFiexdProjectsByLocal(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }

    /**
     * ???????????????
     *
     * @param callBack
     */
    public static void getPhones(final ResponseCallBack<List<RemarkPhoneModel>> callBack) {
        getRetrofitApi().getPhones(ConfigSingleton.getInstance().getCurrentWardCode(), ConfigSingleton.getInstance().getHospitalCode()).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param callBack
     */
    public static void getDutyInfos(final ResponseCallBack<List<DutyInfoModel>> callBack) {
        getRetrofitApi().getDutyInfos(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }

    /**
     * ????????????????????????
     *
     * @param callBack
     */
    public static void getDutyPersonInfos(final ResponseCallBack<List<DutyInfoPersonModel>> callBack) {
        getRetrofitApi().getDutyPersonInfos(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param callBack
     */
    public static void saveDutyInfos(List<DutyInfoModel.TableCellContent> cellContents, final ResponseCallBack<List<Object>> callBack) {
        getRetrofitApi().saveDutyInfos(ConfigSingleton.getInstance().getCurrentNurseModel().getCardId(),
                new Gson().toJson(cellContents)).enqueue(callBack);
    }

    /**
     * ???????????????
     *
     * @param callBack
     */
    public static void getMemo(final ResponseCallBack<List<MemoModel>> callBack) {
        getRetrofitApi().getMemo(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }

    /**
     * ?????????????????????????????????
     *
     * @param callBack
     */
    public static void getDynamicProjectConfig(final ResponseCallBack<ProjectConfigModel> callBack) {
        getRetrofitApi().getProjectConfig(ConfigSingleton.getInstance().getCurrentWardCode(), PAGE_DYNAMIC).enqueue(callBack);
    }

    /**
     * ????????????5????????????????????????
     *
     * @param callBack
     */
    public static void getMemoLastFive(final ResponseCallBack<List<MemoModel>> callBack) {
        getRetrofitApi().getMemoLast5(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }


    /**
     * ??????????????????
     */
    public static void getPatients(String deptCode, final ResponseCallBack<List<PatientInfo>> callBack) {
        getRetrofitApi().getPatients(ConfigSingleton.getInstance().getCurrentWardCode(), deptCode).enqueue(callBack);
    }

//    /**
//     * ????????????????????????
//     */
//    public static void getOutPatients(final ResponseCallBack<List<PatientInfo>> callBack) {
//        getRetrofitApi().getOutPatients(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
//    }

    /**
     * ??????????????????????????????
     */
    public static void getCandidateOutPatients(final ResponseCallBack<List<PatientInfo>> callBack) {
        getRetrofitApi().getCandidateOutPatients(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }

    /**
     * ???????????????????????????
     */
    public static void getAllMyPatients(String nurseId, final ResponseCallBack<List<MyPatientsModel>> callBack) {
        getRetrofitApi().getAllMyPatients(ConfigSingleton.getInstance().getCurrentWardCode(), nurseId).enqueue(callBack);
    }

    /**
     * ????????????????????????
     */
    public static void getPatientsStatus(final ResponseCallBack<List<PatientInfo>> callBack) {
        getRetrofitApi().getPatientsStatus(ConfigSingleton.getInstance().getCurrentWardCode(), ConfigInfo.get(ConfigInfo.IDENTIFIER)).enqueue(callBack);
    }


    /**
     * ???????????????????????????????????????
     *
     * @param callBack
     */
    public static void getServerVersion(final ResponseCallBack<String> callBack) {
        getRetrofitApi().getServerVersion().enqueue(callBack);
    }


    /**
     * ???????????????
     */
    public static void getDepts(final String mac, MainThreadCallBack<List<HospitalModel>> callBack) {
        Call call = getRetrofitApi().getDepts(mac);
        call.enqueue(callBack);
    }


    /**
     * ???????????????????????????
     */
    public static void getPushUrl(ResponseCallBack<String> callBack) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Object>() {
            @Nullable
            @Override
            public Object doInBackground() throws Throwable {
                Call call = getRetrofitApi().getPushUrl();
                call.enqueue(callBack);
                return null;
            }

            @Override
            public void onSuccess(@Nullable Object result) {

            }
        });
    }

    /**
     * ??????????????????
     *
     * @param deptCode   ?????????
     * @param pageNumber ??????????????????
     * @param deptCode   ?????????
     * @param callBack
     */
    public static void updateConfig(@NonNull String deptCode, @NonNull String pageNumber,
                                    final List<ConfigViewModel> configViewModels, final ResponseCallBack<Object> callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(configViewModels));
        getRetrofitApi().updateConfig(deptCode, pageNumber, body).enqueue(callBack);
    }

    /**
     * ?????????????????????
     *
     * @param resonseCallBack
     */
    public static void getServerTime(ResponseCallBack<String> resonseCallBack) {
        getRetrofitApi().getServerTime().enqueue(resonseCallBack);
    }


    /**
     * ????????????????????????
     *
     * @param remarkPhoneModels
     * @param callBack
     */
    public static void updatePhoneConfig(String deptCode, final List<RemarkPhoneModel> remarkPhoneModels, final ResponseCallBack<Object> callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(remarkPhoneModels));
        getRetrofitApi().updatePhoneConfig(deptCode, body).enqueue(callBack);
    }

    /**
     * ?????????????????????
     *
     * @param list
     * @param callBack
     */
    public static void addTask(final List<TaskBean> list, final ResponseCallBack<Object> callBack) {
        getRetrofitApi().addTask(list).enqueue(callBack);
    }

    /**
     * ????????????
     *
     * @param detailId
     * @param callBack
     */
    public static void finishTask(final String detailId, final String detailRemark, final ResponseCallBack<Object> callBack) {
        getRetrofitApi().finishTask(detailId, detailRemark).enqueue(callBack);
    }

    /**
     * ????????????
     *
     * @param map
     * @param callBack
     */
    public static void updateTask(final Map<String, Object> map, final ResponseCallBack<Object> callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(map));
        getRetrofitApi().updateTask(body).enqueue(callBack);
    }

    /**
     * ????????????
     *
     * @param deptCode
     * @param recId
     * @param isIndividual ??????????????????
     * @param callBack
     */
    public static void editTask(String deptCode, String recId, String userId, boolean isIndividual, String oriOrder, final ResponseCallBack<Float> callBack) {
        getRetrofitApi().editTask(deptCode, recId, userId, isIndividual, oriOrder).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param cancelFixedModel
     * @param callBack
     */
    public static void cancelTask(CancelFixedModel cancelFixedModel, final ResponseCallBack<Object> callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(cancelFixedModel));
        getRetrofitApi().cancelTask(body).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param map
     * @param callBack
     */
    public static void updatePhone(Map<String, String> map, final ResponseCallBack<Object> callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(map));
        getRetrofitApi().updatePhone(body).enqueue(callBack);
    }

    /**
     * ????????????
     *
     * @param groupId
     * @param nurseId
     * @param nurseName
     * @param deptCode
     * @param topTime
     * @param callBack
     */
    public static void topMemo(String groupId, String nurseId, String nurseName, String deptCode, String topTime, final ResponseCallBack<Object> callBack) {
        getRetrofitApi().topMemo(groupId, nurseId, nurseName, deptCode, topTime).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param groupId
     * @param nurseId
     * @param nurseName
     * @param deptCode
     * @param callBack
     */
    public static void cancelTopMemo(String groupId, String nurseId, String nurseName, String deptCode, final ResponseCallBack<Object> callBack) {
        getRetrofitApi().cancelTopMemo(groupId, nurseId, nurseName, deptCode).enqueue(callBack);
    }

    /**
     * ????????????
     *
     * @param groupId
     * @param nurseId
     * @param nurseName
     * @param deptCode
     * @param callBack
     */
    public static void cancelMemo(String groupId, String nurseId, String nurseName, String deptCode, final ResponseCallBack<Object> callBack) {
        getRetrofitApi().cancelMemo(groupId, nurseId, nurseName, deptCode).enqueue(callBack);
    }

    /**
     * ????????????
     *
     * @param groupId
     * @param nurseId
     * @param nurseName
     * @param deptCode
     * @param callBack
     */
    public static void revokeMemo(String groupId, String nurseId, String nurseName, String deptCode, final ResponseCallBack<Object> callBack) {
        getRetrofitApi().revokeMemo(groupId, nurseId, nurseName, deptCode).enqueue(callBack);
    }

    /**
     * ????????????
     *
     * @param aiAdviceModel
     */
    public static Response<RetrofitModel<Object>> ignoreDecision(List<AIAdvicePatient> aiAdviceModel) throws IOException {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(aiAdviceModel));
        return getRetrofitApi().ignoreDecision(body).execute();
    }

    /**
     * ?????????????????????
     *
     * @param adviceId
     * @param addMemos
     */
    public static void adviceToMemo(String adviceId, List<AddMemos> addMemos) throws IOException {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(addMemos));
        getRetrofitApi().adviceToMemo(adviceId, body).execute();
    }

    /**
     * ????????????
     *
     * @param timeStamp
     * @param macAddress
     * @param content
     * @param callBack
     */
    public static void saveFrontLog(String timeStamp, String macAddress, String content, final ResponseCallBack<Object> callBack) {
        getRetrofitApi().saveFrontLog(timeStamp, macAddress, content).enqueue(callBack);
    }

    /**
     * ?????????????????????
     *
     * @param list
     * @param callBack
     */
    public static void backIgnore(List<IgnoreDetailModel> list, final ResponseCallBack<Object> callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(list));
        getRetrofitApi().backIgnore(body).enqueue(callBack);
    }


    /**
     * ??????mac
     *
     * @param callBack
     */
    public static void bindMac(String mac, String hospitalCode, String deptCode, final ResponseCallBack<Object> callBack) {
        getRetrofitApi().bindMac(mac, hospitalCode, deptCode).enqueue(callBack);
    }

    /**
     * ????????????????????????
     *
     * @param callBack
     */
    public static void addLoginCount(final ResponseCallBack callBack) {
        String meetingTimes = String.valueOf((Integer.parseInt(ConfigSingleton.getInstance().getCurrentNurseModel().getMeetingTimes()) + 1));
        getRetrofitApi().addLoginCount(ConfigSingleton.getInstance().getCurrentNurseModel().getCardId(),
                ConfigSingleton.getInstance().getCurrentNurseModel().getUserId(),
                meetingTimes).enqueue(callBack);
    }

    /**
     * ????????????????????????
     *
     * @param addMemos
     * @param callBack
     */
    public static void addMemos(AddMemos addMemos, final BaseResponseCallBack callBack) {
        getRetrofitApi().addMemos(addMemos).enqueue(callBack);
    }


    /**
     * ????????????????????????????????????
     *
     * @param callBack
     */
    public static void getBedsWithIgoreOrders(final BaseResponseCallBack callBack) {
        getRetrofitApi().getBedsWithIgoreOrders(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }

    /**
     * ????????????????????????
     */
    public static void getDetailOrders(List<PatIgnoreModel> list, final BaseResponseCallBack callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(list));
        getRetrofitApi().getDetailOrders(ConfigSingleton.getInstance().getCurrentWardCode(), body).enqueue(callBack);
    }


    /**
     * ???????????????
     *
     * @param wardCode
     * @param callback
     */
    public static void getDutyNursePlan(String wardCode, ResponseCallBack<List<NurseGroup>> callback) {
        getRetrofitApi().getDutyNursePlan(wardCode).enqueue(callback);
    }

    /**
     * ????????????????????????
     *
     * @param wardCode
     * @param callback
     */
    public static void getDutyNursePlanNew(String wardCode, ResponseCallBack<List<DutyNurseBed>> callback) {
        getRetrofitApi().getDutyNursePlanNew(wardCode).enqueue(callback);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param subTaskList
     * @param callBack
     */
    public static void setSubTaskOrder(List<SubTaskBean> subTaskList, final ResponseCallBack<Object> callBack) {
        getRetrofitApi().setSubTaskOrder(subTaskList).enqueue(callBack);
    }


    /**
     * ???????????????
     *
     * @param optionList
     * @param callBack
     */
    public static void saveConfig(List<ConfigOption> optionList, final BaseResponseCallBack callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(optionList));
        getRetrofitApi().saveConfig(body).enqueue(callBack);
    }

    /**
     * ?????????????????????????????????
     *
     * @param list
     * @param callBack
     */
    public static void saveNurseAttention(List<Map<String, String>> list, final BaseResponseCallBack callBack) {
        getRetrofitApi().saveNurseAttention(ConfigSingleton.getInstance().getCurrentWardCode(), ConfigSingleton.getInstance().getCurrentNurseModel().getUserId(), new Gson().toJson(list)).enqueue(callBack);
    }

    /**
     * ????????????????????????????????????
     *
     * @param list
     * @param callBack
     */
    public static void insertAttention(List<AddOutModel> list, final BaseResponseCallBack callBack) {
        getRetrofitApi().insertAttention(ConfigSingleton.getInstance().getCurrentWardCode(), new Gson().toJson(list)).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param patientOrderList
     * @param callBack
     */
    public static void setPatientOrder(List<PatientOrderRequestDTO> patientOrderList, final ResponseCallBack<String> callBack) {
        getRetrofitApi().setPatientOrder(patientOrderList).enqueue(callBack);
    }

    private static RetrofitApi getRetrofitApi() {
        return retrofitApi;
    }

    public static void setRetrofitApi(RetrofitApi retrofitApi) {
        NetApi.retrofitApi = retrofitApi;
    }

    /**
     * ??????????????????
     *
     * @param userId
     * @param facePhotoModel
     * @param callBack
     */
    public static void bindFaceData(String userId, FacePhotoModel facePhotoModel, final ResponseCallBack callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(facePhotoModel));
        getRetrofitApi().bindFaceData(userId, body).enqueue(callBack);
    }

    public static void getAllNurse(String deptCode, final ResponseCallBack<List<NurseModel>> callBack) {
        getRetrofitApi().getAllNurse(deptCode).enqueue(callBack);
    }

    public static void deleteFaceData(String userId, final ResponseCallBack callBack) {
        getRetrofitApi().deleteFaceData(userId).enqueue(callBack);
    }

    /**
     * ?????????????????????
     *
     * @param callback
     */
    public static void getSystemConfig(String version, ResponseCallBack<SystemConfig> callback) {
        getRetrofitApi().getSystemConfig(ConfigSingleton.getInstance().getCurrentWardCode(), ConfigSingleton.getInstance().getCurrentWardName(),
                MacUtil.getMac(), version).enqueue(callback);
    }


    /**
     * ????????????????????????????????????
     *
     * @param wardNo
     * @param callBack
     */
    public static void getQcCallInfo(String wardNo, final BaseResponseCallBack<RetrofitModel<OriginCallInfo>> callBack) {
        if (getQCNetService() == null) {
            return;
        }
        getQCNetService().getQcCallInfo(wardNo).enqueue(callBack);
    }

    /**
     * ????????????????????????????????????
     *
     * @param wardNos
     * @param callBack
     */
    public static void getWardsQcCallInfo(List<String> wardNos, final BaseResponseCallBack<RetrofitModel<OriginCallInfo>> callBack) {
        if (getQCNetService() != null) {
            getQCNetService().getWardsQcCallInfo(wardNos).enqueue(callBack);
        }
    }


    /**
     * ????????????????????????
     *
     * @param keyword
     * @param callBack
     */
    public static void getSearchPhoneList(String keyword, ResponseCallBack<List<ItemPhone>> callBack) {
        getRetrofitApi().getSearchPhoneList(keyword).enqueue(callBack);
    }

    /**
     * ???????????????????????????
     * @param wardCode      ?????????
     * @param propertyNames ?????????
     * @param callBack      ??????
     */
    public static void getInfusionSetting(String wardCode, String[] propertyNames, InfusionResponseCallBack<InfusionSetting> callBack) {
        if (getQCNetService() != null) {
            getQCNetService().getInfusionSetting(wardCode, propertyNames).enqueue(callBack);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param settingBeans ??????????????????????????????
     * @param callBack     ??????
     * @return ?????????
     */
    public static void saveInfusionSetting(List<InfusionSettingBean> settingBeans, InfusionResponseCallBack<String> callBack) {
        if (getQCNetService() != null) {
            getQCNetService().getInfusionSetting(settingBeans).enqueue(callBack);
        }
    }

    /**
     * ??????????????????
     *
     * @param wardCode ?????????
     */
    public static void getInfusionList(String wardCode, InfusionResponseCallBack<List<InfusionDto>> callBack) {
        if (getQCNetService() != null) {
            getQCNetService().getInfusionList(wardCode).enqueue(callBack);
        }
    }

    public static void getWardsInfusionList(List<String> wardNos, InfusionResponseCallBack<List<InfusionDto>> callBack) {
        if (getQCNetService() != null) {
            getQCNetService().getWardsInfusions(wardNos).enqueue(callBack);
        }
    }

    /**
     * ?????????????????????
     *
     * @param wardCode
     * @param bedNo
     * @param deviceId
     * @param settingBeans
     * @param callBack
     */
    public static void setInfusionLimit(String wardCode, String bedNo, String deviceId, List<InfusionSettingBean> settingBeans, InfusionResponseCallBack callBack) {
        if (getQCNetService() != null) {
            getQCNetService().setInfusionLimit(wardCode, bedNo, deviceId, settingBeans).enqueue(callBack);
        }
    }

    /**
     * ??????????????????
     *
     * @param wardCode
     * @param callBack
     */
    public static void getWardSubDepts(String wardCode, ResponseCallBack<List<DeptBean>> callBack) {
        getRetrofitApi().getWardSubDepts(wardCode).enqueue(callBack);
    }

    public static void getAnniversaryDoctor(String deptCode, String hospitalName, ResponseCallBack<List<AnniversaryStaffDto>> callBack) {
        getRetrofitApi().getAnniversaryDoctor(deptCode, hospitalName).enqueue(callBack);
    }

    public static void finishOrderTask(SyncOrderModel syncOrderModel, ResponseCallBack<Object> callBack) {
        getRetrofitApi().finishOrderTask(new Gson().toJson(syncOrderModel)).enqueue(callBack);
    }

    /**
     * ???????????????
     *
     * @param callBack
     */
    public static void getBillboardData(ResponseCallBack<List<BillboardItemBean>> callBack) {
        getRetrofitApi().getBillboardData(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }

    /**
     * ???????????????
     */
    public static void getReservedPats(String deptCode, ResponseCallBack<List<PatientInfo>> callBack) {
        getRetrofitApi().getReservedPatientInfo(deptCode).enqueue(callBack);
    }

    /**
     * ?????????
     */
    public static void rerangeBed(String pid, String vid, String deptCode, String newBed, String oldBed,
                                  String nurseName, ResponseCallBack<Object> callBack) {
        getRetrofitApi().rerangeBed(pid, vid, deptCode, newBed, oldBed, nurseName).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param wardNo
     * @param callBack
     */
    public static void getNurseSchedules(String wardNo, final ResponseCallBack<List<NurseScheduleList>> callBack) {
        getRetrofitApi().getNurseSchedule(wardNo).enqueue(callBack);
    }

    /**
     * ??????get???set??????
     *
     * @param url
     * @param callBack
     */
    public static void execGet(String url, ResponseCallBack callBack) {
        getQCNetService().execGet(url).enqueue(callBack);
    }

    public static void execPost(String url, ResponseCallBack callBack) {
        getQCNetService().execPost(url).enqueue(callBack);
    }

    public static void getInfusionDetail(String wardCode, String bedNo, String deviceId, InfusionResponseCallBack<InfusionDto> callBack) {
        if (getQCNetService() != null) {
            getQCNetService().getInfusionDetail(wardCode, bedNo, deviceId).enqueue(callBack);
        }
    }

    public static void getPatientViewPage(String patientId, ResponseCallBack<String> callBack) {
        getRetrofitApi().getPatientViewPage(patientId).enqueue(callBack);
    }

    public static void getExchangeReport(String deptCode, ResponseCallBack<List<ExchangeInfo>> callBack) {
        getRetrofitApi().getExchangeReport(deptCode).enqueue(callBack);
    }

    /**
     * ????????????????????????
     *
     * @param dutyNurseUpdateModel ??????????????????????????????
     * @param callBack             ???????????????
     */
    public static void updateDutyNurseList(final DutyNurseUpdateModel dutyNurseUpdateModel,
                                           final ResponseCallBack<Object> callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),
                new Gson().toJson(dutyNurseUpdateModel));
        getRetrofitApi().updateDutyNurseList(body).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param wardNo
     * @param callBack
     */
    public static void getAnnouncement(String wardNo, final ResponseCallBack<String> callBack) {
        getRetrofitApi().getAnnouncement(wardNo).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param wardNo
     * @param announcement
     * @param callBack
     */
    public static void saveAnnouncement(String wardNo, String announcement, final ResponseCallBack callBack) {
        getRetrofitApi().saveAnnouncement(wardNo, announcement).enqueue(callBack);
    }

    public static void getNurseFile(String deptCode, ResponseCallBack<List<EduTheme>> callBack) {
        getRetrofitApi().getNurseFile(deptCode).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param callBack
     */
    public static void sendFaceLandMsg(FaceLandModel faceLandModel, final ResponseCallBack callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),
                new Gson().toJson(faceLandModel));
        getRetrofitApi().sendFaceLandMsg(body).enqueue(callBack);
    }

    public static void getPersonFace(String userId, final ResponseCallBack callBack) {
        getRetrofitApi().getPersonFace(userId).enqueue(callBack);
    }

    /**
     * ????????????????????????
     * @param bedLabel ??????
     * @return ??????
     */
//    public static Observable<RetrofitModel<String>> getBedNote(String wardNo, String bedLabel) {
//        return getRetrofitApi().getBedNote(wardNo, bedLabel);
//    }

    /**
     * ??????????????????
     *
     * @param wardNo
     * @param bedLabel
     * @param remark
     * @return
     */
    public static void saveReservedRemark(String wardNo, String deptCode, String bedLabel, String remark
            , final ResponseCallBack callBack) {
        getRetrofitApi().saveReservedRemark(wardNo, deptCode, bedLabel, remark).enqueue(callBack);
    }

    public static void getDeptMemoLabel(String deptCode, final ResponseCallBack<List<RemarkLabelBean>> callBack) {
        getRetrofitApi().getDeptMemoLabel(deptCode).enqueue(callBack);
    }

    public static void saveOneDeptMemoLabel(RemarkLabelBean remarkLabelBean, final ResponseCallBack callBack) {
        getRetrofitApi().saveOneDeptMemoLabel(remarkLabelBean).enqueue(callBack);
    }

    public static void saveDeptMemoLabelList(String deptCode, List<RemarkLabelBean> remarkLabelBeanList, final ResponseCallBack callBack) {
        getRetrofitApi().saveDeptMemoLabelList(deptCode, remarkLabelBeanList).enqueue(callBack);
    }

    /**
     * ????????????????????????
     *
     * @param callBack ??????
     */
    public static void fetchSurgicalArrangement(ResponseCallBack<List<SurgicalBean>> callBack) {
        getRetrofitApi().fetchSurgicalArrangement(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }

    /**
     * ??????????????????????????????
     *
     * @param learningBeans ?????????????????????
     */
    public static void updateSurgicalLearningStatus(List<SurgicalLearningBean> learningBeans, ResponseCallBack<String> callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),
                new Gson().toJson(learningBeans));
        getRetrofitApi().updateSurgicalLearningStatus(body).enqueue(callBack);
    }


    /**
     * ?????????????????????????????????
     *
     * @param startTime
     * @param endTime
     * @param callBack
     */
    public static void searchSurgicalByDate(String startTime, String endTime, ResponseCallBack<List<SurgicalBean>> callBack) {
        getRetrofitApi().searchSurgicalByDate(startTime, endTime, ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }


    /**
     * ????????????????????????
     *
     * @param messageList
     * @param callBack
     */
    public static void handleInfusionWarn(List<InfusionMessage> messageList, final ResponseCallBack callBack) {
        getQCNetService().handleInfusionWarn(messageList).enqueue(callBack);
    }

    public static void queryStatisticsData(String deptCode, String startDate, String endDate, final ResponseCallBack<List<StatisticsNurseModel>> callBack) {
        getRetrofitApi().queryStatisticsData(deptCode, startDate, endDate).enqueue(callBack);
    }

    public static void getStatistDetailData(int pageSize, int page, String deptCode, String startDate, String endDate, String itemCode, final ResponseCallBack<NurseTaskItemModel> callBack) {
        getRetrofitApi().getStatistDetailData(pageSize, page, deptCode, startDate, endDate, itemCode).enqueue(callBack);
    }

    public static void getTaskDataByPage(int pageSize, int page, String deptCode, String startDate, String endDate, String itemCode, final ResponseCallBack<DatePageTaskModel> callBack) {
        getRetrofitApi().getTaskDataByPage(pageSize, page, deptCode, startDate, endDate, itemCode).enqueue(callBack);
    }

    /**
     * ????????????
     *
     * @param queryMap
     * @param callBack
     */
    public static void queryStatisticsOrderData(Map<String, String> queryMap, int pageSize, int page, final ResponseCallBack<TotalOrderBean> callBack) {
        getRetrofitApi().queryOrderInfos(queryMap, pageSize, page).enqueue(callBack);
    }

    /**
     * ?????????????????????
     *
     * @param deptCode
     * @param callBack
     */
    public static void getSignsTimePoint(String deptCode, ResponseCallBack<List<String>> callBack) {
        getRetrofitApi().getSignsTimePoint(deptCode).enqueue(callBack);
    }

    public static void getSignsData(String deptCode, String date, String timePoint, boolean isDutyLinked, String nurseCode, ResponseCallBack<SignsData> callBack) {
        getRetrofitApi().getSignsData(deptCode, date, timePoint, isDutyLinked, nurseCode).enqueue(callBack);
    }

    public static void getBedOrderDetail(String deptCode, String orderNo, String date, ResponseCallBack<TaskDetailModel> callBack) {
        getRetrofitApi().getBedOrderDetail(deptCode, orderNo, date).enqueue(callBack);
    }

    public static void getStatusData(String deptCode, ResponseCallBack<List<StatusData>> callback) {
        getRetrofitApi().getNursingFlag(deptCode).enqueue(callback);
    }

    /**
     * ??????????????????
     *
     * @param callBack
     */
    public static void getDoctorInfo(ResponseCallBack<List<TaskDoctorModel>> callBack) {
        getRetrofitApi().getDoctorInfo(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param callBack
     */
    public static void getInspectionArrangeData(ResponseCallBack<InspectionArrangeModel> callBack) {
        getRetrofitApi().getInspectionArrangeData(ConfigSingleton.getInstance().getCurrentWardCode()).enqueue(callBack);
    }

    public static void sendPOSTRequest(String path) {
        ScheduledTaskUtil.getInstance().start(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(path);
                    HttpResponse httpResponse = httpClient.execute(post);
                    int status = httpResponse.getStatusLine().getStatusCode();
                    if (status == 200) {
                        Log.i(TAG, "success");
                    } else {
                        Log.i(TAG, "fail");
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    L.writeToFileDoBack('q', "request", path + " error " + e.getMessage());
                }
            }
        }, 0);
    }

    public static void sendGETRequest(String path) {
        ScheduledTaskUtil.getInstance().start(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(path);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        Log.i(TAG, "success");
                    } else {
                        Log.i(TAG, "fail");
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    L.writeToFileDoBack('q', "request", path + " error " + e.getMessage());
                }
            }
        }, 0);
    }

    /**
     * ?????????????????????
     *
     * @param writingBoardInsertBean
     * @param callBack
     */
    public static void saveWritingBoardInfo(WritingBoardInsertBean writingBoardInsertBean, final ResponseCallBack callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(writingBoardInsertBean));
        getRetrofitApi().saveWritingBoardInfo(body).enqueue(callBack);
    }

    /**
     * ????????????????????????????????????
     *
     * @param deptCode
     * @param boardTag
     * @param callBack
     */
    public static void getWritingBoardInfo(String deptCode, String boardTag, final ResponseCallBack<WritingBoardOutputBean> callBack) {
        getRetrofitApi().getWritingBoardInfo(deptCode, boardTag).enqueue(callBack);
    }


    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param mid      ????????????
     * @param callBack
     */
    public static void doMsgPositionCallback(long mid, final ResponseCallBack callBack) {
        getRetrofitApi().doMsgPositionCallback(mid).enqueue(callBack);
    }


    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param mid      ????????????
     * @param callBack
     */
    public static void doMsgNegativeCallback(long mid, final ResponseCallBack callBack) {
        getRetrofitApi().doMsgNegativeCallback(mid).enqueue(callBack);
    }


    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param mid      ????????????
     * @param callBack
     */
    public static void doMsgNetureCallback(long mid, final ResponseCallBack callBack) {
        getRetrofitApi().doMsgNetureCallback(mid).enqueue(callBack);
    }

    /**
     * ?????????????????????
     *
     * @param callBack
     */
    public static void updateProjects(Map<String, Object> map, final ResponseCallBack<Object> callBack) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(map));
        getRetrofitApi().updateProjects(body).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param deleteBedTypes
     * @param saveBedTypes
     * @param deleteBedInfos
     * @param saveBedInfos
     * @param callBack
     */
    public static void saveBedTypesAndBedInfos(List<BedType> deleteBedTypes, List<BedType> saveBedTypes, List<BedTypeBed> deleteBedInfos, List<BedTypeBed> saveBedInfos, final ResponseCallBack callBack) {
        getRetrofitApi().saveBedTypesAndBedInfos(ConfigSingleton.getInstance().getCurrentNurseModel().getUserId(),
                new Gson().toJson(deleteBedTypes), new Gson().toJson(saveBedTypes),
                new Gson().toJson(deleteBedInfos), new Gson().toJson(saveBedInfos)).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param deptCode
     * @param callBack
     */
    public static void getBedTypes(String deptCode, final ResponseCallBack<List<BedType>> callBack) {
        getRetrofitApi().getBedTypes(deptCode).enqueue(callBack);
    }

    /**
     * ??????????????????
     *
     * @param deptCode
     * @param callBack
     */
    public static void getBedInfos(String deptCode, final ResponseCallBack<List<BedTypeBed>> callBack) {
        getRetrofitApi().getBedInfos(deptCode).enqueue(callBack);
    }


    /**
     * ????????????????????????????????????
     *
     * @param callBack
     */
    public static void getOtherCallServerPort(final ResponseCallBack<Integer> callBack) {
        getRetrofitApi().getOtherCallServerPort().enqueue(callBack);
    }

    /**
     * ?????????????????????????????????
     *
     * @param deptCode
     * @param callBack
     */
    public static void getWrdNamesByDept(String deptCode, final ResponseCallBack<List<String>> callBack) {
        getRetrofitApi().getWrdNamesByDept(deptCode).enqueue(callBack);
    }


    /**
     * ????????????????????????
     *
     * @param callBack
     */
    public static void getSkins(final ResponseCallBack<List<SkinBean>> callBack) {
        getRetrofitApi().getSkins().enqueue(callBack);
    }

    /**
     * ????????????????????????
     */
    public static void getLeavePatients(String deptCode, final ResponseCallBack<List<PatientInfo>> callBack) {
        getRetrofitApi().getLeavePatients(ConfigSingleton.getInstance().getCurrentWardCode(), deptCode).enqueue(callBack);
    }

    /**
     * ??????????????????????????????
     */
    public static void getInPatients(String deptCode, final ResponseCallBack<List<PatientInfo>> callBack) {
        getRetrofitApi().getInPatients(ConfigSingleton.getInstance().getCurrentWardCode(), deptCode).enqueue(callBack);
    }

    /**
     * ??????????????????????????????
     */
    public static void getOutPatients(String deptCode, final ResponseCallBack<List<PatientInfo>> callBack) {
        getRetrofitApi().getOutPatients(ConfigSingleton.getInstance().getCurrentWardCode(), deptCode).enqueue(callBack);
    }
	
    //?????????????????????????????????
    public static XinShangAnInfusionNetService getXSAInfusionService() {
        if (xsaInfusionService == null) {
            OkHttpClient client = new OkHttpClient.Builder().
                    addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).
                    connectTimeout(20, TimeUnit.SECONDS).
                    readTimeout(20, TimeUnit.SECONDS).
                    writeTimeout(20, TimeUnit.SECONDS).build();
            Retrofit.Builder qcBuilder;
            if (!TextUtils.isEmpty(ConfigSingleton.getInstance().getInfusionUrl())) {
                ConfigSingleton.getInstance().setInfusionUrl(ConfigSingleton.getInstance().getInfusionUrl().startsWith("http") ?
                        ConfigSingleton.getInstance().getInfusionUrl() : "http://" + ConfigSingleton.getInstance().getInfusionUrl());
                ConfigSingleton.getInstance().setInfusionUrl(ConfigSingleton.getInstance().getInfusionUrl().endsWith("/") ?
                        ConfigSingleton.getInstance().getInfusionUrl() : ConfigSingleton.getInstance().getInfusionUrl() + "/");
            }
            try {
                qcBuilder = new Retrofit.Builder()
                        .baseUrl(ConfigSingleton.getInstance().getInfusionUrl());
            } catch (IllegalArgumentException | NullPointerException e) {
                //????????????????????????????????????????????????
                ConfigSingleton.getInstance().setInfusionUrl("");
                return null;
            }
            xsaInfusionService = qcBuilder
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(XinShangAnInfusionNetService.class);
        }
        return xsaInfusionService;
    }

    public static void refreshXSAInfusionService() {
        xsaInfusionService = null;
        getXSAInfusionService();
    }
    /**
     * ??????????????????
     */
    public static void getXSAInfusionList(Map<String, Object> map, XSAInfusionResponseCallBack<List<XSAInfusionDto>> callBack) {
        if (getXSAInfusionService() != null) {
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(map));
            getXSAInfusionService().getXSAInfusionList(body).enqueue(callBack);
        }
    }

}
