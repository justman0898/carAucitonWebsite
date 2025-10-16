package semicolon.carauctionsystem.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import semicolon.carauctionsystem.auctions.data.models.Bid;
import semicolon.carauctionsystem.auctions.dtos.response.BidResponseDto;
import semicolon.carauctionsystem.auctions.services.BidService;
import semicolon.carauctionsystem.auctions.services.BidServiceImpl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class AuctionWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private BidService bidService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, List<WebSocketSession>> auctionSessions = new ConcurrentHashMap<>();
    @Autowired
    private BidServiceImpl bidServiceImpl;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String auctionId = getAuctionIdFromSession(session);
        auctionSessions.computeIfAbsent(auctionId, k-> new CopyOnWriteArrayList<>()).add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String auctionId = getAuctionIdFromSession(session);
        List<WebSocketSession> sessions = auctionSessions.get(auctionId);
        if (sessions != null) sessions.remove(session);
    }

    public void broadcastBids(UUID auctionId, BidResponseDto bidResponseDto) {
//        List<Bid> auctionBids = bidServiceImpl.getCurrentBidsByAuctionId(auctionId);
        try {
            String json = objectMapper.writeValueAsString(bidResponseDto);

            String stringAuctionId = auctionId.toString();
            List<WebSocketSession> sessions = auctionSessions.getOrDefault(stringAuctionId, Collections.emptyList());
            sessions.forEach(session -> {
                try {
                    session.sendMessage(new TextMessage(json));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAuctionIdFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if(query != null && query.startsWith("auctionId=")) {
            return query.substring("auctionId=".length());
        }
        return "all";
    }




}
