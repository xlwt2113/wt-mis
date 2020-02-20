
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.MeterBox;
import com.wt.mis.dev.repository.MeterBoxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/dev/meterbox")
public class MeterBoxController extends BaseController<MeterBox> {

    @Autowired
    MeterBoxRepository meterBoxRepository;

    @Override
    public BaseRepository<MeterBox, Long> repository() {
        return meterBoxRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/meterbox";
    }

    @Override
    protected String generateSearchSql(MeterBox meterBox, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from dev_meter_box as t1  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(meterBox.getMeterBoxName())) {
            sql.append(" and t1.meter_box_name like '%" + meterBox.getMeterBoxName() + "%'");
        }
        if (StringUtils.isNotEmpty(meterBox.getInstallationLocation())) {
            sql.append(" and t1.installation_location like '%" + meterBox.getInstallationLocation() + "%'");
        }
        return sql.toString();
    }
}

