package com.wt.mis.event.view;

import lombok.Data;

import java.util.Date;

@Data
public class PowerOutageView {

    private Date occurTime;
    private String occurTimeStr;
    private String devTypeName;
    private String devName;
    private String devParentType;
    private String devParentName;
    private String transformName;
    private String powerStatus;
    private String phaseStatus;
    private String voltageStatusA;
    private String voltageStatusB;
    private String voltageStatusC;
    private String operationsTeamName;
    private long transformId;
    private int devType;
    private long devId;
}
