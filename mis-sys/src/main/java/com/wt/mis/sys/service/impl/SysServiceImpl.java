package com.wt.mis.sys.service.impl;

import com.wt.mis.sys.entity.UploadFiles;
import com.wt.mis.sys.repository.UploadFilesRepository;
import com.wt.mis.sys.service.SysService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SysServiceImpl implements SysService {

    @Autowired
    UploadFilesRepository uploadFilesRepository;

    @Override
    public List<UploadFiles> getUploadFilesByIds(List<Long> ids){
        return uploadFilesRepository.findAllByIds(ids);
    }
}
