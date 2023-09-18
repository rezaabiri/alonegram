package ir.cenlearn.alonegram.Others;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import ir.cenlearn.alonegram.R;

public class ProgressCustom {

    AlertDialog dialogBuilder;

    public void show(Context context){

        dialogBuilder = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.layout_progressbar_custom, null);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        dialogBuilder.setCancelable(false);
    }
    public void dismiss(){
        dialogBuilder.dismiss();
    }
}
