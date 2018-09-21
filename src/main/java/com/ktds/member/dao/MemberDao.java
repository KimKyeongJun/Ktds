package com.ktds.member.dao;

import java.util.Map;

import com.ktds.member.vo.MemberVO;

public interface MemberDao {
	
	public int insertMember(MemberVO memberVO);
	
	public MemberVO selectOneMember(MemberVO memberVO);
	
	public int updatePoint(Map<String, Object> param);
	
	public Integer isBlockUser(String userId);
	
	public int unBlockUser(String userId);
	
	public int increaseLoginFailCount(String userId);
	
}
