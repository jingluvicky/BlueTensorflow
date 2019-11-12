package com.albert.uaes.tensorflowlibrary.tf;

import com.albert.uaes.tensorflowlibrary.model.Node;

public class bodyBoundary {


/*
This inline function was automatically generated using DecisionTreeToCpp Converter

It takes feature vector as single argument:
feature_vector[0] - m
feature_vector[1] - a1
feature_vector[2] - a2
feature_vector[3] - a3
feature_vector[4] - a4
feature_vector[5] - a5
feature_vector[6] - 1m
feature_vector[7] - 2m
feature_vector[8] - 3m
feature_vector[9] - 14
feature_vector[10] - 24
feature_vector[11] - 34
feature_vector[12] - 15
feature_vector[13] - 25
feature_vector[14] - 35


It returns index of predicted class:
0 - 1
1 - 2
2 - 3


Simply include this file to your project and use it
*/

     public static int decision_tree6(Node[] nodes)

    {
        double[]feature_vector={
                nodes[0].rssi_filtered,nodes[1].rssi_filtered,nodes[2].rssi_filtered,nodes[3].rssi_filtered,
                nodes[4].rssi_filtered,nodes[5].rssi_filtered,
                nodes[1].rssi_filtered-nodes[0].rssi_filtered,nodes[2].rssi_filtered-nodes[0].rssi_filtered,nodes[3].rssi_filtered-nodes[0].rssi_filtered,
                nodes[1].rssi_filtered-nodes[4].rssi_filtered,nodes[2].rssi_filtered-nodes[4].rssi_filtered,nodes[3].rssi_filtered-nodes[4].rssi_filtered,
                nodes[1].rssi_filtered-nodes[5].rssi_filtered,nodes[2].rssi_filtered-nodes[5].rssi_filtered,nodes[3].rssi_filtered-nodes[5].rssi_filtered};
        if (feature_vector[9] <= 12.5) {
            if (feature_vector[6] <= 15.5) {
                if (feature_vector[4] <= 44.5) {
                    if (feature_vector[2] <= 54.5) {
                        if (feature_vector[3] <= 65.5) {
                            if (feature_vector[5] <= 51.5) {
                                return 0;
                            }
                            else {
                                return 1;
                            }
                        }
                        else {
                            if (feature_vector[13] <= 11.5) {
                                return 1;
                            }
                            else {
                                return 0;
                            }
                        }
                    }
                    else {
                        if (feature_vector[5] <= 43.0) {
                            if (feature_vector[10] <= 16.5) {
                                return 1;
                            }
                            else {
                                return 0;
                            }
                        }
                        else {
                            if (feature_vector[2] <= 55.5) {
                                return 1;
                            }
                            else {
                                return 1;
                            }
                        }
                    }
                }
                else {
                    return 1;
                }
            }
            else {
                if (feature_vector[13] <= 2.5) {
                    return 1;
                }
                else {
                    if (feature_vector[12] <= 3.5) {
                        if (feature_vector[3] <= 61.5) {
                            if (feature_vector[4] <= 45.0) {
                                return 0;
                            }
                            else {
                                return 1;
                            }
                        }
                        else {
                            if (feature_vector[2] <= 55.5) {
                                return 0;
                            }
                            else {
                                return 1;
                            }
                        }
                    }
                    else {
                        if (feature_vector[8] <= 12.0) {
                            return 1;
                        }
                        else {
                            if (feature_vector[11] <= 21.0) {
                                return 0;
                            }
                            else {
                                return 1;
                            }
                        }
                    }
                }
            }
        }
        else {
            if (feature_vector[8] <= 10.5) {
                if (feature_vector[14] <= 15.5) {
                    if (feature_vector[1] <= 76.5) {
                        if (feature_vector[4] <= 45.5) {
                            if (feature_vector[3] <= 49.5) {
                                return 1;
                            }
                            else {
                                return 0;
                            }
                        }
                        else {
                            if (feature_vector[11] <= 2.5) {
                                return 1;
                            }
                            else {
                                return 1;
                            }
                        }
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    if (feature_vector[9] <= 17.5) {
                        return 1;
                    }
                    else {
                        if (feature_vector[10] <= -2.5) {
                            if (feature_vector[4] <= 57.5) {
                                return 1;
                            }
                            else {
                                return 0;
                            }
                        }
                        else {
                            return 0;
                        }
                    }
                }
            }
            else {
                if (feature_vector[6] <= 15.5) {
                    if (feature_vector[9] <= 15.5) {
                        if (feature_vector[4] <= 50.5) {
                            if (feature_vector[4] <= 36.5) {
                                return 0;
                            }
                            else {
                                return 1;
                            }
                        }
                        else {
                            return 0;
                        }
                    }
                    else {
                        if (feature_vector[8] <= 22.5) {
                            if (feature_vector[7] <= -6.5) {
                                return 1;
                            }
                            else {
                                return 0;
                            }
                        }
                        else {
                            if (feature_vector[5] <= 40.5) {
                                return 1;
                            }
                            else {
                                return 0;
                            }
                        }
                    }
                }
                else {
                    if (feature_vector[8] <= 12.5) {
                        if (feature_vector[7] <= 15.0) {
                            if (feature_vector[10] <= -3.5) {
                                return 0;
                            }
                            else {
                                return 0;
                            }
                        }
                        else {
                            if (feature_vector[10] <= 14.5) {
                                return 0;
                            }
                            else {
                                return 1;
                            }
                        }
                    }
                    else {
                        if (feature_vector[12] <= 2.5) {
                            if (feature_vector[1] <= 52.5) {
                                return 0;
                            }
                            else {
                                return 1;
                            }
                        }
                        else {
                            if (feature_vector[10] <= -16.5) {
                                return 1;
                            }
                            else {
                                return 0;
                            }
                        }
                    }
                }
            }
        }
    }
}
