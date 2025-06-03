package com.example.approject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class main extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment main.
     */
    // TODO: Rename and change types and number of parameters
    public static main newInstance(String param1, String param2) {
        main fragment = new main();
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
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // main프래그먼트 진입시 main버튼 초록색으로 바꾸기
        /*MainActivity mainActivity = (MainActivity) getActivity();
        LinearLayout linearLayout = mainActivity.findViewById(R.id.lo_bottombtns);
        linearLayout.setBackground(ContextCompat.getDrawable(mainActivity,R.drawable.back_in));
*/
        // 사진편집하기 프래그먼트 이동
        ImageButton j_editpic = view.findViewById(R.id.btn_editpicture);
        j_editpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextfragment = new editpicture();

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_r, R.anim.fade_out_r, R.anim.fade_in_r, R.anim.slide_out_r)
                        .replace(R.id.FMV_Common, nextfragment).addToBackStack(null).commit();
            }
        });

        // 그림그리기 프래그먼트 이동
        ImageButton j_drowing = view.findViewById(R.id.btn_drowing);
        j_drowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextfragment = new drowing();

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_r, R.anim.fade_out_r, R.anim.fade_in_r, R.anim.slide_out_r)
                        .replace(R.id.FMV_Common, nextfragment).addToBackStack(null).commit();
            }
        });

        // 배경화면 선택 프래그먼트 이동
        ImageButton j_background = view.findViewById(R.id.btn_backgound);
        j_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextfragment = new background();

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_r, R.anim.fade_out_r, R.anim.fade_in_r, R.anim.slide_out_r)
                        .replace(R.id.FMV_Common, nextfragment).addToBackStack(null).commit();
            }
        });

        // 팁 프래그먼트 이동
        ImageButton j_tip = view.findViewById(R.id.btn_tip);
        j_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextfragment = new tip();

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_r, R.anim.fade_out_r, R.anim.fade_in_r, R.anim.slide_out_r)
                        .replace(R.id.FMV_Common, nextfragment).addToBackStack(null).commit();
            }
        });
        return view;
    }

}