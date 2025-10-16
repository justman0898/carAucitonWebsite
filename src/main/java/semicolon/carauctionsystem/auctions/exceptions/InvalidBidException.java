package semicolon.carauctionsystem.auctions.exceptions;

public class InvalidBidException extends IllegalArgumentException {
    public InvalidBidException() {
        super("Invalid bid request");
    }
}
