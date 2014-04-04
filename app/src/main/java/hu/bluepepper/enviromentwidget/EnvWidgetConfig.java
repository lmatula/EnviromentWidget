package hu.bluepepper.enviromentwidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnvWidgetConfig extends Activity {

    private static final String _ID = "id";
    private static final String _VALUE = "value";
    private static final String _KEY = "key";
    public static final String PREFS_NAME = "EnvWidgetConfig";
    private int tmpTemperatureVal = 0;
    private int tmpHumidityVal = 0;

    public enum SettingKeys {
        UPDATE_INTERVAL,THEME,LOW_TEMPERATURE_THRESHOLD,HIGH_HUMIDITY_THRESHOLD
    }

    public enum Themes {
        DARK,LIGHT
    }

	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	Button configOkButton;
    ListView configItems;
    SharedPreferences settings;
    SimpleAdapter adapter;
    List<HashMap<String,String>> settingsScreen = new ArrayList<HashMap<String, String>>();

    public int getTmpTemperatureVal() {
        return tmpTemperatureVal;
    }

    public void setTmpTemperatureVal(int tmpTemperatureVal) {
        this.tmpTemperatureVal = tmpTemperatureVal;
    }

    public int getTmpHumidityVal() {
        return tmpHumidityVal;
    }

    public void setTmpHumidityVal(int tmpHumidityVal) {
        this.tmpHumidityVal = tmpHumidityVal;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);

	    setContentView(R.layout.config);
		Intent intent = getIntent();
	    Bundle extras = intent.getExtras();
	    if (extras != null) {
	        mAppWidgetId = extras.getInt(
	                AppWidgetManager.EXTRA_APPWIDGET_ID,
	                AppWidgetManager.INVALID_APPWIDGET_ID);
	    }
	 
	    // If they gave us an intent without the widget id, just bail.
	    if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
	        finish();
	    }
        Log.d("Config","widget id: " + mAppWidgetId);
        settings = getSharedPreferences(PREFS_NAME, 0);
	    configOkButton = (Button)findViewById(R.id.save);
	    configOkButton.setOnClickListener(configOkButtonOnClickListener);
        configItems = (ListView)findViewById(R.id.configItems);
        settingsScreen.clear();
        settingsScreen.addAll(this.settingsView());
        adapter = new SimpleAdapter(
                this,
                settingsScreen,
                android.R.layout.two_line_list_item,
                new String[] {_KEY,_VALUE},
                new int[] {android.R.id.text1,android.R.id.text2}
        );

        setTmpTemperatureVal(getLowTemperatureThresholdValue());
        setTmpHumidityVal(getHighHumidityThresholdValue());

        configItems.setAdapter(adapter);
        configItems.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> o = (HashMap<String, String> )parent.getItemAtPosition(position);
                String itemKey = o.get(_ID);
                //Resources res = getResources();
                Toast.makeText(getBaseContext(), itemKey, Toast.LENGTH_LONG).show();
                SettingKeys selectedSettingKey = SettingKeys.valueOf(itemKey);
                switch (selectedSettingKey) {
                    case THEME: {
                        //FireMissilesDialogFragment popup = new FireMissilesDialogFragment();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag(SettingKeys.THEME.toString());
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        // Create and show the dialog.
                        ThemeChoseDialogFragment themeChose = new ThemeChoseDialogFragment();
                        themeChose.show(ft, SettingKeys.THEME.toString());
                        break;
                    }
                    case UPDATE_INTERVAL: {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag(SettingKeys.UPDATE_INTERVAL.toString());
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        // Create and show the dialog.
                        UpdateIntervalDialogFragment refreshInterval = new UpdateIntervalDialogFragment();
                        refreshInterval.show(ft, SettingKeys.UPDATE_INTERVAL.toString());
                        break;
                    }
                    case LOW_TEMPERATURE_THRESHOLD: {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag(SettingKeys.LOW_TEMPERATURE_THRESHOLD.toString());
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        // Create and show the dialog.
                        LowTemperatureDialogFragment lowTempThreshold  = new LowTemperatureDialogFragment();
                        lowTempThreshold.show(ft, SettingKeys.LOW_TEMPERATURE_THRESHOLD.toString());
                        break;
                    }
                    case HIGH_HUMIDITY_THRESHOLD: {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag(SettingKeys.HIGH_HUMIDITY_THRESHOLD.toString());
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        // Create and show the dialog.
                        HighHumidityDialogFragment highHumThreshold  = new HighHumidityDialogFragment();
                        highHumThreshold.show(ft, SettingKeys.HIGH_HUMIDITY_THRESHOLD.toString());
                        break;
                    }
                }
            }
        });
	}

    public class ThemeChoseDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            Resources res = getResources();
            builder.setTitle(res.getString(R.string.themeTitle))
                    .setItems(R.array.theme, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Resources res = getResources();
                            String[] themes = res.getStringArray(R.array.themes_values);
                            Log.d("theme", themes[which]);
                            setThemeValue(res.getStringArray(R.array.themes_values)[which]);
                            updateDataSet();
                        }
                    });
            return builder.create();
        }
    }

    public class UpdateIntervalDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            Resources res = getResources();
            builder.setTitle(res.getString(R.string.updateIntervalTitle))
                    .setItems(R.array.refresh_interval, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Resources res = getResources();
                            setRefreshInterval(res.getIntArray(R.array.refresh_interval_values)[which]);
                            updateDataSet();
                        }
                    });
            return builder.create();
        }
    }

    public class LowTemperatureDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            Resources res = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.number_picker, null);
