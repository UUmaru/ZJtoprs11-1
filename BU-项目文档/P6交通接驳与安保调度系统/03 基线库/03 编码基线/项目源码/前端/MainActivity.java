package com.kyee.iwis.nana;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.kyee.downloader.DownloadListener;
import com.kyee.downloader.DownloadUtil;
import com.kyee.iwis.nana.Speech.YzsAsrUtils;
import com.kyee.iwis.nana.Speech.YzsTtsUtils;
import com.kyee.iwis.nana.ThirdPartyInfusion.InfusionFloatAction;
import com.kyee.iwis.nana.arcface.ArcFaceSingleton;
import com.kyee.iwis.nana.arcface.utils.DetectFaceUtil;
import com.kyee.iwis.nana.base.ExitApplication;
import com.kyee.iwis.nana.base.net.NetApi;
import com.kyee.iwis.nana.base.net.ResponseCallBack;
import com.kyee.iwis.nana.base.status.StatusManager;
import com.kyee.iwis.nana.bedmanage.rangebed.BedRangeInfoDao;
import com.kyee.iwis.nana.bedmanage.rangebed.BedRangeInfoMap;
import com.kyee.iwis.nana.bedmanage.reservedpat.ReservedPatDialog;
import com.kyee.iwis.nana.bedmanage.reservedpat.ReservedPatFragment;
import com.kyee.iwis.nana.business.BusinessData;
import com.kyee.iwis.nana.callremind.bean.CallInfoBean;
import com.kyee.iwis.nana.callremind.bean.CallMessage;
import com.kyee.iwis.nana.callremind.bean.CallingBean;
import com.kyee.iwis.nana.callremind.bean.CallingBeanFactory;
import com.kyee.iwis.nana.callremind.bean.OverTimeCallBean;
import com.kyee.iwis.nana.callremind.bean.SpeakBean;
import com.kyee.iwis.nana.callremind.bean.XmlCallingBean;
import com.kyee.iwis.nana.callremind.floatcalldialog.TimeOutDialog;
import com.kyee.iwis.nana.callremind.floatcalldialog.WarnMessageDialog;
import com.kyee.iwis.nana.callremind.socket.SocketConnect;
import com.kyee.iwis.nana.callremind.socket.SocketMessageUtil;
import com.kyee.iwis.nana.camera.CameraSingleton;
import com.kyee.iwis.nana.camera.CameraUtil;
import com.kyee.iwis.nana.common.ConfigSingleton;
import com.kyee.iwis.nana.controller.widget.bean.MenuInfo;
import com.kyee.iwis.nana.controller.widget.dialog.MenuItemDialog;
import com.kyee.iwis.nana.dutynurse.dialog.DutyNurseSettingDialog;
import com.kyee.iwis.nana.dynamic.view.DynamicFragment;
import com.kyee.iwis.nana.faceland.FaceLandDialog;
import com.kyee.iwis.nana.generalsigns.GeneralSignsFragment;
import com.kyee.iwis.nana.infusion.InfusionSettingFragment;
import com.kyee.iwis.nana.inspectionarrange.InspectionArrangeFragment;
import com.kyee.iwis.nana.loading.LoadingUtil;
import com.kyee.iwis.nana.login.callback.ILoginCallBack;
import com.kyee.iwis.nana.main.contract.MainContract;
import com.kyee.iwis.nana.main.dialog.InputContentDialog;
import com.kyee.iwis.nana.main.presenter.MainPresenter;
import com.kyee.iwis.nana.main.view.BaseMenuView;
import com.kyee.iwis.nana.main.view.ExchangeReportMenuView;
import com.kyee.iwis.nana.main.view.InfusionMenuView;
import com.kyee.iwis.nana.main.view.NurseAssistantMenuView;
import com.kyee.iwis.nana.main.view.ShowDataMenuView;
import com.kyee.iwis.nana.main.view.WardInfoMenuView;
import com.kyee.iwis.nana.main.view.WorkArrangeMenuView;
import com.kyee.iwis.nana.model.ConfigInfo;
import com.kyee.iwis.nana.model.DeptBean;
import com.kyee.iwis.nana.model.MyPatientsModel;
import com.kyee.iwis.nana.model.NurseModel;
import com.kyee.iwis.nana.model.PatientInfo;
import com.kyee.iwis.nana.model.XMPPLoginMessageModel;
import com.kyee.iwis.nana.patient.card.dialog.PatientInfoDialog;
import com.kyee.iwis.nana.patient.card.view.PatientCardFragment;
import com.kyee.iwis.nana.services.ACacheService;
import com.kyee.iwis.nana.services.AutoCloseDialogManager;
import com.kyee.iwis.nana.services.PublicHandler;
import com.kyee.iwis.nana.services.SoundManager;
import com.kyee.iwis.nana.skin.SkinBean;
import com.kyee.iwis.nana.splash.ProxyHelper;
import com.kyee.iwis.nana.surgicalArrangement.view.SurgicalFragment;
import com.kyee.iwis.nana.task.view.TaskFragment;
import com.kyee.iwis.nana.utils.ACache;
import com.kyee.iwis.nana.utils.BedNumUtil;
import com.kyee.iwis.nana.utils.CollectionUtils;
import com.kyee.iwis.nana.utils.DateUtil;
import com.kyee.iwis.nana.utils.FileUtils;
import com.kyee.iwis.nana.utils.KyeeUtils;
import com.kyee.iwis.nana.utils.L;
import com.kyee.iwis.nana.utils.MacUtil;
import com.kyee.iwis.nana.utils.NavigationBarUtil;
import com.kyee.iwis.nana.utils.PermissionUtil;
import com.kyee.iwis.nana.utils.PropertiesUtil;
import com.kyee.iwis.nana.utils.ScreenadapterUtils;
import com.kyee.iwis.nana.utils.SharedPreferencesUtil;
import com.kyee.iwis.nana.utils.ThreadUtils;
import com.kyee.iwis.nana.utils.X5WebView;
import com.kyee.iwis.nana.utils.update.UpdateManager;
import com.kyee.iwis.nana.widget.CameraSurfaceView;
import com.kyee.iwis.nana.widget.SecondConfirmView;
import com.kyee.iwis.nana.widget.Toaster;
import com.kyee.iwis.nana.widget.spinner.SpinnerView;
import com.kyee.iwis.yzsabslibrary.IYzsAsrCallback;
import com.kyee.logxmanager.LogXManager;
import com.kyee.logxmanager.SettingActivity;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.feng.skin.manager.base.BaseFragmentActivity;
import cn.feng.skin.manager.listener.ILoaderListener;
import cn.feng.skin.manager.loader.SkinManager;
import java8.util.Optional;
import java8.util.stream.StreamSupport;

public class MainActivity extends BaseFragmentActivity implements MainContract.View, ILoginCallBack {
    public static final String NATIVE_NETWORK_ERROR = "NATIVE_NETWORK_ERROR";
    public static final String NETWORK_SUCCESS = "NETWORK_SUCCESS";
    public static final String SERVER_NETWORK_ERROR = "SERVER_NETWORK_ERROR";
    public static final String CALL_SERVER_URL = "CallServerUrl";
    private static final String TAG = "MainActivity";
    /**
     * ????????????
     */

    //?????????????????????
    public static final MainHandler handler = new MainHandler();
    private static final String BIRTHDAY_TIP = "????????????";
    /**
     * ??????-???????????????-???????????????
     */
    private static final String ASR_SWITCH = "????????????";
    //?????????
    private Timer timer;
    //?????????????????????
    private Timer blueTimer;

    /**
     * ????????????
     */
    @BindView(R.id.date)
    TextView dateView;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.informationImg)
    Button informationButton;
    @BindView(R.id.refreshImg)
    Button refreshButton;
    @BindView(R.id.time)
    TextView tvTime;
    @BindView(R.id.button_magic)
    Button buttonMagic;
    @BindView(R.id.anim_refreshImg)
    Button animRefreshImg;
    @BindView(R.id.updateTime)
    TextView tvUpdateTime;
    @BindView(R.id.dept_name)
    TextView tvDeptName;
    @BindView(R.id.inhospital_name)
    TextView tvHospitalName;
    @BindView(R.id.loadingLayout)
    RelativeLayout loadingLayout;
    @BindView(R.id.img_lock)
    ImageView imgLock;
    //    @BindView(R.id.ll_lock)
