package it.polimi.tiw.auctions.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.auctions.beans.Auction;
import it.polimi.tiw.auctions.beans.Bid;
import it.polimi.tiw.auctions.beans.User;
import it.polimi.tiw.auctions.dao.AuctionsDAO;
import it.polimi.tiw.auctions.dao.BidDAO;
import it.polimi.tiw.auctions.utils.ConnectionHandler;

@WebServlet("/Offer")
public class GoToOfferPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
	public GoToOfferPage() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = "/WEB-INF/Offer.html";
		HttpSession session = request.getSession();
		Integer auctionID;
		try {
			 auctionID = Integer.parseInt(request.getParameter("auctionID"));
		} catch (NumberFormatException e) {
			Auction currAuction = (Auction) session.getAttribute("auction");
			auctionID = currAuction.getAuctionId();
		}
		
		User user = (User) session.getAttribute("user");
		AuctionsDAO auctionsDAO = new AuctionsDAO(this.connection);
		BidDAO bidDAO = new BidDAO(this.connection);
		Auction auction;
		List<Bid> bids;
		try {
			auction = auctionsDAO.findAuctionById(auctionID);
			bids = bidDAO.getBidsForAuction(auctionID);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover auctions");
			return;
		}
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("username", user.getName());
		ctx.setVariable("auction", auction);
		ctx.setVariable("bids", bids);
		if(bids.size()>0)
			ctx.setVariable("maxOffer", bids.get(0).getOffer());
		else
			ctx.setVariable("maxOffer", auction.getStartingPrice());
		session.setAttribute("auction", auction);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
