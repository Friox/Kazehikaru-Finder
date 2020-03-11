package com.friox.kazehikarufinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.RemoteMessage;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    StaticInfo staticInfo = new StaticInfo(getApplicationContext());

    public FirebaseMessagingService() {}

    @Override
    public void onNewToken(String token) {
        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        shownotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
    }

    private void shownotification(String title, String body) {
        String cv_title, id;
        int icon;
        if (title.equals(staticInfo.animeTag)) {
            id = staticInfo.notiAnimeId;
            cv_title = getResources().getString(R.string.noti_update_anime);
            icon = R.drawable.ic_movie_black_24dp;
        } else if (title.equals(staticInfo.manageTag)) {
            id = staticInfo.notiManageId;
            cv_title = getResources().getString(R.string.noti_server);
            icon = R.drawable.ic_star_black_24dp;
        } else {
            id = staticInfo.notiUnknownId;
            cv_title = getResources().getString(R.string.noti_unknown);
            icon = R.drawable.ic_error_outline_black_24dp;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), id)
                .setSmallIcon(icon)
                .setContentTitle(cv_title)
                .setContentText(body);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .build();
        // request
        Request request = new Request.Builder()
                .url(staticInfo.registerUrl)
                .post(body)
                .build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
