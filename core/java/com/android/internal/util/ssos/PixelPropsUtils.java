/*
 * Copyright (C) 2020 The Pixel Experience Project
 *               2021-2022 crDroid Android Project
 *               2019-2022 Evolution X
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.internal.util.ssos;

import android.app.Application;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PixelPropsUtils {

    public static final String PACKAGE_GMS = "com.google.android.gms";
    public static final String PACKAGE_SETTINGS_SERVICES = "com.google.android.settings.intelligence";
    private static final String DEVICE = "org.havoc.device";
    private static final String TAG = PixelPropsUtils.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static boolean isPixelDevice = false;
    private static final Map<String, Object> propsToChange;
    private static final Map<String, Object> propsToChangePixel7Pro;
    private static final Map<String, Object> propsToChangePixel5;
    private static final Map<String, Object> propsToChangePixelXL;
    private static final Map<String, Object> propsToChangeROG1;
    private static final Map<String, Object> propsToChangeXP5;
    private static final Map<String, Object> propsToChangeOnePlus9Pro;
    private static final Map<String, Object> propsToChangeMI10;
    private static final Map<String, Object> propsToChangeMI11;
    private static final Map<String, ArrayList<String>> propsToKeep;

    private static final String[] packagesToChangePixel7Pro = {
            "com.google.android.apps.customization.pixel",
            "com.google.android.apps.nexuslauncher",
            "com.google.android.apps.subscriptions.red",
            "com.google.android.apps.wallpaper",
            "com.google.android.apps.wallpaper.pixel",
            "com.google.pixel.dynamicwallpapers",
            "com.google.pixel.livewallpaper",
    };

    private static final String[] packagesToChangePixelXL = {
            PACKAGE_GMS,
            "com.google.android.inputmethod.latin",
            "com.google.android.apps.photos",
            "com.samsung.accessory",
            "com.samsung.accessory.fridaymgr",
            "com.samsung.accessory.berrymgr",
            "com.samsung.accessory.neobeanmgr",
            "com.samsung.android.app.watchmanager",
            "com.samsung.android.geargplugin",
            "com.samsung.android.gearnplugin",
            "com.samsung.android.modenplugin",
            "com.samsung.android.neatplugin",
            "com.samsung.android.waterplugin"
    };

    private static final String[] extraPackagesToChange = {
            "com.android.chrome",
            "com.android.vending",
            "com.breel.wallpapers20",
            "com.nhs.online.nhsonline"
    };

    private static final String[] packagesToKeep = {
            "com.google.android.GoogleCamera",
            "com.google.android.GoogleCamera.Cameight",
            "com.google.android.GoogleCamera.Go",
            "com.google.android.GoogleCamera.Urnyx",
            "com.google.android.GoogleCameraAsp",
            "com.google.android.GoogleCameraCVM",
            "com.google.android.GoogleCameraEng",
            "com.google.android.GoogleCameraEng2",
            "com.google.android.GoogleCameraGood",
            "com.google.android.MTCL83",
            "com.google.android.UltraCVM",
            "com.google.android.apps.cameralite",
            "com.google.android.apps.youtube.kids",
            "com.google.android.apps.youtube.music",
            "com.google.android.dialer",
            "com.google.android.youtube",
            "com.google.ar.core"
    };

    private static final String[] packagesToChangeROG1 = {
            "com.dts.freefireth",
            "com.dts.freefiremax",
            "com.madfingergames.legends"
    };

    private static final String[] packagesToChangeXP5 = {
            "com.activision.callofduty.shooter",
            "com.tencent.tmgp.kr.codm",
            "com.garena.game.codm",
            "com.vng.codmvn"
    };

    private static final String[] packagesToChangeOnePlus9Pro = {
            "com.tencent.ig",
            "com.pubg.imobile",
            "com.pubg.krmobile",
            "com.pubg.newstate",
            "com.vng.pubgmobile",
            "com.rekoo.pubgm",
            "com.tencent.tmgp.pubgmhd",
            "com.riotgames.league.wildrift",
            "com.riotgames.league.wildrifttw",
            "com.riotgames.league.wildriftvn",
            "com.netease.lztgglobal",
	    "com.google.android.apps.wearables.maestro.companion"
    };

    private static final String[] packagesToChangeMI10 = {
            "com.mobile.legends"
    };

    private static final String[] packagesToChangeMI11 = {
            "com.ea.gp.apexlegendsmobilefps",
            "com.epicgames.fortnite",
            "com.tencent.tmgp.sgame"
    };

    // Codenames for currently supported Pixels by Google
    private static final String[] pixelCodenames = {
            "oriole",
            "raven",
            "redfin",
            "barbet",
            "bramble",
            "sunfish",
            "coral",
            "flame",
            "bonito",
            "sargo"
    };

    private static volatile boolean sIsGms = false;
    private static volatile boolean sIsFinsky = false;

    static {
        propsToKeep = new HashMap<>();
        propsToKeep.put(PACKAGE_SETTINGS_SERVICES, new ArrayList<>(Collections.singletonList("FINGERPRINT")));
        propsToChange = new HashMap<>();
        propsToChangePixel7Pro = new HashMap<>();
        propsToChangePixel7Pro.put("BRAND", "google");
        propsToChangePixel7Pro.put("MANUFACTURER", "Google");
        propsToChangePixel7Pro.put("DEVICE", "cheetah");
        propsToChangePixel7Pro.put("PRODUCT", "cheetah");
        propsToChangePixel7Pro.put("MODEL", "Pixel 7 Pro");
        propsToChangePixel7Pro.put("FINGERPRINT", "google/cheetah/cheetah:13/TQ2A.230505.002/9891397:user/release-keys");
        propsToChangePixel5 = new HashMap<>();
        propsToChangePixel5.put("BRAND", "google");
        propsToChangePixel5.put("MANUFACTURER", "Google");
        propsToChangePixel5.put("DEVICE", "redfin");
        propsToChangePixel5.put("PRODUCT", "redfin");
        propsToChangePixel5.put("MODEL", "Pixel 5");
        propsToChangePixel5.put("FINGERPRINT", "google/redfin/redfin:13/TQ2A.230505.002/9891397:user/release-keys");
        propsToChangePixelXL = new HashMap<>();
        propsToChangePixelXL.put("BRAND", "google");
        propsToChangePixelXL.put("MANUFACTURER", "Google");
        propsToChangePixelXL.put("DEVICE", "marlin");
        propsToChangePixelXL.put("PRODUCT", "marlin");
        propsToChangePixelXL.put("MODEL", "Pixel XL");
        propsToChangePixelXL.put("FINGERPRINT", "google/marlin/marlin:10/QP1A.191005.007.A3/5972272:user/release-keys");
        propsToChangeROG1 = new HashMap<>();
        propsToChangeROG1.put("MODEL", "ASUS_Z01QD");
        propsToChangeROG1.put("MANUFACTURER", "asus");
        propsToChangeXP5 = new HashMap<>();
        propsToChangeXP5.put("MODEL", "SO-52A");
        propsToChangeOnePlus9Pro = new HashMap<>();
        propsToChangeOnePlus9Pro.put("BRAND", "OnePlus");
        propsToChangeOnePlus9Pro.put("MANUFACTURER", "OnePlus");
        propsToChangeOnePlus9Pro.put("DEVICE", "OnePlus9Pro");
        propsToChangeOnePlus9Pro.put("PRODUCT", "OnePlus9Pro_EEA");
        propsToChangeOnePlus9Pro.put("MODEL", "LE2123");
        propsToChangeOnePlus9Pro.put("FINGERPRINT", "OnePlus/OnePlus9Pro_EEA/OnePlus9Pro:11/RKQ1.201105.002/2107082109:user/release-keys");
        propsToChangeMI10 = new HashMap<>();
        propsToChangeMI10.put("BRAND", "Xiaomi");
        propsToChangeMI10.put("MANUFACTURER", "Xiaomi");
        propsToChangeMI10.put("MODEL", "Mi 10 Pro");
        propsToChangeMI11 = new HashMap<>();
        propsToChangeMI11.put("BRAND", "Xiaomi");
        propsToChangeMI11.put("MANUFACTURER", "Xiaomi");
        propsToChangeMI11.put("DEVICE", "star");
        propsToChangeMI11.put("PRODUCT", "star");
        propsToChangeMI11.put("MODEL", "M2102K1G");
        isPixelDevice = Arrays.asList(pixelCodenames).contains(SystemProperties.get(DEVICE));
    }

    public static void setProps(String packageName) {
        if (packageName == null || (Arrays.asList(packagesToKeep).contains(packageName)) || isPixelDevice) {
            return;
        }
        if (Arrays.asList(packagesToKeep).contains(packageName)) {
            return;
        }
        if (packageName.startsWith("com.google.")
                || Arrays.asList(extraPackagesToChange).contains(packageName)) {

            boolean isPixelDevice = Arrays.asList(pixelCodenames).contains(SystemProperties.get(DEVICE));

            if (packageName.equals("com.google.android.apps.photos")) {
                propsToChange.putAll(propsToChangePixelXL);
            } else if (packageName.equals("com.android.vending")) {
                sIsFinsky = true;
                return;
            } else if (!isPixelDevice) {
                if (Arrays.asList(packagesToChangePixel7Pro).contains(packageName)) {
                    propsToChange.putAll(propsToChangePixel7Pro);
                } else if (Arrays.asList(packagesToChangePixelXL).contains(packageName)) {
                    propsToChange.putAll(propsToChangePixelXL);
                } else {
                    propsToChange.putAll(propsToChangePixel5);
                }
            }

            if (DEBUG)
                Log.d(TAG, "Defining props for: " + packageName);
            for (Map.Entry<String, Object> prop : propsToChange.entrySet()) {
                String key = prop.getKey();
                Object value = prop.getValue();
                if (propsToKeep.containsKey(packageName) && propsToKeep.get(packageName).contains(key)) {
                    if (DEBUG)
                        Log.d(TAG, "Not defining " + key + " prop for: " + packageName);
                    continue;
                }
                if (DEBUG)
                    Log.d(TAG, "Defining " + key + " prop for: " + packageName);
                setPropValue(key, value);
            }
            if (packageName.equals(PACKAGE_GMS)) {
                final String processName = Application.getProcessName();
                if (processName.equals("com.google.android.gms.unstable")) {
                    sIsGms = true;
                    setPropValue("FINGERPRINT", "google/marlin/marlin:7.1.2/NJH47F/4146041:user/release-keys");
                    setPropValue("PRODUCT", "marlin");
                    setPropValue("DEVICE", "marlin");
                    setPropValue("MODEL", "Pixel XL");
                    setVersionField("DEVICE_INITIAL_SDK_INT", Build.VERSION_CODES.N_MR1);
                }
            }
            if (isPixelDevice) {
                if (packageName.equals(PACKAGE_GMS)) {
                    final String processName = Application.getProcessName();
                    if (processName.equals("com.google.android.gms.unstable")) {
                        setPropValue("MODEL", Build.MODEL + " ");
                    }
                }
            }
            // Set proper indexing fingerprint
            if (packageName.equals(PACKAGE_SETTINGS_SERVICES)) {
                setPropValue("FINGERPRINT", Build.VERSION.INCREMENTAL);
            }
        } else {
            if (Arrays.asList(packagesToChangeROG1).contains(packageName)) {
                if (DEBUG)
                    Log.d(TAG, "Defining props for: " + packageName);
                for (Map.Entry<String, Object> prop : propsToChangeROG1.entrySet()) {
                    String key = prop.getKey();
                    Object value = prop.getValue();
                    setPropValue(key, value);
                }
            } else if (Arrays.asList(packagesToChangeXP5).contains(packageName)) {
                if (DEBUG)
                    Log.d(TAG, "Defining props for: " + packageName);
                for (Map.Entry<String, Object> prop : propsToChangeXP5.entrySet()) {
                    String key = prop.getKey();
                    Object value = prop.getValue();
                    setPropValue(key, value);
                }
            } else if (Arrays.asList(packagesToChangeOnePlus9Pro).contains(packageName)) {
                if (DEBUG)
                    Log.d(TAG, "Defining props for: " + packageName);
                for (Map.Entry<String, Object> prop : propsToChangeOnePlus9Pro.entrySet()) {
                    String key = prop.getKey();
                    Object value = prop.getValue();
                    setPropValue(key, value);
                }
           } else if (Arrays.asList(packagesToChangeMI10).contains(packageName)) {
                if (DEBUG)
                    Log.d(TAG, "Defining props for: " + packageName);
                for (Map.Entry<String, Object> prop : propsToChangeMI10.entrySet()) {
                    String key = prop.getKey();
                    Object value = prop.getValue();
                    setPropValue(key, value);
                }
            } else if (Arrays.asList(packagesToChangeMI11).contains(packageName)) {
                if (DEBUG)
                    Log.d(TAG, "Defining props for: " + packageName);
                for (Map.Entry<String, Object> prop : propsToChangeMI11.entrySet()) {
                    String key = prop.getKey();
                    Object value = prop.getValue();
                    setPropValue(key, value);
                }
            }
        }
    }

    private static void setPropValue(String key, Object value) {
        try {
            if (DEBUG)
                Log.d(TAG, "Defining prop " + key + " to " + value.toString());
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to set prop " + key, e);
        }
    }

    private static void setVersionField(String key, Object value) {
        try {
            if (DEBUG) Log.d(TAG, "Defining version field " + key + " to " + value.toString());
            Field field = Build.VERSION.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to set version field " + key, e);
        }
    }

    private static boolean isCallerSafetyNet() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .anyMatch(elem -> elem.getClassName().contains("DroidGuard"));
    }

    public static void onEngineGetCertificateChain() {
        // Check stack for SafetyNet
        if (sIsGms && isCallerSafetyNet()) {
            throw new UnsupportedOperationException();
        }

        // Check stack for PlayIntegrity
        if (sIsFinsky) {
            throw new UnsupportedOperationException();
        }
    }
}
