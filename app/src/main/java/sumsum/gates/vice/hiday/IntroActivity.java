package sumsum.gates.vice.hiday;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;

/**
 * Created by Vice on 23/08/2017.
 */

public class IntroActivity extends AppIntro {



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(SampleSlide.newInstance(R.layout.silde_1));
        addSlide(SampleSlide.newInstance(R.layout.slide_2));
        addSlide(SampleSlide.newInstance(R.layout.slide_3));
        addSlide(SampleSlide.newInstance(R.layout.slide_4));

        showSkipButton(false);
        setFadeAnimation();



        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int callPermissionState = ActivityCompat.checkSelfPermission(this , Manifest.permission.CALL_PHONE);


        if (permissionState != PackageManager.PERMISSION_GRANTED){
            askForPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);

        }


        if (callPermissionState != PackageManager.PERMISSION_GRANTED){
            askForPermissions(new String[]{Manifest.permission.CALL_PHONE} , 3);

        }


    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }


}
