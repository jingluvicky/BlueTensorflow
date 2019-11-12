package com.albert.uaes.bluetensorflow;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.albert.uaes.bluetensorflow.net.BleDisconnectEvent;
import com.albert.uaes.bluetensorflow.service.MyService;
import com.albert.uaes.bluetensorflow.utils.FileUtils;
import com.albert.uaes.bluetensorflow.utils.RxPermissionUtils;
import com.albert.uaes.bluetensorflow.utils.ThreadPoolManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SensorFragment extends BaseFragment {
    private int BLE_OPENED =  0;
    public MyService myService;
    private final static int UPDATE_UI = 0;
    public static boolean isRecord=false;
    public static String curTime;
    public static File file;
    public static FileOutputStream fos;
    public static int recordtime;
    private Runnable updateUiRunnable;
    private EditText editText ;
    private TextView txt_name,txt_motion,txt_location,txt_position,txt_model,txt_minite,txt_second,txt_remark;
    public  static Button btn_refresh,btn_begin;
    private Spinner spinner_name,spinner_location,spinner_motion,spinner_position,spinner_model;
    public static String name,location,motion,position,model,remark;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_UI:
                    updateUi();
                    break;
            }
        }
    };

    public static SensorFragment newInstance() {

        Bundle args = new Bundle();

        SensorFragment fragment = new SensorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    protected void init(View view, Bundle savedInstanceState) {
        doUpdateThread();
        EventBus.getDefault().register(this);
        txt_name=view.findViewById(R.id.txt_name);
        txt_location=view.findViewById(R.id.txt_location);
        txt_position=view.findViewById(R.id.txt_position);
        txt_motion=view.findViewById(R.id.txt_motion);
        txt_model=view.findViewById(R.id.txt_model);
        spinner_name=view.findViewById(R.id.spinner_name);
        spinner_location=view.findViewById(R.id.spinner_location);
        spinner_position=view.findViewById(R.id.spinner_position);
        spinner_motion=view.findViewById(R.id.spinner_motion);
        spinner_model=view.findViewById(R.id.spinner_model);
        btn_refresh=view.findViewById(R.id.btn_refresh);
        btn_begin=view.findViewById(R.id.btn_begin);
        txt_minite=view.findViewById(R.id.txt_minite);
        txt_second=view.findViewById(R.id.txt_second);
        txt_remark=view.findViewById(R.id.txt_remark);
        btn_refresh.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                spinner_name.setSelection(0);
                spinner_location.setSelection(0);
                spinner_model.setSelection(0);
                spinner_position.setSelection(0);
                spinner_motion.setSelection(0);
                txt_remark.setText(" ");
                txt_minite.setText("0");
                txt_second.setText("0");

            }
        });
        btn_begin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int minite=Integer.valueOf(txt_minite.getEditableText().toString());
                int second=Integer.valueOf(txt_second.getEditableText().toString());
                if (isRecord){
                    isRecord=false;
                    ScanFragment.aSwitch.setChecked(false);
                    btn_begin.setBackgroundResource(R.drawable.begin);
                }else{
                    remark=txt_remark.getEditableText().toString();
                    recordtime=20*(Integer.valueOf(txt_minite.getEditableText().toString())*60+Integer.valueOf(txt_second.getEditableText().toString()));
                    SimpleDateFormat format = new SimpleDateFormat("MM.dd HH_mm_ss");
                    curTime=format.format(new Date());
                    ScanFragment.aSwitch.setChecked(true);
                    try {
                        file = FileUtils.getInstance().createFile(SensorFragment.curTime +"_"+name+ ".txt");
                        fos = new FileOutputStream(file);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    btn_begin.setBackgroundResource(R.drawable.pause);
                  //  BaseFragment.myService.createFile(name);
                    isRecord=true;
                }
            }
        });
        spinner_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                int pro = spinner.getSelectedItemPosition();
                int arrayLength=getResources().getStringArray(R.array.name_array).length;
                if(pro==arrayLength-1) {

                    final EditText editText=new EditText(getContext());
                    AlertDialog.Builder bdr=new AlertDialog.Builder(getContext());

                    bdr.
                            setMessage("请输入你的名字")
                            .setView(editText).
                            setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    txt_name.setText(editText.getText().toString());
                                    name=editText.getText().toString();

                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();

                }
                else{
                    txt_name.setText(spinner.getSelectedItem().toString());
                    name=spinner.getSelectedItem().toString();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner_location.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                int pro = spinner.getSelectedItemPosition();
                int arrayLength=getResources().getStringArray(R.array.location_array).length;
                if(pro==arrayLength-1) {

                    final EditText editText2=new EditText(getContext());
                    AlertDialog.Builder bdr=new AlertDialog.Builder(getContext());

                    bdr.
                            setMessage("请输入手机位置")
                            .setView(editText2).
                            setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    txt_location.setText(editText2.getText().toString());
                                    location=editText2.getText().toString();

                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();

                }
                else{
                    txt_location.setText(spinner.getSelectedItem().toString());
                    location=spinner.getSelectedItem().toString();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner_model.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                int pro = spinner.getSelectedItemPosition();
                int arrayLength=getResources().getStringArray(R.array.model_array).length;
                if(pro==arrayLength-1) {

                    final EditText editText3=new EditText(getContext());
                    AlertDialog.Builder bdr=new AlertDialog.Builder(getContext());

                    bdr.
                            setMessage("请输入手机型号")
                            .setView(editText3).
                            setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    txt_model.setText(editText3.getText().toString());
                                    model=editText3.getText().toString();

                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();

                }
                else{
                    txt_model.setText(spinner.getSelectedItem().toString());
                    model=spinner.getSelectedItem().toString();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner_position.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position1, long id) {
                Spinner spinner = (Spinner)parent;
                int pro = spinner.getSelectedItemPosition();
                int arrayLength=getResources().getStringArray(R.array.position_array).length;
                if(pro==arrayLength-1) {

                    final EditText editText4=new EditText(getContext());
                    AlertDialog.Builder bdr=new AlertDialog.Builder(getContext());

                    bdr.
                            setMessage("请输入测试地点")
                            .setView(editText4).
                            setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    txt_position.setText(editText4.getText().toString());
                                    position=editText4.getText().toString();

                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();

                }
                else{
                    txt_position.setText(spinner.getSelectedItem().toString());
                    position=spinner.getSelectedItem().toString();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner_motion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                int pro = spinner.getSelectedItemPosition();
                int arrayLength=getResources().getStringArray(R.array.motion_array).length;
                if(pro==arrayLength-1) {


                    AlertDialog.Builder bdr=new AlertDialog.Builder(getContext());
                    final EditText editText5=new EditText(getContext());
                    bdr.
                            setMessage("请输入运动状态")
                            .setView(editText5).
                            setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    txt_motion.setText(editText5.getText().toString());
                                    motion=editText5.getText().toString();

                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();

                }
                else{
                    txt_motion.setText(spinner.getSelectedItem().toString());
                    motion=spinner.getSelectedItem().toString();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }




    private void doUpdateThread() {
        updateUiRunnable = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(UPDATE_UI);

            }
        };
        ThreadPoolManager.getInstance(ThreadPoolManager.Scheduled_ThreadPool_Executor_Type).scheduleAtFixedRate(updateUiRunnable,0,20,TimeUnit.MILLISECONDS);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(BleDisconnectEvent bleDisconnectEvent) {
        if (bleDisconnectEvent.isConnect()){
            doUpdateThread();
        }else {

        }
    }
    private void updateUi(){
        if (isRecord==true){
            btn_begin.setBackgroundResource(R.drawable.pause);
        }else{
            btn_begin.setBackgroundResource(R.drawable.begin);
            //ScanFragment.aSwitch.setChecked(false);
        }
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_sensor;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
