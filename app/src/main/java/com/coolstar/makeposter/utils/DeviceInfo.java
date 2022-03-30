package com.coolstar.makeposter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.InputStream;
import java.util.regex.Pattern;

public final class DeviceInfo {

	// 手机IMEI号
	public static String DEVICE_ID;

	public static String MAC_ADDR;

	// 屏幕宽度（像素）WelcomeActivity onResume之后才有值
	public static int WIDTH;

	// 屏幕高度（像素）WelcomeActivity onResume之后才有值
	public static int HEIGHT;

	// 屏幕密度（0.75 / 1.0 / 1.5）WelcomeActivity onResume之后才有值
	public static float DENSITY;

	// 屏幕密度DPI（120 / 160 / 240）WelcomeActivity onResume之后才有值
	public static int DENSITY_DPI;

	public static float SCALED_DENSITY;

	// 是否插入耳机
	public static boolean IS_EARPHONE;

	// 小米的MIUI？
	public static boolean IS_MIUI;

	// 总内存(B为单位）
	public static long TOTAL_MEM;
	// 总内存(MB为单位）
	public static int TOTAL_MEM_MB;
	// cpu频率(MHz为单位)
	public static int CPU_MAX;
	

	@SuppressWarnings("deprecation")
	public static void init(Context context) {
		if (isInited) {
			return;
		}
		try {
			WifiManager mgr = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			MAC_ADDR = mgr.getConnectionInfo().getMacAddress();
			if (TextUtils.isEmpty(MAC_ADDR)) {
				MAC_ADDR = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			MAC_ADDR = "";
		}
		
//		try {
//			String deviceId = ((TelephonyManager) context
//					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
//			if (TextUtils.isEmpty(deviceId) || "0".equals(deviceId)) {
//				if (!TextUtils.isEmpty(MAC_ADDR)) {
//					deviceId = MAC_ADDR;
//				}
//			}
//			DEVICE_ID = deviceId;
//		} catch (Exception e) {
//			e.printStackTrace();
//			DEVICE_ID = "";
//		}

		try {
			AudioManager localAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			IS_EARPHONE = localAudioManager.isWiredHeadsetOn();
			Log.d("HeadsetControlReceiver", "耳机初始化"+IS_EARPHONE);
		} catch (Exception e) {
			e.printStackTrace();
			IS_EARPHONE = false;
		}
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageInfo("miui", PackageManager.GET_UNINSTALLED_PACKAGES);
			IS_MIUI = true;
		} catch (NameNotFoundException e) {
			IS_MIUI = false;
		}
		
		TOTAL_MEM = getTotalMemory();
		TOTAL_MEM_MB = (int) (TOTAL_MEM/1024/1024);
		CPU_MAX = getMaxCpuFreq();
		isInited = true;
		initScreenInfo(context);
		LogMgr.d(DeviceInfo.class.getSimpleName(),"inited ok");
	}

	public static void initScreenInfo(Context context) {
		if (WIDTH == 0) {
			try {
				DisplayMetrics dm = new DisplayMetrics();
				((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
				WIDTH = Math.min(dm.widthPixels, dm.heightPixels);
				HEIGHT = Math.max(dm.widthPixels, dm.heightPixels);
				DENSITY = dm.density;
				DENSITY_DPI = dm.densityDpi;
				SCALED_DENSITY = dm.scaledDensity;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	private static long getTotalMemory() {
		String[] arrayOfString;
		long initial_memory = 0;

		try {
			FileReader localFileReader = new FileReader("/proc/meminfo");
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 1024);
			try {
				String firstLine = localBufferedReader.readLine();
				if(firstLine == null) return initial_memory;
				arrayOfString = firstLine.split("\\s+");
				initial_memory = (long)(Integer.valueOf(arrayOfString[1]).intValue()) * 1024;
			} finally {
				localBufferedReader.close();
			}
		} catch (Throwable e) {}
		return initial_memory;
	}

	private DeviceInfo() {
	}

	private static boolean isInited;
	
	/**
	 * 返回内存大小，以MB为单位
	 * @return
	 */
	public static long getMemTotalSize(){
		return TOTAL_MEM_MB;
	}
	
	public static int getMaxCpuFreq() {// 获取cpu最大频率
	    String result = "";
	    ProcessBuilder cmd;
	    try {
	        String[] args = { "/system/bin/cat",
	                "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
	        cmd = new ProcessBuilder(args);
	        Process process = cmd.start();
	        InputStream is = process.getInputStream();
	        byte[] te = new byte[24];
	        while (is.read(te) != -1) {
	            result += new String(te);
	        }
	        float core = Float.valueOf(result);
	        core = core / 1000;  //把khz转为MHz
	        result = (int) core+"";
	        is.close();
	    } catch (Error e) {
	    	result = "1024";
	    } catch (Exception e) {
	    	result = "1024";
	    }
	    try {
		    return Integer.parseInt(result);
		} catch (Exception e) {
			return 512;
		}
	}
    
	
	public static int getNumCores() {// 获取cpu核心数
	    class CpuFilter implements FileFilter {
	        @Override
	        public boolean accept(File pathname) {
	            if (Pattern.matches("cpu[0-9]", pathname.getName())) {
	                return true;
	            }
	            return false;
	        }

	    }
	    try {
	        File dir = new File("/sys/devices/system/cpu/");
	        File[] files = dir.listFiles(new CpuFilter());
	        return files.length;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return 1;
	    }
	}

	public static String getNumCoure()// 转换核心数
	{
	    String result = "";
	    if (getNumCores() == 1) {
	        result = "单核";
	    } else if (getNumCores() == 2) {
	        result = "双核";
	    } else if (getNumCores() == 4) {
	        result = "四核";
	    } else {
	        result = "你手机为劣质手机,无法检测!";
	    }
	    return result;
	}

	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasIceCreamSandwich() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean hasKitKat() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}
}
