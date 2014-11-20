package org.solarex.fileexplorer.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.solarex.fileexplorer.R;
import org.solarex.fileexplorer.bean.FileInfo;
import org.solarex.fileexplorer.bean.FileItem;
import org.solarex.fileexplorer.utils.AsyncLoadImage;

import java.io.File;
import java.util.ArrayList;

public class FileListAdapter extends BaseAdapter {
    private static final String TAG = "FileListAdapter";
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<FileInfo> allFileInfos;
    private AsyncLoadImage asyncLoadImage;

    public FileListAdapter(Context context, ArrayList<FileInfo> allFileInfos,
            Handler handler) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.allFileInfos = allFileInfos;
        this.asyncLoadImage = new AsyncLoadImage(handler);
    }

    public void bindData(ArrayList<FileInfo> allFileInfos) {
        this.allFileInfos = allFileInfos;
        this.notifyDataSetChanged();
    }
    

    public AsyncLoadImage getAsyncLoadImage() {
        return asyncLoadImage;
    }

    @Override
    public int getCount() {
        return this.allFileInfos != null ? this.allFileInfos.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (this.allFileInfos != null) ? this.allFileInfos.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileItem item = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.file_item, parent, false);
            item = new FileItem();
            item.fileIcon = (ImageView) convertView.findViewById(R.id.file_icon);
            item.fileName = (TextView) convertView.findViewById(R.id.file_name);
            item.fileChecked = (CheckBox) convertView.findViewById(R.id.file_check);
            convertView.setTag(item);
        } else {
            item = (FileItem) convertView.getTag();
        }
        FileInfo info = this.allFileInfos.get(position);
        File file = info.getFile();
        item.fileName.setText(file.getName());
        item.fileChecked.setChecked(info.isSelected());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files!=null && files.length>0) {
                item.fileIcon.setImageResource(R.drawable.folder_);
            } else {
                item.fileIcon.setImageResource(R.drawable.folder);
            }
        } else {
            String name = file.getName().toLowerCase();
            if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")
                    || name.endsWith(".bmp")) {
                item.fileIcon.setTag(file.getAbsolutePath());
                asyncLoadImage.loadImage(item.fileIcon);
            } else if (name.endsWith(".txt")) {
                item.fileIcon.setImageResource(R.drawable.text);
            } else if (name.endsWith(".chm")) {
                item.fileIcon.setImageResource(R.drawable.chm);
            } else if (name.endsWith(".html") || name.endsWith(".xml") || name.endsWith(".htm")) {
                item.fileIcon.setImageResource(R.drawable.html);
            } else if (name.endsWith(".mp4") || name.endsWith(".3gp") || name.endsWith(".wmv")
                    || name.endsWith(".rm")) {
                item.fileIcon.setImageResource(R.drawable.format_media);
            } else if (name.endsWith(".mp3") || name.endsWith(".wma") || name.endsWith(".ape")) {
                item.fileIcon.setImageResource(R.drawable.format_music);
            } else if (name.endsWith(".xls")) {
                item.fileIcon.setImageResource(R.drawable.excel);
            } else if (name.endsWith(".apk")) {
                item.fileIcon.setTag(file.getAbsolutePath());
                asyncLoadImage.loadApkIcon(this.context, item.fileIcon);
            } else {
                item.fileIcon.setImageResource(R.drawable.file);
            }
        }
        return convertView;
    }

}
