package it.polimi.tiw.auctions.controllers;

import java.awt.Image;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import it.polimi.tiw.auctions.beans.Bid;
import it.polimi.tiw.auctions.beans.User;
import it.polimi.tiw.auctions.dao.AuctionsDAO;
import it.polimi.tiw.auctions.dao.BidDAO;
import it.polimi.tiw.auctions.utils.ConnectionHandler;

@WebServlet("/MakeBid")
public class MakeBid extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	public MakeBid() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Get and parse all parameters from request
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Auction auction = (Auction) session.getAttribute("auction");
		boolean isBadRequest = false;
		Integer auctionID = auction.getAuctionId();
		Double offer = null;
		String bidder = user.getUsername();
		BidDAO bidDAO = new BidDAO(this.connection);
		Double maxOffer;
		try {
			Bid maxBid = bidDAO.findHighestBid(auctionID);
			if (maxBid!=null)
				maxOffer = maxBid.getOffer();
			else
				maxOffer = 0.0;
		} catch (SQLException e1) {
			e1.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error retrieving max bid");
			return;
		};
		try {
			offer = Double.parseDouble(request.getParameter("amount"));
			isBadRequest = auctionID == null || (offer < maxOffer+auction.getMinIncrease());
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		// Create bid in DB
		try {
			bidDAO.createBid(auctionID, offer, bidder);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create bid");
			return;
		}

		// return the user to the right view
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/Offer";
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
