/**
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.biometrics;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Slog;
import android.hardware.display.ColorDisplayManager;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Slog;
import android.view.View;

import com.android.systemui.SystemUI;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;

import com.android.internal.util.custom.FodUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.provider.Settings;
import com.android.internal.R;

@Singleton
public class FODCircleViewImpl extends SystemUI implements CommandQueue.Callbacks {
    private static final String TAG = "FODCircleViewImpl";

    private FODCircleView mFodCircleView;
    private final CommandQueue mCommandQueue;
    private boolean mDisableNightMode;
    private boolean mNightModeActive;
    private int mAutoModeState;
    private final boolean mIsEnabled;

    @Inject
    public FODCircleViewImpl(Context context, CommandQueue commandQueue) {
        super(context);
        mCommandQueue = commandQueue;
        mIsEnabled = context.getResources().getBoolean(com.android.internal.R.bool.config_supportsInDisplayFingerprint);
    }

    @Override
    public void start() {
        if (!mIsEnabled) return;
        PackageManager packageManager = mContext.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) ||
                !FodUtils.hasFodSupport(mContext)) {
            return;
        }
        mCommandQueue.addCallback(this);
        try {
            mFodCircleView = new FODCircleView(mContext);
        } catch (RuntimeException e) {
            Slog.e(TAG, "Failed to initialize FODCircleView", e);
        }
        mDisableNightMode = mContext.getResources().getBoolean(R.bool.disable_fod_night_light);
    }

    @Override
    public void showInDisplayFingerprintView() {
        if (mFodCircleView != null) {
            if (isNightLightEnabled()) {
                disableNightMode();
            }
            mFodCircleView.show();
        }
    }

    private boolean isNightLightEnabled() {
       return Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.FOD_NIGHT_LIGHT, mDisableNightMode ? 1 : 0) == 1;
    }

    @Override
    public void hideInDisplayFingerprintView() {
        if (mFodCircleView != null) {
            if (isNightLightEnabled()) {
                setNightMode(mNightModeActive, mAutoModeState);
            }
            mFodCircleView.hide();
        }
    }

    private void disableNightMode() {
        ColorDisplayManager colorDisplayManager = mContext.getSystemService(ColorDisplayManager.class);
        mAutoModeState = colorDisplayManager.getNightDisplayAutoMode();
        mNightModeActive = colorDisplayManager.isNightDisplayActivated();
        colorDisplayManager.setNightDisplayActivated(false);
    }

    private void setNightMode(boolean activated, int autoMode) {
        ColorDisplayManager colorDisplayManager = mContext.getSystemService(ColorDisplayManager.class);
        colorDisplayManager.setNightDisplayAutoMode(0);
        if (autoMode == 0) {
            colorDisplayManager.setNightDisplayActivated(activated);
        } else if (autoMode == 1 || autoMode == 2) {
            colorDisplayManager.setNightDisplayAutoMode(autoMode);
        }
    }
}
