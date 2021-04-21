package douting.hearing.ui.presenter;

import com.see.mvp.base.Presenter;

import java.util.List;

import douting.hearing.core.Hearing;
import douting.hearing.core.callback.ExtCallback;
import douting.hearing.core.entity.Calibration;
import douting.hearing.core.entity.HeadsetDescribe;
import douting.hearing.core.entity.WiredPayInfo;
import douting.hearing.ui.HearingHeadsetActivity;

/**
 * @author by wuxiang@tinglibao.com.cn on 2021/4/20.
 */
public class HearingHeadsetPresenter extends Presenter<HearingHeadsetActivity> {
    private WiredPayInfo mPayInfo;

    @Override
    protected void onResume() {
        super.onResume();
        getWiredPrice();
        getWiredCalibration();
        getDescribe();
    }

    private void getDescribe() {
        Hearing.getDescribe(new ExtCallback<List<HeadsetDescribe>>() {
            @Override
            public void onSuccess(List<HeadsetDescribe> data) {
                for (HeadsetDescribe describe : data) {
                    if (describe.getType() == 1) {
                        getView().showHeadsetDescribe(describe.getDescribeText());
                    } else if (describe.getType() == 0) {
                        getView().showBluetoothDescribe(describe.getDescribeText());
                    }
                }
            }

            @Override
            public void onFail(int i, String s) {

            }
        });
    }

    private void getWiredPrice() {
        Hearing.getWiredPrice(new ExtCallback<WiredPayInfo>() {
            @Override
            public void onSuccess(WiredPayInfo data) {
                mPayInfo = data;
                if (mPayInfo.hasPromotions()) {
                    //有活动
                    getView().showPromotions(mPayInfo.getActivity().getName());
                } else if (mPayInfo.needPay()) {
                    //需要付费
                    getView().showNeedPay();
                } else if (mPayInfo.getHeadset() != null) {
                    int count = mPayInfo.getHeadset().getSurplusFrequency();
                    if (count > 0) {
                        //用户购买了次数，剩余的次数
                        getView().showCount(count);
                    } else {
                        //用户购买了时长，剩余时长
                        getView().showEndTime(mPayInfo.getHeadset().getEndTime());
                    }
                }
            }

            @Override
            public void onFail(int i, String s) {

            }
        });
    }

    private void getWiredCalibration() {
        Hearing.getRecommend(new ExtCallback<Calibration>() {
            @Override
            public void onSuccess(Calibration calibration) {
                getView().showEarModel(calibration.getEarPath(), calibration.getEarModel());
            }

            @Override
            public void onFail(int i, String s) {

            }
        });
    }


    public void checkWiredPrice() {
        if (mPayInfo == null) {
            return;
        }

        if (mPayInfo.needPay()) {
            getView().goBuyHeadset();
        } else {
            getView().goToHeadsetTesting();
        }
    }
}
