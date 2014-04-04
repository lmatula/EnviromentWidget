package hu.bluepepper.enviromentwidget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class EnvWidget extends AppWidgetProvider {
	public static final String WIDGET_ID_KEY ="mywidgetproviderwidgetids";
	public static final String WIDGET_DATA_KEY ="mywidgetproviderwidgetdata";
	
	//static Timer timer = null;
	private AppWidgetManager appWidgetManager;
	private Context context;
	private int theme = R.layout.main_light;
    SharedPreferences settings;
    //int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	@Override
	public void onReceive(Context context, Intent intent) {
        intent.getAction();
	    if (intent.hasExtra(WIDGET_ID_KEY)) {
	        int[] ids = intent.getExtras().getIntArray(WIDGET_ID_KEY);
	        //if (intent.hasExtra(WIDGET_DATA_KEY)) {
//	           Object data = intent.getExtras().getParcelable(WIDGET_DATA_KEY);
//	           this.update(context, AppWidgetManager.getInstance(context), ids, data);
	        //} else {
	            this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
	        //}
	    } else super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i("dbg", "widget update begin");
		this.setAppWidgetManager(appWidgetManager);
		this.context = context;
		new DownloadTask().execute("http://emoncms.org/feed/list.json?apikey=38b848d37f55cc8faef8a90502ef1b64");
//		timer = new Timer();
//        timer.scheduleAtFixedRate(new UpdateTask(context), 1, 60000);

	}
	
	@Override
	public void onEnabled(Context context) {
		Log.i("dbg", "widget enabled");
        this.context = context;
		//timer = new Timer();
        //timer.scheduleAtFixedRate(new UpdateTask(context), 1, 60000);
		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		AppWidgetManager man = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, EnvWidget.class);
	    int[] ids = man.getAppWidgetIds(thisWidget);
	    Intent updateIntent = new Intent();
	    updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	    updateIntent.putExtra(EnvWidget.WIDGET_ID_KEY, ids);

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
		//After after 3 seconds

        settings = context.getSharedPreferences(EnvWidgetConfig.PREFS_NAME, 0);
        Resources res = context.getResources();
        int refRate = settings.getInt(EnvWidgetConfig.SettingKeys.UPDATE_INTERVAL.toString(), res.getInteger(R.integer.default_refresh_intervals));

		am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), refRate*1000 , pi);
		
