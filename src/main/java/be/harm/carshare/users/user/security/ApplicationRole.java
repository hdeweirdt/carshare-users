package be.harm.carshare.users.user.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ApplicationRole {
    USER(Collections.emptyList()),
    ADMIN(Collections.singletonList(
            ApplicationPermission.GET_ALL_USERS));

    private final List<ApplicationPermission> permissions;

    ApplicationRole(List<ApplicationPermission> permissions) {
        this.permissions = permissions;
    }

    /**
     * Returns all authorities associated with a role.
     *
     * @return the authorities (by name) and the role (prefixed by ROLE_)
     */
    public Set<SimpleGrantedAuthority> getGrantedAuthorities () {
        var authorities =  permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return authorities;
    }

    @Converter
    public static class ApplicationRoleConverter implements AttributeConverter<Set<ApplicationRole>, String> {

        private static final String DELIMITER = ";";

        @Override
        public String convertToDatabaseColumn(Set<ApplicationRole> applicationRoles) {
            if (applicationRoles.isEmpty()) {
                return null;
            } else {
                return applicationRoles.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(DELIMITER));
            }
        }

        @Override
        public Set<ApplicationRole> convertToEntityAttribute(String s) {
            if (StringUtils.isEmpty(s)) {
                return EnumSet.noneOf(ApplicationRole.class);
            } else {
                return Stream.of(s.split(DELIMITER))
                        .map(ApplicationRole::valueOf)
                        .collect(Collectors.toSet());
            }
        }
    }
}
