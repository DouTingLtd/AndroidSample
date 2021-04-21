package douting.hearing.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.see.mvp.base.SeeBaseActivity;

import java.util.List;

import douting.hearing.core.Hearing;
import douting.hearing.core.callback.ExtCallback;
import douting.hearing.core.testing.chart.PureToneResult;

/**
 * @author by wuxiang@tinglibao.com.cn on 2021/4/20.
 */

public class HearingRecordActivity extends SeeBaseActivity {
    private ListView mListView;

    @Override
    protected int getContentView() {
        return R.layout.hearing_record_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setTitle(R.string.hearing_test_record);

        mListView = findViewById(R.id.record_list);
        mListView.setEmptyView(findViewById(R.id.empty_record));

        Hearing.getRecord(new ExtCallback<List<PureToneResult>>() {
            @Override
            public void onSuccess(List<PureToneResult> data) {
                showList(data);
            }

            @Override
            public void onFail(int i, String s) {
            }
        });
    }

    private void showList(final List<PureToneResult> results) {
        HearingRecordAdapter adapter = new HearingRecordAdapter(mContext, results);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, HearingResultActivity.class);
                intent.putExtra(HearingResultActivity.RESULT_JSON, results.get(position));
                startActivity(intent);
            }
        });
    }
}
