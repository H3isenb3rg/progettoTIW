package it.polimi.tiw.auctions.controllers;

import java.awt.Image;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.auctions.beans.Auction;
import it.polimi.tiw.auctions.beans.User;
import it.polimi.tiw.auctions.dao.AuctionsDAO;
import it.polimi.tiw.auctions.utils.ConnectionHandler;

@WebServlet("/CloseAuction")
public class CloseAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CloseAuction() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Get and parse all parameters from request
		HttpSession session = request.getSession();
		boolean isBadRequest = false;
		Integer auctionID = null;
		try {
			auctionID = Integer.parseInt(request.getParameter("auctionID"));
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error while closing the auction");
			return;
		}

		// Create auction in DB
		User user = (User) session.getAttribute("user");
		AuctionsDAO auctionsDAO = new AuctionsDAO(connection);
		Auction auction;
		try {
			auction = auctionsDAO.findAuctionById(auctionID);
			Timestamp currTime = new Timestamp(System.currentTimeMillis());
			if (auction.getEndingDate().after(currTime))
				isBadRequest = true;
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get auction");
			return;
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Auction isn't ended");
			return;
		}
		
		
		
		try {
			auctionsDAO.closeAuction(auctionID);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error while closing the auction");
			return;
		}

		// return the user to the right view
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/AuctionDetails";
		response.sendRedirect(path);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}