
package org.solarex.fileexplorer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.solarex.fileexplorer.utils.FileUtils;
import org.solarex.fileexplorer.utils.SolarexFilter;

import java.io.File;

public class MainActivity extends Activity {
    private ListView lv;
    private TextView pathInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pathInfo = (TextView)this.findViewById(R.id.path_info);
        lv = (ListView)this.findViewById(R.id.lv);
        
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, getString(R.string.sd_mounted), Toast.LENGTH_LONG).show();
            File[] allFiles = getFiles();
            //FileListAdapter adapter = new FileListAdapter(this, allFiles, lv);
            //lv.setAdapter(adapter);
        } else {
            Toast.makeText(this, getString(R.string.sd_umounted), Toast.LENGTH_LONG).show();
        }
    }
    
    public File[] getFiles(){
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File[] unSortedFiles = new File(sdPath).listFiles(new SolarexFilter());
        return FileUtils.sort(unSortedFiles);
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
}