//            TextView text = (TextView)view.findViewById(R.id.number_picker_text);
//            text.setText(String.format(res.getString(R.string.temp_low_thersold_seek_msg), getLowTemperatureThresholdValue()) );
            NumberPicker numberpicker = (NumberPicker)view.findViewById(R.id.numberPicker);
            numberpicker.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.format(getResources().getString(R.string.temp_low_thersold_val), value);
                }
            });
            numberpicker.setMaxValue(res.getInteger(R.integer.max_temperature));
            numberpicker.setMinValue(res.getInteger(R.integer.min_temperature));
            numberpicker.setValue(getLowTemperatureThresholdValue());
            numberpicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){

                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    setTmpTemperatureVal(newVal);
                }
            });

            builder.setTitle(res.getString(R.string.temp_low_thersold_title))
                    .setView(view)
                    .setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setLowTemperatureThresholdValue(getTmpTemperatureVal());
                            updateDataSet();
                        }
                    })
                    .setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            return builder.create();
        }
    }

    public class HighHumidityDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            Resources res = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.number_picker, null);
//            TextView text = (TextView)view.findViewById(R.id.number_picker_text);
//            text.setText(String.format(res.getString(R.string.temp_low_thersold_seek_msg), getLowTemperatureThresholdValue()) );
            NumberPicker numberpicker = (NumberPicker)view.findViewById(R.id.numberPicker);
            numberpicker.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.format(getResources().getString(R.string.hum_high_thershold_val), value);
                }
            });
            numberpicker.setMaxValue(res.getInteger(R.integer.max_humidity));
            numberpicker.setMinValue(res.getInteger(R.integer.min_humidity));
            numberpicker.setValue(getHighHumidityThresholdValue());
            numberpicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){

                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    setTmpHumidityVal(newVal);
                }
            });

            builder.setTitle(res.getString(R.string.hum_high_thershold))
                    .setView(view)
                    .setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setHighHumidityThresholdValue(getTmpHumidityVal());
                            updateDataSet();
                        }
                    })
                    .setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            return builder.create();
        }
    }

    private void updateDataSet() {
        SimpleAdapter adapter = (SimpleAdapter)configItems.getAdapter();
        settingsScreen.clear();
        settingsScreen.addAll(this.settingsView());
        adapter.notifyDataSetChanged();
    }

    private List<HashMap<String,String>> settingsView() {
        Resources res = getResources();
        List<HashMap<String,String>> thisList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> item;
        Log.d("getdata","gatdata");
        item = new HashMap<String, String>();
        item.put(_ID, SettingKeys.UPDATE_INTERVAL.toString());
        item.put(_KEY,res.getString(R.string.updateInterval));
        item.put(_VALUE, String.format(res.getString(R.string.updateInterval_msg), getRefreshInterval()/60 ));
        thisList.add(item);

        String[] themes = res.getStringArray(R.array.themes_values);
        String currentThemeVal = getThemeValue();
        int indexOfThemeVal = 0;
        while (!themes[indexOfThemeVal].equalsIgnoreCase(currentThemeVal)) {
            indexOfThemeVal++;
        }
        String themeName = res.getStringArray(R.array.theme)[indexOfThemeVal];
        item = new HashMap<String, String>();
        item.put(_ID, SettingKeys.THEME.toString());
        item.put(_KEY,res.getString(R.string.theme));
        item.put(_VALUE, String.format(res.getString(R.string.theme_msg), themeName ));
        thisList.add(item);

        item = new HashMap<String, String>();
        item.put(_ID, SettingKeys.LOW_TEMPERATURE_THRESHOLD.toString());
        item.put(_KEY,res.getString(R.string.temp_low_thersold));
        item.put(_VALUE, String.format(res.getString(R.string.temp_low_thersold_msg), getLowTemperatureThresholdValue() ));
        thisList.add(item);

        item = new HashMap<String, String>();
        item.put(_ID, SettingKeys.HIGH_HUMIDITY_THRESHOLD.toString());
        item.put(_KEY,res.getString(R.string.hum_high_thershold));
        item.put(_VALUE, String.format(res.getString(R.string.hum_high_thershold_msg), getHighHumidityThresholdValue() ));
        thisList.add(item);

        return thisList;
    }
	
	public int getRefreshInterval() {
        Resources res = getResources();
		return settings.getInt(SettingKeys.UPDATE_INTERVAL.toString(), res.getInteger(R.integer.default_refresh_intervals));
	}
	
	public void setRefreshInterval(int refreshInterval) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SettingKeys.UPDATE_INTERVAL.toString(), refreshInterval);
        editor.commit();
	}

    public String getThemeValue(){
        //Resources res = getResources();
        return settings.getString(SettingKeys.THEME.toString(), Themes.LIGHT.toString());
    }

    public void setThemeValue(String themeValue) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SettingKeys.THEME.toString(), themeValue);
        editor.commit();
    }

    public int getLowTemperatureThresholdValue() {
        Resources res = getResources();
        return settings.getInt(SettingKeys.LOW_TEMPERATURE_THRESHOLD.toString(), res.getInteger(R.integer.default_temperature));
    }

    public void setLowTemperatureThresholdValue(int lowTemperatureThresholdValue){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SettingKeys.LOW_TEMPERATURE_THRESHOLD.toString(), lowTemperatureThresholdValue);
        editor.commit();
    }

    public int getHighHumidityThresholdValue() {
        Resources res = getResources();
        return settings.getInt(SettingKeys.HIGH_HUMIDITY_THRESHOLD.toString(), res.getInteger(R.integer.default_humidity));
    }

    public void setHighHumidityThresholdValue(int highHumidityThresholdValue) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SettingKeys.HIGH_HUMIDITY_THRESHOLD.toString(), highHumidityThresholdValue);
        editor.commit();
    }
	
	private Button.OnClickListener configOkButtonOnClickListener = new Button.OnClickListener(){

	@Override
	public void onClick(View arg0) {
         // TODO Auto-generated method stub

         final Context context = EnvWidgetConfig.this;

         AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

         //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.hellowidget_layout);
         //appWidgetManager.updateAppWidget(mAppWidgetId, views);
         //EnvWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

         Toast.makeText(context, "HelloWidgetConfig.onClick(): " + String.valueOf(mAppWidgetId) , Toast.LENGTH_LONG).show();

         Intent resultValue = new Intent();
         resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
         setResult(RESULT_OK, resultValue);
         finish();
	}};


}
