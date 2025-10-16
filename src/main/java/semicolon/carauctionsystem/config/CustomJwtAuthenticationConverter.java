package semicolon.carauctionsystem.config;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

public class CustomJwtAuthenticationConverter extends JwtAuthenticationConverter {

    public CustomJwtAuthenticationConverter() {
        this.setJwtGrantedAuthoritiesConverter(new CustomRoleConverter());
    }
}

