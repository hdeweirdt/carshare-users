package be.harm.carshare.users.user

import be.harm.carshare.users.testutil.WithMockCustomUser
import com.fasterxml.jackson.databind.ObjectMapper
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(UserRestController.class)
@ActiveProfiles("local")
@AutoConfigureMockMvc
class UserRestControllerTest extends Specification {

    @Autowired
    ObjectMapper mapper

    @Autowired
    private MockMvc mockMvc

    @SpringBean
    private UserService userService = Mock()

    def "when getting an existing user it is returned"() {
        given: "the users exists in the system"
        User user = User.builder()
                .id(1L)
                .build()
        userService.findById(1L) >> Optional.of(user)

        when: "we request the user"
        ResultActions request = mockMvc.perform(get("/users/1"))

        then: "response status is 200"
        request.andExpect(status().isOk())

        and: "the response is returned as JSON"
        request.andExpect(content().contentType(MediaType.APPLICATION_JSON))

        and: "the body holds the user"
        request.andExpect(jsonPath("\$.id").value(1L))
    }

    def "when getting a non-existing user, not-found is returned"() {
        given: "the users does not exist in the system"
        userService.findById(1L) >> Optional.empty()

        when: "we request the user"
        ResultActions request = mockMvc.perform(get("/users/1"))

        then: "response status is 404"
        request.andExpect(status().isNotFound())

        and: "the response content is empty"
        request.andReturn().response.contentAsString.isEmpty()
    }

    def "when registering a valid user, it is saved into the system and its location is returned"() {
        given: "A new user"
        User newUser = User.builder()
                .firstName("Xander")
                .lastName("De Rycke")
                .userName("Xanderke")
                .password("Xanders1eGeheimeWachtwoord")
                .build()

        and: "a service that will register new users"
        1 * userService.saveUser(_ as User) >> { User user -> user.setId(3l); user }

        when: "he registers himself"
        ResultActions request = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser)))

        then: "the response is 'created'"
        request.andExpect(status().isCreated())

        then: "the uri of the created user is returned"
        request.andReturn()
                .response.getHeader("location").endsWith("/users/3")
    }

    def "when registering an invalid user, no user is created and the error is reported"() {
        given: "A new user"
        User newUser = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .userName(userName)
                .password(password)
                .build()

        and: "A service that will register new users"
        0 * userService.saveUser(_ as User) >> { User user -> user.setId(3l); user }

        when: "a user registers himself"
        ResultActions request = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser)))

        then: "the response is 'bad request'"
        request.andExpect(status().isBadRequest())

        where:
        firstName | lastName   | userName        | password
        "Xander"  | "De Rycke" | null            | "ValidPassword1"
        "Xander"  | "De Rycke" | ""              | "ValidPassword1"
        "Xander"  | "De Rycke" | "validusername" | "Short1"
        "Xander"  | "De Rycke" | "validusername" | "NoNumber"
        "Xander"  | "De Rycke" | "validusername" | "nocapitals"

    }

    @WithMockCustomUser(id = 1L)
    def "the currently logged in user may update itself"() {
        given: "An existing user"
        User updatedUser = User.builder()
                .id(1L)
                .firstName("Xander")
                .lastName("De Nieuwe Rycke")
                .password("TetstPaswo21")
                .userName("TTesttUser")
                .build()
        and: "a service that can update users"
        1 * userService.updateUser(_ as User) >> { User user -> user }

        when: "a puts an update to itself"
        ResultActions request = mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedUser)))

        then: "the update is OK"
        request.andExpect(status().isOk())
    }

    @WithMockCustomUser(id = 2L)
    def "the currently logged in user may not update other users"() {
        given: "An existing user"
        User updatedUser = User.builder()
                .id(1L)
                .firstName("Xander")
                .lastName("De Nieuwe Rycke")
                .password("TetstPaswo21")
                .userName("TTesttUser")
                .build()
        and: "a service that can update users"
        0 * userService.updateUser(_ as User) >> { User user -> user }

        when: "a puts an update to another user"
        ResultActions request = mockMvc.perform(put("/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedUser)))

        then: "the update is forbidden"
        request.andExpect(status().isForbidden())

        and: "no update was done"
        0 * userService.updateUser(_ as User)
    }
}
