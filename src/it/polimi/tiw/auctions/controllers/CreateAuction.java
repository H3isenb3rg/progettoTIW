package it.polimi.tiw.auctions.controllers;

import java.awt.Image;
import java.io.IOException;
import java.math.BigDecimal;
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

import it.polimi.tiw.auctions.beans.User;
import it.polimi.tiw.auctions.dao.AuctionsDAO;
import it.polimi.tiw.auctions.utils.ConnectionHandler;

@WebServlet("/CreateAuction")
public class CreateAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	public CreateAuction() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}

		// Get and parse all parameters from request
		boolean isBadRequest = false;
		String productName = null;
		String productDescription = null;
		Image image = null;
		Integer productStartingPrice = null;
		Integer productMinimumRaise = null;
		Date endingDate = null;
		try {
			productStartingPrice = Integer.parseInt(request.getParameter("productStartingPrice"));
			productMinimumRaise = Integer.parseInt(request.getParameter("productMinimumRaise"));
			productName = StringEscapeUtils.escapeJava(request.getParameter("productName"));
			productDescription = StringEscapeUtils.escapeJava(request.getParameter("productDescription"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			endingDate = (Date) sdf.parse(request.getParameter("endingDate"));
			isBadRequest = productName.isEmpty() || productDescription.isEmpty() ||
					endingDate.before(new Date()) ||
					productStartingPrice < 0  || productMinimumRaise < 0;
		} catch (NumberFormatException | NullPointerException | ParseException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		// Create auction in DB
		User user = (User) session.getAttribute("user");
		AuctionsDAO auctionsDAO = new AuctionsDAO(connection);
		try {
			auctionsDAO.createAuction(productStartingPrice, productMinimumRaise, endingDate, user.getUsername(), productName, productDescription);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create mission");
			return;
		}

		// return the user to the right view
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/Sell";
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
