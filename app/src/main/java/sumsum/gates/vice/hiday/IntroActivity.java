package sumsum.gates.vice.hiday;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Vice on 23/08/2017.
 */

public class IntroActivity extends AppIntro {

    boolean show;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        show = getSharedPreferences("shred" ,Context.MODE_PRIVATE).getBoolean("show", true);
        if (show) {
            addSlide(SampleSlide.newInstance(R.layout.silde_1));
            addSlide(SampleSlide.newInstance(R.layout.slide_2));
            addSlide(SampleSlide.newInstance(R.layout.slide_3));
            addSlide(SampleSlide.newInstance(R.layout.slide_4));

            showSkipButton(false);
            setFadeAnimation();


            int permissionState = ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);

            int callPermissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);


            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                askForPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);

            }


            if (callPermissionState != PackageManager.PERMISSION_GRANTED) {
                askForPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);

            }
            SharedPreferences preferences = getSharedPreferences("shred" , Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean("show" , false).commit();
        }else {
            Intent n = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(n);
        }

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent n = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(n);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }


}
