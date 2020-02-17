package com.wt.mis.sys.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sys.entity.RoleMenu;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMenuRepository extends BaseRepository<RoleMenu,Long> {

    List<RoleMenu> findAllByRoleId(Long roleId);

    List<RoleMenu> findAllByMenuId(Long menuId);
}
