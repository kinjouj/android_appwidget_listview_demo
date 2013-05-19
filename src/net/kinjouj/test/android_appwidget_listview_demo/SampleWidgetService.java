package net.kinjouj.test.android_appwidget_listview_demo;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;

@EService
public class SampleWidgetService extends RemoteViewsService {

    private static final String TAG = SampleWidgetService.class.getName();

    @Bean
    protected SampleWidgetViewFactory viewFactory;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.v(TAG, "newGetViewFactory");

        return viewFactory;
    }
}