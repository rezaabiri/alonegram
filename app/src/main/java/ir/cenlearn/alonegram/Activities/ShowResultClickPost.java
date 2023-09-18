package ir.cenlearn.alonegram.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.codegames.aparatview.AparatView;

import ir.cenlearn.alonegram.Model.Comment;
import ir.cenlearn.alonegram.Others.ProgressCustom;
import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ShowResultClickPost extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static TextView video_name, username_post, name_post, like_count, date_post, comment_count, title_desc, view_count;
    String image, name, category, username, fullname, imageinpost, videodesc, bluetickpost, videoid, viewpost, likepoststring, id, token;
    ImageView menu_in_post, comment, like_preview, videoimage, userimageinpost, bluetick, img_views;
    LikeButton likepost, save;
    LinearLayout gotopage, linear_aparat_video;
    AparatView aparatView;
    CardView cardvideo;
    FrameLayout notfound;
    Button btn_delete;
    Bundle extras;
    JSONObject postArray;
    boolean state;
    public static String resp;

    int i = 0;

    SharedPreferences preferences_1, preferences_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_click_post);

        videoimage = findViewById(R.id.clickpost_videoimage);
        video_name = findViewById(R.id.clickpost_videoname);
        save = findViewById(R.id.save_post);
        userimageinpost = findViewById(R.id.userimageinpost);
        username_post = findViewById(R.id.usernameinpost);
        name_post = findViewById(R.id.nameinpost);
        likepost = findViewById(R.id.like_post_outline);
        like_count = findViewById(R.id.likecunt);
        view_count = findViewById(R.id.viewpostcunt);
        comment_count = findViewById(R.id.commentcunt);
        gotopage = findViewById(R.id.gotopage);
        aparatView = findViewById(R.id.aparat);
        cardvideo = findViewById(R.id.cardvideo);
        notfound = findViewById(R.id.show_notfound);
        menu_in_post = findViewById(R.id.menu_in_post);
        date_post = findViewById(R.id.datetimepost);
        comment = findViewById(R.id.comment);
        linear_aparat_video = findViewById(R.id.linear_aparat_video);
        like_preview = findViewById(R.id.like_preview);
        bluetick = findViewById(R.id.bluetick_in_post);
        img_views = findViewById(R.id.img_views);
        title_desc = findViewById(R.id.title_desc);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                AndroidNetworking.initialize(getApplicationContext());


                SendLike();
                GoToUserPage();
                SendView();
                GetStateLikes();
                GetCuntComment();
                GetStateSave();
                GetViewsAndShow();
                GetCountLike();
                GetTimeNow();

                preferences_1 = getSharedPreferences("prefs",MODE_PRIVATE);
                preferences_2 = getSharedPreferences("User_name",MODE_PRIVATE);

                date_post.setText(preferences_1.getString("timenow",""));

                menu_in_post.setOnClickListener(v -> MenuActionPost());

                Intent intent = getIntent();
                extras = intent.getExtras();

                if (extras != null){

                    image = extras.getString("video_image");
                    name = extras.getString("video_name");
                    category = extras.getString("video_category");
                    username = extras.getString("username");
                    fullname = extras.getString("fullname");
                    imageinpost = extras.getString("image");
                    videodesc = extras.getString("videodesc");
                    bluetickpost = extras.getString("bluetick");
                    videoid = extras.getString("videoid");
                    viewpost = extras.getString("viewpost");
                    likepoststring = extras.getString("likepost");
                    id = extras.getString("id");
                    token = extras.getString("token");
                    String zero = extras.getString("0");

                    Picasso.get().load(Urls.host+image).error(R.drawable.p_profile).fit().into(videoimage);
                    Picasso.get().load(Urls.host+ imageinpost).error(R.drawable.p_profile).fit().centerCrop().into(userimageinpost);

                    try {
                        video_name.setText(StringEscapeUtils.unescapeJava(name.replace("&#39;","'")));
                        username_post.setText(username);
                        name_post.setText(fullname.replace("&#39;","'"));
                        title_desc.append(StringEscapeUtils.unescapeJava(videodesc.replace("&#39;","'")));
                        //video_description.setText(StringEscapeUtils.unescapeJava(videodesc.replace("&#39;","'")));
                        view_count.setText(viewpost);
                        like_count.setText(likepoststring);
                        like_count.setText(zero);
                        view_count.setText(zero);


                        if (bluetickpost.equals("active")){bluetick.setVisibility(View.VISIBLE);}
                        if (!videoid.equals("")){
                            notfound.setVisibility(View.GONE);
                            aparatView.setVideoId(videoid);
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        }else {linear_aparat_video.setVisibility(View.GONE);}
                    }catch (Exception e){
                        linear_aparat_video.setVisibility(View.GONE);
                    }
                }
                videoimage.setOnClickListener(v -> {
                    i++;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (i == 2){
                                likepost.onClick(v);
                                if (likepost.isLiked()){
                                    like_preview.setVisibility(View.VISIBLE);
                                    Animation FadeIn = AnimationUtils.loadAnimation(ShowResultClickPost.this, R.anim.fade_in);
                                    like_preview.setAnimation(FadeIn);
                                }
                                Handler likePreview = new Handler();
                                likePreview.postDelayed(() -> {
                                    Animation FadeOut = AnimationUtils.loadAnimation(ShowResultClickPost.this, R.anim.fade_out);
                                    like_preview.setAnimation(FadeOut);
                                    like_preview.setVisibility(View.INVISIBLE);

                                },1000);
                            }
                            i = 0;
                        }
                    },300);
                });
                comment.setOnClickListener(v ->{
                    Intent i = new Intent(ShowResultClickPost.this, CommentActivity.class);
                    i.putExtra("idpost",id);
                    i.putExtra("usernamePost",username);
                    i.putExtra("id",id);
                    startActivity(i);
                });
                save.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        AndroidNetworking.upload(Urls.host+"save_posts.php")
                                .addMultipartParameter("postid",getIntent().getStringExtra("id"))
                                .addMultipartParameter("token",preferences_1.getString("username",""))
                                .setTag("saveposts")
                                .setPriority(Priority.HIGH)
                                .build()
                                .getAsJSONArray(new JSONArrayRequestListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {}
                                    @Override
                                    public void onError(ANError anError) {
                                        Toaster.ToastError(getApplicationContext(),R.string.servererror);
                                    }
                                });
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        AndroidNetworking.upload(Urls.host+"delete_save_posts.php")
                                .addMultipartParameter("postid",getIntent().getStringExtra("id"))
                                .addMultipartParameter("token",preferences_1.getString("username",""))
                                .setTag("saveposts")
                                .setPriority(Priority.HIGH)
                                .build().getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {}

                            @Override
                            public void onError(ANError anError) {
                                Toaster.ToastError(getApplicationContext(),R.string.servererror);
                            }
                        });
                    }
                });

                GetTimeNow();
            }
        },300);

    }

    public void GetTimeNow(){
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.show_post_for_comment+"?post_id="+getIntent().getStringExtra("id"), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        date_post.setText(jsonObject.getString("shamsi"));
                        preferences_1.edit().putString("timenow",jsonObject.getString("shamsi")).apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, error -> {});
        request.setRetryPolicy(new DefaultRetryPolicy(20000,1,1));
        Volley.newRequestQueue(ShowResultClickPost.this).add(request);
    }
    public void GetViewsAndShow(){
        img_views.setOnClickListener(v -> {
            Intent intent = new Intent(ShowResultClickPost.this, ShowViewsActivity.class);
            intent.putExtra("id",getIntent().getStringExtra("id"));
            startActivity(intent);
        });
    }
    public void MenuActionPost(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_menu_post_action, null);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialogBuilder.getWindow().getAttributes().windowAnimations = R.style.DialogThemeMenu;

        TextView btn_edit_post, btn_send_report, btn_delete_post, btn_download_image, btn_rate_to_account;

        btn_edit_post = dialogView.findViewById(R.id.btn_edit_post);
        btn_send_report = dialogView.findViewById(R.id.btn_send_report);
        btn_delete_post = dialogView.findViewById(R.id.btn_delete_post);
        btn_download_image = dialogView.findViewById(R.id.btn_download_image);
        btn_rate_to_account = dialogView.findViewById(R.id.rate_to_account);

        try {
            if (preferences_1.getString("username", "").equals(username)){
                btn_delete_post.setVisibility(View.VISIBLE);
                btn_edit_post.setVisibility(View.VISIBLE);

            }else if (preferences_1.getString("username","").equals("alonegram")){
                btn_delete_post.setVisibility(View.VISIBLE);
                btn_edit_post.setVisibility(View.VISIBLE);

            }else {

                btn_delete_post.setVisibility(View.GONE);
                btn_edit_post.setVisibility(View.GONE);
            }

        }catch (Exception e){return;}



        btn_edit_post.setOnClickListener(v -> {
            EditPost();
            dialogBuilder.dismiss();
        });
        btn_send_report.setOnClickListener(v -> {
            SendReport();
            dialogBuilder.dismiss();
        });
        btn_delete_post.setOnClickListener(v -> {
            DeletePost();
            dialogBuilder.dismiss();
        });
        btn_download_image.setOnClickListener(v -> {
            downloadImageNew("alonegram-image",Urls.host+image);
            dialogBuilder.dismiss();
        });
        btn_rate_to_account.setOnClickListener(v -> {
            Rating();
            dialogBuilder.dismiss();
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }
    private void downloadImageNew(String filename, String downloadUrlOfImage){
        try{
            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".jpg");
            dm.enqueue(request);
            Toaster.ToastDownloadNormal(ShowResultClickPost.this,"دانلود تصویر شروع شد");
        }catch (Exception e){
            Toaster.ToastErrorNormal(ShowResultClickPost.this, "دانلود با خطا مواجه شد");
        }
    }
    public void GetStateSave(){

        SharedPreferences preferences = getSharedPreferences("prefs",MODE_PRIVATE);
        String url=Urls.host+Urls.get_state_save_posts+"?postid="+getIntent().getStringExtra("id")+"&token="+preferences.getString("username","");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        postArray = response.getJSONObject(i);
                        String status = postArray.getString("status");
                        if (status.equals("saved")){
                            save.setLiked(true);
                        }else {
                            save.setLiked(false);
                        }
                        //Log.i("onResp",postArray.getString("status"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
        });
        request.setRetryPolicy(new DefaultRetryPolicy(10000,5,5));
        Volley.newRequestQueue(getApplicationContext()).add(request);

    }
    public void SendReport(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
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

        btn_send_report.setOnClickListener(v -> {
            String usernames = preferences_1.getString("username","")+" -> "+ username;
            progressBar.setVisibility(View.VISIBLE);
            btn_send_report.setText("");
            if (!edit_subject.getText().toString().isEmpty() && !edit_body.getText().toString().isEmpty()){
                AndroidNetworking.post(Urls.host+Urls.report_api)
                        .addBodyParameter("id",String.valueOf(id))
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
                                        postArray = response.getJSONObject(i);
                                        if (postArray.getString("status").equals("sent")) {
                                            dialogBuilder.dismiss();
                                            Toaster.ToastSuccess(getApplicationContext(), R.string.reportsubmit);
                                            progressBar.setVisibility(View.GONE);
                                            btn_send_report.setText("ارسال گزارش");
                                        } else {
                                            Toaster.ToastError(getApplicationContext(), R.string.reportnotsend);
                                            progressBar.setVisibility(View.GONE);
                                            btn_send_report.setText("ارسال گزارش");
                                        }
                                    }

                                }catch (Exception e ){return;}

                            }
                            @Override
                            public void onError(ANError anError) {
                                Toaster.ToastError(getApplicationContext(), R.string.servererror);
                                btn_send_report.setText("ارسال گزارش");
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }else {
                Toaster.ToastErrorNormal(this,"موارد خواسته شده را تکمیل  کنید");
                btn_send_report.setText("ارسال گزارش");
                progressBar.setVisibility(View.GONE);
            }


        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
    public void DeletePost(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_delete_post, null);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBuilder.getWindow().getAttributes().windowAnimations = R.style.DialogThemeMenu;

        btn_delete = dialogView.findViewById(R.id.btn_delete);

        btn_delete.setOnClickListener(v -> AndroidNetworking.post(Urls.host+Urls.delete_post_api)
                .addBodyParameter("id",String.valueOf(id))
                .setTag("DELETE")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("[]")){
                            resp = "[]";
                            finish();

                        }else {
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        Toaster.ToastError(getApplicationContext(), R.string.servererror);
                    }
                }));

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
    public void EditPost(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();

        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBuilder.getWindow().getAttributes().windowAnimations = R.style.DialogThemeMenu;

        final View dialogView = inflater.inflate(R.layout.layout_edit_posts, null);

        final ProgressBar progressBar = dialogView.findViewById(R.id.edit_progressBar);
        final EditText edit_name = dialogView.findViewById(R.id.edit_namevideo);
        final EditText edit_desc = dialogView.findViewById(R.id.edit_descvideo);
        final EditText edit_videoid = dialogView.findViewById(R.id.edit_videoid);
        ImageView edit_imagevideo = dialogView.findViewById(R.id.edit_imagevideo);

        edit_name.setText(name);
        edit_desc.setText(videodesc);
        edit_videoid.setText(videoid);

        Picasso.get().load(Urls.host+image).error(R.drawable.image_broken_variant).fit().centerCrop().into(edit_imagevideo);

        final Button btn_edit = dialogView.findViewById(R.id.btn_edit);

        btn_edit.setOnClickListener(v -> {

            progressBar.setVisibility(View.VISIBLE);
            btn_edit.setText("");
            edit_name.setEnabled(false);
            edit_desc.setEnabled(false);
            edit_videoid.setEnabled(false);
            btn_edit.setEnabled(false);

            AndroidNetworking.post(Urls.host+Urls.edit_post_api)
                    .addBodyParameter("id",String.valueOf(id))
                    .addBodyParameter("newname", StringEscapeUtils.escapeJava(StringEscapeUtils.escapeJava(edit_name.getText().toString().trim().replace("'","&#39;"))))
                    .addBodyParameter("newdesc", StringEscapeUtils.escapeJava(StringEscapeUtils.escapeJava(edit_desc.getText().toString().trim().replace("'","&#39;"))))
                    .addBodyParameter("newvideoid", edit_videoid.getText().toString().trim())
                    .setTag("EDIT")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("[]")){
                                Toaster.ToastSuccess(getApplicationContext(), R.string.edited);
                                resp = "[]";
                                progressBar.setVisibility(View.GONE);
                                btn_edit.setText(R.string.editpost);

                                video_name.setText(edit_name.getText().toString());
                                title_desc.setText("توضیحات : "+edit_desc.getText().toString());
                                edit_videoid.setText(edit_videoid.getText().toString());

                                edit_name.setEnabled(true);
                                edit_desc.setEnabled(true);
                                edit_videoid.setEnabled(true);
                                btn_edit.setEnabled(true);
                                dialogBuilder.dismiss();

                            }else {
                                resp = "[]";
                                progressBar.setVisibility(View.GONE);
                                btn_edit.setText(R.string.editpost);
                                edit_name.setEnabled(true);
                                edit_desc.setEnabled(true);
                                edit_videoid.setEnabled(true);
                                btn_edit.setEnabled(true);
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toaster.ToastError(getApplicationContext(), R.string.servererror);;progressBar.setVisibility(View.GONE);
                            btn_edit.setText(R.string.editpost);
                            edit_name.setEnabled(true);
                            edit_desc.setEnabled(true);
                            edit_videoid.setEnabled(true);
                            btn_edit.setEnabled(true);

                        }
                    });

        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
    public void GoToUserPage(){
        gotopage.setOnClickListener(v -> {
            Intent i = new Intent(ShowResultClickPost.this, ShowResultSearch.class);
            i.putExtra("Avatar", imageinpost);
            i.putExtra("Name", username);
            i.putExtra("Fullname", fullname);
            i.putExtra("bluetick", bluetickpost);
            startActivity(i);
        });
    }
    public void SendView(){
        SharedPreferences prefUser = getSharedPreferences("prefs",MODE_PRIVATE);
        String url=Urls.host+Urls.insertview+"?token="+prefUser.getString("username","")+"&id="+getIntent().getStringExtra("id");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        GetView();
                    }
                }, error -> {
                });
        request.setRetryPolicy(new DefaultRetryPolicy(10000,5,5));
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }
    public void GetView(){
        String url=Urls.host+Urls.countview+"?idpost="+getIntent().getStringExtra("id");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        postArray = response.getJSONObject(i);
                        view_count.setText(postArray.getString("COUNT(*)"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
        });
        request.setRetryPolicy(new DefaultRetryPolicy(10000,5,5));
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }
    public void SendLike(){
        SharedPreferences prefUser = getSharedPreferences("prefs",MODE_PRIVATE);
        likepost.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                //SendAlarmLike(username,id);
                String url=Urls.host+Urls.insertlike+"?token="+prefUser.getString("username","")+"&id="+getIntent().getStringExtra("id");
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        GetCountLike();
                    }
                }, error -> {
                });
                request.setRetryPolicy(new DefaultRetryPolicy(18000,2,1));
                Volley.newRequestQueue(getApplicationContext()).add(request);
            }
            @Override
            public void unLiked(LikeButton likeButton) {
                String url=Urls.host+Urls.unlike+"?token="+prefUser.getString("username","")+"&id="+getIntent().getStringExtra("id");
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        GetCountLike();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(18000,2,1));
                Volley.newRequestQueue(getApplicationContext()).add(request);
            }
        });
    }
    public void GetStateLikes(){

        //String android_id = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);

        SharedPreferences prefUser = getSharedPreferences("prefs",MODE_PRIVATE);

        String url=Urls.host+Urls.gettoken+"?token="+prefUser.getString("username","")+"&id="+getIntent().getStringExtra("id");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        postArray = response.getJSONObject(i);
                        String status = postArray.getString("status");
                        if (status.equals("liked")){
                            likepost.setLiked(true);
                            state = true;
                            preferences_1.edit().putBoolean("liked",state).apply();
                            likepost.setLiked(preferences_1.getBoolean("liked",state));

                        }else {
                            likepost.setLiked(false);
                            state = false;
                            preferences_1.edit().putBoolean("not_liked",state).apply();

                            likepost.setLiked(preferences_1.getBoolean("not_liked",state));


                        }
                        //Log.i("onResp",postArray.getString("status"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
        });
        request.setRetryPolicy(new DefaultRetryPolicy(18000,5,1));
        Volley.newRequestQueue(getApplicationContext()).add(request);

    }
    public void GetCountLike(){
        String url=Urls.host+Urls.countlike+"?idpost="+getIntent().getStringExtra("id");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        postArray = response.getJSONObject(i);
                        like_count.setText(postArray.getString("COUNT(*)"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
        });
        request.setRetryPolicy(new DefaultRetryPolicy(10000,5,5));
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }
    public void GetCuntComment(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.countcom+"?idpost="+getIntent().getStringExtra("id"), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    List<Comment> comments = new ArrayList<>();
                    Comment comment = new Comment();
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        comment.setCuntcomment(jsonObject.getString("COUNT(*)"));
                        comments.add(comment);
                        comment_count.setText(comment.getCuntcomment());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(18000, 5, 5));
        Volley.newRequestQueue(ShowResultClickPost.this).add(request);
    }
    public void Rating() {

        AndroidNetworking.upload(Urls.host + Urls.rating)
                .addMultipartParameter("username", preferences_1.getString("username", ""))
                .addMultipartParameter("target_rate", getIntent().getStringExtra("username"))
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("ثبت شده است")){
                            Toaster.ToastErrorNormal(getApplicationContext(),"امتیاز شما قبلا ثبت شده است");
                        }else {
                            Toaster.ToastSuccessNormal(ShowResultClickPost.this, "امتیاز شما با موفقیت ثبت شد");
                            SendAlarmRating(getIntent().getStringExtra("username"));

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Rating();
                    }
                });

    }
    public void SendAlarmRating(String username){
        String body = " کاربر "+preferences_1.getString("username","")+" به شما امتیاز داد ";
        AndroidNetworking.upload(Urls.host+Urls.message_api)
                .addMultipartParameter("username",username)
                .addMultipartParameter("title", "امتیاز جدید")
                .addMultipartParameter("body", StringEscapeUtils.escapeJava(body))
                .addMultipartParameter("status", "پیام جدید")
                .addMultipartParameter("post_id", getIntent().getStringExtra("id"))
                .setTag("SEND")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                    }
                    @Override
                    public void onError(ANError anError) {
                        SendAlarmRating(getIntent().getStringExtra("username"));
                    }
                });

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        GetCuntComment();
        GetCountLike();
    }
    @Override
    protected void onStart() {
        super.onStart();
        GetCuntComment();
        GetCountLike();
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void backarrow(View view) {
        finish();
        overridePendingTransition(0,0);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}