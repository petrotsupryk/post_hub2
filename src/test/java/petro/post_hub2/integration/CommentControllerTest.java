package petro.post_hub2.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import petro.post_hub2.SecurityApplication;
import petro.post_hub2.model.dto.CommentDto;
import petro.post_hub2.model.entity.User;
import petro.post_hub2.model.enums.CommentSortField;
import petro.post_hub2.model.exceptions.InvalidDataException;
import petro.post_hub2.model.request.CommentSearchRequest;
import petro.post_hub2.model.request.NewCommentRequest;
import petro.post_hub2.model.request.UpdateCommentRequest;
import petro.post_hub2.model.response.CustomResponse;
import petro.post_hub2.repository.UserRepository;
import petro.post_hub2.security.JwtTokenProvider;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = SecurityApplication.class)
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class CommentControllerTest {
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
        User admin = userRepository.findByIdAndDeletedFalse(1).orElseThrow(() ->
                new InvalidDataException("Admin with id 1 not found"));
        this.adminJwt = "Bearer " + jwtTokenProvider.generatedToken(admin);

        User user = userRepository.findByIdAndDeletedFalse(2).orElseThrow(() ->
                new InvalidDataException("User with id 2 not found"));
        this.userJwt = "Bearer " + jwtTokenProvider.generatedToken(user);
    }

    @Test
    void getComments_Ok_200() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/comment/all")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getCommentById_Ok() throws Exception {
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/comment/1")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        CustomResponse<CommentDto> response = convertResponseToCommentDto(result.getResponse().getContentAsByteArray());
        assertTrue(response.isSuccess());

        CommentDto commentDto = Objects.nonNull(response.getBody()) ? response.getBody() : null;
        assertNotNull(commentDto);
        assertEquals("updated comment 7", commentDto.getMessage());
    }

    @Test
    void getCommentById_NotFoundException() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/comment/-1")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Transactional
    @Test
    void saveComment_Ok_200() throws Exception {
        NewCommentRequest request = new NewCommentRequest(8, "new Comment");
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/comment/save")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        CustomResponse<CommentDto> response = convertResponseToCommentDto(result.getResponse().getContentAsByteArray());
        assertTrue(response.isSuccess());

        CommentDto commentDto = Objects.nonNull(response.getBody()) ? response.getBody() : null;
        assertNotNull(commentDto);
        assertEquals(request.getMessage(), commentDto.getMessage());
    }

    @Test
    @Transactional
    void saveComment_UserNotFound_404() throws Exception {
        NewCommentRequest request = new NewCommentRequest(-1, "comment from post id -1");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/comment/save")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    void updateComment_Ok_200() throws Exception {
        UpdateCommentRequest request = new UpdateCommentRequest("comment from save");
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/comment/updated/5")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        CustomResponse<CommentDto> response = convertResponseToCommentDto(result.getResponse().getContentAsByteArray());
        assertTrue(response.isSuccess());

        CommentDto commentDto = Objects.nonNull(response.getBody()) ? response.getBody() : null;
        assertNotNull(commentDto);
        assertEquals(request.getMessage(), commentDto.getMessage());
    }

    @Test
    @Transactional
    void updatedComment_NotFoundException_404() throws Exception {
        UpdateCommentRequest request = new UpdateCommentRequest("comment from save");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/comment/updated/-1")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    void updatedComment_DontHaveAccess_403() throws Exception {
        UpdateCommentRequest request = new UpdateCommentRequest("comment from save");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/comment/updated/2")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void updatedComment_ByIdAdmin_Ok_200() throws Exception {
        UpdateCommentRequest request = new UpdateCommentRequest("comment from save");
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/comment/updated/1")
                                .header("Authorization", adminJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        CustomResponse<CommentDto> response = convertResponseToCommentDto(result.getResponse().getContentAsByteArray());
        assertTrue(response.isSuccess());

        CommentDto commentDto = Objects.nonNull(response.getBody()) ? response.getBody() : null;
        assertNotNull(commentDto);
        assertEquals(request.getMessage(), commentDto.getMessage());
    }

    @Test
    @Transactional
    void deletedComment_Ok_200() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/comment/5")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void deletedComment_NotFound_404() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/comment/-1")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    void deletedComment_DontHaveAccess_403() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/comment/1")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void deletedComment_adminById_Ok() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/comment/5")
                                .header("Authorization", adminJwt)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void getAllComment_Ok() throws Exception {
        CommentSearchRequest request = new CommentSearchRequest("", 6, false, CommentSortField.MESSAGE);
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/comment/search")
                                .header("Authorization", userJwt)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private CustomResponse<CommentDto> convertResponseToCommentDto(byte[] content) throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(content, new TypeReference<>() {
        });
    }
}
