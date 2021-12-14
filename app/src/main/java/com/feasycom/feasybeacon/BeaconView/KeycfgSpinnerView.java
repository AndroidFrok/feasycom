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

import com.feasycom.controler.FscBeaconApi;
import com.feasycom.controler.FscBeaconApiImp;
import com.feasycom.feasybeacon.Controler.FscBeaconCallbacksImpParameter;
import com.feasycom.feasybeacon.R;
import com.feasycom.feasybeacon.Widget.TipsDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

/**
 * Created by younger on 2018/8/31.
 */

public class KeycfgSpinnerView extends LinearLayout {
    @BindView(R.id.intervalLabel_1)
    TextView Key;
    @BindView(R.id.advinSpinner)
    Spinner advin;
    @BindView(R.id.durationSpinner)
    Spinner duration;
    private int intervalCount = 0;
    private FscBeaconApi fscBeaconApi = FscBeaconApiImp.getInstance();
    private String advinList[] = {"0", "100", "152", "211", "318", "417", "546", "760", "852", "1022", "1280", "2000"};
    private String durationList[] = {"1022", "5000", "10000", "15000", "20000", "25000", "30000", "35000", "40000", "45000", "50000", "55000", "60000"};
    private TipsDialog tipsDialog;
    private String[] strings = new String[2];
    public static Boolean keycfgSend = false;
    private Context context;
    public static int lastSelect;

    public KeycfgSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = View.inflate(context, R.layout.interval_spinner_view_1, this);
        ButterKnife.bind(view);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LableEditView);
        String label = typedArray.getString(R.styleable.LableEditView_labelText);
        Key.setText(label);
        tipsDialog = new TipsDialog(context);
        typedArray.recycle();
    }

    public void setRed() {
        Key.setTextColor(getResources().getColor(R.color.red));
    }

    public void setBlack() {
        Key.setTextColor(0xff1d1d1d);
    }

    public void spinnerAdvin(ArrayAdapter<String> spinnerAdapter, List<String> spinnerStringList) {
        advin.setAdapter(spinnerAdapter);
        setAdvinSelect(0);
    }

    public void spinnerDuration(ArrayAdapter<String> spinnerAdapter, List<String> spinnerStringList) {
        duration.setAdapter(spinnerAdapter);
        duration.setSelection(0);
    }

    public void setAdvinSelect(int position) {
        lastSelect = position;
        Log.e("TAG", "setAdvinSelect: " +  lastSelect);
        advin.setSelection(position);
    }

    public void setDurationSelect(int position) {
        duration.setSelection(position);
    }

    @OnItemSelected(R.id.advinSpinner)
    public void advin(View v, int id) {

        if (id == 0) {
            keycfgSend = false;
            if (IntervalSpinnerView.interval || GsensorSpinnerView.gsensorSend) {
                IntervalSpinnerView.verify = true;
            } else {
                if(FscBeaconCallbacksImpParameter.state){
                    AlertDialog alertDialog2 = new AlertDialog.Builder(context)
                            .setTitle("ERROR")
                            .setMessage("Interval,Gsonser and Key cannot be \"Zero\" at the same time")
                            .setIcon(R.mipmap.ic_launcher)
                            .setNegativeButton(" cancel", new DialogInterface.OnClickListener() {//添加取消
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e("TAG", "onClick: " + lastSelect);
                                    setAdvinSelect(lastSelect);
                                    return;
                                }
                            })
                            .create();
                    alertDialog2.show();
                }
                IntervalSpinnerView.verify = false;
            }
            setRed();
        } else {
            setAdvinSelect(id);
            keycfgSend = true;
            if (GsensorSpinnerView.gsensorSend || IntervalSpinnerView.interval) {
                IntervalSpinnerView.verify = true;
            } else {
                IntervalSpinnerView.verify = false;
            }
            IntervalSpinnerView.verify = true;
            setBlack();
        }
        strings[0] = advinList[id];
        fscBeaconApi.setKeycfg(strings[0], strings[1]);
    }

    @OnItemSelected(R.id.durationSpinner)
    public void duration(View v, int id) {
        if (id == 0) {
            setRed();
        } else {
            strings[1] = durationList[id - 1];
            setBlack();
        }
        fscBeaconApi.setKeycfg(strings[0], strings[1]);
    }

}