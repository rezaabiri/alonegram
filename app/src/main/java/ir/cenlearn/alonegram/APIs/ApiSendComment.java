package ir.cenlearn.alonegram.APIs;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

import ir.cenlearn.alonegram.Others.Urls;

public class ApiSendComment {

    private Context context;

    public ApiSendComment(Context context) {
        this.context = context;

    }
    public void SendComment(HashMap<String,String> Comments, final onReceivedComment onReceivedComment) {
        String POST_URL = Urls.host+Urls.sendcomment;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, POST_URL, new JSONObject(Comments), response -> {
            onReceivedComment.onReceived(true);
        }, error -> {
            onReceivedComment.onReceived(false);
        }) {
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(18000, 1, 5));
        Volley.newRequestQueue(context).add(jsonObjReq);
    }

    public interface onReceivedComment{
        void onReceived(boolean success);
    }

}
