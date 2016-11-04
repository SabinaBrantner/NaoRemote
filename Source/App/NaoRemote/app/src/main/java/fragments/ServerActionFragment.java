package fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.nao.sabina.projectnao.ActionFile;
import com.nao.sabina.projectnao.ConnectionService;
import com.nao.sabina.projectnao.FileManager;
import com.nao.sabina.projectnao.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServerActionFragment extends Fragment {

    ImageView img1;
    ImageView img2;

    private static FileManager fileManager = null;

    private ConnectionService mService;
    private boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            mService = binder.getService();
            if (mService != null)
                mBound = true;
            else
                mBound = false;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.fragment_server_action, container, false);
        img1 = (ImageView)v.findViewById(R.id.image1);
        img2 = (ImageView)v.findViewById(R.id.image2);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameOfAction = img1.getContentDescription().toString();
                startAction(nameOfAction + ".xar", nameOfAction); //TODO
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameOfAction = img2.getContentDescription().toString();
                startAction(nameOfAction + ".xar", nameOfAction);
            }
        });

        Intent intent = new Intent(this.getContext(), ConnectionService.class);
        if (mBound == false)
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        return v;
    }

    @NonNull
    private String extractFileName(String nameOfAction){
        String[] parts = nameOfAction.split(".");
        return parts[0];
    }

    private void startAction(String fileName, String nameOfAction){
        while(mService == null || mService.getSocket() == null){

        }
        mService.connectionRetry();
        while(mService == null || mService.getSocket() == null){

        }
        if (fileManager == null)
            fileManager = new FileManager(mService.getSocket());
        try {
            Toast.makeText(getContext(), nameOfAction + " wird gestartet", Toast.LENGTH_SHORT).show();
            FileInputStream fis = getContext().openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader in = new BufferedReader(isr);
            fileManager.setBufferedReader(in);
            Toast.makeText(getContext(), "Action wird gesendet", Toast.LENGTH_SHORT).show();
            fileManager.writeFile("behavior.xar", mService.getSocket());
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop(){
        if (mBound) {
            getContext().unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        new HttpRequestTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.Settings) {
            new HttpRequestTask().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, ActionFile[]> {
        @Override
        protected ActionFile[] doInBackground(Void... params) {
            try {
                final String url = "http://vm01.htl-leonding.ac.at/SimpleClub/api/files/actions";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ActionFile[] file = restTemplate.getForObject(url, ActionFile[].class);
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ActionFile[] files) {
            if (files != null) {
                for (ActionFile f : files) {
                    FileOutputStream outputStream;

                    try {
                        outputStream = getContext().openFileOutput(f.getFileName(), Context.MODE_PRIVATE);
                        outputStream.write(f.getContent());
                        outputStream.flush();
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(f.getFileName());
                }
                Toast.makeText(getContext(), "Alle Actions werden geladen", Toast.LENGTH_SHORT).show();
                img1.setImageBitmap(BitmapFactory.decodeByteArray(files[0].getImageBytes(), 0, files[0].getImageBytes().length));
                img1.setContentDescription(files[0].getActionName());
                img2.setImageBitmap(BitmapFactory.decodeByteArray(files[1].getImageBytes(), 0, files[1].getImageBytes().length));
                img2.setContentDescription(files[1].getActionName());
            }
        }
    }
}
