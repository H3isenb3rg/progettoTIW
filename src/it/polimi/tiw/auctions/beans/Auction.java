package it.polimi.tiw.auctions.beans;

import java.awt.Image;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class Auction {
	private int auctionId;
	private Double startingPrice;
	private Double minIncrease;
	private Timestamp endingDate;
	private String ownerId;
	private String productName;
	private String description;
	private Image productImage;
	private Integer timeDiff; //In hours
	private boolean isClosed;
	
	public int getTimeDiff() {
		return timeDiff;
	}
	
	public void setTimeDiff(Integer timeDiff) {
		this.timeDiff = timeDiff;
	}
	
	public int getAuctionId() {
		return auctionId;
	}
	public void setAuctionId(int auctionId) {
		this.auctionId = auctionId;
	}

	public Double getStartingPrice() {
		return startingPrice;
	}
	public void setStartingPrice(Double startingPrice) {
		this.startingPrice = startingPrice;
	}
	
	public Double getMinIncrease() {
		return minIncrease;
	}
	public void setMinIncrease(Double minIncrease) {
		this.minIncrease = minIncrease;
	}
	
	public Timestamp getEndingDate() {
		return endingDate;
	}
	public void setEndingDate(Timestamp endingdate) {
		this.endingDate = endingdate;
	}
	
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Image getProductImage() {
		return productImage;
	}
	public void setProductImage(Image productImage) {
		this.productImage = productImage;
	}
	
	public boolean isClosed() {
		return isClosed;
	}
	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}
}
