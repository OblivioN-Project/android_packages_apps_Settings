/*
 * Copyright (C) 2015 The Android Open Source Project
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
package com.android.settings.applications;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.internal.logging.MetricsProto;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.ChooseLockSettingsHelper;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;

/* Class to prompt for conversion of userdata to file based encryption
 */
public class ConvertToFbe extends SettingsPreferenceFragment {
    static final String TAG = "ConvertToFBE";
    static final String CONVERT_FBE_EXTRA = "ConvertFBE";
    private static final int KEYGUARD_REQUEST = 55;

    private boolean runKeyguardConfirmation(int request) {
        Resources res = getActivity().getResources();
        return new ChooseLockSettingsHelper(getActivity(), this)
            .launchConfirmationActivity(request,
                                        res.getText(R.string.convert_to_file_encryption));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.convert_fbe, null);

        final Button button = (Button) rootView.findViewById(R.id.button_convert_fbe);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!runKeyguardConfirmation(KEYGUARD_REQUEST)) {
                    convert();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != KEYGUARD_REQUEST) {
            return;
        }

        // If the user entered a valid keyguard credential, start the conversion
        // process
        if (resultCode == Activity.RESULT_OK) {
            convert();
        }
    }

    private void convert() {
        SettingsActivity sa = (SettingsActivity) getActivity();
        sa.startPreferencePanel(ConfirmConvertToFbe.class.getName(), null,
                                R.string.convert_to_file_encryption, null, null, 0);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.CONVERT_FBE;
    }
}
