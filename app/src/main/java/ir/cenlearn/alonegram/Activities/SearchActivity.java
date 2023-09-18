package ir.cenlearn.alonegram.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import ir.cenlearn.alonegram.APIs.ApiSearch;
import ir.cenlearn.alonegram.Adapter.UserAdapter;
import ir.cenlearn.alonegram.Model.UserSearchModel;
import ir.cenlearn.alonegram.Others.CustomTypefaceSpan;
import ir.cenlearn.alonegram.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Collections;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class SearchActivity extends AppCompatActivity {

    EditText search_user_edittext;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    public static String usernametextwatcher;
    TextView txt_nouser;
    ProgressBar progressBar;
    BottomNavigationView navigationView;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
       // getSupportActionBar().hide();

        search_user_edittext = findViewById(R.id.edit_search);
        recyclerView = findViewById(R.id.recyclerview_users);
        txt_nouser = findViewById(R.id.nouser);
        progressBar = findViewById(R.id.progressBar);
        navigationView = findViewById(R.id.navigation);

        search_user_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                progressBar.setVisibility(View.VISIBLE);
                txt_nouser.setVisibility(View.GONE);
                usernametextwatcher = search_user_edittext.getText().toString();
                search_user_edittext.setCompoundDrawablesWithIntrinsicBounds(0,0,0, 0);



                ApiSearch apiSearch=new ApiSearch(SearchActivity.this);
                apiSearch.getUsers(new ApiSearch.OnUsersReceived() {
                    @Override
                    public void onReceived(List<UserSearchModel> users) {

                        userAdapter = new UserAdapter(users, SearchActivity.this);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, GridLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(userAdapter);
                        recyclerView.setHasFixedSize(false);
                        userAdapter.notifyDataSetChanged();
                        Collections.shuffle(users);
                        progressBar.setVisibility(View.GONE);
                        search_user_edittext.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.account_search_outline_black, 0);


                        if (usernametextwatcher.isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            txt_nouser.setText(R.string.search_users_ac);
                            txt_nouser.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            search_user_edittext.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.account_search_outline_black, 0);


                        }else if (users.toString().equals("[]")){
                            txt_nouser.setText(R.string.user_not_found);
                            txt_nouser.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            search_user_edittext.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.account_search_outline_black, 0);


                        }else {
                            progressBar.setVisibility(View.GONE);
                            txt_nouser.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            search_user_edittext.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.account_search_outline_black, 0);

                        }

                    }
                });

                if (usernametextwatcher.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    txt_nouser.setText(R.string.search_users_ac);
                    txt_nouser.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    search_user_edittext.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.account_search_outline_black, 0);


                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        try {
            navigationView.setSelectedItemId(R.id.search_user_menu);
        }catch (Exception e){return;}
        Typeface myTypeFace = Typeface.createFromAsset(getApplication().getAssets(), "fonts/font.ttf");

        CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan("fonts/font.ttf", myTypeFace);
        for (int i = 0; i <navigationView.getMenu().size(); i++) {
            MenuItem menuItem = navigationView.getMenu().getItem(i);
            SpannableStringBuilder spannableTitle = new SpannableStringBuilder(menuItem.getTitle());
            spannableTitle.setSpan(typefaceSpan, 0, spannableTitle.length(), 0);
            menuItem.setTitle(spannableTitle);
        }

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.addpost_menu){
                    finish();
                    Intent intent = new Intent(SearchActivity.this, AddPostsActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0); //0 for no animation


                }else if (id == R.id.explore_menu){
                    finish();
                    Intent intent = new Intent(SearchActivity.this, ExploreActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0); //0 for no animation


                }else if (id == R.id.home_menu){
                    finish();
                    overridePendingTransition(0,0); //0 for no animation

                }else if (id == R.id.notification_menu){
                    finish();
                    Intent intent = new Intent(SearchActivity.this, NotifActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0,0); //0 for no animation
                }
                return false;

            }
        });


    }


    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void search(View view) {
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.animation);
        search_user_edittext.setVisibility(View.VISIBLE);
        search_user_edittext.startAnimation(slideUp);

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