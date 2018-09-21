package com.ktds.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ktds.member.biz.MemberBiz;
import com.ktds.member.dao.MemberDao;
import com.ktds.member.vo.MemberVO;

@Service
public class MemberServiceImpl implements MemberService {
	
	@Autowired
	@Qualifier("memberDaoImplMyBatis")
	private MemberDao memberDao;
	
	@Autowired
	private MemberBiz memberBiz;

	@Override
	public boolean registMember(MemberVO memberVO) {
		return this.memberDao.insertMember(memberVO) > 0;
	}
	
	@Override
	public boolean readOneMember(MemberVO memberVO) {
		boolean isBlockUser = this.memberBiz.isBlockUser(memberVO.getEmail());
		if ( !isBlockUser ) {
			MemberVO loginMemberVO = this.memberDao.selectOneMember(memberVO);
			if (loginMemberVO != null ) {
				this.memberBiz.updatePoint(memberVO.getEmail(), +2);
				
				int point = loginMemberVO.getPoint();
				point += 2;
				loginMemberVO.setPoint(point);
				this.memberBiz.unBlockUser(memberVO.getEmail());
				return true;
			}
			else {
				this.memberBiz.increaseLoginFailCount(memberVO.getEmail());
				return false;
			}
		}
		else {
			return false;
		}
	}

}
