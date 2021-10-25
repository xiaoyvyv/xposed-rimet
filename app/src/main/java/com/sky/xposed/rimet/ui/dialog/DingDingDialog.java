/*
 * Copyright (c) 2019 The sky Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sky.xposed.rimet.ui.dialog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sky.xposed.common.ui.util.ViewUtil;
import com.sky.xposed.common.ui.view.CommonFrameLayout;
import com.sky.xposed.common.ui.view.EditTextItemView;
import com.sky.xposed.common.ui.view.SimpleItemView;
import com.sky.xposed.common.ui.view.SwitchItemView;
import com.sky.xposed.common.util.Alog;
import com.sky.xposed.common.util.DisplayUtil;
import com.sky.xposed.rimet.BuildConfig;
import com.sky.xposed.rimet.Constant;
import com.sky.xposed.rimet.contract.RimetContract;
import com.sky.xposed.rimet.data.model.UpdateModel;
import com.sky.xposed.rimet.plugin.interfaces.XConfig;
import com.sky.xposed.rimet.plugin.interfaces.XPlugin;
import com.sky.xposed.rimet.plugin.interfaces.XPluginManager;
import com.sky.xposed.rimet.ui.activity.MapActivity;
import com.sky.xposed.rimet.ui.util.ActivityUtil;
import com.sky.xposed.rimet.ui.util.DialogUtil;

/**
 * Created by sky on 2019/3/13.
 */
public class DingDingDialog extends CommonDialog implements RimetContract.View {

    private TextView tvPrompt;
    private SwitchItemView sivLuckyEnable;
    private EditTextItemView sivLuckyDelayed;
    private SwitchItemView sivFastLuckyEnable;
    private SwitchItemView sivRecallEnable;
    private SwitchItemView sivLocationEnable;
    private SimpleItemView sivSettingsLocation;
    private SimpleItemView sivLove;
    private SimpleItemView sivAbout;

    @Override
    public void createView(CommonFrameLayout frameView) {

        int left = DisplayUtil.dip2px(getContext(), 15);
        int top = DisplayUtil.dip2px(getContext(), 12);

        tvPrompt = new TextView(getContext());
        tvPrompt.setTextSize(14);
        tvPrompt.setBackgroundColor(Color.GRAY);
        tvPrompt.setTextColor(Color.WHITE);
        tvPrompt.setPadding(left, top, left, top);

        sivLuckyEnable = ViewUtil.newSwitchItemView(getContext(), "自动接收红包");
        sivLuckyEnable.setDesc("开启时自动接收红包");

        sivLuckyDelayed = new EditTextItemView(getContext());
        sivLuckyDelayed.setInputType(com.sky.xposed.common.Constant.InputType.NUMBER_SIGNED);
        sivLuckyDelayed.setMaxLength(2);
        sivLuckyDelayed.setUnit("秒");
        sivLuckyDelayed.setName("红包延迟时间");
        sivLuckyDelayed.setExtendHint("单位(秒)");

        sivFastLuckyEnable = ViewUtil.newSwitchItemView(getContext(), "快速打开红包");
        sivFastLuckyEnable.setDesc("开启时快速打开红包");

        sivRecallEnable = ViewUtil.newSwitchItemView(getContext(), "消息防撤回");
        sivRecallEnable.setDesc("开启时消息不会被撤回");

        sivLocationEnable = ViewUtil.newSwitchItemView(getContext(), "虚拟定位(Beta)");
        sivLocationEnable.setDesc("开启时会修改当前位置信息");

        sivSettingsLocation = ViewUtil.newSimpleItemView(getContext(), "位置信息");
        sivSettingsLocation.setExtendHint("设置位置信息");

        sivLove = ViewUtil.newSimpleItemView(getContext(), "爱心公益");
        sivAbout = ViewUtil.newSimpleItemView(getContext(), "关于");

        frameView.addContent(tvPrompt);
        frameView.addContent(sivLuckyEnable);
        frameView.addContent(sivLuckyDelayed);
        frameView.addContent(sivFastLuckyEnable);
        frameView.addContent(sivRecallEnable);
        frameView.addContent(sivLocationEnable);
        frameView.addContent(sivSettingsLocation);

        frameView.addContent(sivLove);
        frameView.addContent(sivAbout);
    }

