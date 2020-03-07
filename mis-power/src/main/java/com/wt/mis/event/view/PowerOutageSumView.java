package com.wt.mis.event.view;

import lombok.Data;

@Data
public class PowerOutageSumView {
    private String devType;
    private String devName;
    private int cnt;
}
