package com.wt.mis.sys.service;

import com.wt.mis.sys.entity.UploadFiles;

import java.util.List;

public interface SysService {

    public List<UploadFiles> getUploadFilesByIds(List<Long> ids);
}
