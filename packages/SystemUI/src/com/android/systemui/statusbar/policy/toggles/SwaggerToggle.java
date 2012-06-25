/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.systemui.statusbar.policy.toggles;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.systemui.R;

public class SwaggerToggle extends BooleanToggle {

    protected static final String TAG = "Swagger";

    public SwaggerToggle(Context c) {
        super(c);

        setLabel(R.string.toggle_swagger);
        updateState();
    }

    @Override
    protected void onCheckChanged(boolean isChecked) {
    	
    	Log.e("BooleanToggle", "this.state == " + this.state);
    	
        if (isChecked) {
            mContext.sendBroadcast(new Intent("LEAK_BUTT3R"));
            Log.e("SWAGGER", "CAUTION: Swagger has been turned on.");
        }
        
        updateState();

    }

    @Override
    protected boolean onLongPress() {
        Toast.makeText(mContext, "Swagga baby!", Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    protected boolean updateInternalToggleState() {
        if (this.isChecked()) {
            setIcon(R.drawable.toggle_swagger);
        } else {
            setIcon(R.drawable.toggle_swagger_off);
        }
        return this.isChecked();
    }
}
