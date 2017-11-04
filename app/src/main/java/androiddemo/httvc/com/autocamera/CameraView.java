package androiddemo.httvc.com.autocamera;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {

    private SurfaceHolder holder;
    private Camera camera;
    private Camera.Parameters parameters;
    private Activity act;
    private Handler handler = new Handler();
    private Context context;
    private SurfaceView surfaceView;
    private AudioManager audio;
    private int current;

    public CameraView(Context context) {
        super(context);

        surfaceView = this;
        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final int current = audio.getRingerMode();
        audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        this.context = context;
        holder = getHolder();// 生成Surface Holder
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 指定Push Buffer

       /* handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (camera == null) {
                    handler.postDelayed(this, 1 * 1000);// 由于启动camera需要时间，在此让其等两秒再进行聚焦知道camera不为空
                } else {

                }
            }
        }, 2 * 1000);*/
    }

    public CameraView(Context context, Activity act) {// 在此定义一个构造方法用于拍照过后把CameraActivity给finish掉
        this(context);
        this.act = act;
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        // TODO Auto-generated method stub

        camera = Camera.open();// 摄像头的初始化
        camera.setDisplayOrientation(90);//摄像头进行旋转90°
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (holder != null) {
                    try {
                        camera.setPreviewDisplay(holder);

                        camera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                if (success) {
                                  /*  try {
                                        Thread.sleep(10000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }*/
                                    camera.takePicture(new Camera.ShutterCallback() {// 如果聚焦成功则进行拍照
                                        @Override
                                        public void onShutter() {
                                        }
                                    }, null, CameraView.this);
                                } else {
                                }
                            }
                        });
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    handler.postDelayed(this, 1 * 1000);
                }
            }
        }, 2 * 1000);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

        Camera.Parameters parameters = camera.getParameters();
        //设置预览照片的大小
       // parameters.setPreviewFpsRange(viewWidth, viewHeight);
        //设置相机预览照片帧数
       // parameters.setPreviewFpsRange(4, 10);
        //设置图片格式
        parameters.setPictureFormat(ImageFormat.JPEG);
        //设置图片的质量
        parameters.set("jpeg-quality", 90);
        //设置照片的大小
      //  parameters.setPictureSize(viewWidth, viewHeight);

    /*    parameters = camera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式*/
        camera.setParameters(parameters);// 设置参数
        camera.startPreview();// 开始预览
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    public void onPictureTaken(byte[] data, Camera camera) {// 拍摄完成后保存照片

        try {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String time = format.format(date);

            //在SD卡上创建文件夹
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/myCamera/pic");
            if (!file.exists()) {

                file.mkdirs();
            }

            String path = Environment.getExternalStorageDirectory()
                    + "/myCamera/pic/" + time + ".jpg";
            data2file(data, path);
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
            holder.removeCallback(CameraView.this);
            audio.setRingerMode(current);
            act.finish();
            //uploadFile(path);

        } catch (Exception e) {

        }
    }

    private void data2file(byte[] w, String fileName) throws Exception {// 将二进制数据转换为文件的函数
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            out.write(w);
            out.close();
        } catch (Exception e) {
            if (out != null)
                out.close();
            throw e;
        }
    }
//  private void uploadFile(String filePath)// 拍照过后上传文件到服务器
//  {
//  }

}