//    RelativeLayout llLock;
    @BindView(R.id.tv_remindNum)
    TextView tvRemindNum;
    @BindView(R.id.ll_remind)
    RelativeLayout llRemind;
    /**
     * ????????????code
     */
    private static final int PERMISSION_REQUEST = 10086;
    private static boolean isLock = true;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.ll_surfaceView)
    LinearLayout llSurfaceView;

    CameraSurfaceView cameraSurfaceView = null;
    CameraGLSurfaceView cameraGLSurfaceView = null;
    @BindView(R.id.ll_face)
    LinearLayout llFace;
    @BindView(R.id.ll_remind_call)
    RelativeLayout llRemindCall;
    @BindView(R.id.tv_remind_call_num)
    TextView tvRemindCallNum;
    /**
     * ????????????????????????
     */
    @BindView(R.id.ll_face_land)
    RelativeLayout llFaceLand;
    /**
     * ???????????????
     */
    @BindView(R.id.select_office)
    SpinnerView spinnerView;
    /**
     * ?????????
     */
    @BindView(R.id.ll_reserved_pat)
    RelativeLayout rlReservedPat;
    @BindView(R.id.tv_reserved_pat_num)
    TextView tvReservedPatNum;

    @BindView(R.id.announcement)
    TextView announcementView;
    @BindView(R.id.announcement_info)
    LinearLayout announcementInfo;
    /**
     * ???????????????
     */
    @BindView(R.id.framelayout)
    LoginListenerView subContentView;
    @BindView(R.id.network_status)
    Button networkStatusView;


    /**
     * ????????????
     */
    //?????????????????????
    private boolean isFirst = true;


    /**
     * ????????????handler
     */
    private Handler refreshHandler;

    //????????????????????????????????????
    public static boolean checkFace = true;
    private MainPresenter mPresenter;
    /**
     * ??????socket???????????????
     */
    private List<CallInfoBean> callInfoBeanList = new ArrayList<>();
    /**
     * ???????????????????????????
     */
    private List<CallInfoBean> qcCallInfoBeanList = new ArrayList<>();
    /**
     * ???????????????socket???????????????????????????????????????
     */
    private CopyOnWriteArrayList<CallInfoBean> mergedCallInfoBeanList;
    private WarnMessageDialog warnMessageDialog;
    /**
     * ????????????????????????
     */
    private List<PatientInfo> birthPatientList;
    /**
     * ???????????????????????????
     */
    public static Map<String, SpeakBean> speakMap = new LinkedHashMap<>(16, 0.75f, true);
    @BindView(R.id.helpImg)
    Button helpImg;

    public static SocketConnect socketConnect;

    /**
     * ????????????????????????
     */
    public static boolean isStartAnimEnd = false;

    /**
     * ????????????????????????
     */
    public static boolean isConnect = false;

    /**
     * ??????????????????
     */
    public static boolean isConnectError = false;

    public boolean isFirstMessage = false;

    /**
     * ????????????????????????
     */
    public static boolean isOnce = false;

    /**
     * ?????????????????????
     */
    public static boolean isChangeWard = false;
    /**
     * ????????????????????????
     */
    private ReservedPatDialog reservedPatDialog;
    private int pointerId0, pointerId1, pointerId2, pointerId3;
    private float oldXPoint0, oldXPoint1, oldXPoint2, oldXPoint3;
    private float oldYPoint0, oldYPoint1, oldYPoint2, oldYPoint3;
    /**
     * ???????????????????????????
     */
    private int mPointCount = -1;

    /**
     * ??????????????????(??????????????????Panel???????????????????????????????????????????????????????????????????????????????????????)
     */
    public static SecondConfirmView secondConfirmView;
    ;
    /**
     * ?????????????????????????????????
     */
    private BaseMenuView menuView;
    /**
     * ???????????????????????????????????????
     */
    private MenuItemDialog menuItemDialog;
    /**
     * ?????????
     */
    protected Runnable timerRunnable;
    /**
     * ????????????????????????
     */
    private boolean isFaceSuccess = false;
    /**
     * ??????????????????
     */
    private FaceLandDialog faceLandDialog;
    /**
     * ?????????????????????????????????fragment?????????????????????
     */
    private String[] fragmentWhiteList = {PatientCardFragment.class.getSimpleName(),
            DynamicFragment.class.getSimpleName(), TaskFragment.class.getSimpleName(),
            ReservedPatFragment.class.getSimpleName(), SurgicalFragment.class.getSimpleName(),
            GeneralSignsFragment.class.getSimpleName(), InspectionArrangeFragment.class.getSimpleName()};

    /**
     * ??????????????????????????????????????????
     */
    public static boolean aiDialogShow = false;

    // ?????????????????????????????????????????????
    private UpdateManager manager = null;

    private String[] announcementList = new String[]{};
    private int currentAnnouncementIndex = 0;
    private Animation animationIn;

    private Intent backIntent;
    private Intent menuIntent;
    /**
     * ?????????????????????
     */
    private TimeOutDialog timeOutDialog;

    /**
     * ??????????????????????????????
     */
    @BindView(R.id.rlAsrTip)
    RelativeLayout rlAsrTip;

    /**
     * ????????????????????????
     */
    @BindView(R.id.ivAsrMonitor)
    ImageView ivAsrMonitor;

    /**
     * ????????????????????????
     */
    @BindView(R.id.tvAsrResult)
    TextView tvAsrResult;

    /**
     * ????????????
     */
    @BindView(R.id.web_view)
    X5WebView webView;

    /**
     * ????????????
     */
    private PatientInfoDialog patientInfoDialog;

    /**
     * ???????????????
     */
    private Vibrator mVibrator;

    /**
     * ???????????????????????????
     */
    private IYzsAsrCallback yzsAsrCallback = new IYzsAsrCallback() {
        @Override
        public void processAsrResult(String result) {
            Log.d(ParamsConstants.LOG, "MainActivity->YzsAsrUtils.IYzsAsrCallback->processAsrResult:" + result);
            showAsrProgressResult(result, false);
            if (patientInfoDialog != null && patientInfoDialog.isShowing()) {
                patientInfoDialog.dismiss();
            }
            //????????????
            if (DirectConstants.intent4RefreshData(result)) {
                mPresenter.refreshAllData(true, true);
            }
            //??????????????????
            else if (DirectConstants.intent4OpenHelpView(result)) {
                mPresenter.showHelpDialog();
                YzsTtsUtils.getInstance().play("?????????????????????");
            }
            //????????????????????????
            else if (DirectConstants.intent4OpenSystemView(result)) {
                mPresenter.showSettingDialogByDirect();
                YzsTtsUtils.getInstance().play("?????????????????????");
            }
            //????????????
            else if (DirectConstants.intent4OpenAdviceDialog(result)) {
                mPresenter.showAIAdviceDialog();
            }
            //????????????????????????-???????????????????????????
            else if (DirectConstants.intent4OpenInfusionSettingView(result)) {
                if (!(menuView instanceof InfusionMenuView)) {
                    showSubContent(MenuInfo.getInstance().getInfusionItem().getMenuName());
                }
                InfusionMenuView menu = (InfusionMenuView) menuView;
                menu.infusionSetting.performClick();
                YzsTtsUtils.getInstance().playImmediately("???????????????????????????");
            }
            //????????????????????????-?????????????????????
            else if (DirectConstants.intent4OpenInfusionView(result)) {
                if (!(menuView instanceof InfusionMenuView)) {
                    showSubContent(MenuInfo.getInstance().getInfusionItem().getMenuName());
                }
                InfusionMenuView menu = (InfusionMenuView) menuView;
                menu.infusionInfo.performClick();
                YzsTtsUtils.getInstance().playImmediately("?????????????????????");
            }
            //????????????????????????
            else if (DirectConstants.intent4OpenInfusionMenu(result)) {
                if (menuView instanceof InfusionMenuView) {
                    YzsTtsUtils.getInstance().playImmediately("????????????????????????");
                    return;
                }
                showSubContent(MenuInfo.getInstance().getInfusionItem().getMenuName());
                YzsTtsUtils.getInstance().play("???????????????????????????");
            }
            //????????????????????????
            else if (DirectConstants.intent4OpenNurseAssistantMenu(result)) {
                if (menuView instanceof NurseAssistantMenuView) {
                    YzsTtsUtils.getInstance().playImmediately("?????????????????????");
                    return;
                }
                showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                YzsTtsUtils.getInstance().play("??????????????????");
            }
            //????????????????????????->????????????
            else if (DirectConstants.intent4OpenNurseFileView(result)) {
                if (!(menuView instanceof WardInfoMenuView)) {
                    showSubContent(MenuInfo.getInstance().getWardInfo().getMenuName());
                }
                WardInfoMenuView menu = (WardInfoMenuView) menuView;
                menu.llNurseFile.performClick();
                YzsTtsUtils.getInstance().play("?????????????????????");
            }
            //????????????????????????->????????????
            else if (DirectConstants.intent4OpenWardinfoView(result)) {
                if (!(menuView instanceof WardInfoMenuView)) {
                    showSubContent(MenuInfo.getInstance().getWardInfo().getMenuName());
                }
                WardInfoMenuView menu = (WardInfoMenuView) menuView;
                menu.wardInfoBtn.performClick();
                YzsTtsUtils.getInstance().play("?????????????????????");
            }
            //????????????????????????
            else if (DirectConstants.intent4OpenWardinfoMenu(result)) {
                if (menuView instanceof WardInfoMenuView) {
                    YzsTtsUtils.getInstance().playImmediately("????????????????????????");
                    return;
                }
                showSubContent(MenuInfo.getInstance().getWardInfo().getMenuName());
                YzsTtsUtils.getInstance().play("?????????????????????");
            }
            //????????????????????????
            else if (DirectConstants.intent4OpenExchangeReportMenu(result)) {
                if (menuView instanceof ExchangeReportMenuView) {
                    YzsTtsUtils.getInstance().playImmediately("????????????????????????");
                    return;
                }
                showSubContent(MenuInfo.getInstance().getExchangeReport().getMenuName());
                YzsTtsUtils.getInstance().play("?????????????????????");
            }
            //????????????????????????-???????????????
            else if (DirectConstants.intent4OpenShowStatisticsView(result)) {
                if (!(menuView instanceof ShowDataMenuView)) {
                    showSubContent(MenuInfo.getInstance().getShowStatistics().getMenuName());
                }
                ShowDataMenuView menu = (ShowDataMenuView) menuView;
                menu.llStatistics.performClick();
                YzsTtsUtils.getInstance().play("?????????????????????");
            }
            //????????????????????????-???????????????
            else if (DirectConstants.intent4OpenShowOrderInfoView(result)) {
                if (!(menuView instanceof ShowDataMenuView)) {
                    showSubContent(MenuInfo.getInstance().getShowStatistics().getMenuName());
                }
                ShowDataMenuView menu = (ShowDataMenuView) menuView;
                menu.llOrderInfo.performClick();
                YzsTtsUtils.getInstance().play("?????????????????????");
            }
            //????????????????????????
            else if (DirectConstants.intent4OpenShowStatisticsMenu(result)) {
                if (menuView instanceof ShowDataMenuView) {
                    YzsTtsUtils.getInstance().playImmediately("????????????????????????");
                    return;
                }
                showSubContent(MenuInfo.getInstance().getShowStatistics().getMenuName());
                YzsTtsUtils.getInstance().play("?????????????????????");
            }
            //????????????????????????->????????????
            else if (DirectConstants.intent4OpenNurseScheduleView(result)) {
                if (!(menuView instanceof WorkArrangeMenuView)) {
                    showSubContent(MenuInfo.getInstance().getWorkArrange().getMenuName());
                }
                WorkArrangeMenuView menu = (WorkArrangeMenuView) menuView;
                menu.workPlanBtn.performClick();
                YzsTtsUtils.getInstance().play("?????????????????????");
            }
            //????????????????????????->????????????????????????
            else if (DirectConstants.intent4OpenNurseAssignView(result)) {
                if (!(menuView instanceof WorkArrangeMenuView)) {
                    showSubContent(MenuInfo.getInstance().getWorkArrange().getMenuName());
                }
                WorkArrangeMenuView menu = (WorkArrangeMenuView) menuView;
                menu.bedArrangeBtn.performClick();
                YzsTtsUtils.getInstance().play("?????????????????????????????????");
            }
            //????????????????????????
            else if (DirectConstants.intent4OpenWorkArrangeMenu(result)) {
                if (menuView instanceof WorkArrangeMenuView) {
                    YzsTtsUtils.getInstance().playImmediately("????????????????????????");
                    return;
                }
                showSubContent(MenuInfo.getInstance().getWorkArrange().getMenuName());
                YzsTtsUtils.getInstance().play("?????????????????????");
            }
            //??????????????????
            else if (DirectConstants.intent4OpenPatientView(result)) {
                //???????????????????????????????????????????????????????????????
                if (!nurseAssistantMenuView.isPatientCardFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("?????????????????????????????????");
                    return;
                }

                //????????????????????????????????????????????????,??????????????????????????????????????????
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }

                //???????????????????????????????????????????????????
                if (nurseAssistantMenuView.isShowPatientCardFragment()) {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.llCard);
                    YzsTtsUtils.getInstance().play("?????????????????????");
                }
            }
            //??????????????????
            else if (DirectConstants.intent4OpenTaskView(result)) {
                //???????????????????????????????????????????????????????????????
                if (!nurseAssistantMenuView.isTaskFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("?????????????????????????????????");
                    return;
                }

                //????????????????????????????????????????????????,??????????????????????????????????????????
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }

                //???????????????????????????????????????????????????
                if (nurseAssistantMenuView.isShowTaskView()) {
                    YzsTtsUtils.getInstance().playImmediately("????????????????????????");
                }
                //??????????????????????????????????????????????????????
                else {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.mLLTask);
                    YzsTtsUtils.getInstance().playImmediately("?????????????????????");
                }
            }
            //??????????????????
            else if (DirectConstants.intent4OpenDynamicView(result)) {
                //???????????????????????????????????????????????????????????????
                if (!nurseAssistantMenuView.isDynamicFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("?????????????????????????????????");
                    return;
                }

                //????????????????????????????????????????????????,??????????????????????????????????????????
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }
                //???????????????????????????????????????????????????
                if (nurseAssistantMenuView.isShowDynamicView()) {
                    YzsTtsUtils.getInstance().playImmediately("????????????????????????");
                }
                //??????????????????????????????????????????????????????
                else {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.llState);
                    YzsTtsUtils.getInstance().playImmediately("?????????????????????");
                }
            }
            //????????????
            else if (DirectConstants.intent4OpenSurgical(result)) {
                //???????????????????????????????????????????????????????????????
                if (!nurseAssistantMenuView.isSurgicalFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("???????????????????????????");
                    return;
                }

                //????????????????????????????????????????????????,??????????????????????????????????????????
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }
                //???????????????????????????????????????????????????
                if (nurseAssistantMenuView.isShowSurgicalView()) {
                    YzsTtsUtils.getInstance().playImmediately("????????????????????????");
                }
                //??????????????????????????????????????????????????????
                else {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.mLlSurgical);
                    YzsTtsUtils.getInstance().playImmediately("?????????????????????");
                }
            }
            //????????????
            else if (DirectConstants.intent4OpenGeneralSigns(result)) {
                //???????????????????????????????????????????????????????????????
                if (!nurseAssistantMenuView.isGeneralSignsFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("?????????????????????????????????");
                    return;
                }

                //????????????????????????????????????????????????,??????????????????????????????????????????
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }
                //???????????????????????????????????????????????????
                if (nurseAssistantMenuView.isShowGeneralSignsView()) {
                    YzsTtsUtils.getInstance().playImmediately("????????????????????????");
                }
                //??????????????????????????????????????????????????????
                else {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.llGeneralSigns);
                    YzsTtsUtils.getInstance().play("?????????????????????");
                }
            }
            //????????????
            else if (DirectConstants.intent4OpenInspectionArrangement(result)) {
                //???????????????????????????????????????????????????????????????
                if (!nurseAssistantMenuView.isInspectionArrangementFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("?????????????????????????????????");
                    return;
                }

                //????????????????????????????????????????????????,??????????????????????????????????????????
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }
                //???????????????????????????????????????????????????
                if (nurseAssistantMenuView.isShowInspectionArrangementView()) {
                    YzsTtsUtils.getInstance().playImmediately("????????????????????????");
                }
                //??????????????????????????????????????????????????????
                else {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.llInspectionArrange);
                    YzsTtsUtils.getInstance().playImmediately("?????????????????????");
                }
            }
            //????????????????????????????????????
            else if (DirectConstants.intent4ChangePatientViewTab(result)) {
                //???????????????????????????????????????????????????????????????
                if (!nurseAssistantMenuView.isPatientCardFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("?????????????????????????????????");
                    return;
                }
                //??????????????????????????????,????????????????????????????????????????????????intent4ChangePatientViewTab????????????
                String originTabName = DirectConstants.getChangePatientViewTabOriginName(result);
                //??????????????????????????????,?????????????????????????????????
                if (!nurseAssistantMenuView.isPatientCardATabAndTabVisiable(originTabName)) {
                    YzsTtsUtils.getInstance().playImmediately("?????????????????????????????????????????????");
                    return;
                }

                //????????????????????????????????????????????????,??????????????????????????????????????????
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }

                //???????????????????????????????????????????????????????????????????????????????????????
                if (!nurseAssistantMenuView.isShowPatientCardFragment()) {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.llCard);
                }

                //????????????????????????
                nurseAssistantMenuView.changePatientCardTab(originTabName);
                YzsTtsUtils.getInstance().playImmediately("?????????" + originTabName + "????????????");
            }
            //???????????????????????????,????????????15??????????????????"????????????"
            else if (DirectConstants.intent4OpenPatientDialog(result)) {

                List<PatientInfo> patientInfoList = ACacheService.getPatients();
                if (CollectionUtils.isEmpty(patientInfoList)) {
                    YzsTtsUtils.getInstance().playImmediately("?????????????????????");
                    return;
                }

                //????????????????????????????????????
                PatientInfo patientInfo = null;
                for (int k = 0; k < patientInfoList.size(); k++) {
                    PatientInfo patientInfo1 = patientInfoList.get(k);
                    if (patientInfo1 != null) {
                        String patName = patientInfo1.getPatientName() + "??????";
                        String bedNo = "?????????";
                        if (KyeeUtils.isNumeric(patientInfo1.getBedLabel())) {
                            bedNo = BedNumUtil.numberToChinese(Integer.parseInt(patientInfo1.getBedLabel())) + bedNo;
                        }
                        String patNamePinYin = BedNumUtil.HanZiToPinYin(patName);
                        String bedNoPinYin = BedNumUtil.HanZiToPinYin(bedNo);
                        String resultPinYin = BedNumUtil.HanZiToPinYin(result);
                        if (patNamePinYin.contains(resultPinYin) || bedNoPinYin.contains(resultPinYin)) {
                            patientInfo = patientInfo1;
                            break;
                        }
                    }
                }

                if (patientInfo == null) {
                    if (result.endsWith("?????????")) {
                        YzsTtsUtils.getInstance().playImmediately("??????????????????");
                    } else {
                        YzsTtsUtils.getInstance().playImmediately("?????????????????????");
                    }
                    return;
                }

                //??????
                if (TextUtils.isEmpty(patientInfo.getPatientName())) {
                    YzsTtsUtils.getInstance().playImmediately("??????????????????");
                    return;
                }
                if (patientInfoDialog == null) {
                    patientInfoDialog = new PatientInfoDialog(ConfigSingleton.getInstance().getCurrentContext(), patientInfo, patientInfo.getLevelColor(), patientInfo.getLevelTextColor(), patientInfo.getCustomCardColor());
                    patientInfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            patientInfoDialog = null;
                        }
                    });
                    patientInfoDialog.show();
                } else {
                    if (!patientInfoDialog.isShowing()) {
                        patientInfoDialog.show();
                    }
                    patientInfoDialog.refreshView(patientInfo, patientInfo.getLevelColor(), patientInfo.getLevelTextColor(), patientInfo.getCustomCardColor());
                }
            } else {
                YzsTtsUtils.getInstance().playImmediately("????????????????????????????????????????????????~");
            }
        }

        @Override
        public void onWeakUpSuccess() {
            //??????????????????????????????
            if (isLock) {
                onPinLoginSuccess();
            }

            //??????????????????????????????
            if (mVibrator.hasVibrator()) {
                //??????????????????0.3???
                mVibrator.vibrate(300);
            }

            showAsrProgressResult("", false);
        }

        @Override
        public void onAsrProgressResult(String progressResult) {
            showAsrProgressResult(progressResult, true);
        }

        @Override
        public void onAsrSleep() {
            //????????????????????????????????????
            MainActivity.handler.sendEmptyMessageDelayed(ParamsConstants.MSG_CLOSE_ASR_VIEW, 3000);
//            closeAsrTipView();
        }
    };


    /**
     * ?????????????????????????????????
     */
    private void showAsrProgressResult(String result, boolean isAppend) {
        if (rlAsrTip.getVisibility() == View.INVISIBLE) {
            rlAsrTip.setVisibility(View.VISIBLE);
        }
        if (isAppend && TextUtils.isEmpty(tvAsrResult.getText().toString())) {
            result = tvAsrResult.getText() + result;
        }
        tvAsrResult.setText(result);

    }

    /**
     * ?????????????????????????????????
     */
    private void closeAsrTipView() {
        if (rlAsrTip.getVisibility() != View.INVISIBLE) {
            rlAsrTip.setVisibility(View.INVISIBLE);
        }
    }


    private String thirdPartyInfusionUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //?????????????????????
        String Path = FileUtils.path + "/" + FileUtils.filename + ".xml";
        String value_dpi= FileUtils.readTxt(Path);
        String value="0";
        //????????????
        if(value_dpi!=null && value_dpi!="" && !"0".equals(value_dpi)){
            ScreenadapterUtils.setCustomDensity(this, getApplication(), Integer.parseInt(value_dpi));
        }
        initStartView();

        //??????????????????????????????????????????
        //SpeechCenter.getInstance().init(MainActivity.this);
