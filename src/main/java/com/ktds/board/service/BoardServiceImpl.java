package com.ktds.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ktds.board.dao.BoardDao;
import com.ktds.board.vo.BoardSearchVO;
import com.ktds.board.vo.BoardVO;
import com.ktds.common.exceptions.PolicyViolationException;
import com.ktds.member.biz.MemberBiz;
import com.ktds.member.vo.MemberVO;
import com.ktds.reply.dao.ReplyDao;
import com.ktds.reply.vo.ReplyVO;

import io.github.seccoding.web.pager.Pager;
import io.github.seccoding.web.pager.PagerFactory;
import io.github.seccoding.web.pager.explorer.ClassicPageExplorer;
import io.github.seccoding.web.pager.explorer.ListPageExplorer;
import io.github.seccoding.web.pager.explorer.PageExplorer;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	@Qualifier("boardDaoMybatisImpl")
	private BoardDao boardDao;
	
	@Autowired
	private MemberBiz memberBiz;
	
	@Autowired
	private ReplyDao replyDao;

	/*
	 * public void setBoardDao(BoardDao boardDao) {
	 * System.out.println("Spring 에서 호출함."); System.out.println(boardDao);
	 * this.boardDao = boardDao; }
	 */

	@Override
	public boolean createBoard(BoardVO boardVO, MemberVO memberVO) {

		// 업로드를 했다면.
		boolean isUploadFile = boardVO.getOrigineFileName().length() > 0;

		int point = 10;

		if (isUploadFile) {
			point += 10;
		}
		this.memberBiz.updatePoint(memberVO.getEmail(), point);
		

		int memberPoint = memberVO.getPoint();
		memberPoint += point;
		memberVO.setPoint(memberPoint);
		
		return this.boardDao.insertBoard(boardVO) > 0;
	}

	@Override
	public boolean updateBoard(BoardVO boardVO) {

		return this.boardDao.updateBoard(boardVO) > 0;
	}

	@Override
	public BoardVO readOneBoard(int id) {
		return this.boardDao.selectOneBoard(id);
	}

	@Override
	public BoardVO readOneBoard(int id, MemberVO memberVO) {
		BoardVO boardVO = this.readOneBoard(id);
		
		List<ReplyVO> replyList = this.replyDao.selectAllReplies(id);
		boardVO.setReplyList(replyList);

		if (!boardVO.getEmail().equals(memberVO.getEmail())) {
			
			if (memberVO.getPoint() < 2 ) {
				throw new PolicyViolationException("포인트가 부족합니다.","/board/list");
			}
			this.memberBiz.updatePoint(memberVO.getEmail(), -2);
			int point = memberVO.getPoint();
			point -= 2;
			memberVO.setPoint(point);
		}
		return boardVO;
	}

	@Override
	public boolean deleteOneBoard(int id) {
		return this.boardDao.deleteOneBoard(id) > 0;
	}

	@Override
	public PageExplorer readAllBoards(BoardSearchVO boardSearchVO) {
		int totalCount = this.boardDao.selectAllBoardsCount(boardSearchVO);		// 게시물의 개수를 count해서 페이지의 수 계산
		
		Pager pager = PagerFactory.getPager(Pager.ORACLE, boardSearchVO.getPageNo()+"");	// Oracle페이지, 현재 볼 페이지 선택 (몇번부터 몇번까지의 정보 나옴)
		
		pager.setTotalArticleCount(totalCount);
		
		PageExplorer pageExplorer = pager.makePageExplorer(ClassicPageExplorer.class, boardSearchVO); // 시작번호와 끝번호가 나옴
		
		pageExplorer.setList( this.boardDao.selectAllBoards(boardSearchVO) );
		
		return pageExplorer;
	}

}
