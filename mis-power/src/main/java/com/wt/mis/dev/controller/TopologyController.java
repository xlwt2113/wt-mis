
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.Topology;
import com.wt.mis.dev.repository.TopologyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/dev/topology")
public class TopologyController extends BaseController<Topology> {

    @Autowired
    TopologyRepository topologyRepository;

    @Override
    public BaseRepository<Topology, Long> repository() {
        return topologyRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/topology";
    }

    @Override
    protected String generateSearchSql(Topology topology, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from dev_topology as t1  where t1.del = 0 ");
        return sql.toString();
    }


}
