package semicolon.carauctionsystem.auctions.data.repositories;

import org.springframework.stereotype.Repository;
import semicolon.carauctionsystem.auctions.data.models.Bid;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class TempBidRepository {

    private final List<Bid> bids = new ArrayList<>();


    public void save(Bid bid) {
        bids.add(bid);
    }

    public List<Bid> getBidsByAuctionId(UUID auctionId) {
        return bids.stream().filter(bid -> auctionId.equals(bid.getAuctionId())).collect(Collectors.toList());
    }

}
