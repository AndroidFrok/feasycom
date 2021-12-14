package com.feasycom.feasybeacon.BeaconView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.feasycom.bean.FeasyBeacon;
import com.feasycom.controler.FscBeaconApi;
import com.feasycom.controler.FscBeaconApiImp;
import com.feasycom.feasybeacon.Controler.FscBeaconCallbacksImpParameter;
import com.feasycom.feasybeacon.R;
import com.feasycom.feasybeacon.Widget.TipsDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

import static com.feasycom.feasybeacon.Activity.ParameterSettingActivity.isModule_BP109;
/**
 * Created by younger on 2018/8/31.
 */

public class IntervalSpinnerView extends LinearLayout {
    private static FeasyBeacon fb;
    @BindView(R.id.intervalLabel)
    TextView intervalLabel;
    @BindView(R.id.intervalSpinner)
    Spinner intervalSpinner;
    private int intervalCount = 0;
    private FscBeaconApi fscBeaconApi= FscBeaconApiImp.getInstance();
    private String intervalList[] = {"0","100","152","211","318","417","546","760","852","1022","1280","2000","3000","4000","5000","6000","7000","8000","9000","10000"};
    // private String intervalList_1[] = {"100","150","200","300","400","550","750","850","1000","1300","2000"};
    private TipsDialog tipsDialog;
    public static Boolean interval = true;
    private Context context;
    public static Boolean verify = false;
    public static int position;
    public static int lastSelect;

    public IntervalSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = View.inflate(context, R.layout.interval_spinner_view, this);
        ButterKnife.bind(view);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LableEditView);
        String label = typedArray.getString(R.styleable.LableEditView_labelText);
        // 给intervalLabel设置Interval
        intervalLabel.setText(label);
        tipsDialog = new TipsDialog(context);
        typedArray.recycle();
    }

    public void setRed() {
        intervalLabel.setTextColor(getResources().getColor(R.color.red));
    }

    public void setBlack() {
        intervalLabel.setTextColor(0xff1d1d1d);
    }

    public void spinnerInit(ArrayAdapter<String> spinnerAdapter, List<String> spinnerStringList,FeasyBeacon fb) {
        this.fb = fb;
        intervalSpinner.setAdapter(spinnerAdapter);
        intervalSpinner.setSelection(0);
    }

    public void setSelect(int position) {
        lastSelect = position;
        intervalSpinner.setSelection(position);
    }




    @OnItemSelected(R.id.intervalSpinner)
    public void intervalSelect(View v, int id) {

        if(!(fb.getKeycfg() || fb.getGsensor())){
            id += 1;
        }
        intervalCount++;
        if (id == 0) {
            interval = false;
            if (GsensorSpinnerView.gsensorSend || KeycfgSpinnerView.keycfgSend) {
                verify = true;
            }else{
                if(FscBeaconCallbacksImpParameter.state){
                    AlertDialog alertDialog2 = new AlertDialog.Builder(context)
                            .setTitle("ERROR")
                            .setMessage("Interval,Gsonser and Key cannot be \"Zero\" at the same time")
                            .setIcon(R.mipmap.ic_launcher)
                            .setNegativeButton(" cancel", new DialogInterface.OnClickListener() {//添加取消
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e(TAG, "onClick: " );
                                    setSelect(lastSelect);
                                    return;
                                }
                            })
                            .create();
                    alertDialog2.show();
                }
                verify = false;
            }
            setRed();
        }else{
            interval = true;
            verify = true;
            setBlack();
        }

       if(isModule_BP109 == true && intervalCount != 2){
            if(id >= 8 && id < 11) {
                tipsDialog.setInfo("Selecting this advertising interval may lower the broadcast distance!");
                tipsDialog.show();
            }
            else if(id >= 11){
                tipsDialog.setInfo("Selecting this advertising interval may lower the broadcast distance and the connection probability!");
                tipsDialog.show();
            }
        }
        else if(isModule_BP109 == false && intervalCount != 2 ){
            if (id >= 11) {
                tipsDialog.setInfo("Selecting this advertising interval may lower the connection probability!");
                tipsDialog.show();
            }
        }
        position = id;
        setBlack();
        fscBeaconApi.setBroadcastInterval(intervalList[id]);
    }

    private static final String TAG = "IntervalSpinnerView";

}