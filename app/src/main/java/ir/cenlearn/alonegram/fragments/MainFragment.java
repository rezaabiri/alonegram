package ir.cenlearn.alonegram.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.cenlearn.alonegram.APIs.ApiServiceHome;
import ir.cenlearn.alonegram.Activities.ExploreActivity;
import ir.cenlearn.alonegram.Activities.NotifActivity;
import ir.cenlearn.alonegram.Adapter.PostsAdapter;
import ir.cenlearn.alonegram.Activities.AddPostsActivity;
import ir.cenlearn.alonegram.Model.PostModel;
import ir.cenlearn.alonegram.Others.CustomTypefaceSpan;
import ir.cenlearn.alonegram.Others.SwipeLayout;
import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.R;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.Activities.SearchActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Collections;
import java.util.List;

import ir.cenlearn.alonegram.Activities.ShowResultClickPost;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;


public class MainFragment extends Fragment {

    ImageView bluetick ;
    CircleImageView img_user;
    public static ImageView loading, new_notif;
    public static SharedPreferences preferences;
    public static TextView show_username, show_name, postCunt, rateAccount;
    public static RecyclerView recyclerView_posts;
    public static PostsAdapter postsAdapter;
    public static String bltick;
    public static LinearLayout nopostyet;
    public static Drawable new_notification;
    BottomNavigationView navigationView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView app_name = getActivity().findViewById(R.id.app_name);
        ImageView drawer = getActivity().findViewById(R.id.drawer);
        FrameLayout actionbar = getActivity().findViewById(R.id.actionbar);
        app_name.setTextColor(Color.parseColor("#ffffff"));
        drawer.setImageResource(R.drawable.ic_list_white);
        actionbar.setBackgroundColor(Color.TRANSPARENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getActivity().getWindow();
            //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //w.addFlags(WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION);
            //w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.setStatusBarColor(Color.parseColor("#2DABBC"));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AndroidNetworking.initialize(getContext());

        recyclerView_posts = getActivity().findViewById(R.id.recyclerview_posts);
        show_name = getActivity().findViewById(R.id.show_name);
        show_username = getActivity().findViewById(R.id.show_username);
        img_user = getActivity().findViewById(R.id.userimage);
        postCunt = getActivity().findViewById(R.id.postCunt);
        loading = getActivity().findViewById(R.id.loading);
        bluetick = getActivity().findViewById(R.id.bluetick);
        nopostyet = getActivity().findViewById(R.id.nopostyet);
        navigationView = getActivity().findViewById(R.id.navigation_home);
        rateAccount = getActivity().findViewById(R.id.rateAccount);
        new_notif = getActivity().findViewById(R.id.new_notif);

        preferences = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        rateAccount.setText(preferences.getString("rates",""));

        if (preferences.getString("uImage","").equals("")){
            img_user.setImageResource(R.drawable.p_profile);
        }else {
            Picasso.get().load(Urls.host+preferences.getString("uImage","")).fit().centerCrop().placeholder(R.drawable.p_profile).into(img_user);

            img_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenImageProfile();
                }
            });
        }

        show_name.setText(preferences.getString("fullname","").replace("&#39;","'"));
        show_username.setText(preferences.getString("username",""));

        postCunt.setText(preferences.getString("postcunt",""));
        if (preferences.getString("uBluetick","").equals("active")){
            bluetick.setVisibility(View.VISIBLE);
        }

        try {
            GetDetailAccount(preferences.getString("username", ""));
            GetRating(preferences.getString("username", ""));
        }catch (Exception e){e.printStackTrace();}

        Glide.with(getContext()).load(R.drawable.p_loading_post).into(loading);
        loading.setVisibility(View.VISIBLE);
        LoadPosts();
        navigationView.setSelectedItemId(R.id.home_menu);

        new_notification = VectorDrawableCompat.create(getActivity().getResources(), R.drawable.ic_new_notification, null);

        Typeface myTypeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/font.ttf");
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

                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, new SearchUserFragment()).commit();

            }else if (id == R.id.addpost_menu){
                Intent intent = new Intent(getActivity(), AddPostsActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

            }else if (id == R.id.explore_menu){
                Intent intent = new Intent(getActivity(), ExploreActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }else if (id == R.id.notification_menu){

                Intent intent = new Intent(getActivity(), NotifActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
            return false;

        });

    }
    public void LoadPosts(){
        ApiServiceHome apiServiceHome =new ApiServiceHome(getActivity());

        apiServiceHome.getPosts(new ApiServiceHome.OnPostsReceived() {
            @Override
            public void onReceived(List<PostModel> postModels) {

                RecyclerView.ItemDecoration itemDecoration;
                while (recyclerView_posts.getItemDecorationCount() > 0 &&(itemDecoration = recyclerView_posts.getItemDecorationAt(0)) != null) {
                    recyclerView_posts.removeItemDecoration(itemDecoration);
                }

                postsAdapter = new PostsAdapter(postModels, getActivity());
                RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                recyclerView_posts.setLayoutManager(mLayoutManager);
                recyclerView_posts.setHasFixedSize(false);
                Collections.reverse(postModels);
                recyclerView_posts.setAdapter(postsAdapter);
                postsAdapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);
                if (postsAdapter.getItemCount() == 0){
                    nopostyet.setVisibility(View.VISIBLE);
                }else {
                    nopostyet.setVisibility(View.GONE);
                }
                preferences.edit().putString("postcunt", String.valueOf(postsAdapter.getItemCount())).apply();
                postCunt.setText(preferences.getString("postcunt",""));

            }
        });
    }

    public void GetDetailAccount(String username){
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.showposts+"?username="+username, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);

                        try {
                            SharedPreferences preferences = getActivity().getSharedPreferences("prefs",MODE_PRIVATE);
                            preferences.edit().putString("uUsername", jsonObject.getString("username")).apply();
                            preferences.edit().putString("fullname", jsonObject.getString("fullname")).apply();
                            preferences.edit().putString("uImage", jsonObject.getString("image")).apply();
                            preferences.edit().putString("uBluetick", jsonObject.getString("bluetick")).apply();

                            show_name.setText(preferences.getString("fullname",""));

                            if (preferences.getString("uImage","").equals("")){
                                img_user.setImageResource(R.drawable.p_profile);
                            }else {
                                Picasso.get().load(Urls.host+preferences.getString("uImage","")).fit().centerCrop().error(R.drawable.image_broken_variant).into(img_user);
                            }
                            //Picasso.get().load(Urls.host+preferences.getString("uImage", "")).fit().centerCrop().into(img_user);

                            bltick = jsonObject.getString("bluetick");
                            if (bltick.equals("active")){
                                bluetick.setVisibility(View.VISIBLE);
                            }
                        }catch (NullPointerException e){e.printStackTrace();}


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
        request.setRetryPolicy(new DefaultRetryPolicy(18000,5,1));
        Volley.newRequestQueue(getActivity()).add(request);
    }

    public void GetRating(String username){
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.get_rating+"?username="+username, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);

                        preferences.edit().putString("rates",jsonObject.getString("rates")).apply();
                        rateAccount.setText(jsonObject.getString("rates"));

                    } catch (JSONException e) {
                        preferences.edit().putString("rates","0").apply();
                        rateAccount.setText("0");                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(18000,5,1));
        Volley.newRequestQueue(getActivity()).add(request);
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null){
            final String fullname = extras.getString("Fullname").replace("&#39;","'");
            show_name.setText(fullname);


        }

    }

    public void OpenImageProfile(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext(), R.style.DialogThemeProfile).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_show_profile, null);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBuilder.getWindow().getAttributes().windowAnimations = R.style.DialogThemeProfile;

        ImageView img_user = dialogView.findViewById(R.id.img_xml);
        ImageView download = dialogView.findViewById(R.id.download_image_profile);
        Picasso.get().load(Urls.host+preferences.getString("uImage","")).centerCrop().fit().into(img_user);
        download.setOnClickListener(v -> downloadImageNew("profile-from-alonegram",Urls.host+preferences.getString("uImage","")));
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void downloadImageNew(String filename, String downloadUrlOfImage){
        try{
            DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".jpg");
            dm.enqueue(request);
            Toaster.ToastDownloadNormal(getActivity(),"دانلود تصویر شروع شد");
        }catch (Exception e){
            Toaster.ToastErrorNormal(getActivity(), "دانلود با خطا مواجه شد");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (ShowResultClickPost.resp.equals("[]")){
                LoadPosts();
                ShowResultClickPost.resp = "";
            }
        }catch (Exception e){
            return;
        }
    }
}
