package com.ktds.member.biz;

import com.ktds.member.vo.MemberVO;

public interface MemberBiz {

	public boolean insertMember(MemberVO memberVO);

	public MemberVO selectOneMember(MemberVO memberVO);

	public int updatePoint(String email, int point);

	public boolean isBlockUser(String userId);

	public boolean unBlockUser(String userId);

	public boolean increaseLoginFailCount(String userId);

}
