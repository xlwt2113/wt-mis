
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.Meter;
import com.wt.mis.dev.repository.MeterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/dev/meter")
public class MeterController extends BaseController<Meter> {

    @Autowired
    MeterRepository meterRepository;

    @Override
    public BaseRepository<Meter, Long> repository() {
        return meterRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/meter";
    }

    @Override
    protected String generateSearchSql(Meter meter, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from dev_meter as t1  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(meter.getInstallationLocation())) {
            sql.append(" and t1.installation_location like '%" + meter.getInstallationLocation() + "%'");
        }
        if (StringUtils.isNotEmpty(meter.getMeterBarcode())) {
            sql.append(" and t1.meter_barcode like '%" + meter.getMeterBarcode() + "%'");
        }
        return sql.toString();
    }
}

