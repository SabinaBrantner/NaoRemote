package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.nao.sabina.projectnao.R;

/**
 * SpeakingFragment
 * Author: Melanie Muehleder
 * Description: In this class/fragment the user is able to let ones Nao speak.
 */
public class SpeakingFragment extends Fragment {

    public SpeakingFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_speaking,container,false);

        Button buttonSpeak = (Button) v.findViewById(R.id.speakButton);

        buttonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Nachricht wurde gesendet...", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
