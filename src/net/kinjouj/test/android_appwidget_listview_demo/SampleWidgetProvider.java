package net.kinjouj.test.android_appwidget_listview_demo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class SampleWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_CLICK =
        "net.kinjouj.test.android_appwidget_listview_demo.ACTION_CLICK";

    private static final String ACTION_ITEM_CLICK =
        "net.kinjouj.test.android_appwidget_listview_demo.ACTION_ITEM_CLICK";

    @Override
    public void onUpdate(Context ctx, AppWidgetManager manager, int[] appWidgetIds) {
        super.onUpdate(ctx, manager, appWidgetIds);

        for(int appWidgetId : appWidgetIds) {
            Intent remoteViewsFactoryIntent = new Intent(ctx, SampleWidgetService.class);

            RemoteViews rv = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout);
            rv.setRemoteAdapter(R.id.listView, remoteViewsFactoryIntent);

            setOnItemSelectedPendingIntent(ctx, rv);
            setOnButtonClickPendingIntent(ctx, rv, appWidgetId);

            manager.updateAppWidget(appWidgetId, rv);
        }
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        super.onReceive(ctx, intent);

        if(ACTION_CLICK.equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            );

            if(appWidgetId != 0) {
                AppWidgetManager.getInstance(ctx).notifyAppWidgetViewDataChanged(
                    appWidgetId,
                    R.id.listView
                );
            }
        } else if(ACTION_ITEM_CLICK.equals(intent.getAction())) {
            Uri uri = intent.getData();

            if(uri != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                ctx.startActivity(browserIntent);
            }
        }
    }

    private void setOnItemSelectedPendingIntent(Context ctx, RemoteViews rv) {
        Intent itemClickIntent = new Intent(ctx, SampleWidgetProvider.class);
        itemClickIntent.setAction(ACTION_ITEM_CLICK);

        PendingIntent itemClickPendingIntent = PendingIntent.getBroadcast(
            ctx,
            0,
            itemClickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );

        rv.setPendingIntentTemplate(R.id.listView, itemClickPendingIntent);
    }

    private void setOnButtonClickPendingIntent(Context ctx, RemoteViews rv, int appWidgetId) {
        Intent btnClickIntent = new Intent(ACTION_CLICK);
        btnClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent btnClickPendingIntent = PendingIntent.getBroadcast(
            ctx,
            0,
            btnClickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );

        rv.setOnClickPendingIntent(R.id.btn, btnClickPendingIntent);
    }
}