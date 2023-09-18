package ir.cenlearn.alonegram.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.cenlearn.alonegram.Adapter.PostsAdapter;
import ir.cenlearn.alonegram.Model.PostModel;
import ir.cenlearn.alonegram.Others.Encoder;
import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ShowResultSearch extends AppCompatActivity {

    TextView uUsername, uFullname, postCunt, rate_account_detail;
    String avatar, username, fullname, bluetick;
    ImageView loading, bluetickimg, send_report_account;
    CircleImageView imageView;

    RecyclerView recyclerView_posts;
    PostsAdapter postsAdapter;

    LinearLayout nopostyet;

    SharedPreferences preferences;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        imageView = findViewById(R.id.show_image_user);
        uUsername = findViewById(R.id.name);
        uFullname = findViewById(R.id.fullname);
        postCunt = findViewById(R.id.postCunt);
        loading =  findViewById(R.id.loading_user_detail);
        bluetickimg = findViewById(R.id.bluetickresultsearch);
        recyclerView_posts = findViewById(R.id.recyclerview_posts);
        nopostyet = findViewById(R.id.nopostyet_user_detail);
        rate_account_detail = findViewById(R.id.rate_Account_detail);
        send_report_account = findViewById(R.id.send_report_account);

        preferences = getSharedPreferences("prefs",MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //w.addFlags(WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION);
            //w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.setStatusBarColor(Color.parseColor("#2DABBC"));
        }

        getPosts();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        Glide.with(this).load(R.drawable.p_loading_post).into(loading);
        loading.setVisibility(View.VISIBLE);


        try {
            if (extras != null){
                avatar = extras.getString("Avatar");
                username = extras.getString("Name");
                fullname = extras.getString("Fullname");
                bluetick = extras.getString("bluetick");


                if (avatar.equals("")){
                    imageView.setBackgroundResource(R.drawable.p_profile);
                }else {
                    Picasso.get().load(Urls.host+avatar).error(R.drawable.image_broken_variant).fit().centerCrop().into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OpenImageProfile();
                        }
                    });
                }
                uUsername.setText(username);
                uFullname.setText(fullname);

            }

        }catch (Exception e){return;}

        try {
            if (bluetick.equals("active")){
                bluetickimg.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){e.printStackTrace();}

        send_report_account.setOnClickListener(v -> {SendReportAccount();});

        GetRating(username);
    }

    public void GetRating(String username){
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.get_rating+"?username="+username, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        rate_account_detail.setText(jsonObject.getString("rates"));
                    } catch (JSONException e) {
                        rate_account_detail.setText("0");                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(18000,5,1));
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    public void getPosts(){
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.selectdata+"?username="+getIntent().getStringExtra("Name"), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<PostModel> postModels =new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            PostModel postModel =new PostModel();
                            try {
                                JSONObject jsonObject=response.getJSONObject(i);

                                postModel.setVideoName(StringEscapeUtils.unescapeJava(jsonObject.getString("videoname").replace("&#39;","'")));
                                postModel.setId(jsonObject.getString("idpost"));
                                postModel.setCategory(StringEscapeUtils.unescapeJava(jsonObject.getString("videocategory").replace("&#39;","'")));
                                postModel.setVideoImage(jsonObject.getString("videoimage"));
                                postModel.setUsername(jsonObject.getString("username"));
                                postModel.setFullname(jsonObject.getString("fullname").replace("&#39;","'"));
                                postModel.setImage(jsonObject.getString("image"));
                                postModel.setVideodescription(StringEscapeUtils.unescapeJava(jsonObject.getString("videodescription").replace("&#39;","'")));
                                postModel.setVideoid(jsonObject.getString("videoid"));
                                postModel.setView(jsonObject.getString("view"));
                                postModel.setLikes(jsonObject.getString("likes"));
                                postModel.setBluetick(jsonObject.getString("bluetick"));
                                postModel.setDatetime(jsonObject.getString("shamsi"));
                                postModel.setMusic(jsonObject.getString("music"));
                                postModels.add(postModel);

                                RecyclerView.ItemDecoration itemDecoration;
                                while (recyclerView_posts.getItemDecorationCount() > 0 &&(itemDecoration = recyclerView_posts.getItemDecorationAt(0)) != null) {
                                    recyclerView_posts.removeItemDecoration(itemDecoration);
                                }

                                postsAdapter = new PostsAdapter(postModels, ShowResultSearch.this);
                                RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
                                recyclerView_posts.setLayoutManager(mLayoutManager);
                                recyclerView_posts.setHasFixedSize(false);
                                Collections.reverse(postModels);
                                recyclerView_posts.setAdapter(postsAdapter);
                                postsAdapter.notifyDataSetChanged();
                                postCunt.setText(String.valueOf(postsAdapter.getItemCount()));
                                loading.setVisibility(View.GONE);

                            } catch (JSONException e) {
                                nopostyet.setVisibility(View.VISIBLE);
                                loading.setVisibility(View.GONE);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(18000,5,5));
        Volley.newRequestQueue(this).add(request);
    }

    public void SendReportAccount(){
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
            String usernames = preferences.getString("username","")+" -> "+ username;
            progressBar.setVisibility(View.VISIBLE);
            btn_send_report.setText("");
            if (!edit_subject.getText().toString().isEmpty() && !edit_body.getText().toString().isEmpty()){
                AndroidNetworking.post(Urls.host+Urls.report_api)
                        .addBodyParameter("account", username)
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

    public void OpenImageProfile(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(ShowResultSearch.this, R.style.DialogThemeProfile).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_show_profile, null);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView img_user = dialogView.findViewById(R.id.img_xml);
        ImageView download = dialogView.findViewById(R.id.download_image_profile);
        Picasso.get().load(Urls.host+avatar).centerCrop().fit().into(img_user);
        download.setOnClickListener(v -> downloadImageNew("profile-from-alonegram",Urls.host+avatar));

        Animation slideUp = AnimationUtils.loadAnimation(ShowResultSearch.this, R.anim.fade_in);
        dialogView.setAnimation(slideUp);
        dialogView.animate();
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
            Toaster.ToastDownloadNormal(getApplicationContext(),"دانلود تصویر شروع شد");
        }catch (Exception e){
            Toaster.ToastErrorNormal(getApplicationContext(), "دانلود با خطا مواجه شد");
        }
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void backarrow(View view) {
        finish();
        overridePendingTransition(0,0);
    }
}