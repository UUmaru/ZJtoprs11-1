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
     * 各种工具
     */

    //主线程异步处理
    public static final MainHandler handler = new MainHandler();
    private static final String BIRTHDAY_TIP = "生日提醒";
    /**
     * 设置-》特色功能-》语音识别
     */
    private static final String ASR_SWITCH = "语音识别";
    //定时器
    private Timer timer;
    //蓝牙重连定时器
    private Timer blueTimer;

    /**
     * 控件定义
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
     * 请求权限code
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
     * 人脸签到是否显示
     */
    @BindView(R.id.ll_face_land)
    RelativeLayout llFaceLand;
    /**
     * 选择科室号
     */
    @BindView(R.id.select_office)
    SpinnerView spinnerView;
    /**
     * 预入科
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
     * 主内容界面
     */
    @BindView(R.id.framelayout)
    LoginListenerView subContentView;
    @BindView(R.id.network_status)
    Button networkStatusView;


    /**
     * 各种标志
     */
    //是否第一次打开
    private boolean isFirst = true;


    /**
     * 语音消息handler
     */
    private Handler refreshHandler;

    //标记此时是否可以识别人脸
    public static boolean checkFace = true;
    private MainPresenter mPresenter;
    /**
     * 来自socket的呼叫信息
     */
    private List<CallInfoBean> callInfoBeanList = new ArrayList<>();
    /**
     * 来自质控的呼叫信息
     */
    private List<CallInfoBean> qcCallInfoBeanList = new ArrayList<>();
    /**
     * 合并质控和socket的信息（可能两者同时存在）
     */
    private CopyOnWriteArrayList<CallInfoBean> mergedCallInfoBeanList;
    private WarnMessageDialog warnMessageDialog;
    /**
     * 今天生日的患者们
     */
    private List<PatientInfo> birthPatientList;
    /**
     * 存放语音消息的队列
     */
    public static Map<String, SpeakBean> speakMap = new LinkedHashMap<>(16, 0.75f, true);
    @BindView(R.id.helpImg)
    Button helpImg;

    public static SocketConnect socketConnect;

    /**
     * 开机动画播放完成
     */
    public static boolean isStartAnimEnd = false;

    /**
     * 网络是否连接成功
     */
    public static boolean isConnect = false;

    /**
     * 网络连接失败
     */
    public static boolean isConnectError = false;

    public boolean isFirstMessage = false;

    /**
     * 是不是第一次登陆
     */
    public static boolean isOnce = false;

    /**
     * 是否是切换科室
     */
    public static boolean isChangeWard = false;
    /**
     * 预入科患者信息框
     */
    private ReservedPatDialog reservedPatDialog;
    private int pointerId0, pointerId1, pointerId2, pointerId3;
    private float oldXPoint0, oldXPoint1, oldXPoint2, oldXPoint3;
    private float oldYPoint0, oldYPoint1, oldYPoint2, oldYPoint3;
    /**
     * 几个手指在屏幕上面
     */
    private int mPointCount = -1;

    /**
     * 二次认证弹框(要写成全局，Panel在刷新的时候会重新设置，写成局部会导致弹框对象和实体对不上)
     */
    public static SecondConfirmView secondConfirmView;
    ;
    /**
     * 五指抓取后的各个子页面
     */
    private BaseMenuView menuView;
    /**
     * 点击悬浮窗后弹出的菜单窗口
     */
    private MenuItemDialog menuItemDialog;
    /**
     * 计时器
     */
    protected Runnable timerRunnable;
    /**
     * 人脸识别是否成功
     */
    private boolean isFaceSuccess = false;
    /**
     * 人脸签到弹框
     */
    private FaceLandDialog faceLandDialog;
    /**
     * 护理助手页面中可显示的fragment对应类名的数组
     */
    private String[] fragmentWhiteList = {PatientCardFragment.class.getSimpleName(),
            DynamicFragment.class.getSimpleName(), TaskFragment.class.getSimpleName(),
            ReservedPatFragment.class.getSimpleName(), SurgicalFragment.class.getSimpleName(),
            GeneralSignsFragment.class.getSimpleName(), InspectionArrangeFragment.class.getSimpleName()};

    /**
     * 切换病区时候智能建议是否弹框
     */
    public static boolean aiDialogShow = false;

    // 保留一个实例，避免重复创建线程
    private UpdateManager manager = null;

    private String[] announcementList = new String[]{};
    private int currentAnnouncementIndex = 0;
    private Animation animationIn;

    private Intent backIntent;
    private Intent menuIntent;
    /**
     * 呼叫超时显示框
     */
    private TimeOutDialog timeOutDialog;

    /**
     * 语音识别结果提示面板
     */
    @BindView(R.id.rlAsrTip)
    RelativeLayout rlAsrTip;

    /**
     * 语音识别提示图标
     */
    @BindView(R.id.ivAsrMonitor)
    ImageView ivAsrMonitor;

    /**
     * 语音识别提示文字
     */
    @BindView(R.id.tvAsrResult)
    TextView tvAsrResult;

    /**
     * 输液监控
     */
    @BindView(R.id.web_view)
    X5WebView webView;

    /**
     * 患者弹窗
     */
    private PatientInfoDialog patientInfoDialog;

    /**
     * 振动器服务
     */
    private Vibrator mVibrator;

    /**
     * 语音识别的简要回调
     */
    private IYzsAsrCallback yzsAsrCallback = new IYzsAsrCallback() {
        @Override
        public void processAsrResult(String result) {
            Log.d(ParamsConstants.LOG, "MainActivity->YzsAsrUtils.IYzsAsrCallback->processAsrResult:" + result);
            showAsrProgressResult(result, false);
            if (patientInfoDialog != null && patientInfoDialog.isShowing()) {
                patientInfoDialog.dismiss();
            }
            //刷新数据
            if (DirectConstants.intent4RefreshData(result)) {
                mPresenter.refreshAllData(true, true);
            }
            //用户帮助页面
            else if (DirectConstants.intent4OpenHelpView(result)) {
                mPresenter.showHelpDialog();
                YzsTtsUtils.getInstance().play("已打开用户帮助");
            }
            //打开系统设置页面
            else if (DirectConstants.intent4OpenSystemView(result)) {
                mPresenter.showSettingDialogByDirect();
                YzsTtsUtils.getInstance().play("已打开系统设置");
            }
            //智能建议
            else if (DirectConstants.intent4OpenAdviceDialog(result)) {
                mPresenter.showAIAdviceDialog();
            }
            //输液监控菜单页面-》输液监测设置页面
            else if (DirectConstants.intent4OpenInfusionSettingView(result)) {
                if (!(menuView instanceof InfusionMenuView)) {
                    showSubContent(MenuInfo.getInstance().getInfusionItem().getMenuName());
                }
                InfusionMenuView menu = (InfusionMenuView) menuView;
                menu.infusionSetting.performClick();
                YzsTtsUtils.getInstance().playImmediately("已打开输液监测设置");
            }
            //输液监控菜单页面-》输液监测页面
            else if (DirectConstants.intent4OpenInfusionView(result)) {
                if (!(menuView instanceof InfusionMenuView)) {
                    showSubContent(MenuInfo.getInstance().getInfusionItem().getMenuName());
                }
                InfusionMenuView menu = (InfusionMenuView) menuView;
                menu.infusionInfo.performClick();
                YzsTtsUtils.getInstance().playImmediately("已打开输液监测");
            }
            //输液监控菜单页面
            else if (DirectConstants.intent4OpenInfusionMenu(result)) {
                if (menuView instanceof InfusionMenuView) {
                    YzsTtsUtils.getInstance().playImmediately("已在输液监测页面");
                    return;
                }
                showSubContent(MenuInfo.getInstance().getInfusionItem().getMenuName());
                YzsTtsUtils.getInstance().play("已打开输液监测页面");
            }
            //护理助手菜单页面
            else if (DirectConstants.intent4OpenNurseAssistantMenu(result)) {
                if (menuView instanceof NurseAssistantMenuView) {
                    YzsTtsUtils.getInstance().playImmediately("已在护理助页面");
                    return;
                }
                showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                YzsTtsUtils.getInstance().play("已打开护理助");
            }
            //综合信息菜单页面->护理资料
            else if (DirectConstants.intent4OpenNurseFileView(result)) {
                if (!(menuView instanceof WardInfoMenuView)) {
                    showSubContent(MenuInfo.getInstance().getWardInfo().getMenuName());
                }
                WardInfoMenuView menu = (WardInfoMenuView) menuView;
                menu.llNurseFile.performClick();
                YzsTtsUtils.getInstance().play("已打开护理资料");
            }
            //综合信息菜单页面->综合信息
            else if (DirectConstants.intent4OpenWardinfoView(result)) {
                if (!(menuView instanceof WardInfoMenuView)) {
                    showSubContent(MenuInfo.getInstance().getWardInfo().getMenuName());
                }
                WardInfoMenuView menu = (WardInfoMenuView) menuView;
                menu.wardInfoBtn.performClick();
                YzsTtsUtils.getInstance().play("已打开综合信息");
            }
            //综合信息菜单页面
            else if (DirectConstants.intent4OpenWardinfoMenu(result)) {
                if (menuView instanceof WardInfoMenuView) {
                    YzsTtsUtils.getInstance().playImmediately("已在综合信息页面");
                    return;
                }
                showSubContent(MenuInfo.getInstance().getWardInfo().getMenuName());
                YzsTtsUtils.getInstance().play("已打开综合信息");
            }
            //交班报告菜单页面
            else if (DirectConstants.intent4OpenExchangeReportMenu(result)) {
                if (menuView instanceof ExchangeReportMenuView) {
                    YzsTtsUtils.getInstance().playImmediately("已在交班报告页面");
                    return;
                }
                showSubContent(MenuInfo.getInstance().getExchangeReport().getMenuName());
                YzsTtsUtils.getInstance().play("已打开交班报告");
            }
            //数据中心菜单页面-》数据统计
            else if (DirectConstants.intent4OpenShowStatisticsView(result)) {
                if (!(menuView instanceof ShowDataMenuView)) {
                    showSubContent(MenuInfo.getInstance().getShowStatistics().getMenuName());
                }
                ShowDataMenuView menu = (ShowDataMenuView) menuView;
                menu.llStatistics.performClick();
                YzsTtsUtils.getInstance().play("已打开数据统计");
            }
            //数据中心菜单页面-》医嘱查询
            else if (DirectConstants.intent4OpenShowOrderInfoView(result)) {
                if (!(menuView instanceof ShowDataMenuView)) {
                    showSubContent(MenuInfo.getInstance().getShowStatistics().getMenuName());
                }
                ShowDataMenuView menu = (ShowDataMenuView) menuView;
                menu.llOrderInfo.performClick();
                YzsTtsUtils.getInstance().play("已打开医嘱查询");
            }
            //数据中心菜单页面
            else if (DirectConstants.intent4OpenShowStatisticsMenu(result)) {
                if (menuView instanceof ShowDataMenuView) {
                    YzsTtsUtils.getInstance().playImmediately("已在数据中心页面");
                    return;
                }
                showSubContent(MenuInfo.getInstance().getShowStatistics().getMenuName());
                YzsTtsUtils.getInstance().play("已打开数据中心");
            }
            //工作安排菜单页面->护士排班
            else if (DirectConstants.intent4OpenNurseScheduleView(result)) {
                if (!(menuView instanceof WorkArrangeMenuView)) {
                    showSubContent(MenuInfo.getInstance().getWorkArrange().getMenuName());
                }
                WorkArrangeMenuView menu = (WorkArrangeMenuView) menuView;
                menu.workPlanBtn.performClick();
                YzsTtsUtils.getInstance().play("已打开护士排班");
            }
            //工作安排菜单页面->责任护士分管床位
            else if (DirectConstants.intent4OpenNurseAssignView(result)) {
                if (!(menuView instanceof WorkArrangeMenuView)) {
                    showSubContent(MenuInfo.getInstance().getWorkArrange().getMenuName());
                }
                WorkArrangeMenuView menu = (WorkArrangeMenuView) menuView;
                menu.bedArrangeBtn.performClick();
                YzsTtsUtils.getInstance().play("已打开责任护士分管床位");
            }
            //工作安排菜单页面
            else if (DirectConstants.intent4OpenWorkArrangeMenu(result)) {
                if (menuView instanceof WorkArrangeMenuView) {
                    YzsTtsUtils.getInstance().playImmediately("已在工作安排页面");
                    return;
                }
                showSubContent(MenuInfo.getInstance().getWorkArrange().getMenuName());
                YzsTtsUtils.getInstance().play("已打开工作安排");
            }
            //打开患者概览
            else if (DirectConstants.intent4OpenPatientView(result)) {
                //判断当前科室护理助手页面是否有患者概览页面
                if (!nurseAssistantMenuView.isPatientCardFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("当前科室无患者概览页面");
                    return;
                }

                //判断当前是否打开的是护理助手页面,若不是则先切换到护理助手页面
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }

                //判断当前展示的是否是患者概览碎片，
                if (nurseAssistantMenuView.isShowPatientCardFragment()) {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.llCard);
                    YzsTtsUtils.getInstance().play("已打开患者概览");
                }
            }
            //打开护理任务
            else if (DirectConstants.intent4OpenTaskView(result)) {
                //判断当前科室护理助手页面是否有护理任务页面
                if (!nurseAssistantMenuView.isTaskFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("当前科室无护理任务页面");
                    return;
                }

                //判断当前是否打开的是护理助手页面,若不是则先切换到护理助手页面
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }

                //判断当前展示的是否是护理任务碎片，
                if (nurseAssistantMenuView.isShowTaskView()) {
                    YzsTtsUtils.getInstance().playImmediately("已在护理任务页面");
                }
                //当前展示的不是护理任务碎片，切换碎片
                else {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.mLLTask);
                    YzsTtsUtils.getInstance().playImmediately("已打开护理任务");
                }
            }
            //打开病区动态
            else if (DirectConstants.intent4OpenDynamicView(result)) {
                //判断当前科室护理助手页面是否有病区动态页面
                if (!nurseAssistantMenuView.isDynamicFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("当前科室无病区动态页面");
                    return;
                }

                //判断当前是否打开的是护理助手页面,若不是则先切换到护理助手页面
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }
                //判断当前展示的是否是病区动态碎片，
                if (nurseAssistantMenuView.isShowDynamicView()) {
                    YzsTtsUtils.getInstance().playImmediately("已在病区动态页面");
                }
                //当前展示的不是病区动态碎片，切换碎片
                else {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.llState);
                    YzsTtsUtils.getInstance().playImmediately("已打开病区动态");
                }
            }
            //手术安排
            else if (DirectConstants.intent4OpenSurgical(result)) {
                //判断当前科室护理助手页面是否有手术安排页面
                if (!nurseAssistantMenuView.isSurgicalFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("当前科室无手术安排");
                    return;
                }

                //判断当前是否打开的是护理助手页面,若不是则先切换到护理助手页面
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }
                //判断当前展示的是否是手术安排碎片，
                if (nurseAssistantMenuView.isShowSurgicalView()) {
                    YzsTtsUtils.getInstance().playImmediately("已在手术安排页面");
                }
                //当前展示的不是手术安排碎片，切换碎片
                else {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.mLlSurgical);
                    YzsTtsUtils.getInstance().playImmediately("已打开手术安排");
                }
            }
            //全科体征
            else if (DirectConstants.intent4OpenGeneralSigns(result)) {
                //判断当前科室护理助手页面是否有全科体征页面
                if (!nurseAssistantMenuView.isGeneralSignsFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("当前科室无全科体征页面");
                    return;
                }

                //判断当前是否打开的是护理助手页面,若不是则先切换到护理助手页面
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }
                //判断当前展示的是否是全科体征碎片，
                if (nurseAssistantMenuView.isShowGeneralSignsView()) {
                    YzsTtsUtils.getInstance().playImmediately("已在全科体征页面");
                }
                //当前展示的不是全科体征碎片，切换碎片
                else {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.llGeneralSigns);
                    YzsTtsUtils.getInstance().play("已打开全科体征");
                }
            }
            //检查安排
            else if (DirectConstants.intent4OpenInspectionArrangement(result)) {
                //判断当前科室护理助手页面是否有检查安排页面
                if (!nurseAssistantMenuView.isInspectionArrangementFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("当前科室无检查安排页面");
                    return;
                }

                //判断当前是否打开的是护理助手页面,若不是则先切换到护理助手页面
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }
                //判断当前展示的是否是检查安排碎片，
                if (nurseAssistantMenuView.isShowInspectionArrangementView()) {
                    YzsTtsUtils.getInstance().playImmediately("已在检查安排页面");
                }
                //当前展示的不是检查安排碎片，切换碎片
                else {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.llInspectionArrange);
                    YzsTtsUtils.getInstance().playImmediately("已打开检查安排");
                }
            }
            //意图打开患者概览的标签页
            else if (DirectConstants.intent4ChangePatientViewTab(result)) {
                //判断当前科室护理助手页面是否有患者概览页面
                if (!nurseAssistantMenuView.isPatientCardFragmentVisible()) {
                    YzsTtsUtils.getInstance().playImmediately("当前科室无患者概览页面");
                    return;
                }
                //获取标签页的原始名称,此返回值必定有值，因为已经调用过intent4ChangePatientViewTab方法判断
                String originTabName = DirectConstants.getChangePatientViewTabOriginName(result);
                //判断该标签页是否可见,注意，这肯定是个标签页
                if (!nurseAssistantMenuView.isPatientCardATabAndTabVisiable(originTabName)) {
                    YzsTtsUtils.getInstance().playImmediately("当前科室患者概览页面无此过滤器");
                    return;
                }

                //判断当前是否打开的是护理助手页面,若不是则先切换到护理助手页面
                if (menuView != nurseAssistantMenuView) {
                    showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
                }

                //判断当前展示的是否是患者概览碎片，不是则切换到患者概览页面
                if (!nurseAssistantMenuView.isShowPatientCardFragment()) {
                    nurseAssistantMenuView.menuClickT(nurseAssistantMenuView.llCard);
                }

                //切换到指定标签页
                nurseAssistantMenuView.changePatientCardTab(originTabName);
                YzsTtsUtils.getInstance().playImmediately("已打开" + originTabName + "患者列表");
            }
            //意图为打开患者弹窗,目前仅“15床患者”或者"陈林患者"
            else if (DirectConstants.intent4OpenPatientDialog(result)) {

                List<PatientInfo> patientInfoList = ACacheService.getPatients();
                if (CollectionUtils.isEmpty(patientInfoList)) {
                    YzsTtsUtils.getInstance().playImmediately("暂无该患者信息");
                    return;
                }

                //在缓存中寻找该患者的信息
                PatientInfo patientInfo = null;
                for (int k = 0; k < patientInfoList.size(); k++) {
                    PatientInfo patientInfo1 = patientInfoList.get(k);
                    if (patientInfo1 != null) {
                        String patName = patientInfo1.getPatientName() + "患者";
                        String bedNo = "床患者";
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
                    if (result.endsWith("床患者")) {
                        YzsTtsUtils.getInstance().playImmediately("暂无该床信息");
                    } else {
                        YzsTtsUtils.getInstance().playImmediately("暂无该患者信息");
                    }
                    return;
                }

                //空床
                if (TextUtils.isEmpty(patientInfo.getPatientName())) {
                    YzsTtsUtils.getInstance().playImmediately("该床暂无患者");
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
                YzsTtsUtils.getInstance().playImmediately("对不起，娜娜暂不支持这个语音操作~");
            }
        }

        @Override
        public void onWeakUpSuccess() {
            //唤醒成功后，解锁屏幕
            if (isLock) {
                onPinLoginSuccess();
            }

            //判断设备是否有振动器
            if (mVibrator.hasVibrator()) {
                //控制手机震动0.3秒
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
            //延时关闭语音识别结果面板
            MainActivity.handler.sendEmptyMessageDelayed(ParamsConstants.MSG_CLOSE_ASR_VIEW, 3000);
//            closeAsrTipView();
        }
    };


    /**
     * 展示语音识别的渐进结果
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
     * 关闭语音识别的提示面板
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
        //获取屏幕适配项
        String Path = FileUtils.path + "/" + FileUtils.filename + ".xml";
        String value_dpi= FileUtils.readTxt(Path);
        String value="0";
        //屏幕适配
        if(value_dpi!=null && value_dpi!="" && !"0".equals(value_dpi)){
            ScreenadapterUtils.setCustomDensity(this, getApplication(), Integer.parseInt(value_dpi));
        }
        initStartView();

        //需要权限后再初始化云知音引擎
        //SpeechCenter.getInstance().init(MainActivity.this);
//        SpeechUtilOffline.getInstance();

        getWindow().setBackgroundDrawable(null);
        /**初始化语音handler*/
        refreshHandler = new Handler();
        /**隔一秒监测语音队列中是否有语音*/
        refreshHandler.post(refreshTask);
        /**根据设备初始化*/
        SoundManager.getInstance();
        SoundManager.initSounds(getBaseContext());
        /**加载语音文件*/
        SoundManager.loadSounds();
        LoadingUtil.getInstance().init(MainActivity.this);
        ExitApplication.getInstance().addActivity(this);
        animationIn = AnimationUtils.loadAnimation(this, R.anim.animation_slip_in);
        menuIntent = new Intent(this, MenuFloatService.class);

        //获取震动服务
        mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }

    /**
     * emq服务器地址（设置项中的呼叫地址）测试
     */
    private void initSocketConnet() {
        String url = ConfigInfo.get(ConfigInfo.CALL_SERVER_URL);
        L.writeToFile('s', "call url:", url);
        if (TextUtils.isEmpty(url)) {
            disconnect();
            Log.e(TAG, "Socket 地址为空");
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
                L.writeToFile('s', "connect", "socket连接成功");
                Toaster.makeToast(getApplicationContext(), Toaster.TOAST_SUCCESS, "连接成功", Toast.LENGTH_LONG);
                if (socketConnect != null) {
                    socketConnect.send(SocketMessageUtil.getInstance().transformString(ConfigSingleton.getInstance().getCurrentWardCode()));
                }
            }

            @Override
            public void onDisconnected() {
                L.writeToFile('s', "connect", "socket连接断开");
                Toaster.makeToast(getApplicationContext(), Toaster.TOAST_FAILED, "连接已断开", Toast.LENGTH_LONG);
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
                        Log.e(TAG, "数据格式错误，bytes：" + bytes);
                        L.writeToFile('s', "亚华呼叫", "数据格式错误，bytes：" + bytes);
                        Log.e(TAG, "数据格式错误，string：" + fullMsg);
                        L.writeToFile('s', "亚华呼叫", "数据格式错误，string：" + fullMsg);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Xml2Obj error", e);
                }
            }


            @Override
            public void onError(String msg) {
                L.writeToFile('s', "connect", "socket连接失败,msg:" + msg);
                Toaster.makeToast(getApplicationContext(), Toaster.TOAST_FAILED, msg, Toast.LENGTH_LONG);
                if ("连接失败，请断开连接后重新尝试！".equals(msg)) {
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
     * 定时器刷新 1s，语音呼叫
     */
    Runnable refreshTask = new Runnable() {
        @Override
        public void run() {
            refreshHandler.postDelayed(refreshTask, 1000);
            ttsCall();
        }
    };

    /**
     * 语音调用,同一时间只允许一个线程访问
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
                //利用linkedhashmap的特性，每次访问后放到链表尾部
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
     * 语音播放
     */
    private void computeTtsString(String codeResult) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        StringBuilder sb = new StringBuilder();
        //Android 8.0的语音播报适配
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
                //展示悬浮窗
                startService(menuIntent);
            }
        } else {
            startService(menuIntent);
        }
        ConfigSingleton.getInstance().setCurrentContext(this);
        //查询配置信息，每次resume的时候查询
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
            //护理任务模块显示的时候从去获取智能建议的数据
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
        LoadingUtil.getInstance().show("数据刷新中...");
        refreshing();
    }

    @Override
    public void onConfigReceived() {
        //ble_nfc服务初始化
//        if (llLock.getVisibility() == View.VISIBLE) {
//            waitForBlueTooth();
//        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                L.writeToFile('s', "CallSource", ConfigSingleton.getInstance().getCallSource() + "");
                //配置项变化时先退出原先的呼叫接收通道，并清除原有呼叫数据
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
                //初始化呼叫路径
                if (ConfigSingleton.CALL_FROM_SOCKET == ConfigSingleton.getInstance().getCallSource() && socketConnect == null) {
                    initSocketConnet();
                } else if (ConfigSingleton.CALL_FROM_RABBITMQ == ConfigSingleton.getInstance().getCallSource()) {
                    mPresenter.initRabbitmq();
                } else if (ConfigSingleton.CALL_FROM_EMQ == ConfigSingleton.getInstance().getCallSource()) {
                    mPresenter.initEmq();
                } else if(!TextUtils.isEmpty(ConfigSingleton.getInstance().getInfusionUrl())
                        && "第三方输液数据对接版".equals(ConfigInfo.get(ConfigInfo.INFUSION_SOURCE))){
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

        if (ConfigInfo.get(ConfigInfo.SPEC_FUNCTION).contains("公告信息展示")) {
            announcementInfo.setVisibility(View.VISIBLE);
            initAnnouncement();
        } else {
            handler.removeMessages(ParamsConstants.ANNOUNCEMENT_SHOW);
            currentAnnouncementIndex = 0;
            announcementInfo.setVisibility(View.GONE);
            ACache.get(ConfigSingleton.getInstance().getCurrentContext()).put("CURRENT_ANNOUNCEMENT", "");
            announcementView.setText("请输入信息");
        }
        //判断特色功能下的语音识别功能是否开启
        if (ConfigInfo.get(ConfigInfo.SPEC_FUNCTION).contains(ASR_SWITCH)) {
            //确认开启
            YzsAsrUtils.getInstance().init(this, yzsAsrCallback);
        } else {
            //关闭语音识别
            YzsAsrUtils.getInstance().release();
        }

        //排换床是否显示
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
        //创建与服务端的webSocket连接
        mPresenter.connectServerWebSocket();
    }


    @Override
    public void onConfigUnReceived() {
        LoadingUtil.getInstance().hide();
        afterRefresh();
        Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_FAILED, "配置信息请求失败", Toast.LENGTH_SHORT);
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
        Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_UNLOCK, "屏幕已解锁", Toast.LENGTH_SHORT);
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
     * 初始化主页面
     */
    private void initStartView() {
        //初始化handler等
        initValues();
        subContentView.setLoginListener(new LoginListenerView.LoginListener() {
            @Override
            public void showLoginView() {
                unLock(null);
            }
        });
        //初始化刷新按钮旋转
        initRefreshTurnAround();
        //检查网络配置
        mPresenter.checkNet();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                mPresenter.stopFaceRecognition();
                //10秒后人脸识别是否成功，不成功则打开个人登录页面
                if (!isFaceSuccess) {
                    tvName.setText("");
                    Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "人脸识别过久，请用账号登录", Toast.LENGTH_SHORT);
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
        //判断开机动画是否播放完成
        if (isStartAnimEnd) {
            Intent intent = new Intent(ParamsConstants.FINSH_SPLASH_ANIM);
            sendBroadcast(intent);
        }
        tvHospitalName.setText(ConfigSingleton.getInstance().getHospitalName());
        tvDeptName.setText(ConfigSingleton.getInstance().getCurrentWardName());

        //初始化控件时间显示
        initialTvTime();
        //初始化Timer
        mPresenter.startScheduleTask();
        showSubContent(MenuInfo.getInstance().getNurseAssistant().getMenuName());
        checkUpdate();
        mPresenter.refreshAllData(false, true);
        if (isFirst) {
            isFirst = false;
        }
        refreshCallInfoBean();
        ACache.get(ConfigSingleton.getInstance().getCurrentContext()).put("CURRENT_ANNOUNCEMENT", "");
        announcementView.setText("请输入信息");
    }

    /**
     * 患者信息发生了变化时，需要修改输液消息里的相关信息？？
     * 刷新呼叫消息提醒框的数据
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
     * 点击刷新按钮会触发这个方法
     */
    public void initData(View view) {
        mPresenter.refreshAllData(true, false);
        //InfusionSettingFragment没有doAction一直不能刷新，所以加了这个方法
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
            //清除缓存的过生日的病人
            ACacheService.putList("GET_IGNORE_BIRTHDAY", new ArrayList<>());
            //清除生日提醒消息
            clearBirthMessage();
            showBirthdayDialog();
        }
    }

    /**
     * 新的一天清除生日提醒消息
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
        // 根据配置项设置下面的手术安排按钮
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

    //初始化旋转刷新按钮
    Animation refreshAnimation;

    private void initRefreshTurnAround() {
        //动画
        refreshAnimation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        refreshAnimation.setInterpolator(lin);

    }

    /**
     * 刷新时
     */
    private void refreshing() {
        animRefreshImg.setVisibility(View.VISIBLE);
        animRefreshImg.startAnimation(refreshAnimation);
        refreshButton.setVisibility(View.GONE);
    }

    /**
     * 刷新后
     */
    private void afterRefresh() {
        animRefreshImg.setVisibility(View.GONE);
        animRefreshImg.clearAnimation();
        refreshButton.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化权限检查
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
            //云之声新增权限
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

    //用于记录科室点击次数
    final static int COUNTS = 5;// 点击次数
    final static long DURATION = 1000;// 规定有效时间
    long[] mHits = new long[COUNTS];

    /*
     * 初始化handler等
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initValues() {
        NavigationBarUtil.hideNavigationBar(this.getWindow());
        setContentView(R.layout.activity_main);
        //初始化ButterKnife
        ButterKnife.bind(this);
        ConfigSingleton.getInstance().setCurrentContext(this);
        initPresenter();
        //初始化字体图标库
        initMenuAnimation();
        //为智能提醒按钮设置触摸事件
        llRemind.setOnTouchListener(new RemindBtnOnTouchListener());
        getSharedValues();
        loadingLayout.setVisibility(View.VISIBLE);
        isConnect = false;
        ProxyHelper.getInstant().start(this);
        Log.i("MainActivity", "开机动画");
        checkPermission();
        handler.setMainActivity(this);
        ConfigSingleton.getInstance().setPublicHandler(new PublicHandler());
        //初始化设置字段
        ConfigInfo.initConfigMap(ACacheService.getConfigList());

        //长按LogX，打开设置界面
        tvDeptName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //每次点击时，数组向前移动一位
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                //为数组最后一位赋值
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    mHits = new long[COUNTS];//重新初始化数组
//                    Toast.makeText(this, "连续点击了5次", Toast.LENGTH_LONG).show();
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
     * 获取缓存记录
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
     * 权限检查
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            boolean isAllGrant = true;
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    Toaster.makeToast(this, Toaster.TOAST_REMIND, String.format("权限不足，请设置允许：%s", Arrays.toString(permissions)), Toast.LENGTH_SHORT);
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
     * 初始化字体图标库
     */
    private void initMenuAnimation() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        refreshButton.setTypeface(font);
        animRefreshImg.setTypeface(font);
//        (menuLLs.get(0)).setBackgroundResource(R.color.list_selected);
    }

    /*
     * 初始化顶部栏控件时间和刷新时间
     */
    private void initialTvTime() {
        dateView.setText(DateUtil.getTimeString(ConfigSingleton.getInstance().getCurrentTime(), "MM月dd日 EEEE"));
        tvTime.setText(DateUtil.getTimeString(ConfigSingleton.getInstance().getCurrentTime(), "HH:mm"));
        tvUpdateTime.setText("刚刚更新");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        /*if (isLock) {
            // 锁屏不处理手势
            return super.dispatchTouchEvent(ev);
        }*/
        switch (ev.getAction() & ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                StatusManager.getInstance().setStatus(StatusConstants.STATUS_TOUCH, true);
                pointerId0 = -1;
                pointerId1 = -1;
                pointerId2 = -1;
                pointerId3 = -1;
                // 第1个手指
                pointerId0 = ev.getPointerId(0);
                oldXPoint0 = ev.getX(0);
                oldYPoint0 = ev.getY(0);
                mPointCount = 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i(TAG, "dispatchTouchEvent: 几点触控：" + ev.getPointerCount());
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

                        Log.i(TAG, "dispatchTouchEvent: " + ev.getPointerCount() + "指向下滑动");
//                        tvDeptName.setText("当前下滑");
                    } else if (ev.getPointerCount() > 3 && offsetY0 < -200 && offsetY1 < -200 && offsetY2 < -200 && offsetY3 < -200) {

                        Log.i(TAG, "dispatchTouchEvent: " + ev.getPointerCount() + "指向上滑动");
//                        tvDeptName.setText("当前上滑");
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
                        Log.i(TAG, "dispatchTouchEvent: " + ev.getPointerCount() + "捏合");
//                        tvDeptName.setText("当前捏合");
                    }
                }
                break;
            //获取触摸动作，如果ACTION_UP，计时开始。
            case MotionEvent.ACTION_UP:
                StatusManager.getInstance().setStatus(StatusConstants.STATUS_TOUCH, false);
                Log.i(TAG, "dispatchTouchEvent: " + ev.getPointerCount() + "测试五指up");
                if (mPointCount == 5) {
                    //弹出菜单切换页面
                    menuItemDialog = new MenuItemDialog(this);
                    menuItemDialog.show();
                }
                break;
            //否则其他动作计时取消
            default:
                StatusManager.getInstance().setStatus(StatusConstants.STATUS_TOUCH, true);
                break;
        }
        Log.i(TAG, "dispatchTouchEvent: " + ev.getPointerCount() + "测试五指");
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

        //若存在则关闭第三方webSocket连接
        mPresenter.disconnectThirdPartyWebSocket();

        //断开与NaNa服务端的WebSocket连接
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
        //ZHBFDEVIMP-166 Build.VERSION.SDK_INT=23时测试失败，加了后面的代码
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
                Toaster.makeToast(MainActivity.this, Toaster.TOAST_SUCCESS, "网络连接成功", Toast.LENGTH_SHORT);
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
                ParamsConstants.DEPARTMENT_PIN_UNLOCK, null, null, null, "医生",
                null, null, null, "-1"));
        buttonMagic.setVisibility(View.GONE);
        Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_LOCK, "屏幕已锁定", Toast.LENGTH_SHORT);
        if (ConfigSingleton.getInstance().getIsMagic()) {
            magicWay(false);
        } else {
            if (ConfigSingleton.getInstance().getCurrentFragment() instanceof PatientCardFragment) {
                loadingLayout.setVisibility(View.GONE);
            }
        }
        //锁屏后，退出责护待办回到护理界面
        Intent intent = new Intent(ParamsConstants.CHANGE_TASK_VIEW);
        sendBroadcast(intent);
    }

    /**
     * 更新检测
     **/
    @Override
    public void checkUpdate() {
        String url = ConfigSingleton.getInstance().getServerUrl();
        url = url + "apk/";
        if (manager == null) {
            manager = new UpdateManager(this, url);
        }
        // 检查软件更新
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

    //关闭所有弹框
    private void closeDialog() {
        StatusManager.getInstance().setStatus(StatusConstants.STATUS_CLOSE_ALL_DIALOG, 1);
        AutoCloseDialogManager.getInstance().clean();
    }


    private void afterLoginByUser() {
        subContentView.setIntercept(false);
        Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_UNLOCK, "屏幕已解锁", Toast.LENGTH_SHORT);
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
        tvName.setText(String.format("你好,  %s", ConfigSingleton.getInstance().getCurrentNurseModel().getUserName()));
        tvName.setClickable(false);
        if (ConfigSingleton.getInstance().isLoginVoiceOn())
            computeTtsString(String.format("你好,  %s", ConfigSingleton.getInstance().getCurrentNurseModel().getUserName()));
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
        //回写后台次数加一
        mPresenter.addLoginCount();
//        ConfigSingleton.getInstance().getCurrentFragment().refreshView();
    }

    @SuppressWarnings("unchecked")
    private void initMyPatientList() {
        List<PatientInfo> myPatientsList = new ArrayList<>();
        //所有护士关注的患者
        List<MyPatientsModel> allMyPatientList1 = ACacheService.getAllMyPatientList();
        for (int i = 0; i < allMyPatientList1.size(); i++) {
            if (allMyPatientList1.get(i).getNurseId().equals(ConfigSingleton.getInstance().getCurrentNurseModel().getUserId())) {
                //当前护士关注的患者
                myPatientsList = allMyPatientList1.get(i).getPatients();
                break;
            }
        }
        //所有患者
        List<PatientInfo> patientList = ACacheService.getPatients();
        for (int i = 0; i < myPatientsList.size(); i++) {
            if (myPatientsList.get(i) != null) {
                for (int j = 0; j < patientList.size(); j++) {
                    if (patientList.get(j).getPatientId() != null && patientList.get(j).getPatientId() != "" && patientList.get(j).getPatientId().equals(myPatientsList.get(i).getPatientId())) {
                        myPatientsList.set(i, patientList.get(j));
                        break;
                        //任务ZHBFDEVIMP-165修改
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
        if (!ConfigSingleton.getInstance().getLoginType().contains("人脸")) {
            return;
        }
        if (!isLock) {
            return;
        }
        startFaceRecognition(false, 0);
    }

    public void startFaceRecognition(boolean isFromOper, int sec) {
        if (isFromOper) {
            //未传此参数的话，默认10s关闭人脸识别
            if (sec == 0) {
                sec = 10;
            }
            handler.postDelayed(timerRunnable, sec * 1000);
        }
        if (!CameraUtil.hasCamera()) {
            Log.e("main", "没有检测到摄像头，无法使用人脸功能");
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
            Log.e(this.getClass().getName(), "没有注册人脸，请先注册！");
            if (!isFromOper) {
                tvName.post(() -> {
                    tvName.setText("该病区暂未录入人脸");
                    tvName.setOnClickListener(v -> unLock(v));
                });
            }
        }
    }

    @Override
    public void onStopFaceRecognition() {
        if (!CameraUtil.hasCamera()) {
            Log.e("onStopFaceRecognition", "没有检测到摄像头，无法使用人脸功能");
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
     * 生成呼叫提醒的语音文本并置入队列
     */
    @Override
    public void generateSpeakText() {
        speakMap.clear();
        for (CallInfoBean callInfoBean : mergedCallInfoBeanList) {
            if (callInfoBean.getCallStatus() == CallInfoBean.CALL_STATUS_NOT_READ) {
                if (callInfoBean.isCallMessage() && !ConfigInfo.get(ConfigInfo.SOUND_CONTROL).contains("呼叫")) {
                    continue;
                }
                if (callInfoBean.isInfusionMessage() && !ConfigInfo.get(ConfigInfo.SOUND_CONTROL).contains("输液")) {
                    continue;
                }
                if ("1".equals(callInfoBean.getMessageType()) || "2".equals(callInfoBean.getMessageType()))
                    continue;
                String speakText;
                //来组于服务端的第三方呼叫信息
                if (callInfoBean.isFromServer()) {
                    speakText = callInfoBean.getServerWebSocketMsgBean().getContent();
                }
                //非病房的呼叫信息
                else if (!callInfoBean.isRoomCall()) {
                    if (callInfoBean.isInfusionMessage()) {
                        speakText = callInfoBean.getMessageCodeInfo().replaceAll("\n", "");
                    } else {
                        speakText = callInfoBean.getBedLabel().replaceAll("床", "") + "床" + callInfoBean.getCallEventName();
                    }
                }
                //病房呼叫信息
                else {
                    speakText = callInfoBean.getRoomName().replaceAll("病房|房", "") + "病房呼叫";
                }
                int currentVersion = android.os.Build.VERSION.SDK_INT;
                //Android 8.0的语音播报适配
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
     * 处理生日数量
     */
    private void dealBirthdayNum() {
        if (CollectionUtils.isNotEmpty(mergedCallInfoBeanList)) {
            for (int k = 0; k < mergedCallInfoBeanList.size(); k++) {
                CallInfoBean callInfoBean = mergedCallInfoBeanList.get(k);
                //如果是生日消息则删除
                if ("1".equals(callInfoBean.getMessageType())) {
                    mergedCallInfoBeanList.remove(k);
                    k--;
                }
            }
        }
    }


    /**
     * 处理服务端中没有回调的消息
     * 即服务端的消息没有回调方法的，在消息弹窗消失后，即清除该消息信息
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
     * 显示要提醒的消息的数目
     */
    private void showWarnMessageNum() {
        int birthdayNum = showBirthdayMessageNum();
        //判断是不是呼叫超时消息，根据配置项控制是否展示
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
     * 去除提醒消息数据中的超时消息
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
     * 显示生日提醒的条数
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
                        //说明此病人已经被处理
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
                //判断列表是不是已经有此患者的生日提醒
                List<PatientInfo> patientInfos = ACacheService.getIgnorePatBirthday();
                for (int i = 0; i < patientInfos.size(); i++) {
                    PatientInfo patientInfo1 = patientInfos.get(i);
                    if (patientInfo.getPatientId().equals(patientInfo1.getPatientId()) && patientInfo.getVisitId().equals(patientInfo1.getVisitId())) {
                        //说明已经有了这个患者的信息
                        hasPat = true;
                        break;
                    }
                }
                if (CollectionUtils.isNotEmpty(mergedCallInfoBeanList)) {
                    for (CallInfoBean callInfoBean : mergedCallInfoBeanList) {
                        if ("1".equals(callInfoBean.getMessageType())) {
                            PatientInfo patientInfo1 = callInfoBean.getPatientInfo();
                            if (patientInfo.getPatientId().equals(patientInfo1.getPatientId()) && patientInfo.getVisitId().equals(patientInfo1.getVisitId())) {
                                //说明已经有了这个患者的信息
                                hasPat = true;
                                break;
                            }
                        }
                    }
                }
                //说明消息列表没有当前这个病人
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
     * 判断给定字符串是否能转化为int型数字
     *
     * @param port 给定字符串
     * @return true->表示可以转换，false->表示不可转换
     */
    private boolean isCanParseInt(String port) {
        if (TextUtils.isEmpty(port)) {
            return false;
        }
        return port.matches("\\d+");
    }

    private void showBirthdayDialog() {
        //没有患者的时候不弹出
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
     * 今日生日的患者
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
        //如果有新加入的今天生日的，弹弹窗
        if (ConfigInfo.get(ConfigInfo.SPEC_FUNCTION).contains(BIRTHDAY_TIP)) {
            if (CollectionUtils.isNotEmpty(birthdayPatientList)) {
                if (CollectionUtils.isEmpty(birthPatientList)) {
                    birthPatientList = new ArrayList<>();

                }
                //有今日生日的患者出院，数量相应减少
                if (birthPatientList.size() > birthdayPatientList.size()) {
                    birthPatientList = birthdayPatientList;
                    dealBirthdayNum();
                    showWarnMessageNum();
                }
                //有新增的今日生日的患者
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
                        //说明有新加入的今天过生日的病人
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
        //ZHBFDEVIMP-151问题修改
        //refreshWarnMessageDialog();
    }

    /**
     * 6:30之前不能弹出弹窗
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
     * 智能提醒触摸事件
     */
    class RemindBtnOnTouchListener implements View.OnTouchListener {
        boolean isMoveOut = false;

        RectF rectF;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    int[] location = new int[2];
                    // 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
                    v.getLocationOnScreen(location);
                    rectF = new RectF(location[0], location[1], location[0] + v.getWidth(),
                            location[1] + v.getHeight());
                    v.setBackgroundColor(Color.parseColor("#E5963C"));
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getRawX(); // 获取相对于屏幕左上角的 x 坐标值
                    float y = event.getRawY(); // 获取相对于屏幕左上角的 y 坐标值
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
     * 消息处理
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
//                            mainActivity.slideShow();//开始锁屏轮播
//                        }
//                    }
                    break;
                case ParamsConstants.RESTART_BLUETOOTH:
                    if (msg.arg1 == 1) {
                        //打开定时器，每5秒钟重新绑定蓝牙服务
                        if (mainActivity.blueTimer == null && !ConfigSingleton.getInstance().getIsBlueConnect()) {
                            mainActivity.blueTimer = new Timer();
                            mainActivity.blueTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                }
                            }, 5000, 5000);
                        }
                    } else if (msg.arg1 == -1) {
                        //蓝牙连接成功，关闭重连定时器
                        ConfigSingleton.getInstance().setIsBlueConnect(true);
                        if (mainActivity.blueTimer != null) {
                            mainActivity.blueTimer.cancel();
                            mainActivity.blueTimer = null;
                        }
                    } else if (msg.arg1 == 2) {
                        //异常断开连接,解绑重新绑定蓝牙
                        ConfigSingleton.getInstance().setIsBlueConnect(false);
                    }
                    break;
                case ParamsConstants.FACE_CHECKING:
                    mainActivity.tvName.setText("人脸识别中...");
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
                        //关闭动画界面
                    } else {
                        //判断是不是第一次登陆，如果是动画完成后出现服务器登陆框
                        if (isOnce) {
                            //关闭动画界面
                            mainActivity.mPresenter.showNetDialog();
                        } else if (isConnectError) {
                            Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_FAILED, "网络错误，刷新失败", Toast.LENGTH_LONG);
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
                    mainActivity.computeTtsString("签到成功");
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
                    mainActivity.computeTtsString("娜娜遇见新的内容了，需要根据您的选择学习");
                    break;
                case ParamsConstants.BIRTHDAY_PAT:
                    List<PatientInfo> patientInfos = (List<PatientInfo>) msg.obj;
                    Log.i(TAG, "handleMessage: " + patientInfos.size());
                    mainActivity.setTodayBornPatients(patientInfos);
                    break;
                case ParamsConstants.CHANGE_DENSITY:
//                    ScreenadapterUtils.setCustomDensity(mainActivity, mainActivity.getApplication(), ScreenadapterUtils.DEFAULT_DENSITY);
                    break;
                //关闭语音识别提示面板
                case ParamsConstants.MSG_CLOSE_ASR_VIEW:
                    mainActivity.closeAsrTipView();
                    break;
                //应用主题发生了改变，切换主题
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
        deptBeans.add(0, new DeptBean("全部", ""));
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
     * 人脸签到的点击处理
     */
    @OnClick({R.id.ll_face_land})
    public void onFaceLand() {
        //如果是人脸签到的弹框，在打开前关闭右上角的人脸识别
        if (!ArcFaceSingleton.getInstance().getMFaceDB().getMRegister().isEmpty()) {
            onStopFaceRecognition();
            llSurfaceView.setVisibility(View.INVISIBLE);
        }
        //打开人脸签到弹框
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
     * 预入科患者信息弹框
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
     * 编辑公告信息
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
     * 初始化公告栏信息
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
            announcementView.setText("请输入信息");
        }
    }

    private NurseAssistantMenuView nurseAssistantMenuView;

    /**
     * 显示主页面中中间核心内容部分的界面
     *
     * @param name
     */
    private void showSubContent(String name) {
        if (name.equals(MenuInfo.getInstance().getInfusionItem().getMenuName())) {
            if ("第三方输液系统apk版".equals(ConfigInfo.get(ConfigInfo.INFUSION_SOURCE))) {
                webView.setVisibility(View.GONE);
                //todo 这部分判断逻辑与判断是否安装QQ浏览器相同 日后正式接入输液跳转第三方应用再改进
                String packageName = ConfigInfo.get(ConfigInfo.THIRD_PARTY_INFUSION_INFO);
                if (TextUtils.isEmpty(packageName)) {
                    Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "请配置第三方输液系统安装包包名", Toast.LENGTH_SHORT);
                    return;
                }
                List<PackageInfo> info = getPackageManager().getInstalledPackages(0);
                if (info == null || info.isEmpty()) {
                    Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "请安装输液监控相关应用", Toast.LENGTH_SHORT);
                    return;
                }
                for (int i = 0; i < info.size(); i++) {
                    if (info.get(i).packageName.equals(packageName)) {
                        MenuInfo.getInstance().showBackBtn();
                        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);//原来默认包名："com.ambition.ep"
                        startActivity(intent);
                        return;
                    }
                }
                Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "请安装输液监控相关应用", Toast.LENGTH_SHORT);
                return;
            } else if ("第三方输液系统web版".equals(ConfigInfo.get(ConfigInfo.INFUSION_SOURCE))) {
                //todo 这部分判断逻辑与判断是否安装QQ浏览器相同 日后正式接入输液跳转第三方应用再改进
                String url = ConfigInfo.get(ConfigInfo.THIRD_PARTY_INFUSION_INFO);
                if (TextUtils.isEmpty(url)) {
                    webView.setVisibility(View.GONE);
                    Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "请配置第三方输液系统地址", Toast.LENGTH_SHORT);
                    return;
                }
                if (!TextUtils.isEmpty(thirdPartyInfusionUrl) && thirdPartyInfusionUrl.equals(url)) {
                    webView.setVisibility(View.VISIBLE);
                    title.setText("输液监控");
                } else {
                    title.setText("输液监控");
                    thirdPartyInfusionUrl = url;
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl(url);
                }
                return;
            }else if ("第三方输液数据对接版".equals(ConfigInfo.get(ConfigInfo.INFUSION_SOURCE))) {
                //todo 这部分判断逻辑与判断是否安装QQ浏览器相同 日后正式接入输液跳转第三方应用再改进
                String url = ConfigInfo.get(ConfigInfo.THIRD_PARTY_INFUSION_INFO);
                if (TextUtils.isEmpty(url)) {
                    Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "请配置第三方输液系统服务地址", Toast.LENGTH_SHORT);
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
     * 判断是否在允许的白名单列表fragmentWhiteList中
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
     * 显示网络状态
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
     * 自动播放公告信息
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
     * 跳转浏览器
     *
     * @param url
     */
    private void toWebPage(String url) {
//        List<PackageInfo> info = getPackageManager().getInstalledPackages(0);
//        if (info == null || info.isEmpty()) {
//            Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "请安装QQ浏览器", Toast.LENGTH_SHORT);
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
//        Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "请安装QQ浏览器", Toast.LENGTH_SHORT);


        //使用系统默认浏览器
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
                        //如果有超时呼叫，必然放最后面
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
     * 完成主题的修改
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
            //切换其它主题
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
     * 切肤的预准备
     * 若本地不存在皮肤包，则先下载皮肤包
     *
     * @param skinName 设置中选择的皮肤的名称，将通过此名称找到皮肤包信息
     */
    private void prepareChangeSkin(String skinName) {
        List<SkinBean> skins = ACacheService.getSkins();
        if (CollectionUtils.isEmpty(skins)) {
            Toaster.makeToast(this, Toaster.TOAST_FAILED, "皮肤信息加载中，请稍候再试", Toast.LENGTH_LONG);
            return;
        }

        Optional<SkinBean> skinModel = StreamSupport.stream(skins).filter(x -> skinName.equals(x.getSkinName())).findFirst();
        if (!skinModel.isPresent()) {
            Toaster.makeToast(this, Toaster.TOAST_FAILED, "未获取到该皮肤信息，请稍候再试", Toast.LENGTH_LONG);
            return;
        }

        if(TextUtils.isEmpty(skinModel.get().getDownloadUrl()) || !skinModel.get().getDownloadUrl().contains("/")){
            Toaster.makeToast(this, Toaster.TOAST_FAILED, "皮肤信息不完整，请联系管理员", Toast.LENGTH_LONG);
            return;
        }

//        String skinFileFullPath = MainActivity.this.getCacheDir() + File.separator + skinModel.get().getDownloadUrl().substring(skinModel.get().getDownloadUrl().lastIndexOf("/") + 1);
        String skinFileFullPath =Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "nanaLight.skin";
        File skin = new File(skinFileFullPath);

        //本地不存在皮肤包，则先下载，然后切肤
        if (!skin.exists()) {
            DownloadUtil.download(ConfigSingleton.getInstance().getServerUrl() + skinModel.get().getDownloadUrl(), skinFileFullPath, new DownloadListener() {
                @Override
                public void onStart() {
                    handler.post(() -> {
                        LoadingUtil.getInstance().show("开始下载皮肤包.....");
                    });
                    Log.d(ParamsConstants.LOG_DOWNLOAD_INFO, "开始下载皮肤包");
                }

                @Override
                public void onProgress(int progress) {
                    handler.post(() -> {
                        LoadingUtil.getInstance().show("已下载" + progress + ".....");
                    });
                    Log.d(ParamsConstants.LOG_DOWNLOAD_INFO, "已经下载" + progress);
                }

                @Override
                public void onFinish(String path) {
                    handler.post(() -> {
                        LoadingUtil.getInstance().hide();
                    });
                    Log.d(ParamsConstants.LOG_DOWNLOAD_INFO, "下载完成");
                    //切换皮肤
                    applySkin(skin);
                }

                @Override
                public void onFail(String errorInfo) {
                    handler.post(() -> {
                        LoadingUtil.getInstance().hide();
                        Toaster.makeToast(MainActivity.this, Toaster.TOAST_FAILED, "下载皮肤包出错,原因" + errorInfo, Toast.LENGTH_SHORT);
                    });

                    Log.e(ParamsConstants.LOG_DOWNLOAD_INFO, "下载皮肤包" + skinModel.get().getSkinName() + "失败，原因:" + errorInfo);

                    //删除相关文件
                    if (skin.exists()) {
                        boolean deleteSuccess = skin.delete();
                        if (!deleteSuccess) {
                            Log.e(ParamsConstants.LOG_DOWNLOAD_INFO, "删除下载失败皮肤包残余文件" + skinModel.get().getSkinName() + "失败，这可能会引起换肤失败的问题，请清理缓存后重新下载皮肤包");
                        }
                    }
                }
            });
        }
        //皮肤包存在，则直接切肤
        else {
            applySkin(skin);
        }


    }

    /**
     * 应用皮肤包，完成切肤
     *
     * @param skin 皮肤包文件，需保存此皮肤包文件存在且无误，否则会导致切肤失败
     */
    public void applySkin(File skin) {
        //皮肤包已经存在，直接加载皮肤包
        SkinManager.getInstance().load(skin.getAbsolutePath(),
                new ILoaderListener() {
                    @Override
                    public void onStart() {
                        Log.d(ParamsConstants.LOG_SKIN, "加载皮肤包");
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(ParamsConstants.LOG_SKIN, "切换成功");
                    }

                    @Override
                    public void onFailed() {
                        Log.e(ParamsConstants.LOG_SKIN, "加载皮肤包失败");
                    }
                });

    }

}