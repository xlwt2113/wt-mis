package com.wt.mis.data.view;

import lombok.Data;

import java.util.Date;

@Data
public class FreezeView {

    private Date callTime;
    private Date freezeTime;
    private String freezeTimeStr;
    private String devType;
    private String devName;
    private String devParentType;
    private String devParentName;
    private String transformName;
    private Double freezeDataA;
    private Double freezeDataB;
    private Double freezeDataC;
    private String operationsTeamName;
}
