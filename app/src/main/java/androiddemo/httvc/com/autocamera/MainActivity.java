package androiddemo.httvc.com.autocamera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private CameraView2 view;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置横屏模式以及全屏模式

        view = new CameraView2(this);// 通过一个surfaceview的view来实现拍照
       // view.setId(1);
        view = new CameraView2(this, this);
        setContentView(R.layout.activity_main);
        RelativeLayout relative = (RelativeLayout) this.findViewById(R.id.ly);
        RelativeLayout.LayoutParams Layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);// 设置surfaceview使其满足需求无法观看预览
        Layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        Layout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);

        relative.addView(view, Layout);

    }
}
