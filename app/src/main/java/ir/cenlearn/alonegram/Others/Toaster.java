package ir.cenlearn.alonegram.Others;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ir.cenlearn.alonegram.R;

public class Toaster {

    public Context context;
    private static float fontSize;
    private static float iconSize;

    public Toaster(Context context){
        this.context=context;
    }
    public static void ToastError(Context context, int text){
        LayoutInflater inflater = LayoutInflater.from(context);

        View layout = inflater.inflate(R.layout.layout_custom_toast_error, null,false);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/font.ttf");
        TextView textToast = layout.findViewById(R.id.toastTextError);
        textToast.setTypeface(typeface);
        textToast.setText(text);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 400);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

    }

    public static void ToastSuccess(Context context, int text){
        LayoutInflater inflater = LayoutInflater.from(context);

        View layout = inflater.inflate(R.layout.layout_custom_toast_success, null,false);
        TextView textToast = layout.findViewById(R.id.toastTextSuccess);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/font.ttf");
        textToast.setTypeface(typeface);
        textToast.setText(text);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 400);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static class Builder {
        private Toaster toaster;
        public Builder(Context context){
            toaster = new Toaster(context);
        }

        public Builder setFontSize (float fontSize){
            toaster.fontSize =fontSize;
            return this;
        }
        public Toaster build(){
            return toaster;
        }

    }

    public static void ToastErrorNormal(Context context, String text){
        LayoutInflater inflater = LayoutInflater.from(context);

        View layout = inflater.inflate(R.layout.layout_custom_toast_error, null,false);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/font.ttf");
        TextView textToast = layout.findViewById(R.id.toastTextError);
        textToast.setTypeface(typeface);
        textToast.setText(text);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 400);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void ToastSuccessNormal(Context context, String text){
        LayoutInflater inflater = LayoutInflater.from(context);

        View layout = inflater.inflate(R.layout.layout_custom_toast_success, null,false);
        TextView textToast = layout.findViewById(R.id.toastTextSuccess);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/font.ttf");
        textToast.setTypeface(typeface);
        textToast.setText(text);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 400);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void ToastDownloadNormal(Context context, String text){
        LayoutInflater inflater = LayoutInflater.from(context);

        View layout = inflater.inflate(R.layout.layout_custom_toast_download, null,false);
        TextView textToast = layout.findViewById(R.id.toastTextDownload);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/font.ttf");
        textToast.setTypeface(typeface);
        textToast.setText(text);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 400);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
