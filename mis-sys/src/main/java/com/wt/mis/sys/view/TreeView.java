package com.wt.mis.sys.view;

import lombok.Data;

import java.util.List;

@Data
public class TreeView {

    private String id;
    private String title;
    private boolean spread;
    private String type;
    private List<TreeView> children;

    public TreeView(String id,String title,boolean spread,String type){
        this.id = id;
        this.title = title;
        this.spread = spread;
        this.type = type;
    }

    public TreeView(String id,String title,boolean spread){
        this.id = id;
        this.title = title;
        this.spread = spread;
    }

    public TreeView(String id,String title){
        this.id = id;
        this.title = title;
    }
}
