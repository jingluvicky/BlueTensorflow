package com.albert.uaes.tensorflowlibrary.tf;

import com.albert.uaes.tensorflowlibrary.model.Node;


public class LUTprediction_top {
    private int sintaParkCaliTable[][]={

            {     0	,    68	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,     0	,    58	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,     0	,   255	,     0	,    56	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,     0	,   255	,     0	,   255	,     0	,    57	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,    53	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,    58	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,    56	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,    87	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,    64	,    51	,    80	,     0	,    62	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    61	,    87	,    67	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,    89	,    53	,    58	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,    73	,     0	,   255	,    57	,   255	,    57	,   255	,    75	,    80	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,    71	,     0	,   255	,    40	,    58	,    50	,   255	,     0	,    72	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    52	,   255	,    54	,    59	,    57	,   255	,    48	,    66	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,    77	,    54	,    59	,    40	,   255	,    66	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    74	,   255	,     0	,   255	,     0	,   255	,    48	,    58	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    69	,    74	,    71	,   255	,    47	,    58	,     0	,    76	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,    78	,   255	,     0	,    63	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,    76	,     0	,   255	,    40	,    57	,    54	,    75	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,    69	,    67	,   255	,    69	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,    65	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,    82	,   255	,     0	,    68	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    68	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,    65	,    55	,    60	,     0	,    77	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    66	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,    79	,   255	,    52	,    62	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    57	,   255	,     0	,    66	,    61	,    71	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,    58	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,     0	,   255	,     0	,    68	,    69	,   255	,     0	,    69	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,    75	,     0	,   255	,     0	,   255	,     0	,   255	,    78	,    89	,     0	,   255	,     0	,    69	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,    72	,     0	,   255	,     0	,   255	,    57	,    62	,     0	,    58	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    40	,    70	,    49	,   255	,    59	,    83	,     0	,    66	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,     0	,   255	,    51	,    64	,    51	,   255	,     0	,    59	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,    65	,    70	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,    82	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    66	,   255	,     0	,   255	,     0	,    64	,    72	,    77	,     0	,    78	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,     0	,   255	,     0	,    58	,     0	,   255	,    75	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,    76	,   255	,     0	,    60	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    49	,   255	,    59	,   255	,    67	,    77	,    48	,    62	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    43	,    76	,     0	,   255	,    59	,   255	,    54	,    59	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,    49	,    86	,     0	,    62	,     0	,   255	,     0	,   255	,     0	,   255	,    63	,    68	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    49	,   255	,     0	,    59	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,    65	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,    86	,    72	,    77	,     0	,   255	,     0	,   255	,     0	,   255	,    77	,    85	,    79	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    58	,   255	,    66	,    77	,     0	,    60	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,    72	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    57	,   255	,     0	,    59	,    72	,   255	,     0	,    79	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,    75	,     0	,   255	,     0	,   255	,    77	,   255	,    53	,    58	,     0	,    75	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    40	,    72	,    80	,   255	,    60	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,    63	,    76	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    74	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,    66	,    72	,    83	,   255	,    65	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,    76	,    79	,   255	,     0	,    73	,    76	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,     0	,   255	,    63	,   255	,    76	,   255	,    50	,    56	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,   255	,    64	,   255	,    55	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,    52	,    57	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    54	,    75	,     0	,   255	,    61	,   255	,     0	,    67	,    50	,    56	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    82	,   255	,    70	,   255	,     0	,    88	,    78	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    46	,    88	,    81	,   255	,     0	,    75	,    76	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    59	,   255	,     0	,   255	,     0	,   255	,    76	,   255	,     0	,    57	,    42	,    86	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    59	,    71	,    67	,    79	,     0	,    64	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    69	,    83	,    40	,    62	,     0	,   255	,     0	,   255	,     0	,   255	,    62	,   255	,     0	,    58	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    64	,    88	,     0	,   255	,     0	,   255	,    68	,    73	,    80	,   255	,     0	,    65	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {     0	,    75	,    46	,    69	,     0	,    79	,    80	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4},
            {    59	,    77	,    78	,   255	,    44	,   255	,     0	,   255	,     0	,   255	,     0	,   255	,     0	,    64	,0	,   255	,0	,   255	,0	,   255	,0	,   255	,4}
    };

    private int sintaPECaliTable[][] = {

            {0,48,0,255,0,255,0,255,0,255,0,255,0,255,0,255,0,255,0,255,0},
            {0,255,0,255,0,255,0,255,0,255,0,255,0,255,0,47,0,255,0,255,0},
            {0,255,0,255,0,255,0,255,0,255,0,255,0,255,0,255,0,47,0,255,0},
            {0,255,0,255,0,255,0,255,0,255,0,255,0,255,0,255,0,255,0,40,0},
            {0,65,0,255,0,255,0,255,0,60,0,255,0,255,0,60,0,60,0,47,0},
            {     0    ,    59    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,     0    ,    45    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255,0,255,0,255,0,255,1},
            {     0    ,   255    ,     0    ,   255    ,     0    ,    49    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,    49    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,    44    ,     0    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,    47    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,    47    ,0,255,0,255,0,255,1},
            {    45    ,    77    ,     0    ,    50    ,     0    ,   255    ,    42    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,    78    ,   255    ,     0    ,    86    ,     0    ,    53    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,    73    ,     0    ,   255    ,     0    ,   255    ,    63    ,   255    ,     0    ,    50    ,     0    ,    56    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {    66    ,   255    ,     0    ,   255    ,    67    ,   255    ,     0    ,   255    ,    44    ,    49    ,     0    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,    81    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,    79    ,     0    ,    50    ,    41    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,    47    ,    52    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,    60    ,0,255,0,255,0,255,1},
            {    54    ,   255    ,     0    ,   255    ,     0    ,   255    ,    46    ,    52    ,    50    ,   255    ,    56    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {    41    ,    75    ,     0    ,    55    ,    69    ,   255    ,    77    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,    48    ,    53    ,     0    ,    54    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,    53    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,    68    ,   255    ,     0    ,    50    ,    55    ,    79    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {    50    ,   255    ,     0    ,    54    ,    64    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,    47    ,    57    ,0,255,0,255,0,255,1},
            {     0    ,    61    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,    64    ,    82    ,     0    ,    80    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,    78    ,   255    ,     0    ,    70    ,     0    ,    52    ,0,255,0,255,0,255,1},
            {    51    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,    46    ,    51    ,     0    ,    85    ,0,255,0,255,0,255,1},
            {     0    ,    61    ,     0    ,   255    ,     0    ,    55    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,    51    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,    53    ,     0    ,    73    ,     0    ,    59    ,0,255,0,255,0,255,1},
            {    60    ,    65    ,    46    ,   255    ,     0    ,    51    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,    42    ,   255    ,0,255,0,255,0,255,1},
            {    69    ,   255    ,     0    ,   255    ,    40    ,    54    ,     0    ,    74    ,    49    ,    54    ,     0    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,    51    ,     0    ,   255    ,    55    ,    60    ,0,255,0,255,0,255,1},
            {    53    ,    72    ,     0    ,   255    ,     0    ,   255    ,    74    ,   255    ,     0    ,    52    ,     0    ,    87    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,    68    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,    51    ,0,255,0,255,0,255,1},
            {    50    ,    73    ,    47    ,    52    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,    71    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,    74    ,    43    ,    48    ,0,255,0,255,0,255,1},
            {     0    ,   255    ,     0    ,    55    ,     0    ,    52    ,     0    ,    57    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {    67    ,    72    ,     0    ,    53    ,    47    ,   255    ,    67    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {    52    ,   255    ,     0    ,    49    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,    85    ,    46    ,   255    ,0,255,0,255,0,255,1},
            {     0    ,    70    ,     0    ,    59    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,    51    ,    70    ,     0    ,    83    ,0,255,0,255,0,255,1},
            {    75    ,   255    ,    75    ,    80    ,     0    ,    56    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {    48    ,    68    ,     0    ,   255    ,     0    ,   255    ,    78    ,   255    ,    73    ,   255    ,     0    ,   255    ,     0    ,   255    ,0,255,0,255,0,255,1},
            {    62    ,    85    ,    81    ,   255    ,     0    ,   255    ,     0    ,   255    ,     0    ,   255    ,    58    ,   255    ,    81    ,   255    ,0,255,0,255,0,255,1},
            {    79    ,   255    ,    63    ,    85    ,    40    ,    80    ,    65    ,   255    ,    47    ,    87    ,    57    ,    76    ,    54    ,   255,0,255,0,255,0,255,3},
            {    78    ,    83    ,    57    ,    80    ,    63    ,    70    ,    47    ,    75    ,    74    ,   255    ,    44    ,    84    ,    56    ,   255,0,255,0,255,0,255,3},
            {    73    ,    80    ,    53    ,    75    ,    50    ,    76    ,    59    ,   255    ,    44    ,    69    ,    71    ,   255    ,    53    ,    85,0,255,0,255,0,255,3},
            {    64    ,   255    ,    59    ,    82    ,    71    ,   255    ,    62    ,    86    ,    62    ,    67    ,    57    ,   255    ,    57    ,    82,0,255,0,255,0,255,3},
            {    58    ,   255    ,    62    ,   255    ,    44    ,    84    ,    73    ,   255    ,    62    ,   255    ,    61    ,   255    ,    47    ,    87,0,255,0,255,0,255,3},
            {    68    ,   255    ,    58    ,    68    ,    40    ,    63    ,    60    ,   255    ,    63    ,   255    ,    56    ,    87    ,    75    ,    86,0,255,0,255,0,255,3},
            {    56    ,    88    ,    66    ,   255    ,    56    ,    72    ,    59    ,    84    ,    63    ,    68    ,    58    ,    77    ,    64    ,   255,0,255,0,255,0,255,3},
            {    67    ,    84    ,    74    ,   255    ,    55    ,   255    ,    73    ,    78    ,    58    ,    69    ,    58    ,    83    ,    60    ,   255,0,255,0,255,0,255,3},
            {    53    ,   255    ,    58    ,    84    ,    73    ,   255    ,    66    ,   255    ,    56    ,    83    ,    62    ,   255    ,    68    ,    73,0,255,0,255,0,255,3},
            {    58    ,   255    ,    61    ,    80    ,    55    ,    76    ,    48    ,    73    ,    62    ,    80    ,    56    ,    83    ,    40    ,    66,0,255,0,255,0,255,3},
            {    80    ,   255    ,    56    ,    65    ,    40    ,    75    ,    54    ,   255    ,    69    ,   255    ,    40    ,    80    ,    53    ,   255,0,255,0,255,0,255,3},
            {    66    ,   255    ,    63    ,    80    ,    61    ,    70    ,    59    ,   255    ,    61    ,   255    ,    57    ,    85    ,    67    ,    80,0,255,0,255,0,255,3},
            {    73    ,   255    ,    66    ,   255    ,    40    ,    80    ,    62    ,   255    ,    57    ,   255    ,    40    ,    69    ,    58    ,    69,0,255,0,255,0,255,3},
            {    63    ,   255    ,    80    ,   255    ,    65    ,   255    ,    56    ,   255    ,    49    ,    89    ,    58    ,    80    ,    69    ,    75,0,255,0,255,0,255,3},
            {    65    ,   255    ,    64    ,    89    ,    60    ,   255    ,    40    ,    80    ,    58    ,    87    ,    46    ,    86    ,    62    ,    67,0,255,0,255,0,255,3},
            {    75    ,   255    ,    75    ,   255    ,    42    ,    82    ,    69    ,   255    ,    55    ,    64    ,    67    ,    88    ,    53    ,   255,0,255,0,255,0,255,3},
            {    64    ,   255    ,    63    ,    76    ,    71    ,   255    ,    73    ,    88    ,    67    ,    82    ,    60    ,    83    ,    54    ,   255    ,0,255,0,255,0,255,3}};


    private int sintaPSCaliTable[][] = {

            {0,255,5,255,0,255,0,255,0,255,0,255,0,255,0,255,3,255,0,255,0,255,6,255,0,255,0,255,0,255,0,-255,255,-255,255,-255,255,-255,255,-255,255,0},

            {0,255,0,255,-6,255,0,255,0,255,0,255,0,255,-3,10,0,255,0,255,0,255,0,255,-7,10,0,255,0,255,0,-255,255,-255,255,-255,255,-255,255,-255,255,0 },
            {-10,255,-10,255,-255,0,-10,255,-10,255,-10,255,-10,255,-10,255,-10,255,-10,255,-10,255,-10,255,-10,255,-10,255,-10,255,-10,255,-10,255,10,255,0,255,0,255,0}
    };
    private int Incarindex [][]=  {

            { 2, 0 }, { 3, 0 }, { 4, 0 }, { 5, 0 }, { 6, 0 },

            { 2, 7 }, { 3, 7 }, { 4, 7 }, { 5, 7 }, { 6, 7 },

            { 2, 8 }, { 3, 8 }, { 4, 8 }, { 5, 8 }, { 6, 8 },
            {2,9},{3,9},{4,9},{5,9},{6,9}

    };



    private int PS_POINT_NUM=3,PE_NODE_NUM=10,PE_POINT_NUM=59,PS_NODE_NUM=20,PARK_POINT_NUM=53,PARK_NODE_NUM=10,OFFSET=0;

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
                zone = sintaPSCaliTable[point_index][40];
                value = intaRssi[Incarindex[node_index][0]] - intaRssi[Incarindex[node_index][1]];
                if ((zone != -1) && (value >= min) && (value <= max))
                {
                    if (node_index == 19)//条件无效，是否满足之前的条件
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

    private int PE_s32CaliFunction(int[] intaRssi)
    {
        int min;
        int max;
        int index = -1;
        int zone = -1;
        for (int point_index = 0; point_index < PE_POINT_NUM; point_index++)
        {
            for (int node_index = 0; node_index < PE_NODE_NUM; node_index++)
            {
                min = sintaPECaliTable[point_index][2 * node_index];
                max = sintaPECaliTable[point_index][2 * node_index + 1];
                zone = sintaPECaliTable[point_index][ 20];
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

    private int Park_s32CaliFunction(int[] intaRssi)
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
    }

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

    public int PEPS_s32CaliFunction(Node[] nodes,int pocketState)
    {
        int[]rssi=new int[PE_NODE_NUM];
        for (int i=0;i<PE_NODE_NUM;i++){
            rssi[i]=(int)nodes[i].rssi_filtered-pocketState*3;
        }
      ///  int index=Park_s32CaliFunction(rssi);
        //if (index==4){
        int index = PS_s32CaliFunction(rssi);
        int zone = -1;
        if (index != -1)
        {
            zone = sintaPSCaliTable[index][40];
        }
        else
        {
            index=Park_s32CaliFunction(rssi);
            if (index!=-1){

            index = PE_s32CaliFunction(rssi);
            if (index != -1)
            {
                zone = sintaPECaliTable[index][20];
            }
            else
            {
                zone=4;
            }
            }
        }
        return zone;
    }

}
