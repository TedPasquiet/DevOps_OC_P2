package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.configuration.security.CustomUserDetailService;
import com.openclassrooms.etudiant.configuration.security.JwtAuthenticationFilter;
import com.openclassrooms.etudiant.dto.StudentDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentDtoMapper;
import com.openclassrooms.etudiant.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StudentControllerTest {

    private static final String BASE_URL = "/api/students";
    private static final Long ID = 1L;
    private static final String FIRST_NAME = "Alice";
    private static final String LAST_NAME = "Martin";
    private static final String EMAIL = "alice@example.com";
    private static final String CURSUS = "Informatique";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private StudentDtoMapper studentDtoMapper;

    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private StudentDTO buildStudentDTO() {
        StudentDTO dto = new StudentDTO();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);
        dto.setCursus(CURSUS);
        return dto;
    }

    private Student buildStudent() {
        Student student = new Student();
        student.setId(ID);
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        student.setCursus(CURSUS);
        return student;
    }

    @Test
    public void getAll_returns200WithList() throws Exception {
        // GIVEN
        StudentDTO dto = buildStudentDTO();
        when(studentService.findAll()).thenReturn(List.of(buildStudent()));
        when(studentDtoMapper.toDto(any(Student.class))).thenReturn(dto);

        // WHEN / THEN
        mockMvc.perform(get(BASE_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$[0].email").value(EMAIL));
    }

    @Test
    public void getById_found_returns200() throws Exception {
        // GIVEN
        Student student = buildStudent();
        StudentDTO dto = buildStudentDTO();
        when(studentService.findById(ID)).thenReturn(Optional.of(student));
        when(studentDtoMapper.toDto(student)).thenReturn(dto);

        // WHEN / THEN
        mockMvc.perform(get(BASE_URL + "/" + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME));
    }

    @Test
    public void getById_notFound_returns404() throws Exception {
        // GIVEN
        when(studentService.findById(ID)).thenReturn(Optional.empty());

        // WHEN / THEN
        mockMvc.perform(get(BASE_URL + "/" + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void create_validDTO_returns201() throws Exception {
        // GIVEN
        StudentDTO dto = buildStudentDTO();
        Student student = buildStudent();
        when(studentDtoMapper.toEntity(any(StudentDTO.class))).thenReturn(student);
        when(studentService.create(any(Student.class))).thenReturn(student);
        when(studentDtoMapper.toDto(student)).thenReturn(dto);

        // WHEN / THEN
        mockMvc.perform(post(BASE_URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void create_invalidDTO_returns400() throws Exception {
        // GIVEN - empty DTO missing required fields
        StudentDTO dto = new StudentDTO();

        // WHEN / THEN
        mockMvc.perform(post(BASE_URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_validDTO_verifyMapperAndServiceAreCalled() throws Exception {
        // GIVEN
        StudentDTO dto = buildStudentDTO();
        Student student = buildStudent();
        when(studentDtoMapper.toEntity(any(StudentDTO.class))).thenReturn(student);
        when(studentService.create(any(Student.class))).thenReturn(student);
        when(studentDtoMapper.toDto(student)).thenReturn(dto);

        // WHEN
        mockMvc.perform(post(BASE_URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // THEN
        verify(studentDtoMapper).toEntity(any(StudentDTO.class));
        verify(studentService).create(any(Student.class));
    }

    @Test
    public void update_found_returns200() throws Exception {
        // GIVEN
        StudentDTO dto = buildStudentDTO();
        Student student = buildStudent();
        when(studentDtoMapper.toEntity(any(StudentDTO.class))).thenReturn(student);
        when(studentService.update(eq(ID), any(Student.class))).thenReturn(student);
        when(studentDtoMapper.toDto(student)).thenReturn(dto);

        // WHEN / THEN
        mockMvc.perform(put(BASE_URL + "/" + ID)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME));
    }

    @Test
    public void update_notFound_returns400() throws Exception {
        // GIVEN
        StudentDTO dto = buildStudentDTO();
        Student student = buildStudent();
        when(studentDtoMapper.toEntity(any(StudentDTO.class))).thenReturn(student);
        doThrow(new IllegalArgumentException("Student not found with id: " + ID))
                .when(studentService).update(eq(ID), any(Student.class));

        // WHEN / THEN
        mockMvc.perform(put(BASE_URL + "/" + ID)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_found_returns204() throws Exception {
        // WHEN / THEN
        mockMvc.perform(delete(BASE_URL + "/" + ID))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void delete_notFound_returns400() throws Exception {
        // GIVEN
        doThrow(new IllegalArgumentException("Student not found with id: " + ID))
                .when(studentService).delete(ID);

        // WHEN / THEN
        mockMvc.perform(delete(BASE_URL + "/" + ID))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
