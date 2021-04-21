package douting.hearing.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import douting.hearing.core.testing.chart.PureToneResult;

/**
 * @author by wuxiang@tinglibao.com.cn on 2021/4/20.
 */
public class HearingRecordAdapter extends BaseAdapter {
    private List<PureToneResult> mList;
    private Context mContext;

    public HearingRecordAdapter(Context context, List<PureToneResult> lists) {
        this.mList = lists;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        } else {
            return mList.size();
        }
    }

    @Override
    public PureToneResult getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.hearing_test_result_item, parent, false);
            holder = new ViewHolder();
            holder.hearing_record_title = view.findViewById(R.id.hearing_record_title);
            holder.hearing_right_point = view.findViewById(R.id.hearing_right_point);
            holder.hearing_left_point = view.findViewById(R.id.hearing_left_point);
            holder.hearing_test_time = view.findViewById(R.id.hearing_test_time);
            holder.hearing_all_point = view.findViewById(R.id.hearing_all_point);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        PureToneResult result = getItem(position);
        String title = mContext.getString(R.string.hearing_record_title);
        String rightPoint = mContext.getString(R.string.hearing_right_point);
        String leftPoint = mContext.getString(R.string.hearing_left_point);
        String testTime = mContext.getString(R.string.hearing_test_time);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String timeValue = dateFormat.format(result.getCreateTime());

        holder.hearing_record_title.setText(String.format(title, position + 1));
        holder.hearing_right_point.setText(String.format(rightPoint, 100 - result.getRightLoss()));
        holder.hearing_left_point.setText(String.format(leftPoint, 100 - result.getLeftLoss()));
        holder.hearing_test_time.setText(String.format(testTime, timeValue));
        holder.hearing_all_point.setText(String.valueOf((200 - result.getLeftLoss() - result.getRightLoss()) / 2));

        return view;
    }

    class ViewHolder {
        TextView hearing_record_title;
        TextView hearing_right_point;
        TextView hearing_left_point;
        TextView hearing_test_time;
        TextView hearing_all_point;
    }
}