//        SpeechUtilOffline.getInstance();

        getWindow().setBackgroundDrawable(null);
        /**???????????????handler*/
        refreshHandler = new Handler();
        /**?????????????????????????????????????????????*/
        refreshHandler.post(refreshTask);
        /**?????????????????????*/
        SoundManager.getInstance();
        SoundManager.initSounds(getBaseContext());
        /**??????????????????*/
        SoundManager.loadSounds();
        LoadingUtil.getInstance().init(MainActivity.this);
        ExitApplication.getInstance().addActivity(this);
        animationIn = AnimationUtils.loadAnimation(this, R.anim.animation_slip_in);
        menuIntent = new Intent(this, MenuFloatService.class);

        //??????????????????
        mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }

    /**
     * emq??????????????????????????????????????????????????????
     */
    private void initSocketConnet() {
        String url = ConfigInfo.get(ConfigInfo.CALL_SERVER_URL);
        L.writeToFile('s', "call url:", url);
        if (TextUtils.isEmpty(url)) {
            disconnect();
            Log.e(TAG, "Socket ????????????");
            return;
        }
        callInfoBeanList = new ArrayList<>();
        tvRemindCallNum.setText("");
        String ip = url, port = "";
        if (url.contains(":")) {
            ip = url.split(":")[0];
            port = url.split(":")[1];
        }
//        socketConnect = new SocketConnect(ip, isCanParseInt(port) ? Integer.parseInt(port) : 9088, new SocketConnect.Callback() {
        socketConnect = new SocketConnect(ip, isCanParseInt(port) ? Integer.parseInt(port) : 8088, new SocketConnect.Callback() {
            @Override
            public void onConnected() {
                L.writeToFile('s', "connect", "socket????????????");
                Toaster.makeToast(getApplicationContext(), Toaster.TOAST_SUCCESS, "????????????", Toast.LENGTH_LONG);
                if (socketConnect != null) {
                    socketConnect.send(SocketMessageUtil.getInstance().transformString(ConfigSingleton.getInstance().getCurrentWardCode()));
                }
            }

            @Override
            public void onDisconnected() {
                L.writeToFile('s', "connect", "socket????????????");
                Toaster.makeToast(getApplicationContext(), Toaster.TOAST_FAILED, "???????????????", Toast.LENGTH_LONG);
                socketConnect = null;
            }

            @Override
            public void onReconnected() {
            }

            @Override
            public void onSend() {
            }

            @Override
            public void onReceived(byte[] msg) {
//                try {
//                    String xml = SocketMessageUtil.getInstance().transformBytes(msg);
//                    L.writeToFile('s',"receive string",xml);
//                    XmlCallingBean xmlCallingBean = SocketMessageUtil.getInstance().xml2Obj(xml, XmlCallingBean.class);
//                    CallingBean callingBean = CallingBeanFactory.getInstance().fromXmlCallingBean(xmlCallingBean);
//                    mPresenter.updateCallInfoBean(callingBean);
//                    Log.i("MainActivity", callingBean.toString());
//                } catch (Exception e) {
//                    Log.e(TAG, "Xml2Obj error", e);
//                }
                try {
                    XmlCallingBean xmlCallingBean = new XmlCallingBean();
                    xmlCallingBean.setFunctionCode(msg[0]);
                    byte[] byteData = new byte[msg.length - 1];
                    System.arraycopy(msg, 1, byteData, 0, msg.length - 1);
                    String fullMsg = (new String(byteData, Charset.forName(SocketMessageUtil.getInstance().getCharset()))).trim();
                    if (fullMsg.endsWith(SocketMessageUtil.getInstance().getYAHUA_SUFFIX())) {
                        xmlCallingBean.setMagInfo(fullMsg.substring(0, fullMsg.length() - SocketMessageUtil.getInstance().getYAHUA_SUFFIX().length()));
                        CallingBean callingBean = CallingBeanFactory.getInstance().fromXmlCallingBean(xmlCallingBean);
                        mPresenter.updateCallInfoBean(callingBean);
                        Log.i("MainActivity", callingBean.toString());
                    } else {
                        String bytes = "";
                        for (int i = 0; i < byteData.length; i++) {
                            bytes += byteData[i] + " ";
                        }
                        Log.e(TAG, "?????????????????????bytes???" + bytes);
                        L.writeToFile('s', "????????????", "?????????????????????bytes???" + bytes);
                        Log.e(TAG, "?????????????????????string???" + fullMsg);
                        L.writeToFile('s', "????????????", "?????????????????????string???" + fullMsg);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Xml2Obj error", e);
                }
            }


            @Override
            public void onError(String msg) {
                L.writeToFile('s', "connect", "socket????????????,msg:" + msg);
                Toaster.makeToast(getApplicationContext(), Toaster.TOAST_FAILED, msg, Toast.LENGTH_LONG);
                if ("????????????????????????????????????????????????".equals(msg)) {
                    disconnect();
                }
            }
        });
        socketConnect.setHeartbeatMessage(ConfigSingleton.getInstance().getCurrentWardCode());
        socketConnect.connect();
    }

    private void disconnect() {
        if (null != socketConnect) {
            socketConnect.disconnect();
            socketConnect = null;
        }
    }

    /**
     * ??????????????? 1s???????????????
     */
    Runnable refreshTask = new Runnable() {
        @Override
        public void run() {
            refreshHandler.postDelayed(refreshTask, 1000);
            ttsCall();
        }
    };

    /**
     * ????????????,???????????????????????????????????????
     */
    private synchronized void ttsCall() {
        if (speakMap.size() != 0 && YzsTtsUtils.getInstance().isReady()/*&& SSpeechCenter.getInstance().isReady()*/) {
            Map.Entry<String, SpeakBean> next = speakMap.entrySet().iterator().next();
            if (ParamsConstants.MESSAGE_SPEAK.equals(next.getKey())) {
                SpeakBean speech = next.getValue();
//                SpeechCenter.getInstance().speak(speech.getContent());
                YzsTtsUtils.getInstance().play(speech.getContent());
                speakMap.remove(next.getKey());
            } else {
                //??????linkedhashmap?????????????????????????????????????????????
                if (next.getValue().getCount() % next.getValue().getReadCount() == next.getValue().getReadCount() - 1) {
                    speakMap.get(next.getKey());
                }
                String content = next.getValue().getContent();
//                SpeechCenter.getInstance().speak(content);
                YzsTtsUtils.getInstance().play(content);
                next.getValue().setCount(next.getValue().getCount() + 1);
            }
        }
    }

    /**
     * ????????????
     */
    private void computeTtsString(String codeResult) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        StringBuilder sb = new StringBuilder();
        //Android 8.0?????????????????????
//        if (currentVersion >= 26) {
//            codeResult = EscapeUtils.unescape(unicodeEncode(codeResult));
//        }
        if (speakMap != null) {
            speakMap.put(ParamsConstants.MESSAGE_SPEAK, new SpeakBean(codeResult, 0));
        }
        ttsCall();
    }


    static boolean hasFloatPermission = false;

    @Override
    protected void onResume() {
        super.onResume();
//        ScreenadapterUtils.setCustomDensity(this, getApplication(), ScreenadapterUtils.DEFAULT_DENSITY);
        if (!hasFloatPermission) {
            int type = PermissionUtil.getFloatWindowType(this);
            if (type != -1) {
                hasFloatPermission = true;
                //???????????????
                startService(menuIntent);
            }
        } else {
            startService(menuIntent);
        }
        ConfigSingleton.getInstance().setCurrentContext(this);
        //???????????????????????????resume???????????????
        if (!isFirst) {
            mPresenter.refreshAllData(true, true);
        }
        getSubDepts();
        if (null != backIntent) {
            stopService(backIntent);
            backIntent = null;
        }
        MenuInfo.getInstance().hideBackBtn();
        YzsAsrUtils.getInstance().onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        YzsAsrUtils.getInstance().onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoginDialogShow() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onAiAdviceUpdate(int num) {
        if (num != 0) {
            boolean isShowNursingModule = ConfigSingleton.getInstance().isShowTask();
            //??????????????????????????????????????????????????????????????????
            if (!isShowNursingModule) {
                llRemind.setVisibility(View.INVISIBLE);
                return;
            }
            llRemind.setVisibility(View.VISIBLE);
            if (num <= 99) {
                tvRemindNum.setText(num + "");
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvRemindNum.getLayoutParams();
//                lp.width = 28;
                tvRemindNum.setLayoutParams(lp);
                tvRemindNum.setBackgroundResource(R.drawable.shape_corner_new_red);
            } else {
                tvRemindNum.setText("99+");
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvRemindNum.getLayoutParams();
//                lp.width = 42;
                tvRemindNum.setLayoutParams(lp);
                tvRemindNum.setBackgroundResource(R.drawable.iv_num_more);
            }
        } else {
            llRemind.setVisibility(View.INVISIBLE);
            tvRemindNum.setText("");
        }
    }

    @Override
    public void onRefresh() {
        LoadingUtil.getInstance().show("???????????????...");
        refreshing();
    }

    @Override
    public void onConfigReceived() {
        //ble_nfc???????????????
//        if (llLock.getVisibility() == View.VISIBLE) {
//            waitForBlueTooth();
//        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                L.writeToFile('s', "CallSource", ConfigSingleton.getInstance().getCallSource() + "");
                //????????????????????????????????????????????????????????????????????????????????????
                if (ConfigSingleton.CALL_FROM_RABBITMQ != ConfigSingleton.getInstance().getCallSource() && mPresenter.isRabbitMqInit()) {
                    mPresenter.closeRabbitmq();
                    mPresenter.resetCallList();
                }
                if (ConfigSingleton.CALL_FROM_SOCKET != ConfigSingleton.getInstance().getCallSource() && null != socketConnect) {
                    disconnect();
                    mPresenter.resetCallList();
                }
                if (ConfigSingleton.CALL_FROM_EMQ != ConfigSingleton.getInstance().getCallSource()) {
                    mPresenter.reSetEmq();
                }
                if (ConfigSingleton.CALL_FROM_OTHER_WEB_SOCKET != ConfigSingleton.getInstance().getCallSource()) {
                    mPresenter.disconnectThirdPartyWebSocket();
                }
                //?????????????????????
                if (ConfigSingleton.CALL_FROM_SOCKET == ConfigSingleton.getInstance().getCallSource() && socketConnect == null) {
                    initSocketConnet();
                } else if (ConfigSingleton.CALL_FROM_RABBITMQ == ConfigSingleton.getInstance().getCallSource()) {
                    mPresenter.initRabbitmq();
                } else if (ConfigSingleton.CALL_FROM_EMQ == ConfigSingleton.getInstance().getCallSource()) {
                    mPresenter.initEmq();
                } else if(!TextUtils.isEmpty(ConfigSingleton.getInstance().getInfusionUrl())
                        && "??????????????????????????????".equals(ConfigInfo.get(ConfigInfo.INFUSION_SOURCE))){
                    mPresenter.initAndConnectThirdPartyWebSocket();
                }
            }
        }, 5000);

        if (ConfigSingleton.getInstance().getPatientCardDivideDept()
                && ACacheService.getDeptBeans().size() > 1) {
            if (spinnerView.getVisibility() == View.GONE) {
                showOfficeName(ACacheService.getDeptBeans());
            }
            spinnerView.setVisibility(View.VISIBLE);
        } else {
            spinnerView.setVisibility(View.GONE);
        }

        if (ConfigInfo.get(ConfigInfo.SPEC_FUNCTION).contains("??????????????????")) {
            announcementInfo.setVisibility(View.VISIBLE);
            initAnnouncement();
        } else {
            handler.removeMessages(ParamsConstants.ANNOUNCEMENT_SHOW);
            currentAnnouncementIndex = 0;
            announcementInfo.setVisibility(View.GONE);
            ACache.get(ConfigSingleton.getInstance().getCurrentContext()).put("CURRENT_ANNOUNCEMENT", "");
            announcementView.setText("???????????????");
        }
        //??????????????????????????????????????????????????????
        if (ConfigInfo.get(ConfigInfo.SPEC_FUNCTION).contains(ASR_SWITCH)) {
            //????????????
            YzsAsrUtils.getInstance().init(this, yzsAsrCallback);
        } else {
            //??????????????????
            YzsAsrUtils.getInstance().release();
        }

        //?????????????????????
        if (ConfigSingleton.getInstance().getPreAdmissionDepartment()) {
            rlReservedPat.setVisibility(View.GONE);
        } else {
            rlReservedPat.setVisibility(View.GONE);
        }

        if (ConfigSingleton.getInstance().getFaceLand()) {
            llFaceLand.setVisibility(View.VISIBLE);
        } else {
            llFaceLand.setVisibility(View.GONE);
        }

        if (!ConfigSingleton.getInstance().isShowTask()) {
            llRemind.setVisibility(View.INVISIBLE);
        }
        //?????????????????????webSocket??????
        mPresenter.connectServerWebSocket();
    }


    @Override
    public void onConfigUnReceived() {
        LoadingUtil.getInstance().hide();
        afterRefresh();
        Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_FAILED, "????????????????????????", Toast.LENGTH_SHORT);
    }

    @Override
    public void onQrcodeLoginSuccess(XMPPLoginMessageModel model) {
        if (MacUtil.getMac().equals(model.getMac())) {
            StatusManager.getInstance().setStatus(StatusConstants.STATUS_HIDE_LOGIN_DIALOG, 1);
            ConfigSingleton.getInstance().setCurrentNurseModel(model.getUser());
            afterLoginByUser();
        }
    }

    @Override
    public void onFaceRecognitionLoginSuccess() {
        isFaceSuccess = true;
        mPresenter.dismissLoginDialog();
        onPasswordLoginSuccess();
    }

    @Override
    public void onPinLoginSuccess() {
        Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_UNLOCK, "???????????????", Toast.LENGTH_SHORT);
