package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class StudentServiceTest {

    private static final Long ID = 1L;
    private static final String FIRST_NAME = "Alice";
    private static final String LAST_NAME = "Martin";
    private static final String EMAIL = "alice@example.com";
    private static final String CURSUS = "Informatique";

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

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
    public void findAll_returnsAllStudents() {
        // GIVEN
        Student student = buildStudent();
        when(studentRepository.findAll()).thenReturn(List.of(student));

        // WHEN
        List<Student> result = studentService.findAll();

        // THEN
        assertThat(result).hasSize(1).contains(student);
    }

    @Test
    public void findById_found_returnsStudent() {
        // GIVEN
        Student student = buildStudent();
        when(studentRepository.findById(ID)).thenReturn(Optional.of(student));

        // WHEN
        Optional<Student> result = studentService.findById(ID);

        // THEN
        assertThat(result).isPresent().contains(student);
    }

    @Test
    public void findById_notFound_returnsEmpty() {
        // GIVEN
        when(studentRepository.findById(ID)).thenReturn(Optional.empty());

        // WHEN
        Optional<Student> result = studentService.findById(ID);

        // THEN
        assertThat(result).isEmpty();
    }

    @Test
    public void create_savesAndReturnsStudent() {
        // GIVEN
        Student student = buildStudent();
        when(studentRepository.save(student)).thenReturn(student);

        // WHEN
        Student result = studentService.create(student);

        // THEN
        assertThat(result).isEqualTo(student);
        verify(studentRepository).save(student);
    }

    @Test
    public void update_found_updatesAndReturnsStudent() {
        // GIVEN
        Student existing = buildStudent();
        Student updated = new Student();
        updated.setFirstName("Bob");
        updated.setLastName("Dupont");
        updated.setEmail("bob@example.com");
        updated.setCursus("Mathématiques");

        when(studentRepository.findById(ID)).thenReturn(Optional.of(existing));
        when(studentRepository.save(any(Student.class))).thenReturn(existing);

        // WHEN
        Student result = studentService.update(ID, updated);

        // THEN
        assertThat(result.getFirstName()).isEqualTo("Bob");
        assertThat(result.getLastName()).isEqualTo("Dupont");
        assertThat(result.getEmail()).isEqualTo("bob@example.com");
        assertThat(result.getCursus()).isEqualTo("Mathématiques");
        verify(studentRepository).save(existing);
    }

    @Test
    public void update_notFound_throwsIllegalArgumentException() {
        // GIVEN
        when(studentRepository.findById(ID)).thenReturn(Optional.empty());

        // THEN
        assertThrows(IllegalArgumentException.class,
                () -> studentService.update(ID, new Student()));
    }

    @Test
    public void delete_found_deletesStudent() {
        // GIVEN
        when(studentRepository.existsById(ID)).thenReturn(true);

        // WHEN
        studentService.delete(ID);

        // THEN
        verify(studentRepository).deleteById(ID);
    }

    @Test
    public void delete_notFound_throwsIllegalArgumentException() {
        // GIVEN
        when(studentRepository.existsById(ID)).thenReturn(false);

        // THEN
        assertThrows(IllegalArgumentException.class,
                () -> studentService.delete(ID));
    }
}
