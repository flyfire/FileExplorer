
package org.solarex.fileexplorer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.solarex.fileexplorer.adapter.FileListAdapter;
import org.solarex.fileexplorer.bean.FileInfo;
import org.solarex.fileexplorer.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends Activity implements OnItemClickListener, OnScrollListener {
    private ListView lv;
    private TextView pathInfo;
    private ArrayList<FileInfo> allFileInfos;
    private Handler handler;
    private FileListAdapter adapter;
    private File parentFile;
    private HashSet<FileInfo> selectedFileInfos;
    private HashSet<FileInfo> selectedCopy;
    private static int OPERATION_TYPE = -1;
    private final int ACTION_COPY = 0;
    private final int ACTION_MOVE = 1;
    private final int CREATE_FOLDER_RESULT = 42;
    private ProgressDialog pd;
    private MenuItem pasteItem, deleteItem;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pathInfo = (TextView) this.findViewById(R.id.path_info);
        lv = (ListView) this.findViewById(R.id.lv);
        
        selectedFileInfos = new HashSet<FileInfo>();
        pd = new ProgressDialog(this);
        
        this.handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    //unnecessary
                    case CREATE_FOLDER_RESULT:
                        boolean isSuccess = (Boolean) msg.obj;
                        if (isSuccess) {
                            Toast.makeText(MainActivity.this, "Create folder success!", Toast.LENGTH_LONG).show();
                            allFileInfos = FileUtils.GetPathFiles(pathInfo.getText().toString());
                            adapter.bindData(allFileInfos);
                            lv.setAdapter(adapter);
                        } else {
                            Toast.makeText(MainActivity.this, "Create folder failed!", Toast.LENGTH_LONG).show();
                        }
                        break;

                    default:
                        break;
                }
            }
            
        };
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
            allFileInfos = FileUtils.GetPathFiles(sdPath);
            FileUtils.PrintFileInfos(allFileInfos);
            adapter = new FileListAdapter(this, allFileInfos, handler, selectedFileInfos);
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
        pasteItem = menu.getItem(4);
        deleteItem = menu.getItem(5);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_selectall:
                for (FileInfo fileInfo : allFileInfos) {
                    fileInfo.setSelected(true);
                    selectedFileInfos.add(fileInfo);
                }
                for (FileInfo fileInfo : selectedFileInfos) {
                    Log.d(TAG, "selected file info = " + fileInfo);
                }
                adapter.bindData(allFileInfos);
                lv.setAdapter(adapter);
                break;
            case R.id.action_unselectall:
                for (FileInfo fileInfo : allFileInfos) {
                    fileInfo.setSelected(false);
                }
                selectedFileInfos.clear();
                adapter.bindData(allFileInfos);
                lv.setAdapter(adapter);
                break;
            case R.id.action_mkdir:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.action_mkdir));
                final EditText text = new EditText(this);
                builder.setView(text);
                builder.setPositiveButton("OK", new OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = text.getText().toString();
                        Log.v(TAG, "input name = " + name);
                        boolean isSuccess = false;
                        if (!TextUtils.isEmpty(name)) {
                            isSuccess = FileUtils.CreateFolder(pathInfo.getText().toString(), name);
                        }else {
                            Toast.makeText(MainActivity.this, "Folder name cant be empty", Toast.LENGTH_LONG).show();
                        }
                        Log.v(TAG, "isSuccess = " + isSuccess);
                        Message msg = Message.obtain();
                        msg.what = CREATE_FOLDER_RESULT;
                        msg.obj = isSuccess;
                        handler.sendMessage(msg);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
                break;
            case R.id.action_copy:
                if (canPaste()) {
                    OPERATION_TYPE = ACTION_COPY;
                }
                Log.v(TAG, "action copy clicked!");
                break;
            case R.id.action_move:
                if (canPaste()) {
                    OPERATION_TYPE = ACTION_MOVE;
                }
                Log.v(TAG, "action move clicked!");
                break;
            case R.id.action_paste:
                if (canPaste()) {
                    switch (OPERATION_TYPE) {
                        case ACTION_COPY:
                            if (selectedCopy == null) {
                                selectedCopy = new HashSet<FileInfo>();
                            }
                            for (FileInfo fileInfo : selectedFileInfos) {
                                selectedCopy.add(fileInfo);
                            }
                            selectedFileInfos.clear();
                            new CopyFileTask(pathInfo.getText().toString()).execute();
                            break;
                        case ACTION_MOVE:
                            if (selectedCopy == null) {
                                selectedCopy = new HashSet<FileInfo>();
                            }
                            for (FileInfo fileInfo : selectedFileInfos) {
                                selectedCopy.add(fileInfo);
                            }
                            selectedFileInfos.clear();
                            new MoveFileTask(pathInfo.getText().toString()).execute();
                            break;
                        default:
                            Toast.makeText(this, getString(R.string.set_operation),
                                    Toast.LENGTH_LONG).show();
                            break;
                    }
                }
                Log.v(TAG, "action paste clicked operation type = " + OPERATION_TYPE);
                break;
            case R.id.action_delete:
                if (canPaste()) {
                    if (selectedCopy == null) {
                        selectedCopy = new HashSet<FileInfo>();
                    }
                    for (FileInfo fileInfo : selectedFileInfos) {
                        selectedCopy.add(fileInfo);
                    }
                    selectedFileInfos.clear();
                    new DeleteFileTask().execute();
                }
                Log.v(TAG, "action delete clicked");
                break;
            default:
                break;
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
            this.allFileInfos = FileUtils.GetPathFiles(pathInfo.getText().toString());
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
                allFileInfos = FileUtils.GetPathFiles(parentPath);
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
    
    private boolean canPaste(){
        return this.selectedFileInfos.size() != 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.selectedFileInfos.clear();
    }
    
    class CopyFileTask extends AsyncTask<Void, Void, Void>{
        private String dest;
        private ProgressDialog pd;
        
        public CopyFileTask(String dest){
            this.dest = dest;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (null == pd) {
                pd = new ProgressDialog(MainActivity.this);
            }
            pd.setTitle("Copy files");
            pd.setMessage("Operation copying files...");
            pd.show();
            pasteItem.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (FileInfo fileInfo : selectedCopy) {
                FileUtils.CopyFile(fileInfo, dest);
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            selectedCopy.clear();
            selectedCopy = null;
            allFileInfos = FileUtils.GetPathFiles(pathInfo.getText().toString());
            adapter.bindData(allFileInfos);
            lv.setAdapter(adapter);
            pd.dismiss();
            pasteItem.setEnabled(true);
        }
        
    }
    
    class MoveFileTask extends AsyncTask<Void, Void, Void>{
        private String path;
        public MoveFileTask(String path){
            this.path = path;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (null == pd) {
                pd = new ProgressDialog(MainActivity.this);
            }
            pd.setTitle("Moving files");
            pd.setMessage("Operation moving files...");
            pd.show();
            pasteItem.setEnabled(false);
        }
        @Override
        protected Void doInBackground(Void... params) {
            for (FileInfo fileInfo : selectedCopy) {
                FileUtils.MoveFile(fileInfo, path);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            selectedCopy.clear();
            selectedCopy = null;
            allFileInfos = FileUtils.GetPathFiles(pathInfo.getText().toString());
            adapter.bindData(allFileInfos);
            lv.setAdapter(adapter);
            pasteItem.setEnabled(true);
            pd.dismiss();
        }
        
        
    }
    
    class DeleteFileTask extends AsyncTask<Void, Void, Void>{
        public DeleteFileTask(){
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd == null) {
                pd = new ProgressDialog(MainActivity.this);
            }
            pd.setTitle("Delete files");
            pd.setMessage("Operation deleting files...");
            pd.show();
            deleteItem.setEnabled(false);
        }
        
        @Override
        protected Void doInBackground(Void... params) {
            for (FileInfo fileInfo : selectedCopy) {
                FileUtils.DeleteFiles(fileInfo);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            selectedCopy.clear();
            selectedCopy = null;
            allFileInfos = FileUtils.GetPathFiles(pathInfo.getText().toString());
            adapter.bindData(allFileInfos);
            lv.setAdapter(adapter);
            pd.dismiss();
            deleteItem.setEnabled(true);
        }
        
        
    }
    

}
