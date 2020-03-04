package com.wt.mis.dev.service;

import com.wt.mis.core.dao.SearchDao;
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
                Dep tempDep = depRespository.getOne(Long.parseLong(depId));
                level_name = level_name + tempDep.getName() + "_" ;
            }
            if(StringUtils.isNotEmpty(level_name)){
                level_name = level_name.substring(0,level_name.length()-1);
            }
            dev.setOperationsTeamName(level_name);
        }
        return dev;
    }

    @Override
    public List<SelectOption> getDevListForSelect(int devType, Long depId) {
        String sql = "";
        if(devType == 1){
            sql = "select t1.id,t1.line_name as name from dev_line as t1  left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0";
        }else if(devType == 2){
            sql = "select t1.id,t1.transform_name as name  from dev_transform as t1  left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0 ";
        }else if(devType == 3){
            sql = "select t1.id,t1.branch_box_name as name  from dev_branch_box as t1 left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0 ";
        }else if(devType == 4){
            sql = "select t1.id,t1.meter_box_name as name  from dev_meter_box as t1 left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0  and t1.three_phase = 1";
        }else if(devType == 5){
            sql = "select t1.id,t1.meter_box_name as name  from dev_meter_box as t1 left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0 and t1.three_phase = 2";
        }else if(devType == 6){
            sql = "select t1.id,t1.meter_barcode as name  from dev_meter as t1  left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0 and t1.three_phase = 1";
        }else if(devType == 7){
            sql = "select t1.id,t1.meter_barcode as name  from dev_meter as t1  left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0 and t1.three_phase = 2";
        }
        if(depId != null){
            sql = sql + " and (t2.level like '" + depId + "%' or t1.operations_team is null )";
        }
        List list = searchDao.findAllBySql(sql).getContent();
        List<SelectOption> result = new ArrayList<SelectOption>();
        for(Object obj:list){
            HashMap<String, Object> map = (HashMap) obj;
            SelectOption so = new SelectOption();
            so.setName(String.valueOf(map.get("name")));
            so.setValue(String.valueOf(map.get("id")));
            result.add(so);
        }
        return result;
    }
}
