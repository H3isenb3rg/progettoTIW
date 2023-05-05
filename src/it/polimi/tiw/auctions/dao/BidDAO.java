package it.polimi.tiw.auctions.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.auctions.beans.Bid;

public class BidDAO {
	private Connection connection;

	public BidDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Bid findHighestBid(int auctionId) throws SQLException {
		Bid bid = null;

		String query = "SELECT * "
				+ "FROM bid AS b "
				+ "WHERE auctionID=? AND offer=( "
				+ "	SELECT MAX(offer) "
				+ "    FROM bid "
				+ "    WHERE auctionID=? "
				+ ")";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, auctionId);
			pstatement.setInt(2, auctionId);
			System.out.println(pstatement);
			try (ResultSet result = pstatement.executeQuery();) {
				System.out.println("Query Done");
				while (result.next()) {
					System.out.println("Reading Result Row");
					bid = new Bid();
					bid.setAuctionId(result.getInt("auctionID"));
					bid.setBidId(result.getInt("bidID"));
					bid.setDate(result.getDate("date"));
					bid.setOffer(result.getInt("offer"));
					bid.setBidder(result.getString("bidder"));
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return bid;
	}

	public List<Bid> getBidsForAuction(Integer auctionID) throws SQLException {
		List<Bid> bids = new ArrayList<Bid>();

		String query = "SELECT *"
				+ " FROM bid AS b "
				+ " WHERE auctionID = ?"
				+ " ORDER BY offer DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, auctionID);
			System.out.println(pstatement); // Debug
			try (ResultSet result = pstatement.executeQuery();) {
				System.out.println("query done");
				while (result.next()) {
					System.out.println("reading result");
					Bid bid = new Bid();
					bid.setBidId(result.getInt("bidID"));
					bid.setAuctionId(result.getInt("auctionID"));
					bid.setOffer(result.getDouble("offer"));
					bid.setBidder(result.getString("bidder"));
					bid.setDate(result.getDate("date"));
					bids.add(bid);
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return bids;
	}
	
	public void createBid(Integer auctionID, Double offer, String bidder)
			throws SQLException {
		String query = "INSERT into bid (auctionID, offer, date, bidder) VALUES(?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, auctionID);
			pstatement.setDouble(2, offer);
			pstatement.setTimestamp(3, java.sql.Timestamp.from(java.time.Instant.now()));
			pstatement.setString(4, bidder);
			
			pstatement.executeUpdate();
		}
	}
}
