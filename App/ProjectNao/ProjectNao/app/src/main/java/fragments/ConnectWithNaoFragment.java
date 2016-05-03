package fragments;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nao.sabina.projectnao.R;

/**
 * ConnectWithNaoFragment
 * Author: Sabina Brantner
 * Description: In this class/fragment the user is able to connect with ones Nao. Moreover, there
 * is also a help button which describes how to connect.
 */
public class ConnectWithNaoFragment extends Fragment {

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connect_with_nao,container,false);
        Button button = (Button) v.findViewById(R.id.helpConnectionButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConnectionHelp(v);
            }
        });

        return v;
    }

    public void showConnectionHelp(View view) {
        Intent intent = new Intent(this.getActivity(), ConnectionHelpFragment.class);
        startActivity(intent);
    }

}
