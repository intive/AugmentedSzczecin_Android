package blstream.com.as.ar;

import android.app.Fragment;
import android.hardware.Camera;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;


public class ARActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        if (savedInstanceState != null) {
            return;
        }
        ARFragment arFragment = new ARFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.arFragment, arFragment).commit();
    }
}
