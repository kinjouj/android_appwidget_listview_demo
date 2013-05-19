package net.kinjouj.test.android_appwidget_listview_demo;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.widget.RemoteViewsService.RemoteViewsFactory;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.HttpsClient;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;


@EBean
public class SampleWidgetViewFactory implements RemoteViewsFactory {

    private static final String TAG = SampleWidgetViewFactory.class.getName();

    @RootContext
    protected Context context;

    @Bean
    protected TwitterApi api;

    @HttpsClient
    protected HttpClient httpClient;

    private ResponseList<Status> statuses;

    public void onCreate() {
        Log.v(TAG, "[onCreate]");
    }

    public void onDataSetChanged() {
        Log.v(TAG, "[onDataSetChanged]");

        try {
            statuses = api.getTwitter().getHomeTimeline();
        } catch (TwitterException e) {
            handleError(e);
        }
    }

    public void onDestroy() {
        Log.v(TAG, "[onDestroy]");
    }

    public RemoteViews getViewAt(int position) {
        Log.v(TAG, "[getViewAt]: " + position);

        if(getCount() <= 0) {
            return null;
        }

        RemoteViews rv = null;

        try {
            Status status = statuses.get(position);

            if(status != null) {
                User user = status.getUser();

                rv = new RemoteViews(context.getPackageName(), R.layout.widget_listview_row);
                rv.setTextViewText(R.id.text1, status.getText());
                rv.setImageViewBitmap(
                    R.id.profile_image,
                    fetchProfileBitmap(user.getProfileImageURLHttps())
                );

                Uri browserUri = Uri.parse(
                    String.format(
                        "https://twitter.com/%s/status/%d",
                        user.getScreenName(),
                        status.getId()
                    )
                );

                Intent intent = new Intent();
                intent.setData(browserUri);

                rv.setOnClickFillInIntent(R.id.text1, intent);
            }
        } catch (Exception e) {
            handleError(e);
        }

        return rv;
    }

    public long getItemId(int position) {
        Log.v(TAG, "[getItemId]: " + position);

        return position;
    }

    public int getCount() {
        Log.v(TAG, "[getCount]");

        return statuses != null ? statuses.size() : 0;
    }

    public RemoteViews getLoadingView() {
        Log.v(TAG, "[getLoadingView]");

        return null;
    }

    public int getViewTypeCount() {
        Log.v(TAG, "[getViewTypeCount]");

        return 1;
    }

    public boolean hasStableIds() {
        Log.v(TAG, "[hasStableIds]");

        return true;
    }

    @UiThread
    protected void handleError(Throwable t) {
        Toast.makeText(
            context,
            "ERROR: " + t.getMessage(),
            Toast.LENGTH_LONG
        ).show();
    }

    private HttpClient getThreadSafeHttpClient() {
        HttpParams params = httpClient.getParams();
        SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();

        return new DefaultHttpClient(
            new ThreadSafeClientConnManager(params, schemeRegistry),
            params
        );
    }

    private Bitmap fetchProfileBitmap(String imageUrl) {
        HttpClient httpClient = null;
        Bitmap bm = null;

        try {
            httpClient = getThreadSafeHttpClient();
            bm = httpClient.execute(new HttpGet(imageUrl), new ResponseHandler<Bitmap>() {
                @Override
                public Bitmap handleResponse(HttpResponse response)
                    throws ClientProtocolException, IOException {
                    BufferedHttpEntity entity = new BufferedHttpEntity(
                        response.getEntity()
                    );

                    return BitmapFactory.decodeStream(entity.getContent());
                }                    
            });
        } catch (Exception e) {
            handleError(e);
        } finally {
            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }

        return bm;
    }
}