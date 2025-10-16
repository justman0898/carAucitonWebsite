package semicolon.carauctionsystem.auctions.exceptions;

public class AuctionNotFoundException extends  RuntimeException {
    public AuctionNotFoundException(String auctionId) {
        super(String.format("Auction Not Found: %s", auctionId));
    }
}
