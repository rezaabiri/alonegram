package ir.cenlearn.alonegram.APIs;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import ir.cenlearn.alonegram.Model.Notification;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;
import ir.cenlearn.alonegram.fragments.MainFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static ir.cenlearn.alonegram.Activities.NotifActivity.loading_notif;
import static ir.cenlearn.alonegram.Activities.NotifActivity.nonotification;

public class ApiServiceNotification {

    private Context context;

    public ApiServiceNotification(Context context) {
        this.context = context;

    }

    public void getNotifications(final OnNotificationReceived OnNotifReceived) {
        String username = MainFragment.preferences.getString("username","").toLowerCase();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.sendnotif+"?username="+username.toLowerCase(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Notification> notifications = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    Notification notification  = new Notification();
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        notification.setUsername(jsonObject.getString("username"));
                        notification.setTitle(jsonObject.getString("title"));
                        notification.setBody(jsonObject.getString("body"));
                        notification.setDatetime(jsonObject.getString("shamsi"));
                        notification.setStatus(jsonObject.getString("status"));
                        notification.setId(jsonObject.getString("id"));
                        notification.setPostID(jsonObject.getString("post_id"));
                        notification.setImageUrl(jsonObject.getString("videoimage"));
                        notification.setAccount(jsonObject.getString("account"));
                        notifications.add(notification);

                    } catch (JSONException e) {
                        nonotification.setVisibility(View.VISIBLE);
                        loading_notif.setVisibility(View.GONE);
                    }
                }
                OnNotifReceived.onReceived(notifications);
            }
        }, error -> {
            try {
                nonotification.setVisibility(View.INVISIBLE);
                loading_notif.setVisibility(View.VISIBLE);
                loading_notif.setImageResource(R.drawable.refresh);
                loading_notif.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Glide.with(context).load(R.drawable.p_loading_post).into(loading_notif);
                        getNotifications(OnNotifReceived);
                        //nonotification.setVisibility(View.VISIBLE);
                    }});

            }catch (Exception e){
                return;
            }

        });

        request.setRetryPolicy(new DefaultRetryPolicy(18000, 5, 5));
        Volley.newRequestQueue(context).add(request);

    }

    public interface OnNotificationReceived{
        void onReceived(List<Notification> notifications);
    }
}