    @Override
    protected void initView(View view, Bundle args) {
        super.initView(view, args);

        // 创建对象
//        mRimetPresenter = new RimetPresenter(getPluginManager(), this);

        setTitle(Constant.Name.TITLE);

        TextView tvExt = sivSettingsLocation.getExtendView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tvExt.getLayoutParams();
        params.leftMargin = DisplayUtil.dip2px(getContext(), 100);
        tvExt.setMaxLines(2);
        tvExt.setEllipsize(TextUtils.TruncateAt.END);
        tvExt.setTextSize(12);

        SharedPreferences preferences = getDefaultSharedPreferences();
        XPluginManager pluginManager = getPluginManager();
        if (pluginManager == null) {
            Alog.e("XPluginManager == null!!!");
            return;
        }
        XPlugin xPlugin = pluginManager.getXPluginById(Constant.Plugin.DING_DING);

        if (xPlugin == null) {
            Alog.e("xPlugin:XPlugin");
            return;
        }
        sivLuckyEnable.bind(getDefaultSharedPreferences(),
                Integer.toString(Constant.XFlag.ENABLE_LUCKY), true,
                (view1, key, value) -> {
                    xPlugin.setEnable(Constant.XFlag.ENABLE_LUCKY, value);
                    return true;
                });

        sivLuckyDelayed.bind(getDefaultSharedPreferences(),
                Integer.toString(Constant.XFlag.LUCKY_DELAYED), "",
                (view12, key, value) -> true);

        sivFastLuckyEnable.bind(getDefaultSharedPreferences(),
                Integer.toString(Constant.XFlag.ENABLE_FAST_LUCKY), true,
                (view1, key, value) -> {
                    xPlugin.setEnable(Constant.XFlag.ENABLE_FAST_LUCKY, value);
                    return true;
                });

        sivRecallEnable.bind(getDefaultSharedPreferences(),
                Integer.toString(Constant.XFlag.ENABLE_RECALL), true,
                (view1, key, value) -> {
                    xPlugin.setEnable(Constant.XFlag.ENABLE_RECALL, value);
                    return true;
                });

        sivLocationEnable.bind(getDefaultSharedPreferences(),
                Integer.toString(Constant.XFlag.ENABLE_LOCATION), false,
                (view1, key, value) -> {
                    xPlugin.setEnable(Constant.XFlag.ENABLE_LOCATION, value);
                    return true;
                });

        // 设置初始信息
        sivSettingsLocation.setExtend(preferences.getString(
                Integer.toString(Constant.XFlag.ADDRESS), ""));
        sivSettingsLocation.setOnClickListener(v -> {
            // 跳转到地图界面
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClassName(BuildConfig.APPLICATION_ID, MapActivity.class.getName());
            startActivityForResult(intent, 99);
        });

        sivLove.setOnClickListener(v -> {
            // 打开捐赠界面
            LoveDialog loveDialog = new LoveDialog();
            loveDialog.show(getFragmentManager(), "love");
        });

        sivAbout.setOnClickListener(v -> {
            // 打开关于界面
            DialogUtil.showAboutDialog(getContext());
        });

        // 是否支持版本
        XConfig xConfig = pluginManager.getVersionManager().getSupportConfig();
        setPromptText(xConfig != null ? "" : "不支持当前版本!");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 99 && resultCode == Activity.RESULT_OK) {
            // 保存位置信息
            saveLocationInfo(
                    data.getStringExtra("address"),
                    data.getDoubleExtra("latitude", 0),
                    data.getDoubleExtra("longitude", 0));
        }
    }

    @Override
    public void onUpdate(UpdateModel model) {
        // 提示用户更新
        DialogUtil.showDialog(getContext(),
                "提示", "发现新的版本,是否更新? \n" + model.getDesc(),
                "更新", (dialog, which) -> {
                    // 跳转到浏览器安装
                    ActivityUtil.openUrl(getContext(), model.getUrl());
                },
                !model.isForce() ? "取消" : "", null, !model.isForce());
    }

    @Override
    public void onUpdateFailed(String msg) {
        showMessage(msg);
    }

    @Override
    public void onUpdateConfigSucceed() {
        setPromptText("配置更新成功,需要重启钉钉即可生效!");
    }

    @Override
    public void onUpdateConfigFailed(String msg) {
        showMessage(msg);
    }

    @Override
    public void onClearConfigSucceed() {
        showMessage("清除配置成功!");
    }

    @Override
    public void onClearConfigFailed(String msg) {
        showMessage(msg);
    }

    /**
     * 设置提示消息
     * @param text
     */
    private void setPromptText(String text) {
        tvPrompt.setText(text);
        ViewUtil.setVisibility(tvPrompt, TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    /**
     * 保存位置信息
     * @param address
     * @param latitude
     * @param longitude
     */
    private void saveLocationInfo(String address, double latitude, double longitude) {

        getDefaultSharedPreferences()
                .edit()
                .putString(Integer.toString(Constant.XFlag.ADDRESS), address)
                .putString(Integer.toString(Constant.XFlag.LATITUDE), Double.toString(latitude))
                .putString(Integer.toString(Constant.XFlag.LONGITUDE), Double.toString(longitude))
                .apply();

        // 设置UI信息
        sivSettingsLocation.setExtend(address);
    }
}
