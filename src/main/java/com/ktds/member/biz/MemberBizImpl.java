package com.ktds.member.biz;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ktds.member.dao.MemberDao;
import com.ktds.member.vo.MemberVO;

@Component
public class MemberBizImpl implements MemberBiz {
	
	
	
	@Autowired
	@Qualifier("memberDaoImplMyBatis")
	private MemberDao memberDao;
	
	
	
	@Override
	public int updatePoint(String email, int point) {
		Map<String, Object> param = new HashMap<>();
		param.put("email", email);
		param.put("point", point);
		
		return this.memberDao.updatePoint(param);
	}

	@Override
	public boolean isBlockUser(String userId) {
		Integer loginFailCount = this.memberDao.isBlockUser(userId);
		if ( loginFailCount == null ) {
			loginFailCount = 0;
		}
		return loginFailCount >=3 ;
	}

	@Override
	public boolean unBlockUser(String userId) {
		return this.memberDao.unBlockUser(userId) > 0;
	}

	@Override
	public boolean increaseLoginFailCount(String userId) {
		return this.memberDao.increaseLoginFailCount(userId) > 0;
	}

	@Override
	public boolean insertMember(MemberVO memberVO) {
		return this.memberDao.insertMember(memberVO) > 0;
	}

	@Override
	public MemberVO selectOneMember(MemberVO memberVO) {
		return this.memberDao.selectOneMember(memberVO);
	}
	
	

}
