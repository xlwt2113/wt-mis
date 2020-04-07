package com.wt.mis.dev.service;

import com.wt.mis.core.dao.SearchDao;
import com.wt.mis.core.exception.AppException;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.*;
import com.wt.mis.dev.repository.*;
import com.wt.mis.dev.view.DevModel;
import com.wt.mis.dev.view.SelectOption;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.repository.DepRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class DevServiceImpl implements DevService{

    @Autowired
    BranchBoxRepository branchBoxRepository;
    @Autowired
    LineRepository lineRepository;
    @Autowired
    MeterRepository meterRepository;
    @Autowired
    MeterBoxRepository meterBoxRepository;
    @Autowired
    TopologyRepository topologyRepository;
    @Autowired
    TransFormRepository transFormRepository;
    @Autowired
    DepRespository depRespository;
    @Autowired
    SearchDao searchDao;

    @Override
    public DevModel getDevModel(long devId, int devType) {
        DevModel dev = new DevModel();
        dev.setDevType(devType);
        if(devType==1){
            //线路
            Line line = lineRepository.getOne(devId);
            dev.setDevName(line.getLineName());
            dev.setOperationsTeam(line.getOperationsTeam());
        }else if(devType==2){
            //台区
            TransForm transForm = transFormRepository.getOne(devId);
            dev.setDevName(transForm.getTransformName());
            dev.setOperationsTeam(transForm.getOperationsTeam());
        }else if(devType==3){
            //分支箱
            BranchBox branchBox = branchBoxRepository.getOne(devId);
            dev.setDevName(branchBox.getBranchBoxName());
            dev.setOperationsTeam(branchBox.getOperationsTeam());
        }else if(devType==4 || devType == 5){
            //电表相
            MeterBox meterBox = meterBoxRepository.getOne(devId);
            dev.setDevName(meterBox.getMeterBoxName());
            dev.setOperationsTeam(meterBox.getOperationsTeam());
        }else if(devType==6 || devType == 7){
            //单相电能表
            Meter meter = meterRepository.getOne(devId);
            dev.setDevName(meter.getMeterBarcode());
            dev.setOperationsTeam(meter.getOperationsTeam());
        }
        Dep dep = depRespository.getOne(dev.getOperationsTeam());
        if(StringUtils.isNotEmpty(dep.getLevel())){
            String[] depIds = dep.getLevel().split("_");
            String level_name = "";
            for(String depId : depIds){
                if(StringUtils.isNotEmpty(depId)){
                    Dep tempDep = depRespository.getOne(Long.parseLong(depId));
                    level_name = level_name + tempDep.getName() + "_" ;
                }
            }
            if(StringUtils.isNotEmpty(level_name)){
                level_name = level_name.substring(0,level_name.length()-1);
            }
            dev.setOperationsTeamName(level_name);
        }
        return dev;
    }

    @Override
    public List<SelectOption> getDevListForSelect(int devType,Long transFormId, String depLevel) {
        String  devTableName = "dev_line";
        if(devType==2){devTableName = "dev_transform";}
        if(devType==3){devTableName = "dev_branch_box";}
        if(devType==4 || devType==5){devTableName = "dev_meter_box";}
        if(devType==6 || devType ==7){devTableName = "dev_meter";}

        String sql = "SELECT t1.* FROM dev_topology t1 LEFT JOIN "+devTableName+" t2 ON t1.dev_id = t2.id left join sys_dep t3 on t2.operations_team = t3.id WHERE t1.del = 0 ";
        sql = sql + " and t1.dev_type = "+devType ;

        if(StringUtils.isNotEmpty(depLevel)){
            sql = sql + " and t3.level like '" + depLevel + "%'";
        }
        if(transFormId != null){
            sql = sql + " and t1.transform_id = "+transFormId;
        }
        List list = searchDao.findAllBySql(sql).getContent();
        List<SelectOption> result = new ArrayList<SelectOption>();
        for(Object obj:list){
            HashMap<String, Object> map = (HashMap) obj;
            SelectOption so = new SelectOption();
            so.setName(String.valueOf(map.get("dev_name")));
            so.setValue(String.valueOf(map.get("dev_id")));
            result.add(so);
        }
        return result;
    }

    @Override
    public void deleteDev(long devId, int devType) throws AppException {
        List<Topology> topologyList = topologyRepository.findAllByDelAndDevIdAndDevType(0,devId,devType);
        if(topologyList != null && topologyList.size() > 0 && devType !=1){
            throw  new  AppException("所选设备正在运行状态，不允许删除");
        }else{
            if(devType == 2){
                transFormRepository.deleteByIdOnLogic(devId);
            }
            if(devType == 3){
                branchBoxRepository.deleteByIdOnLogic(devId);
            }
            if(devType == 4 || devType == 5){
                meterBoxRepository.deleteByIdOnLogic(devId);
            }
            if(devType == 6 || devType == 7){
                meterRepository.deleteByIdOnLogic(devId);
            }
            if(devType == 1){
                //删除线路
                int cnt = transFormRepository.countAllByDelAndLineId(0,devId);
                if(cnt>0){
                    throw  new  AppException("所选线路上有台区，不允许删除");
                }else{
                    lineRepository.deleteByIdOnLogic(devId);
                }
            }
        }
    }

    @Override
    public void deleteDevs(List<Long> devIds, int devType) throws AppException {
        for(Long devId:devIds){
            this.deleteDev(devId,devType);
        }
    }
}
