package it.polimi.tiw.auctions.dao;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.auctions.beans.Auction;

public class AuctionsDAO {
	private Connection connection;

	public AuctionsDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Auction> findOpenAuctionByUser(String username) throws SQLException {
		List<Auction> auctions = new ArrayList<Auction>();

		String query = "SELECT *, timestampdiff(hour, CURRENT_TIMESTAMP(), endingDate) as 'timeDiff' "
				+ " FROM auction AS a "
				+ " WHERE ownerID = ? AND isClosed = 0 "
				+ " ORDER BY endingDate ASC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Auction auction = new Auction();
					auction.setAuctionId(result.getInt("auctionID"));
					auction.setStartingPrice(result.getDouble("startingPrice"));
					auction.setMinIncrease(result.getDouble("minIncrease"));
					String datetime = result.getString("endingDate");
					auction.setEndingDate(Timestamp.valueOf(datetime));
					auction.setOwnerId(result.getString("ownerID"));
					auction.setProductName(result.getString("productName"));
					auction.setDescription(result.getString("description"));
					auction.setTimeDiff(result.getInt("timeDiff"));
					auction.setProductImage(null); //todo get image from db
					auction.setClosed(false);
					auctions.add(auction);
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return auctions;
	}
	
	public List<Auction> findClosedAuctionByUser(String username) throws SQLException {
		List<Auction> auctions = new ArrayList<Auction>();

		String query = "SELECT *, timestampdiff(hour, CURRENT_TIMESTAMP(), endingDate) as 'timeDiff' "
				+ " FROM auction AS a "
				+ " WHERE ownerID = ? AND isClosed = 1 "
				+ " ORDER BY endingDate ASC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Auction auction = new Auction();
					auction.setAuctionId(result.getInt("auctionID"));
					auction.setStartingPrice(result.getDouble("startingPrice"));
					auction.setMinIncrease(result.getDouble("minIncrease"));
					String datetime = result.getString("endingDate");
					auction.setEndingDate(Timestamp.valueOf(datetime));
					auction.setOwnerId(result.getString("ownerID"));
					auction.setProductName(result.getString("productName"));
					auction.setDescription(result.getString("description"));
					auction.setTimeDiff(result.getInt("timeDiff"));
					auction.setProductImage(null); //todo get image from db
					auction.setClosed(true);
					auctions.add(auction);
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return auctions;
	}

	public void createAuction(Integer startingPrice, Integer minIncrease, Date endingDate, String ownerID, String productName, String description)
			throws SQLException {
		//TODO: image insert
		String query = "INSERT into auction (startingPrice, minIncrease, endingDate, ownerID, productName, description) VALUES(?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, startingPrice);
			pstatement.setInt(2, minIncrease);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
			String currentDateTime = format.format(endingDate);
			pstatement.setString(3, currentDateTime);
			pstatement.setString(4, ownerID);
			pstatement.setString(5, productName);
			pstatement.setString(6, description);
			System.out.println(pstatement);
			pstatement.executeUpdate();
		}
	}

	public void closeAuction(Integer auctionID)
			throws SQLException {
		String query = "UPDATE auction SET isClosed = b'1' WHERE (auctionID = ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, auctionID);
			System.out.println(pstatement);
			pstatement.executeUpdate();
		}
	}
	
	public List<Auction> findOpenAuctionByKeyword(String username, String keyword) throws SQLException {
		List<Auction> auctions = new ArrayList<Auction>();

		String query = "SELECT *, timestampdiff(hour, CURRENT_TIMESTAMP(), endingDate) as 'timeDiff' "
				+ "FROM auction AS a "
				+ "WHERE ownerID != ? AND isClosed = 0 AND (productName like ? OR description like ?)"
				+ "ORDER BY endingDate ASC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, "%"+keyword+"%");
			pstatement.setString(3, "%"+keyword+"%");
			System.out.println(pstatement);
			try (ResultSet result = pstatement.executeQuery();) {
				System.out.println("query done");
				while (result.next()) {
					System.out.println("reading result");
					Auction auction = new Auction();
					auction.setAuctionId(result.getInt("auctionID"));
					auction.setStartingPrice(result.getDouble("startingPrice"));
					auction.setMinIncrease(result.getDouble("minIncrease"));
					auction.setEndingDate(result.getTimestamp("endingDate"));
					auction.setOwnerId(result.getString("ownerID"));
					auction.setProductName(result.getString("productName"));
					auction.setDescription(result.getString("description"));
					auction.setTimeDiff(result.getInt("timeDiff"));
					auction.setProductImage(null); //todo get image from db
					auction.setClosed(false);
					auctions.add(auction);
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return auctions;
	}

	public Auction findAuctionById(Integer auctionID) throws SQLException {
		Auction auction = null;

		String query = "SELECT *, timestampdiff(hour, CURRENT_TIMESTAMP(), endingDate) as 'timeDiff' "
				+ "FROM auction AS a "
				+ "WHERE auctionID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, auctionID);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					auction = new Auction();
					auction.setAuctionId(result.getInt("auctionID"));
					auction.setStartingPrice(result.getDouble("startingPrice"));
					auction.setMinIncrease(result.getDouble("minIncrease"));
					auction.setEndingDate(result.getTimestamp("endingDate"));
					auction.setOwnerId(result.getString("ownerID"));
					auction.setProductName(result.getString("productName"));
					auction.setDescription(result.getString("description"));
					auction.setTimeDiff(result.getInt("timeDiff"));
					auction.setProductImage(null); //todo get image from db
					String isClosed = result.getString("isClosed");
					if (isClosed.equals("1"))
						auction.setClosed(true);
					else
						auction.setClosed(false);
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return auction;
	}
	
}
