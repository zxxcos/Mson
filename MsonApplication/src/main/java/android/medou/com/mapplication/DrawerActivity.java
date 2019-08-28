package android.medou.com.mapplication;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.Toast;

import com.medou.android.mson.Mson;

public class DrawerActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_drawer);


        Student student = new Student();
        student._id = 9;
        student.goods = "sdffsf";

        student = Mson.fromJson(Mson.toJson(student), Student.class);
        Log.e("Mson", "ss" + student.goods);
    }

    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

//    public void setMenuFragment(final Fragment fragment) {
//        fragment.setUserVisibleHint(false);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.relativeDrawer, fragment, fragment.getSceneId());
//        transaction.commit();
//    }
//
//    public void setContentFragment(final Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.container, fragment, fragment.getSceneId());
//        transaction.setPrimaryNavigationFragment(fragment); // primary
//        transaction.commit();
//    }

}
