package com.feasycom.feasyblue.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feasycom.feasyblue.bean.BaseEvent;
import com.feasycom.feasyblue.R;
import com.feasycom.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import static com.feasycom.feasyblue.activity.ParameterModificationActivity.CUSTOMIZE_COMMAND_COUNT;

public class CustomizeAtCommandNoParamView extends LinearLayout {
    TextView label;
//    EditText parameterEdit;
    EditText commandEdit;
    CheckBox checkFlag;

    public CustomizeAtCommandNoParamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.customize_at_command_no_param, this);
//        ButterKnife.bind(view);
        label = (TextView) view.findViewById(R.id.label);
//        parameterEdit = (EditText) view.findViewById(R.id.parameter);
        commandEdit = (EditText) view.findViewById(R.id.command);
        checkFlag = (CheckBox) view.findViewById(R.id.checkFlag);
        checkFlag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (/*parameterEdit.getText().toString().length() > 0
                            &&*/ commandEdit.getText().toString().length() > 0) {
//                        parameterEdit.setEnabled(!isChecked);
                        commandEdit.setEnabled(!isChecked);
                        CUSTOMIZE_COMMAND_COUNT++;
                        EventBus.getDefault().post(new BaseEvent(BaseEvent.CUSTOMIZE_COMMAND_COUNT_CHANGE));
                    } else {
                        checkFlag.setChecked(false);
                        ToastUtil.show(getContext(), getResources().getString(R.string.none));
                    }
                } else {
//                    parameterEdit.setEnabled(!isChecked);
                    commandEdit.setEnabled(!isChecked);
                    CUSTOMIZE_COMMAND_COUNT--;
                    EventBus.getDefault().post(new BaseEvent(BaseEvent.CUSTOMIZE_COMMAND_COUNT_CHANGE));
                }
            }
        });
    }
    public String getCommandInfo(){
        if(checkFlag.isChecked()){
            return "AT+"+commandEdit.getText().toString()/*+"="+parameterEdit.getText().toString()*/;
        }
        return "";
    }
    public void setCommandInfo(String comand){
        commandEdit.setText(comand);
//        parameterEdit.setText(info);
        checkFlag.setChecked(true);
    }
}