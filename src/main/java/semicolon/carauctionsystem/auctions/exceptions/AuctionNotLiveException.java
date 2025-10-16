package semicolon.carauctionsystem.auctions.exceptions;

public class AuctionNotLiveException extends  RuntimeException {
    public AuctionNotLiveException() {
        super("Auction Not Live");
    }
}
