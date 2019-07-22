package com.albert.uaes.bluetensorflow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

public class ControlFragment extends BaseFragment {

    private SendCMD mysendCMD;
    private ImageView btn_Lock;
    private ImageView btn_UnLock;
    private ImageView btn_Panic;
    private ImageView btn_trunkunlock;
    private Switch switch_record;
    public boolean isRecord=false;

    public static ControlFragment newInstance() {

        Bundle args = new Bundle();

        ControlFragment fragment = new ControlFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        mysendCMD = (ControlFragment.SendCMD)getActivity();
        switch_record=view.findViewById(R.id.switch_record);
        btn_Lock = view.findViewById(R.id.btn_Lock);
        btn_UnLock = view.findViewById(R.id.btn_UnLock);
        btn_Panic = view.findViewById(R.id.btn_Panic);
        btn_trunkunlock = view.findViewById(R.id.btn_trunkunlock);

        btn_Lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setTitle("")
                        .setMessage("Are you sure to lock the car?")
                        .setPositiveButton("YES",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "Lock Cmd", Toast.LENGTH_SHORT).show();
                                mysendCMD.sendControlCMD(1);
                            }
                        }).setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                }).show();
            }
        });
        btn_UnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setTitle("")
                        .setMessage("Are you sure to unlock the car?")
                        .setPositiveButton("YES",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                Toast.makeText(getActivity(), "Unlock Cmd", Toast.LENGTH_SHORT).show();
                                mysendCMD.sendControlCMD(2);
                            }
                        }).setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();//在按键响应事件中显示此对话框
            }
        });
        btn_Panic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setTitle("")
                        .setMessage("Panic?")
                        .setPositiveButton("YES",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "Panic Cmd", Toast.LENGTH_SHORT).show();
                                mysendCMD.sendControlCMD(3);
                            }
                        }).setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }

                }).show();//在按键响应事件中显示此对话框
            }
        });
        btn_trunkunlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setTitle("")

                        .setMessage("Are you sure to unlock the trunk?")

                        .setPositiveButton("YES",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                Toast.makeText(getActivity(), "Trunk unlock Cmd", Toast.LENGTH_SHORT).show();
                                mysendCMD.sendControlCMD(4);
                            }
                        }).setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }

                }).show();//在按键响应事件中显示此对话框
            }
        });
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_control;
    }


    public interface SendCMD{

        public void sendControlCMD(int cmd);//1 Lock 2 UnLock 3 Panic

    }
}
