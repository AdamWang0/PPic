package com.example.administrator.opencvdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ToGrayActivity extends AppCompatActivity {

    private Bitmap origin;
    private ImageView im;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri uri;
    private double max_size = 1024;
    private Bitmap selectbp;
    private ImageButton gray;
    private ImageButton save;

    private void iniLoadOpenCV(){
        boolean success = OpenCVLoader.initDebug();
        if(success){
            Log.i("CV_TAG","OpenCV Libraries loaded...");
        }
        else{
            Toast.makeText(this.getApplicationContext(),
                    "WARNING:could not load OpenCV Libraries!",
                    Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_gray);
        iniLoadOpenCV();
        im = findViewById(R.id.lemon1);
        origin = BitmapFactory.decodeResource(this.getResources(),R.drawable.kangong);
        //origin = ((BitmapDrawable)im.getDrawable()).getBitmap();
        im.setScaleType(ImageView.ScaleType.FIT_CENTER);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        gray = findViewById(R.id.toGray);
        save = findViewById(R.id.save);
        gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gray();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePic();
            }
        });
    }

    public void Gray(){
        Bitmap bitmap = ((BitmapDrawable)im.getDrawable()).getBitmap();
        //Bitmap bitmap = origin;
        Mat src = new Mat();
        Mat des = new Mat();
        Utils.bitmapToMat(bitmap,src);
        Imgproc.cvtColor(src,des,Imgproc.COLOR_BGR2GRAY);
        Utils.matToBitmap(des,bitmap);
        im.setImageBitmap(bitmap);
        src.release();
        des.release();
    }

    private void savePic(){
        Bitmap bitmap = ((BitmapDrawable)im.getDrawable()).getBitmap();
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
            MediaStore.Images.Media.insertImage(ToGrayActivity.this.getContentResolver(),
                    file.getAbsolutePath(),fileName,null);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        //最后通知图库更新
        ToGrayActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://"+"")));
        Toast.makeText(ToGrayActivity.this,"照片保存至:" + file,Toast.LENGTH_LONG).show();
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
            im.setImageBitmap(selectbp);
            origin = selectbp;
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"选择图像..."), PICK_IMAGE_REQUEST);
    }

}
