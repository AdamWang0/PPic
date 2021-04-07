package com.example.administrator.opencvdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FilterActivity extends AppCompatActivity {

    private Bitmap origin;
    private ImageView im;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri uri;
    private double max_size = 1024;
    private Bitmap selectbp;

    private Button reset;
    private Button save;
    private Button threshold;
    private Button outline;
    private Button nostalgia;
    private Button relievo;

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
        setContentView(R.layout.activity_filter);
        iniLoadOpenCV();

        im = findViewById(R.id.lemon2);
        origin = BitmapFactory.decodeResource(this.getResources(),R.drawable.shanghai);
        im.setScaleType(ImageView.ScaleType.FIT_CENTER);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        reset = findViewById(R.id.reset);
        save = findViewById(R.id.save);
        threshold = findViewById(R.id.Threshold);
        outline = findViewById(R.id.Outline);
        nostalgia = findViewById(R.id.Nostalgia);
        relievo = findViewById(R.id.Relievo);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                im.setImageBitmap(origin);
            }
        });
        threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = threshold(origin);
                im.setImageBitmap(bitmap);
            }
        });
        outline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = outline(origin);
                im.setImageBitmap(bitmap);
            }
        });
        nostalgia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = nostalgia(origin);
                im.setImageBitmap(bitmap);
            }
        });
        relievo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = relievo(origin);
                im.setImageBitmap(bitmap);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePic();
            }
        });
    }

    //二值化滤镜
    Bitmap threshold(Bitmap photo){
        Mat mat = new Mat();
        Bitmap thes = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.bitmapToMat(photo, mat);
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);
        Core.bitwise_not(mat,mat);
        Imgproc.threshold(mat,mat,130,255,Imgproc.THRESH_BINARY_INV);
        Utils.matToBitmap(mat,thes);
        return thes;
    }

    //轮廓
    Bitmap outline(Bitmap photo){
        Mat mat = new Mat();
        Mat Cmat = new Mat();
        Mat Bmat = new Mat();
        Bitmap cartton = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.bitmapToMat(photo, mat);
        Imgproc.Canny(mat,Cmat,40,80);
        Core.bitwise_not(Cmat,Cmat);
        Utils.matToBitmap(Cmat, cartton);
        return cartton;
    }

    //怀旧色滤镜
    Bitmap nostalgia(Bitmap photo){
        Bitmap nostalgia = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        for(int i = 0;i<photo.getWidth();i++){
            for( int j = 0;j<photo.getHeight();j++){
                int A = photo.getPixel(i,j);
                int AR =(int)(0.393*Color.red(A) + 0.769*Color.green(A) + 0.189*Color.blue(A));
                int AG =(int)(0.349*Color.red(A) + 0.686*Color.green(A) + 0.168*Color.blue(A));
                int AB =(int)(0.272*Color.red(A) + 0.534*Color.green(A) + 0.131*Color.blue(A));
                AR = AR > 255 ? 255 : AR;
                AG = AG > 255 ? 255 : AG;
                AB = AB > 255 ? 255 : AB;
                nostalgia.setPixel(i,j,Color.rgb(AR,AG,AB));
            }
        }
        return nostalgia;
    }

    //浮雕滤镜
    Bitmap relievo(Bitmap photo){
        Bitmap relievo  = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        for(int i = 1;i<photo.getWidth()-1;i++){
            for( int j = 1;j<photo.getHeight()-1;j++){
                int A = photo.getPixel(i-1,j-1);
                int B = photo.getPixel(i+1,j+1);
                int AR =Color.red(B)-Color.red(A)+128;
                int AG =Color.green(B)-Color.green(A)+128;
                int AB =Color.blue(B)-Color.blue(A)+128;
                AR = AR > 255 ? 255 : AR;
                AG = AG > 255 ? 255 : AG;
                AB = AB > 255 ? 255 : AB;
                relievo.setPixel(i,j,Color.rgb(AR,AG,AB));
            }
        }
        return relievo;
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
            MediaStore.Images.Media.insertImage(FilterActivity.this.getContentResolver(),
                    file.getAbsolutePath(),fileName,null);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        //最后通知图库更新
        FilterActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://"+"")));
        Toast.makeText(FilterActivity.this,"照片保存至:" + file,Toast.LENGTH_LONG).show();
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