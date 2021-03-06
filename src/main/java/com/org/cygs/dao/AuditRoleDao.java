package com.org.cygs.dao;

import java.util.List;
import java.util.Map;

import com.org.cygs.pojo.AuditRole;
import com.org.cygs.pojo.MissionType;

public interface AuditRoleDao {

    public int deleteByPrimaryKey(String arId);

    public int insertSelective(AuditRole record);

    public AuditRole selectByPrimaryKey(String arId);

    public int updateByPrimaryKeySelective(AuditRole record);
    
	//根据任务书类型获取审核角色信息
	public List<AuditRole> getAuditRoleListByMtId(String mtId);
	
	//根据任务书类型获取审核角色信息条目数
	public int getCountById(String mtId);
	
	//根据审核角色ID获取该条目；该返回对象包含任务书名称
	public AuditRole getAuditRoleById(String arId);
	
	//根据角色ID和审核步骤获取审核信息
    public List<AuditRole> getArByRoAndAstep(Map<String, Object> map);
    
  //根据任务书类型和审核步骤获取角色名称
    public String getRoleNameByMtAndAstep(int mt_code, int steps);
    
    //查找当前任务书审核的最大步骤
    public int getMaxStepByMtId(String mtId);
    
    //通过任务书类型和角色 查询当前步骤
    public int getCexStepByMtIdAndRoId(String mtId, String RoId);
    
    //通过任务书类型编号获取审核信息，降序
    public List<AuditRole> getAuditRoleByMtCode(int mtCode);
    
    public List<AuditRole> getAuditRoleList(Map<String, Object> map);
    public int getAuditRoleCount(Map<String, Object> map);
}