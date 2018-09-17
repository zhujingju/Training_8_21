package com.grasp.training.fragmet;

import android.view.View;

import com.grasp.training.R;
import com.grasp.training.tool.BaseMqttFragment;

public class Scenario extends BaseMqttFragment {
    @Override
    public int getInflate() {
        return R.layout.scenario;
    }

    @Override
    public void init(View v) {

    }

    @Override
    public String getMyTopic() {
        return null;
    }

    @Override
    public String getMyTopicDing() {
        return null;
    }

    @Override
    public void MyMessageArrived(String message) {

    }
}
