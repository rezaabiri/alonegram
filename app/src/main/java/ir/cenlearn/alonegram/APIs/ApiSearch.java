package ir.cenlearn.alonegram.APIs;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import ir.cenlearn.alonegram.Activities.SearchActivity;
import ir.cenlearn.alonegram.Model.UserSearchModel;
import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiSearch {

    private Context context;

    public ApiSearch(Context context){
        this.context=context;
    }

    public void getUsers(final OnUsersReceived onUsersReceived){
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.search_user+"?username="+ SearchActivity.usernametextwatcher, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<UserSearchModel> users =new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    UserSearchModel user =new UserSearchModel();
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        user.setuAvatar(jsonObject.getString("image"));
                        user.setuFullname(jsonObject.getString("fullname"));
                        user.setuUsername(jsonObject.getString("username"));
                        user.setuBluetick(jsonObject.getString("bluetick"));

                        users.add(user);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onUsersReceived.onReceived(users);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toaster.ToastError(context, R.string.servererror);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(18000,1,5));
        Volley.newRequestQueue(context).add(request);
    }

    public interface OnUsersReceived{
        void onReceived(List<UserSearchModel> users);
    }
}
