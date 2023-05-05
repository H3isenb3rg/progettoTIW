package it.polimi.tiw.auctions.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginCheck implements Filter{
	public LoginCheck() {
		super();
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("Executing login check...");
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginpath = req.getServletContext().getContextPath() + "/login.html";
		HttpSession session = req.getSession();
		
		if(session.isNew() || session.getAttribute("user") == null) {
			res.setStatus(403);
			res.setHeader("Location", loginpath);
			System.out.println("Login check failed!");
			res.sendRedirect(loginpath);
			return;
		}
		
		chain.doFilter(request, response);
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
	}
	
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	
}

