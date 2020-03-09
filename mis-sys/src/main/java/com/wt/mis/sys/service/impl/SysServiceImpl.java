package com.wt.mis.sys.service.impl;

import com.wt.mis.sys.entity.Dict;
import com.wt.mis.sys.entity.DictItem;
import com.wt.mis.sys.entity.Register;
import com.wt.mis.sys.entity.UploadFiles;
import com.wt.mis.sys.repository.DictItemRepository;
import com.wt.mis.sys.repository.DictRepository;
import com.wt.mis.sys.repository.RegisterRepository;
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

    @Autowired
    RegisterRepository registerRepository;

    @Autowired
    DictItemRepository dictItemRepository;

    @Autowired
    DictRepository dictRepository;

    @Override
    public List<UploadFiles> getUploadFilesByIds(List<Long> ids){
        return uploadFilesRepository.findAllByIds(ids);
    }

    @Override
    public String getRegisterValue(String itemName) {
         return this.getRegisterValue(itemName,null);
    }

    @Override
    public String getRegisterValue(String itemName,String defaultVal) {
        Register register =  registerRepository.getRegisterByItemName(itemName);
        if(register!=null){
            return register.getItemValue();
        }else{
            return defaultVal;
        }
    }

    @Override
    public List<DictItem> getDictItems(String dictName) {
        Dict dict = dictRepository.getFirstByDictNameAndDel(dictName,0);
        List<DictItem> dictItemList = dictItemRepository.getAllByDictAndDel(dict,0);
        return dictItemList;
    }

    @Override
    public List<Dict> getAllDict() {
        return dictRepository.findAllByDel(0);
    }
}
