package com.wt.mis.sms.entity;

import lombok.Data;

import java.util.List;

@Data
public class JsonTreeModel {
    private  String id;
    private  String title;
    private List<JsonTreeModel> children;
    private int type;
}
