// buttonsetting.java
package com.example.approject;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class buttonsetting extends Fragment {

    public buttonsetting() {
        // Required empty public constructor
    }

    public static buttonsetting newInstance(String param1, String param2) {
        buttonsetting fragment = new buttonsetting();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buttonsetting, container, false);

        SeekBar seekBar = view.findViewById(R.id.Sensitivity_seekBar);
        TextView sensitivityValue = view.findViewById(R.id.sensitivity_value);

        // 최대/초기값 설정
        seekBar.setMax(20);
        seekBar.setProgress((int) (Global.sen * 10));

        // ✅ TextView도 Global.sen으로 초기화
        sensitivityValue.setText(String.format("%.1f", Global.sen));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Global.sen = progress / 10.0f;
                sensitivityValue.setText(String.format("%.1f", Global.sen));
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return view;
    }

}
