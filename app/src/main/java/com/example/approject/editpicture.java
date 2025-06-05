package com.example.approject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import java.io.IOException;

public class editpicture extends Fragment {

    // 구성 요소
    private ImageView player; // 움직이는 캐릭터
    private JoystickView joystickView; // 조이스틱
    private DrawingView drawingView; // 그림 그리기 뷰
    private ImageButton drawButton; // 그리기 버튼
    private Button colorButton, saveButton; // 색상/저장 버튼
    private SeekBar thicknessSeekBar; // 굵기 조절

    // 이동 및 그리기
    private float dx = 0, dy = 0;
    private boolean isDrawing = false;
    private int currentColor = Color.BLACK;

    // 애니메이션 및 반복 이동
    private AnimationDrawable frameAnimation;
    private final Handler handler = new Handler();
    private final int MOVE_INTERVAL = 16; // 약 60fps

    // 센서 관련 (흔들기 감지)
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY, lastZ;
    private long lastShakeTime = 0;

    private Button j_btn_selectpicture;
    private Bitmap galleryBitmap = null;
    private ActivityResultLauncher<String> galleryLauncher;

    // 주기적으로 플레이어 이동
    private final Runnable moveRunnable = new Runnable() {
        @Override
        public void run() {
            movePlayer();
            handler.postDelayed(this, MOVE_INTERVAL);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_editpicture, container, false);


        // 뷰 초기화
        player = root.findViewById(R.id.dudu);
        joystickView = root.findViewById(R.id.joystickView);
        drawingView = root.findViewById(R.id.drawingView);
        drawButton = root.findViewById(R.id.draw_btn);
        colorButton = root.findViewById(R.id.color_btn);
        saveButton = root.findViewById(R.id.save_btn);
        thicknessSeekBar = root.findViewById(R.id.thickness_SeekBar);
        j_btn_selectpicture = root.findViewById(R.id.picture_btn);
        j_btn_selectpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
            }
        });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                                galleryBitmap = bitmap;
                                drawingView.setBackgroundImage(galleryBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

        );

        // SharedPreferences에서 선택된 캐릭터 불러오기
        SharedPreferences prefs = requireContext().getSharedPreferences("CharacterPrefs", Context.MODE_PRIVATE);
        String selectedCharacter = prefs.getString("selected_character", "du");

        // 선택된 캐릭터에 따라 이미지 설정
        if (selectedCharacter.equals("minion")) {
            player.setImageResource(R.drawable.minions);
        } else if(selectedCharacter.equals("du")){
            player.setImageResource(R.drawable.dudu);
        } else {
            player.setImageResource(R.drawable.point);
        }

        // 조이스틱 움직임 설정
        joystickView.setJoystickListener((xPercent, yPercent) -> {
            dx = xPercent * 10 * Global.sen;
            dy = yPercent * 10 * Global.sen;
        });

        handler.post(moveRunnable); // 반복 이동 시작

        // 그리기 버튼 터치 시 애니메이션 + 경로 시작
        if(selectedCharacter.equals("minion")||selectedCharacter.equals("du")) {
            drawButton.setOnTouchListener((v, event) -> {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isDrawing = true;

                        if (selectedCharacter.equals("minion")) {
                            player.setBackgroundResource(R.drawable.minions_drawing_animation);
                        } else {
                            player.setBackgroundResource(R.drawable.du_drawing_animation);
                        }

                        frameAnimation = (AnimationDrawable) player.getBackground();
                        frameAnimation.start();
                        player.setImageResource(0); // static 이미지 제거

                        drawingView.startNewPath(
                                player.getX() + player.getWidth() / 2f,
                                player.getY() + player.getHeight() / 2f
                        );
                        return true;

                    case MotionEvent.ACTION_UP:
                        isDrawing = false;
                        if (frameAnimation != null) frameAnimation.stop();
                        player.setBackgroundResource(0);

                        // 캐릭터별 정적 이미지 복원
                        if (selectedCharacter.equals("minion")) {
                            player.setImageResource(R.drawable.minions);
                        } else {
                            player.setImageResource(R.drawable.dudu);
                        }
                        return true;
                }
                return false;
            });
        } else {
            drawButton.setOnTouchListener((v, event) -> {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isDrawing = true;
                        drawingView.startNewPath(
                                player.getX() + player.getWidth() / 2f,
                                player.getY() + player.getHeight() / 2f
                        );
                        return true;

                    case MotionEvent.ACTION_UP:
                        isDrawing = false;
                        return true;
                }
                return false;
            });
        }

        saveButton.setOnClickListener(v -> {
            Bitmap drawingBitmap = drawingView.exportToBitmap();
            if (drawingBitmap != null) {
                String savedImageURL = MediaStore.Images.Media.insertImage(
                        requireActivity().getContentResolver(),
                        drawingBitmap,
                        "그림_" + System.currentTimeMillis(),
                        "그림 저장"
                );
                if (savedImageURL != null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("저장 완료")
                            .setMessage("사진이 갤러리에 저장되었습니다.")
                            .setPositiveButton("확인", null)
                            .show();
                }
            }
        });


        // 색상 선택 버튼
        colorButton.setOnClickListener(v -> showColorPicker());

        // 굵기 조절 SeekBar
        thicknessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawingView.setStrokeWidth(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        drawingView.setStrokeWidth(thicknessSeekBar.getProgress());

        // 저장 버튼
        saveButton.setOnClickListener(v -> {
            Bitmap combinedBitmap = drawingView.exportToBitmap();
            if (combinedBitmap != null) {
                String savedImageURL = MediaStore.Images.Media.insertImage(
                        requireActivity().getContentResolver(),
                        combinedBitmap,
                        "그림_" + System.currentTimeMillis(),
                        "그림 저장"
                );
                if (savedImageURL != null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("저장 완료")
                            .setMessage("사진이 갤러리에 저장되었습니다.")
                            .setPositiveButton("확인", null)
                            .show();
                }
            }
        });



        // 센서 등록 (흔들면 clear)
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        return root;
    }


    // 플레이어 움직임 + 그리기 좌표 추가
    private void movePlayer() {
        if (player == null) return;

        float x = player.getX() + dx;
        float y = player.getY() + dy;

        View parent = (View) player.getParent();
        int maxX = parent.getWidth() - player.getWidth();
        int maxY = parent.getHeight() - player.getHeight();

        // 범위 제한
        x = Math.max(0, Math.min(x, maxX));
        y = Math.max(0, Math.min(y, maxY));

        player.setX(x);
        player.setY(y);

        if (isDrawing) {
            drawingView.addPoint(
                    x + player.getWidth() / 2f,
                    y + player.getHeight() / 2f
            );
        }
    }

    // 색상 선택 다이얼로그
    private void showColorPicker() {
        final String[] colors = {"검정", "빨강", "초록", "파랑", "노랑"};
        final int[] colorValues = { Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW };

        new AlertDialog.Builder(getContext())
                .setTitle("색상 선택")
                .setItems(colors, (dialog, which) -> {
                    currentColor = colorValues[which];
                    colorButton.setBackgroundTintList(ColorStateList.valueOf(currentColor));
                    drawingView.setPaintColor(currentColor);
                }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 센서 리스너 등록
        if (accelerometer != null) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 센서 리스너 해제
        if (accelerometer != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    // 흔들기 감지 리스너
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float deltaX = Math.abs(lastX - x);
            float deltaY = Math.abs(lastY - y);
            float deltaZ = Math.abs(lastZ - z);

            if ((deltaX > 10 || deltaY > 10 || deltaZ > 10)) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastShakeTime > 1000) {
                    lastShakeTime = currentTime;
                    drawingView.clear();  // 흔들면 그림 초기화
                }
            }

            lastX = x;
            lastY = y;
            lastZ = z;
        }

        @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };
}