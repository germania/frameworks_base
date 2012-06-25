package com.android.systemui.statusbar.policy.toggles;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class BrightnessModeToggle extends Toggle {

	private static final int[] BRIGHTNESS_LEVELS = { 0, 90, 180, 255, -1 };
	private int currentLevel = 0;
	
	public BrightnessModeToggle(Context context) {

		super(context);

		this.availableStates = BRIGHTNESS_LEVELS.length;

		SettingsObserver so = new SettingsObserver(new Handler());
        so.observe();

	}

	@Override
	protected boolean updateInternalToggleState() {

        int automatic;
        mSystemChange = true;
        
        try {
        	
            automatic = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            
            if(automatic != 0) {
            	
            	currentLevel = BRIGHTNESS_LEVELS.length - 1;
            	
            } else {
            	
	            int tmp = Settings.System.getInt(mContext.getContentResolver(),
	                Settings.System.SCREEN_BRIGHTNESS);
	            
// TODO this is pretty stupid
	            int curr = tmp;
	            for(int i=0; i<BRIGHTNESS_LEVELS.length; i++) {
	            	
	            	int n = BRIGHTNESS_LEVELS[i];
	            	
	            	if(Math.abs(n - tmp) < curr) {
	            		curr = n;
	            		this.state = i;
	            	}
	            	
	            }
	            
	            currentLevel = curr;
            }
            
            
        } catch (SettingNotFoundException e) {  
        	
        } finally {
        	mSystemChange = false;
        }
        
		return false;
	}

	@Override
	protected void onCheckChanged() {
		
		mSystemChange = true;
		
		try {
			
			int level = BRIGHTNESS_LEVELS[this.state];
			if(level == -1) { //automatic
				Settings.System.putInt(mContext.getContentResolver(),
	                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
			} else {
				Settings.System.putInt(mContext.getContentResolver(),
	                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
				Settings.System.putInt(mContext.getContentResolver(),
	                    Settings.System.SCREEN_BRIGHTNESS, level);
			}
			
		} finally {
			
			mSystemChange = false;
			
		}
		
	}

	// TODO copied from BrightnessSlider, they should probably use a shared class for this
	class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            ContentResolver resolver = mContext.getContentResolver();
            resolver.registerContentObserver(
                    Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, this);
            resolver.registerContentObserver(
                    Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE), false, this);
        }

        @Override
        public void onChange(boolean selfChange) {
            updateInternalToggleState();
        }
    }

}
