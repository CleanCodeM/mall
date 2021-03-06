package com.cskaoyan.mall.mapper;

import com.cskaoyan.mall.bean.Role;
import com.cskaoyan.mall.bean.RoleExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {
    long countByExample(RoleExample example);

    int deleteByExample(RoleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Role record);

    int insertSelective(Role record);

    List<Role> selectByExample(RoleExample example);

    Role selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Role record, @Param("example") RoleExample example);

    int updateByExample(@Param("record") Role record, @Param("example") RoleExample example);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    List<Role> allRole();

    List<Role> selectAll();

    int selectCountId();

    List<Role> selectByNameLike(@Param("name") String name);

    int selectCountIdByNameLike(@Param("name") String name);

    int update(@Param("role") Role role);

    int selectLastId();

    Role selectById(@Param("id") int id);

    int insertRole(@Param("role") Role role);

    int deleteRoleById(@Param("id") Integer id);
}
