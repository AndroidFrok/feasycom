package com.feasycom.feasyblue.dfileselector.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.feasycom.feasyblue.R;
import com.feasycom.feasyblue.dfileselector.helper.FileSelector;
import com.feasycom.feasyblue.dfileselector.provide.filter.FileFilterProvide;
import com.feasycom.feasyblue.dfileselector.widget.FileSelectorLayout;

import java.util.ArrayList;

public class DefaultSelectorActivity extends BasePermissionActivity implements View.OnClickListener {
    //startActivityForResult request code
    public static final int FILE_SELECT_REQUEST_CODE = 255;
    //send broadcast when get file
    public static final String FILE_SELECT_ACTION = "com.fileselector.select.file.action";
    //返回数据为路径字符串数组
    private static final String FILE_SELECT_STRING_ARRAY_PARAM = "file_select_string_array_param";

    private ImageView imageViewBack;
    private FileSelectorLayout fileSelectorLayout;

    //接收到的参数
    private boolean isForResult;//是否是 startActivityForResult 方式启动的
    private boolean isNeedBroadCast;//是否需要发送广播传递选中的数据
    private boolean isMultiMode;//是否是多选模式
    private int maxFileCount;//多选模式文件的最大数量
    private String path;
    private boolean isFilter = false;

    public static void startActivity(Context context) {
        startActivity(context, true);
    }

    public static void startActivity(Context context, boolean isNeedBroadCast) {
        startActivity(context, isNeedBroadCast, false, 0);
    }

    public static void startActivity(Context context, boolean isNeedBroadCast, boolean isMultiMode, int maxFileCount) {
        start(context, false, isNeedBroadCast, isMultiMode, maxFileCount);
    }

    public static void startActivityForResult(Activity activity) {
        startActivityForResult(activity, false);
    }

    public static void startActivityForResult(Activity activity, boolean isNeedBroadCast) {
        startActivityForResult(activity, isNeedBroadCast, false, 0);
    }

    public static void startActivityForResult(Activity activity, boolean isNeedBroadCast, boolean isMultiMode, int maxFileCount) {
        start(activity, true, isNeedBroadCast, isMultiMode, maxFileCount);
    }

    public static void startActivityForResult(Activity activity, boolean isNeedBroadCast, boolean isMultiMode,
                                              int maxFileCount, String path, boolean isFilter) {
        start(activity, true, isNeedBroadCast, isMultiMode, maxFileCount, path, isFilter);
    }

    private static void start(Context context, boolean isForResult, boolean isNeedBroadCast, boolean isMultiMode, int maxFileCount) {
        if (context == null || (isForResult && !(context instanceof Activity))) {
            return;
        }
        Intent intent = new Intent(context, DefaultSelectorActivity.class);
        intent.putExtra("isForResult", isForResult);
        intent.putExtra("isNeedBroadCast", isNeedBroadCast);
        intent.putExtra("isMultiMode", isMultiMode);
        intent.putExtra("maxFileCount", maxFileCount);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, FILE_SELECT_REQUEST_CODE);
        } else {
            context.startActivity(intent);
        }
    }

    private static void start(Context context, boolean isForResult, boolean isNeedBroadCast,
                              boolean isMultiMode, int maxFileCount, String path, boolean filter) {
        if (context == null || (isForResult && !(context instanceof Activity))) {
            return;
        }
        Intent intent = new Intent(context, DefaultSelectorActivity.class);
        intent.putExtra("isForResult", isForResult);
        intent.putExtra("isNeedBroadCast", isNeedBroadCast);
        intent.putExtra("isMultiMode", isMultiMode);
        intent.putExtra("maxFileCount", maxFileCount);
        intent.putExtra("path", path);
        intent.putExtra("filter", filter);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, FILE_SELECT_REQUEST_CODE);
        } else {
            context.startActivity(intent);
        }
    }

    private void parseIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        isForResult = intent.getBooleanExtra("isForResult", false);
        isNeedBroadCast = intent.getBooleanExtra("isNeedBroadCast", false);
        isMultiMode = intent.getBooleanExtra("isMultiMode", false);
        maxFileCount = intent.getIntExtra("maxFileCount", 0);
        path = intent.getStringExtra("path");
        isFilter = intent.getBooleanExtra("filter", false);
    }

    public static ArrayList<String> getDataFromIntent(Intent intent) {
        Log.e("TAG", "getDataFromIntent: " + (intent == null) );
        if (intent == null) {
            return null;
        }
        Log.e("TAG", "getDataFromIntent: " + intent.getStringArrayListExtra(FILE_SELECT_STRING_ARRAY_PARAM) );
        return intent.getStringArrayListExtra(FILE_SELECT_STRING_ARRAY_PARAM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent(getIntent());
        setContentView(R.layout.dfileselector_activity_file_selector);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        fileSelectorLayout = (FileSelectorLayout) findViewById(R.id.activity_file_select_layout);
        fileSelectorLayout.selectPath(path);

        imageViewBack = (ImageView) findViewById(R.id.activity_back);
        imageViewBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        int id = v.getId();
        if (id == R.id.activity_back) {
            finish();
        }
    }

    @Override
    protected void onPermissionSuccess() {
        if (!isFilter){
            FileSelector.with(fileSelectorLayout)
                    .listen(onFileSelectListener)
                    .setMultiSelectionModel(isMultiMode)
                    .setMultiModelMaxSize(maxFileCount)
                    .setup();
        }else {
            FileSelector.with(fileSelectorLayout)
                    .listen(onFileSelectListener)
                    .setMultiSelectionModel(isMultiMode)
                    .setMultiModelMaxSize(maxFileCount)
                    .setFileFilter(new FileFilterProvide(new String[]{"dfu"}, false, 0))
                    .setup();
        }

    }

    private FileSelector.OnFileSelectListener onFileSelectListener = new FileSelector.OnFileSelectListener() {
        @Override
        public void onSelected(ArrayList<String> list) {
            for (String s:
                 list) {
                Log.e("TAG", "onSelected: "+ s );
            }
            Intent intent = new Intent();
            //保存数据到intent中
            intent.putStringArrayListExtra(FILE_SELECT_STRING_ARRAY_PARAM, list);
            if (isNeedBroadCast) {
                //发送广播
                intent.setAction(FILE_SELECT_ACTION);
                DefaultSelectorActivity.this.sendBroadcast(intent);
            }
            Log.e("TAG", "onSelected: " + isForResult );
            if (isForResult) {
                Log.e("TAG", "onSelected: " + intent.getData() );
                setResult(Activity.RESULT_OK, intent);
            }
            Log.e("TAG", "onSelected: 退出" );
            DefaultSelectorActivity.this.finish();
        }
    };
}
