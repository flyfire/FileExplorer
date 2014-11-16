package org.solarex.fileexplorer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.io.File;

public class FileListAdapter extends BaseAdapter implements OnScrollListener{
    private LayoutInflater inflater;
    private File[] allFiles;
    private ListView lv;
    
    public FileListAdapter(Context context, File[] allFiles, ListView lv){
        inflater = LayoutInflater.from(context);
        this.allFiles = allFiles;
        this.lv = lv;
    }
    
    public void bindData(File[] allFiles){
        this.allFiles = allFiles;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.allFiles==null?0:this.allFiles.length;
    }

    @Override
    public Object getItem(int position) {
        return this.allFiles==null?null:this.allFiles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        
        
        
        
        return null;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        
    }

}
