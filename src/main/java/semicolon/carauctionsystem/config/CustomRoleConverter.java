package semicolon.carauctionsystem.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class CustomRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Object rolesObj = jwt.getClaim("roles");

        if (!(rolesObj instanceof List)) {
            return Collections.emptyList();
        }

        List<?> roleList = (List<?>) rolesObj;

        return roleList.stream()
                .filter(role -> role instanceof Map)
                .map(role -> (Map<?, ?>) role)
                .map(roleMap -> (String) roleMap.get("authority"))
                .filter(Objects::nonNull)
                .map(role -> "ROLE_" + role)  // Spring expects "ROLE_BUYER"
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}

