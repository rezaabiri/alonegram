package ir.cenlearn.alonegram.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ir.cenlearn.alonegram.Activities.ShowResultClickPost;
import ir.cenlearn.alonegram.Model.Notification;
import ir.cenlearn.alonegram.Others.ProgressCustom;
import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    List<Notification> notifications;
    Context mContext;
    public static String username_notif;
    LayoutInflater inflater;

    ProgressCustom progressCustom = new ProgressCustom();

    public NotificationAdapter(List<Notification> notifications, Context mContext) {
        this.notifications = notifications;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View aView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message, parent, false);
        return new MyViewHolder(aView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Notification notifitems = notifications.get(position);

        if (notifitems.getStatus().equals("پیام جدید")){

            //holder.status_readable.setBackgroundResource(R.drawable.not_read);
            holder.status.setTextColor(Color.parseColor("#ffffff"));
            holder.status.setText("پیام جدید");

        }else if (notifitems.getStatus().equals("خوانده شده")){

           // holder.status_readable.setBackgroundResource(R.drawable.read);
            holder.status.setText("خوانده شده");
            holder.status.setTextColor(Color.parseColor("#939393"));
            holder.fram_new_msg.setBackgroundColor(Color.parseColor("#00ffffff"));

        }

        holder.Title.setText(notifitems.getTitle());
        holder.Body.setText(StringEscapeUtils.unescapeJava(notifitems.getBody().replace("&#39;","'")));
        holder.datetime.setText(notifitems.getDatetime());

        if (notifitems.getImageUrl().equals("null")){
            holder.image_preview.setVisibility(View.GONE);
            holder.post_id.setText(notifitems.getPostID());
            //holder.notiflinear.setEnabled(false);
            //holder.notiflinear.setClickable(false);
            //holder.notiflinear.setActivated(false);

        }if (notifitems.getPostID().equals("") || notifitems.getPostID().equals("null")){

            holder.image_preview.setVisibility(View.GONE);
            holder.post_id.setText(notifitems.getAccount());
           // holder.notiflinear.setEnabled(false);
           // holder.notiflinear.setClickable(false);
           // holder.notiflinear.setActivated(false);

        }else {
            Picasso.get().load(Urls.host+notifitems.getImageUrl()).fit().centerCrop().into(holder.image_preview);
        }

        username_notif = notifitems.getUsername();


        holder.notiflinear.setOnLongClickListener(v -> {

            try {
                final AlertDialog dialogBuilder = new AlertDialog.Builder(mContext).create();
                inflater = LayoutInflater.from(mContext);
                View dialogView = inflater.inflate(R.layout.layout_delete_notification, null);
                dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Button btn_delete = dialogView.findViewById(R.id.btn_delete_notification);

                btn_delete.setOnClickListener(v1 -> {

                    AndroidNetworking.upload(Urls.host+Urls.delnotif)
                            .addMultipartParameter("notifid", notifitems.getId())
                            .setTag("COMMENT")
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    notifications.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, notifications.size());
                                }

                                @Override
                                public void onError(ANError anError) {
                                }
                            });
                    dialogBuilder.dismiss();
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        });
        
        holder.notiflinear.setOnClickListener(v -> {

            if (!notifitems.getPostID().equals("0")){
                progressCustom.show(mContext);
                JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.show_post_for_comment+"?post_id="+notifitems.getPostID(), null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject=response.getJSONObject(i);

                                Intent intent = new Intent(mContext, ShowResultClickPost.class);

                                intent.putExtra("video_image", jsonObject.getString("videoimage"));
                                intent.putExtra("video_name", jsonObject.getString("videoname").replace("&#39;", "'"));
                                intent.putExtra("video_category", jsonObject.getString("videocategory").replace("&#39;", "'"));
                                intent.putExtra("username", jsonObject.getString("username"));
                                intent.putExtra("fullname", jsonObject.getString("fullname").replace("&#39;", "'"));
                                intent.putExtra("image", jsonObject.getString("image"));
                                intent.putExtra("videodesc", jsonObject.getString("videodescription").replace("&#39;", "'"));
                                intent.putExtra("bluetick", jsonObject.getString("bluetick"));
                                intent.putExtra("viewpost", jsonObject.getString("view"));
                                intent.putExtra("likepost", jsonObject.getString("likes"));
                                intent.putExtra("id", jsonObject.getString("idpost"));
                                intent.putExtra("datetime", jsonObject.getString("shamsi"));
                                intent.putExtra("videoid", jsonObject.getString("videoid"));
                                intent.putExtra("0","0");

                                mContext.startActivity(intent);
                                progressCustom.dismiss();

                                notifitems.setStatus("خوانده شده");

                                holder.status.setText("خوانده شده");
                                holder.status.setTextColor(Color.parseColor("#939393"));
                                holder.fram_new_msg.setBackgroundColor(Color.parseColor("#00ffffff"));

                            } catch (JSONException e) {
                                Toaster.ToastError(mContext,R.string.post_not_exists);
                                progressCustom.dismiss();
                            }
                        }
                    }
                }, error -> { });

                request.setRetryPolicy(new DefaultRetryPolicy(18000, 5, 5));
                Volley.newRequestQueue(mContext).add(request);
            }
        });
    }


    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView Title, Body, datetime, status, post_id, account_txt;
        public LinearLayout notiflinear;
        FrameLayout fram_new_msg;
        ImageView status_readable;
        RoundedImageView image_preview;


        public MyViewHolder(View itemView) {
            super(itemView);

            Title = itemView.findViewById(R.id.msg_title);
            Body = itemView.findViewById(R.id.msg_body);
            notiflinear = itemView.findViewById(R.id.NotifLinear);
            datetime = itemView.findViewById(R.id.datetime);
            status_readable = itemView.findViewById(R.id.read_not_read);
            status = itemView.findViewById(R.id.status);
            image_preview = itemView.findViewById(R.id.image_preview);
            fram_new_msg = itemView.findViewById(R.id.frame_new_msg);
            post_id = itemView.findViewById(R.id.post_id_txt);
            account_txt = itemView.findViewById(R.id.account_txt);

        }
    }
}
