package ir.cenlearn.alonegram.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import ir.cenlearn.alonegram.APIs.ApiSavePosts;
import ir.cenlearn.alonegram.Adapter.SavePostsAdapter;
import ir.cenlearn.alonegram.Model.PostModel;
import ir.cenlearn.alonegram.R;


public class SavePostsFragment extends Fragment {

    RecyclerView recyclerView_save_posts;
    SavePostsAdapter savePostsAdapter;
    public static LinearLayout nopostyet;
    public static ImageView loading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, new MainFragment()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        TextView app_name = getActivity().findViewById(R.id.app_name);
        ImageView drawer = getActivity().findViewById(R.id.drawer);
        FrameLayout actionbar = getActivity().findViewById(R.id.actionbar);
        app_name.setTextColor(Color.parseColor("#000000"));
        drawer.setImageResource(R.drawable.ic_list);
        actionbar.setElevation(5);
        actionbar.setBackgroundColor(Color.parseColor("#ffffff"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getActivity().getWindow();
            //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //w.addFlags(WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION);
            //w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.setStatusBarColor(Color.parseColor("#B8B8B8"));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_save_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView_save_posts = getActivity().findViewById(R.id.recyclerview_save_posts);
        nopostyet = getActivity().findViewById(R.id.nopostyet_save);
        loading = getActivity().findViewById(R.id.loading_save);

        Glide.with(getContext()).load(R.drawable.p_loading_post).into(loading);
        loading.setVisibility(View.VISIBLE);

        SavePostsReceived();
    }

    public void SavePostsReceived(){

        ApiSavePosts apiSavePosts =new ApiSavePosts(getActivity());

        apiSavePosts.getSavePosts(new ApiSavePosts.OnSavePostsReceived() {
            @Override
            public void onReceived(List<PostModel> postModels) {
                try {
                    RecyclerView.ItemDecoration itemDecoration;
                    while (recyclerView_save_posts.getItemDecorationCount() > 0 &&(itemDecoration = recyclerView_save_posts.getItemDecorationAt(0)) != null) {
                        recyclerView_save_posts.removeItemDecoration(itemDecoration);
                    }

                    savePostsAdapter = new SavePostsAdapter(postModels, getActivity());
                    RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
                    recyclerView_save_posts.setLayoutManager(mLayoutManager);
                    recyclerView_save_posts.setHasFixedSize(false);
                    Collections.reverse(postModels);
                    recyclerView_save_posts.setAdapter(savePostsAdapter);
                    savePostsAdapter.notifyDataSetChanged();
                    loading.setVisibility(View.GONE);
                    if (String.valueOf(savePostsAdapter.getItemCount()).equals("0")){
                        nopostyet.setVisibility(View.VISIBLE);
                    }else {
                        nopostyet.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    loading.setVisibility(View.GONE);
                    nopostyet.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        SavePostsReceived();
    }
}