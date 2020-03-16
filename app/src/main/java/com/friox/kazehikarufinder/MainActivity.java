package com.friox.kazehikarufinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private boolean backLock = false, error = false;
    private String nowPath, versionName;
    private TextView progressText, progressLight, versionText;
    private StaticInfo staticInfo;
    private RecyclerView recyclerView;
    private ArrayList<ListObject> list = null;
    private ItemAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    CustomAlertDialog customAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        progressText = findViewById(R.id.progress_text);
        progressLight = findViewById(R.id.progress_status);
        versionText = findViewById(R.id.version_text);
        swipeRefreshLayout = findViewById(R.id.recyclerView_parent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getInfo().execute(staticInfo.currentUrl);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        versionName = getAppVersionName();
        versionText.setText(versionName);
        staticInfo = new StaticInfo(getApplicationContext());
        createNotificationChannel();
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, 1));
        new getInfo().execute(staticInfo.mainUrl);
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence animeName = getResources().getString(R.string.noti_setting_anime);
        CharSequence manageName = getResources().getString(R.string.noti_setting_manage);
        CharSequence unknownName = getResources().getString(R.string.noti_setting_unknown);
        NotificationChannel animeChannel = new NotificationChannel(staticInfo.notiAnimeId, animeName, NotificationManager.IMPORTANCE_HIGH);
        NotificationChannel manageChannel = new NotificationChannel(staticInfo.notiManageId, manageName, NotificationManager.IMPORTANCE_HIGH);
        NotificationChannel unknownChannel = new NotificationChannel(staticInfo.notiUnknownId, unknownName, NotificationManager.IMPORTANCE_DEFAULT);
        animeChannel.enableVibration(true);
        manageChannel.enableVibration(true);
        notificationManager.createNotificationChannel(animeChannel);
        notificationManager.createNotificationChannel(manageChannel);
        notificationManager.createNotificationChannel(unknownChannel);
    }

    public String getAppVersionName(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "UNKNOWN";
        }
        return packageInfo.versionName;
    }

    private class getInfo extends AsyncTask<String, Void, Integer> {

        private int statusCode = 0;
        private ArrayList<ListObject> originList;
        Connection.Response response;
        String userAgent = staticInfo.getUserAgent();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressText.setText("LOADING...");
            progressLight.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.progressWork));
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                response = Jsoup.connect(params[0]).userAgent(userAgent).execute();
                statusCode = response.statusCode();
                originList = new ArrayList<>();
                list = new ArrayList<>();
                Document doc = response.parse();
                nowPath = doc.select("h1").get(0).text();
                nowPath = nowPath.substring(nowPath.lastIndexOf('/') + 1);
                Elements tr = doc.select("tr");
                for (int i = 2; i < tr.size() - 1; i++) {
                    Elements element = tr.get(i).select("td");
                    String type = element.get(0).select("img").get(0).attr("alt");
                    String title = element.get(1).select("a").text();
                    String url = element.get(1).select("a").get(0).attr("href");
                    String size = element.get(3).text();

                    int typeCode;
                    if (type.equals("[PARENTDIR]")) typeCode = 0;
                    else if (type.equals("[DIR]")) typeCode = 1;
                    else if (type.equals("[VID]")) typeCode = 2;
                    else typeCode = 99;

                    if (typeCode == 0) url = staticInfo.baseUrl + url;
                    else url = staticInfo.currentUrl + url;

                    String fileExtension;
                    if (typeCode < 2) {
                        // Parent & Folder
                        title = title.substring(0, title.length() - 1);
                        fileExtension = "*NOEXTN";
                    } else {
                        // Other Files
                        if (title.contains(".")) {
                            // Include Extensions
                            fileExtension = title.substring(title.lastIndexOf('.') + 1);
                            title = title.substring(0, title.lastIndexOf('.'));
                        } else {
                            // Without Extensions
                            fileExtension = "*NOEXTN";
                        }
                    }
                    originList.add(new ListObject(title, size, 0, url, typeCode, fileExtension, null));
                }

                // Filter
                for(ListObject i : originList) {
                    if (i.getViewType() == 2) {
                        try {
                            String temp = i.getTitle();
                            temp = temp.substring(0, temp.lastIndexOf('(') - 1);
                            temp = temp.substring(temp.lastIndexOf('-') + 2);
                            if (temp.length() <= 8) i.setEpisode("EP : " + temp);
                        } catch (Exception e) {
                            // Rename Error, No Action
                        }
                        // if VIDEO
                        String fileName = i.getTitle();
                        for(ListObject j : originList) {
                            // Check Sub
                            String target = j.getTitle();
                            String targetExtension = j.getFileExtn();
                            if ((fileName + "smi").equals(target + targetExtension)) i.subOk();
                            if ((fileName + "ass").equals(target + targetExtension)) i.subOk();
                            if ((fileName + "srt").equals(target + targetExtension)) i.subOk();
                        }
                    }
                    if (i.getViewType() < 3) list.add(i);
                }

                // Sort, First Folder
                int listSize = list.size();
                int sortCnt = 0;
                for (int i = 0; i < listSize; i++) {
                    if (list.get(i).getViewType() == 1) {
                        for (int j = i; j > sortCnt + 1; j--) {
                            Collections.swap(list, j, j - 1);
                        }
                        sortCnt++;
                    }
                }

                // if Root Folder
                if (nowPath.equals(staticInfo.rootName)) {
                    list.remove(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            backLock = false;
            if (result == 1) {
                updateActionBarSubTitle(nowPath.equals(staticInfo.rootName) ? "ROOT" : nowPath);
                progressText.setText("READY (" + statusCode + ")");
                progressLight.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.progressGood));
                adapter = new ItemAdapter(list, MainActivity.this);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                recyclerView.removeAllViewsInLayout();
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                error = false;
            } else {
                customAlertDialog = new CustomAlertDialog(MainActivity.this, getString(R.string.notice_alert_title), getString(R.string.no_network_dialog), getColor(R.color.colorPrimary));
                customAlertDialog.show();
                progressText.setText(R.string.connection_error);
                progressLight.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.progressBad));
                error = true;
            }
            adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    int type = list.get(position).getViewType();
                    if (type < 2) {
                        // PARENT & FOLDER
                        adapter.setOnItemClickListener(null); // Prevent input while working
                        staticInfo.updateParent(staticInfo.currentUrl);
                        String url = list.get(position).getUrl();
                        new getInfo().execute(url);
                        staticInfo.updateURL(url);
                    } else if (type == 2) {
                        if (list.get(position).getFileExtn().equals("part")) {
                            customAlertDialog = new CustomAlertDialog(MainActivity.this, getString(R.string.notice_alert_title), getString(R.string.notice_now_downloading), getColor(R.color.colorPrimary));
                            customAlertDialog.show();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = Uri.parse(list.get(position).getUrl());
                            intent.setDataAndType(uri, "video/*");
                            startActivity(intent);
                        }
                    } else {
                        // OTHER
                    }
                }
            });
        }
    }

    public void updateActionBarSubTitle(String string) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(string);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exit_btn:
                // Code
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                };
                customAlertDialog = new CustomAlertDialog(MainActivity.this, getString(R.string.notice_alert_title), getString(R.string.confirm_exit_dialog), getColor(R.color.colorPrimary), onClickListener, null);
                customAlertDialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (error) {
            // on Error, allow to stop this app
            super.onBackPressed();
        } else if (nowPath.equals(staticInfo.rootName)) {
            // on Root, allow to stop this app
            super.onBackPressed();
        } else {
            if (!backLock) {
                backLock = true;
                adapter.setOnItemClickListener(null); // Prevent input while working
                staticInfo.updateParent(staticInfo.currentUrl);
                String url = list.get(0).getUrl();
                new getInfo().execute(url);
                staticInfo.updateURL(url);
            }
        }
    }
}