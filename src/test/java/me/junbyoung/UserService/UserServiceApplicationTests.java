package me.junbyoung.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.junbyoung.UserService.model.User;
import me.junbyoung.UserService.payload.SignUpRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EmbeddedKafka(partitions = 1, topics = {"user-events"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceApplicationTests {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private KafkaTemplate<String, Long> kafkaTemplate; // KafkaTemplate을 Mock 처리

	private User newUser;

	@BeforeEach
	void setUp() {
		when(kafkaTemplate.send(eq("user-events"), anyLong()))
				.thenReturn(CompletableFuture.completedFuture(null));
	}

	@Test
	@Order(1)
	void addUser() throws Exception {
		SignUpRequest signUpRequest = new SignUpRequest("tester","test@test.com","1234");
		String jsonContent = objectMapper.writeValueAsString(signUpRequest);

		newUser = objectMapper.readValue(
				mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonContent))
						.andExpect(status().isCreated())
						.andReturn()
						.getResponse()
						.getContentAsString(),
				User.class
		);
	}

	@Test
	@Order(2)
	void getUserInfo() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/users/me")
						.header("X-User-Id",newUser.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(newUser.getName()))
				.andExpect(jsonPath("$.email").value(newUser.getEmail()));
	}

	@Test
	@Order(3)
	void getUser() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{userId}",newUser.getId())
						.header("User-Agent","FeignClient"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(newUser.getName()))
				.andExpect(jsonPath("$.email").value(newUser.getEmail()));
	}

	@Test
	@Order(4)
	void getUserWithoutHeader() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{userId}",newUser.getId()))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@Order(5)
	void deleteUser() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/users")
						.header("X-User-Id",newUser.getId()))
				.andExpect(status().isNoContent());

		verify(kafkaTemplate).send("user-events", newUser.getId()); // 카프카서버에 메시지 전송 여부 확인
	}
}
