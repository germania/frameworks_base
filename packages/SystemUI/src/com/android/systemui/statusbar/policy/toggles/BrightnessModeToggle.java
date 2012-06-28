package com.android.systemui.statusbar.policy.toggles;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.ServiceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class BrightnessModeToggle extends Toggle {

	private static final int[] BRIGHTNESS_LEVELS = { 
		android.os.Power.BRIGHTNESS_DIM, 
		(android.os.Power.BRIGHTNESS_ON - android.os.Power.BRIGHTNESS_DIM) / 3, 
		((android.os.Power.BRIGHTNESS_ON - android.os.Power.BRIGHTNESS_DIM) / 3) * 2, 
		android.os.Power.BRIGHTNESS_ON,
		-1 
	};
	
	private IPowerManager mPower;
	
	public BrightnessModeToggle(Context context) {

		super(context);

		mPower = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
		
		SettingsObserver so = new SettingsObserver(new Handler());
        so.observe();

	}
	
	@Override
	protected int getAvailableStates() {
		return BRIGHTNESS_LEVELS.length;
	}

	@Override
	protected boolean updateInternalToggleState() {

        int automatic;
        mSystemChange = true;
        
        try {
        	
            automatic = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            
            if(automatic != 0) {
            	
            	this.state = BRIGHTNESS_LEVELS.length - 1;
            	
            } else {
            	
// TODO this is pretty stupid
	            
	            int tmp = Settings.System.getInt(mContext.getContentResolver(),
	                Settings.System.SCREEN_BRIGHTNESS);
	            
	            int curr = BRIGHTNESS_LEVELS.length;
	            
	            for(int i=0; i<BRIGHTNESS_LEVELS.length - 1; i++) {
	            	
	            	int n = BRIGHTNESS_LEVELS[i];
	            	
	            	if(Math.abs(n - tmp) < curr) {
	            		curr = i;
	            	}
	            	
	            }
	            
	            this.state = curr;
	            
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
				
				mPower.setBacklightBrightness(level);
				
			}
			
		} catch(Exception e) {
			
			throw new RuntimeException(e);
			
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
            updateState();
        }
    }

}
