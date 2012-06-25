package com.android.systemui.statusbar.policy.toggles;

import android.content.Context;

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
		
	}
	
	protected abstract void onCheckChanged(boolean isChanged);
	
	protected boolean isChecked() {
		return this.state == 1;
	}

}
