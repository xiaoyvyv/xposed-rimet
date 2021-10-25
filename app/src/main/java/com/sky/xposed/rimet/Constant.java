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

package com.sky.xposed.rimet;

/**
 * Created by sky on 2019/3/14.
 */
public interface Constant {
    String TAG = "DDHelper";

    interface Service {

        String BASE_URL = "https://coding.net/u/sky_wei/p/xposed-rimet/git/raw/develop/";
    }

    interface Rimet {

        String PACKAGE_NAME = "com.alibaba.android.rimet";
    }

    interface Event {

        int CLICK = 0x01;
    }

    interface Color {

        int BLUE = 0xFF393A3F;

        int TOOLBAR = 0xff303030;

        int TITLE = 0xff004198;

        int DESC = 0xff303030;
    }

    interface GroupId {

        int GROUP = 999;
    }

    interface Name {

        String TITLE = "钉钉助手";

        String RIMET = "rimet";
    }

    interface XFlag {

        int MAIN_MENU = 0x000001;

        int ENABLE_LUCKY = 0x000002;

        int LUCKY_DELAYED = 0x000003;

        int ENABLE_FAST_LUCKY = 0x000004;

        int ENABLE_RECALL = 0x000005;

        int ENABLE_LOCATION = 0x000006;

        int ADDRESS = 0x000007;

        int LATITUDE = 0x000008;

        int LONGITUDE = 0x000009;

        int UPDATE_LAST_TIME = 0x00000A;
    }

    interface Preference {

        String MAIN_MENU = "main.menu";

        String AUTO_LOGIN = "other.autoLogin";

        String ACTIVITY_CYCLE = "develop.activityCycle";

        String ACTIVITY_START = "develop.activityStart";

        String ACTIVITY_RESULT = "develop.activityResult";

        String WECHAT_LOG = "develop.wechatLog";
    }

    interface Flag {

        int MAIN = 0xFF000000;
    }

    interface Plugin {

        int MAIN_SETTINGS = 0x00000000;

        int DEBUG = 0x01000000;

        int DING_DING = 0x02000000;

        int LUCKY_MONEY = 0x03000000;

        int REMITTANCE = 0x04000000;
    }

    interface ItemId {

        int MAIN_SETTINGS = 60001;
    }
}
