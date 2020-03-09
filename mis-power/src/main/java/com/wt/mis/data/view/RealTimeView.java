package com.wt.mis.data.view;

import lombok.Data;

import java.util.Date;

@Data
public class RealTimeView {
    private Date callTime;
    private String callTimeStr;
    private String devType;
    private String devName;
    private String devParentType;
    private String devParentName;
    private String transformName;
    private double realDyDataA;
    private double realDyDataB;
    private double realDyDataC;
    private double realDlDataA;
    private double realDlDataB;
    private double realDlDataC;
    private String operationsTeamName;
}
