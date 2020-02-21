package com.wt.mis.sys.util;

import com.wt.mis.sys.entity.Dict;
import com.wt.mis.sys.entity.DictItem;
import com.wt.mis.sys.repository.DictRepository;
import com.wt.mis.sys.service.SysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DictUtils {

    @Resource
    private SysService sysServiceAuto;

    private static SysService sysService;

    private static Map<String,List<DictItem>> dictItemMap;

    @PostConstruct
    public void init(){
        sysService = sysServiceAuto;
        dictItemMap = new HashMap<>();
        List<Dict> dictList = sysService.getAllDict();
        for(Dict dict:dictList){
            dictItemMap.put(dict.getDictName(),dict.getDictItemList());
        }
    }

    /**
     * 根据字典名称获取所有字典项目
     * @param dictName
     * @return
     */
    public static List<DictItem> getDictItems(String dictName){
        List<DictItem> list = dictItemMap.get(dictName);
        if(list==null||list.size()==0){
            list = sysService.getDictItems(dictName);
        }
        return list;
    }

    /**
     * 清除字典缓存
     * @param dictName
     */
    public static void cleanDictCache(String dictName){
        dictItemMap.replace(dictName,null);
    }

    /**
     * 更加字典项名称及键值获取键名
     * @param dictName
     * @param dictItemValue
     * @return
     */
    public static String getDictItemKey(String dictName,String dictItemValue){
        List<DictItem> dictItemList =  getDictItems(dictName);
        String key = "";
        for(DictItem item:dictItemList){
            if(item.getItemValue().equals(dictItemValue)){
                key = item.getItemKey();
                break;
            }
        }
        return key;
    }
}
