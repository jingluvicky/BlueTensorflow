package com.albert.uaes.tensorflowlibrary.tf;

import android.content.res.AssetManager;
import android.util.Log;

import com.albert.uaes.tensorflowlibrary.model.Node;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class PredictionTF_zone {
    private static final String TAG = "PredictionTF_zone";
    //设置模型输入/输出节点的数据维度
    private static final int window = 1;
    private static final int sensor_number=4;
    //模型中输入变量的名称
    private static final String input_name = "x_input";
    //模型中输出变量的名称
    private static final String outputName = "output";
    private static final String modePath="file:///android_asset/HARModel190220003.pb";
    TensorFlowInferenceInterface inferenceInterface;


    private float[][]storage=new float[window][sensor_number];
    static {
        //加载libtensorflow_inference.so库文件
        System.loadLibrary("tensorflow_inference");
        Log.e(TAG,"libtensorflow_inference.so库加载成功");
    }


    public PredictionTF_zone(AssetManager assetManager) {
        //初始化TensorFlowInferenceInterface对象
        inferenceInterface = new TensorFlowInferenceInterface(assetManager,modePath);
        Log.e(TAG,"TensoFlow模型文件加载成功");

    }

    /**
     *  利用训练好的TensoFlow模型预测结果
     *
     * @return 返回预测结果，int数组
     */

    public float[] getPredict() {

        float []inputdata2=new float[window*sensor_number];

        for (int j=0;j<window;j++) {
            for (int i = 0; i < sensor_number; i++) {
                if (storage==null){
                    inputdata2[j * sensor_number + i] = 0;
                }else{
                    inputdata2[j * sensor_number + i] = storage[j][i];
                }
            }
        }
        //将数据feed给tensorflow的输入节点

        inferenceInterface.feed(input_name, inputdata2,1,window,sensor_number);
        //运行tensorflow
        String[] outputNames = new String[] {outputName};
        inferenceInterface.run(outputNames);
        ///获取输出节点的输出信息
        //用于存储模型的输出数据
        float[] outputs = new float[3];
        inferenceInterface.fetch(outputName, outputs);

        return outputs;
    }

    public void TFclose(){
        inferenceInterface.close();
    }


    public void Storage(Node[] Nodes){

        int Window=window;
        for (int i=0;i<Window-1;i++){
            for(int j=0;j<sensor_number;j++){
                storage[i][j]=storage[i+1][j];
            }

        }
        float maxnum=100;
        float maxnum2=100;
        for (int i=1;i<=9;i++){

            if (i!=0 && Nodes[i].rssi_filtered<maxnum){
                maxnum2=maxnum;
                maxnum=(float)Nodes[i].rssi_filtered;
            }
        }
        storage[window-1][0]=(float)Nodes[0].rssi_filtered;
        storage[window-1][1]=maxnum;
        storage[window-1][2]=maxnum2;

        double[] temp={Nodes[1].rssi_filtered,Nodes[2].rssi_filtered,Nodes[3].rssi_filtered,Nodes[4].rssi_filtered,Nodes[5].rssi_filtered,Nodes[6].rssi_filtered};
        storage[window-1][3]=(float)getStandardDeviation(temp);


    }
    public double getAverage(double[] arr) {
        double sum = 0;
        int number = arr.length;
        for (int i = 0; i < number; i++) {
            sum += arr[i];
        }
        return sum / number;
    }

    public double getStandardDeviation(double[] arr) {
        double sum = 0;
        int number = arr.length;
        //获取平均值
        double avgValue = getAverage(arr);
        for (int i = 0; i < number; i++) {
            sum += Math.pow((arr[i] - avgValue), 2);
        }

        return Math.sqrt((sum / (number - 1)));
    }
}

