package com.example.approject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {
    // 변수 선언부
    ImageButton j_camera, j_home, j_setting;
    LinearLayout j_bottom_lo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.maindrawer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // 메인 메뉴 맨 위의 메뉴창
        ImageButton j_imgbtnmenu = findViewById(R.id.imgbtn_main_menu);
        DrawerLayout j_drwmainmenu = findViewById(R.id.maindrawer);
        // 메인 메뉴 오른쪽에서 나오게 하기
        j_imgbtnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                j_drwmainmenu.openDrawer(GravityCompat.END);
            }
        });

        // 메인메뉴 하단의 카메라, 홈, 설정 버튼
        j_camera = findViewById(R.id.btn_bottom_camera);
        j_home = findViewById(R.id.btn_bottom_home);
        j_setting = findViewById(R.id.btn_bottom_setting);
        // 메인메뉴 하단 메뉴 레이아웃(버튼 누를때 마다 이미지 바뀌게 만들려고 만듦)
        j_bottom_lo = findViewById(R.id.lo_bottombtns);

        j_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextfragment = new camera();
                getSupportFragmentManager().beginTransaction().replace(R.id.FMV_Common, nextfragment)
                        .addToBackStack(null).commit();
                j_bottom_lo.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.img_bottombtn_camera));
            }
        });
        j_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextfragment = new main();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_r, R.anim.fade_out_r, R.anim.fade_in_r, R.anim.slide_out_r)
                        .replace(R.id.FMV_Common, nextfragment).addToBackStack(null).commit();
                j_bottom_lo.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.img_bottombtn_home));
            }
        });
        j_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextfragment = new setting();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_r, R.anim.fade_out_r, R.anim.fade_in_r, R.anim.slide_out_r)
                        .replace(R.id.FMV_Common, nextfragment).addToBackStack(null).commit();
                j_bottom_lo.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.img_bottombtn_setting));

            }
        });

    }
}