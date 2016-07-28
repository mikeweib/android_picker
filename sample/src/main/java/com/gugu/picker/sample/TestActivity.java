package com.gugu.picker.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gugu.picker.BasePicker;
import com.gugu.picker.SamplePicker;

import java.util.Arrays;


public class TestActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
    }

    public void onTest(View view) {
        String[] apps = {"测试", "一个", "两个", "三个"};
        SamplePicker p = new SamplePicker(this);
        p.setItems(Arrays.asList(apps));
        p.setOnSelectedListener(i -> Log.d("TAG", "i = " + i));
        p.show();
    }
}
