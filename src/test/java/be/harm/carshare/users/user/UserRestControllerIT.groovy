package be.harm.carshare.users.user

import be.harm.carshare.users.CarshareUsersApplication
import be.harm.carshare.users.security.authentication.token.JwtTokenService
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang.StringUtils
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import javax.servlet.Filter

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(classes = CarshareUsersApplication.class)
@TestPropertySource(locations = "classpath:application-local.properties")
@ActiveProfiles("local")
@WebAppConfiguration
@ContextConfiguration
@ExtendWith(SpringExtension.class)
class UserRestControllerIT extends Specification {

    @Autowired
    ObjectMapper mapper

    private MockMvc mvc

    @Autowired
    private WebApplicationContext context

    @Autowired
    private Filter springSecurityFilterChain

    @Autowired
    private JwtTokenService jwtTokenService

    def setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build()
    }


    def "after logging in the Authorization header contains a JWT token for the logged in user."() {
        given: "a user"
        User user = new User("userName", "SafePassword1")

        and: "the user has registered itself"
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))

        when: "the users logs in"
        ResultActions request = mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))

        then: "the Authorization header has a JWT token"
        String jwtToken = StringUtils.removeStart(request.andReturn().response.getHeader("Authorization"), "Bearer").trim()

        and: "it contains the users name"
        String userNameFromToken = jwtTokenService.verify(jwtToken).get()
        userNameFromToken == user.getUserName()
    }

    def "The only one that can update a user is the user itself"() {
        given: "a user"
        User user = new User("userName2", "SafePassword1")

        and: "the user has registered itself"
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))

        and: "the user is logged in."
        ResultActions loginRequest = mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
        String jwtToken = loginRequest.andReturn().response.getHeader("Authorization")

        when: "The user sends an update"
        user.setDrivingLicenseNumber("123")
        ResultActions request = mvc.perform(put("/users/3")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", jwtToken)
                .content(mapper.writeValueAsString(user)))

        then: "the request is accepted and returns status code OK"
        request.andExpect(status().isOk())


    }
}
