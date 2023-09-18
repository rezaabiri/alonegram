package ir.cenlearn.alonegram.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.cenlearn.alonegram.Adapter.UserAdapterViews;
import ir.cenlearn.alonegram.Model.ViewModel;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;

public class ShowViewsActivity extends AppCompatActivity {

    RecyclerView recyclerView_views;
    UserAdapterViews adapterViews;
    ImageView loading_views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_views);
        recyclerView_views = findViewById(R.id.recyclerview_views);
        loading_views = findViewById(R.id.loading_views);

        Glide.with(this).load(R.drawable.p_loading_post).into(loading_views);
        loading_views.setVisibility(android.view.View.VISIBLE);
        LoadViews();
    }

    public void LoadViews(){

        String url= Urls.host+Urls.get_views_and_show+"?id_post="+getIntent().getStringExtra("id");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<ViewModel> viewModels = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        ViewModel viewModel = new ViewModel();
                        JSONObject jsonObject = response.getJSONObject(i);
                        viewModel.setUsername(jsonObject.getString("username"));
                        viewModel.setFullname(jsonObject.getString("fullname"));
                        viewModel.setImage(jsonObject.getString("image"));
                        viewModel.setBluetick(jsonObject.getString("bluetick"));
                        viewModels.add(viewModel);

                        RecyclerView.ItemDecoration itemDecoration;
                        while (recyclerView_views.getItemDecorationCount() > 0 &&(itemDecoration = recyclerView_views.getItemDecorationAt(0)) != null) {
                            recyclerView_views.removeItemDecoration(itemDecoration);
                        }

                        adapterViews = new UserAdapterViews(viewModels, getApplicationContext());
                        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, GridLayoutManager.VERTICAL);
                        recyclerView_views.setLayoutManager(mLayoutManager);
                        recyclerView_views.setHasFixedSize(false);
                        Collections.reverse(viewModels);
                        recyclerView_views.setAdapter(adapterViews);
                        adapterViews.notifyDataSetChanged();
                        loading_views.setVisibility(android.view.View.GONE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading_views.setVisibility(android.view.View.VISIBLE);
                loading_views.setImageResource(R.drawable.refresh);
                loading_views.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Glide.with(ShowViewsActivity.this).load(R.drawable.p_loading_post).into(loading_views);
                        LoadViews();

                    }
                });
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(18000,5,1));
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void back_arrow_views(View view){
        finish();
        overridePendingTransition(0,0);
    }
}