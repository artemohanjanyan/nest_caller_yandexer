package com.artemohanjanyan.nest.io;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.artemohanjanyan.nest.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class AsyncYandexer extends AsyncTask<String, Void, SearchResult> {
    private Context context;

    public AsyncYandexer(Context context) {
        this.context = context;
    }

    @Override
    protected SearchResult doInBackground(String... strings) {
        Log.d(this.getClass().getSimpleName(), "downloading...");

        try {
            Document document = Jsoup
                    .connect("https://yandex.ru/search/?text=" + strings[0].replace(" ", "%20"))
                    .get();

            Elements result = document.getElementsByClass("serp-item__title-link");
            String text = result.text();
            String link = result.get(0).attr("abs:href");

            result = document.getElementsByClass("organic__content-wrapper");
            String description = result.get(0).text();

            return new SearchResult(text, link, description);
        } catch (IOException e) {
            Log.d(this.getClass().getSimpleName(), "loading failed", e);
            return null;
        }

        // Оставлю на память
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
//                new URL("https://yandex.ru/search/?text=" + strings[0].replace(" ", "%20"))
//                        .openConnection()
//                        .getInputStream()))) {
//
//            StringBuilder response = new StringBuilder();
//            String inputLine;
//
//            while ((inputLine = reader.readLine()) != null)
//                response.append(inputLine);
//
//            String res = response.toString(); // Очень суровый парсинг.
//            String startStr = "tabindex=\"2\">";
//            String endStr = "</a></h2><div";
//            int startIndex = res.indexOf(startStr);
//            int endIndex = res.indexOf(endStr, startIndex);
//
//            return res.substring(startIndex + startStr.length(), endIndex);
//        } catch (Exception e) {
//            Log.d(this.getClass().getSimpleName(), "failed", e);
//            return null;
//        }
    }

    @Override
    protected void onPostExecute(SearchResult result) {
        NotificationManagerCompat.from(context).notify(0, new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory
                        .decodeResource(context.getResources(), R.mipmap.ic_launcher))

                .setContentTitle(result.getTitle())
                .setContentText(result.getDescription())
                .setContentIntent(PendingIntent.getActivity(context, 0,
                        new Intent(Intent.ACTION_VIEW, Uri.parse(result.getLink())), 0))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(result.getDescription()))

                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)

                .build());
        context = null;
    }
}
