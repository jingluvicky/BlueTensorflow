package com.albert.uaes.tensorflowlibrary.tf;

import com.albert.uaes.tensorflowlibrary.model.Node;


public class LUTprediction_top {
    //private int sintaParkCaliTable[][]=LUTprediction_top_data.sintaParkCaliTable;
    private int sintaPECaliTable_inPocket[][] = LUTprediction_top_data.sintaPECaliTable_inpocket;

    private int sintaPECaliTable[][] = LUTprediction_top_data.sintaPECaliTable;

    private int sintaPSCaliTable[][] = LUTprediction_top_data.sintaPSCaliTable;

    private int Incarindex [][]= LUTprediction_top_data.Incarindex;

    private int sintaPECaliTable_2scenario[][]=LUTprediction_top_data.sintaPECaliTable_2scenario;

    private int PS_POINT_NUM=LUTprediction_top_data.PS_POINT_NUM,
            PE_NODE_NUM=LUTprediction_top_data.PE_NODE_NUM,
            PE_POINT_NUM=LUTprediction_top_data.PE_POINT_NUM,
            PS_NODE_NUM=LUTprediction_top_data.PS_NODE_NUM,

    PARK_POINT_NUM=LUTprediction_top_data.PARK_POINT_NUM,
            PARK_NODE_NUM=LUTprediction_top_data.PARK_NODE_NUM,
            OFFSET=0;

    public int PS_s32CaliFunction(int[] intaRssi)
    {

        int min;
        int max;
        int index = -1;
        int zone = 2;
        int value = 0;
        for (int point_index = 0; point_index < PS_POINT_NUM; point_index++)
        {
            for (int node_index = 0; node_index < PS_NODE_NUM; node_index++)
            {
                min = sintaPSCaliTable[point_index][ 2 * node_index];
                max = sintaPSCaliTable[point_index][2 * node_index + 1];
                zone = sintaPSCaliTable[point_index][PS_NODE_NUM*2];
                if (Incarindex[node_index][0]!=Incarindex[node_index][1]) {
                    value = intaRssi[Incarindex[node_index][0]] - intaRssi[Incarindex[node_index][1]];
                }else{
                    value = intaRssi[Incarindex[node_index][0]];
                }
                if ((zone != -1) && (value >= min) && (value <= max))
                {
                    if (node_index == PS_NODE_NUM-1)//条件无效，是否满足之前的条件
                    {
                        index = point_index;
                    }
                    continue;
                }
                else
                {
                    break;
                }

            }
            if (index != -1)
            {
                break;
            }
        }
        return index;
    }

    /*
     * name     : PE_s32CaliFunction
     * function : Locate the Key outside the car
     * parameter: array of Rssi for master and anchors
     * return   :
     *       -1: Not Found
     *       0 : In the PS Zone
     *       1 : In the UnLock Zone
     *       2 ：In the Buffer Zone
     *           3 : In the Lock Zone
     */

    private int PE_s32CaliFunction(int[] intaRssi,int curEarState)
    {
        int min;
        int max;
        int index = -1;
        int zone = -1;
        int[][]temp;
        if (curEarState==1)
            temp=sintaPECaliTable_2scenario;
        else
            temp=sintaPECaliTable;
        for (int point_index = 0; point_index < PE_POINT_NUM; point_index++)
        {
            for (int node_index = 0; node_index < PE_NODE_NUM; node_index++)
            {
                min = temp[point_index][2 * node_index];
                max = temp[point_index][2 * node_index + 1];
                zone = temp[point_index][ PE_NODE_NUM*2];
                if ((zone!=-1) && (intaRssi[node_index] >= min) && (intaRssi[node_index] <= max))
                {
                    if (node_index == 9)//条件无效，是否满足之前的条件
                    {
                        index = point_index;
                    }
                    continue;
                }
                else
                {
                    break;
                }

            }
            if (index != -1)
            {
                break;
            }
        }
        return index;
    }

/*    private int Park_s32CaliFunction(int[] intaRssi)
    {
        int min;
        int max;
        int index = -1;
        int zone = -1;
        for (int point_index = 0; point_index < PARK_POINT_NUM; point_index++)
        {
            for (int node_index = 0; node_index < PARK_NODE_NUM; node_index++)
            {
                min = sintaParkCaliTable[point_index][2 * node_index];
                max = sintaParkCaliTable[point_index][2 * node_index + 1];
                zone = sintaParkCaliTable[point_index][ 20];
                if ((zone!=-1) && (intaRssi[node_index] >= min) && (intaRssi[node_index] <= max))
                {
                    if (node_index == 9)//条件无效，是否满足之前的条件
                    {
                        index = point_index;
                    }
                    continue;
                }
                else
                {
                    break;
                }

            }
            if (index != -1)
            {
                break;
            }
        }
        return index;
    }*/

    /*
     * name     : PEPS_s32CaliFunction
     * function : Locate the Key
     * parameter: array of Rssi for master and anchors
     * return   :
     *       -1: Not Found
     *       0 : In the PS Zone
     *       1 : In the UnLock Zone
     *       2 ：In the Buffer Zone
     *           3 : In the Lock Zone
     */

    public int PEPS_s32CaliFunction(Node[] nodes,int curEarState)
    {
        int[]rssi=new int[PE_NODE_NUM];
        for (int i=0;i<PE_NODE_NUM;i++){
            rssi[i]=(int)nodes[i].rssi_filtered;
        }
      ///  int index=Park_s32CaliFunction(rssi);
        //if (index==4){
        int index = PS_s32CaliFunction(rssi);
        int zone = -1;
        if (index != -1)
        {
            zone = sintaPSCaliTable[index][PS_NODE_NUM*2];
        }
        else
        {
            //index=Park_s32CaliFunction(rssi);
            //if (index!=-1){

            index = PE_s32CaliFunction(rssi,curEarState);
            if (index != -1)
            {
               if (curEarState==1)
                   zone=sintaPECaliTable_2scenario[index][PE_NODE_NUM*2];
               else
                    zone=sintaPECaliTable[index][PE_NODE_NUM*2];
            }
            else
            {
                zone=-1;
            }
            //}
        }
        return zone;
    }

}