//	    updateIntent.putExtra(EnvWidget.WIDGET_DATA_KEY, data);
	    //context.sendBroadcast(updateIntent);

		super.onEnabled(context);
	}

    private void doTempNotification(Enviroment result){
        Resources res = context.getResources();
        SharedPreferences settings = context.getSharedPreferences(EnvWidgetConfig.PREFS_NAME, 0);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("HU", "hu"));
        DecimalFormat df = new DecimalFormat("#.##", symbols);

        int lowTempV = settings.getInt(EnvWidgetConfig.SettingKeys.LOW_TEMPERATURE_THRESHOLD.toString(), res.getInteger(R.integer.default_temperature));
        if (lowTempV > result.getTemp()) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.notif_tempicon)
                            .setContentTitle(String.format(res.getString(R.string.temp_low_thersold_notifi_title),lowTempV))
                            .setContentText(String.format(res.getString(R.string.temp_low_thersold_notifi_value),df.format(result.getTemp())))
                            .setSound(alarmSound);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(0, mBuilder.build());
        }
    }

    private void doHumidityNotification(Enviroment result){
        Resources res = context.getResources();
        SharedPreferences settings = context.getSharedPreferences(EnvWidgetConfig.PREFS_NAME, 0);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("HU", "hu"));
        DecimalFormat df = new DecimalFormat("#.##", symbols);

        int highHumV = settings.getInt(EnvWidgetConfig.SettingKeys.HIGH_HUMIDITY_THRESHOLD.toString(), res.getInteger(R.integer.default_humidity));
        if (highHumV < result.getHum()) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.notif_humicon)
                            .setContentTitle(String.format(res.getString(R.string.hum_high_thershold_notify_title),highHumV))
                            .setContentText(String.format(res.getString(R.string.hum_high_thershold_notify_value),df.format(result.getHum())))
                            .setSound(alarmSound);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(1, mBuilder.build());
        }
    }

	@Override
	public void onDisabled(Context context){
		Intent intent = new Intent(context, EnvWidget.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
		super.onDisabled(context);
		//timer.cancel();
	}
	
	public void setProgressIndicator() {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), theme);
		ComponentName thisWidget = new ComponentName(context, EnvWidget.class);
		Log.i("dbg", "Begin retrive data set");
		remoteViews.setViewVisibility(R.id.progressBar1, View.VISIBLE);
		appWidgetManager.updateAppWidget(thisWidget, remoteViews);
	}
	
	public void unSetProgressIndicator() {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), theme);
		ComponentName thisWidget = new ComponentName(context, EnvWidget.class);
		remoteViews.setViewVisibility(R.id.progressBar1, View.INVISIBLE);
		appWidgetManager.updateAppWidget(thisWidget, remoteViews);
		Log.i("dbg", "Finsih retrive data set");
	}
	
	public void doDraw(Enviroment dataSet) {
		DateFormat format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("HU", "hu"));
		//symbols.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#.##", symbols);

        Resources res = context.getResources();
        SharedPreferences settings = context.getSharedPreferences(EnvWidgetConfig.PREFS_NAME, 0);

        String themeVal = settings.getString(EnvWidgetConfig.SettingKeys.THEME.toString(), EnvWidgetConfig.Themes.LIGHT.toString());

        if (themeVal.equalsIgnoreCase(EnvWidgetConfig.Themes.LIGHT.toString())){
            theme = R.layout.main_light;
        }else if (themeVal.equalsIgnoreCase(EnvWidgetConfig.Themes.DARK.toString())){
            theme = R.layout.main_dark;
        }

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), theme);
		ComponentName thisWidget = new ComponentName(context, EnvWidget.class);
		
		Log.i("dbg",  dataSet.getTemp() + "C°, " + dataSet.getHum() + "%");
		remoteViews.setTextViewText(R.id.tempval, df.format(dataSet.getTemp()) + "°C" );
		remoteViews.setTextViewText(R.id.humval, df.format(dataSet.getHum()) + "%" );
		remoteViews.setTextViewText(R.id.maxhum, df.format(dataSet.getHumMax()) + "%" );
		remoteViews.setTextViewText(R.id.minhum, df.format(dataSet.getHumMin()) + "%" );
		remoteViews.setTextViewText(R.id.maxtemp, df.format(dataSet.getTempMax()) + "°C" );
		remoteViews.setTextViewText(R.id.mintemp, df.format(dataSet.getTempMin()) + "°C" );
		remoteViews.setTextViewText(R.id.lastupd, format.format(new Date()) );
		this.appWidgetManager.updateAppWidget(thisWidget, remoteViews);
	}

	public AppWidgetManager getAppWidgetManager() {
		return appWidgetManager;
	}

	private void setAppWidgetManager(AppWidgetManager appWidgetManager) {
		this.appWidgetManager = appWidgetManager;
	}
	
	/*private class UpdateTask extends TimerTask {
		Context context;
		
		UpdateTask(Context context) {
			this.context = context;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i("dbg",  "Timer task");
			updateMyWidgets(context);
		}
		
	}*/
	
	private class DownloadTask extends AsyncTask<String, Void, Enviroment> {
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */
	    protected Enviroment doInBackground(String... urls) {
	    	setProgressIndicator();
	    	HttpClient client=new DefaultHttpClient();
	        HttpGet request=new HttpGet(urls[0]);
	        Enviroment data = new Enviroment();
	        try
	        {
	            HttpResponse response= client.execute(request);
	            HttpEntity entity= response.getEntity();
	            InputStreamReader in=new InputStreamReader(entity.getContent());
	            BufferedReader reader=new BufferedReader(in);

	            StringBuilder stringBuilder=new StringBuilder();
	            String line ="";

	            while ((line=reader.readLine())!=null)
	            {
	                  stringBuilder.append(line);
	            }
	            
	            JSONArray jsonArry=new JSONArray(stringBuilder.toString());
	            JSONObject jsonObject;
	            
	            for(int i=0 ;i<jsonArry.length();i++)
	            {
	                jsonObject=jsonArry.getJSONObject(i);
	                if (jsonObject.getString("name").equals("Rpi Temp")) {
	             	   data.setTemp(Float.valueOf(jsonObject.getString("value")));
	                } else if (jsonObject.getString("name").equals("rpi hum")) {
	             	   data.setHum(Float.valueOf(jsonObject.getString("value")));
	                } else if (jsonObject.getString("name").equals("min temp")) {
	             	   data.setTempMin(Float.valueOf(jsonObject.getString("value")));
	                } else if (jsonObject.getString("name").equals("max temp")) {
	             	   data.setTempMax(Float.valueOf(jsonObject.getString("value")));
	                } else if (jsonObject.getString("name").equals("rpi hum min")) {
	             	   data.setHumMin(Float.valueOf(jsonObject.getString("value")));
	                } else if (jsonObject.getString("name").equals("rpi hum max")) {
	             	   data.setHumMax(Float.valueOf(jsonObject.getString("value")));
	                }
	            }
	        }
	        catch(IOException e)
	        {
	            e.printStackTrace();
	        } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				unSetProgressIndicator();
			}
	        return data;
	    }
	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(Enviroment result) {
	        doDraw(result);
            doTempNotification(result);
            doHumidityNotification(result);
	    }
	}

}
