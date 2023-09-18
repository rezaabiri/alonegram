package ir.cenlearn.alonegram.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
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
import com.bumptech.glide.Glide;
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
import ir.cenlearn.alonegram.Model.Comment;
import ir.cenlearn.alonegram.Others.ProgressCustom;
import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;
import jp.wasabeef.picasso.transformations.BlurTransformation;

import static ir.cenlearn.alonegram.Activities.ExploreActivity.view;

public class MusicActivity extends AppCompatActivity {

    ImageView imagePlayPause, forward, previous, music_cover, comment, image_views, menu_in_post, music_replay;
    TextView textCurrentTime, textTotalDuration, music_name, artist, count_comment, like_count, view_count, datetimemusic;
    SeekBar playerSeekBar;
    MediaPlayer mediaPlayer;
    LikeButton like, save;
    Button btn_delete;
    LinearLayout video_id_music;
    SharedPreferences preferences;
    Handler handler = new Handler();

    boolean looping = true;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        imagePlayPause = findViewById(R.id.play_pause);
        forward = findViewById(R.id.forward);
        previous = findViewById(R.id.previous);
        textCurrentTime = findViewById(R.id.current_time);
        textTotalDuration = findViewById(R.id.total_time);
        playerSeekBar = findViewById(R.id.seekBar);
        music_cover = findViewById(R.id.music_cover);
        music_name = findViewById(R.id.music_name);
        artist = findViewById(R.id.artist);
        comment = findViewById(R.id.comment);
        like = findViewById(R.id.like_post_outline);
        save = findViewById(R.id.save_post);
        count_comment = findViewById(R.id.commentcunt);
        like_count = findViewById(R.id.likecunt);
        view_count = findViewById(R.id.viewpostcunt);
        image_views = findViewById(R.id.img_views);
        menu_in_post = findViewById(R.id.menu_in_post);
        music_replay = findViewById(R.id.music_replay);
        datetimemusic = findViewById(R.id.datetimemusic);

        mediaPlayer = new MediaPlayer();

        ProgressCustom progressCustom = new ProgressCustom();
        progressCustom.show(this);

        FrameLayout frameLayout = findViewById(R.id.frame_acolizer);
        AnimationDrawable animationDrawable = (AnimationDrawable) frameLayout.getBackground();

        SendLike();
        GetCountLike();
        GetStateLikes();
        GetTimeNow();

        menu_in_post.setOnClickListener(v -> {MenuActionPost();});

        preferences = getSharedPreferences("prefs",MODE_PRIVATE);
        datetimemusic.setText(preferences.getString("timenow",""));

        Handler h = new Handler();

        h.postDelayed(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {

                imagePlayPause.setOnClickListener(v -> {
                    if (mediaPlayer.isPlaying()){
                        handler.removeCallbacks(updater);
                        mediaPlayer.pause();
                        imagePlayPause.setImageResource(R.drawable.ic_play);
                        animationDrawable.stop();


                    } else {
                        mediaPlayer.start();
                        imagePlayPause.setImageResource(R.drawable.ic_pause);
                        updateSeekBar();

                        animationDrawable.setEnterFadeDuration(1000);
                        animationDrawable.setExitFadeDuration(1000);
                        animationDrawable.start();

                    }
                });
                playerSeekBar.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        SeekBar seekBar = (SeekBar) view;
                        int playPosition = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
                        mediaPlayer.seekTo(playPosition);
                        textCurrentTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));

