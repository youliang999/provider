package com.java1234.dao.impl;

import com.java1234.dao.RepairDao;
import com.java1234.model.Repair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository("repairDao")
public class RepairDaoImpl implements RepairDao{

	@Resource
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void add(Repair repair) {
		String sql="insert into t_repair values(null,?,?,null,now(),null,0,1)";
		jdbcTemplate.update(sql, new Object[]{repair.getEquipmentId(),repair.getUserMan()});
	}

}
