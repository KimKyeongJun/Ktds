package com.ktds.member.service;

import com.ktds.member.vo.MemberVO;

public interface MemberService {
	
	public boolean registMember(MemberVO memberVO);
	
	public boolean readOneMember(MemberVO memberVO);

}
