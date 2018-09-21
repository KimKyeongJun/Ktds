package com.ktds.member.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.ktds.common.session.Session;
import com.ktds.member.service.MemberService;
import com.ktds.member.validator.MemberValidator;
import com.ktds.member.vo.MemberVO;

@Controller
public class MemberController {
	
	@Autowired
	private MemberService memberSerivce;
	
	@GetMapping("/member/logout")
	public String doMemberLogoutAction(HttpSession session) {
		// Logout
		session.invalidate();	//session 다 날려버리기
		return "redirect:/member/login";
	}
	
	@GetMapping("/member/regist")
	public String viewRegistMemberPage() {
		return "member/regist";
	}
	
	// /member/check/duplicate?email=값
	@PostMapping("/member/check/duplicate")
	@ResponseBody
	public Map<String, Object> doCheckDuplicateEmail(@RequestParam String email){
		
		Random random = new Random();
		Map<String, Object> result = new HashMap<>();
		result.put("status","OK");
		result.put("duplicated", random.nextBoolean());
		return result;
	}
	
	
	@PostMapping("/member/regist")
	public ModelAndView doRegistMemberAction(@Validated({MemberValidator.Regist.class}) @ModelAttribute MemberVO memberVO, Errors errors) {
		ModelAndView view = new ModelAndView("redirect:/member/login");
		if ( errors.hasErrors() ) {
			view.setViewName("member/regist");
			view.addObject(memberVO);
			return view;
		}		
		
		boolean isSuccess = this.memberSerivce.registMember(memberVO);
		
		return view;
	}
	
	@PostMapping("/member/check/validate")
	@ResponseBody
	public Map<String, Object> doCheckRegist(@Validated({MemberValidator.Regist.class}) @ModelAttribute MemberVO memberVO, Errors errors, HttpSession session){
		Map<String, Object> result = new HashMap<>();
		System.out.println("============" + memberVO);
		
		if( errors.hasErrors() ) {
			StringBuilder errorMessage = new StringBuilder();
			for ( FieldError fieldError : errors.getFieldErrors() ) {
				errorMessage.append(fieldError.getDefaultMessage());
				errorMessage.append("\n");
				result.put("message", errorMessage);
			}
			result.put("status", false);
			session.setAttribute("_VAILDCHECK_", memberVO);
			return result;
		}
		result.put("status", true);
		this.memberSerivce.registMember(memberVO);
		return result;
	}
	
	@GetMapping("/member/login")
	public String viewMemberLoginPage() {
		return "member/login";
	}
	
	@PostMapping("/member/login")
	public ModelAndView doMemberLoginAction(@Validated({MemberValidator.Login.class}) @ModelAttribute MemberVO memberVO, Errors errors, HttpSession session ) {
		ModelAndView view = new ModelAndView("redirect:/board/list");
		if ( errors.hasErrors() ) {
			view.setViewName("member/login");
			view.addObject("memberVO",memberVO);
			return view;
		}
		boolean isLogin = this.memberSerivce.readOneMember(memberVO);
		if ( isLogin ) {
			//session.setAttribute(Session.USER, loginMemberVO);			
		}
		else {
			new ModelAndView("redirect:/memer/login");
		}
		return view;		
	}
	
	

}
