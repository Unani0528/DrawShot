package com.example.approject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link voice#newInstance} factory method to
 * create an instance of this fragment.
 */
public class voice extends Fragment {
    static int kimchicheck = 0;
    static int cheesecheck = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public voice() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment voice.
     */
    // TODO: Rename and change types and number of parameters
    public static voice newInstance(String param1, String param2) {
        voice fragment = new voice();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voice, container, false);

        Switch j_voice_kimchi = view.findViewById(R.id.sw_voice_kimchi);
        Switch j_voice_cheeze = view.findViewById(R.id.sw_voice_cheeze);
        if(kimchicheck != 0) j_voice_kimchi.setChecked(true);
        if(cheesecheck != 0) j_voice_cheeze.setChecked(true);
        // 김치 버튼 누르면 김치 추가 제거
        j_voice_kimchi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
            {
                if(!voicekeywords.keywordList.contains(("김치"))){
                    kimchicheck = 1;
                    voicekeywords.keywordList.add("김치");
                    Toast.makeText(getContext(),"음성인식 키워드에 '김치'가 추가됩니다.", Toast.LENGTH_SHORT).show();
                }

            }else if(voicekeywords.keywordList.contains("김치")){
                kimchicheck = 0;
                voicekeywords.keywordList.remove("김치");
                Toast.makeText(getContext(),"음성인식 키워드에 '김치'가 제거됩니다.", Toast.LENGTH_SHORT).show();
            }
            }
        });
        // 치즈 버튼 누르면 치즈 추가 제거
        j_voice_cheeze.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(!voicekeywords.keywordList.contains(("치즈"))){
                        cheesecheck = 1;
                        voicekeywords.keywordList.add("치즈");
                        Toast.makeText(getContext(),"음성인식 키워드에 '치즈'가 추가됩니다.", Toast.LENGTH_SHORT).show();
                    }

                }else if(voicekeywords.keywordList.contains("치즈")){
                    cheesecheck = 0;
                    voicekeywords.keywordList.remove("치즈");
                    Toast.makeText(getContext(),"음성인식 키워드에 '김치'가 제거됩니다.", Toast.LENGTH_SHORT).show();
                    }
                }

        });
        return view;
    }
}