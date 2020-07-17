
package com.wt.mis.fuse.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.fuse.entity.DevHub;
import com.wt.mis.fuse.repository.DevHubRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/fuse/devhub")
public class DevHubController extends BaseController<DevHub> {

    @Autowired
    DevHubRepository devHubRepository;

    @Override
    public BaseRepository<DevHub, Long> repository() {
        return devHubRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "fuse/devhub";
    }

    @Override
    protected String generateSearchSql(DevHub devHub, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from fuse_dev_hub as t1  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(devHub.getHubAddress())) {
            sql.append(" and t1.hub_address like '%" + devHub.getHubAddress() + "%'");
        }
        if (StringUtils.isNotEmpty(request.getParameter("hubName"))) {
            sql.append(" and t1.hub_location like '%" + request.getParameter("hubName") + "%'");
        }
        return sql.toString();
    }


}
