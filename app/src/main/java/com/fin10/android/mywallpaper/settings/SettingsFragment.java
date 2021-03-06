package com.fin10.android.mywallpaper.settings;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.fin10.android.mywallpaper.Log;
import com.fin10.android.mywallpaper.R;

public final class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    public static void setTutorialEnabled(@NonNull Context context, boolean enabled) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(context.getString(R.string.pref_key_tutorial_enabled), enabled).apply();
    }

    public static boolean isTutorialEnabled(@NonNull Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(context.getString(R.string.pref_key_tutorial_enabled), true);
    }

    public static void setAutoChangeEnabled(@NonNull Context context, boolean enabled) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(context.getString(R.string.pref_key_auto_change_enabled), enabled).apply();
    }

    public static boolean isAutoChangeEnabled(@NonNull Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(context.getString(R.string.pref_key_auto_change_enabled), false);
    }

    public static void setPeriod(@NonNull Context context, int period) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(context.getString(R.string.pref_key_auto_change_period), period).apply();
    }

    public static int getPeriod(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(context.getString(R.string.pref_key_auto_change_period), PeriodPreference.Period.USUALLY);
    }

    public static long getInterval(@NonNull Context context) {
        long interval = AlarmManager.INTERVAL_DAY;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int period = pref.getInt(context.getString(R.string.pref_key_auto_change_period), PeriodPreference.Period.USUALLY);
        switch (period) {
            case PeriodPreference.Period.SOMETIMES:
                interval = 3 * AlarmManager.INTERVAL_DAY;
                break;
            case PeriodPreference.Period.USUALLY:
                interval = AlarmManager.INTERVAL_DAY;
                break;
            case PeriodPreference.Period.FREQUENTLY:
                interval = 3 * AlarmManager.INTERVAL_HOUR;
                break;
        }

        return interval;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference autoChangeEnabled = findPreference(getString(R.string.pref_key_auto_change_enabled));
        autoChangeEnabled.setOnPreferenceChangeListener(this);

        Preference autoChangePeriod = findPreference(getString(R.string.pref_key_auto_change_period));
        autoChangePeriod.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d("%s, newValue:%s", preference, newValue);
        String key = preference.getKey();
        if (TextUtils.equals(key, getString(R.string.pref_key_auto_change_enabled))) {
            boolean value = (boolean) newValue;
            if (value) WallpaperChangeScheduler.start(getActivity(), SettingsFragment.getInterval(getActivity()));
            else WallpaperChangeScheduler.stop(getActivity());
        } else if (TextUtils.equals(key, getString(R.string.pref_key_auto_change_period))) {
            WallpaperChangeScheduler.stop(getActivity());
            WallpaperChangeScheduler.start(getActivity(), SettingsFragment.getInterval(getActivity()));
        }

        return true;
    }
}
