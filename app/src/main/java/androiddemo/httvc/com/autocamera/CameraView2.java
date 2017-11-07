package androiddemo.httvc.com.autocamera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class CameraView2 extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {

    private SurfaceHolder holder;
    private Camera mCamera;
    private Camera.Parameters parameters;
    private Activity act;
    private Handler handler = new Handler();
    private Context context;
    private SurfaceView surfaceView;
    private AudioManager audio;
    private int current;
    private static final int CAMERA_ID = 0; //后置摄像头

    public CameraView2(Context context) {
        super(context);

        surfaceView = this;
        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final int current = audio.getRingerMode();
        audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        this.context = context;
        holder = getHolder();// 生成Surface Holder
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 指定Push Buffer

    }

    public CameraView2(Context context, Activity act) {// 在此定义一个构造方法用于拍照过后把CameraActivity给finish掉
        this(context);
        this.act = act;
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
       /* handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (holder != null) {
                    try {
                        mCamera.setPreviewDisplay(holder);

                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                if (success) {
                                    camera.takePicture(new Camera.ShutterCallback() {// 如果聚焦成功则进行拍照
                                        @Override
                                        public void onShutter() {
                                        }
                                    }, null, CameraView2.this);
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
        }, 2 * 1000);*/
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }


    private void startPreview() {

        mCamera = Camera.open(CAMERA_ID);

        Camera.Parameters parameters = mCamera.getParameters();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        Camera.Size size = getBestPreviewSize(width, height, parameters);
        if (size != null) {
            //设置预览分辨率
            parameters.setPreviewSize(size.width, size.height);
            //设置保存图片的大小
            parameters.setPictureSize(size.width, size.height);
        }

        //自动对焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setPreviewFrameRate(20);

        //设置相机预览方向
        mCamera.setDisplayOrientation(90);

        mCamera.setParameters(parameters);

        try {
            mCamera.setPreviewDisplay(holder);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        mCamera.startPreview();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mCamera.takePicture(new Camera.ShutterCallback() {// 如果聚焦成功则进行拍照
            @Override
            public void onShutter() {
            }
        }, null, CameraView2.this);
    }

    private void stopPreview() {
        //释放Camera对象
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
                holder.removeCallback(CameraView2.this);
                audio.setRingerMode(current);
                act.finish();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return result;
    }

    public void onPictureTaken(byte[] data, Camera camera) {// 拍摄完成后保存照片

        try {
            FileOutputStream fos = new FileOutputStream(new File
                    ( Environment.getExternalStorageDirectory()
                            + "/myCamera/pic/" + System.currentTimeMillis() + ".jpg"));
            //旋转角度，保证保存的图片方向是对的
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            stopPreview();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //mCamera.startPreview();
    }
}