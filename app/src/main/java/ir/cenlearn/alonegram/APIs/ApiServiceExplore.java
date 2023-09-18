package ir.cenlearn.alonegram.APIs;

import android.content.Context;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import ir.cenlearn.alonegram.Model.PostModel;

import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static ir.cenlearn.alonegram.Activities.ExploreActivity.loading_explore;

public class ApiServiceExplore {
    private Context context;

    public ApiServiceExplore(Context context){
        this.context=context;
    }

    public void getPosts(final OnPostsReceived onPostsReceived){


        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.explore, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<PostModel> postModels =new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    PostModel postModel =new PostModel();

                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        postModel.setVideoName(StringEscapeUtils.unescapeJava(jsonObject.getString("videoname").replace("&#39;","'")));
                        postModel.setCategory(StringEscapeUtils.unescapeJava(jsonObject.getString("videocategory").replace("&#39;","'")));
                        postModel.setVideodescription(StringEscapeUtils.unescapeJava(jsonObject.getString("videodescription").replace("&#39;","'")));
                        postModel.setId(jsonObject.getString("idpost"));
                        postModel.setVideoImage(jsonObject.getString("videoimage"));
                        postModel.setUsername(jsonObject.getString("username"));
                        postModel.setFullname(jsonObject.getString("fullname").replace("&#39;","'"));
                        postModel.setImage(jsonObject.getString("image"));
                        postModel.setBluetick(jsonObject.getString("bluetick"));
                        postModel.setVideoid(jsonObject.getString("videoid"));
                        postModel.setMusic(jsonObject.getString("music"));
                        postModel.setView(jsonObject.getString("view"));
                        postModel.setLikes(jsonObject.getString("likes"));
                        postModel.setDatetime(jsonObject.getString("shamsi"));

                       // post.setToken(jsonObject.getString("token"));
                        postModels.add(postModel);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onPostsReceived.onReceived(postModels);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading_explore.setImageResource(R.drawable.refresh);
                loading_explore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Glide.with(context).load(R.drawable.p_loading_post).into(loading_explore);
                        getPosts(onPostsReceived);

                    }
                });
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(18000,5,5));
        Volley.newRequestQueue(context).add(request);
    }

    public interface OnPostsReceived{
        void onReceived(List<PostModel> postModels);
    }
}
