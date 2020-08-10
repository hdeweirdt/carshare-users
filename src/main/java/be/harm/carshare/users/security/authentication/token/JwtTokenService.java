package be.harm.carshare.users.security.authentication.token;

import be.harm.carshare.users.security.authentication.AuthenticatedUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public final class JwtTokenService implements TokenService {

    @Value("${carshare.users.jwt.secret}")
    private String tokenSecret;

    @Override
    public String createToken(AuthenticatedUser user) {
        Algorithm tokenEncryptionAlgorithm = Algorithm.HMAC256(tokenSecret);
        final Date now = new Date();
        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(getExpirationDate(now))
                .sign(tokenEncryptionAlgorithm);
    }

    /**
     * @return the name of the verified user
     */
    @Override
    public Optional<String> verify(String token) {
        Algorithm tokenEncryptionAlgorithm = Algorithm.HMAC256(tokenSecret);
        JWTVerifier verifier = JWT.require(tokenEncryptionAlgorithm).build();
        try {
            DecodedJWT decodedToken = verifier.verify(token);
            return Optional.of(decodedToken.getSubject());
        } catch (JWTVerificationException verificationException) {
            return Optional.empty();
        }
    }

    private Date getExpirationDate(Date start) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }
}

