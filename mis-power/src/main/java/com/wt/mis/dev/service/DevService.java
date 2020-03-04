package com.wt.mis.dev.service;

import com.wt.mis.dev.view.DevModel;
import com.wt.mis.dev.view.SelectOption;

import java.util.List;

public interface DevService {

    /**
     * 根据设备ID及类型获取设备的基本信息
     * @param devId
     * @param devType
     * @return
     */
    DevModel getDevModel(long devId, int devType);

    /**
     * 根据设备类型获取该类型下的所有设备,根据设备类型，list中为相应的类型的设备对象
     * @param devType
     * @param depId
     * @return
     */
    List<SelectOption> getDevListForSelect(int devType, Long depId);

}