                        return false;
                    }
                });

                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        try {
                            playerSeekBar.setSecondaryProgress(percent);
                        }catch (NullPointerException e){e.printStackTrace();}
                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.seekTo(0);
                        playerSeekBar.setProgress(0);
                        imagePlayPause.setImageResource(R.drawable.ic_play);
                        textCurrentTime.setText("00:00");
                        textTotalDuration.setText("00:00");
                        mediaPlayer.reset();
                        prepareMediaPlayer();
                    }
                });
                prepareMediaPlayer();
                progressCustom.dismiss();
            }
        },300);

        playerSeekBar.setMax(100);
        GetCuntComment();

        Picasso.get().load(Urls.host+getIntent().getStringExtra("music_cover")).into(music_cover);
        music_name.setText(StringEscapeUtils.unescapeJava(getIntent().getStringExtra("music_name")));
        artist.setText(StringEscapeUtils.unescapeJava(getIntent().getStringExtra("artist")));


        comment.setOnClickListener(v -> {
            Intent i = new Intent(MusicActivity.this, CommentActivity.class);
            i.putExtra("idpost", getIntent().getStringExtra("id"));
            i.putExtra("usernamePost", getIntent().getStringExtra("username"));
            i.putExtra("id", getIntent().getStringExtra("id"));
            startActivity(i);
        });
        save.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                AndroidNetworking.upload(Urls.host+"save_posts.php")
                        .addMultipartParameter("postid",getIntent().getStringExtra("id"))
                        .addMultipartParameter("token",preferences.getString("username",""))
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
                        .addMultipartParameter("token",preferences.getString("username",""))
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
        music_replay.setOnClickListener(v -> {

            if (looping){
                mediaPlayer.setLooping(true);
                music_replay.setImageResource(R.drawable.ic_loop_on);
                looping = false;
            }else {
                mediaPlayer.setLooping(false);
                music_replay.setImageResource(R.drawable.ic_loop_off);
                looping = true;
            }

        });



        /*
        ImageView imageViewBackground = findViewById(R.id.blurimage);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        File file = new File("reza");
        mmr.setDataSource(file.getPath());
        byte [] data = mmr.getEmbeddedPicture();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        imageViewBackground.setImageBitmap(bitmap);

         */
        //Picasso.get().load(R.drawable.p_bg).transform(new BlurTransformation(this, 70,1)).into(imageViewBackground);

    }

    public void GetTimeNow(){
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.show_post_for_comment+"?post_id="+getIntent().getStringExtra("id"), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        datetimemusic.setText(jsonObject.getString("shamsi"));
                        preferences.edit().putString("timenow",jsonObject.getString("shamsi")).apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, error -> {});
        request.setRetryPolicy(new DefaultRetryPolicy(20000,1,1));
        Volley.newRequestQueue(MusicActivity.this).add(request);
    }
    private void prepareMediaPlayer(){
        try {
            mediaPlayer.setDataSource(Urls.host+getIntent().getStringExtra("music_url"));
            mediaPlayer.prepare();
            textTotalDuration.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private Runnable updater = () -> {
        updateSeekBar();
        long currentDuration = mediaPlayer.getCurrentPosition();
        textCurrentTime.setText(milliSecondsToTimer(currentDuration));
    };

    private void updateSeekBar(){
        if (mediaPlayer.isPlaying()){
            playerSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration() * 100)));
            handler.postDelayed(updater, 1000);
        }
    }

    private String milliSecondsToTimer(long milliSecond){
        String timerString = "";
        String secondString;

        int hours = (int) (milliSecond / (1000 * 60 * 60));
        int minute = (int) (milliSecond % (1000 * 60 * 60) / (1000 * 60));
        int second = (int) (milliSecond % (1000 * 60 * 60) % (1000 * 60) / 1000);

        if (hours > 0){
            timerString = hours + ":";
        }
        if (second < 10){
            secondString =  "0" + second;
        }else {
            secondString = "" + second;

        }

        timerString = timerString + minute + ":" + secondString;
        return timerString;

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
                        count_comment.setText(comment.getCuntcomment());

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
        Volley.newRequestQueue(MusicActivity.this).add(request);
    }

    public void SendLike(){
        SharedPreferences prefUser = getSharedPreferences("prefs",MODE_PRIVATE);
        like.setOnLikeListener(new OnLikeListener() {
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
                        JSONObject postArray = response.getJSONObject(i);
                        String status = postArray.getString("status");
                        if (status.equals("liked")){
                            like.setLiked(true);
                        }else {
                            like.setLiked(false);
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
                        JSONObject postArray = response.getJSONObject(i);
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
                        JSONObject postArray = response.getJSONObject(i);
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

    public void GetStateSave(){

        SharedPreferences preferences = getSharedPreferences("prefs",MODE_PRIVATE);
        String url=Urls.host+Urls.get_state_save_posts+"?postid="+getIntent().getStringExtra("id")+"&token="+preferences.getString("username","");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject postArray = response.getJSONObject(i);
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

    public void GetViewsAndShow(){
        image_views.setOnClickListener(v -> {
            Intent intent = new Intent(MusicActivity.this, ShowViewsActivity.class);
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
        btn_download_image.setText("دانلود آهنگ");
        btn_rate_to_account = dialogView.findViewById(R.id.rate_to_account);

        try {
            if (preferences.getString("username", "").equals(getIntent().getStringExtra("username"))){
                btn_delete_post.setVisibility(View.VISIBLE);
                btn_edit_post.setVisibility(View.VISIBLE);

            }else if (preferences.getString("username","").equals("alonegram")){
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
            downloadImageNew("alonegram-music",Urls.host+getIntent().getStringExtra("music_url"));
            dialogBuilder.dismiss();
        });
        btn_rate_to_account.setOnClickListener(v -> {
            Rating();
            dialogBuilder.dismiss();
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

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
            String usernames = preferences.getString("username","")+" -> "+ getIntent().getStringExtra("username");
            progressBar.setVisibility(View.VISIBLE);
            btn_send_report.setText("");
            if (!edit_subject.getText().toString().isEmpty() && !edit_body.getText().toString().isEmpty()){
                AndroidNetworking.post(Urls.host+Urls.report_api)
                        .addBodyParameter("id",String.valueOf(getIntent().getStringExtra("id")))
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
    public void DeletePost(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_delete_post, null);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBuilder.getWindow().getAttributes().windowAnimations = R.style.DialogThemeMenu;

        btn_delete = dialogView.findViewById(R.id.btn_delete);

        btn_delete.setOnClickListener(v -> AndroidNetworking.post(Urls.host+Urls.delete_post_api)
                .addBodyParameter("id",String.valueOf(getIntent().getStringExtra("id")))
                .setTag("DELETE")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("[]")){
                            ShowResultClickPost.resp = "[]";
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
        video_id_music = dialogView.findViewById(R.id.video_id_music);


        video_id_music.setVisibility(View.GONE);

        TextView title_post, artist_post;

        title_post = dialogView.findViewById(R.id.title_post);
        artist_post = dialogView.findViewById(R.id.artist_post);

        title_post.setText("نام آهنگ :");
        artist_post.setText("نام خواننده :");

        final ProgressBar progressBar = dialogView.findViewById(R.id.edit_progressBar);
        final EditText edit_name = dialogView.findViewById(R.id.edit_namevideo);
        final EditText edit_desc = dialogView.findViewById(R.id.edit_descvideo);
        final EditText edit_videoid = dialogView.findViewById(R.id.edit_videoid);
        ImageView edit_imagevideo = dialogView.findViewById(R.id.edit_imagevideo);

        edit_name.setText(getIntent().getStringExtra("music_name"));
        edit_desc.setText(getIntent().getStringExtra("artist"));

        Picasso.get().load(Urls.host+getIntent().getStringExtra("music_cover")).error(R.drawable.image_broken_variant).fit().centerCrop().into(edit_imagevideo);

        final Button btn_edit = dialogView.findViewById(R.id.btn_edit);

        btn_edit.setOnClickListener(v -> {

            progressBar.setVisibility(View.VISIBLE);
            btn_edit.setText("");
            edit_name.setEnabled(false);
            edit_desc.setEnabled(false);
            edit_videoid.setVisibility(View.GONE);
            btn_edit.setEnabled(false);

            AndroidNetworking.post(Urls.host+Urls.edit_post_api)
                    .addBodyParameter("id",String.valueOf(getIntent().getStringExtra("id")))
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
                                ShowResultClickPost.resp = "[]";
                                progressBar.setVisibility(View.GONE);
                                btn_edit.setText(R.string.editpost);

                                music_name.setText(edit_name.getText().toString());
                                artist.setText(edit_desc.getText().toString());

                                edit_name.setEnabled(true);
                                edit_desc.setEnabled(true);
                                edit_videoid.setVisibility(View.GONE);
                                btn_edit.setEnabled(true);
                                dialogBuilder.dismiss();

                            }else {
                                ShowResultClickPost.resp = "[]";
                                progressBar.setVisibility(View.GONE);
                                btn_edit.setText(R.string.editpost);
                                edit_name.setEnabled(true);
                                edit_desc.setEnabled(true);
                                edit_videoid.setVisibility(View.GONE);
                                btn_edit.setEnabled(true);
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toaster.ToastError(getApplicationContext(), R.string.servererror);;progressBar.setVisibility(View.GONE);
                            btn_edit.setText(R.string.editpost);
                            edit_name.setEnabled(true);
                            edit_desc.setEnabled(true);
                            edit_videoid.setVisibility(View.GONE);
                            btn_edit.setEnabled(true);

                        }
                    });

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
                    .setMimeType("*/*") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".mp3");
            dm.enqueue(request);
            Toaster.ToastDownloadNormal(MusicActivity.this,"دانلود آهنگ شروع شد");
        }catch (Exception e){
            Toaster.ToastErrorNormal(MusicActivity.this, "دانلود با خطا مواجه شد");
        }
    }
    public void Rating() {

        AndroidNetworking.upload(Urls.host + Urls.rating)
                .addMultipartParameter("username", preferences.getString("username", ""))
                .addMultipartParameter("target_rate", getIntent().getStringExtra("username"))
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("ثبت شده است")){
                            Toaster.ToastErrorNormal(getApplicationContext(),"امتیاز شما قبلا ثبت شده است");
                        }else {
                            Toaster.ToastSuccessNormal(MusicActivity.this, "امتیاز شما با موفقیت ثبت شد");
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
        String body = " کاربر "+preferences.getString("username","")+" به شما امتیاز داد ";
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
    protected void onStart() {
        super.onStart();
        SendLike();
        GetCountLike();
        GetStateLikes();
        GetStateSave();
        SendView();
        GetViewsAndShow();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        GetCuntComment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.pause();
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void backarrow(View view) {
        finish();
        overridePendingTransition(0,0);
    }
}