package com.example.approject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class DrawingView extends View {
    private Paint paint;            //붓 역할
    private Path currentPath;       //현재 선의 경로
    private ArrayList<Path> paths = new ArrayList<>();      //지금까지 그린 각각의 선들 저장 리스트
    private ArrayList<Paint> paints = new ArrayList<>();        //그려진 선들의 붓 스타일 리스트

    private Bitmap backgroundBitmap;  // 배경 이미지



    public DrawingView(Context context, AttributeSet attrs) {       //커스텀 뷰 사용시 있어야하는 생성자
        super(context, attrs);
        init();  // 초기 설정
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);         // 선 그리기
        paint.setStrokeJoin(Paint.Join.ROUND);      // 선 연결
        paint.setStrokeCap(Paint.Cap.ROUND);        // 선 끝
        paint.setAntiAlias(true);
    }

    public void startNewPath(float x, float y) {
        currentPath = new Path();
        currentPath.moveTo(x, y);           //시작점
        paths.add(currentPath);             //선 경로 저장

        Paint newPaint = new Paint(paint);  // 현재 붓 상태 복사
        paints.add(newPaint);               //후 상태 저장
        invalidate();                       //초기화
    }

    public void addPoint(float x, float y) {
        if (currentPath != null) {
            currentPath.lineTo(x, y);       //선 이어서 그리기
            invalidate();
        }
    }

    public void setPaintColor(int color) {
        paint.setColor(color);
    }

    public void setStrokeWidth(float width) {
        paint.setStrokeWidth(width);
    }

    public void clear() {
        paths.clear();
        paints.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 배경 이미지가 있으면 먼저 그림
        if (backgroundBitmap != null) {
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        }

        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i), paints.get(i));       //그린 선들과 그에 맞는 붓 상태로 그림
        }
    }

    public void saveToGallery(Context context, boolean transparent) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);  //비트맵 이미지 만들기(도화지 역할)
        Canvas canvas = new Canvas(bitmap);

        // 배경 처리
        if (!transparent) {                         //그림 그리기 화면의 스위치의 필요기능(투명배경 할지말지)
            canvas.drawColor(Color.WHITE);
        } else {
            canvas.drawARGB(0, 0, 0, 0); //투명 배경
        }

        // 배경 이미지 있다면 같이 그림
        if (backgroundBitmap != null) {
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        }

        // 지금까지 그려진 그림을 그린다
        draw(canvas);

        // 저장 경로 설정(MyDrawings 폴더에 넣을거)
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyDrawings");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = "drawing_" + System.currentTimeMillis() + ".png";
        File file = new File(directory, filename);

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(context, "저장 완료: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap exportToBitmap() {
        if (getWidth() == 0 || getHeight() == 0) return null;

        Bitmap resultBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);

        // 배경 그리기
        if (backgroundBitmap != null) {
            // 배경 이미지를 뷰 크기에 맞게 스케일해서 그릴 수도 있음 (필요시)
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        }

        // 그림 그리기
        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i), paints.get(i));
        }

        return resultBitmap;
    }


    // 배경 이미지 설정
    public void setBackgroundImage(Bitmap bitmap) {
        backgroundBitmap = bitmap;
        invalidate();
    }
}
