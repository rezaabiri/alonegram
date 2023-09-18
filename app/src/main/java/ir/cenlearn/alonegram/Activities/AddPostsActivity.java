package ir.cenlearn.alonegram.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import ir.cenlearn.alonegram.GetTime.Utilities;
import ir.cenlearn.alonegram.ImageCompressor.Compressor;
import ir.cenlearn.alonegram.ImageCompressor.FileUtil;
import ir.cenlearn.alonegram.Others.CustomTypefaceSpan;
import ir.cenlearn.alonegram.Others.ProgressCustom;
import ir.cenlearn.alonegram.Others.RealPath;
import ir.cenlearn.alonegram.Others.SwipeLayout;
import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.util.FileUtils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static android.content.Intent.ACTION_PICK;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import static android.content.Intent.parseUri;

public class AddPostsActivity extends AppCompatActivity {

    ImageView videoImage;
    FrameLayout show_prev_image;
    EditText editname, editdesc, editvideoid;
    Button btn_submit;
    String path;
    static SharedPreferences preferences;
    File compressfile;
    TextView guideidfa, video_desc;
    String android_id, datetime;
    BottomNavigationView navigationView;
    ProgressCustom progressbar;
    Bitmap bitmap;


    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_posts);

        videoImage = findViewById(R.id.video_image);
        editname = findViewById(R.id.editnamevideo);
        btn_submit = findViewById(R.id.btn_submit);
        show_prev_image = findViewById(R.id.show_edit_img);
        editdesc = findViewById(R.id.editdescvideo);
        editvideoid = findViewById(R.id.videoid);
        guideidfa = findViewById(R.id.guideid);
        video_desc = findViewById(R.id.video_desc);
        navigationView = findViewById(R.id.navigation);
        AndroidNetworking.initialize(this);

        progressbar = new ProgressCustom();

        videoImage.setOnClickListener(v -> askReadPermission());
        guideidfa.setOnClickListener(v -> Guide());

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        navigationView.setSelectedItemId(R.id.addpost_menu);
        Typeface myTypeFace = Typeface.createFromAsset(getApplication().getAssets(), "fonts/font.ttf");

        CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan("fonts/font.ttf", myTypeFace);
        for (int i = 0; i <navigationView.getMenu().size(); i++) {
            MenuItem menuItem = navigationView.getMenu().getItem(i);
            SpannableStringBuilder spannableTitle = new SpannableStringBuilder(menuItem.getTitle());
            spannableTitle.setSpan(typefaceSpan, 0, spannableTitle.length(), 0);
            menuItem.setTitle(spannableTitle);
        }

        navigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.search_user_menu){
                finish();
                Intent intent = new Intent(AddPostsActivity.this, SearchActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0,0); //0 for no animation


            }else if (id == R.id.explore_menu){
                finish();
                Intent intent = new Intent(AddPostsActivity.this, ExploreActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0,0); //0 for no animation

            }else if (id == R.id.home_menu){
                finish();
                overridePendingTransition(0,0); //0 for no animation

            }else if (id == R.id.notification_menu){
                finish();
                Intent intent = new Intent(AddPostsActivity.this, NotifActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0,0); //0 for no animation
            }
            return false;

        });



        btn_submit.setOnClickListener(v -> {

            if (editname.getText().toString().isEmpty() || editdesc.getText().toString().isEmpty() || path == null){
                Toaster.ToastError(getApplicationContext(), R.string.correctinfo);
            }else if (!editvideoid.getText().toString().isEmpty() && !(editvideoid.getText().toString().length() == 5)) {
                Toaster.ToastError(getApplicationContext(), R.string.wrongvideoid);
            }else {
                progressbar.show(this);

                Date date = Calendar.getInstance().getTime();
                datetime = Utilities.getCurrentShamsidate() +" || "+date.getHours() +":"+date.getMinutes();
                //upload();
                UploadVolley();

            }

        });

    }

    private void UploadVolley(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.host + Urls.add_post_api, response -> {
            if (response.equals("error")){
                Toaster.ToastError(this, R.string.servererror);
                progressbar.dismiss();
                finish();
            }else {
                finish();
                ShowResultClickPost.resp = "[]";
                overridePendingTransition(0,0);
                progressbar.dismiss();
            }
        }, error -> {
            Toaster.ToastError(this, R.string.servererror);
            progressbar.dismiss();

        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("videoimage", ImageToString(bitmap));
                params.put("videoname",StringEscapeUtils.escapeJava(editname.getText().toString().replace("'","&#39;")));
                params.put("videodesc",StringEscapeUtils.escapeJava(editdesc.getText().toString().replace("'","&#39;")));
                params.put("videoid",editvideoid.getText().toString());
                params.put("token",android_id);
                params.put("datetime",datetime);
                params.put("username",preferences.getString("username",""));

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(8000,1,1));
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private String ImageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageByte,Base64.DEFAULT);
    }

    private void askReadPermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        pickImage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response != null && response.isPermanentlyDenied()){
                            Snackbar.make(btn_submit,R.string.permission, Snackbar.LENGTH_LONG).setAction(R.string.allow, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    i.setData(Uri.fromParts("package",AddPostsActivity.this.getPackageName(),null));
                                    startActivity(i);
                                }
                            }).show();
                        }else {
                            Snackbar.make(btn_submit,R.string.permission,Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, final PermissionToken token) {
                        Snackbar.make(btn_submit,R.string.permission,Snackbar.LENGTH_LONG).setAction(R.string.allow, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                token.continuePermissionRequest();
                            }
                        }).show();
                    }
                }).check();
    }

    private void pickImage(){

        //Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(takePicture, 0);//zero can be replaced with any action code (called requestCode)
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);//one can be replaced with any action code

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data.getData() != null) {
            try {
                preferences = this.getSharedPreferences("prefs", MODE_PRIVATE);
                path = RealPath.getPathFromUri(this, data.getData());
                File imgFile = new File(path);
                bitmap = Compressor.getDefault(this).compressToBitmap(imgFile);
                compressfile = Compressor.getDefault(this).compressToFile(imgFile);

                btn_submit.setEnabled(true);
                Picasso.get().load(compressfile).fit().into(videoImage);
                show_prev_image.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void Guide(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_guide, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
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
        overridePendingTransition(
                0,
                R.anim.close_without_animation
        );
    }

}