//        llLock.setVisibility(View.GONE);
        subContentView.setIntercept(false);
        imgLock.setImageDrawable(SkinManager.getInstance().getDrawable(R.drawable.new_unlock));
        dynamicAddSkinEnableView(imgLock,"src",R.drawable.new_unlock);
        isLock = false;
        StatusManager.getInstance().setStatus(StatusConstants.STATUS_IS_UNLOCK, true);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        tvName.setText("");
        tvName.setClickable(false);
        llSurfaceView.setVisibility(View.INVISIBLE);
        mPresenter.stopFaceRecognition();
    }

    @Override
    public void onPasswordLoginSuccess() {
        afterLoginByUser();
    }

    /*
     * ??????????????????
     */
    private void initStartView() {
        //?????????handler???
        initValues();
        subContentView.setLoginListener(new LoginListenerView.LoginListener() {
            @Override
            public void showLoginView() {
                unLock(null);
            }
        });
        //???????????????????????????
        initRefreshTurnAround();
        //??????????????????
        mPresenter.checkNet();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                mPresenter.stopFaceRecognition();
                //10?????????????????????????????????????????????????????????????????????
                if (!isFaceSuccess) {
                    tvName.setText("");
                    Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "???????????????????????????????????????", Toast.LENGTH_SHORT);
                    mPresenter.showPassLoginDialog();
                }
                isFaceSuccess = false;
            }
        };
        if (timeOutDialog == null) {
            timeOutDialog = new TimeOutDialog(MainActivity.this);
        }
    }

    @Override
    public void afterServerSuccess() {
        isConnect = true;
        //????????????????????????????????????
        if (isStartAnimEnd) {
            Intent intent = new Intent(ParamsConstants.FINSH_SPLASH_ANIM);
            sendBroadcast(intent);
        }
        tvHospitalName.setText(ConfigSingleton.getInstance().getHospitalName());
        tvDeptName.setText(ConfigSingleton.getInstance().getCurrentWardName());

        //???????????????????????????
        initialTvTime();
        //?????????Timer
        mPresenter.startScheduleTask();
        showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
        checkUpdate();
        mPresenter.refreshAllData(false, true);
        if (isFirst) {
            isFirst = false;
        }
        refreshCallInfoBean();
        ACache.get(ConfigSingleton.getInstance().getCurrentContext()).put("CURRENT_ANNOUNCEMENT", "");
        announcementView.setText("???????????????");
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     * ????????????????????????????????????
     */
    private void refreshCallInfoBean() {
        if (CollectionUtils.isNotEmpty(mergedCallInfoBeanList)) {
            List<PatientInfo> patientInfos = ACacheService.getPatients();
            for (CallInfoBean callInfoBean : mergedCallInfoBeanList) {
                for (PatientInfo patientInfo : patientInfos) {
                    if (!TextUtils.isEmpty(callInfoBean.getPatId())
                            && callInfoBean.getPatId().equals(patientInfo.getPatientId())) {
                        callInfoBean.setBedLabel(patientInfo.getBedLabel());
                        callInfoBean.setPatientInfo(patientInfo);
                        break;
                    }
                }
            }
            if (warnMessageDialog != null && warnMessageDialog.isShowing()) {
                warnMessageDialog.setList(mergedCallInfoBeanList);
            }
        }
    }

    /**
     * ???????????????????????????????????????
     */
    public void initData(View view) {
        mPresenter.refreshAllData(true, false);
        //InfusionSettingFragment??????doAction?????????????????????????????????????????????
        if (ConfigSingleton.getInstance().getCurrentFragment() instanceof InfusionSettingFragment) {
            ConfigSingleton.getInstance().getCurrentFragment().refreshView();
        }
        checkUpdate();
    }

    @Override
    public void onRefreshTimeUpdate(String time) {
        tvUpdateTime.setText(time);
    }

    @Override
    public void onTimeUpdate(String yearMonth, String hoursAndMinute) {
        dateView.setText(yearMonth);
        tvTime.setText(hoursAndMinute);
        if ("06:30".equals(hoursAndMinute)) {
            //?????????????????????????????????
            ACacheService.putList("GET_IGNORE_BIRTHDAY", new ArrayList<>());
            //????????????????????????
            clearBirthMessage();
            showBirthdayDialog();
        }
    }

    /**
     * ????????????????????????????????????
     */
    private void clearBirthMessage() {
        if (CollectionUtils.isNotEmpty(mergedCallInfoBeanList)) {
            for (int i = mergedCallInfoBeanList.size() - 1; i >= 0; i--) {
                if ("1".equals(mergedCallInfoBeanList.get(i).getMessageType())) {
                    mergedCallInfoBeanList.remove(i);
                }
            }
        }
        if (warnMessageDialog != null && warnMessageDialog.isShowing()) {
            if (CollectionUtils.isNotEmpty(mergedCallInfoBeanList)) {
                warnMessageDialog.setList(mergedCallInfoBeanList);
            } else {
                warnMessageDialog.dismiss();
            }
        }
    }

    @Override
    public void onRefreshFinish() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingLayout.getVisibility() == View.VISIBLE) {
//                    loadingLayout.setVisibility(View.GONE);
                    subContentView.setIntercept(true);
                }
                LoadingUtil.getInstance().hide();
                afterRefresh();
                StatusManager.getInstance().setStatus(StatusConstants.CONFIG_CHANGED, 1);
            }
        });
    }

    @Override
    public void onSubDeptsReceived(List<DeptBean> deptBeans) {
        if (ConfigSingleton.getInstance().getPatientCardDivideDept()) {
            showOfficeName(deptBeans);
        } else {
            spinnerView.setVisibility(View.GONE);
        }
        // ????????????????????????????????????????????????
        if (nurseAssistantMenuView != null)
            nurseAssistantMenuView.setSurgicalPage();
    }

    private void waitForBlueTooth() {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Object>() {
            @Nullable
            @Override
            public Object doInBackground() throws Throwable {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "InterruptedException", e);
                    Thread.currentThread().interrupt();
                }
                return null;
            }

            @Override
            public void onSuccess(@Nullable Object result) {

            }
        });
    }

    //???????????????????????????
    Animation refreshAnimation;

    private void initRefreshTurnAround() {
        //??????
        refreshAnimation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
        LinearInterpolator lin = new LinearInterpolator();//????????????????????????
        refreshAnimation.setInterpolator(lin);

    }

    /**
     * ?????????
     */
    private void refreshing() {
        animRefreshImg.setVisibility(View.VISIBLE);
        animRefreshImg.startAnimation(refreshAnimation);
        refreshButton.setVisibility(View.GONE);
    }

    /**
     * ?????????
     */
    private void afterRefresh() {
        animRefreshImg.setVisibility(View.GONE);
        animRefreshImg.clearAnimation();
        refreshButton.setVisibility(View.VISIBLE);
    }

    /**
     * ?????????????????????
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }
            //?????????????????????
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_CONTACTS);
            }

            if (permissions.size() > 0) {
                String[] array = permissions.toArray(new String[permissions.size()]);
                ActivityCompat.requestPermissions(this, array, PERMISSION_REQUEST);
            } else {
                YzsTtsUtils.getInstance().init(this);
                LogXManager.getInstance().init(this);
            }
        } else {
            YzsTtsUtils.getInstance().init(this);
            LogXManager.getInstance().init(this);
        }
    }

    //??????????????????????????????
    final static int COUNTS = 5;// ????????????
    final static long DURATION = 1000;// ??????????????????
    long[] mHits = new long[COUNTS];

    /*
     * ?????????handler???
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initValues() {
        NavigationBarUtil.hideNavigationBar(this.getWindow());
        setContentView(R.layout.activity_main);
        //?????????ButterKnife
        ButterKnife.bind(this);
        ConfigSingleton.getInstance().setCurrentContext(this);
        initPresenter();
        //????????????????????????
        initMenuAnimation();
        //???????????????????????????????????????
        llRemind.setOnTouchListener(new RemindBtnOnTouchListener());
        getSharedValues();
        loadingLayout.setVisibility(View.VISIBLE);
        isConnect = false;
        ProxyHelper.getInstant().start(this);
        Log.i("MainActivity", "????????????");
        checkPermission();
        handler.setMainActivity(this);
        ConfigSingleton.getInstance().setPublicHandler(new PublicHandler());
        //?????????????????????
        ConfigInfo.initConfigMap(ACacheService.getConfigList());

        //??????LogX?????????????????????
        tvDeptName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //??????????????????????????????????????????
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                //???????????????????????????
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    mHits = new long[COUNTS];//?????????????????????
//                    Toast.makeText(this, "???????????????5???", Toast.LENGTH_LONG).show();
                    SettingActivity.showActivity(MainActivity.this);
                }
            }
        });

        if (!ConfigSingleton.getInstance().getActivityList().contains(this)) {
            ConfigSingleton.getInstance().getActivityList().add(this);
        }
    }


    // this is to start to be visible!
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void initPresenter() {
        mPresenter = new MainPresenter(this, this, this);
    }

    /**
     * ??????????????????
     */
    private void getSharedValues() {
        Properties p = PropertiesUtil.LoadProperties(this);
        Object o = SharedPreferencesUtil.get(this, "ServerUrl",
                p.get("serverUrl"));
        if (o != null) {
            ConfigSingleton.getInstance().setServerUrl(o.toString());
        } else {
            ConfigSingleton.getInstance().setServerUrl("welcome");
        }
    }

    /**
     * ????????????
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            boolean isAllGrant = true;
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    Toaster.makeToast(this, Toaster.TOAST_REMIND, String.format("?????????????????????????????????%s", Arrays.toString(permissions)), Toast.LENGTH_SHORT);
                    isAllGrant = false;
                    android.os.Process.killProcess(android.os.Process.myPid());
                    break;
                }
            }
            if (isAllGrant) {
                YzsTtsUtils.getInstance().init(this);
                LogXManager.getInstance().init(this);
            }
        }
    }


    /*
     * ????????????????????????
     */
    private void initMenuAnimation() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        refreshButton.setTypeface(font);
        animRefreshImg.setTypeface(font);
