package ir.cenlearn.alonegram.APIs;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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

import ir.cenlearn.alonegram.Activities.CommentActivity;
import ir.cenlearn.alonegram.Adapter.CommentAdapter;
import ir.cenlearn.alonegram.Model.Comment;
import ir.cenlearn.alonegram.Model.PostModel;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;

import static ir.cenlearn.alonegram.Activities.CommentActivity.linear_nocomment;
import static ir.cenlearn.alonegram.Activities.CommentActivity.loading_comments;
import static ir.cenlearn.alonegram.Activities.ExploreActivity.loading_explore;

public class ApiGetComments {
    private Context context;

    public ApiGetComments(Context context){
        this.context=context;
    }

    public void getComments(String id_comment, final ApiGetComments.OnCommentReceived onCommentReceived){

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.getcomment+"?idpost="+id_comment, null, response -> {

            List<Comment> comments = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                Comment comment  = new Comment();
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    comment.setUsername(jsonObject.getString("username"));
                    comment.setImageComment(jsonObject.getString("image"));
                    comment.setComment(jsonObject.getString("comment").replace("&#39;","'"));
                    comment.setPost_id(jsonObject.getString("post_id"));
                    comment.setDatetime(jsonObject.getString("shamsi"));
                    comment.setCommentid(jsonObject.getString("auto_id"));
                    comment.setFullname(jsonObject.getString("fullname"));
                    comment.setBluetick(jsonObject.getString("bluetick"));
                    comment.setTargetReplay(jsonObject.getString("TargetReplay"));
                    comment.setPreviewReplay(jsonObject.getString("PreviewReplay"));
                    try{
                        comments.add(0,comment);
                    }catch (Exception e){e.printStackTrace();}

                } catch (JSONException e) {e.printStackTrace();}
                onCommentReceived.onReceived(comments);
            }
        }, error -> {
            loading_comments.setVisibility(android.view.View.VISIBLE);
            loading_comments.setImageResource(R.drawable.refresh);
            loading_comments.setOnClickListener(v -> {
                Glide.with(context).load(R.drawable.p_loading_post).into(loading_comments);
                getComments(id_comment,onCommentReceived);

            });
        });

        request.setRetryPolicy(new DefaultRetryPolicy(18000, 5, 5));
        Volley.newRequestQueue(context).add(request);
    }

    public interface OnCommentReceived{
        void onReceived(List<Comment> comments);
    }
}
