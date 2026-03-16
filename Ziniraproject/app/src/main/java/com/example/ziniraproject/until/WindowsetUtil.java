package com.example.ziniraproject.until;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class WindowsetUtil {

    /**
     * Applies insets to the toolbar INTER TO MATERIAL TOOLBAR (MODERN api 30+ )
     * @param materialToolbar
     */
    public static  void applyToolbarInsets(@NonNull MaterialToolbar materialToolbar){
        ViewCompat.setOnApplyWindowInsetsListener(materialToolbar,(v, insets) -> {
            int topInset= insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding
                    (v.getPaddingLeft(),
                            topInset,
                            v.getPaddingRight()
                            ,v.getPaddingBottom()
                    );
            return WindowInsetsCompat.CONSUMED;
        });
    }
    public static void applyToolbarInsets(@NonNull MaterialToolbar materialToolbar,boolean applyTopInset, boolean applyBottomInset) {

        ViewCompat.setOnApplyWindowInsetsListener(materialToolbar, (v, insets) -> {

            int top = applyTopInset?
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top:
            v.getPaddingTop();

            int bottom = applyBottomInset?
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom:
                    v.getPaddingBottom();

            v.setPadding(v.getPaddingLeft(),
                    top,v.getPaddingRight(),bottom
            );

            return   WindowInsetsCompat.CONSUMED;

        });
    }
}
