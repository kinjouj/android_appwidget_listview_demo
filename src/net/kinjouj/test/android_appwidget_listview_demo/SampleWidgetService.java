package net.kinjouj.test.android_appwidget_listview_demo;

import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SampleWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new SampleWidgetFactory();
    }

    private class SampleWidgetFactory implements RemoteViewsFactory {

        private static final String TAG = "SampleViewFactory";

        private JSONArray jsons = new JSONArray();
        private Bitmap profileImage = null;

        public void onCreate() {
            Log.v(TAG, "[onCreate]");
        }

        public void onDataSetChanged() {
            Log.v(TAG, "[onDataSetChanged]");

            fetchTimelines();
        }

        public void onDestroy() {
            Log.v(TAG, "[onDestroy]");
        }

        public RemoteViews getViewAt(int position) {
            Log.v(TAG, "[getViewAt]: " + position);

            if(jsons.length() <= 0) {
                return null;
            }

            RemoteViews rv = null;

            try {
                JSONObject json = jsons.getJSONObject(position);

                if(json != null) {
                    JSONObject user = json.getJSONObject("user");
                    String profileImageUrl = user.getString("profile_image_url");

                    rv = new RemoteViews(getPackageName(), R.layout.widget_listview_row);
                    rv.setTextViewText(R.id.text1, json.getString("text"));
                    rv.setImageViewBitmap(R.id.profile_image, fetchProfileImage(profileImageUrl));

                    String screenName = user.getString("screen_name");
                    String tweetId = user.getString("id_str");

                    Uri browserUri = Uri.parse(
                        "https://twitter.com/" + screenName + "/status/" + tweetId
                    );

                    Intent intent = new Intent();
                    intent.setData(browserUri);

                    rv.setOnClickFillInIntent(R.id.text1, intent);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }

            return rv;
        }

        public long getItemId(int position) {
            Log.v(TAG, "[getItemId]: " + position);

            return position;
        }

        public int getCount() {
            Log.v(TAG, "[getCount]");

            return jsons.length();
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

        private void fetchTimelines() {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(
                    new HttpGet("https://api.twitter.com/1/statuses/user_timeline/kinjou_j.json?count=10")
                );

                if(response.getStatusLine().getStatusCode() != 200) {
                    return;
                }

                jsons = new JSONArray(EntityUtils.toString(response.getEntity()));
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

        private Bitmap fetchProfileImage(String url) {
            if(profileImage == null) {
                Log.v(TAG, "[fetchProfileImage]: "  + url);

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(new HttpGet(url));
    
                    if(response.getStatusLine().getStatusCode() == 200) {
                        profileImage = BitmapFactory.decodeStream(response.getEntity().getContent());
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            return profileImage;
        }
    }
}