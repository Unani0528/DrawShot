package com.example.approject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

//import org.jspecify.annotations.NonNull;

import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

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
    private Button j_btn_capture, j_btn_changecamera, j_btn_savePhoto, j_btn_showGalery;
    private ImageCapture j_imageCapture;
    private ImageView j_capturedPhoto;
    private int cameramode = 0;

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
        j_btn_changecamera = view.findViewById(R.id.btn_changeCamera);
        j_btn_savePhoto = view.findViewById(R.id.btn_savePhoto);
        j_btn_showGalery = view.findViewById(R.id.btn_showGallery);


        // 카메라 전환 전면, 후면
        j_btn_changecamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameramode == 0) {
                    cameramode = 1;
                    startCamera();
                } else {
                    cameramode = 0;
                    startCamera();
                }
            }
        });

        // 카메라 권환 확인하기
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, 1000);
        }

        // 카메라 실행하기
        startCamera();

        // 사진 촬영하기
        j_btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (j_btn_capture.getText().toString().equals("촬영")) {
                    takePicture();
                } else {
                    j_btn_changecamera.setVisibility(VISIBLE);
                    j_btn_savePhoto.setVisibility(GONE);
                    j_btn_capture.setText("촬영");
                    j_prv_cameraPreview.setVisibility(VISIBLE);
                    j_capturedPhoto.setVisibility(GONE);
                    j_btn_savePhoto.setVisibility(GONE);
                }
            }
        });
        // 사진 저장하기
        j_btn_savePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePicture();
            }
        });

        j_btn_showGalery.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivity(intent);
            }
        });
        return view;
    }

    private void startCamera()
    {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        // cameraProviderFuture ProcesCameraProvider가 실행 가능한지 확인하는것
        cameraProviderFuture.addListener(() ->
        {
            try
            {
                // cameraProviderFuture는 미래에 카메라를 쓸 수 있는지 확인하는 비동기식 객체이다.
                // cameraProviderFuture.get()은 카메라가 사용이 가능해진다면(사용할 준비가 끝나면) 알려주는동작이다.
                // 이 코드는 cameraProviderFuture라는 객체를 통해 카메라가 사용가능한 준비가 끝나면 알려주고 그 뒤 cameraProvider 객체를 생성하는 것이다.
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // 카메라를 화면에 보여주기 위해서 Preview 객체를 생성한다.
                // Preview 객체는 카메라 데이터를 앱에 전달하는 역할을 한다.
                Preview cameraPreview = new Preview.Builder().build();

                // 카메라를 어디에 보여줄지 설정한다.
                //j_prv_cameraPreview 에서 cameraPreview를 받아서 보여준다.
                cameraPreview.setSurfaceProvider(j_prv_cameraPreview.getSurfaceProvider());

                j_imageCapture = new ImageCapture.Builder().build();
                CameraSelector cameraSelector;
                if (cameramode == 0)
                {
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                }
                else
                {
                    cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                }

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, cameraPreview, j_imageCapture);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }
    public Bitmap rotateBitmap(File file)
    {
        if(file.exists())
        {
            //비트맵 불러오기
            Bitmap tempPhotoBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            // 비트맵 회전정보 가져오기
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(file.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotate = 0;
                switch (orientation)
                {
                    case ExifInterface.ORIENTATION_ROTATE_90: rotate = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180: rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270: rotate = 270;
                        break;
                    default: rotate = 0;
                }

                if (rotate != 0)
                {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotate);
                    tempPhotoBitmap = Bitmap.createBitmap(tempPhotoBitmap, 0, 0, tempPhotoBitmap.getWidth(), tempPhotoBitmap.getHeight(), matrix, true);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
                return(tempPhotoBitmap);
        }
        else return null;
    }
    private void takePicture()
    {
        File tempPhotoFile = new File(requireContext().getCacheDir(), "temp_Photo.jpg");
        ImageCapture.OutputFileOptions tempOutputOptions = new ImageCapture.OutputFileOptions.Builder(tempPhotoFile).build();

        j_imageCapture.takePicture(tempOutputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.@NonNull OutputFileResults outputFileResults)
                    {
                            j_capturedPhoto.setImageBitmap(rotateBitmap(tempPhotoFile));
                            j_prv_cameraPreview.setVisibility(GONE);
                            j_btn_changecamera.setVisibility(GONE);
                            j_capturedPhoto.setVisibility(VISIBLE);
                            j_btn_capture.setText("다시 찍기");
                            j_btn_savePhoto.setVisibility(VISIBLE);
                        }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {

                    }
                });
    }
    private void savePicture()
    {
        File cachePhoto = new File(requireContext().getCacheDir(), "temp_Photo.jpg");
        if(!cachePhoto.exists()) return;

        // Mediastore에 저장할 이미지의 정보를 저장할 contentValues객체를 생성한다.
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "img/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/Camera");

        Uri uri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if(uri == null) return;

        try (OutputStream out = requireContext().getContentResolver().openOutputStream(uri))
        {
            Bitmap bitmap = rotateBitmap(cachePhoto);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            Toast.makeText(requireContext(), "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cachePhoto.exists()) cachePhoto.delete();
        

    }

}