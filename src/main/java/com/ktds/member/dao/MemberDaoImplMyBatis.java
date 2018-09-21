package com.ktds.member.dao;

import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ktds.member.vo.MemberVO;

@Repository
public class MemberDaoImplMyBatis extends SqlSessionDaoSupport implements MemberDao {
	
	private Logger logger = LoggerFactory.getLogger(MemberDaoImplMyBatis.class);
	@Autowired
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		logger.debug("Initiate MyBatis");
	    super.setSqlSessionTemplate(sqlSessionTemplate);	//SqlSessionDaoSupport에 있는 setSqlSessionTemplate를 Call
	 }

	@Override
	public int insertMember(MemberVO memberVO) {
		return getSqlSession().insert("MemberDao.insertMember", memberVO);
	}

	@Override
	public MemberVO selectOneMember(MemberVO memberVO) {
		return getSqlSession().selectOne("MemberDao.selectOneMember", memberVO);
	}

	@Override
	public int updatePoint(Map<String, Object> param) {
		return getSqlSession().update("MemberDao.updatePoint", param);
	}

	@Override
	public Integer isBlockUser(String userId) {
		return getSqlSession().selectOne("MemberDao.isBlockUser", userId);
	}

	@Override
	public int unBlockUser(String userId) {
		return getSqlSession().update("MemberDao.unBlockUser", userId);
	}

	@Override
	public int increaseLoginFailCount(String userId) {
		return getSqlSession().update("MemberDao.increaseLoginFailCount", userId);
	}
	
	

}
