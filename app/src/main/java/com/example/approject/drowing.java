package com.example.approject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

public class drowing extends Fragment {

    // 구성 요소
    private ImageView player; // 움직이는 캐릭터
    private JoystickView joystickView; // 조이스틱
    private DrawingView drawingView; // 그림 그리기 뷰
    private ImageButton drawButton; // 그리기 버튼
    private Button colorButton, saveButton; // 색상/저장 버튼
    private SeekBar thicknessSeekBar; // 굵기 조절
    private Switch backgroundSwitch; // 배경 투명 여부
    private float sen = 1.0f;

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

    // 주기적으로 플레이어 이동
    private final Runnable moveRunnable = new Runnable() {
        @Override
        public void run() {
            movePlayer();
            handler.postDelayed(this, MOVE_INTERVAL);
        }
    };

    public drowing() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_drowing, container, false);

        // 뷰 초기화
        player = root.findViewById(R.id.player);
        joystickView = root.findViewById(R.id.joystickView);
        drawingView = root.findViewById(R.id.drawingView);
        drawButton = root.findViewById(R.id.drawButton);
        colorButton = root.findViewById(R.id.colorButton);
        saveButton = root.findViewById(R.id.saveButton);
        thicknessSeekBar = root.findViewById(R.id.thicknessSeekBar);
        backgroundSwitch = root.findViewById(R.id.backgroundSwitch);

        // 조이스틱 움직임 설정
        joystickView.setJoystickListener((xPercent, yPercent) -> {
            dx = xPercent * 10 * sen;
            dy = yPercent * 10 * sen;
        });

        handler.post(moveRunnable); // 반복 이동 시작

        // 그리기 버튼 터치 시 애니메이션 + 경로 시작
        drawButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isDrawing = true;
                    player.setBackgroundResource(R.drawable.du_drawing_animation); // 애니메이션 시작
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
                    player.setImageResource(R.drawable.dudu); // static 이미지 복구
                    return true;
            }
            return false;
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
            boolean isTransparent = backgroundSwitch.isChecked();
            drawingView.saveToGallery(getContext(), isTransparent);
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