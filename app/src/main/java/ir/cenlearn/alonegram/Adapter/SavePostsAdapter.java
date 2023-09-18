package ir.cenlearn.alonegram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ir.cenlearn.alonegram.Activities.MusicActivity;
import ir.cenlearn.alonegram.Activities.ShowResultClickPost;
import ir.cenlearn.alonegram.Model.PostModel;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;

public class SavePostsAdapter extends RecyclerView.Adapter<SavePostsAdapter.MyViewHolder> {

    List<PostModel> postsList;
    Context mContext;
    public String bluetick, videoid, id;

    public SavePostsAdapter(List<PostModel> postsList, Context mContext) {
        this.postsList = postsList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View aView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_posts, parent, false);
        return new MyViewHolder(aView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final PostModel postModel = postsList.get(position);

        holder.Videoname.setText(postModel.getVideoName());
        Picasso.get().load(Urls.host+ postModel.getVideoImage()).error(R.drawable.image_broken_variant).fit().into(holder.Videoimage);

        if (!postModel.getMusic().equals("null")){
            holder.type_music.setVisibility(View.VISIBLE);
        }
        holder.LinearPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    holder.LinearPosts.setEnabled(false);
                    JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.show_post_for_comment+"?post_id="+ postModel.getId(), null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObject=response.getJSONObject(i);

                                    if (!postModel.getMusic().equals("null")){
                                        holder.type_music.setVisibility(View.VISIBLE);
                                        Intent intent = new Intent(mContext, MusicActivity.class);
                                        intent.putExtra("music_cover", jsonObject.getString("videoimage"));
                                        intent.putExtra("music_url",jsonObject.getString("music"));
                                        intent.putExtra("id", jsonObject.getString("idpost"));
                                        intent.putExtra("username", jsonObject.getString("username"));
                                        intent.putExtra("music_name", jsonObject.getString("videoname").replace("&#39;","'"));
                                        intent.putExtra("artist", jsonObject.getString("videodescription").replace("&#39;","'"));
                                        mContext.startActivity(intent);
                                        holder.LinearPosts.setEnabled(true);
                                    }else {
                                        Intent intent = new Intent(mContext, ShowResultClickPost.class);

                                        intent.putExtra("video_image",jsonObject.getString("videoimage"));
                                        intent.putExtra("video_name",jsonObject.getString("videoname").replace("&#39;","'"));
                                        intent.putExtra("video_category",jsonObject.getString("videocategory").replace("&#39;","'"));
                                        intent.putExtra("username",jsonObject.getString("username"));
                                        intent.putExtra("fullname",jsonObject.getString("fullname").replace("&#39;","'"));
                                        intent.putExtra("image",jsonObject.getString("image"));
                                        intent.putExtra("videodesc",jsonObject.getString("videodescription").replace("&#39;","'"));
                                        intent.putExtra("bluetick",jsonObject.getString("bluetick"));
                                        intent.putExtra("viewpost",jsonObject.getString("view"));
                                        intent.putExtra("likepost",jsonObject.getString("likes"));
                                        intent.putExtra("id",jsonObject.getString("idpost"));
                                        intent.putExtra("datetime",jsonObject.getString("shamsi"));
                                        intent.putExtra("videoid",jsonObject.getString("videoid"));
                                        intent.putExtra("0","0");

                                        mContext.startActivity(intent);
                                        holder.LinearPosts.setEnabled(true);
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { }
                    });

                    request.setRetryPolicy(new DefaultRetryPolicy(18000, 5, 5));
                    Volley.newRequestQueue(mContext).add(request);

                }catch (Exception e){e.printStackTrace();}

            }
        });
    }
    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView Videoimage, type_music;
        public TextView Videoname;
        public LinearLayout LinearPosts;
        public CardView card_viewholder;
        public MyViewHolder(View itemView) {
            super(itemView);

            Videoimage = itemView.findViewById(R.id.video_image);
            Videoname = itemView.findViewById(R.id.video_name);
            LinearPosts = itemView.findViewById(R.id.Linear_posts);
            card_viewholder = itemView.findViewById(R.id.card_main);
            type_music = itemView.findViewById(R.id.type_music);

        }
    }
}
