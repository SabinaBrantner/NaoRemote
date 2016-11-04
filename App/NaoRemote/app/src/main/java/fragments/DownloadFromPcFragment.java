package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nao.sabina.projectnao.R;

/*
 * ConnectionHelpFragment
 * Author: Sabina Brantner
 * Description: This Fragment shows you a text which describes how to connect with the Nao
 */
public class DownloadFromPcFragment extends Fragment {

    String message = "Damit Sie heruntergeladene Actions auf Ihr Smartphone übertragen können, stecken Sie dieses mithilfe eines USB Kabels an Ihren PC an. Kann auf den internen Speicher zugegriffen werden, fügen Sie die gewünschte Action in den Ordner „NaoRemote“ ein." +
            "Bitte stellen Sie sicher, dass die .xar Datei nicht beschädigt ist und dass sie vom Nao ausführbar ist.";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_download_from_pc, container, false);
        TextView view = (TextView) v.findViewById(R.id.downloadTextView);
        view.setTextSize(23);
        view.setText(message);
        return v;
    }

}
