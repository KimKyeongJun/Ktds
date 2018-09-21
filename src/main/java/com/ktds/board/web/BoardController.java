package com.ktds.board.web;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ktds.board.service.BoardService;
import com.ktds.board.vo.BoardSearchVO;
import com.ktds.board.vo.BoardVO;
import com.ktds.common.exceptions.PolicyViolationException;
import com.ktds.common.session.Session;
import com.ktds.common.web.DownloadUtil;
import com.ktds.member.vo.MemberVO;
import com.nhncorp.lucy.security.xss.XssFilter;

import io.github.seccoding.web.pager.explorer.PageExplorer;

@Controller
public class BoardController {				//servlet의 역할을대체
		
	//private Logger logger = LoggerFactory.getLogger(BoardContoller.class);
	private Logger staticticsLogger = LoggerFactory.getLogger("list.statistics");
	private Logger paramLogger = LoggerFactory.getLogger(BoardController.class);
	@Autowired
	@Qualifier("boardServiceImpl")
	private BoardService boardService;
	
	@Value("${upload.path}")
	private String uploadPath;
	
	@RequestMapping("/board/list/init")
	public String viewBoardListPageForInitiate(HttpSession session) {
		session.removeAttribute(Session.SERARCH);
		return "redirect:/board/list";
	}
	
	@RequestMapping("/board/list")
	public ModelAndView viewBoardListPage(@ModelAttribute BoardSearchVO boardSearchVO, HttpServletRequest request, HttpSession session) {
		
		// 전체 검색 or 상세 -> 목록 or 글쓰기
		if ( boardSearchVO.getSearchKeyword() == null ) {
			boardSearchVO = (BoardSearchVO) session.getAttribute(Session.SERARCH);
			if ( boardSearchVO == null ) {
				boardSearchVO = new BoardSearchVO();
				boardSearchVO.setPageNo(0);
			}
		}
		
		//html태그, 게시글, 페이지정보
		PageExplorer pageExplorer = this.boardService.readAllBoards(boardSearchVO);
		XssFilter xssFilter = XssFilter.getInstance("lucy-xss-superset.xml");
		for ( Object boardVO : pageExplorer.getList() ) {
			BoardVO convertVO = (BoardVO) boardVO;
			convertVO.setSubject(xssFilter.doFilter(convertVO.getSubject()));
			convertVO.setContent(xssFilter.doFilter(convertVO.getContent()));
		}
		
		staticticsLogger.info("URL : /board/list, IP : "+request.getRemoteAddr() + ", List Size : "+ pageExplorer.getList().size());
		
		session.setAttribute(Session.SERARCH, boardSearchVO);
		
		ModelAndView view = new ModelAndView("/board/list");
		view.addObject("boardList", pageExplorer.getList());
		view.addObject("pagenation", pageExplorer.make());
		view.addObject("boardSearchVO", boardSearchVO);
		return view;
	}
	// Spring 4.2 이하에서 사용
	// @RequestMapping(value="/write", method=RequestMethod.GET)
	// Spring 4.3 이상에서 사용
	@GetMapping("/board/write")
	public String viewBoardWritePage() {	
		return "board/write";
	}
	
	@PostMapping("/board/write")
	public ModelAndView doBoardWriteAction(@Valid @ModelAttribute BoardVO boardVO, Errors errors, @SessionAttribute(Session.USER) MemberVO memberVO,
			HttpServletRequest request) {
		
		ModelAndView view = new ModelAndView("redirect:/board/list/init");
		
		//Validation Annotation이 실패했는지 체크
		if ( errors.hasErrors() ) {
			view.setViewName("board/write");
			view.addObject("boardVO",boardVO);
			return view;
		}
		
		MultipartFile uploadFile = boardVO.getFile();
		
		if ( !uploadFile.isEmpty() ) {
			//실제 파일명
			String originFileName = uploadFile.getOriginalFilename();
			//난수화된 파일명
			String fileName = UUID.randomUUID().toString();
			File uploadDir = new File(this.uploadPath);
			
			//경로에 폴더 없을 시 폴더 생성
			if( !uploadDir.exists()) {
				uploadDir.mkdirs();
			}
			
			//파일이 업로드될 경로 지정
			File destFile = new File(this.uploadPath, fileName);
			try {
				// 업로드
				uploadFile.transferTo(destFile);
				//DB에 파일정보 저장
				boardVO.setFileName(fileName);
				boardVO.setOrigineFileName(originFileName);
			} catch (IllegalStateException | IOException e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}		
		boardVO.setEmail(memberVO.getEmail());
		boardVO.setMemberVO(memberVO);
		XssFilter xssFilter = XssFilter.getInstance("lucy-xss-superset.xml");
		boardVO.setSubject(xssFilter.doFilter(boardVO.getSubject()));
		boardVO.setContent(xssFilter.doFilter(boardVO.getContent()));
		
		boolean isSuccess = this.boardService.createBoard(boardVO, memberVO);
		
		String paramFormat = "IP:%s, Param: %s, Result:%s";
		paramLogger.debug(String.format(paramFormat, request.getRemoteAddr()
				, boardVO.getSubject() + ", "+boardVO.getContent() + ", "+boardVO.getEmail() + ", "+boardVO.getFileName()
				+", "+ boardVO.getOrigineFileName(), view.getViewName()));
		if ( isSuccess ) {
			return view;			
		}
		else {
			return view;
		}
	}
	
	//http://localhost:8080/HelloSpring/board/detail?id=1
	@RequestMapping("/board/detail/{id}")
	public ModelAndView viewBoardDetailPage( @PathVariable int id ,@SessionAttribute(Session.USER) MemberVO memberVO) {		
		BoardVO boardVO = this.boardService.readOneBoard(id, memberVO);		
		
		ModelAndView view = new ModelAndView("board/detail");
		view.addObject("boardVO", boardVO);
		return view;
	}
	 @RequestMapping("/board/delete/{id}")
	public String doBoardDeleteAction(@PathVariable int id) {
		boolean isSuccess = this.boardService.deleteOneBoard(id);
		return "redirect:/board/list";
	}
	 
	 @RequestMapping("/board/download/{id}")
	 public void fileDownload(@PathVariable int id, HttpServletRequest request, HttpServletResponse response,
			 @SessionAttribute(Session.USER) MemberVO memberVO) {
		 
		 if ( memberVO.getPoint() < 5 ) {
			 throw new PolicyViolationException("다운로드를 위한 포인트가 부족합니다.","redirect:"
			 		+ "/board/detail/"+id);
		 }
		 BoardVO boardVO = this.boardService.readOneBoard(id);
		 
		 String fileName= boardVO.getFileName();
		 String originFileName = boardVO.getOrigineFileName();
		 
		 
		 // Windows \
		 // Unix/Linux/macos /
		 
		 try {
			new DownloadUtil(this.uploadPath+File.separator+fileName).download(request,response,originFileName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(),e);
		}		 
	 }
}
