package petro.post_hub2.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import petro.post_hub2.SecurityApplication;
import petro.post_hub2.model.dto.PostDto;
import petro.post_hub2.model.entity.User;
import petro.post_hub2.model.enums.PostSortField;
import petro.post_hub2.model.exceptions.InvalidDataException;
import petro.post_hub2.model.request.NewPostRequest;
import petro.post_hub2.model.request.PostSearchRequest;
import petro.post_hub2.model.request.UpdatePostRequest;
import petro.post_hub2.model.response.CustomResponse;
import petro.post_hub2.repository.UserRepository;
import petro.post_hub2.security.JwtTokenProvider;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = SecurityApplication.class)
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String adminJwt;
    private String userJwt;


    @BeforeAll
    void authorize() {

        User admin = userRepository.findByIdAndDeletedFalse(3).orElseThrow(() -> new InvalidDataException("admin with id 1 not found"));
        // Hibernate.initialize(admin.getRoles());
        this.adminJwt = "Bearer " + jwtTokenProvider.generatedToken(admin);
        User user = userRepository.findByIdAndDeletedFalse(2).orElseThrow(() -> new InvalidDataException("user with id 2 not found"));
        // Hibernate.initialize(user.getRoles());
        this.userJwt = "Bearer " + jwtTokenProvider.generatedToken(user);
    }

    @Test
    void getPosts_Ok_200() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/post/all")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getPostById_Ok_200() throws Exception {
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/post/3")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        CustomResponse<PostDto> response = convertResponseToPostDto(result.getResponse().getContentAsByteArray());
        assertTrue(response.isSuccess());

        PostDto postDto = Objects.nonNull(response.getBody()) ? response.getBody() : null;
        assertNotNull(postDto);
        assertEquals("roman_mentor", postDto.getTitle());
        assertEquals("success", postDto.getContent());
    }

    @Test
    void getById_NotFound_404() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/post/-1")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    void savePost_Ok() throws Exception {
        NewPostRequest request = new NewPostRequest("new day", "new comment day");
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/post/save")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        CustomResponse<PostDto> response = convertResponseToPostDto(result.getResponse().getContentAsByteArray());
        assertTrue(response.isSuccess());

        PostDto postDto = Objects.nonNull(response.getBody()) ? response.getBody() : null;
        assertNotNull(postDto);
        assertEquals(request.getTitle(), postDto.getTitle());
        assertEquals(request.getContent(), postDto.getContent());
    }

    @Test
    @Transactional
    void savePost_TitleExists_400() throws Exception {
        NewPostRequest request = new NewPostRequest("new day", "new comment day");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/post/save")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    @Transactional
    void updatePost_Ok() throws Exception {
        UpdatePostRequest request = new UpdatePostRequest("weather", "content of weather");

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/post/updated/11")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        CustomResponse<PostDto> response = convertResponseToPostDto(result.getResponse().getContentAsByteArray());
        assertTrue(response.isSuccess());

        PostDto postDto = Objects.nonNull(response.getBody()) ? response.getBody() : null;
        assertNotNull(postDto);
        assertEquals(request.getTitle(), postDto.getTitle());
        assertEquals(request.getContent(), postDto.getContent());
    }

    @Test
    @Transactional
    void updatePost_NotFound_404() throws Exception {
        UpdatePostRequest request = new UpdatePostRequest("weather", "content weather");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/post/updated/-1")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    void updatedPost_DontHaveAccess_403() throws Exception {
        UpdatePostRequest request = new UpdatePostRequest("weather", "content weather");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/post/updated/5")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void updatedPost_adminById_Ok() throws Exception {
        UpdatePostRequest request = new UpdatePostRequest("weather", "content of weather");
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/post/updated/5")
                                .header("Authorization", adminJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        CustomResponse<PostDto> response = convertResponseToPostDto(result.getResponse().getContentAsByteArray());
        assertTrue(response.isSuccess());

        PostDto postDto = Objects.nonNull(response.getBody()) ? response.getBody() : null;
        assertNotNull(postDto);

        assertEquals(request.getTitle(), postDto.getTitle());
        assertEquals(request.getContent(), postDto.getContent());
    }

    @Test
    @Transactional
    void deletedPost_Ok() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/post/2")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void deletedPost_NotFound_404() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/post/-1")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    void deletedPost_DontHaveAccess() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/post/5")   // ПЕРЕВІРИТИ   !!!
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void deletedPost_adminById_Ok() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/post/5")   //  ПЕРЕВІРИТИ !!!
                                .header("Authorization", adminJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void getAllPost_Ok() throws Exception {
        PostSearchRequest request = new PostSearchRequest("", "", false, "", PostSortField.CONTENT);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/post/search")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    private CustomResponse<PostDto> convertResponseToPostDto(byte[] content) throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(content, new TypeReference<>() {
        });
    }
}
