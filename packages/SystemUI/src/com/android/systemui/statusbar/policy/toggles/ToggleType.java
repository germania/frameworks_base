package com.android.systemui.statusbar.policy.toggles;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public enum ToggleType {
	T_ROTATE(AutoRotateToggle.class),
	T_BT(BluetoothToggle.class),
	T_GPS(GpsToggle.class),
	T_LTE(LteToggle.class),
	T_DATA(NetworkToggle.class),
	T_WIFI(WifiToggle.class),
	T_2G(TwoGToggle.class),
	T_AP(WifiAPToggle.class),
	T_AIRPLANE_MODE(AirplaneModeToggle.class),
	T_VIBRATE(VibrateToggle.class),
	T_SILENT(SilentToggle.class),
	T_TORCH(TorchToggle.class),
	T_SYNC(SyncToggle.class),
	T_SWAGGER(SwaggerToggle.class),
	T_FCHARGE(FChargeToggle.class),
	T_TETHER(USBTetherToggle.class),
	T_NFC(NFCToggle.class),
	T_SOUND_MODE(SoundVibSilentToggle.class),
	T_BRIGHT_MODE(BrightnessModeToggle.class);
	
	private static final String TOGGLE_DELIMITER = "|";
	private static final String TAG = "ToggleType";
	
	Class<? extends Toggle> clazz;

	ToggleType(Class<? extends Toggle> c) {
		this.clazz = c;
	}
	
	public Class<? extends Toggle> getToggleClass() {
		return clazz;
	}
	
	public String settingsName() {
		return this.name().substring(2);
	}
	
	public Toggle create(Context mContext) {
		try {
			Class<? extends Toggle> clazz = getToggleClass(); 
			Constructor<? extends Toggle> ctor = clazz.getConstructor(Context.class);
			return ctor.newInstance(mContext);
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String combine(ToggleType ... types) {
		StringBuffer result = new StringBuffer();
		boolean first = true;
		for(ToggleType type : types) {
			if(!first) result.append(TOGGLE_DELIMITER);
			result.append(type.settingsName());
		}
		return result.toString();
	}

	public static ArrayList<ToggleType> split(String toggles) {
		String[] tArr = toggles.split("\\" + TOGGLE_DELIMITER);
		ArrayList<ToggleType> result = new ArrayList<ToggleType>();

		for(String type : tArr) {
			ToggleType t = ToggleType.valueOf("T_" + type);
			if(t == null) {
				Log.e(TAG, "Skipping unknown toggle " + type);
				continue;
			}
			result.add(t);
		}

		return result;
	}

}
