package com.cadescola.service;

import com.cadescola.model.Student;
import com.cadescola.model.StudentStatus;
import com.cadescola.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    // Listar todos os estudantes com paginação
    public Page<Student> findAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return studentRepository.findAll(pageable);
    }
    
    // Buscar estudantes por termo
    public Page<Student> searchStudents(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return studentRepository.findAll(pageable);
        }
        return studentRepository.findBySearchTerm(searchTerm.trim(), pageable);
    }
    
    // Buscar por ID
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }
    
    // Salvar estudante
    public Student save(Student student) {
        if (student.getId() == null && studentRepository.existsByEmail(student.getEmail())) {
            throw new RuntimeException("Email já cadastrado no sistema");
        }
        return studentRepository.save(student);
    }
    
    // Atualizar estudante
    public Student update(Long id, Student studentDetails) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));
        
        // Verificar se email já existe (exceto para o próprio estudante)
        if (!student.getEmail().equals(studentDetails.getEmail()) && 
            studentRepository.existsByEmail(studentDetails.getEmail())) {
            throw new RuntimeException("Email já cadastrado no sistema");
        }
        
        student.setName(studentDetails.getName());
        student.setEmail(studentDetails.getEmail());
        student.setPhone(studentDetails.getPhone());
        student.setCourse(studentDetails.getCourse());
        student.setStatus(studentDetails.getStatus());
        
        return studentRepository.save(student);
    }
    
    // Deletar estudante
    public void deleteById(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Estudante não encontrado");
        }
        studentRepository.deleteById(id);
    }
    
    // Estatísticas
    public long getTotalStudents() {
        return studentRepository.count();
    }
    
    public long getActiveStudents() {
        return studentRepository.countByStatus(StudentStatus.ACTIVE);
    }
    
    public long getInactiveStudents() {
        return studentRepository.countByStatus(StudentStatus.INACTIVE);
    }
    
    public List<String> getAllCourses() {
        return studentRepository.findDistinctCourses();
    }
    
    // Ativar/Desativar estudante
    public Student toggleStatus(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));
        
        student.setStatus(student.getStatus() == StudentStatus.ACTIVE ? 
                         StudentStatus.INACTIVE : StudentStatus.ACTIVE);
        
        return studentRepository.save(student);
    }
}
