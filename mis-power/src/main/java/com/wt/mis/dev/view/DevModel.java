package com.wt.mis.dev.view;

import lombok.Data;

@Data
public class DevModel {
    /**
     * 设备名称
     */
    private String devName;

    /**
     * 设备id
     */
    private long devId;

    /**
     * 设备类型
     */
    private int devType;

    private String devParentName;
    private int devParentId;
    private int devParentType;

    /**
     * 所在班组Id
     */
    private long operationsTeam;
    /**
     * 所在班组名称
     */
    private String operationsTeamName;
}
