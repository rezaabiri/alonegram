package ir.cenlearn.alonegram.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import ir.cenlearn.alonegram.APIs.ApiServiceNotification;
import ir.cenlearn.alonegram.Adapter.NotificationAdapter;
import ir.cenlearn.alonegram.BroadCasts.BroadCastInternt;
import ir.cenlearn.alonegram.Model.Notification;
import ir.cenlearn.alonegram.Others.CustomTypefaceSpan;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;

import java.util.Collections;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import static ir.cenlearn.alonegram.Activities.MainActivity.notifManager;
import static ir.cenlearn.alonegram.fragments.MainFragment.new_notif;

public class NotifActivity extends AppCompatActivity {

    BottomNavigationView navigationView;
    public static NotificationAdapter notificationAdapter;
    public static RecyclerView recyclerView_notification;
    public static LinearLayout nonotification;
    SharedPreferences preferences;
    public static ImageView loading_notif;
    BroadcastReceiver broadcastReceiverNet;
    private final BroadcastReceiver MyReceiver = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        navigationView = findViewById(R.id.navigation);
        recyclerView_notification = findViewById(R.id.recyclerview_notification);
        nonotification = findViewById(R.id.nonotification);
        loading_notif = findViewById(R.id.loading);

        Glide.with(this).load(R.drawable.p_loading_post).into(loading_notif);
        loading_notif.setVisibility(View.VISIBLE);

        new_notif.setVisibility(View.GONE);
        notifManager.cancelAll();

        GetNotification();

        broadcastReceiverNet = new BroadCastInternt();

        Intent intent1 = new Intent();
        intent1.addCategory(Intent.CATEGORY_DEFAULT);
        intent1.setAction("ir.cenlearn.filmbin.BroadCasts.BroadCastInternt");
        sendBroadcast(intent1);

        broadcastIntent();

        preferences = getSharedPreferences("prefs",MODE_PRIVATE);

        navigationView.setSelectedItemId(R.id.notification_menu);
        Typeface myTypeFace = Typeface.createFromAsset(getApplication().getAssets(), "fonts/font.ttf");

        CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan("fonts/font.ttf", myTypeFace);
        for (int i = 0; i <navigationView.getMenu().size(); i++) {
            MenuItem menuItem = navigationView.getMenu().getItem(i);
            SpannableStringBuilder spannableTitle = new SpannableStringBuilder(menuItem.getTitle());
            spannableTitle.setSpan(typefaceSpan, 0, spannableTitle.length(), 0);
            menuItem.setTitle(spannableTitle);
        }

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.addpost_menu){
                    Intent intent = new Intent(NotifActivity.this, AddPostsActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0,0); //0 for no animation


                }else if (id == R.id.home_menu){
                    finish();
                    overridePendingTransition(0,0); //0 for no animation


                }else if (id == R.id.search_user_menu){
                    Intent intent = new Intent(NotifActivity.this, SearchActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0,0); //0 for no animation

                }else if (id == R.id.explore_menu) {
                    Intent intent = new Intent(NotifActivity.this, ExploreActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0); //0 for no animation
                }
                return false;

            }
        });
    }

    public void GetNotification(){
        ApiServiceNotification apiServiceNotification = new ApiServiceNotification(this);

        apiServiceNotification.getNotifications(new ApiServiceNotification.OnNotificationReceived() {
            @Override
            public void onReceived(List<Notification> notifications) {

                RecyclerView.ItemDecoration itemDecoration;
                while (recyclerView_notification.getItemDecorationCount() > 0
                        &&(itemDecoration = recyclerView_notification.getItemDecorationAt(0)) != null) {
                    recyclerView_notification.removeItemDecoration(itemDecoration);
                }

                notificationAdapter = new NotificationAdapter(notifications, NotifActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NotifActivity.this, RecyclerView.VERTICAL, false);
                recyclerView_notification.setLayoutManager(mLayoutManager);
                recyclerView_notification.setHasFixedSize(false);
                Collections.reverse(notifications);
                recyclerView_notification.setAdapter(notificationAdapter);
                notificationAdapter.notifyDataSetChanged();
                loading_notif.setVisibility(View.GONE);
                nonotification.setVisibility(View.GONE);
                if (notificationAdapter.getItemCount() == 0){
                    nonotification.setVisibility(View.VISIBLE);
                }
                ReadMessage();

            }
        });
    }

    public void ReadMessage(){
        AndroidNetworking.upload(Urls.host+Urls.readmessage+"?username="+preferences.getString("username",""))
                .addMultipartParameter("status", "خوانده شده")
                .setTag("POST")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }
    public void backarrow(View view) {
        finish();
        overridePendingTransition(0,0);
    }
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(
                0,
                R.anim.close_without_animation
        );
    }
    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiverNet, intentFilter);

        Intent startServiceIntent = new Intent(this, MainActivity.class);
        startService(startServiceIntent);

    }
    public void broadcastIntent() {
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
}