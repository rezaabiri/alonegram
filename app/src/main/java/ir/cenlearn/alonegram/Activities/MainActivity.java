package ir.cenlearn.alonegram.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import de.hdodenhof.circleimageview.CircleImageView;
import ir.cenlearn.alonegram.BroadCasts.BroadCastInternt;
import ir.cenlearn.alonegram.BuildConfig;
import ir.cenlearn.alonegram.Model.PostModel;
import ir.cenlearn.alonegram.Others.ProgressCustom;
import ir.cenlearn.alonegram.Others.SessionManager;
import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;
import ir.cenlearn.alonegram.fragments.EditProfileFragment;
import ir.cenlearn.alonegram.fragments.ContactUsFragment;
import ir.cenlearn.alonegram.fragments.MainFragment;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.pushpole.sdk.PushPole;
import com.squareup.picasso.Picasso;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.cenlearn.alonegram.fragments.SavePostsFragment;
import static ir.cenlearn.alonegram.fragments.MainFragment.new_notif;

public class MainActivity extends AppCompatActivity {


    ImageView drawer, image_drawer;
    CircleImageView pro_image;
    TextView pro_user, pro_mail;

    Button btn_delete, btn_logout;

    public static NotificationManager notifManager;
    String offerChannelId = "Offers";
    SharedPreferences preferences;

    View dialogView;
    AlertDialog dialogBuilder;
    ConstraintLayout back_drawer;

    NotificationCompat.Builder sNotifBuilder;

    int versionCode;

