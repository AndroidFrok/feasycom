package com.feasycom.feasyblue.dfileselector.helper;


import com.feasycom.feasyblue.dfileselector.adapter.FileSelectorAdapter;
import com.feasycom.feasyblue.dfileselector.bean.FileItem;
import com.feasycom.feasyblue.dfileselector.util.FileSelectorUtils;
import com.feasycom.feasyblue.dfileselector.util.FileUtils;
import com.feasycom.feasyblue.dfileselector.widget.FileSelectorLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author duke
 * @dateTime 2018-09-12 23:31
 * @description
 */
public class ClickHelper implements FileSelectorAdapter.OnFileItemClickListener,
        FileSelectorLayout.OnButtonClickListener {
    private FileSelectorLayout selectorLayout;
    private FileSelector.OnFileSelectListener onFileSelectListener;

    private static final String TAG = "ClickHelper";

    public ClickHelper(FileSelectorLayout selectorLayout, FileSelector.OnFileSelectListener onFileSelectListener) {
        this.selectorLayout = selectorLayout;
        this.onFileSelectListener = onFileSelectListener;
    }


    @Override
    public void onFolderClick(File file) {
        if (file == null) {
            return;
        }
        selectorLayout.refreshUI(file.getAbsolutePath(), FileSelectorUtils.parseToFileItemList(file.listFiles(selectorLayout.getFileFilter())));
    }

    @Override
    public void onFileSelected(File file) {
        if (onFileSelectListener == null || file == null) {
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        list.add(file.getAbsolutePath());
        onFileSelectListener.onSelected(list);
    }

    @Override
    public void onBackLevelClick(String currentFolder) {
        if (FileSelectorUtils.isEmpty(currentFolder)) {
            return;
        }
        File currentFile = new File(currentFolder);
        if (!currentFile.exists()) {
            return;
        }
        File fileRoot = FileUtils.getRootFile();
        if (fileRoot == null || fileRoot.getAbsolutePath().equals(currentFile.getAbsolutePath())) {
            //已经是根目录
            return;
        }
        onFolderClick(currentFile.getParentFile());
    }

    @Override
    public void onSubmitClick(List<FileItem> list) {
        if (onFileSelectListener == null || FileSelectorUtils.isEmpty(list)) {
            return;
        }
        onFileSelectListener.onSelected(FileSelectorUtils.getSelectedFileList(list));
    }
}
