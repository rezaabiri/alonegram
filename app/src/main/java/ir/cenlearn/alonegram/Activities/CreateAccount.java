package ir.cenlearn.alonegram.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import java.util.ArrayList;

import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.Others.Urls;
import ir.cenlearn.alonegram.Others.Encoder;
import ir.cenlearn.alonegram.R;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class CreateAccount extends AppCompatActivity {

    Button login_btn;
    EditText username_edittext, password_edittext, email_edittext;
    Button create_account_cardview;
    String question;

    Button btn_create_account;
    EditText edit_question;
    ProgressBar progressBar_create_account;

    TextView privacy;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //getSupportActionBar().hide();

        login_btn = findViewById(R.id.login);
        username_edittext = findViewById(R.id.username);
        password_edittext = findViewById(R.id.password);
        email_edittext = findViewById(R.id.email);
        create_account_cardview = findViewById(R.id.create);
        privacy = findViewById(R.id.privacy);
        //progressBar = findViewById(R.id.progressBar);

        AndroidNetworking.initialize(this);
        login_btn.setOnClickListener(v -> {
            startActivity(new Intent(CreateAccount.this, LoginActivity.class));
            finish();
        });


        CheckUserName();
        CheckPassword1();
        CheckEmail();

        privacy.setOnClickListener(v -> {
            startActivity(new Intent(CreateAccount.this, PrivacyPolicyActivity.class));
        });

        create_account_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username_edittext.getText().toString().trim().toLowerCase().length() < 6){
                    Toaster.ToastError(getApplicationContext(), R.string.wrongusername);

                }else if (password_edittext.getText().toString().trim().length() < 6){
                   Toaster.ToastError(getApplicationContext(), R.string.wrongpassword);

                }else if (!email_edittext.getText().toString().trim().toLowerCase().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){
                    Toaster.ToastError(getApplicationContext(), R.string.wronemail);
                }else{SecretQuestion();}
            }
        });
    }

    private void SecretQuestion(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_secret_question, null);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialogBuilder.getWindow().getAttributes().windowAnimations = R.style.DialogThemeMenu;

        Spinner secret_question;

        progressBar_create_account = dialogView.findViewById(R.id.progress_create_account);
        edit_question = dialogView.findViewById(R.id.edit_question);
        btn_create_account = dialogView.findViewById(R.id.btn_create_account);
        secret_question = dialogView.findViewById(R.id.secret_question);


        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("نام خواننده علاقه شما چیست؟");
        arrayList.add("نام حیوان خانگی شما چیست؟");
        arrayList.add("نام غذای مورد علاقه شما چیست؟");
        arrayList.add("نام آهنگ مورد علاقه شما چیست؟");
        arrayList.add("نام و نام خانوادگی بهترین دوستتان چیست؟");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.layout_spiner, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.layout_spiner);
        secret_question.setAdapter(arrayAdapter);

        secret_question.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                question = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

        btn_create_account.setOnClickListener(v -> {
            if (!edit_question.getText().toString().trim().isEmpty()){
                progressBar_create_account.setVisibility(View.VISIBLE);
                progressBar_create_account.isShown();
                btn_create_account.setText("");
                btn_create_account.setEnabled(false);

                String encrypt = Encoder.sha256(password_edittext.getText().toString().trim());
                String pass = encrypt.substring(9, encrypt.length() - 10);
                createAcc(username_edittext.getText().toString().trim().toLowerCase(), Encoder.sha256(pass.toLowerCase().trim()), email_edittext.getText().toString().trim().toLowerCase(), Encoder.sha256(edit_question.getText().toString()));
            }else {
                Toaster.ToastErrorNormal(getApplicationContext(),"پاسخ را وارد کنید");
            }

        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
    private void createAcc(String username, String password, String email, String answer){
        AndroidNetworking.post(Urls.host+Urls.create_account_api)
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .addBodyParameter("email", email)
                .addBodyParameter("question_secret",question)
                .addBodyParameter("answer_secret",answer)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("done")){
                            Toaster.ToastSuccessNormal( getApplicationContext(),"حساب کاربری با موفقیت ساخته شد");
                            progressBar_create_account.setVisibility(View.GONE);
                            btn_create_account.setText("ساخت حساب کاربری");
                            btn_create_account.setEnabled(true);

                            Intent i = new Intent(CreateAccount.this, LoginActivity.class);
                            String username = username_edittext.getText().toString().trim();
                            String password = password_edittext.getText().toString().trim();
                            i.putExtra("username", username);
                            i.putExtra("password", password);
                            startActivity(i);

                            finish();
                        }else if (response.contains("exists")){
                            progressBar_create_account.setVisibility(View.GONE);
                            btn_create_account.setText("ساخت حساب کاربری");
                            btn_create_account.setEnabled(true);
                            Toaster.ToastError(getApplicationContext(), R.string.userexist);
                        }else {
                            progressBar_create_account.setVisibility(View.GONE);
                            btn_create_account.setText("ساخت حساب کاربری");
                            btn_create_account.setEnabled(true);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar_create_account.setVisibility(View.GONE);
                        btn_create_account.setText("ساخت حساب کاربری");
                        btn_create_account.setEnabled(true);
                      Toaster.ToastError(getApplicationContext(), R.string.servererror);
                    }
                });
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void CheckUserName(){
        username_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (username_edittext.getText().toString().trim().toLowerCase().length() < 6){
                    username_edittext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user_icon_edit,0,0,0);
                }else {
                    username_edittext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user_icon_edit_green,0,0,0);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void CheckPassword1(){
        password_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password_edittext.getText().toString().trim().length() < 6 ){
                    password_edittext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.password,0,0,0);
                }else {
                    password_edittext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.password_green,0,0,0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void CheckEmail(){
       email_edittext.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               if (!email_edittext.getText().toString().trim().toLowerCase().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){
                   email_edittext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email_icon,0,0,0);
               }else {
                   email_edittext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email_icon_green,0,0,0);

               }
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });
    }


}
