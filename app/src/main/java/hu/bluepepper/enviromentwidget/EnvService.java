package hu.bluepepper.enviromentwidget;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by lmatula on 2014.04.03..
 */
public class EnvService extends IntentService {

    static final String ACTION_TIMETRAVEL = "hu.bluepepper.enviromentwidget.DATASET_UPDATE";
    static final String EXTRA_ENV_DATA = "hu.bluepepper.enviromentwidget.EXTRA_ENV_DATA";

    public EnvService() {
        super("EnvWidgetService");
    }

    @Override
    public void onHandleIntent(Intent workIntent) {
        /*AlarmManager am=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(EXTRA_ENV_DATA, ids);

        PendingIntent pi = PendingIntent.getBroadcast(this, 0, updateIntent, 0);
        //After after 3 seconds
        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 60000 , pi);*/
    }
}
