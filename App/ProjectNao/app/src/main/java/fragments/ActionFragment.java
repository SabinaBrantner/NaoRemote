package fragments;


import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
import com.nao.sabina.projectnao.FileManager;
import com.nao.sabina.projectnao.ImageAdapter;
import com.nao.sabina.projectnao.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActionFragment extends Fragment {

    private static FileManager fileManager = null;
    private static ConnectionManager connectionManager;

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
        return v;
    }

    public void setFileManager(FileManager fileM){
        this.fileManager = fileM;
    }

    public void setConnectionManager(ConnectionManager cM){connectionManager = cM;}

    private String extractFileName(String nameOfAction){
        String fileName = nameOfAction.replaceAll(" ", "");
        fileName = fileName + ".xar";
        return fileName;
    }

    private void startAction(String fileName, String nameOfAction){
        fileManager = new FileManager(connectionManager.getSocketCon(), "behavior.xar");
        fileManager.writeFile("behavior.xar");
        /*if (this.fileManager == null || this.fileManager.getSocketCon() == null)
            Toast.makeText(getContext(), "Bitte verbinden Sie sich mit dem Nao", Toast.LENGTH_SHORT).show();
        else {
            if (this.fileManager.getSocketCon().isConnected() == false)
                this.fileManager.openSocketConnection();
            if (this.fileManager.getSocketCon().isConnected() == false)
                Toast.makeText(getContext(), "Bitte verbinden Sie sich mit dem Nao", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getContext(), nameOfAction + "selected \n Datei wird gestartet", Toast.LENGTH_SHORT).show();
                fileName = "behavior.xar";
                this.fileManager.writeFile(fileName);
            }
        }*/
    }
}