//        (menuLLs.get(0)).setBackgroundResource(R.color.list_selected);
    }

    /*
     * ?????????????????????????????????????????????
     */
    private void initialTvTime() {
        dateView.setText(DateUtil.getTimeString(ConfigSingleton.getInstance().getCurrentTime(), "MM???dd??? EEEE"));
        tvTime.setText(DateUtil.getTimeString(ConfigSingleton.getInstance().getCurrentTime(), "HH:mm"));
        tvUpdateTime.setText("????????????");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        /*if (isLock) {
            // ?????????????????????
            return super.dispatchTouchEvent(ev);
        }*/
        switch (ev.getAction() & ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                StatusManager.getInstance().setStatus(StatusConstants.STATUS_TOUCH, true);
                pointerId0 = -1;
                pointerId1 = -1;
                pointerId2 = -1;
                pointerId3 = -1;
                // ???1?????????
                pointerId0 = ev.getPointerId(0);
                oldXPoint0 = ev.getX(0);
                oldYPoint0 = ev.getY(0);
                mPointCount = 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i(TAG, "dispatchTouchEvent: ???????????????" + ev.getPointerCount());
                mPointCount = ev.getPointerCount();
                if (ev.getActionIndex() == 1) {
                    pointerId1 = ev.getPointerId(1);
                    oldXPoint1 = ev.getX(1);
                    oldYPoint1 = ev.getY(1);
                }
                if (ev.getActionIndex() == 2) {
                    pointerId2 = ev.getPointerId(2);
                    oldXPoint2 = ev.getX(2);
                    oldYPoint2 = ev.getY(2);
                }
                if (ev.getActionIndex() == 3) {
                    pointerId3 = ev.getPointerId(3);
                    oldXPoint3 = ev.getX(3);
                    oldYPoint3 = ev.getY(3);
                }
                break;


            case MotionEvent.ACTION_MOVE:
                if (ev.getPointerCount() == 4 || ev.getPointerCount() == 5) {


                    float offsetY0 = 0, offsetY1 = 0, offsetY2 = 0, offsetY3 = 0;
                    if (pointerId0 != -1 && ev.findPointerIndex(pointerId0) != -1) {

                        offsetY0 = ev.getY(ev.findPointerIndex(pointerId0)) - oldYPoint0;
                    }
                    if (pointerId1 != -1 && ev.findPointerIndex(pointerId1) != -1) {
                        offsetY1 = ev.getY(ev.findPointerIndex(pointerId1)) - oldYPoint1;
                    }
                    if (pointerId2 != -1 && ev.findPointerIndex(pointerId2) != -1) {

                        offsetY2 = ev.getY(ev.findPointerIndex(pointerId2)) - oldYPoint2;
                    }
                    if (pointerId3 != -1 && ev.findPointerIndex(pointerId3) != -1) {

                        offsetY3 = ev.getY(ev.findPointerIndex(pointerId3)) - oldYPoint3;
                    }
                    Log.i(TAG, "dispatchTouchEvent: absY(" + offsetY0 + "," + offsetY1 + "," + offsetY2 + "," + offsetY3 + ")");
                    if (ev.getPointerCount() > 3 && offsetY0 > 200 && offsetY1 > 200 && offsetY2 > 200 && offsetY3 > 200) {

                        Log.i(TAG, "dispatchTouchEvent: " + ev.getPointerCount() + "???????????????");
//                        tvDeptName.setText("????????????");
                    } else if (ev.getPointerCount() > 3 && offsetY0 < -200 && offsetY1 < -200 && offsetY2 < -200 && offsetY3 < -200) {

                        Log.i(TAG, "dispatchTouchEvent: " + ev.getPointerCount() + "???????????????");
//                        tvDeptName.setText("????????????");
                    } else if (ev.getPointerCount() > 4) {
                        float[] pointYs = {offsetY0, offsetY1, offsetY2, offsetY3};
                        int positive = -1, negative = -1;
                        for (float v : pointYs) {
                            if (v > 0 && positive == -1) {
                                positive = 1;
                            }
                            if (v < 0 && negative == -1) {
                                negative = 1;
                            }
                            if (negative + positive == 2) break;
                        }
                        Log.i(TAG, "dispatchTouchEvent: " + ev.getPointerCount() + "??????");
//                        tvDeptName.setText("????????????");
                    }
                }
                break;
            //???????????????????????????ACTION_UP??????????????????
            case MotionEvent.ACTION_UP:
                StatusManager.getInstance().setStatus(StatusConstants.STATUS_TOUCH, false);
                Log.i(TAG, "dispatchTouchEvent: " + ev.getPointerCount() + "????????????up");
                if (mPointCount == 5) {
                    //????????????????????????
                    menuItemDialog = new MenuItemDialog(this);
                    menuItemDialog.show();
                }
                break;
            //??????????????????????????????
            default:
                StatusManager.getInstance().setStatus(StatusConstants.STATUS_TOUCH, true);
                break;
        }
        Log.i(TAG, "dispatchTouchEvent: " + ev.getPointerCount() + "????????????");
        return super.dispatchTouchEvent(ev);
    }

    @OnClick(R.id.button_magic)
    public void magic(Button v) {
        magicWay(false);
    }

    private void magicWay(boolean isBlue) {
        if (!isBlue || !ConfigSingleton.getInstance().getIsMagic()) {
            if (ConfigSingleton.getInstance().getIsMagic()) {
                buttonMagic.setBackground(SkinManager.getInstance().getMipmap(R.mipmap.icon_magic_unselector));
                ConfigSingleton.getInstance().setIsMagic(false);
            } else {
                buttonMagic.setBackground(SkinManager.getInstance().getMipmap(R.mipmap.icon_magic_selector));
                ConfigSingleton.getInstance().setIsMagic(true);
            }
            StatusManager.getInstance().setStatus(StatusConstants.STATUS_ISMAGIC, ConfigSingleton.getInstance().getIsMagic());
        }
        if (ConfigSingleton.getInstance().getCurrentFragment() instanceof PatientCardFragment) {
            loadingLayout.setVisibility(View.GONE);
        } else if (ConfigSingleton.getInstance().getCurrentFragment() instanceof TaskFragment ||
                ConfigSingleton.getInstance().getCurrentFragment() instanceof DynamicFragment) {
            loadingLayout.setVisibility(View.GONE);
//            ConfigSingleton.getInstance().getCurrentFragment().refreshView();
        }
    }


    public void redirectToSettingView(View view) {
        mPresenter.showSettingDialog();
    }


    @OnTouch({R.id.button_magic, R.id.informationImg})
    public boolean menuButtonTouch(Button v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (v.getId() == R.id.button_magic) {
                    v.setBackgroundResource(R.mipmap.icon_magic_selector);
                } else if (v.getId() == R.id.informationImg) {
                    v.setBackground(SkinManager.getInstance().getMipmap(R.mipmap.config_selected));
                }
                break;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                if (v.getId() == R.id.button_magic) {
                    v.setBackgroundResource(R.mipmap.icon_magic_unselector);
                } else if (v.getId() == R.id.informationImg) {
                    v.setBackground(SkinManager.getInstance().getMipmap(R.mipmap.icon_setting));
                }
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (blueTimer != null) {
            blueTimer.cancel();
            blueTimer = null;
        }

        //???????????????????????????webSocket??????
        mPresenter.disconnectThirdPartyWebSocket();

        //?????????NaNa????????????WebSocket??????
        mPresenter.disConnectServerWebSocket();

        onStopFaceRecognition();

        RefWatcher refWatcher = LeakApplication.getRefWatcher(this);//1
        refWatcher.watch(this);

//        SpeechCenter.getInstance().release();
        YzsTtsUtils.getInstance().release();
        YzsAsrUtils.getInstance().release();
        manager = null;
        if (Build.VERSION.SDK_INT > 26) {
            android.os.Process.killProcess(android.os.Process.myPid());
            InfusionFloatAction.getInstance().stop(getApplicationContext());
        }
        //ZHBFDEVIMP-166 Build.VERSION.SDK_INT=23???????????????????????????????????????
        //20210311
        else {
            finish();
            System.exit(0);
        }
    }


    public void lockClick(View view) {
        if (isLock) {
            unLock(null);
        } else {
            lockPage();
        }
    }

    public void unLock(View view) {
        if (mPointCount != 5) {
            mPresenter.showPinLoginDialog();
        }
    }

    @Override
    public void lockPage() {
        lockPage(null);
        closeDialog();
    }

    @Override
    public void onNetWorkReconnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toaster.makeToast(MainActivity.this, Toaster.TOAST_SUCCESS, "??????????????????", Toast.LENGTH_SHORT);
            }
        });

        if (loadingLayout.getVisibility() == View.VISIBLE || LoadingUtil.getInstance().isShowing()) {
            mPresenter.checkNet();
        }
    }

    private void lockPage(View view) {
        if (isLock) return;
        checkFace = false;
        subContentView.setIntercept(true);
        tvName.setText("");
        imgLock.setImageDrawable(SkinManager.getInstance().getDrawable(R.drawable.new_lock));
        dynamicAddSkinEnableView(imgLock,"src",R.drawable.new_lock);
        isLock = true;
        onStartFaceRecognition();
        StatusManager.getInstance().setStatus(StatusConstants.STATUS_IS_UNLOCK, false);
        ConfigSingleton.getInstance().setCurrentNurseModel(new NurseModel(ParamsConstants.DEPARTMENT_PIN_UNLOCK,
                ParamsConstants.DEPARTMENT_PIN_UNLOCK, null, null, null, "??????",
                null, null, null, "-1"));
        buttonMagic.setVisibility(View.GONE);
        Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_LOCK, "???????????????", Toast.LENGTH_SHORT);
        if (ConfigSingleton.getInstance().getIsMagic()) {
            magicWay(false);
        } else {
            if (ConfigSingleton.getInstance().getCurrentFragment() instanceof PatientCardFragment) {
                loadingLayout.setVisibility(View.GONE);
            }
        }
        //????????????????????????????????????????????????
        Intent intent = new Intent(ParamsConstants.CHANGE_TASK_VIEW);
        sendBroadcast(intent);
    }

    /**
     * ????????????
     **/
    @Override
    public void checkUpdate() {
        String url = ConfigSingleton.getInstance().getServerUrl();
        url = url + "apk/";
        if (manager == null) {
            manager = new UpdateManager(this, url);
        }
        // ??????????????????
        manager.checkUpdate(url);
    }

    @Override
    public boolean isInitFinish() {
        if (loadingLayout.getVisibility() == View.VISIBLE) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isEndAnim() {
        return isStartAnimEnd;
    }

    //??????????????????
    private void closeDialog() {
        StatusManager.getInstance().setStatus(StatusConstants.STATUS_CLOSE_ALL_DIALOG, 1);
        AutoCloseDialogManager.getInstance().clean();
    }


    private void afterLoginByUser() {
        subContentView.setIntercept(false);
        Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_UNLOCK, "???????????????", Toast.LENGTH_SHORT);
        llSurfaceView.setVisibility(View.INVISIBLE);
        mPresenter.stopFaceRecognition();
        imgLock.setImageDrawable(SkinManager.getInstance().getDrawable(R.drawable.new_unlock));
        dynamicAddSkinEnableView(imgLock,"src",R.drawable.new_unlock);
        isLock = false;
        StatusManager.getInstance().setStatus(StatusConstants.STATUS_IS_UNLOCK, true);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        tvName.setText(String.format("??????,  %s", ConfigSingleton.getInstance().getCurrentNurseModel().getUserName()));
        tvName.setClickable(false);
        if (ConfigSingleton.getInstance().isLoginVoiceOn())
            computeTtsString(String.format("??????,  %s", ConfigSingleton.getInstance().getCurrentNurseModel().getUserName()));
        buttonMagic.setVisibility(View.VISIBLE);
        NetApi.getAllMyPatients(ConfigSingleton.getInstance().getCurrentNurseModel().getUserId(), new ResponseCallBack<List<MyPatientsModel>>() {
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
        //????????????????????????
        mPresenter.addLoginCount();
//        ConfigSingleton.getInstance().getCurrentFragment().refreshView();
    }

    @SuppressWarnings("unchecked")
    private void initMyPatientList() {
        List<PatientInfo> myPatientsList = new ArrayList<>();
        //???????????????????????????
        List<MyPatientsModel> allMyPatientList1 = ACacheService.getAllMyPatientList();
        for (int i = 0; i < allMyPatientList1.size(); i++) {
            if (allMyPatientList1.get(i).getNurseId().equals(ConfigSingleton.getInstance().getCurrentNurseModel().getUserId())) {
                //???????????????????????????
                myPatientsList = allMyPatientList1.get(i).getPatients();
                break;
            }
        }
        //????????????
        List<PatientInfo> patientList = ACacheService.getPatients();
        for (int i = 0; i < myPatientsList.size(); i++) {
            if (myPatientsList.get(i) != null) {
                for (int j = 0; j < patientList.size(); j++) {
                    if (patientList.get(j).getPatientId() != null && patientList.get(j).getPatientId() != "" && patientList.get(j).getPatientId().equals(myPatientsList.get(i).getPatientId())) {
                        myPatientsList.set(i, patientList.get(j));
                        break;
                        //??????ZHBFDEVIMP-165??????
                    } else if (patientList.get(j).getBedLabel().equals(myPatientsList.get(i).getBedLabel())) {
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

        ACache.get(this).put("MYPATIENTLIST", array_mine_patientInfo);
//        ConfigSingleton.getInstance().getCurrentFragment().refreshView();
    }

    public void redirectToHelpView(View view) {
        mPresenter.showHelpDialog();
    }


    @Override
    public void onStartFaceRecognition() {
        if (!ConfigSingleton.getInstance().getLoginType().contains("??????")) {
            return;
        }
        if (!isLock) {
            return;
        }
        startFaceRecognition(false, 0);
    }

    public void startFaceRecognition(boolean isFromOper, int sec) {
        if (isFromOper) {
            //??????????????????????????????10s??????????????????
            if (sec == 0) {
                sec = 10;
            }
            handler.postDelayed(timerRunnable, sec * 1000);
        }
        if (!CameraUtil.hasCamera()) {
            Log.e("main", "???????????????????????????????????????????????????");
            return;
        }
        if (!ArcFaceSingleton.getInstance().getMFaceDB().getMRegister().isEmpty()) {
            ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Object>() {
                @Nullable
                @Override
                public Object doInBackground() throws Throwable {
                    DetectFaceUtil.getInstance().getCamera();
                    return null;
                }

                @Override
                public void onFail(Throwable t) {
                    super.onFail(t);
                }

                @Override
                public void onSuccess(@Nullable Object result) {
                    cameraSurfaceView = new CameraSurfaceView(MainActivity.this);
                    cameraGLSurfaceView = new CameraGLSurfaceView(MainActivity.this);
                    DetectFaceUtil.getInstance().setView(cameraSurfaceView, cameraGLSurfaceView);

                    DetectFaceUtil.getInstance().start(null, true);
                    DetectFaceUtil.getInstance().initLoginCallback(MainActivity.this);

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(1, 1);
                    cameraSurfaceView.setLayoutParams(layoutParams);
                    ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    cameraGLSurfaceView.setLayoutParams(layoutParams1);
                    llSurfaceView.addView(cameraSurfaceView);
                    llSurfaceView.addView(cameraGLSurfaceView);
                    llSurfaceView.setVisibility(View.VISIBLE);
                }
            });

        } else {
            Log.e(this.getClass().getName(), "????????????????????????????????????");
            if (!isFromOper) {
                tvName.post(() -> {
                    tvName.setText("???????????????????????????");
                    tvName.setOnClickListener(v -> unLock(v));
                });
            }
        }
    }

    @Override
    public void onStopFaceRecognition() {
        if (!CameraUtil.hasCamera()) {
            Log.e("onStopFaceRecognition", "???????????????????????????????????????????????????");
            return;
        }
        CameraSingleton.getInstance().stopPreView();
        if (cameraSurfaceView != null) {
            cameraSurfaceView.setOnCameraListener(null);
            cameraSurfaceView = null;
        }
        if (null != cameraGLSurfaceView) {
            cameraGLSurfaceView = null;
        }
        if (null != llSurfaceView) {
            llSurfaceView.removeAllViews();
        }
        DetectFaceUtil.getInstance().stop();
    }

    /**
     * ????????????????????????????????????????????????
     */
    @Override
    public void generateSpeakText() {
        speakMap.clear();
        for (CallInfoBean callInfoBean : mergedCallInfoBeanList) {
            if (callInfoBean.getCallStatus() == CallInfoBean.CALL_STATUS_NOT_READ) {
                if (callInfoBean.isCallMessage() && !ConfigInfo.get(ConfigInfo.SOUND_CONTROL).contains("??????")) {
                    continue;
                }
                if (callInfoBean.isInfusionMessage() && !ConfigInfo.get(ConfigInfo.SOUND_CONTROL).contains("??????")) {
                    continue;
                }
                if ("1".equals(callInfoBean.getMessageType()) || "2".equals(callInfoBean.getMessageType()))
                    continue;
                String speakText;
                //??????????????????????????????????????????
                if (callInfoBean.isFromServer()) {
                    speakText = callInfoBean.getServerWebSocketMsgBean().getContent();
                }
                //????????????????????????
                else if (!callInfoBean.isRoomCall()) {
                    if (callInfoBean.isInfusionMessage()) {
                        speakText = callInfoBean.getMessageCodeInfo().replaceAll("\n", "");
                    } else {
                        speakText = callInfoBean.getBedLabel().replaceAll("???", "") + "???" + callInfoBean.getCallEventName();
                    }
                }
                //??????????????????
                else {
                    speakText = callInfoBean.getRoomName().replaceAll("??????|???", "") + "????????????";
                }
                int currentVersion = android.os.Build.VERSION.SDK_INT;
                //Android 8.0?????????????????????
//                if (currentVersion >= 26) {
//                    speakText = EscapeUtils.unescape(unicodeEncode(speakText));
//                }
                if (!TextUtils.isEmpty(speakText)) {
                    if (callInfoBean.isInfusionMessage()) {
                        speakMap.put(callInfoBean.getBedLabel(), new SpeakBean(speakText, 0, 1));
                    } else {
                        speakMap.put(callInfoBean.getMessageId(), new SpeakBean(speakText, 0, 3));
                    }
                }
            }
        }
    }

    public static String unicodeEncode(String string) {
        char[] utfBytes = string.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
//                hexB = "00" + hexB;
            }
            hexB = "00" + hexB;
            int strRes = hexB.length();
            hexB = hexB.substring(strRes - 2, strRes) + hexB.substring(strRes - 4, strRes - 2);

            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes.replaceAll("\\\\", "%");
    }

    /**
     * ??????????????????
     */
    private void dealBirthdayNum() {
        if (CollectionUtils.isNotEmpty(mergedCallInfoBeanList)) {
            for (int k = 0; k < mergedCallInfoBeanList.size(); k++) {
                CallInfoBean callInfoBean = mergedCallInfoBeanList.get(k);
                //??????????????????????????????
                if ("1".equals(callInfoBean.getMessageType())) {
                    mergedCallInfoBeanList.remove(k);
                    k--;
                }
            }
        }
    }


    /**
     * ???????????????????????????????????????
     * ????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private void dealNocallbackServerMsg() {
        if (CollectionUtils.isNotEmpty(mergedCallInfoBeanList)) {
            for (int k = 0; k < mergedCallInfoBeanList.size(); k++) {
                CallInfoBean callInfoBean = mergedCallInfoBeanList.get(k);
                if (CallInfoBean.MESSAGE_TYPE_FROM_SERVER.equals(callInfoBean.getMessageType()) && !callInfoBean.getServerWebSocketMsgBean().isHasCallback()) {
                    mergedCallInfoBeanList.remove(k);
                    k--;
                }
            }
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void showWarnMessageNum() {
        int birthdayNum = showBirthdayMessageNum();
        //?????????????????????????????????????????????????????????????????????
        dealCallTimeInfo();
        if (CollectionUtils.isNotEmpty(mergedCallInfoBeanList) || birthdayNum > 0) {
            llRemindCall.setVisibility(View.VISIBLE);
            if (CollectionUtils.isNotEmpty(mergedCallInfoBeanList)) {
                tvRemindCallNum.setText(String.valueOf(mergedCallInfoBeanList.size() + birthdayNum));
            } else {
                tvRemindCallNum.setText(String.valueOf(birthdayNum));
            }
        } else {
            llRemindCall.setVisibility(View.GONE);
            if (warnMessageDialog != null && warnMessageDialog.isShowing()) {
                warnMessageDialog.dismiss();
            }
        }
    }

    /**
     * ??????????????????????????????????????????
     */
    private void dealCallTimeInfo() {
        if (ConfigSingleton.getInstance().getCallOverTime() < 0 && CollectionUtils.isNotEmpty(mergedCallInfoBeanList)) {
            for (int k = 0; k < mergedCallInfoBeanList.size(); k++) {
                CallInfoBean callInfoBean = mergedCallInfoBeanList.get(k);
                if (callInfoBean != null && "2".equals(callInfoBean.getMessageType())) {
                    mergedCallInfoBeanList.remove(k);
                    k--;
                }
            }
        }
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    private int showBirthdayMessageNum() {
        List<PatientInfo> patientInfos = ACacheService.getIgnorePatBirthday();
        if (CollectionUtils.isNotEmpty(birthPatientList) && ConfigInfo.get(ConfigInfo.SPEC_FUNCTION).contains(BIRTHDAY_TIP)) {
            int size = birthPatientList.size();
            for (int i = 0; i < birthPatientList.size(); i++) {
                PatientInfo patientInfo = birthPatientList.get(i);
                if (CollectionUtils.isNotEmpty(patientInfos)) {
                    for (int m = 0; m < patientInfos.size(); m++) {
                        PatientInfo patientInfo1 = patientInfos.get(m);
                        //??????????????????????????????
                        if (patientInfo.getPatientId().equals(patientInfo1.getPatientId()) &&
                                patientInfo.getVisitId().equals(patientInfo1.getVisitId())) {
                            size--;
                            break;
                        }
                    }
                }
            }
            return size;
        }
        return 0;
    }


    @Override
    public void showWarnMessageDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ((CollectionUtils.isNotEmpty(mergedCallInfoBeanList) || CollectionUtils.isNotEmpty(birthPatientList)) && (null == warnMessageDialog || !warnMessageDialog.isShowing())) {
                    warnMessageDialog = new WarnMessageDialog(MainActivity.this, mergedCallInfoBeanList, timeOutDialog);
                    warnMessageDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            warnMessageDialog = null;
                            dealNocallbackServerMsg();
                            dealBirthdayNum();
                            showWarnMessageNum();
                            speakMap.clear();
//                            SpeechCenter.getInstance().onPause();
                            YzsTtsUtils.getInstance().stop();
                        }
                    });
                    warnMessageDialog.setOnItemDealListener(new WarnMessageDialog.onItemClickListener() {
                        @Override
                        public void onItemDeal(CallInfoBean callInfoBean) {
                            if (callInfoBean.isInfusionMessage()) {
                                speakMap.remove(callInfoBean.getBedLabel());
                                for (int i = 0; i < mergedCallInfoBeanList.size(); i++) {
                                    if (mergedCallInfoBeanList.get(i).getBedLabel().equals(callInfoBean.getBedLabel())) {
                                        mergedCallInfoBeanList.remove(i);
                                    }
                                }
                                //mergedCallInfoBeanList.removeIf(infobean->infobean.getBedLabel().equals(callInfoBean.getBedLabel()));
                            } else {
                                speakMap.remove(callInfoBean.getMessageId());
                                for (int i = 0; i < mergedCallInfoBeanList.size(); i++) {
                                    boolean isSameMessage = mergedCallInfoBeanList.get(i) != null && !TextUtils.isEmpty(mergedCallInfoBeanList.get(i).getMessageId()) && mergedCallInfoBeanList.get(i).getMessageId().equals(callInfoBean.getMessageId());
                                    if (isSameMessage) {
                                        mergedCallInfoBeanList.remove(i);
                                    }
                                }
                                //mergedCallInfoBeanList.removeIf(infobean->infobean.getMessageId().equals(callInfoBean.getMessageId()));
                            }
                            mPresenter.updateCallList(callInfoBean);
                        }
                    });
                    warnMessageDialog.show();
                }
            }
        });
    }


    @Override
    public void refreshWarnMessageDialog() {
        if (ConfigInfo.get(ConfigInfo.SPEC_FUNCTION).contains(BIRTHDAY_TIP) && CollectionUtils.isNotEmpty(birthPatientList)) {
            if (mergedCallInfoBeanList == null) {
                mergedCallInfoBeanList = new CopyOnWriteArrayList<>();
            }
            for (int k = 0; k < birthPatientList.size(); k++) {
                PatientInfo patientInfo = birthPatientList.get(k);
                boolean hasPat = false;
                //??????????????????????????????????????????????????????
                List<PatientInfo> patientInfos = ACacheService.getIgnorePatBirthday();
                for (int i = 0; i < patientInfos.size(); i++) {
                    PatientInfo patientInfo1 = patientInfos.get(i);
                    if (patientInfo.getPatientId().equals(patientInfo1.getPatientId()) && patientInfo.getVisitId().equals(patientInfo1.getVisitId())) {
                        //???????????????????????????????????????
                        hasPat = true;
                        break;
                    }
                }
                if (CollectionUtils.isNotEmpty(mergedCallInfoBeanList)) {
                    for (CallInfoBean callInfoBean : mergedCallInfoBeanList) {
                        if ("1".equals(callInfoBean.getMessageType())) {
                            PatientInfo patientInfo1 = callInfoBean.getPatientInfo();
                            if (patientInfo.getPatientId().equals(patientInfo1.getPatientId()) && patientInfo.getVisitId().equals(patientInfo1.getVisitId())) {
                                //???????????????????????????????????????
                                hasPat = true;
                                break;
                            }
                        }
                    }
                }
                //??????????????????????????????????????????
                if (!hasPat) {
                    CallInfoBean callInfoBean = new CallInfoBean();
                    callInfoBean.setMessageType("1");
                    callInfoBean.setPatId(patientInfo.getPatientId());
                    callInfoBean.setPatientName(patientInfo.getPatientName());
                    callInfoBean.setBedLabel(patientInfo.getBedLabel());
                    callInfoBean.setNurseLevel(patientInfo.getNurseLevel());
                    callInfoBean.setPatientInfo(patientInfo);
                    mergedCallInfoBeanList.add(callInfoBean);
                }
            }
        }
        if (warnMessageDialog != null && warnMessageDialog.isShowing()) {
            if (CollectionUtils.isNotEmpty(mergedCallInfoBeanList)) {
                warnMessageDialog.setList(mergedCallInfoBeanList);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        warnMessageDialog.dismiss();
                    }
                });
            }
        }
    }

    @OnClick(R.id.ll_remind_call)
    public void openWarnMessageDialog() {
        refreshWarnMessageDialog();
        showWarnMessageDialog();
    }


    /**
     * ???????????????????????????????????????int?????????
     *
     * @param port ???????????????
     * @return true->?????????????????????false->??????????????????
     */
    private boolean isCanParseInt(String port) {
        if (TextUtils.isEmpty(port)) {
            return false;
        }
        return port.matches("\\d+");
    }

    private void showBirthdayDialog() {
        //??????????????????????????????
        if (ConfigInfo.get(ConfigInfo.SPEC_FUNCTION).contains(BIRTHDAY_TIP) && CollectionUtils.isNotEmpty(birthPatientList) && isBirthDayDialogAllowed()) {
            BusinessData.getInstance().setBirthPatientList(birthPatientList);
            showWarnMessageDialog();
        }
    }

    @Override
    public void onReservedPatRefresh(List<PatientInfo> patientInfos) {
        if (reservedPatDialog != null && reservedPatDialog.isResumed()) {
            reservedPatDialog.refreshData(patientInfos);
        }
        if (CollectionUtils.isEmpty(patientInfos)) {
            rlReservedPat.setVisibility(View.GONE);
        } else {
            rlReservedPat.setVisibility(View.GONE);
            tvReservedPatNum.setText(String.valueOf(patientInfos.size()));
        }
    }

    /**
     * ?????????????????????
     *
     * @param patientInfos
     */
    @Override
    public void setTodayBornPatients(List<PatientInfo> patientInfos) {
        List<PatientInfo> birthdayPatientList = new ArrayList<>();
        for (PatientInfo patientInfo : patientInfos) {
            if (!TextUtils.isEmpty(patientInfo.getDateOfBirth()) &&
                    new SimpleDateFormat("MM-dd", Locale.getDefault()).format(ConfigSingleton.getInstance().getCurrentTime())
                            .equals(DateUtil.millisecondToDate(patientInfo.getDateOfBirth(), new SimpleDateFormat("MM-dd", Locale.getDefault())))
                    && !new SimpleDateFormat("yyMMdd", Locale.getDefault()).format(ConfigSingleton.getInstance().getCurrentTime())
                    .equals(DateUtil.millisecondToDate(patientInfo.getDateOfBirth(), new SimpleDateFormat("yyMMdd", Locale.getDefault())))) {
                birthdayPatientList.add(patientInfo);
            }
        }
        //????????????????????????????????????????????????
        if (ConfigInfo.get(ConfigInfo.SPEC_FUNCTION).contains(BIRTHDAY_TIP)) {
            if (CollectionUtils.isNotEmpty(birthdayPatientList)) {
                if (CollectionUtils.isEmpty(birthPatientList)) {
                    birthPatientList = new ArrayList<>();

                }
                //???????????????????????????????????????????????????
                if (birthPatientList.size() > birthdayPatientList.size()) {
                    birthPatientList = birthdayPatientList;
                    dealBirthdayNum();
                    showWarnMessageNum();
                }
                //?????????????????????????????????
                else {
                    for (int i = 0; i < birthdayPatientList.size(); i++) {
                        PatientInfo patientInfo = birthdayPatientList.get(i);
                        boolean bool = false;
                        for (int m = 0; m < birthPatientList.size(); m++) {
                            PatientInfo patientInfo1 = birthPatientList.get(m);
                            if (patientInfo.getVisitId().equals(patientInfo1.getVisitId()) && patientInfo.getPatientId().equals(patientInfo1.getPatientId())) {
                                bool = true;
                            }
                        }
                        //?????????????????????????????????????????????
                        if (!bool) {
                            birthPatientList = birthdayPatientList;
                            dealBirthdayNum();
                            showWarnMessageNum();
                            refreshWarnMessageDialog();
                            showBirthdayDialog();
                            break;
                        }
                    }
                }

            } else {
                birthPatientList = birthdayPatientList;
                showWarnMessageNum();
            }
        } else {
            birthPatientList = birthdayPatientList;
            showWarnMessageNum();
        }
        //ZHBFDEVIMP-151????????????
        //refreshWarnMessageDialog();
    }

    /**
     * 6:30????????????????????????
     *
     * @return
     */
    private boolean isBirthDayDialogAllowed() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ConfigSingleton.getInstance().getCurrentTime());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        return hour * 60 + min >= 6 * 60 + 30;
    }


    /**
     * ????????????????????????
     */
    class RemindBtnOnTouchListener implements View.OnTouchListener {
        boolean isMoveOut = false;

        RectF rectF;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    int[] location = new int[2];
                    // ?????????????????????????????????????????????????????????????????????????????? x???y ??????
                    v.getLocationOnScreen(location);
                    rectF = new RectF(location[0], location[1], location[0] + v.getWidth(),
                            location[1] + v.getHeight());
                    v.setBackgroundColor(Color.parseColor("#E5963C"));
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getRawX(); // ????????????????????????????????? x ?????????
                    float y = event.getRawY(); // ????????????????????????????????? y ?????????
                    if (!rectF.contains(x, y)) {
                        isMoveOut = true;
                        v.setBackgroundColor(SkinManager.getInstance().getColor(R.color.colorBackground));
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    v.setBackgroundColor(SkinManager.getInstance().getColor(R.color.colorBackground));
                    if (!isMoveOut) {
                        /*   */
                        mPresenter.showAIAdviceDialog();
                    }
                    isMoveOut = false;
                    v.performClick();
                    break;
            }
            return true;
        }
    }

    /*
     * ????????????
     */
    public static class MainHandler extends Handler {
        WeakReference<MainActivity> weakMainActivity;
        private DutyNurseSettingDialog mDutyNurseSettingDialog;


        private void setMainActivity(MainActivity mainActivity) {
            this.weakMainActivity = new WeakReference<>(mainActivity);
        }

        @Override
        @SuppressLint("SetTextI18n")
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            final MainActivity mainActivity = this.weakMainActivity.get();
            switch (msg.what) {
                case ParamsConstants.STOPTURNING:
                    mainActivity.afterRefresh();
                    break;
                case ParamsConstants.SLIDESHOW:
                    break;
                case ParamsConstants.BLUETOOTH_LOGIN:
                    mainActivity.afterLoginByUser();
                    break;
                case ParamsConstants.OPENBLUETH:
                    break;
                case ParamsConstants.STARTSLIDE:
//                    if (mainActivity.loadingLayout.getVisibility() == View.GONE && mainActivity.commonViewPager.getVisibility() == View.VISIBLE) {
//                        if (mainActivity.llLock.getVisibility() == View.VISIBLE) {
//                            mainActivity.slideShow();//??????????????????
//                        }
//                    }
                    break;
                case ParamsConstants.RESTART_BLUETOOTH:
                    if (msg.arg1 == 1) {
                        //?????????????????????5??????????????????????????????
                        if (mainActivity.blueTimer == null && !ConfigSingleton.getInstance().getIsBlueConnect()) {
                            mainActivity.blueTimer = new Timer();
                            mainActivity.blueTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                }
                            }, 5000, 5000);
                        }
                    } else if (msg.arg1 == -1) {
                        //??????????????????????????????????????????
                        ConfigSingleton.getInstance().setIsBlueConnect(true);
                        if (mainActivity.blueTimer != null) {
                            mainActivity.blueTimer.cancel();
                            mainActivity.blueTimer = null;
                        }
                    } else if (msg.arg1 == 2) {
                        //??????????????????,????????????????????????
                        ConfigSingleton.getInstance().setIsBlueConnect(false);
                    }
                    break;
                case ParamsConstants.FACE_CHECKING:
                    mainActivity.tvName.setText("???????????????...");
                    mainActivity.tvName.setClickable(false);
                    break;
                case ParamsConstants.JUMP_TO_PATIENTCARD:
                    mainActivity.showSubContent((String) msg.obj);
                    mainActivity.nurseAssistantMenuView.onCloseSpin();
                    break;
                case ParamsConstants.SHOW_TITLE:
                    mainActivity.title.setText((String) msg.obj);
                    break;
                case ParamsConstants.START_ANIM_END:
                    Intent intent = new Intent(ParamsConstants.FINSH_SPLASH_ANIM);
                    mainActivity.sendBroadcast(intent);
                    MainActivity.isStartAnimEnd = true;
                    if (isConnect) {
                        //??????????????????
                    } else {
                        //?????????????????????????????????????????????????????????????????????????????????
                        if (isOnce) {
                            //??????????????????
                            mainActivity.mPresenter.showNetDialog();
                        } else if (isConnectError) {
                            Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_FAILED, "???????????????????????????", Toast.LENGTH_LONG);
                        }
                    }
                    break;
                case ParamsConstants.CHANGE_WRAD:
                    new BedRangeInfoDao(mainActivity).deleteAll();
                    BedRangeInfoMap.getInstance().setBedRangeMap(new HashMap<>());
                    if (CollectionUtils.isNotEmpty(mainActivity.birthPatientList)) {
                        mainActivity.birthPatientList.clear();
                    }
                    if (CollectionUtils.isNotEmpty(mainActivity.mergedCallInfoBeanList)) {
                        mainActivity.mergedCallInfoBeanList.clear();
                    }
                    mainActivity.showWarnMessageNum();
                    if (0 == ConfigSingleton.getInstance().getCallSource()) {
                        mainActivity.disconnect();
                    } else if (1 == ConfigSingleton.getInstance().getCallSource()) {
                        mainActivity.mPresenter.closeRabbitmq();
                    }
                    if (mainActivity.nurseAssistantMenuView != null) {
                        mainActivity.nurseAssistantMenuView.onChangeWard();
                    }
                    mainActivity.getSubDepts();
                    break;
                case ParamsConstants.SHOW_PERSONAL_DIALOG:
                    mainActivity.mPresenter.showPassLoginDialog();
                    break;
                case ParamsConstants.SHOW_PERSONAL_FACE:
                    mainActivity.isFaceSuccess = false;
                    mainActivity.startFaceRecognition(true, msg.arg2);
                    break;
                case ParamsConstants.CLOSE_SCREEN_PHOTO:
                    if (mainActivity.loadingLayout.getVisibility() == View.VISIBLE) {
                        mainActivity.loadingLayout.setVisibility(View.GONE);
                        mainActivity.subContentView.setIntercept(true);
                    }
                    break;
                case ParamsConstants.START_FACE_RECOGNITION:
                    mainActivity.onStartFaceRecognition();
                    break;
                case ParamsConstants.MSG_FACE_LAND_SOUND:
                    mainActivity.computeTtsString("????????????");
                    break;
                case ParamsConstants.MSG_CLOSE_FACE:
                    mainActivity.onStopFaceRecognition();
                    break;
                case ParamsConstants.ANNOUNCEMENT_SHOW:
                    mainActivity.autoPlayAnnouncement();
                    MainActivity.handler.sendEmptyMessageDelayed(ParamsConstants.ANNOUNCEMENT_SHOW, 10 * 1000L);
                    break;
                case ParamsConstants.SHOW_BACK_BTN:
                    mainActivity.toWebPage((String) msg.obj);
                    break;
                case ParamsConstants.MSG_AIADVICE_SOUND:
                    mainActivity.computeTtsString("????????????????????????????????????????????????????????????");
                    break;
                case ParamsConstants.BIRTHDAY_PAT:
                    List<PatientInfo> patientInfos = (List<PatientInfo>) msg.obj;
                    Log.i(TAG, "handleMessage: " + patientInfos.size());
                    mainActivity.setTodayBornPatients(patientInfos);
                    break;
                case ParamsConstants.CHANGE_DENSITY:
