package fragments;


import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntegerRes;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nao.sabina.projectnao.ConnectionManager;
import com.nao.sabina.projectnao.ConnectionService;
import com.nao.sabina.projectnao.FileManager;
import com.nao.sabina.projectnao.ImageAdapter;
import com.nao.sabina.projectnao.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActionFragment extends Fragment {

    private static FileManager fileManager = null;
    private static ConnectionManager connectionManager;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.fragment_action, container, false);
        GridView gridView = (GridView) v.findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(this.getContext()));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nameOfAction = view.getContentDescription().toString();
                startAction(extractFileName(nameOfAction), nameOfAction);
            }
        });
        Intent intent = new Intent(this.getContext(), ConnectionService.class);
        if (mBound == false)
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        return v;
    }

    @Override
    public void onStop(){
        if (mBound) {
            getContext().unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    private String extractFileName(String nameOfAction){
        String fileName = nameOfAction.replaceAll(" ", "");
        fileName = fileName + ".xar";
        return fileName.toLowerCase();
    }

    private void startAction(String fileName, String nameOfAction){
        mService.connectionRetry();
        while(mService.getSocket() == null || mService.getSocket().isClosed()){

        }
        if (fileManager == null)
            fileManager = new FileManager(mService.getSocket());
        try {
            Toast.makeText(getContext(), nameOfAction + " wird gestartet", Toast.LENGTH_SHORT).show();
            BufferedReader in = new BufferedReader(new InputStreamReader(getResources().getAssets().open(fileName)));
            fileManager.setBufferedReader(in);
            fileManager.writeFile("behavior.xar", mService.getSocket());
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