    public static JSONArray response_editprofile;
    public static JSONArray response_myaccount;
    public BroadcastReceiver broadcastReceiverNet;
    private final BroadcastReceiver MyReceiver = null;
    public static String username_notification;
    public static FrameLayout frameLayout_status;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity_main_app);
        PushPole.initialize(this,true);

        versionCode = BuildConfig.VERSION_CODE;
        CheckForUpdate();

        drawer = findViewById(R.id.drawer);

        broadcastReceiverNet = new BroadCastInternt();
        Intent intent1 = new Intent();
        intent1.addCategory(Intent.CATEGORY_DEFAULT);
        intent1.setAction("com.example.serverconnect2.BroadCasts.BroadCastInternt");
        sendBroadcast(intent1);
        broadcastIntent();

        drawer.setOnClickListener(v -> {

            dialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this, R.style.DialogTheme).create();
            LayoutInflater inflater = getLayoutInflater();
            dialogView = inflater.inflate(R.layout.drawer_custom, null);
            dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogBuilder.setCanceledOnTouchOutside(false);
            LinearLayout home_drawer, edit_drawer, save_posts_drawer, contact_drawer, rate_drawer, share_drawer, logout_drawer, delete_drawer;

            pro_image = dialogView.findViewById(R.id.pro_image);
            pro_mail = dialogView.findViewById(R.id.pro_email);
            pro_user = dialogView.findViewById(R.id.pro_username);

            home_drawer = dialogView.findViewById(R.id.drawer_home);
            edit_drawer = dialogView.findViewById(R.id.drawer_edit);
            contact_drawer = dialogView.findViewById(R.id.drawer_contact);
            rate_drawer = dialogView.findViewById(R.id.drawer_rate);
            share_drawer = dialogView.findViewById(R.id.drawer_share);
            logout_drawer = dialogView.findViewById(R.id.drawer_logout);
            delete_drawer = dialogView.findViewById(R.id.drawer_deleteacc);
            save_posts_drawer = dialogView.findViewById(R.id.drawer_save_posts);

            back_drawer = dialogView.findViewById(R.id.back_drawer);
            image_drawer = dialogView.findViewById(R.id.image_drawer);

            SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
            pro_user.setText(sharedPreferences.getString("username", ""));
            pro_mail.setText(sharedPreferences.getString("email", ""));

            try {
                image_drawer.setVisibility(View.VISIBLE);
                Picasso.get().load(Urls.host+preferences.getString("uImage","")).fit().centerCrop().into(image_drawer);
                Picasso.get().load(Urls.host+preferences.getString("uImage","")).fit().centerCrop().placeholder(R.drawable.p_profile).into(pro_image);

            }catch (Exception e){e.printStackTrace();}

            home_drawer.setOnClickListener(v1 -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, new MainFragment()).commit();
                dialogBuilder.dismiss();
            });
            edit_drawer.setOnClickListener(v1 -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, new EditProfileFragment()).commit();
                dialogBuilder.dismiss();
            });
            save_posts_drawer.setOnClickListener(v1 -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, new SavePostsFragment()).commit();
                dialogBuilder.dismiss();
            });
            contact_drawer.setOnClickListener(v1 -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, new ContactUsFragment()).commit();
                dialogBuilder.dismiss();
            });
            rate_drawer.setOnClickListener(v1 -> {
                try {



                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setData(Uri.parse("bazaar://details?id=" + "ir.cenlearn.alonegram"));
                    intent.setPackage("com.farsitel.bazaar");
                    startActivity(intent);

                    /*
                    String url= "myket://comment?id=ir.cenlearn.alonegram";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                     */
                    dialogBuilder.dismiss();

                }catch (Exception e){
                    Toaster.ToastErrorNormal(this,"کافه بازار نصب نیست");
                }
            });
            share_drawer.setOnClickListener(v1 -> {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    //String shareMessage = "https://myket.ir/app/ir.cenlearn.alonegram";
                    String shareMessage = "http://cafebazaar.ir/app/?id=ir.cenlearn.alonegram&ref=share";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(shareIntent);
                    dialogBuilder.dismiss();
                } catch(Exception e) {
                }

            });
            logout_drawer.setOnClickListener(v1 -> {
                LogOut();
                dialogBuilder.dismiss();

            });
            delete_drawer.setOnClickListener(v1 -> {
                DeleteAccount();
                dialogBuilder.dismiss();

            });

            dialogBuilder.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            dialogBuilder.setView(dialogView);
            dialogBuilder.show();

        });

        preferences = getSharedPreferences("prefs",MODE_PRIVATE);
        getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, new MainFragment()).commit();
        AndroidNetworking.initialize(this);

        EditProfile();
        MyAccount();
        GetView();
        GetNotification();

        notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }


    public void EditProfile(){

        SharedPreferences preferences = getSharedPreferences("prefs",MODE_PRIVATE);
        AndroidNetworking.upload(Urls.host+Urls.information_edit_api)
                .addMultipartParameter("username", preferences.getString("username", ""))
                .setTag("POST")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.getString(0).equals("error")){
                            }else {
                                response_editprofile = response;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    public void MyAccount(){
        SharedPreferences preferences = getSharedPreferences("prefs",MODE_PRIVATE);
        AndroidNetworking.upload(Urls.host+Urls.information_api)
                .addMultipartParameter("username", preferences.getString("username", ""))
                .setTag("POST")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.getString(0).equals("error")){
                            }else {
                                response_myaccount = response;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });

    }

    public void DeleteAccount(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_delete_account, null);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        btn_delete = dialogView.findViewById(R.id.btn_delete);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressCustom progressCustom = new ProgressCustom();
                progressCustom.show(MainActivity.this);
                dialogBuilder.dismiss();
                SharedPreferences preferences = getSharedPreferences("prefs",MODE_PRIVATE);

                AndroidNetworking.upload(Urls.host+Urls.delete_account_api)
                        .addMultipartParameter("username", preferences.getString("username", ""))
                        .setTag("UPLOAD")
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    if (response.getString(0).equals("success")){

                                        Toaster.ToastSuccess(getApplicationContext(),R.string.sucdelacc);
                                        progressCustom.dismiss();
                                        SessionManager sessionManager = new SessionManager(getApplicationContext());
                                        sessionManager.setLoggedIn(false);
                                        startActivity(new Intent(MainActivity.this, CreateAccount.class));
                                        finish();
                                    }else {
                                        progressCustom.dismiss();
                                        Toaster.ToastError(getApplicationContext(),R.string.wrondelacc);
                                    }
                                } catch (JSONException e) {
                                    progressCustom.dismiss();
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                progressCustom.dismiss();
                            }
                        });
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    public void LogOut(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_logout, null);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        
        btn_logout = dialogView.findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                sessionManager.setLoggedIn(false);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    public void GetView(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.selectdata, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    List<PostModel> postModels = new ArrayList<>();
                    PostModel postModel = new PostModel();
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        postModel.setView(jsonObject.getString("view"));
                        postModels.add(postModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toaster.ToastError(getApplicationContext(),R.string.servererror);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(18000, 5, 5));
        Volley.newRequestQueue(MainActivity.this).add(request);
    }

    public void GetNotification(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.sendnotif+"?username="+preferences.getString("username","").toLowerCase(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        username_notification = jsonObject.getString("username");
                        String status = jsonObject.getString("status");
                        String title = jsonObject.getString("title").replace("&#39;", "'");
                        String body = jsonObject.getString("body").replace("&#39;", "'");
                        if (status.equals("پیام جدید")){
                            createNotifChannel();
                            simpleNotification(StringEscapeUtils.unescapeJava(title),StringEscapeUtils.unescapeJava(body));
                            new_notif.setVisibility(View.VISIBLE);
                            //new_notification.setVisibility(View.VISIBLE);
                        }else {
                            //new_notification.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, error -> {
        });

        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(MainActivity.this).add(request);
    }

    private void createNotifChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String offerChannelName = "Shop offers";
            String offerChannelDescription= "Best offers for customers";
            int offerChannelImportance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notifChannel = new NotificationChannel(offerChannelId, offerChannelName, offerChannelImportance);
            notifChannel.setDescription(offerChannelDescription);
            notifChannel.enableLights(true);
            notifChannel.setLightColor(Color.GREEN);

            notifManager.createNotificationChannel(notifChannel);

        }

    }

    public void simpleNotification(String titlemsg, String body) {

        NotificationCompat.BigTextStyle bStyle = new NotificationCompat.BigTextStyle()
                .bigText(body)
                .setBigContentTitle(titlemsg)
                .setSummaryText("اعلان جدید");

        sNotifBuilder = new NotificationCompat.Builder(this, offerChannelId)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_logo_foreground))
                .setContentTitle(titlemsg)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_logo)
                .setVibrate(new long[]{100,500,500,500,500,500})
                .setStyle(bStyle)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        notifManager.notify(0, sNotifBuilder.build());

    }

    public void CheckForUpdate(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_update, null);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        JsonArrayRequest request_update = new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.checkUpdate, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    String NewVersionCode = jsonObject.getString("versionCode");
                    if (Integer.parseInt(NewVersionCode) > versionCode){
                        dialogBuilder.setView(dialogView);
                        dialogBuilder.show();
                    }
                } catch (JSONException e) {e.printStackTrace();}
            }
        }, error -> {
        });

        request_update.setRetryPolicy(new DefaultRetryPolicy(18000, 5, 5));
        Volley.newRequestQueue(getApplicationContext()).add(request_update);


        Button btn_update = dialogView.findViewById(R.id.btn_update);

        btn_update.setOnClickListener(v -> {

            try {
                /*
                String url= "myket://download/ir.cenlearn.alonegram";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                */

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("bazaar://details?id=" + "ir.cenlearn.alonegram"));
                intent.setPackage("com.farsitel.bazaar");
                startActivity(intent);

                dialogBuilder.dismiss();

            }catch (Exception e){
                Toaster.ToastErrorNormal(this,"کافه بازار نصب نیست");
            }

            dialogBuilder.dismiss();

        });
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiverNet, intentFilter);

        Intent startServiceIntent = new Intent(this, MainActivity.class);
        startService(startServiceIntent);

    }
    @Override
    public void onBackPressed () {
        super.onBackPressed() ;
    }
    public void broadcastIntent() {
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //new_notification.setVisibility(View.GONE);
    }
}
