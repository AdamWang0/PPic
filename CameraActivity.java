package com.example.administrator.opencvdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    private Camera camera;//定义一个摄像头对象 Alt+Enter导入android.hardware.Camera
    private boolean isPreview=false; //是否为预览状态，false表示非预览状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏显示
        if(!android.os.Environment.getExternalStorageState().equals(// 判断手机是否安装SD卡
                Environment.MEDIA_MOUNTED)){
            Toast.makeText(this,"请安装SD卡", Toast.LENGTH_SHORT).show();//提示安装SD卡
        }
        /*************************打开摄像头并预览*****************************/
        SurfaceView surfaceView=findViewById(R.id.surfaceView);//用于显示摄像头预览的
        final SurfaceHolder surfaceHolder=surfaceView.getHolder();//获取SurfaceHolder
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置SurfaceView自己不维护缓冲
        Button preview =findViewById(R.id.preview);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPreview){
                    Toast.makeText(getApplicationContext(),"try to open camera", Toast.LENGTH_SHORT).show();
                    camera=Camera.open();//打开摄像头
                    isPreview=true;//设置为预览状态
                    try {
                        camera.setPreviewDisplay(surfaceHolder);//设置用于显示预览的SurfaceView
                        Camera.Parameters parameters=camera.getParameters();// 获取摄像头参数

                        parameters.setPictureFormat(PixelFormat.JPEG);//设置图片为JPG图片
                        parameters.set("jpeg-quality",80);//设置图片的质量

                        camera.setParameters(parameters);//重新设置摄像头参数
                        camera.startPreview();//开始预览
                        camera.autoFocus(null);//设置自动对焦
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Button takePicture =findViewById(R.id.takephoto);//获取“拍照”按钮
        //实现相机拍照功能
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //实现拍照功能
                if(camera!=null){
                    camera.takePicture(null,null,jpeg);//进行拍照
                }
            }
        });
    }
    final Camera.PictureCallback jpeg=new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap=BitmapFactory.decodeByteArray(data,0,data.length);//根据拍照所得数据创建位图
            camera.stopPreview();//停止预览
            isPreview=false;//设置为非预览状态
            //获取sd卡根目录
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
                MediaStore.Images.Media.insertImage(CameraActivity.this.getContentResolver(),
                        file.getAbsolutePath(),fileName,null);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            //最后通知图库更新
            CameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://"+"")));
            Toast.makeText(CameraActivity.this,"照片保存至:" + file,Toast.LENGTH_LONG).show();
            resetCamera();//重新预览
        }
    };
    private void resetCamera(){
        if(!isPreview) {//如果是非预览状态
            camera.startPreview();//开启预览
            isPreview=true;
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        //停止预览并释放摄像头资源
        if(camera!=null){
            camera.stopPreview();//停止预览
            camera.release();//释放资源
        }
    }
}