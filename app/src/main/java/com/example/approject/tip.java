package com.example.approject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class tip extends Fragment {

    private ScrollView tipScroll;
    private TextView tg1, tg2, tg3, tg4, tg5, tg6;

    public tip() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tip, container, false);

        // ScrollView
        tipScroll = view.findViewById(R.id.tip_scroll);

        // 타겟 텍스트뷰
        tg1 = view.findViewById(R.id.tg1);
        tg2 = view.findViewById(R.id.tg2);
        tg3 = view.findViewById(R.id.tg3);
        tg4 = view.findViewById(R.id.tg4);
        tg5 = view.findViewById(R.id.tg5);
        tg6 = view.findViewById(R.id.tg6);

        // 버튼 리스너 설정
        setScrollButton(view, R.id.btn1, tg1);
        setScrollButton(view, R.id.btn2, tg2);
        setScrollButton(view, R.id.btn3, tg3);
        setScrollButton(view, R.id.btn4, tg4);
        setScrollButton(view, R.id.btn5, tg5);
        setScrollButton(view, R.id.btn6, tg6);

        return view;
    }

    private void setScrollButton(View parent, int buttonId, final TextView target) {
        Button button = parent.findViewById(buttonId);
        button.setOnClickListener(v -> {
            tipScroll.post(() -> {
                // ScrollView가 해당 TextView 위치로 스크롤
                tipScroll.smoothScrollTo(0, target.getTop());
            });
        });
    }
}
