package semicolon.carauctionsystem.users.utils;


public class Utils {

    public static String getWelcomeMessage(String name){
        return """
                🎉 Welcome to AuctionX! %s \s
                
                Your account has been successfully created.
                Start bidding, selling, and winning great deals today.
                
                👉 Visit your dashboard to explore live auctions!
                
                Happy Bidding %s! 🛍️""".formatted(name, name);
    }

    public static String getEmailSubject(){
        return "Registration Successful";
    }

    public static String getFailedEmailSubject(){
        return "Login Failed";
    }

    public static String getIncorrectPasswordMessage() {
        return """
            ❌ Login Failed for this Account
                       \s
            The password you entered is incorrect.
            Please double-check your credentials and try again.
           \s
            🔒 Too many failed attempts may lock your account temporarily.
           \s
            Need help? 👉 Visit the password reset page or contact support.
           \s""";
    }

    public static String getAdminWelcomeMessage(String name){

        return """
                Hello %s,
                
                Welcome aboard! \s
                Your admin account for the Car Auction System has been successfully created.
                
                As an administrator, you now have privileged access to manage auctions, approve listings, and oversee platform activity. \s
                Please remember that your admin credentials are highly sensitive. \s
                
                ⚠️ For your security:
                - Never share your password with anyone.
                - Use a strong, unique password.
                - If you suspect any unauthorized access, reset your password immediately.
                
                Y
                Thank you for keeping our platform secure and efficient.
                
                Best regards, \s
                The Car Auction System Team
                
                """.formatted(name);
    }
}
