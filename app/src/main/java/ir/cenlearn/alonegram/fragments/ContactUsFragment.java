package ir.cenlearn.alonegram.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ir.cenlearn.alonegram.Others.Toaster;
import ir.cenlearn.alonegram.R;


public class ContactUsFragment extends Fragment {

    ImageView twitter, instagram, email, github, website;

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

        TextView app_name = getActivity().findViewById(R.id.app_name);
        ImageView drawer = getActivity().findViewById(R.id.drawer);
        FrameLayout actionbar = getActivity().findViewById(R.id.actionbar);
        app_name.setTextColor(Color.parseColor("#000000"));
        drawer.setImageResource(R.drawable.ic_list);
        actionbar.setElevation(5);
        actionbar.setBackgroundColor(Color.parseColor("#ffffff"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getActivity().getWindow();
            //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //w.addFlags(WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION);
            //w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.setStatusBarColor(Color.parseColor("#B8B8B8"));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_us, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        twitter = getActivity().findViewById(R.id.twitter);
        instagram = getActivity().findViewById(R.id.instagram);
        email = getActivity().findViewById(R.id.gmail);
        github = getActivity().findViewById(R.id.github);
        website = getActivity().findViewById(R.id.website);

        SocialMedia();

    }

    public void SocialMedia(){
        twitter.setOnClickListener(v -> {

            Intent intent = null;
            try {
                // get the Twitter app if possible
                getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=Abiri_Reza"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } catch (Exception e) {
                // no Twitter app, revert to browser
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Abiri_Reza"));
            }
            this.startActivity(intent);

        });

        instagram.setOnClickListener(v -> {

            Uri uri = Uri.parse("http://instagram.com/_u/pr.rezaabiri");
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/pr.rezaabiri")));
            }

        });

        email.setOnClickListener(v -> {

            try {
                Intent email= new Intent(Intent.ACTION_SENDTO);
                email.setData(Uri.parse("mailto:pr.rezaabiri@gmail.com"));
                startActivity(email);
            }catch (Exception e){
                Toaster.ToastError(getContext(),R.string.google_service);
            }



        });

        github.setOnClickListener(v -> {

           try {
               String url = "https://github.com/rezaabiri";
               Intent i = new Intent(Intent.ACTION_VIEW);
               i.setData(Uri.parse(url));
               startActivity(i);
           }catch (Exception e){return;}

        });

        website.setOnClickListener(v -> {
            try {
                String url = "https://cenlearn.ir";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }catch (Exception e){return;}

        });
    }
}
