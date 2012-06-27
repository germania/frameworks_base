package com.android.systemui.statusbar.policy.toggles;

import com.android.systemui.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

public class SoundVibSilentToggle extends Toggle {

	public SoundVibSilentToggle(Context c) {
		super(c);
		
        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        c.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                updateState();
            }
            
        }, filter);
		
	}
	
	@Override
	protected int getAvailableStates() {
		return 3;
	}

	@Override
	protected boolean updateInternalToggleState() {
		
		AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int mode = am.getRingerMode();
        switch(mode) {
	        case AudioManager.RINGER_MODE_NORMAL: 
	        	this.state = 0;
	        break;
	        case AudioManager.RINGER_MODE_VIBRATE: 
	        	this.state = 1;
	        break;
	        case AudioManager.RINGER_MODE_SILENT: 
	        	this.state = 2; 
	        break;
        }
        
        this.updateIcon();
        
        return state == 0;
		
	}

	@Override
	protected void onCheckChanged() {

		AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		
		switch(this.state) {
			case 0: am.setRingerMode(AudioManager.RINGER_MODE_NORMAL); break;
			case 1: am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE); break;
			case 2: am.setRingerMode(AudioManager.RINGER_MODE_SILENT); break;
		}
		
		this.updateIcon();

	}
	
	private void updateIcon() {
		switch(this.state) {
			case 0: 
				setIcon(R.drawable.toggle_silence_off);
			break;
			case 1: 
				setIcon(R.drawable.toggle_vibrate);
			break;
			case 2: 
				setIcon(R.drawable.toggle_silence);
			break;
	    }
	}

}
