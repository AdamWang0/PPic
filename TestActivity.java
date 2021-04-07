package com.example.administrator.opencvdemo;

import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.os.AsyncTask.Status.RUNNING;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "test";
    private double max_size = 1024;
    private int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Bitmap selectbp;
    private Bitmap origin;
    private Uri uri;
    private BilateralFilterTask bilateralFilterTask;

    private ImageButton save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        staticLoadCVLibraries();
        selectbp = BitmapFactory.decodeResource(this.getResources(),R.drawable.mazi);
        origin = selectbp;
        bilateralFilterTask = new BilateralFilterTask();
        imageView = findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePic();
            }
        });
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (imageView != null&&uri!=null) {
                    readFile();
                    convertGray(seekBar.getProgress() / 100);
                }
                else
                {
                    convertGray(seekBar.getProgress() / 100);
                }
            }
        });
    }


    //OpenCV库静态加载并初始化
    private void staticLoadCVLibraries(){
        boolean load = OpenCVLoader.initDebug();
        if(load) {
            Log.i("CV", "Open CV Libraries loaded...");
        }
    }
    private class BilateralFilterTask extends AsyncTask<Float, Bitmap, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(Float... bilityTraversal) {
            //Bitmap processbp = Bitmap.createBitmap(origin);
            Bitmap processbp = origin;
            if (bilityTraversal[0] > 0) {
// 磨皮美颜算法
                int dx = (int)bilityTraversal[0].floatValue() * 10; // 双边滤波参数之一
                double fc = bilityTraversal[0] * 25; // 双边滤波参数之一
                double p = 0.1f; // 透明度
                Mat image = new Mat(), dst = new Mat(), matBilFilter = new Mat(), matGaussSrc = new Mat(), matGaussDest = new Mat(), matTmpDest = new Mat(), matSubDest = new Mat(), matTmpSrc = new Mat();

                // 双边滤波
                Utils.bitmapToMat(processbp, image);
                Imgproc.cvtColor(image, image, Imgproc.COLOR_BGRA2BGR);
                Imgproc.bilateralFilter(image, matBilFilter, dx, fc, fc);

                Core.subtract(matBilFilter, image, matSubDest);
                Core.add(matSubDest, new Scalar(128, 128, 128, 128), matGaussSrc);
                // 高斯模糊
                Imgproc.GaussianBlur(matGaussSrc, matGaussDest, new Size(2 * bilityTraversal[0] - 1, 2 * bilityTraversal[0] - 1), 0, 0);
                matGaussDest.convertTo(matTmpSrc, matGaussDest.type(), 2, -255);
                Core.add(image, matTmpSrc, matTmpDest);
                Core.addWeighted(image, p, matTmpDest, 1 - p, 0.0, dst);

                Core.add(dst, new Scalar(10, 10, 10), dst);
                Utils.matToBitmap(dst, processbp);

            }

            return processbp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
    private void convertGray(float bilityTraversal) {
        bilateralFilterTask.cancel(true);
        bilateralFilterTask = new BilateralFilterTask();
        bilateralFilterTask.execute(bilityTraversal);
    }

    private void savePic(){
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        File appDir = new File(Environment.getExternalStorageDirectory(),"/DCIM/Camera/");
        if(!appDir.exists()){ //如果该目录不存在
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis()+".jpg";//将获取当前系统时间设置为照片名称
        File file = new File(appDir,fileName);//创建文件对象

        try{
            FileOutputStream fos = new FileOutputStream(file);//创建一个文件输出流对象
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);//将图片内容压缩为JPEG格式
            fos.flush();//将缓冲区中的数据全部写出到输出流中
            fos.close();//关闭文件输出流对象
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        //将照片插入到系统图库
        try{
            MediaStore.Images.Media.insertImage(TestActivity.this.getContentResolver(),
                    file.getAbsolutePath(),fileName,null);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        //最后通知图库更新
        TestActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://"+"")));
        Toast.makeText(TestActivity.this,"照片保存至:" + file,Toast.LENGTH_LONG).show();
    }

    private void readFile() {
        try {
            Log.d("image-tag", "start to decode selected image now...");
            InputStream input = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            int raw_width = options.outWidth;
            int raw_height = options.outHeight;
            int max = Math.max(raw_width, raw_height);
            int newWidth = raw_width;
            int newHeight = raw_height;
            int inSampleSize = 1;
            if(max > max_size) {
                newWidth = raw_width / 2;
                newHeight = raw_height / 2;
                while((newWidth/inSampleSize) > max_size || (newHeight/inSampleSize) > max_size) {
                    inSampleSize *=2;
                }
            }

            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            selectbp = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
            origin = selectbp;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            readFile();
            imageView.setImageBitmap(selectbp);
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"选择图像..."), PICK_IMAGE_REQUEST);
    }
}
