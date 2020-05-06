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
    private Double realDyDataA;
    private Double realDyDataB;
    private Double realDyDataC;
    private Double realDlDataA;
    private Double realDlDataB;
    private Double realDlDataC;
    private String operationsTeamName;
}
