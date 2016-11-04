package fragments;


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nao.sabina.projectnao.ConnectionService;
import com.nao.sabina.projectnao.R;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * A simple {@link Fragment} subclass.
 */
public class ControllFragment extends Fragment {


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

    private float startPosX = 0;
    private float startPosY = 0;
    private float endPosX = 0;
    private float endPosY = 0;
    private float oldPosX = 0;
    private float oldPosY = 0;

    private boolean oneTime = false;

    CoordinateHandler handler = new CoordinateHandler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_controll, container, false);
        final ImageView imageViewBall = ((ImageView) v.findViewById(R.id.ball));

        imageViewBall.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float dX = imageViewBall.getX();
                float dY = imageViewBall.getY();

                if(startPosY == 0 && startPosX == 0){
                    startPosY = dY;
                    startPosX = dX;
                }

                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        dX = imageViewBall.getX() - event.getX();
                        dY = imageViewBall.getY() - event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        imageViewBall.setX(event.getX()+dX);
                        imageViewBall.setY(event.getY()+dY);
                        break;
                    case MotionEvent.ACTION_UP:
                        oldPosX = endPosX;
                        oldPosY = endPosX;
                        endPosX = event.getX() + dX;
                        endPosY = event.getY() + dY;
                        imageViewBall.setX(endPosX);
                        imageViewBall.setY(endPosY);
                        oneTime = true;
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        Intent intent = new Intent(this.getContext(), ConnectionService.class);
        if (mBound == false) {
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }

        new Thread(handler).start();
        return v;
    }

    @Override
    public void onStart(){
        startPosX = getView().getLayoutParams().width / 2;
        startPosY = getView().getLayoutParams().height / 2;
        super.onStart();
    }

    private class CoordinateHandler implements Runnable{

        @Override
        public void run() {
            while (true){
                if (oldPosY != endPosY && oldPosX != endPosX && oneTime) {
                    float x = (startPosX - endPosX)/100;
                    float y = (endPosY - startPosY)/100;
                    oneTime = false;

                    while(mService == null || mService.getSocket() == null){

                    }
                    mService.connectionRetry();
                    while(mService == null || mService.getSocket() == null){

                    }
                    try {
                        OutputStreamWriter streamWriter = null;
                        streamWriter = new OutputStreamWriter(mService.getSocket().getOutputStream(), StandardCharsets.UTF_8);
                        streamWriter.write("SET Ko");
                        streamWriter.flush();
                        streamWriter.write(-y + ";" + x); //Koordinaten werden immer vertauscht
                        streamWriter.flush();
                    }catch(IOException e){
                        e.printStackTrace();
                    }

                }
            }
        }
    }

}
