package ir.cenlearn.alonegram.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ir.cenlearn.alonegram.Activities.ShowResultSearch;
import ir.cenlearn.alonegram.Model.Comment;
import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;

import static ir.cenlearn.alonegram.Activities.CommentActivity.edit_sendcomment;
import static ir.cenlearn.alonegram.Activities.CommentActivity.frame_replay;
import static ir.cenlearn.alonegram.Activities.CommentActivity.replay_preview;
import static ir.cenlearn.alonegram.Activities.CommentActivity.target_replay;
import static ir.cenlearn.alonegram.Activities.ShowResultClickPost.username_post;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    List<Comment> comments;
    Context mContext;
    public static SharedPreferences preferences;
    public static String idpost,preview_replay;
    LayoutInflater inflater;

    public CommentAdapter(List<Comment> comments, Context mContext) {
        this.comments = comments;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View aView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
        return new MyViewHolder(aView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Comment comment = comments.get(position);

        Picasso.get().load(Urls.host+comment.getImageComment()).placeholder(R.drawable.p_profile).centerCrop().fit().into(holder.aImageUser);


        holder.aComment.setText(StringEscapeUtils.unescapeJson(StringEscapeUtils.unescapeJava(comment.getComment())));
        holder.aUsername.setText(comment.getUsername());
        holder.aDatetime.setText(comment.getDatetime());

        if (comment.getBluetick().equals("active")){
            holder.bluetick.setVisibility(View.VISIBLE);
        }
        if (!comment.getTargetReplay().equals("null") && !comment.getTargetReplay().equals("") && !comment.getPreviewReplay().equals("")){
            holder.username_replay.setText(comment.getTargetReplay());
            holder.preview_replay.setText(StringEscapeUtils.unescapeJava(comment.getPreviewReplay()));
            holder.preview_replay.setVisibility(View.VISIBLE);

        }else{
            holder.username_replay.setVisibility(View.GONE);
            holder.preview_replay.setVisibility(View.GONE);
        }
        idpost = comment.getPost_id();

        holder.replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target_replay.setText("@"+comment.getUsername());
                frame_replay.setVisibility(View.VISIBLE);
                replay_preview.setText(StringEscapeUtils.unescapeJava(comment.getComment()));
                preview_replay = comment.getComment();

                if(edit_sendcomment.requestFocus()){
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edit_sendcomment,InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        holder.send_report_comment.setOnClickListener(v -> {
            final AlertDialog dialogBuilder = new AlertDialog.Builder(mContext).create();
            LayoutInflater inflater;
            inflater = LayoutInflater.from(mContext);
            View dialogView = inflater.inflate(R.layout.layout_report, null);
            dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogBuilder.getWindow().getAttributes().windowAnimations = R.style.DialogThemeMenu;


            Button btn_send_report;
            final EditText edit_subject, edit_body;
            ProgressBar progressBar;

            progressBar = dialogView.findViewById(R.id.edit_progress_report);
            edit_subject = dialogView.findViewById(R.id.edit_subject_report);
            edit_body = dialogView.findViewById(R.id.edit_body_report);
            btn_send_report = dialogView.findViewById(R.id.btn_send_report);

            btn_send_report.setOnClickListener(v1 -> {
                SharedPreferences preferences_comment = mContext.getSharedPreferences("prefs",Context.MODE_PRIVATE);
                String usernames = preferences_comment.getString("username","")+" -> "+ comment.getUsername();
                progressBar.setVisibility(View.VISIBLE);
                btn_send_report.setText("");
                if (!edit_subject.getText().toString().isEmpty() && !edit_body.getText().toString().isEmpty()){
                    AndroidNetworking.post(Urls.host+Urls.report_api)
                            .addBodyParameter("id", comment.getCommentid())
                            .addBodyParameter("subject", edit_subject.getText().toString().replace("'","&#39;"))
                            .addBodyParameter("body", edit_body.getText().toString().replace("'","&#39;"))
                            .addBodyParameter("username", usernames)
                            .setTag("REPORT")
                            .build()
                            .getAsJSONArray(new JSONArrayRequestListener() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        for (int i = 0; i < response.length(); i++) {
                                            JSONObject postArray = response.getJSONObject(i);
                                            if (postArray.getString("status").equals("sent")) {
                                                dialogBuilder.dismiss();
                                                Toaster.ToastSuccess(mContext, R.string.reportsubmit);
                                                progressBar.setVisibility(View.GONE);
                                                btn_send_report.setText("ارسال گزارش");
                                            } else {
                                                Toaster.ToastError(mContext, R.string.reportnotsend);
                                                progressBar.setVisibility(View.GONE);
                                                btn_send_report.setText("ارسال گزارش");
                                            }
                                        }

                                    }catch (Exception e ){return;}

                                }
                                @Override
                                public void onError(ANError anError) {
                                    Toaster.ToastError(mContext, R.string.servererror);
                                    btn_send_report.setText("ارسال گزارش");
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }else {
                    Toaster.ToastErrorNormal(mContext,"موارد خواسته شده را تکمیل  کنید");
                    btn_send_report.setText("ارسال گزارش");
                    progressBar.setVisibility(View.GONE);
                }


            });
            dialogBuilder.setView(dialogView);
            dialogBuilder.show();
        });

        holder.username_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.search_user+"?username="+holder.username_replay.getText().toString().replace("@","").trim(), null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject=response.getJSONObject(i);

                                Intent intent = new Intent(mContext, ShowResultSearch.class);

                                intent.putExtra("Avatar", jsonObject.getString("image"));
                                intent.putExtra("Name", jsonObject.getString("username"));
                                intent.putExtra("Fullname", jsonObject.getString("fullname").replace("&#39;", "'"));
                                intent.putExtra("bluetick", jsonObject.getString("bluetick"));

                                mContext.startActivity(intent);

                            } catch (JSONException e) {
                                Toaster.ToastErrorNormal(mContext,"کاربر وجود ندارد");
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { }
                });

                request.setRetryPolicy(new DefaultRetryPolicy(18000, 5, 5));
                Volley.newRequestQueue(mContext).add(request);

            }
        });
        holder.aCommentLinear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    SharedPreferences preferences = mContext.getSharedPreferences("prefs",Context.MODE_PRIVATE);
                    if (username_post.getText().toString().toLowerCase().equals(preferences.getString("username","").toLowerCase()) || comment.getUsername().toLowerCase().equals(preferences.getString("username","").toLowerCase())) {

                        final AlertDialog dialogBuilder = new AlertDialog.Builder(mContext).create();
                        inflater = LayoutInflater.from(mContext);
                        View dialogView = inflater.inflate(R.layout.layout_delete_comment, null);
                        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        Button btn_delete = dialogView.findViewById(R.id.btn_delete_comment);

                        btn_delete.setOnClickListener(v1 -> {

                            AndroidNetworking.upload(Urls.host+Urls.delcomment)
                                    .addMultipartParameter("commentid", comment.getCommentid())
                                    .addMultipartParameter("comment", StringEscapeUtils.escapeJava(comment.getComment()))
                                    .setTag("COMMENT")
                                    .build()
                                    .getAsString(new StringRequestListener() {
                                        @Override
                                        public void onResponse(String response){
                                            comments.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position,comments.size());
                                            notifyDataSetChanged();
                                        }
                                        @Override
                                        public void onError(ANError anError){}
                                    });
                            dialogBuilder.dismiss();
                        });

                        dialogBuilder.setView(dialogView);
                        dialogBuilder.show();
                    }else if (preferences.getString("username","").toLowerCase().equals("alonegram")){

                        final AlertDialog dialogBuilder = new AlertDialog.Builder(mContext).create();
                        inflater = LayoutInflater.from(mContext);
                        View dialogView = inflater.inflate(R.layout.layout_delete_comment, null);
                        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        Button btn_delete = dialogView.findViewById(R.id.btn_delete_comment);

                        btn_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AndroidNetworking.upload(Urls.host+Urls.delcomment)
                                        .addMultipartParameter("commentid", comment.getCommentid())
                                        .addMultipartParameter("comment", StringEscapeUtils.escapeJava(comment.getComment()))
                                        .setTag("COMMENT")
                                        .build()
                                        .getAsString(new StringRequestListener() {
                                            @Override
                                            public void onResponse(String response){
                                                comments.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position,comments.size());
                                                notifyDataSetChanged();
                                            }
                                            @Override
                                            public void onError(ANError anError){}
                                        });
                                dialogBuilder.dismiss();
                            }
                        });

                        dialogBuilder.setView(dialogView);
                        dialogBuilder.show();
                    }else {

                        Intent intent = new Intent(mContext,ShowResultSearch.class);
                        intent.putExtra("Name",comment.getUsername());
                        intent.putExtra("Avatar",comment.getImageComment());
                        intent.putExtra("Fullname",comment.getFullname());
                        intent.putExtra("bluetick",comment.getBluetick());
                        mContext.startActivity(intent);

                    }
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView aImageUser, bluetick;
        public TextView aUsername, aDatetime, aComment, username_replay, replay, preview_replay, send_report_comment;
        public LinearLayout aCommentLinear;


        public MyViewHolder(View itemView) {
            super(itemView);

            aComment = itemView.findViewById(R.id.commenttext);
            aUsername = itemView.findViewById(R.id.username_comment);
            aImageUser = itemView.findViewById(R.id.user_image_commment);
            aDatetime = itemView.findViewById(R.id.date_comment);
            username_replay = itemView.findViewById(R.id.username_replay);
            replay = itemView.findViewById(R.id.replay);
            send_report_comment = itemView.findViewById(R.id.send_report_comment);
            preview_replay = itemView.findViewById(R.id.preview_replay);
            aCommentLinear = itemView.findViewById(R.id.linear_comment);
            bluetick = itemView.findViewById(R.id.bluetick_in_comment);

        }
    }



}
