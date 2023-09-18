package ir.cenlearn.alonegram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ir.cenlearn.alonegram.Activities.ShowResultSearch;
import ir.cenlearn.alonegram.Model.ViewModel;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;

public class UserAdapterViews extends RecyclerView.Adapter<UserAdapterViews.MyViewHolder> {

    List<ViewModel> userList;
    Context mContext;

    public UserAdapterViews(List<ViewModel> userList, Context mContext) {
        this.userList = userList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View aView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_show_views, parent, false);
        return new MyViewHolder(aView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final ViewModel user = userList.get(position);

        holder.aUsername.setText(user.getUsername());
        holder.aFullname.setText(user.getFullname());
        Picasso.get().load(Urls.host+user.getImage()).placeholder(R.drawable.p_profile).centerCrop().fit().into(holder.aAvatar);


        if (user.getBluetick().equals("active")){
            holder.bluetick.setVisibility(View.VISIBLE);
        }


        holder.userViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShowResultSearch.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Avatar", user.getImage());
                intent.putExtra("Name", user.getUsername());
                intent.putExtra("Fullname", user.getFullname().replace("&#39;","'"));
                intent.putExtra("bluetick", user.getBluetick());
                mContext.startActivity(intent);

                /*
                try {

                    Intent intent = new Intent(mContext, ShowResultSearch.class);
                    intent.putExtra("Avatar", user.getImage());
                    intent.putExtra("Name", user.getUsername());
                    intent.putExtra("Fullname", user.getFullname().replace("&#39;","'"));
                    intent.putExtra("bluetick", user.getBluetick());
                    mContext.startActivity(intent);

                    /*

                    Intent i = new Intent(mContext, ShowResultSearch.class);
                    String aAvatar = aItem.getuAvatar();
                    String aName = holder.aUsername.getText().toString();
                    String aFullname = holder.aFullname.getText().toString();
                    String bluetick = aItem.getuBluetick();
                    i.putExtra("Avatar", aAvatar);
                    i.putExtra("Name", aName);
                    i.putExtra("Fullname", aFullname.replace("&#39;","'"));
                    i.putExtra("bluetick", bluetick);
                    mContext.startActivity(i);

                    preferences = mContext.getSharedPreferences("User_name",Context.MODE_PRIVATE);
                    preferences.edit().putString("User_name", aName).apply();




                }catch (Exception e){
                    Toast.makeText(mContext, "l:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


                 */

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView bluetick, aAvatar;
        public TextView aUsername, aFullname;

        public LinearLayout userViews;


        public MyViewHolder(View itemView) {
            super(itemView);

            aAvatar = itemView.findViewById(R.id.user_image_views);
            aUsername = itemView.findViewById(R.id.username_views);
            aFullname = itemView.findViewById(R.id.fullname_views);
            userViews = itemView.findViewById(R.id.linear_views);
            bluetick = itemView.findViewById(R.id.bluetick_in_views);

        }
    }

}
