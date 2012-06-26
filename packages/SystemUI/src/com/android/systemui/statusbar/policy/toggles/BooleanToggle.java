package com.android.systemui.statusbar.policy.toggles;

import android.content.Context;
import android.util.Log;

public abstract class BooleanToggle extends Toggle {
	
	public BooleanToggle(Context c) {
        
		super(c);
		
		this.availableStates = 2;
		this.state = 0;

    }

	@Override
	protected void onCheckChanged() {
		this.onCheckChanged(this.state == 1);
	}
	
	protected void setChecked(boolean checked) {
		Log.e("BooleanToggle", "BooleanToggle " + (checked ? "enabled" : "disabled"));
		this.state = checked ? 1 : 0;
	}
	
	protected abstract void onCheckChanged(boolean isChanged);
	
	protected boolean isChecked() {
		return this.state == 1;
	}

}
