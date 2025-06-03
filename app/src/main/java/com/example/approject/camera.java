package com.example.approject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.imagecapture.JpegBytes2Disk;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.common.util.concurrent.ListenableFuture;

//import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.OutputStream;

import androidx.annotation.NonNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link camera#newInstance} factory method to
 * create an instance of this fragment.
 */
public class camera extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // CameraX관련 변수 선언
    private PreviewView j_prv_cameraPreview;
    private Button j_btn_capture;
    private ImageCapture j_imageCapture;
    private ImageView j_capturedPhoto;

    public camera() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment camera.
     */
    // TODO: Rename and change types and number of parameters
    public static camera newInstance(String param1, String param2) {
        camera fragment = new camera();
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
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        j_prv_cameraPreview = view.findViewById(R.id.prv_cameraView);
        j_btn_capture = view.findViewById(R.id.btn_cameraCapture);
        j_capturedPhoto = view.findViewById(R.id.imgv_capturedPhoto);


        // 카메라 권환 확인하기
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, 1000);
        }

        // 카메라 실행하기
        startCamera();

        // 촬영 버튼 누르면 사진 촬영하기
        j_btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(j_btn_capture.getText().toString().equals("촬영"))
                {
                    takePicture();
                }
                else
                {
                    j_btn_capture.setText("촬영");
                    j_prv_cameraPreview.setVisibility(VISIBLE);
                    j_capturedPhoto.setVisibility(GONE);
                }
            }
        });
        return view;
    }

    private void startCamera()
    {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() ->
        {
            try
            {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                /* Preview : 카메라 데이터를 앱에 전달
                   PreviewView : 전달받은 영상을 실제 화면에 보여주는 역할

                   Preview와 PreviewView 를 연결해서 화면에 보여주도록 하는 코드
                 */
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(j_prv_cameraPreview.getSurfaceProvider());

                // 이미지 캡쳐 Usecase 생성
                j_imageCapture = new ImageCapture.Builder().build();

                // 기본 후면카메라 사용하기
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // 기존 연결 해제하고 새로 연결하기
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(), cameraSelector, preview, j_imageCapture);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    // TODO:사진촬영시, 바로 저장되게 하지 말고 화면에 보여주고, 저장 버튼을 누르면 저장하게 하기
    private void takePicture() {
        // 1. ContentValues 선언
        ContentValues values = new ContentValues();
        String filename = "IMG_" + System.currentTimeMillis() + ".jpg";
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        // 2. OutputFileOptions 생성
        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(
                        requireContext().getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                ).build();

        // 3. takePicture 호출
        j_imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = outputFileResults.getSavedUri();
                        j_capturedPhoto.setImageURI(savedUri);
                        j_prv_cameraPreview.setVisibility(View.GONE);
                        j_capturedPhoto.setVisibility(View.VISIBLE);
                        j_btn_capture.setText("다시 찍기");
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        // 에러 처리
                    }
                }
        );
    }

    /*
    private void takepicture()
    {
        // 저장할 파일 생성
        File photoFile = new File(requireContext().getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");

        // Output 옵션 생성
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        // 사진 촬영하기
        j_imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.@NonNull OutputFileResults outputFileResults) {
                        // 사진을 bitmap에 저장하기
                        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        // 사진 저장하기
                        ContentValues values = new ContentValues();
                        String filename = "IMG_" + System.currentTimeMillis() + ".jpy"
                        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                        Uri uri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        try (OutputStream out = requireContext().getContentResolver().openOutputStream(uri)) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG,100, out);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        j_capturedPhoto.setImageBitmap(bitmap);
                        j_cameraPreview.setVisibility(GONE);
                        j_capturedPhoto.setVisibility(VISIBLE);
                        j_btn_capture.setText("다시 찍기");
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {

                    }
                });
    }*/

}