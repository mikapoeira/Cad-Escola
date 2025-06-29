package com.cadescola.repository;

import com.cadescola.model.Student;
import com.cadescola.model.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    // Busca por nome, email ou curso
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.course) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Student> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    // Busca por status
    List<Student> findByStatus(StudentStatus status);
    
    // Conta alunos por status
    long countByStatus(StudentStatus status);
    
    // Busca por curso
    List<Student> findByCourseContainingIgnoreCase(String course);
    
    // Verifica se email já existe
    boolean existsByEmail(String email);
    
    // Busca cursos únicos
    @Query("SELECT DISTINCT s.course FROM Student s ORDER BY s.course")
    List<String> findDistinctCourses();
}
