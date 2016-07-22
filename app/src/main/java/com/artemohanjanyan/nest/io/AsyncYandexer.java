package com.artemohanjanyan.nest.io;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.artemohanjanyan.nest.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class AsyncYandexer extends AsyncTask<String, Void, String> {
    private Context context;

    public AsyncYandexer(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d(this.getClass().getSimpleName(), "downloading...");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new URL("https://yandex.ru/search/?text=" + strings[0].replace(" ", "%20"))
                        .openConnection()
                        .getInputStream()))) {

            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = reader.readLine()) != null)
                response.append(inputLine);

            String res = response.toString(); // Очень суровый парсинг.
            String startStr = "tabindex=\"2\">";
            String endStr = "</a></h2><div";
            int startIndex = res.indexOf(startStr);
            int endIndex = res.indexOf(endStr, startIndex);

            return res.substring(startIndex + startStr.length(), endIndex);
        } catch (Exception e) {
            Log.d(this.getClass().getSimpleName(), "failed", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        NotificationManagerCompat.from(context).notify(0, new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.incoming))
                .setContentText(result != null ? result : context.getString(R.string.not_found))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLargeIcon(BitmapFactory
                        .decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .build());
        context = null;
    }
}
