package com.wt.mis.sys.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sys.entity.Menu;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MenuRepository extends BaseRepository<Menu, Long> {

    /**
     * 导航目录
     */
    int MENU_TYPE_TOP = 0;
    /**
     * 目录
     */
    int MENU_TYPE_FOLDER = 1;
    /**
     * 菜单
     */
    int MENU_TYPE_MENU = 2;
    /**
     * 菜单
     */
    int MENU_TYPE_BUTTON = 3;

    /**
     * 根据菜单类型获取菜单
     *
     * @param menuType 菜单类型 0 导航目录 1 目录  2 菜单  3 操作或按钮
     * @param del 删除标记，0 未删除
     * @return 菜单列表
     */
    List<Menu> findByMenuTypeAndDelOrderBySeqAsc(int menuType, int del);

    /**
     * 根据父菜单ID获取所有的下级菜单
     * @param parId 父菜单ID
     * @param del  删除标记，0 未删除
     * @return
     */
    List<Menu> findAllByParIdAndDel(Long parId, int del);

    /**
     * 根据路径找到所有对应菜单
     * @param href
     * @return
     */
    List<Menu> findAllByHref(String href);

    /**
     * 根据路径查找菜单
     * @param href
     * @return
     */
    Menu  findByHref(String href);

    /**
     * 根据路径删除相关菜单
     * @param href
     */
    @Transactional
    @Modifying
    void deleteAllByHref(String href);

    /**
     * 根据父菜单删除所有子菜单
     * @param parId
     */
    @Transactional
    @Modifying
    void deleteAllByParId(Long parId);

    @Query("from Menu where del = 0 and parId = (select id from Menu where href = ?1 or title = ?1 and del = 0) and href = ?2")
    List<Menu> getMenuByTtileAndHref(String title,String opt);


}
