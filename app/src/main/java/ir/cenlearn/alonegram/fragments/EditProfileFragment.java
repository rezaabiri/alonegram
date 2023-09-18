package ir.cenlearn.alonegram.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.ParsedRequestListener;

import ir.cenlearn.alonegram.Activities.MainActivity;
import ir.cenlearn.alonegram.Activities.ShowResultClickPost;
import ir.cenlearn.alonegram.ImageCompressor.Compressor;

import ir.cenlearn.alonegram.Others.ProgressCustom;
import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.R;
import ir.cenlearn.alonegram.Others.RealPath;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.Model.UserModel;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;


import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static ir.cenlearn.alonegram.Activities.MainActivity.frameLayout_status;


public class EditProfileFragment extends Fragment {


    public EditProfileFragment() {
        // Required empty public constructor
    }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getActivity().getWindow();
            w.setStatusBarColor(Color.parseColor("#2DABBC"));
            TextView app_name = getActivity().findViewById(R.id.app_name);
            ImageView drawer = getActivity().findViewById(R.id.drawer);
            FrameLayout actionbar = getActivity().findViewById(R.id.actionbar);
            app_name.setTextColor(Color.parseColor("#ffffff"));
            drawer.setImageResource(R.drawable.ic_list_white);
            actionbar.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false);

    }

    ImageView image_test,ed_img;
    EditText edit_name, edit_email, edit_phone;
    Button btn_submit;
    TextView guid;
    static ProgressDialog progressDialog, progressDialogimg;
    static SharedPreferences preferences, prefPath;
    static String path, status;
    ProgressCustom progressbar;
    TextView change_profile;
    String full_name, email, phone;
    Bitmap bitmap;

    static Bitmap image;
    File compresed;

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ed_img = getActivity().findViewById(R.id.edit_userimage);
        edit_name = getActivity().findViewById(R.id.edit_fullname);
        edit_email = getActivity().findViewById(R.id.edit_email);
        btn_submit = getActivity().findViewById(R.id.btn_submit);
        edit_phone = getActivity().findViewById(R.id.edit_phone);
        change_profile = getActivity().findViewById(R.id.change_profile);
        progressbar = new ProgressCustom();
        AndroidNetworking.initialize(getContext());

        preferences = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        prefPath = getActivity().getSharedPreferences("path",MODE_PRIVATE);

        if (preferences.getString("uImage", "").equals("")){
            ed_img.setImageResource(R.drawable.p_profile);
        }else {
            Picasso.get().load(Urls.host+preferences.getString("uImage","")).fit().centerCrop().placeholder(R.drawable.p_profile).into(ed_img);
        }

        full_name = preferences.getString("name","");
        email = preferences.getString("email","");
        phone = preferences.getString("phone","");

        edit_name.setText(preferences.getString("name", "").replace("&#39;","'"));
        edit_email.setText(preferences.getString("email", ""));
        edit_phone.setText(preferences.getString("phone", ""));

        PhoneCheck();
        EmailCheck();
        NameCheck();
        ApplyChange();

        change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askReadPermission();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    if (edit_email.getText().toString().isEmpty() || ! edit_email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                        return;
                    }else if (! edit_phone.getText().toString().isEmpty() && ! edit_phone.getText().toString().trim().matches("(\\+98|0)?9\\d{9}")){
                        return;
                    } else if(full_name.equals(edit_name.getText().toString()) && email.equals(edit_email.getText().toString()) && phone.equals(edit_phone.getText().toString())) {
                        //Toaster.ToastSuccess(getContext(), R.string.updateprofile);
                        //startActivity(new Intent(getActivity(),MainActivity.class));
                    }else {
                        progressbar.show(getActivity());
                        update();

                    }
                }catch (Exception e){}

            }
        });
    }
    public void update(){
        AndroidNetworking.upload(Urls.host+Urls.update_profile_api)
                .addMultipartParameter("fullname", edit_name.getText().toString().replace("'","&#39;"))
                .addMultipartParameter("email", edit_email.getText().toString())
                .addMultipartParameter("phone", edit_phone.getText().toString())
                .addMultipartParameter("username", preferences.getString("username", ""))
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);

                                preferences.edit().putString("name",jsonObject.getString("fullname")).apply();
                                preferences.edit().putString("email",jsonObject.getString("email")).apply();
                                preferences.edit().putString("phone",jsonObject.getString("phone")).apply();

                                Toaster.ToastSuccess(getContext(), R.string.updateprofile);
                                progressbar.dismiss();
                                //Intent intent = new Intent(getActivity(), MainActivity.class);
                               // String aFullname = jsonObject.getString("fullname").replace("&#39;","'");
                               // intent.putExtra("Fullname", aFullname);
                               // getActivity().startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toaster.ToastError(getContext(), R.string.servererror);
                        progressbar.dismiss();
                    }
                });

    }

    public void UpdateProf(File file){
        progressbar.show(getContext());
        AndroidNetworking.upload(Urls.host+Urls.imageprof)
                .addMultipartFile("image",file)
                .addMultipartParameter("username", preferences.getString("username", ""))
                .setTag("UPDATE")
                .build()
                .getAsObject(UserModel.class, new ParsedRequestListener<UserModel>() {
                    @Override
                    public void onResponse(UserModel response) {

                        try {
                            preferences = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
                            preferences.edit().putString("uImage",response.getImageUrl()).apply();
                            progressbar.dismiss();
                            Toaster.ToastSuccess(getContext(), R.string.updateprofile);


                        }catch (Exception e){
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Toaster.ToastError(getContext(), R.string.servererror);
                        progressbar.dismiss();
                    }
                });

    }

    private void askReadPermission(){
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        pickImage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response != null && response.isPermanentlyDenied()){
                            Snackbar.make(btn_submit,R.string.permission, Snackbar.LENGTH_LONG).setAction(R.string.allow, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    i.setData(Uri.fromParts("package",getContext().getPackageName(),null));
                                    startActivity(i);
                                }
                            }).show();
                        }else {
                            Snackbar.make(btn_submit,R.string.permission,Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, final PermissionToken token) {
                        Snackbar.make(btn_submit,R.string.permission,Snackbar.LENGTH_LONG).setAction(R.string.allow, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                token.continuePermissionRequest();
                            }
                        }).show();
                    }
                }).check();
    }

    private void pickImage(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);//one can be replaced with any action code



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data.getData() != null) {

            preferences = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
            path = RealPath.getPathFromUri(getActivity(), data.getData());
            prefPath.edit().putString("path",path.toString()).apply();

            bitmap = Compressor.getDefault(getContext()).compressToBitmap(new File(path));

            btn_submit.setEnabled(true);
            compresed = Compressor.getDefault(getContext()).compressToFile(new File(path));

            Picasso.get().load(compresed).centerCrop().fit().placeholder(R.drawable.p_gif_loading).into(ed_img);
            UpdateProfile(bitmap);


        }
    }

    private void UpdateProfile(Bitmap bm){
        progressbar.show(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.host + Urls.imageprof, response -> {
            if (response.equals("error")){
                Toaster.ToastError(getContext(), R.string.servererror);
                progressbar.dismiss();
            }else {
                progressbar.dismiss();
                Toaster.ToastSuccessNormal(getActivity(), "تصویر پروفایل بروزرسانی شد");
                preferences = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
                preferences.edit().putString("uImage",response).apply();
            }
        }, error -> {
            Toaster.ToastError(getContext(), R.string.servererror);
            progressbar.dismiss();

        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("imageProfile", ImageToString(bm));
                params.put("username",preferences.getString("username",""));

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(7000,1,1));
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    private String ImageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageByte,Base64.DEFAULT);
    }


    public void ApplyChange(){
        String username = MainFragment.preferences.getString("username","");
        Log.i("username:",username);

        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, Urls.host+Urls.showposts+"?username="+username, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);

                        edit_email.setText(jsonObject.getString("email"));
                        edit_name.setText(jsonObject.getString("fullname").replace("&#39;","'"));
                        edit_phone.setText(jsonObject.getString("phone"));

                        preferences.edit().putString("email", edit_email.getText().toString().trim()).apply();
                        preferences.edit().putString("name", edit_name.getText().toString().replace("'","&#39;")).apply();
                        preferences.edit().putString("phone", edit_phone.getText().toString().trim()).apply();

                    } catch (JSONException e) {
                        Log.i("onResponse e: ",e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(18000,5,5));
        Volley.newRequestQueue(getContext()).add(request);
    }

    public void NameCheck(){
        edit_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edit_name.getText().toString().isEmpty()){
                    edit_name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user_icon_edit_green,0,0,0);
                }else {
                    edit_name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user_icon_edit,0,0,0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void PhoneCheck(){
        edit_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edit_phone.getText().toString().trim().matches("(\\+98|0)?9\\d{9}")){
                    edit_phone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phone_android_green, 0, 0, 0);
                }else {
                    edit_phone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phone_android, 0, 0, 0);
                }
            }
        });

    }

    public void EmailCheck(){
        edit_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edit_email.getText().toString().isEmpty() || ! edit_email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){
                    edit_email.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email_icon, 0, 0, 0);
                }else {
                    edit_email.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email_icon_green, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
