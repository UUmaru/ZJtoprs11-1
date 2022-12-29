package com.kyee.iwis.nana.config.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kyee.iwis.nana.BuildConfig;
import com.kyee.iwis.nana.ParamsConstants;
import com.kyee.iwis.nana.R;
import com.kyee.iwis.nana.StatusConstants;
import com.kyee.iwis.nana.arcface.ArcFaceSingleton;
import com.kyee.iwis.nana.base.net.MainThreadCallBack;
import com.kyee.iwis.nana.base.net.NetApi;
import com.kyee.iwis.nana.base.net.ResponseCallBack;
import com.kyee.iwis.nana.base.status.StatusManager;
import com.kyee.iwis.nana.base.view.BaseDialog;
import com.kyee.iwis.nana.bedmanage.bedsetting.BedSettngDialog;
import com.kyee.iwis.nana.bedmanage.rangebed.BedRangeDialog;
import com.kyee.iwis.nana.common.ConfigSingleton;
import com.kyee.iwis.nana.config.CheckEditAdapter;
import com.kyee.iwis.nana.config.MultiTextAdapter;
import com.kyee.iwis.nana.controller.fragment.ConfigItems;
import com.kyee.iwis.nana.controller.fragment.ConfigItemsItem;
import com.kyee.iwis.nana.controller.fragment.ConfigMultiItem;
import com.kyee.iwis.nana.controller.fragment.ConfigRadioItem;
import com.kyee.iwis.nana.controller.widget.dialog.SetFaceDialog;
import com.kyee.iwis.nana.faceland.FacePersonInfoDialog;
import com.kyee.iwis.nana.faceland.FacePersonSettingDialog;
import com.kyee.iwis.nana.faceland.FaceSettingDialog;
import com.kyee.iwis.nana.loading.LoadingUtil;
import com.kyee.iwis.nana.login.password.dialog.PasswordLoginDialog;
import com.kyee.iwis.nana.model.ConfigInfo;
import com.kyee.iwis.nana.model.ConfigModel;
import com.kyee.iwis.nana.model.ConfigOption;
import com.kyee.iwis.nana.services.AutoCloseDialogManager;
import com.kyee.iwis.nana.utils.BitmapUtils;
import com.kyee.iwis.nana.utils.CollectionUtils;
import com.kyee.iwis.nana.utils.ContinuousClick;
import com.kyee.iwis.nana.utils.FileUtils;
import com.kyee.iwis.nana.utils.NavigationBarUtil;
import com.kyee.iwis.nana.widget.Toaster;
import com.kyee.test.http.HttpTestDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class ConfigDialog extends BaseDialog implements ConfigItems.OnItemClickListerner {

    @BindView(R.id.ll_config_items)
    LinearLayout ll_config_items;
    @BindView(R.id.tv_optiondesc)
    TextView tv_optiondesc;
    @BindView(R.id.sv_radio)
    ScrollView sv_radio;
    @BindView(R.id.sv_multi)
    ScrollView sv_multi;
    @BindView(R.id.et_config)
    EditText et_config;
    @BindView(R.id.radiogroup)
    RadioGroup radiogroup;
    @BindView(R.id.ll_multi)
    LinearLayout ll_multi;
    @BindView(R.id.rl_information)
    RelativeLayout rl_information;
    @BindView(R.id.rl_settings)
    RelativeLayout rl_settings;
    @BindView(R.id.tv_updatemessage)
    TextView tv_updatemessage;
    @BindView(R.id.tv_client)
    TextView tv_client;
    @BindView(R.id.tv_server)
    TextView tv_server;
    @BindView(R.id.rv_config)
    RecyclerView rv_config;
    @BindView(R.id.tv_version_name)
    TextView tvVersionName;
    private List<ConfigModel> configModelList = new ArrayList<>();
    @BindView(R.id.rv_config2)
    RecyclerView rv_config_cet;
    public static Handler handler;
    private ConfigItemsItem rl_selectOption;
    private RadioButton selectRadioButton;
    private List<ConfigMultiItem> configMultiItemList;
    private OnButtonClickListener mOnButtonClickListener;

    private ConfigOption configOption;
    private MultiTextAdapter multiTextAdapter;
    private CheckEditAdapter checkEditAdapter;
    private SparseArray<String> editArray;
    private Context mContext;

    private SetFaceDialog setFaceDialog;
    private PasswordLoginDialog passwordLoginDialog;
    /**
     * 人脸识别设置弹框
     */
    private FaceSettingDialog faceSettingDialog;

    private FacePersonInfoDialog facePersonInfoDialog;

    private FacePersonSettingDialog facePersonSettingDialog;

    private ConfigOption curConfigOption;

    ConfigDialog(@NonNull Context context, int themeResId, final List<ConfigModel> configModelList) {
        super(context, themeResId);
        this.mContext = context;
        this.configModelList = configModelList;
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.mOnButtonClickListener = onButtonClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_config);
        ButterKnife.bind(this);

        handler = new MyHandler(ConfigDialog.this);
        if (CollectionUtils.isNotEmpty(configModelList)) {
            curConfigOption = configModelList.get(0).getOptions().get(0);
        }
        for (int i = 0; i < configModelList.size(); i++) {
            ConfigItems configItems = new ConfigItems(ConfigSingleton.getInstance().getCurrentContext());
            configItems.init(configModelList.get(i), i);
            configItems.setOnItemClickListerner(ConfigDialog.this);
            ll_config_items.addView(configItems);
        }

        tv_updatemessage.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_updatemessage.setText(BuildConfig.versionMessage);
        tv_client.setText(String.format("安卓端：%s（%s）", BuildConfig.VERSION_NAME, BuildConfig.releaseTime));
        tv_server.setText(String.format("服务端：%s", ConfigSingleton.getInstance().getServerVersion()));


        tvVersionName.setOnClickListener(new ContinuousClick(5) {
            @Override
            public void onContinuousClick() {
                NavigationBarUtil.showNavigationBar(ConfigDialog.this.getWindow());
            }
        });

        tvVersionName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                HttpTestDialog httpTestDialog = new HttpTestDialog(v.getContext());
                httpTestDialog.show();
                return true;
            }
        });

    }

    @Override
    public boolean onItemClick(final ConfigOption configOption) {
        if (isLegalConfig(curConfigOption)) {
            curConfigOption = configOption;
            return true;
        } else {
            curConfigOption = (ConfigOption) rl_selectOption.getTag();
            return false;
        }
    }

    static class MyHandler extends Handler {
        final WeakReference<ConfigDialog> weakDialog;

        MyHandler(ConfigDialog weakDialog) {
            this.weakDialog = new WeakReference<>(weakDialog);
        }

        @Override
        public void handleMessage(Message msg) {
            ConfigDialog dialog = weakDialog.get();
            switch (msg.what) {
                case ParamsConstants.CONFIG_OPTION_SELECT:
                    dialog.rl_selectOption.setBackgroundColor(Color.parseColor("#00000000"));
                    dialog.rl_selectOption = (ConfigItemsItem) msg.obj;
                    dialog.configOption = (ConfigOption) dialog.rl_selectOption.getTag();
                    dialog.rl_selectOption.setBackground(ConfigSingleton.getInstance().getCurrentContext().getResources().getDrawable(R.drawable.config_item_select_bg));
                    dialog.initOptions();
                    break;
                case ParamsConstants.CONFIG_DEFAULT_OPTION:
                    dialog.rl_selectOption = (ConfigItemsItem) msg.obj;
                    dialog.rl_selectOption.setBackground(ConfigSingleton.getInstance().getCurrentContext().getResources().getDrawable(R.drawable.config_item_select_bg));
                    dialog.configOption = (ConfigOption) dialog.rl_selectOption.getTag();
                    dialog.et_config.addTextChangedListener(new MyTextWatcher(dialog));
                    dialog.initOptions();
                    break;
                case ParamsConstants.CONFIG_DEFAULT_SELECTED_RADIOBUTTON:
                    dialog.selectRadioButton = (RadioButton) msg.obj;
                    dialog.selectRadioButton.setChecked(true);
                    break;
                case ParamsConstants.CONFIG_SELECT_RADIOBUTTON:
                    dialog.selectRadioButton.setChecked(false);
                    dialog.selectRadioButton = (RadioButton) msg.obj;
                    //用参数区分是只有单选还是控制输入框的单选（0是只有单选，1是控制输入框的单选）
                    int args1 = msg.arg1;
                    dialog.selectRadioButton.setChecked(true);
                    Map<String, Object> map = (Map<String, Object>) dialog.selectRadioButton.getTag();
                    String configValue = (String) map.get("value");
                    ConfigOption configOption = (ConfigOption) map.get("configOption");
                    for (int i = 0; i < dialog.configModelList.size(); i++) {
                        if (dialog.configModelList.get(i).getItemName().equals(configOption.getConfigtypeName())) {
                            for (int j = 0; j < dialog.configModelList.get(i).getOptions().size(); j++) {
                                if (dialog.configModelList.get(i).getOptions().get(j).getConfigKey().equals(configOption.getConfigKey())) {
                                    if (args1 == 0) {
                                        dialog.configModelList.get(i).getOptions().get(j).setConfigValue(configValue);
                                    } else {
                                        String oldValue = dialog.configModelList.get(i).getOptions().get(j).getConfigValue();
                                        if (!TextUtils.isEmpty(oldValue)) {
                                            String[] values = oldValue.split("\\*&\\*",-1);
                                            if (values.length >= 2) {
//                                                String newValue = configValue + "*&*" + values[1];
                                                String newValue = configValue + oldValue.substring(values[0].length());
                                                dialog.configModelList.get(i).getOptions().get(j).setConfigValue(newValue);
                                            }
                                        } else {
                                            dialog.configModelList.get(i).getOptions().get(j).setConfigValue(configValue);
                                        }
                                        if (dialog.checkEditAdapter != null) {
                                            dialog.checkEditAdapter.setConfigData(dialog.configModelList.get(i).getOptions().get(j));
                                        }
                                    }
                                    dialog.rl_selectOption.setCOnfigValue(configValue);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                case ParamsConstants.DIALOG_CHECKBOX_CLICK:
                    String checkBoxConfigValue = "";
                    for (int i = 0; i < dialog.configMultiItemList.size(); i++) {
                        if (dialog.configMultiItemList.get(i).getCheckBoxIsChecked()) {
                            checkBoxConfigValue = String.format("%s*#*%s", checkBoxConfigValue, dialog.configMultiItemList.get(i).getOption());
                        }
                    }
                    if (!"".equals(checkBoxConfigValue)) {
                        checkBoxConfigValue = checkBoxConfigValue.substring(3);
                    }
                    for (int i = 0; i < dialog.configModelList.size(); i++) {
                        if (dialog.configModelList.get(i).getItemName().equals(((ConfigOption) dialog.configMultiItemList.get(0).getTag()).getConfigtypeName())) {
                            for (int j = 0; j < dialog.configModelList.get(i).getOptions().size(); j++) {
                                if (dialog.configModelList.get(i).getOptions().get(j).getConfigKey().equals(((ConfigOption) dialog.configMultiItemList.get(0).getTag()).getConfigKey())) {
                                    dialog.configModelList.get(i).getOptions().get(j).setConfigValue(checkBoxConfigValue);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                case ParamsConstants.CONFIG_SAVE_ERROR:
                    Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_FAILED, "保存失败", Toast.LENGTH_SHORT);
                    break;
            }
        }
    }


    private void initOptions() {
        final ConfigOption configOption = (ConfigOption) rl_selectOption.getTag();
        tv_optiondesc.setText(configOption.getConfigComment());
        if (configOption.getControlType() == null) {
            Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_FAILED, "数据访问超时，请稍后再试", Toast.LENGTH_SHORT);
        } else {
            switch (configOption.getControlType()) {
                case ConfigOption.MULTICHECK: {
                    sv_multi.setVisibility(View.VISIBLE);
                    sv_radio.setVisibility(View.GONE);
                    et_config.setVisibility(View.GONE);
                    rv_config.setVisibility(View.GONE);
                    rv_config_cet.setVisibility(View.GONE);
                    String[] optionsContents = configOption.getOptionsContent().split("\\*#\\*");
                    String[] optionsDesc;
                    if (configOption.getOptionsDesc() != null) {
                        optionsDesc = configOption.getOptionsDesc().split("\\*#\\*");
                    } else {
                        optionsDesc = new String[optionsContents.length];
                        for (int i = 0; i < optionsDesc.length; i++) {
                            optionsDesc[i] = "";
                        }
                    }
                    ll_multi.removeAllViews();
                    String[] configValues = configOption.getConfigValue().split("\\*#\\*");
                    configMultiItemList = new ArrayList<>();
                    for (int i = 0; i < optionsContents.length; i++) {
                        boolean isSelected = false;
                        for (String configValue : configValues) {
                            if (configValue.equals(optionsContents[i])) {
                                isSelected = true;
                                break;
                            }
                        }
                        ConfigMultiItem configMultiItem = new ConfigMultiItem(ConfigSingleton.getInstance().getCurrentContext(), optionsContents[i],
                                i < optionsDesc.length ? optionsDesc[i] : "", isSelected);
                        configMultiItem.setTag(configOption);
                        configMultiItemList.add(configMultiItem);
                        ll_multi.addView(configMultiItem);
                    }
                    break;
                }
                case ConfigOption.SINGLECHECK: {
                    sv_multi.setVisibility(View.GONE);
                    sv_radio.setVisibility(View.VISIBLE);
                    et_config.setVisibility(View.GONE);
                    rv_config.setVisibility(View.GONE);
                    rv_config_cet.setVisibility(View.GONE);
                    String[] optionsContents = configOption.getOptionsContent().split("\\*#\\*");
                    String[] optionsDesc;
                    if (configOption.getOptionsDesc() != null) {
                        optionsDesc = configOption.getOptionsDesc().split("\\*#\\*");
                    } else {
                        optionsDesc = new String[optionsContents.length];
                        for (int i = 0; i < optionsDesc.length; i++) {
                            optionsDesc[i] = "";
                        }
                    }
                    radiogroup.removeAllViews();
                    for (int i = 0; i < optionsContents.length; i++) {
                        boolean isSelected = false;
                        if (optionsContents[i].equals(configOption.getConfigValue())) {
                            isSelected = true;
                        }
                        ConfigRadioItem configRadioItem = new ConfigRadioItem(ConfigSingleton.getInstance().getCurrentContext(), optionsContents[i],
                                i < optionsDesc.length ? optionsDesc[i] : "", isSelected, configOption, 0);
                        radiogroup.addView(configRadioItem);

                    }
                    break;
                }
                case ConfigOption.TEXTVIEW:
                    if (myTextWatcher != null) {
                        et_config.removeTextChangedListener(myTextWatcher);
                    }
                    myTextWatcher = new MyTextWatcher(ConfigDialog.this);
                    sv_multi.setVisibility(View.GONE);
                    sv_radio.setVisibility(View.GONE);
                    et_config.setVisibility(View.VISIBLE);
                    rv_config.setVisibility(View.GONE);
                    rv_config_cet.setVisibility(View.GONE);
                    if (ConfigInfo.REFRESH_TIME.equals(configOption.getConfigKey())
                            || ConfigInfo.REMIND_TIME_INTERVAL.equals(configOption.getConfigKey())
                        ||ConfigInfo.CALL_TIMEOUT.equals(configOption.getConfigKey())) {
                        et_config.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else if (ConfigInfo.CLIENT_PIN.equals(configOption.getConfigKey())) {
                        et_config.setInputType(InputType.TYPE_CLASS_NUMBER);
                        et_config.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                    } else if(ConfigInfo.CLIENT_DPI.equals(configOption.getConfigKey())){
                        et_config.setInputType(InputType.TYPE_CLASS_NUMBER);
                        et_config.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

                    }
                    else{
                        et_config.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                    et_config.setText(configOption.getConfigValue());
                    et_config.setSelection(et_config.getText().toString().length());
                    break;
                case ConfigOption.MULTI_TEXTVIEW:
                    sv_multi.setVisibility(View.GONE);
                    sv_radio.setVisibility(View.GONE);
                    et_config.setVisibility(View.GONE);
                    rv_config.setVisibility(View.VISIBLE);
                    rv_config_cet.setVisibility(View.GONE);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rv_config.setLayoutManager(linearLayoutManager);
                    multiTextAdapter = new MultiTextAdapter(getContext());
                    rv_config.setAdapter(multiTextAdapter);
                    multiTextAdapter.setOnItemInfoChangedListener((position, info) -> {
                        editArray.put(position, info);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < editArray.size(); i++) {
                            sb.append("*#*");
                            sb.append(editArray.get(i));
                        }
                        configOption.setConfigValue(sb.toString().substring(3));
                        multiTextAdapter.setConfigValues(sb.toString().substring(3));
                    });
                    multiTextAdapter.setConfigData(configOption);
                    editArray = new SparseArray<>();
                    if (!TextUtils.isEmpty(configOption.getConfigValue())) {
                        String[] editValues = configOption.getConfigValue().split("\\*#\\*", -1);
                        for (int i = 0; i < editValues.length; i++) {
                            editArray.put(i, editValues[i]);
                        }
                    }
                    rv_config.setVerticalScrollBarEnabled(true);
                    break;
                case "CHECK_EDITTEXT":
                    sv_multi.setVisibility(View.GONE);
                    sv_radio.setVisibility(View.GONE);
                    et_config.setVisibility(View.GONE);
                    rv_config.setVisibility(View.GONE);
                    rv_config_cet.setVisibility(View.VISIBLE);
                    LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
                    linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
                    rv_config_cet.setLayoutManager(linearLayoutManager1);
                    checkEditAdapter = new CheckEditAdapter(getContext());
                    rv_config_cet.setAdapter(checkEditAdapter);
                    checkEditAdapter.setOnItemInfoChangedListener((type, info) -> {
                        String[] configValues = configOption.getConfigValue().split("\\*&\\*",-1);
                        configValues[type] = info;
                        String value = TextUtils.join("*&*",configValues);
                        configOption.setConfigValue(value);
                    });
                    checkEditAdapter.setConfigData(configOption);
                    editArray = new SparseArray<>();
                    if (!TextUtils.isEmpty(configOption.getConfigValue())) {
                        String[] configValues = configOption.getConfigValue().split("\\*&\\*");
                        int index = 0;
                        for(int i = 0;i<configValues.length;i++){
                            if(i==0) continue;
                            String[] editValues = configValues[i].split("\\*#\\*", -1);
                            for (int j = 0; j < editValues.length; j++) {
                                    editArray.put(index, editValues[j]);
                                    index++;
                            }
                        }
                    }
                    rv_config_cet.setVerticalScrollBarEnabled(true);
                    break;
                default:
                    break;
            }
        }
    }

    private MyTextWatcher myTextWatcher;



    static class MyTextWatcher implements TextWatcher {

        final WeakReference<ConfigDialog> weakDialog;

        MyTextWatcher(ConfigDialog weakDialog) {
            this.weakDialog = new WeakReference<>(weakDialog);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            ConfigDialog dialog = weakDialog.get();
            for (int i = 0; i < dialog.configModelList.size(); i++) {
                if (dialog.configModelList.get(i).getItemName().equals(dialog.configOption.getConfigtypeName())) {
                    for (int j = 0; j < dialog.configModelList.get(i).getOptions().size(); j++) {
                        if (dialog.configModelList.get(i).getOptions().get(j).getConfigKey().equals(dialog.configOption.getConfigKey())) {
                            dialog.configModelList.get(i).getOptions().get(j).setConfigValue(dialog.et_config.getText().toString());
                            dialog.configOption.setConfigValue(dialog.et_config.getText().toString());
                            dialog.rl_selectOption.setCOnfigValue(dialog.et_config.getText().toString());
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }


    @Override
    public void show() {
        super.show();
        StatusManager.getInstance().setStatus(StatusConstants.STOP_AUTO_REFRESH, true);
        AutoCloseDialogManager.getInstance().addDialog(this);
    }


    public static class Builder {
        private final ConfigDialog configDialog;

        public Builder(Context context, List<ConfigModel> configModelList) {
            configDialog = new ConfigDialog(context, R.style.dialog_login, configModelList);
        }

        public ConfigDialog create() {
            return configDialog;
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        StatusManager.getInstance().setStatus(StatusConstants.STOP_AUTO_REFRESH, false);
        AutoCloseDialogManager.getInstance().remove(this.getClass());
    }

    /**
     * 判断设置项是否可行
     *
     * @param configOption
     * @return
     */
    private boolean isLegalConfig(ConfigOption configOption) {
        if (ConfigOption.NURSE_LEVEL_CONFIG.equals(configOption.getConfigTag())) {
            //对色值进行限制，设置错误会提示
            String[] editValues = configOption.getConfigValue().split("\\*#\\*");
            String[] optionContents = configOption.getOptionsContent().split("\\*#\\*");
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < editValues.length; k++) {
                if (optionContents.length >= editValues.length) {
                    if ("特级护理显示名称".equals(optionContents[k]) || "一级护理显示名称".equals(optionContents[k]) ||
                            "二级护理显示名称".equals(optionContents[k]) || "三级护理显示名称".equals(optionContents[k])) {
                        if (editValues[k].length() == 0) {
                            Toaster.makeToast(getContext(), Toaster.TOAST_REMIND, "护理等级名称不能为空，恢复为原值", Toast.LENGTH_SHORT);
                            for (int m = 0; m < editValues.length; m++) {
                                sb.append("*#*");
                                if (editValues[m].length() == 0) {
                                    if ("特级护理显示名称".equals(optionContents[m])) {
                                        sb.append(ConfigSingleton.getInstance().getNurseLevelName()[0]);
                                        editArray.put(k, ConfigSingleton.getInstance().getNurseLevelName()[0]);
                                    } else if ("一级护理显示名称".equals(optionContents[m])) {
                                        sb.append(ConfigSingleton.getInstance().getNurseLevelName()[1]);
                                        editArray.put(k, ConfigSingleton.getInstance().getNurseLevelName()[1]);
                                    } else if ("二级护理显示名称".equals(optionContents[m])) {
                                        sb.append(ConfigSingleton.getInstance().getNurseLevelName()[2]);
                                        editArray.put(k, ConfigSingleton.getInstance().getNurseLevelName()[2]);
                                    } else if ("三级护理显示名称".equals(optionContents[m])) {
                                        sb.append(ConfigSingleton.getInstance().getNurseLevelName()[3]);
                                        editArray.put(k, ConfigSingleton.getInstance().getNurseLevelName()[3]);
                                    } else {
                                        sb.append(editValues[m]);
                                    }
                                } else {
                                    sb.append(editValues[m]);
                                }

                            }
                            configOption.setConfigValue(sb.toString().substring(3));
                            multiTextAdapter.setConfigData(configOption);
                            return false;
                        }
                    }
                    else {
                        if (editValues[k].length() != 6 && editValues[k].length() != 8) {
                            Toaster.makeToast(getContext(), Toaster.TOAST_REMIND, "护理等级色值不规范，恢复原值", Toast.LENGTH_SHORT);
                            for (int m = 0; m < editValues.length; m++) {
                                sb.append("*#*");
                                if (editValues[m].length() != 6 && editValues[m].length() != 8) {
                                    //获取错误项是第几护理等级，名称、背景色还是字体颜色
                                    int nurlvl = m / 3;
                                    int nurItem = m % 3;
                                    if (nurItem == 1) {
                                        sb.append(ConfigSingleton.getInstance().getNurseCardConfig().get(nurlvl).getBackgroundColor().substring(1));
                                        editArray.put(m, ConfigSingleton.getInstance().getNurseCardConfig().get(nurlvl).getBackgroundColor().substring(1));
                                    } else if (nurItem == 2) {
                                        sb.append(ConfigSingleton.getInstance().getNurseCardConfig().get(nurlvl).getTextColor().substring(1));
                                        editArray.put(m, ConfigSingleton.getInstance().getNurseCardConfig().get(nurlvl).getTextColor().substring(1));
                                    } else {
                                        sb.append(editValues[m]);
                                    }
                                } else {
                                    sb.append(editValues[m]);
                                }
                            }
                            configOption.setConfigValue(sb.toString().substring(3));
                            multiTextAdapter.setConfigData(configOption);
                            Log.i("tttt", "重置色" + curConfigOption.getConfigValue());
                            return false;
                        }
                    }

                }
            }
        } else if (ConfigOption.NURSE_LEVEL_FLAG_CONFIG.equals(configOption.getConfigTag())) {
            //对色值进行限制，设置错误会提示
            String[] editValues = configOption.getConfigValue().split("\\*#\\*");
            String[] optionContents = configOption.getOptionsContent().split("\\*#\\*");
            //修复三级等级为空时，不提醒颜色值异常的问题
            if (editValues.length < optionContents.length) {
                String[] x = new String[optionContents.length];
                for (int i = 0; i < editValues.length; i++) {
                    x[i] = editValues[i];
                }
                for (int j = editValues.length; j < optionContents.length; j++) {
                    x[j] = "";
                }
                editValues = x;
            }
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < editValues.length; k++) {
                if (optionContents.length >= editValues.length) {
                    //ZHBFDEVIMP-231
//                    if ("特级护理显示名称".equals(optionContents[k]) || "一级护理显示名称".equals(optionContents[k]) ||
//                            "二级护理显示名称".equals(optionContents[k]) || "三级护理显示名称".equals(optionContents[k])) {
//                        if (editValues[k].length() == 0) {
//                            Toaster.makeToast(getContext(), Toaster.TOAST_REMIND, "护理等级名称不能为空，恢复为原值", Toast.LENGTH_SHORT);
//                            for (int m = 0; m < editValues.length; m++) {
//                                sb.append("*#*");
//                                if (editValues[m].length() == 0) {
//                                    if ("特级护理显示名称".equals(optionContents[m])) {
//                                        sb.append(ConfigSingleton.getInstance().getNurseLevelName()[0]);
//                                        editArray.put(k, ConfigSingleton.getInstance().getNurseLevelName()[0]);
//                                    } else if ("一级护理显示名称".equals(optionContents[m])) {
//                                        sb.append(ConfigSingleton.getInstance().getNurseLevelName()[1]);
//                                        editArray.put(k, ConfigSingleton.getInstance().getNurseLevelName()[1]);
//                                    } else if ("二级护理显示名称".equals(optionContents[m])) {
//                                        sb.append(ConfigSingleton.getInstance().getNurseLevelName()[2]);
//                                        editArray.put(k, ConfigSingleton.getInstance().getNurseLevelName()[2]);
//                                    } else if ("三级护理显示名称".equals(optionContents[m])) {
//                                        sb.append(ConfigSingleton.getInstance().getNurseLevelName()[3]);
//                                        editArray.put(k, ConfigSingleton.getInstance().getNurseLevelName()[3]);
//                                    } else {
//                                        sb.append(editValues[m]);
//                                    }
//                                } else {
//                                    sb.append(editValues[m]);
//                                }
//
//                            }
//                            configOption.setConfigValue(sb.toString().substring(3));
//                            multiTextAdapter.setConfigData(configOption);
//                            return false;
//                        }
//                    }
//                    else {
//                        if (editValues[k].length() != 6 && editValues[k].length() != 8) {
//                            Toaster.makeToast(getContext(), Toaster.TOAST_REMIND, "护理等级色值不规范，恢复原值", Toast.LENGTH_SHORT);
//                            for (int m = 0; m < editValues.length; m++) {
//                                sb.append("*#*");
//                                if (editValues[m].length() != 6 && editValues[m].length() != 8) {
//                                    //获取错误项是第几护理等级，名称、背景色还是字体颜色
//                                    int nurlvl = m / 3;
//                                    int nurItem = m % 3;
//                                    if (nurItem == 1) {
//                                        sb.append(ConfigSingleton.getInstance().getNurseCardFlagConfig().get(nurlvl).getFlagTextColor().substring(1));
//                                        editArray.put(m, ConfigSingleton.getInstance().getNurseCardFlagConfig().get(nurlvl).getFlagTextCircleColor().substring(1));
//                                    } else if (nurItem == 2) {
//                                        sb.append(ConfigSingleton.getInstance().getNurseCardFlagConfig().get(nurlvl).getFlagTextColor().substring(1));
//                                        editArray.put(m, ConfigSingleton.getInstance().getNurseCardFlagConfig().get(nurlvl).getFlagTextCircleColor().substring(1));
//                                    } else {
//                                        sb.append(editValues[m]);
//                                    }
//                                } else {
//                                    sb.append(editValues[m]);
//                                }
//
//
//                            }
//                            configOption.setConfigValue(sb.toString().substring(3));
//                            multiTextAdapter.setConfigData(configOption);
//                            Log.i("tttt", "重置色" + curConfigOption.getConfigValue());
//                            return false;
//                        }
//                    }
                    if (editValues[k].length() != 6 && editValues[k].length() != 8) {
                        Toaster.makeToast(getContext(), Toaster.TOAST_REMIND, "护理等级色值不规范，恢复原值", Toast.LENGTH_SHORT);
                        for (int m = 0; m < editValues.length; m++) {
                            sb.append("*#*");
                            if (editValues[m].length() != 6 && editValues[m].length() != 8) {
                                //获取错误项是第几护理字体颜色还是圆圈颜色
                                int nurlvl = m / 2;
                                int nurItem = m % 2;
                                if (nurItem == 0) {
                                    sb.append(ConfigSingleton.getInstance().getNurseCardFlagConfig().get(nurlvl).getFlagTextColor().substring(1));
                                    editArray.put(m, ConfigSingleton.getInstance().getNurseCardFlagConfig().get(nurlvl).getFlagTextColor().substring(1));
                                } else if (nurItem == 1) {
                                    sb.append(ConfigSingleton.getInstance().getNurseCardFlagConfig().get(nurlvl).getFlagTextCircleColor().substring(1));
                                    editArray.put(m, ConfigSingleton.getInstance().getNurseCardFlagConfig().get(nurlvl).getFlagTextCircleColor().substring(1));
                                } else {
                                    sb.append(editValues[m]);
                                }
                            } else {
                                sb.append(editValues[m]);
                            }
                        }
                        configOption.setConfigValue(sb.toString().substring(3));
                        multiTextAdapter.setConfigData(configOption);
                        Log.i("tttt", "重置色" + curConfigOption.getConfigValue());
                        return false;
                    }
                }
            }
        } else if (ConfigInfo.REFRESH_TIME.equals(configOption.getConfigKey())) {
            if (TextUtils.isEmpty(configOption.getConfigValue())) {
                Toaster.makeToast(getContext(), Toaster.TOAST_REMIND, "刷新频率不能为空，恢复原值", Toast.LENGTH_SHORT);
                configOption.setConfigValue(ConfigInfo.get(ConfigInfo.REFRESH_TIME));
                et_config.setText(ConfigInfo.get(ConfigInfo.REFRESH_TIME));
                et_config.setSelection(et_config.getText().toString().length());
                return false;
            } else if (Integer.parseInt(configOption.getConfigValue()) < 60) {
                Toaster.makeToast(getContext(), Toaster.TOAST_REMIND, "刷新频率设置不能低于60秒，恢复原值", Toast.LENGTH_SHORT);
                configOption.setConfigValue(ConfigInfo.get(ConfigInfo.REFRESH_TIME));
                et_config.setText(ConfigInfo.get(ConfigInfo.REFRESH_TIME));
                et_config.setSelection(et_config.getText().toString().length());
                return false;
            }
        } else if (ConfigInfo.CLIENT_PIN.equals(configOption.getConfigKey())) {
            if (TextUtils.isEmpty(configOption.getConfigValue()) || configOption.getConfigValue().length() != 4) {
                Toaster.makeToast(getContext(), Toaster.TOAST_REMIND, "PIN码设置不规范，恢复原值", Toast.LENGTH_SHORT);
                configOption.setConfigValue(ConfigInfo.get(ConfigInfo.CLIENT_PIN));
                et_config.setText(ConfigInfo.get(ConfigInfo.CLIENT_PIN));
                et_config.setSelection(et_config.getText().toString().length());
                return false;
            }
        } else if (ConfigInfo.REMIND_TIME_INTERVAL.equals(configOption.getConfigKey())) {
            if (TextUtils.isEmpty(configOption.getConfigValue())) {
                Toaster.makeToast(getContext(), Toaster.TOAST_REMIND, "消息提醒间隔不能为空，恢复原值", Toast.LENGTH_SHORT);
                configOption.setConfigValue(ConfigInfo.get(ConfigInfo.REMIND_TIME_INTERVAL));
                et_config.setText(ConfigInfo.get(ConfigInfo.REMIND_TIME_INTERVAL));
                et_config.setSelection(et_config.getText().toString().length());
                return false;
            }
        }else if(ConfigInfo.CLIENT_DPI.equals(configOption.getConfigKey())){
            //DPI 范围在160至480之间
            if (!configOption.getConfigValue().equals("0") && (TextUtils.isEmpty(configOption.getConfigValue())
                    || Integer.parseInt(configOption.getConfigValue())<160
                    || Integer.parseInt(configOption.getConfigValue()) > 480)) {
                Toaster.makeToast(getContext(), Toaster.TOAST_REMIND, "设置屏幕默认DPI，恢复原值", Toast.LENGTH_SHORT);
                configOption.setConfigValue("0");
                et_config.setText("0");
                FileUtils.writeDataToFile(configOption.getConfigValue(), FileUtils.filename);
                et_config.setSelection(et_config.getText().toString().length());
                return false;
            }
        } else if (ConfigInfo.NURSE_SHIFT_COLOR_SETTING.equals(configOption.getConfigKey())) {
            if (TextUtils.isEmpty(configOption.getConfigValue()) || configOption.getConfigValue().length() != 4) {
                //对色值进行限制，设置错误会提示
                String[] editValues = configOption.getConfigValue().split("\\*#\\*");
                String[] optionContents = configOption.getOptionsContent().split("\\*#\\*");
                for (int k = 0; k < editValues.length; k++) {
                    if (optionContents.length >= editValues.length) {
                        if (editValues[k].length() != 6 && editValues[k].length() != 8) {
                            Toaster.makeToast(getContext(), Toaster.TOAST_REMIND, "护士班次色值不规范，请重新输入", Toast.LENGTH_SHORT);
                            return false;
                        }
                    }
                }
            }
        } else if (ConfigInfo.NURSE_LEVEL_COLOR_SETTING.equals(configOption.getConfigKey())) {
            if (TextUtils.isEmpty(configOption.getConfigValue()) || configOption.getConfigValue().length() != 4) {
                //对色值进行限制，设置错误会提示
                String[] editValues = configOption.getConfigValue().split("\\*#\\*");
                String[] optionContents = configOption.getOptionsContent().split("\\*#\\*");
                for (int k = 0; k < editValues.length; k++) {
                    if (optionContents.length >= editValues.length) {
                        if (editValues[k].length() != 6 && editValues[k].length() != 8) {
                            Toaster.makeToast(getContext(), Toaster.TOAST_REMIND, "护士等级色值不规范，请重新输入", Toast.LENGTH_SHORT);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @OnClick({R.id.ib_out, R.id.ib_out2, R.id.btn_config, R.id.btn_change, R.id.btn_back, R.id.ib_information, R.id.btn_setface, R.id.btn_set_range_bed})
    public void cancelClick(View v) {
        String DPI="";
        if (v.getId() == R.id.ib_out || v.getId() == R.id.ib_out2) {
            this.dismiss();
        } else if (v.getId() == R.id.btn_config) {
            if (!isLegalConfig(curConfigOption)) {
                return;
            }
            if(ConfigInfo.CLIENT_DPI.equals(configOption.getConfigKey())){
                String client_dpi=configOption.getConfigValue();
                if(client_dpi=="0" && client_dpi==null && client_dpi=="") {
                    configOption.setConfigValue("0");
                    DPI="0";
                }else{
                    DPI=configOption.getConfigValue();
                }
            }
            List<ConfigOption> optionList = new ArrayList<>();
            for (int i = 0; i < configModelList.size(); i++) {
                optionList.addAll(configModelList.get(i).getOptions());
                if ("显示偏好".equals(configModelList.get(i).getItemName())) {
                    for (int j = 0; j < configModelList.get(i).getOptions().size(); j++) {
                         if ("锁屏界面轮播时间".equals(configModelList.get(i).getOptions().get(j).getConfigTag())) {
                            ConfigSingleton.getInstance().setIsForever("永不".equals(configModelList.get(i).getOptions().get(j).getConfigValue()));
                        }
                    }
                }else if("任务管理".equals(configModelList.get(i).getItemName())){
                    for (int j = 0; j < configModelList.get(i).getOptions().size(); j++) {
                        if ("护理任务显示顺序".equals(configModelList.get(i).getOptions().get(j).getConfigTag())) {
                            if ("按任务状态".equals(configModelList.get(i).getOptions().get(j).getConfigValue())) {
                                ConfigSingleton.getInstance().setIsFinishTask(true);
                            } else {
                                ConfigSingleton.getInstance().setIsGray("灰化任务不显示".equals(configModelList.get(i).getOptions().get(j).getConfigValue()));
                                ConfigSingleton.getInstance().setIsFinishTask(false);
                            }
                        }
                    }
                }
            }

            LoadingUtil.getInstance().show("数据保存中...");
            String finalDPI = DPI;
            NetApi.saveConfig(optionList, new MainThreadCallBack() {
                @Override
                protected void onSuccess(String code, Object o) {
                    if(finalDPI!="") {
                        FileUtils.writeDataToFile(finalDPI, FileUtils.filename);
                    }
                    LoadingUtil.getInstance().hide();
                    dismiss();
                    StatusManager.getInstance().setStatus(StatusConstants.STATUS_BACKSERVER_REFRESH, 1);
                }

                @Override
                protected void onFail(String code, String msg) {
                    onError(null);
                }

                @Override
                protected void onError(Throwable t) {
                    Toaster.makeToast(getContext(), Toaster.TOAST_FAILED, "保存失败！", Toast.LENGTH_LONG);
                    LoadingUtil.getInstance().hide();
                }
            });
        } else if (v.getId() == R.id.btn_change) {
            dismiss();
            if (mOnButtonClickListener != null) {
                mOnButtonClickListener.onNetShow();
            }
        } else if (v.getId() == R.id.btn_back) {
            rl_information.setVisibility(View.GONE);
            rl_settings.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.ib_information) {
            EditionDialog dialog = new EditionDialog(ConfigSingleton.getInstance().getCurrentContext());
            dialog.show();
            dismiss();
        } else if (v.getId() == R.id.btn_setface) {
            if (ArcFaceSingleton.getInstance().getMFaceDB() == null) {
                Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "摄像头不可用", Toast.LENGTH_SHORT);
                return;
            }
            if (!"科室PIN解锁".equals(ConfigSingleton.getInstance().getCurrentNurseModel().getUserName())) {
                /*if (setFaceDialog == null) {
                    setFaceDialog = new SetFaceDialog.Builder(ConfigSingleton.getInstance().getCurrentContext()).create();
                }
                setFaceDialog.show();*/
                //是个人登录，查看个人登录是不是已经有人脸
                NetApi.getPersonFace(ConfigSingleton.getInstance().getCurrentNurseModel().getUserId(), new ResponseCallBack<String>() {

                    @Override
                    protected void onSuccess(String code, String s) {
                        if (s == null) {
                            openFacePersonSettingDialog();
                        } else {
                            dealByteToPhoto(s);
                        }
                    }

                    @Override
                    protected void onFail(String code, String msg) {
                        openFacePersonSettingDialog();
                    }

                    @Override
                    protected void onError(Throwable t) {
                        openFacePersonSettingDialog();
                    }
                });
            } else {
                /*if (passwordLoginDialog == null) {
                    passwordLoginDialog = PasswordLoginDialog.builder()
                            .context(mContext).loginCallBack(new ILoginCallBack() {
                                @Override
                                public void onPinLoginSuccess() {

                                }

                                @Override
                                public void onPasswordLoginSuccess() {
                                    MessageUtil.sendMessage(MainActivity.handler, YzsConstants.BLUETOOTH_LOGIN, null, 0, 0);
                                    SetFaceDialog setFaceDialog = new SetFaceDialog.Builder(ConfigSingleton.getInstance().getCurrentContext()).create();
                                    setFaceDialog.show();
                                }

                                @Override
                                public void onQrcodeLoginSuccess(XMPPLoginMessageModel model) {

                                }

                                @Override
                                public void onFaceRecognitionLoginSuccess() {

                                }
                            }).build();
                }
                passwordLoginDialog.show();*/
                openFaceSettingDialog();
                Toaster.makeToast(ConfigSingleton.getInstance().getCurrentContext(), Toaster.TOAST_REMIND, "请先登录", Toast.LENGTH_SHORT);
            }
        } else if (v.getId() == R.id.btn_set_range_bed) {
//            BedRangeDialog bedRangeDialog = new BedRangeDialog(mContext);
            BedSettngDialog bedSettngDialog = new BedSettngDialog(mContext);
            bedSettngDialog.show();
        }
    }

    private void openFaceSettingDialog() {
        dismiss();
        if (faceSettingDialog == null) {
            faceSettingDialog = new FaceSettingDialog(mContext);
        } else {
            faceSettingDialog.initView();
        }
        faceSettingDialog.show();
    }

    private void openFacePersonSettingDialog() {
        dismiss();
        if (facePersonSettingDialog == null) {
            facePersonSettingDialog = new FacePersonSettingDialog(mContext, ConfigSingleton.getInstance().getCurrentNurseModel());
        } else {
            facePersonSettingDialog.setNurseModel(ConfigSingleton.getInstance().getCurrentNurseModel());
        }
        facePersonSettingDialog.show();
    }

    private void dealByteToPhoto(String bytes) {
        byte[] data = Base64.decode(bytes, Base64.DEFAULT);
        Bitmap bitmap = BitmapUtils.getPicFromBytes(data, null);
        if (bitmap != null) {
            if (facePersonInfoDialog == null) {
                facePersonInfoDialog = new FacePersonInfoDialog(mContext, bitmap, ConfigSingleton.getInstance().getCurrentNurseModel());
            } else {
                facePersonInfoDialog.setNurseData(bitmap, ConfigSingleton.getInstance().getCurrentNurseModel());
            }
            facePersonInfoDialog.show();
        } else {
            openFaceSettingDialog();
        }
    }

    @OnTouch({R.id.ib_out, R.id.ib_out2, R.id.btn_setface, R.id.btn_change, R.id.btn_config})
    public boolean touch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.ib_out:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        view.setBackgroundResource(R.drawable.button_task_up);
                        view.setPadding(17, 17, 17, 17);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        view.setBackgroundResource(R.drawable.bg_circle);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.setBackgroundResource(R.drawable.bg_circle);
                        break;
                }
                break;
            case R.id.ib_out2:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        view.setBackgroundResource(R.drawable.button_task_up);
                        view.setPadding(17, 17, 17, 17);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        view.setBackgroundResource(R.drawable.bg_circle);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.setBackgroundResource(R.drawable.bg_circle);
                        break;
                }
                break;
            case R.id.btn_setface:
            case R.id.btn_change:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackgroundResource(R.drawable.bg_remind_yellow);
                        ((Button) view).setTextColor(mContext.getResources().getColor(R.color.white));
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setBackgroundResource(R.drawable.bg_rec_yellow);
                        ((Button) view).setTextColor(mContext.getResources().getColor(R.color.list_selected));
                        break;
                }
                break;
            case R.id.btn_config:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        view.setBackgroundResource(R.drawable.bg_remind_yellow);
                        ((Button) view).setTextColor(mContext.getResources().getColor(R.color.white));
                        break;
                    case MotionEvent.ACTION_DOWN:
                        view.setBackgroundResource(R.drawable.bg_rec_yellow);
                        ((Button) view).setTextColor(mContext.getResources().getColor(R.color.list_selected));
                        break;
                }
                break;
        }
        return false;
    }

    public interface OnButtonClickListener {
        void onNetShow();
    }
}
