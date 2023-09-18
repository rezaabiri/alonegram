package ir.cenlearn.alonegram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.cenlearn.alonegram.Model.UserSearchModel;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;
import ir.cenlearn.alonegram.Activities.ShowResultSearch;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    List<UserSearchModel> itemList;
    Context mContext;
    public static SharedPreferences preferences;

    public UserAdapter(List<UserSearchModel> itemList, Context mContext) {
        this.itemList = itemList;
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View aView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result_search, parent, false);
        return new MyViewHolder(aView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final UserSearchModel userSearchModel = itemList.get(position);

        holder.aUsername.setText(userSearchModel.getuUsername());
        holder.aFullname.setText(userSearchModel.getuFullname());
        Picasso.get().load(Urls.host+ userSearchModel.getuAvatar()).fit().centerCrop().placeholder(R.drawable.p_profile).into(holder.aAvatar);

        if (userSearchModel.getuBluetick().equals("active")){
            holder.bluetick.setVisibility(View.VISIBLE);
        }else {
            holder.bluetick.setVisibility(View.GONE);
        }


        holder.aItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Intent i = new Intent(mContext, ShowResultSearch.class);
                    i.putExtra("Avatar", userSearchModel.getuAvatar());
                    i.putExtra("Name", holder.aUsername.getText().toString());
                    i.putExtra("Fullname", holder.aFullname.getText().toString().replace("&#39;","'"));
                    i.putExtra("bluetick", userSearchModel.getuBluetick());
                    mContext.startActivity(i);

                    preferences = mContext.getSharedPreferences("User_name",Context.MODE_PRIVATE);
                    preferences.edit().putString("User_name", holder.aUsername.getText().toString()).apply();


                }catch (Exception e){e.printStackTrace();}

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView bluetick;
        public CircleImageView aAvatar;
        public TextView aUsername, aFullname;

        public LinearLayout aItem;


        public MyViewHolder(View itemView) {
            super(itemView);

            aAvatar = itemView.findViewById(R.id.img_avatar);
            aUsername = itemView.findViewById(R.id.txt_username);
            aFullname = itemView.findViewById(R.id.txt_fullname);
            aItem = itemView.findViewById(R.id.item_layout1);
            bluetick = itemView.findViewById(R.id.blueticksearch);

        }
    }

}
