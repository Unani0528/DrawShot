package com.example.approject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class changecharacter extends Fragment {

    private RadioGroup characterRadioGroup;
    private Button btnApply;

    public changecharacter() {
        // Required empty public constructor
    }

    public static changecharacter newInstance(String param1, String param2) {
        changecharacter fragment = new changecharacter();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_changecharacter, container, false);

        characterRadioGroup = view.findViewById(R.id.character_RadioGroup);
        btnApply = view.findViewById(R.id.btnApply);

        // 기존 선택된 캐릭터 표시
        SharedPreferences prefs = requireContext().getSharedPreferences("CharacterPrefs", Context.MODE_PRIVATE);
        String selectedCharacter = prefs.getString("selected_character", "du");

        if (selectedCharacter.equals("minion")) {
            characterRadioGroup.check(R.id.radio_minion);
        } else {
            characterRadioGroup.check(R.id.radio_du);
        }

        btnApply.setOnClickListener(v -> {
            int selectedId = characterRadioGroup.getCheckedRadioButtonId();
            String character = "du"; // 기본값

            if (selectedId == R.id.radio_minion) {
                character = "minion";
            }

            prefs.edit().putString("selected_character", character).apply();
            Toast.makeText(requireContext(), "캐릭터가 저장되었습니다!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
