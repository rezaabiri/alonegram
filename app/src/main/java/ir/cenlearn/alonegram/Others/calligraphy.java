package ir.cenlearn.alonegram.Others;

import android.app.Application;

import ir.cenlearn.alonegram.R;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class calligraphy extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()

                                .setDefaultFontPath("fonts/font.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }

}
