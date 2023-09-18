package ir.cenlearn.alonegram.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import ir.cenlearn.alonegram.APIs.ApiServiceExplore;
import ir.cenlearn.alonegram.Adapter.PostsAdapter;
import ir.cenlearn.alonegram.Model.PostModel;
import ir.cenlearn.alonegram.Others.CustomTypefaceSpan;
import ir.cenlearn.alonegram.Others.SwipeLayout;
import ir.cenlearn.alonegram.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Collections;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class ExploreActivity extends AppCompatActivity {

    public static RecyclerView recyclerView_explore;
    public static PostsAdapter postsAdapter;
    public static ImageView loading_explore;

    public static View view, view2;
    BottomNavigationView navigationView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        recyclerView_explore = findViewById(R.id.recyclerview_explore);
        loading_explore = findViewById(R.id.loading_explore);
        view = findViewById(R.id.viewvertical);
        view2 = findViewById(R.id.viewvertical_right);
        navigationView = findViewById(R.id.navigation);

        Glide.with(this).load(R.drawable.p_loading_post).into(loading_explore);
        loading_explore.setVisibility(View.VISIBLE);

        LoadPosts();

        navigationView.setSelectedItemId(R.id.explore_menu);
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

            if (id == R.id.addpost_menu){
                Intent intent = new Intent(ExploreActivity.this, AddPostsActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0); //0 for no animation

            }else if (id == R.id.home_menu){
                finish();
                overridePendingTransition(0,0); //0 for no animation

            }else if (id == R.id.search_user_menu){
                Intent intent = new Intent(ExploreActivity.this, SearchActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0); //0 for no animation

            }else if (id == R.id.notification_menu){
                Intent intent = new Intent(ExploreActivity.this, NotifActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
            return false;

        });
    }

    public void LoadPosts(){
        ApiServiceExplore apiServiceExplore=new ApiServiceExplore(this);
        apiServiceExplore.getPosts(new ApiServiceExplore.OnPostsReceived() {
            @Override
            public void onReceived(List<PostModel> postModels) {

                postsAdapter = new PostsAdapter(postModels, ExploreActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2,1);
                recyclerView_explore.setLayoutManager(mLayoutManager);
                recyclerView_explore.addItemDecoration(new DividerItemDecoration(ExploreActivity.this, DividerItemDecoration.VERTICAL));
                recyclerView_explore.addItemDecoration(new DividerItemDecoration(ExploreActivity.this, DividerItemDecoration.HORIZONTAL));
                recyclerView_explore.setHasFixedSize(false);
                Collections.shuffle(postModels);
                recyclerView_explore.setAdapter(postsAdapter);
                postsAdapter.notifyDataSetChanged();
                loading_explore.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);

            }
        });
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