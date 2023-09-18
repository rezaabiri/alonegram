package ir.cenlearn.alonegram.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.cenlearn.alonegram.APIs.ApiGetComments;
import ir.cenlearn.alonegram.APIs.ApiSendComment;
import ir.cenlearn.alonegram.Adapter.CommentAdapter;
import ir.cenlearn.alonegram.GetTime.Utilities;
import ir.cenlearn.alonegram.Model.Comment;
import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;

import static ir.cenlearn.alonegram.Adapter.CommentAdapter.preview_replay;

public class CommentActivity extends AppCompatActivity {

    public static EditText edit_sendcomment;
    public static TextView target_replay, replay_preview;
    public static FrameLayout frame_replay;
    public static LinearLayout linear_nocomment;

    public static ImageView imgsend, cancel_replay, loading_comments;
    SharedPreferences preferences;
    Bundle extras;
    String datetime, unicode, edit;
    CommentAdapter commentAdapter;
    public static RecyclerView recyclerView_comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        AndroidNetworking.initialize(this);

        edit_sendcomment = findViewById(R.id.sendcomment);
        imgsend = findViewById(R.id.imgsend);
        recyclerView_comment = findViewById(R.id.recyclerview_comment);
        target_replay = findViewById(R.id.target_replay);
        frame_replay = findViewById(R.id.Frame_replay);
        cancel_replay = findViewById(R.id.cancel_replay);
        loading_comments = findViewById(R.id.loading_comments);
        linear_nocomment = findViewById(R.id.nocommentyet);
        replay_preview = findViewById(R.id.replay_preview);

        Glide.with(this).load(R.drawable.p_loading_post).into(loading_comments);
        loading_comments.setVisibility(View.VISIBLE);

        getComments();

        preferences = getSharedPreferences("prefs",MODE_PRIVATE);

        Intent intent = getIntent();
        extras = intent.getExtras();

        imgsend.setOnClickListener(v -> {
            if (!edit_sendcomment.getText().toString().isEmpty()){
                edit = edit_sendcomment.getText().toString();
                Date date = Calendar.getInstance().getTime();
                SendComment(StringEscapeUtils.escapeJava(StringEscapeUtils.escapeJava(edit_sendcomment.getText().toString()).replace("'","&#39;")));
                String unicodeStr = StringEscapeUtils.escapeJava(edit_sendcomment.getText().toString());
                datetime = Utilities.getCurrentShamsidate() +" || "+date.getHours() +":"+date.getMinutes();
                unicode = StringEscapeUtils.escapeJava(unicodeStr);
                SendAlarmComment(getIntent().getStringExtra("usernamePost"),StringEscapeUtils.escapeCsv(StringEscapeUtils.escapeJava(edit_sendcomment.getText().toString())),getIntent().getStringExtra("id"));
                if (!target_replay.getText().toString().replace("@","").trim().isEmpty()){
                    SendAlarmReplay(target_replay.getText().toString().replace("@","").trim(), StringEscapeUtils.escapeCsv(StringEscapeUtils.escapeJava(edit_sendcomment.getText().toString())),getIntent().getStringExtra("id"));
                }

                edit_sendcomment.getText().clear();

            }

        });

        cancel_replay.setOnClickListener(v -> {
            target_replay.setText("");
            frame_replay.setVisibility(View.GONE);
        });
    }

    public void SendComment(String comment){

        final HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("username", preferences.getString("username", ""));
        postParams.put("idpost", getIntent().getStringExtra("id"));
        postParams.put("comment", comment);
        postParams.put("target_replay", target_replay.getText().toString());
        postParams.put("preview_replay", StringEscapeUtils.escapeJava(preview_replay));

        ApiSendComment apiSendComment = new ApiSendComment(CommentActivity.this);
        apiSendComment.SendComment(postParams, success -> {
            if (success){
                getComments();
            }else {
                Toaster.ToastError(getApplicationContext(),R.string.servererror);
            }

        });
    }

    public void getComments() {

        ApiGetComments getComments = new ApiGetComments(CommentActivity.this);
        getComments.getComments(getIntent().getStringExtra("id"), comments -> {
            try {
                commentAdapter = new CommentAdapter(comments, CommentActivity.this);
                GridLayoutManager layoutManager = new GridLayoutManager(CommentActivity.this,1);
                recyclerView_comment.setLayoutManager(layoutManager);
                recyclerView_comment.setHasFixedSize(false);
                recyclerView_comment.setAdapter(commentAdapter);
                commentAdapter.notifyDataSetChanged();
                loading_comments.setVisibility(View.GONE);
                linear_nocomment.setVisibility(View.GONE);
                target_replay.setText("");
                frame_replay.setVisibility(View.GONE);
            }catch (Exception e){
                loading_comments.setVisibility(View.GONE);
                linear_nocomment.setVisibility(View.VISIBLE);
            }
        });
    }

    public void SendAlarmComment(String username, String Comment, String postid){
        AndroidNetworking.upload(Urls.host+Urls.message_api)
                .addMultipartParameter("username",username)
                .addMultipartParameter("title", "نظر جدید از طرف : "+preferences.getString("username",""))
                .addMultipartParameter("body", Comment)
                .addMultipartParameter("datetime", datetime)
                .addMultipartParameter("status", "پیام جدید")
                .addMultipartParameter("post_id", postid)
                .setTag("SEND")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) { }
                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    public void SendAlarmReplay(String username, String Comment, String postid){
        AndroidNetworking.upload(Urls.host+Urls.message_api)
                .addMultipartParameter("username",username)
                .addMultipartParameter("title", "به نظر شما پاسخ داد : "+preferences.getString("username",""))
                .addMultipartParameter("body", Comment)
                .addMultipartParameter("datetime", datetime)
                .addMultipartParameter("status", "پیام جدید")
                .addMultipartParameter("post_id", postid)
                .setTag("SEND")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) { }
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
}