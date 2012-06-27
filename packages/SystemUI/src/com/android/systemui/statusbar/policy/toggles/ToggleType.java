package com.android.systemui.statusbar.policy.toggles;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.provider.Settings;
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
	private static final String PREFIX = "T_";
	
	public static final ToggleType[] STOCK_TOGGLES_ARR = 
		{ ToggleType.T_WIFI, ToggleType.T_BT, ToggleType.T_GPS, ToggleType.T_ROTATE };
	public static final String STOCK_TOGGLES = ToggleType.combine(STOCK_TOGGLES_ARR);
	
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
	
	public static ToggleType fromSettingName(String setting) {
		return ToggleType.valueOf(PREFIX + setting);
	}
	
	public static String combine(ToggleType ... types) {
		StringBuffer result = new StringBuffer();
		boolean first = true;
		for(ToggleType type : types) {
			if(!first) result.append(TOGGLE_DELIMITER);
			result.append(type.settingsName());
			first = false;
		}
		return result.toString();
	}

	public static ArrayList<ToggleType> split(String toggles) {
		String[] tArr = toggles.split("\\" + TOGGLE_DELIMITER);
		ArrayList<ToggleType> result = new ArrayList<ToggleType>();

		for(String type : tArr) {
			ToggleType t = fromSettingName(type);
			if(t == null) {
				Log.e(TAG, "Skipping unknown toggle " + type);
				continue;
			}
			result.add(t);
		}

		return result;
	}

	public static void setPreferredToggles(Context c, List<ToggleType> types) {
		setPreferredToggles(c, combine(types.toArray(new ToggleType[0])));
	}

	public static void setPreferredToggles(Context c, String types) {
		Settings.System.putString(c.getContentResolver(), 
			Settings.System.STATUSBAR_TOGGLES, types);
	}

	public static ArrayList<ToggleType> getPreferredToggles(Context c) {
		return split(getPreferredTogglesAsString(c));
	}

	public static String getPreferredTogglesAsString(Context c) {
		
		String result = Settings.System.getString(c.getContentResolver(),
            Settings.System.STATUSBAR_TOGGLES);
		
		if(result == null)
			result = STOCK_TOGGLES;
		
		return result;
	}

}
