package ir.cenlearn.alonegram.fragments;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import ir.cenlearn.alonegram.R;
import ir.cenlearn.alonegram.Others.Urls;

import org.json.JSONArray;

import static android.content.Context.MODE_PRIVATE;

public class DeleteAccountFragment extends Fragment {

    Button btn_delete;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_delete_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AndroidNetworking.initialize(getContext());

        DeleteAccount();


        //btn_delete = getActivity().findViewById(R.id.btn_delete);

    }


    public void DeleteAccount(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_delete_account, null);

        btn_delete = dialogView.findViewById(R.id.btn_delete);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences("prefs",MODE_PRIVATE);

                AndroidNetworking.upload(Urls.host+Urls.delete_account_api)
                        .addMultipartParameter("username", preferences.getString("username", ""))
                        .setTag("UPLOAD")
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
}