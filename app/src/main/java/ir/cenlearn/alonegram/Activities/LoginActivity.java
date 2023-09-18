package ir.cenlearn.alonegram.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;

import ir.cenlearn.alonegram.Model.Comment;
import ir.cenlearn.alonegram.Others.SessionManager;
import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.Model.UserModel;
import ir.cenlearn.alonegram.Others.Encoder;
import ir.cenlearn.alonegram.R;

import com.androidnetworking.interfaces.StringRequestListener;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class LoginActivity extends AppCompatActivity {

    EditText user, pass;
    Button create, login;
    ProgressBar progressBar;
    String usernamekey, passwordkey, question_get, username_get;
    TextView forget_password, question;
    SharedPreferences preferences;


    Button btn_confirm, btn_confirm_pass;
    EditText edit_answer_confirm, edit_new_pass, edit_new_pass_confirm;
    ProgressBar progressBar_confirm, progressBar_new_pass;
    ConstraintLayout layout_btn_pass;

    Button btn_find_username;
    EditText edit_username, edit_pass;
    ProgressBar progressBar_next_step;

    LinearLayout linear_get_username, linear_confirm_question, linear_renew_pass;

    AlertDialog dialogBuilder_confirm, dialogBuilder_recovery;
    View dialogView_confirm, dialogView_recovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();


        user = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        create = findViewById(R.id.create);
        progressBar = findViewById(R.id.progressBar);
        login = findViewById(R.id.btn_login);
        forget_password = findViewById(R.id.forget_password);

        final SessionManager manager = new SessionManager(this);
        if (manager.isLoggedIn()){
            startActivity(new Intent(LoginActivity.this, SplashScreenActivity.class));
            finish();
        }

        AndroidNetworking.initialize(this);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CreateAccount.class));
                finish();
            }
        });

        forget_password.setOnClickListener(v -> {
            RecoveryPassword();
        });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null){
            usernamekey = extras.getString("username");
            passwordkey = extras.getString("password");
            user.setText(usernamekey);
            pass.setText(passwordkey);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user.length() < 6){
                    Toaster.ToastError(getApplicationContext(), R.string.wrongusername);
                }else if (pass.length() < 6){
                    Toaster.ToastError(getApplicationContext(), R.string.wrongpassword);
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.isShown();
                    login.setText("");
                    create.setEnabled(false);

                    String encrypt = Encoder.sha256(pass.getText().toString().trim());
                    String passencrypt = encrypt.substring(9, encrypt.length() - 10);

                    final HashMap<String, String> postParams = new HashMap<String, String>();
                    postParams.put("username" ,user.getText().toString().trim().toLowerCase());
                    postParams.put("password" ,Encoder.sha256(passencrypt));
                    Login(postParams, manager);
                }
                 
            }
        });

    }


    public void Login(HashMap<String,String> info, SessionManager manager){
        String POST_URL = Urls.host+Urls.login_api;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, POST_URL, new JSONObject(info), response -> {

            try {
                if (response.getString("username").trim().toLowerCase().equals(user.getText().toString().trim().toLowerCase())){
                    preferences = getSharedPreferences("prefs",MODE_PRIVATE);
                    preferences.edit().putString("username",response.getString("username")).apply();
                    preferences.edit().putString("email",response.getString("email")).apply();
                    preferences.edit().putString("image",response.getString("imageUrl")).apply();
                    manager.setLoggedIn(true);

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                }else {
                    Toaster.ToastErrorNormal(getApplicationContext(), "نام کاربری یا رمز عبور اشتباه است");
                    progressBar.setVisibility(View.GONE);
                    login.setText("ورود به برنامه");
                    create.setEnabled(true);
                }


            } catch (JSONException e) {e.printStackTrace();}

        }, error -> {
            Toaster.ToastError(getApplicationContext(),R.string.servererror);
            progressBar.setVisibility(View.GONE);
            login.setText("ورود به برنامه");
            create.setEnabled(true);
        }) {
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(18000, 1, 5));
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjReq);
    }


    private void RecoveryPassword(){

        dialogBuilder_confirm = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        dialogView_confirm = inflater.inflate(R.layout.layout_forget_password, null);
        dialogBuilder_confirm.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialogBuilder_confirm.getWindow().getAttributes().windowAnimations = R.style.DialogThemeMenu;

        progressBar_confirm = dialogView_confirm.findViewById(R.id.progress_confirm);
        progressBar_new_pass = dialogView_confirm.findViewById(R.id.progress_confirm_pass);
        edit_new_pass = dialogView_confirm.findViewById(R.id.edit_new_pass);
        edit_new_pass_confirm = dialogView_confirm.findViewById(R.id.edit_new_pass_confrim);
        btn_confirm_pass = dialogView_confirm.findViewById(R.id.btn_confirm_pass);
        edit_answer_confirm = dialogView_confirm.findViewById(R.id.edit_answer_confirm);
        btn_confirm = dialogView_confirm.findViewById(R.id.btn_confirm);
        question = dialogView_confirm.findViewById(R.id.question_txt);
        progressBar_next_step = dialogView_confirm.findViewById(R.id.progress_next_step);

        edit_username = dialogView_confirm.findViewById(R.id.edit_username);
        edit_pass = dialogView_confirm.findViewById(R.id.edit_email_forgetpass);
        btn_find_username = dialogView_confirm.findViewById(R.id.btn_find_username);
        layout_btn_pass = dialogView_confirm.findViewById(R.id.layout_btn_pass);

        linear_get_username = dialogView_confirm.findViewById(R.id.linear_get_username);
        linear_confirm_question = dialogView_confirm.findViewById(R.id.linear_confrim_question);
        linear_renew_pass = dialogView_confirm.findViewById(R.id.linear_renew_pass);


        btn_find_username.setOnClickListener(v -> {
            if (!edit_username.getText().toString().trim().isEmpty()){
                progressBar_next_step.setVisibility(View.VISIBLE);
                progressBar_next_step.isShown();
                btn_find_username.setText("");
                btn_find_username.setEnabled(false);
                FindUser(edit_username.getText().toString().trim().toLowerCase(), edit_pass.getText().toString().trim().toLowerCase());
            }else {
                Toaster.ToastErrorNormal(getApplicationContext(),"نام کاربری را وارد کنید");
            }

        });

        btn_confirm.setOnClickListener(v -> {
            if (!edit_answer_confirm.getText().toString().isEmpty()){
                progressBar_confirm.setVisibility(View.VISIBLE);
                progressBar_confirm.isShown();
                btn_confirm.setText("");
                btn_confirm.setEnabled(false);
                ConfirmAnswer(Encoder.sha256(edit_answer_confirm.getText().toString()), username_get);
            }else {
                Toaster.ToastErrorNormal(getApplicationContext(),"پاسخ را وارد کنید");
            }

        });

        dialogBuilder_confirm.setView(dialogView_confirm);
        dialogBuilder_confirm.show();
    }


    public void FindUser(String username, String email){
        String url=Urls.host+Urls.get_secret_question+"?username="+username+"&email="+email;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);

                        if (jsonObject.getString("username").toLowerCase().equals("error")){
                            Toaster.ToastErrorNormal(getApplicationContext(),"نام کاربری یا ایمیل اشتباه است");
                            progressBar_next_step.setVisibility(View.GONE);
                            btn_find_username.setText("مرحله بعدی");
                            btn_find_username.setEnabled(true);
                        }else {
                            question_get = jsonObject.getString("question");
                            username_get = jsonObject.getString("username").toLowerCase();
                            linear_get_username.setVisibility(View.GONE);
                            linear_confirm_question.setVisibility(View.VISIBLE);
                            question.setText(StringEscapeUtils.unescapeJava(jsonObject.getString("question")));
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
        });
        request.setRetryPolicy(new DefaultRetryPolicy(10000,5,5));
        Volley.newRequestQueue(getApplicationContext()).add(request);

    }

    public void ConfirmAnswer(String answer, String username){
        String url=Urls.host+Urls.confirm_question+"?answer="+answer+"&username="+username;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String password = jsonObject.getString("password");

                        if (password.equals("ok")){

                            linear_confirm_question.setVisibility(View.GONE);
                            linear_renew_pass.setVisibility(View.VISIBLE);

                            btn_confirm_pass.setOnClickListener(v -> {
                                if (!edit_new_pass.getText().toString().trim().toLowerCase().equals(edit_new_pass_confirm.getText().toString().toLowerCase())){
                                    Toaster.ToastErrorNormal(getApplicationContext(), "تکرار رمز عبور صحیح نیست");
                                }else if (edit_new_pass.getText().toString().isEmpty() || edit_new_pass_confirm.getText().toString().isEmpty()){
                                    Toaster.ToastErrorNormal(getApplicationContext(), "رمز عبور کوتاه است");
                                } else if (edit_new_pass_confirm.getText().toString().trim().length() < 6){
                                    Toaster.ToastErrorNormal(getApplicationContext(), "رمز عبور کوتاه است");
                                }else {
                                    progressBar_new_pass.setVisibility(View.VISIBLE);
                                    btn_confirm_pass.setText("");
                                    btn_confirm_pass.setEnabled(false);

                                    String encrypt = Encoder.sha256(edit_new_pass_confirm.getText().toString().trim());
                                    String pass = encrypt.substring(9, encrypt.length() - 10);
                                    RenewPassword(username_get, Encoder.sha256(pass.toLowerCase().trim()));
                                }
                            });

                        }else {
                            progressBar_confirm.setVisibility(View.GONE);
                            btn_confirm.setText("تایید اطلاعات");
                            btn_confirm.setEnabled(true);
                            Toaster.ToastErrorNormal(getApplicationContext(), "پاسخ امنیتی اشتباه است");
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            progressBar_confirm.setVisibility(View.GONE);
            btn_confirm.setText("تایید اطلاعات");
            btn_confirm.setEnabled(true);
        });
        request.setRetryPolicy(new DefaultRetryPolicy(10000,5,5));
        Volley.newRequestQueue(getApplicationContext()).add(request);

    }

    public void RenewPassword(String username, String password){
        AndroidNetworking.post(Urls.host+Urls.update_password+"?newpass="+password+"&username="+username)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("ok")){
                            dialogBuilder_confirm.dismiss();
                        }else {
                            Toaster.ToastErrorNormal(getApplicationContext(), "رمز عبور تغییر نکرد");
                            progressBar_new_pass.setVisibility(View.GONE);
                            btn_confirm_pass.setText("تغییر رمز عبور");
                            btn_confirm_pass.setEnabled(true);
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar_new_pass.setVisibility(View.GONE);
                        btn_confirm_pass.setText("تغییر رمز عبور");
                        btn_confirm_pass.setEnabled(true);
                    }
                });

    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
