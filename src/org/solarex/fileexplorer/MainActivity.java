
package org.solarex.fileexplorer;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.solarex.fileexplorer.adapter.FileListAdapter;
import org.solarex.fileexplorer.bean.FileInfo;
import org.solarex.fileexplorer.utils.FileUtils;
import org.solarex.fileexplorer.utils.SolarexFilter;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity implements OnItemClickListener, OnScrollListener {
    private ListView lv;
    private TextView pathInfo;
    private ArrayList<FileInfo> allFileInfos;
    private Handler handler;
    private FileListAdapter adapter;
    private File parentFile;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pathInfo = (TextView) this.findViewById(R.id.path_info);
        lv = (ListView) this.findViewById(R.id.lv);
        this.handler = new Handler();
        lv.setOnItemClickListener(this);
        Log.v(TAG, "lv set onitemclick");
        lv.setOnScrollListener(this);
        /*
         * ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
         * android.R.layout.simple_dropdown_item_1line, new String[]{"aaa",
         * "bbb", "ccc"}); lv.setAdapter(adapter);
         */
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, getString(R.string.sd_mounted), Toast.LENGTH_LONG).show();
            // File[] allFiles = getFiles();
            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            pathInfo.setText(sdPath);
            parentFile = new File(sdPath).getParentFile();
            Log.v(TAG, "onCreate sdPath = " + sdPath + " name = " + new File(sdPath).getName()
                    + " parent = " + new File(sdPath).getParentFile().getAbsolutePath());
            Log.v(TAG, "files = " + new File(sdPath).listFiles());
            allFileInfos = FileUtils.getFiles(sdPath);
            FileUtils.PrintFileInfos(allFileInfos);
            adapter = new FileListAdapter(this, allFileInfos, handler);
            // adapter = new FileListAdapter(this, allFileInfos, lv, handler);
            lv.setAdapter(adapter);
            // FileListAdapter adapter = new FileListAdapter(this, allFiles,
            // lv);
            // lv.setAdapter(adapter);
        } else {
            Toast.makeText(this, getString(R.string.sd_umounted), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v(TAG, "position = " + position);
        if (this.allFileInfos.get(position).getFile().isDirectory()) {
            /*
             * pathInfo.append("/" +
             * this.allFileInfos.get(position).getFile().getName());
             */
            parentFile = this.allFileInfos.get(position).getFile().getParentFile();
            String path = pathInfo.getText() + "/"
                    + this.allFileInfos.get(position).getFile().getName();
            Log.v(TAG, "path = " + path);
            pathInfo.setText(path);
            Log.v(TAG, "pathInfo = " + pathInfo.getText());
            this.allFileInfos = FileUtils.getFiles(pathInfo.getText().toString());
            adapter.bindData(allFileInfos);
            lv.setAdapter(adapter);
        } else {
            Log.v(TAG, "position = " + position + " fileInfo = "
                    + this.allFileInfos.get(position).toString());
            // todo:use Intent to start activity to open file at this position
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.v(TAG, "scrollState = " + scrollState);
        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_FLING:
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                adapter.getAsyncLoadImage().lock();
                break;
            case OnScrollListener.SCROLL_STATE_IDLE:
                adapter.getAsyncLoadImage().unlock();
            default:
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String parentPath = parentFile.getAbsolutePath();
            String parentParentPath = parentFile.getParentFile().getAbsolutePath();
            Log.v(TAG, "parentPath = " + parentPath + " parentParentPath = " + parentParentPath);
            if (parentFile != null && !parentParentPath.equals("/storage")) {
                allFileInfos = FileUtils.getFiles(parentPath);
                adapter.bindData(allFileInfos);
                lv.setAdapter(adapter);
                parentFile = parentFile.getParentFile();
                pathInfo.setText(parentPath);
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return true;
    }

}
