package ir.cenlearn.alonegram.BroadCasts;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import ir.cenlearn.alonegram.APIs.ApiServiceHome;
import ir.cenlearn.alonegram.APIs.ApiServiceNotification;
import ir.cenlearn.alonegram.Activities.MainActivity;
import ir.cenlearn.alonegram.Adapter.NotificationAdapter;
import ir.cenlearn.alonegram.Adapter.PostsAdapter;
import ir.cenlearn.alonegram.Model.Notification;
import ir.cenlearn.alonegram.Model.PostModel;

import java.util.Collections;
import java.util.List;

import static ir.cenlearn.alonegram.Activities.NotifActivity.loading_notif;
import static ir.cenlearn.alonegram.Activities.NotifActivity.nonotification;
import static ir.cenlearn.alonegram.Activities.NotifActivity.notificationAdapter;
import static ir.cenlearn.alonegram.Activities.NotifActivity.recyclerView_notification;
import static ir.cenlearn.alonegram.fragments.MainFragment.loading;
import static ir.cenlearn.alonegram.fragments.MainFragment.postsAdapter;
import static ir.cenlearn.alonegram.fragments.MainFragment.preferences;
import static ir.cenlearn.alonegram.fragments.MainFragment.recyclerView_posts;
import static ir.cenlearn.alonegram.fragments.MainFragment.postCunt;


public class BroadCastInternt extends BroadcastReceiver {
    public static boolean isVersionM;

    @TargetApi(Build.VERSION_CODES.M)
    public void onReceive(final Context context, Intent intent) {

        isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);

        try {
            if (isVersionM && isOnline(context)) {

            }else if (isVersionM){
                ApiServiceHome apiServiceHome =new ApiServiceHome(context);
                apiServiceHome.getPosts(new ApiServiceHome.OnPostsReceived() {
                    @Override
                    public void onReceived(List<PostModel> postModels) {

                        RecyclerView.ItemDecoration itemDecoration;
                        while (recyclerView_posts.getItemDecorationCount() > 0 &&(itemDecoration = recyclerView_posts.getItemDecorationAt(0)) != null) {
                            recyclerView_posts.removeItemDecoration(itemDecoration);
                        }

                        postsAdapter = new PostsAdapter(postModels, context);
                        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
                        recyclerView_posts.setLayoutManager(mLayoutManager);
                        recyclerView_posts.setHasFixedSize(false);
                        Collections.reverse(postModels);
                        recyclerView_posts.setAdapter(postsAdapter);
                        postsAdapter.notifyDataSetChanged();
                        loading.setVisibility(View.GONE);
                        preferences.edit().putString("postcunt", String.valueOf(postsAdapter.getItemCount())).apply();
                        postCunt.setText(preferences.getString("postcunt",""));

                    }
                });


                try {
                    ApiServiceNotification apiServiceNotification = new ApiServiceNotification(context);

                    apiServiceNotification.getNotifications(new ApiServiceNotification.OnNotificationReceived() {
                        @Override
                        public void onReceived(List<Notification> notifications) {

                            if (MainActivity.username_notification == null){
                                nonotification.setVisibility(View.VISIBLE);
                                loading_notif.setVisibility(View.GONE);

                            }else if (!MainActivity.username_notification.toLowerCase().equals(preferences.getString("username","").toLowerCase())){
                                nonotification.setVisibility(View.VISIBLE);
                                loading_notif.setVisibility(View.GONE);

                            }else {
                                loading_notif.setVisibility(View.GONE);
                                nonotification.setVisibility(View.GONE);
                            }

                            RecyclerView.ItemDecoration itemDecoration;
                            while (recyclerView_notification.getItemDecorationCount() > 0
                                    &&(itemDecoration = recyclerView_notification.getItemDecorationAt(0)) != null) {
                                recyclerView_notification.removeItemDecoration(itemDecoration);
                            }

                            notificationAdapter = new NotificationAdapter(notifications, context);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                            recyclerView_notification.setLayoutManager(mLayoutManager);
                            recyclerView_notification.setHasFixedSize(false);
                            Collections.reverse(notifications);
                            recyclerView_notification.setAdapter(notificationAdapter);
                            notificationAdapter.notifyDataSetChanged();
                            loading_notif.setVisibility(View.GONE);
                        }
                    });


                }catch (Exception e){return;}



            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }


    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }

    }

}
