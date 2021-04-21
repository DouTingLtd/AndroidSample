package douting.hearing.ui;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.see.mvp.base.SeeBaseActivity;

import douting.hearing.core.Hearing;
import douting.hearing.core.testing.chart.HearingChart;
import douting.hearing.core.testing.chart.PureToneResult;

/**
 * @author by wuxiang@tinglibao.com.cn on 2021/4/20.
 */
public class HearingResultActivity extends SeeBaseActivity {
    public static final String RESULT_JSON = "RESULT_JSON";
    private PureToneResult mResult;
    private float[] needTest = {Hearing.HZ_500, Hearing.HZ_1000, Hearing.HZ_2000, Hearing.HZ_4000};
    private Bitmap mBitmap;
    private RelativeLayout layout_chart, layout_data;
    private ImageView test_result_image;

    @Override
    protected int getContentView() {
        return R.layout.hearing_result_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setTitle(R.string.hearing_test_result);
        layout_chart = findViewById(R.id.layout_chart);
        layout_data = findViewById(R.id.layout_data);
        test_result_image = findViewById(R.id.test_result_image);

        initCharView();
        setWordResult();
        initServiceWord();
    }

    private void initServiceWord() {
        TextView hearing_service = findViewById(R.id.hearing_service);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(getString(R.string.hearing_service));
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#fc6400")),
                18, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        hearing_service.setText(builder);
    }

    private void initCharView() {
        mResult = getIntent().getParcelableExtra(RESULT_JSON);
        if (mResult != null) {
            final HearingChart chartView = findViewById(R.id.chart_view);
            chartView.getYAxis().setSpaceNum(2);
            chartView.getXAxis().setScale(needTest);
            chartView.addDataSet(mResult.getLeftDataSet());
            chartView.addDataSet(mResult.getRightDataSet());

            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                final Animation mAnimation = AnimationUtils.loadAnimation(this, R.anim.hearing_cart_anim);
                mAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mBitmap = convertViewToBitmap(layout_chart, layout_chart.getWidth(), layout_chart.getHeight());
                        test_result_image.setImageBitmap(mBitmap);
                        layout_chart.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        layout_data.setVisibility(View.VISIBLE);
                        setOnClick();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout_chart.startAnimation(mAnimation);
                    }
                }, 1500);
            } else {
                TextView simple_right_text = findViewById(R.id.simple_right_text);
                simple_right_text.setText(String.format(getString(R.string.hearing_test_right_simple), mResult.getRightEvaluate(mContext)));
                TextView simple_left_text = findViewById(R.id.simple_left_text);
                simple_left_text.setText(String.format(getString(R.string.hearing_test_left_simple), mResult.getLeftEvaluate(mContext)));
            }
        } else {
            finish();
        }
    }

    private void setOnClick() {
        test_result_image.setOnClickListener(this);
        layout_chart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.test_result_image) {
            layout_chart.setVisibility(View.VISIBLE);
            layout_data.setVisibility(View.GONE);
        } else if (v.getId() == R.id.layout_chart) {
            layout_chart.setVisibility(View.GONE);
            layout_data.setVisibility(View.VISIBLE);
        }
    }

    private void setWordResult() {
        TextView result_right_text = findViewById(R.id.result_right_text);
        result_right_text.setText(String.format(getString(R.string.hearing_test_right_state),
                mResult.getRightLoss(), mResult.getRightEvaluate(mContext)));
        TextView result_left_text = findViewById(R.id.result_left_text);
        result_left_text.setText(String.format(getString(R.string.hearing_test_left_state),
                mResult.getLeftLoss(), mResult.getLeftEvaluate(mContext)));
    }

    public static Bitmap convertViewToBitmap(View view, int bitmapWidth, int bitmapHeight) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }
}
