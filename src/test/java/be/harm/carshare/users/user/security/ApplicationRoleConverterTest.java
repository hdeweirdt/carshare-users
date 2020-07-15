package be.harm.carshare.users.user.security;

import org.apache.commons.collections4.SetUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationRoleConverterTest {
    private final ApplicationRole.ApplicationRoleConverter converter = new ApplicationRole.ApplicationRoleConverter();

    @Test
    public void convertingFromEntityToColumnAndBackShouldBeIdempotent() {
        // Given
        Set<ApplicationRole> roles = SetUtils.hashSet(ApplicationRole.ADMIN, ApplicationRole.USER);

        // When
        String convertedRoles = converter.convertToDatabaseColumn(roles);
        Set<ApplicationRole> resultingRoles = converter.convertToEntityAttribute(convertedRoles);

        // Then
        assertEquals(2, resultingRoles.size());
        assertTrue(resultingRoles.contains(ApplicationRole.ADMIN));
        assertTrue(resultingRoles.contains(ApplicationRole.USER));
    }

}