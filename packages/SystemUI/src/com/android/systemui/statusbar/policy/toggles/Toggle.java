/*
 * Copyright (C) 2010 The Android Open Source Project
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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.R;

/**
 * TODO: Listen for changes to the setting.
 */
public abstract class Toggle implements OnClickListener {

    protected static final String TAG = "Toggle";

    View mView;
    protected Context mContext;

    // widgets
    protected ImageView mIcon;
    protected TextView mText;
    protected View mToggle;
    protected ImageView mLeft, mMid, mRight;

    protected boolean mSystemChange = false;
    final boolean useAltButtonLayout;
    final int defaultColor;
    final int defaultBg;
    
    protected int state = 0;
    private int availableStates;

    public Toggle(Context context) {
        mContext = context;

        useAltButtonLayout = Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.STATUSBAR_TOGGLES_USE_BUTTONS, 0) == 1;

        float[] hsv = new float[3];
        int color = context.getResources().getColor(
                com.android.internal.R.color.holo_blue_light);
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.7f; // value component
        defaultColor = Color.HSVToColor(hsv);
        
        defaultBg = context.getResources().getColor(com.android.internal.R.color.holo_blue_dark);
        
        this.init();
        this.availableStates = this.getAvailableStates();

        if(this.availableStates < 3) {
        
	        mView = View.inflate(mContext,
	                useAltButtonLayout ? R.layout.toggle_button : R.layout.toggle,
	                null);
	        
        } else {
        	
        	mView = View.inflate(mContext, 
        			useAltButtonLayout ? R.layout.toggle_button_multi : R.layout.toggle_multi, 
					null);
        	
        	mLeft = (ImageView)mView.findViewById(R.id.left);
        	mMid = (ImageView)mView.findViewById(R.id.mid);
        	mRight = (ImageView)mView.findViewById(R.id.right);
        	
        }
        
        mIcon = (ImageView) mView.findViewById(R.id.icon);
        mToggle = mView.findViewById(R.id.toggle);
        mText = (TextView) mView.findViewById(R.id.label);
        
        mToggle.setOnClickListener(this);
        mToggle.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onLongPress()) {
                    collapseStatusBar();
                    return true;
                } else
                    return false;
            }
        });

    }

    public void updateDrawable(boolean toggle) {
    	
    	if(this.availableStates < 3) {
	        
    		if (!useAltButtonLayout)
	            return;
	
	        Drawable bg = mContext.getResources().getDrawable(
	                toggle ? R.drawable.btn_on : R.drawable.btn_off);
	        if (toggle)
	            bg.setColorFilter(defaultColor, PorterDuff.Mode.SRC_ATOP);
	        else
	            bg.setColorFilter(null);
	        mToggle.setBackgroundDrawable(bg);
	        
    	} else {
    		
//    		int maxWeight = this.availableStates - 2;
    		
    		if(this.state == 0) {
    			
    			Log.e(TAG, "Left");
    			
    			mLeft.setImageResource(android.R.color.holo_blue_light);
    			mMid.setImageResource(android.R.color.darker_gray);
    			mRight.setImageResource(android.R.color.darker_gray);
    			
    		} else
			if(this.state == this.availableStates - 1) {
				
				Log.e(TAG, "Right");
				
				mLeft.setImageResource(android.R.color.darker_gray);
    			mMid.setImageResource(android.R.color.darker_gray);
    			mRight.setImageResource(android.R.color.holo_blue_light);
				
			} else {
				
				Log.e(TAG, "Middle");
				
				mLeft.setImageResource(android.R.color.darker_gray);
    			mMid.setImageResource(android.R.color.holo_blue_light);
    			mRight.setImageResource(android.R.color.darker_gray);
    			
			}
    		
    	}
    }

    /**
     * this method is called when we need to update the state of the toggle due
     * to outside interactions.
     * 
     * @return returns the on/off state of the toggle
     */
    protected abstract boolean updateInternalToggleState();

    /**
     * this method is called when the user manually toggles, update states as
     * needed
     */
    protected abstract void onCheckChanged();

    /**
     * this method is called when the user longpresses the toggle
     */
    protected boolean onLongPress() { 
    	/* Default implementation does nothing */
    	return false;
    }
    
    /**
     * This method is called from the constructor to allow subclasses
     * to load settings and configure themselves before being asked
     * how many states they have
     */
    protected void init() {
    	/* Default implementation does nothing */
    }
    
    /**
     * This method is called from the constructor to determine
     * how many available states an implementation has
     */
    protected abstract int getAvailableStates();

    public void updateState() {
        mSystemChange = true;
        updateDrawable(updateInternalToggleState());
        mSystemChange = false;
    }

    public void collapseStatusBar() {
        try {
            IStatusBarService sb = IStatusBarService.Stub
                    .asInterface(ServiceManager
                            .getService(Context.STATUS_BAR_SERVICE));
            sb.collapse();
        } catch (RemoteException e) {
        }
    }

    @Override
    public final void onClick(View v) {
    	
    	if(mSystemChange)
    		return;
    	
    	this.state = ++this.state % this.availableStates;
    	this.onCheckChanged();
    	
    }

    public View getView() {
        return mView;
    }

    public void setLabel(String s) {
        if (mText != null)
            mText.setText(s);
    }

    public void setLabel(int res) {
        if (mText != null)
            mText.setText(res);
    }

    public void setIcon(int res) {
        if (mIcon != null) {
            mIcon.setImageResource(res);
        }
    }

    public void setupInfo(boolean showIcon, boolean showText) {
        mIcon.setVisibility(showIcon ? View.VISIBLE : View.GONE);
        mText.setVisibility(showText ? View.VISIBLE : View.GONE);
    }

    protected void onStatusbarExpanded() {
    }
}