//                    ScreenadapterUtils.setCustomDensity(mainActivity, mainActivity.getApplication(), ScreenadapterUtils.DEFAULT_DENSITY);
                    break;
                //??????????????????????????????
                case ParamsConstants.MSG_CLOSE_ASR_VIEW:
                    mainActivity.closeAsrTipView();
                    break;
                //??????????????????????????????????????????
                case ParamsConstants.CHANGE_WARD_THEME:
                    mainActivity.applyTheme();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }

    }

    private void getSubDepts() {
        mPresenter.getSubDepts();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    public void showOfficeName(List<DeptBean> deptBeans) {
        if (CollectionUtils.isEmpty(deptBeans) || deptBeans.size() == 1) {
            spinnerView.setVisibility(View.GONE);
            return;
        }
        deptBeans.add(0, new DeptBean("??????", ""));
        spinnerView.setVisibility(View.VISIBLE);
        spinnerView.setData(deptBeans);
        spinnerView.setOnItemClickListerner(new SpinnerView.OnItemClickListerner() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {
                    PatientCardFragment.flag = true;
                } else {
                    PatientCardFragment.flag = false;
                }
                if (!deptBeans.get(position).getCode().equals(ConfigSingleton.getInstance().getDeptCode())) {
                    ConfigSingleton.getInstance().setDeptCode(deptBeans.get(position).getCode());
                    mPresenter.refreshAllData(true, false);
                }
            }
        });
    }

    /**
     * ???????????????????????????
     */
    @OnClick({R.id.ll_face_land})
    public void onFaceLand() {
        //???????????????????????????????????????????????????????????????????????????
        if (!ArcFaceSingleton.getInstance().getMFaceDB().getMRegister().isEmpty()) {
            onStopFaceRecognition();
            llSurfaceView.setVisibility(View.INVISIBLE);
        }
        //????????????????????????
        openFaceLandDialog();
    }

    private void openFaceLandDialog() {
        if (faceLandDialog == null) {
            faceLandDialog = new FaceLandDialog(this);
        } else {
            faceLandDialog.initView();
        }
        faceLandDialog.show();
        faceLandDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                faceLandDialog = null;
            }
        });
    }

    /**
     * ???????????????????????????
     *
     * @param v
     */
    @OnClick({R.id.ll_reserved_pat})
    public void onReservedClick(View v) {
        switch (v.getId()) {
            case R.id.ll_reserved_pat:
                reservedPatDialog = new ReservedPatDialog();
                reservedPatDialog.show(getSupportFragmentManager(), "reservedPat");
                break;
            default:
                break;
        }
    }

    /**
     * ??????????????????
     */
    @OnClick(R.id.edit_announcement)
    public void editAnnouncement() {
        InputContentDialog dialog = new InputContentDialog(this, ACache.get(ConfigSingleton.getInstance().getCurrentContext()).getAsString("CURRENT_ANNOUNCEMENT"), 100, true,
                new InputContentDialog.BtnOnClickListener() {
                    @Override
                    public void onFinishClick(String inputContent) {
                        mPresenter.saveAnnouncement(inputContent);
                        showAnnouncement(inputContent, true);
                    }

                    @Override
                    public boolean canFinish(String inputContent) {
                        return true;
                    }
                });
        dialog.setMultiLine(true);
        dialog.show();
    }

    /**
     * ????????????????????????
     */
    private void initAnnouncement() {
        String announcement = ACache.get(ConfigSingleton.getInstance().getCurrentContext()).getAsString("CURRENT_ANNOUNCEMENT");
        if (!TextUtils.isEmpty(announcement)) {
            showAnnouncement(announcement, false);
        } else {
            mPresenter.getAnnouncement();
        }
    }

    @Override
    public void showAnnouncement(String announcement, boolean isNeedCache) {
        handler.removeMessages(ParamsConstants.ANNOUNCEMENT_SHOW);
        currentAnnouncementIndex = 0;
        if (isNeedCache) {
            ACache.get(ConfigSingleton.getInstance().getCurrentContext()).put("CURRENT_ANNOUNCEMENT", announcement);
        }
        if (!TextUtils.isEmpty(announcement)) {
            announcementList = announcement.split("\\*#\\*");
            announcementView.setText(announcementList[0]);
            if (announcementList.length > 1) {
                handler.sendEmptyMessageDelayed(ParamsConstants.ANNOUNCEMENT_SHOW, 10 * 1000L);
            }
        } else {
            announcementView.setText("???????????????");
        }
    }

    private NurseAssistantMenuView nurseAssistantMenuView;

    /**
     * ???????????????????????????????????????????????????
     *
     * @param name
     */
    private void showSubContent(String name) {
        if (name.equals(MenuInfo.getInstance().getInfusionItem().getMenuName())) {
            if ("?????????????????????apk???".equals(ConfigInfo.get(ConfigInfo.INFUSION_SOURCE))) {
                webView.setVisibility(View.GONE);
                //todo ??????????????????????????????????????????QQ??????????????? ??????????????????????????????????????????????????????
                String packageName = ConfigInfo.get(ConfigInfo.THIRD_PARTY_INFUSION_INFO);
                if (TextUtils.isEmpty(packageName)) {
                    Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "?????????????????????????????????????????????", Toast.LENGTH_SHORT);
                    return;
                }
                List<PackageInfo> info = getPackageManager().getInstalledPackages(0);
                if (info == null || info.isEmpty()) {
                    Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "?????????????????????????????????", Toast.LENGTH_SHORT);
                    return;
                }
                for (int i = 0; i < info.size(); i++) {
                    if (info.get(i).packageName.equals(packageName)) {
                        MenuInfo.getInstance().showBackBtn();
                        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);//?????????????????????"com.ambition.ep"
                        startActivity(intent);
                        return;
                    }
                }
                Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "?????????????????????????????????", Toast.LENGTH_SHORT);
                return;
            } else if ("?????????????????????web???".equals(ConfigInfo.get(ConfigInfo.INFUSION_SOURCE))) {
                //todo ??????????????????????????????????????????QQ??????????????? ??????????????????????????????????????????????????????
                String url = ConfigInfo.get(ConfigInfo.THIRD_PARTY_INFUSION_INFO);
                if (TextUtils.isEmpty(url)) {
                    webView.setVisibility(View.GONE);
                    Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "????????????????????????????????????", Toast.LENGTH_SHORT);
                    return;
                }
                if (!TextUtils.isEmpty(thirdPartyInfusionUrl) && thirdPartyInfusionUrl.equals(url)) {
                    webView.setVisibility(View.VISIBLE);
                    title.setText("????????????");
                } else {
                    title.setText("????????????");
                    thirdPartyInfusionUrl = url;
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl(url);
                }
                return;
            }else if ("??????????????????????????????".equals(ConfigInfo.get(ConfigInfo.INFUSION_SOURCE))) {
                //todo ??????????????????????????????????????????QQ??????????????? ??????????????????????????????????????????????????????
                String url = ConfigInfo.get(ConfigInfo.THIRD_PARTY_INFUSION_INFO);
                if (TextUtils.isEmpty(url)) {
                    Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "??????????????????????????????????????????", Toast.LENGTH_SHORT);
                    return;
                }
            }
        }
        if (null != menuView) {
            closeDialog();
            menuView.removeListener();
            menuView = null;
        }
        webView.setVisibility(View.GONE);
        subContentView.removeAllViews();
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = fragmentList.size() - 1; i >= 0; i--) {
            Fragment fragment = fragmentList.get(i);
            if (isInWhiteList(fragment.getClass().getSimpleName())) {
                transaction.hide(fragment);
            } else {
                transaction.remove(fragment);
            }
        }
        transaction.commitNowAllowingStateLoss();
        if (name.equals(MenuInfo.getInstance().getNurseAssistant().getMenuName())) {
            if (null == nurseAssistantMenuView) {
                nurseAssistantMenuView = new NurseAssistantMenuView(this);
            } else {
                for (int i = fragmentList.size() - 1; i >= 0; i--) {
                    Fragment fragment = fragmentList.get(i);
                    if (isInWhiteList(fragment.getClass().getSimpleName())) {
                        transaction.show(fragment);
                    }
                }
                transaction.commitNowAllowingStateLoss();
                nurseAssistantMenuView.addListener();
                nurseAssistantMenuView.setSurgicalPage();
            }
            menuView = nurseAssistantMenuView;
        } else if (name.equals(MenuInfo.getInstance().getWorkArrange().getMenuName())) {
            menuView = new WorkArrangeMenuView(this);
        } else if (name.equals(MenuInfo.getInstance().getWardInfo().getMenuName())) {
            menuView = new WardInfoMenuView(this);
        } else if (name.equals(MenuInfo.getInstance().getInfusionItem().getMenuName())) {
            menuView = new InfusionMenuView(this);
        } else if (name.equals(MenuInfo.getInstance().getExchangeReport().getMenuName())) {
            menuView = new ExchangeReportMenuView(this);
        } else if (name.equals(MenuInfo.getInstance().getShowStatistics().getMenuName())) {
            menuView = new ShowDataMenuView(this);
        }
        subContentView.addView(menuView.getMenuView());
    }

    /**
     * ???????????????????????????????????????fragmentWhiteList???
     *
     * @param className
     * @return
     */
    private boolean isInWhiteList(String className) {
        for (String item : fragmentWhiteList) {
            if (className.equals(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ??????????????????
     *
     * @param status
     */
    @Override
    public void showNetWorkStatus(String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int drawableId = 0;
                switch (status) {
                    case NATIVE_NETWORK_ERROR:
                        drawableId = R.mipmap.native_network_error;
                        break;
                    case NETWORK_SUCCESS:
                        drawableId = R.mipmap.network_success;
                        break;
                    case SERVER_NETWORK_ERROR:
                        drawableId = R.mipmap.network_error;
                        break;
                    default:
                        break;
                }
                networkStatusView.setBackground(SkinManager.getInstance().getMipmap(drawableId));
            }
        });
    }

    /**
     * ????????????????????????
     */
    private void autoPlayAnnouncement() {
        announcementView.setVisibility(View.GONE);
        currentAnnouncementIndex++;
        if (currentAnnouncementIndex >= announcementList.length) {
            currentAnnouncementIndex = 0;
        }
        if (currentAnnouncementIndex < announcementList.length) {
            announcementView.setText(announcementList[currentAnnouncementIndex]);
        }
        announcementView.startAnimation(animationIn);
        announcementView.setVisibility(View.VISIBLE);
    }

    /**
     * ???????????????
     *
     * @param url
     */
    private void toWebPage(String url) {
//        List<PackageInfo> info = getPackageManager().getInstalledPackages(0);
//        if (info == null || info.isEmpty()) {
//            Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "?????????QQ?????????", Toast.LENGTH_SHORT);
//            return;
//        }
//        for (int i = 0; i < info.size(); i++) {
//            String packageName = info.get(i).packageName;
//            if (packageName.contains("com.tencent.mtt")) {
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.VIEW");
//                Uri uri = Uri.parse(url);
//                intent.setData(uri);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                intent.setClassName(packageName, packageName + ".MainActivity");
//                startActivity(intent);
//                if (null == backIntent) {
//                    backIntent = new Intent(this, BackFloatService.class);
//                }
//                startService(backIntent);
//                stopService(menuIntent);
//                return;
//            }
//        }
//        Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "?????????QQ?????????", Toast.LENGTH_SHORT);


        //???????????????????????????
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        startActivity(intent);
        if (null == backIntent) {
            backIntent = new Intent(this, BackFloatService.class);
        }
        startService(backIntent);
        stopService(menuIntent);
        return;

    }

    @Override
    public void showMqError(String errorInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_FAILED, errorInfo, Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void setAllMessageList(List<CallInfoBean> allMessageList) {
        if (null == mergedCallInfoBeanList) {
            mergedCallInfoBeanList = new CopyOnWriteArrayList<>();
        }
        mergedCallInfoBeanList.clear();
        mergedCallInfoBeanList.addAll(allMessageList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showWarnMessageNum();
                if (CollectionUtils.isEmpty(mergedCallInfoBeanList) ||
                        !"2".equals(mergedCallInfoBeanList.get(mergedCallInfoBeanList.size() - 1).getMessageType())) {
                    if (timeOutDialog != null && timeOutDialog.isShowing()) {
                        timeOutDialog.dismiss();
                    }
                } else {
                    if (timeOutDialog != null && timeOutDialog.isShowing()) {
                        //??????????????????????????????????????????
                        CallInfoBean overTimeCall = mergedCallInfoBeanList.get(mergedCallInfoBeanList.size() - 1);
                        if ("2".equals(overTimeCall.getMessageType())) {
                            List<OverTimeCallBean> overTimeCallList = new ArrayList<>();
                            if (ConfigSingleton.getInstance().getCallSource() == ConfigSingleton.CALL_FROM_QUALITY) {
                                List<CallMessage> callMessages = JSON.parseArray(overTimeCall.getMessageCodeInfo(), CallMessage.class);
                                overTimeCallList.addAll(callMessages);
                            } else {
                                List<CallInfoBean> callMessages = JSON.parseArray(overTimeCall.getMessageCodeInfo(), CallInfoBean.class);
                                overTimeCallList.addAll(callMessages);
                            }
                            timeOutDialog.setCallMessageList(overTimeCallList);
                            overTimeCall.setCallStatus(CallInfoBean.CALL_STATUS_READ);
                        }
                    }
                }
            }
        });
    }


    /**
     * ?????????????????????
     */
    private void applyTheme() {
        String theme = ConfigSingleton.getInstance().getWardTheme();
        if (TextUtils.isEmpty(theme) || ConfigSingleton.getInstance().isDefaultTheme(theme)) {
            SkinManager.getInstance().restoreDefaultTheme();
            if(isFirstMessage){
            imgLock.setImageDrawable(SkinManager.getInstance().getDrawable(R.drawable.new_unlock));
            dynamicAddSkinEnableView(imgLock,"src",R.drawable.new_unlock);
            isLock = false;
            StatusManager.getInstance().setStatus(StatusConstants.STATUS_IS_UNLOCK, true);
            }
            isFirstMessage = true;
        } else {
            //??????????????????
            prepareChangeSkin(theme);
            if(isFirstMessage) {
                imgLock.setImageDrawable(SkinManager.getInstance().getDrawable(R.drawable.new_unlock));
                dynamicAddSkinEnableView(imgLock, "src", R.drawable.new_unlock);
                isLock = false;
                StatusManager.getInstance().setStatus(StatusConstants.STATUS_IS_UNLOCK, true);
            }
            isFirstMessage = true;
        }
    }

    /**
     * ??????????????????
     * ???????????????????????????????????????????????????
     *
     * @param skinName ???????????????????????????????????????????????????????????????????????????
     */
    private void prepareChangeSkin(String skinName) {
        List<SkinBean> skins = ACacheService.getSkins();
        if (CollectionUtils.isEmpty(skins)) {
            Toaster.makeToast(this, Toaster.TOAST_FAILED, "???????????????????????????????????????", Toast.LENGTH_LONG);
            return;
        }

        Optional<SkinBean> skinModel = StreamSupport.stream(skins).filter(x -> skinName.equals(x.getSkinName())).findFirst();
        if (!skinModel.isPresent()) {
            Toaster.makeToast(this, Toaster.TOAST_FAILED, "?????????????????????????????????????????????", Toast.LENGTH_LONG);
            return;
        }

        if(TextUtils.isEmpty(skinModel.get().getDownloadUrl()) || !skinModel.get().getDownloadUrl().contains("/")){
            Toaster.makeToast(this, Toaster.TOAST_FAILED, "??????????????????????????????????????????", Toast.LENGTH_LONG);
            return;
        }

//        String skinFileFullPath = MainActivity.this.getCacheDir() + File.separator + skinModel.get().getDownloadUrl().substring(skinModel.get().getDownloadUrl().lastIndexOf("/") + 1);
        String skinFileFullPath =Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "nanaLight.skin";
        File skin = new File(skinFileFullPath);

        //??????????????????????????????????????????????????????
        if (!skin.exists()) {
            DownloadUtil.download(ConfigSingleton.getInstance().getServerUrl() + skinModel.get().getDownloadUrl(), skinFileFullPath, new DownloadListener() {
                @Override
                public void onStart() {
                    handler.post(() -> {
                        LoadingUtil.getInstance().show("?????????????????????.....");
                    });
                    Log.d(ParamsConstants.LOG_DOWNLOAD_INFO, "?????????????????????");
                }

                @Override
                public void onProgress(int progress) {
                    handler.post(() -> {
                        LoadingUtil.getInstance().show("?????????" + progress + ".....");
                    });
                    Log.d(ParamsConstants.LOG_DOWNLOAD_INFO, "????????????" + progress);
                }

                @Override
                public void onFinish(String path) {
                    handler.post(() -> {
                        LoadingUtil.getInstance().hide();
                    });
                    Log.d(ParamsConstants.LOG_DOWNLOAD_INFO, "????????????");
                    //????????????
                    applySkin(skin);
                }

                @Override
                public void onFail(String errorInfo) {
                    handler.post(() -> {
                        LoadingUtil.getInstance().hide();
                        Toaster.makeToast(MainActivity.this, Toaster.TOAST_FAILED, "?????????????????????,??????" + errorInfo, Toast.LENGTH_SHORT);
                    });

                    Log.e(ParamsConstants.LOG_DOWNLOAD_INFO, "???????????????" + skinModel.get().getSkinName() + "???????????????:" + errorInfo);

                    //??????????????????
                    if (skin.exists()) {
                        boolean deleteSuccess = skin.delete();
                        if (!deleteSuccess) {
                            Log.e(ParamsConstants.LOG_DOWNLOAD_INFO, "???????????????????????????????????????" + skinModel.get().getSkinName() + "??????????????????????????????????????????????????????????????????????????????????????????");
                        }
                    }
                }
            });
        }
        //?????????????????????????????????
        else {
            applySkin(skin);
        }


    }

    /**
     * ??????????????????????????????
     *
     * @param skin ??????????????????????????????????????????????????????????????????????????????????????????
     */
    public void applySkin(File skin) {
        //?????????????????????????????????????????????
        SkinManager.getInstance().load(skin.getAbsolutePath(),
                new ILoaderListener() {
                    @Override
                    public void onStart() {
                        Log.d(ParamsConstants.LOG_SKIN, "???????????????");
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(ParamsConstants.LOG_SKIN, "????????????");
                    }

                    @Override
                    public void onFailed() {
                        Log.e(ParamsConstants.LOG_SKIN, "?????????????????????");
                    }
                });

    }

}