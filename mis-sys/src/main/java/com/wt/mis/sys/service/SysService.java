package com.wt.mis.sys.service;

import com.wt.mis.sys.entity.Dict;
import com.wt.mis.sys.entity.DictItem;
import com.wt.mis.sys.entity.UploadFiles;

import java.util.List;

public interface SysService {

    public List<UploadFiles> getUploadFilesByIds(List<Long> ids);

    public String getRegisterValue(String itemName);

    /**
     * 根据字典名称获取所有字典项目
     * @param dictName
     * @return
     */
    public List<DictItem>  getDictItems(String dictName);

    /**
     * 获取所有在用字典项
     * @return
     */
    public List<Dict> getAllDict();
}
