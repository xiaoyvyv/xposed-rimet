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

package com.sky.xposed.rimet.plugin;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import com.sky.xposed.common.util.Alog;
import com.sky.xposed.common.util.ToastUtil;
import com.sky.xposed.rimet.BuildConfig;
import com.sky.xposed.rimet.Constant;
import com.sky.xposed.rimet.data.ConfigManager;
import com.sky.xposed.rimet.data.ResourceManager;
import com.sky.xposed.rimet.data.VersionManager;
import com.sky.xposed.rimet.data.cache.CacheManager;
import com.sky.xposed.rimet.data.cache.ICacheManager;
import com.sky.xposed.rimet.data.source.IRepositoryFactory;
import com.sky.xposed.rimet.data.source.RepositoryFactory;
import com.sky.xposed.rimet.plugin.develop.DevelopPlugin;
import com.sky.xposed.rimet.plugin.dingding.DingDingHandler;
import com.sky.xposed.rimet.plugin.dingding.DingDingPlugin;
import com.sky.xposed.rimet.plugin.interfaces.XConfigManager;
import com.sky.xposed.rimet.plugin.interfaces.XPlugin;
import com.sky.xposed.rimet.plugin.interfaces.XPluginManager;
import com.sky.xposed.rimet.plugin.interfaces.XResourceManager;
import com.sky.xposed.rimet.plugin.interfaces.XVersionManager;
import com.sky.xposed.rimet.plugin.main.SettingsPlugin;
import com.squareup.picasso.Picasso;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by sky on 2018/9/24.
 */
public class PluginManager implements XPluginManager {

    private static XPluginManager sXPluginManager;

    private Context mContext;
    private Handler mHandler;
    private XC_LoadPackage.LoadPackageParam mLoadPackageParam;
    private XVersionManager mVersionManager;
    private XResourceManager mResourceManager;
    private XConfigManager mConfigManager;
    private ICacheManager mCacheManager;
    private IRepositoryFactory mRepositoryFactory;
    private Map<Class, Object> mObjectMap = new HashMap<>();

    private SparseArray<XPlugin> mXPlugins = new SparseArray<>();

    private PluginManager(Build build) {
        mContext = build.mContext;
        mLoadPackageParam = build.mLoadPackageParam;

        // 调试开关
        Alog.setDebug(BuildConfig.DEBUG);

        // 创建缓存管理
        mCacheManager = new CacheManager(mContext);

        // 创建配置管理对象
        mConfigManager = new ConfigManager
                .Build(this)
                .setConfigName(Constant.Name.RIMET)
                .build();

        // 获取版本管理对象
        mVersionManager = new VersionManager
                .Build(mContext)
                .setConfigManager(mConfigManager)
                .setCacheManager(mCacheManager)
                .build();

        mHandler = new PluginHandler();
        ToastUtil.getInstance().init(mContext);
        Picasso.setSingletonInstance(new Picasso.Builder(mContext).build());

        // 个别需要静态引用
        sXPluginManager = this;
    }

    public static XPluginManager getInstance() {
        return sXPluginManager;
    }

    /**
     * 开始处理需要Hook的插件
     */
    @Override
    public void handleLoadPackage() {

        if (mXPlugins.size() != 0) {
            Alog.d("暂时不需要处理加载的包!");
            return;
        }

        // 加载插件
        loadPlugin();

        for (int i = 0; i < mXPlugins.size(); i++) {
            // 开始处理
            handleLoadPackage(mXPlugins.get(mXPlugins.keyAt(i)));
        }
    }

    @Override
    public void release() {
        mXPlugins.clear();
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public XC_LoadPackage.LoadPackageParam getLoadPackageParam() {
        return mLoadPackageParam;
    }

    @Override
    public XVersionManager getVersionManager() {
        return mVersionManager;
    }

    @Override
    public XConfigManager getConfigManager() {
        return mConfigManager;
    }

    @Override
    public XResourceManager getResourceManager() {
        if (mResourceManager == null) {
            mResourceManager = new ResourceManager
                    .Build(this)
                    .build();
        }
        return mResourceManager;
    }

    @Override
    public IRepositoryFactory getRepositoryFactory() {
        if (mRepositoryFactory == null) {
            mRepositoryFactory = new RepositoryFactory(mConfigManager, mCacheManager);
        }
        return mRepositoryFactory;
    }

    /**
     * 获取所有插件
     * @return
     */
    @Override
    public List<XPlugin> getXPlugins(int flag) {

        List<XPlugin> xPlugins = new ArrayList<>();

        for (int i = 0; i < mXPlugins.size(); i++) {

            XPlugin xPlugin = mXPlugins.get(mXPlugins.keyAt(i));

            if ((xPlugin.getInfo().getId() & flag) > 0) {
                // 添加到列表中
                xPlugins.add(xPlugin);
            }
        }
        return xPlugins;
    }

    @Override
    public XPlugin getXPlugin(XPlugin.Info info) {
        return getXPluginById(info.getId());
    }

    @Override
    public XPlugin getXPluginById(int id) {
        return mXPlugins.get(id);
    }

    @Override
    public <T> T getObject(Class<T> tClass) {

        if (mObjectMap.containsKey(tClass)) {
            return (T) mObjectMap.get(tClass);
        }

        try {
            // 创建实例
            T result = tClass.getConstructor(
                    XPluginManager.class).newInstance(this);
            // 保存实例
            mObjectMap.put(tClass, result);
            return result;
        } catch (Throwable tr) {
            Alog.e("创建版本配置异常", tr);
        }
        return null;
    }

    /**
     * 开始处理需要Hook的插件
     * @param xPlugin
     */
    private void handleLoadPackage(XPlugin xPlugin) {

        if (!xPlugin.isHandler()) return;

        try {
            // 初始化
            xPlugin.initialization();
            // 处理
            xPlugin.onHandleLoadPackage();
        } catch (Throwable tr) {
            Alog.e("handleLoadPackage异常", tr);
        }
    }

    /**
     * 加载需要处理的插件
     */
    private void loadPlugin() {

        addPlugin(new SettingsPlugin(this));
        addPlugin(new DingDingPlugin
                .Build(this)
                .setHandler(new DingDingHandler(this))
                .build());

        if (BuildConfig.DEBUG) {
            addPlugin(new DevelopPlugin(this));
        }
    }

    /**
     * 添加到插件到列表中
     * @param xPlugin
     */
    private void addPlugin(XPlugin xPlugin) {
        mXPlugins.append(xPlugin.getInfo().getId(), xPlugin);
    }

    private final class PluginHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    public static class Build {

        private Context mContext;
        private XC_LoadPackage.LoadPackageParam mLoadPackageParam;

        public Build(Context context) {
            mContext = context;
        }

        public Build setLoadPackageParam(XC_LoadPackage.LoadPackageParam loadPackageParam) {
            mLoadPackageParam = loadPackageParam;
            return this;
        }

        public XPluginManager build() {
            return new PluginManager(this);
        }
    }
}
