package semicolon.carauctionsystem.auctions.utils;

import org.springframework.http.ResponseEntity;

public class Utility {

    public static ResponseEntity<String> checkStatus(String result){
        if(result.equals("Bid placed successfully")) {
            return ResponseEntity.ok("Bid placed successfully");
        } else if (result.startsWith("Bid failed")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.internalServerError().body(result);
    }
}
