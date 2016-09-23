package douting.android.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by WuXiang on 2016/9/6.
 * ..
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
    }

    public void fastTest(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        intent.putExtra(TestActivity.IS_FAST, true);
        startActivity(intent);
    }

    public void manualTest(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        intent.putExtra(TestActivity.IS_FAST, false);
        startActivity(intent);
    }
}