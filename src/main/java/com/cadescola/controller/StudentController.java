package com.cadescola.controller;

import com.cadescola.model.Student;
import com.cadescola.model.StudentStatus;
import com.cadescola.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    private final List<String> availableCourses = Arrays.asList(
        "Ensino Fundamental I",
        "Ensino Fundamental II", 
        "Ensino Médio",
        "Educação Infantil",
        "EJA - Educação de Jovens e Adultos",
        "Curso Técnico",
        "Pré-Vestibular"
    );
    
    // Página principal - Dashboard
    @GetMapping
    public String dashboard(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Page<Student> studentsPage = studentService.searchStudents(search, page, size);
        
        // Estatísticas
        model.addAttribute("totalStudents", studentService.getTotalStudents());
        model.addAttribute("activeStudents", studentService.getActiveStudents());
        model.addAttribute("inactiveStudents", studentService.getInactiveStudents());
        model.addAttribute("totalCourses", studentService.getAllCourses().size());
        
        // Lista de estudantes
        model.addAttribute("studentsPage", studentsPage);
        model.addAttribute("students", studentsPage.getContent());
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentsPage.getTotalPages());
        
        return "students/dashboard";
    }
    
    // Formulário para novo estudante
    @GetMapping("/new")
    public String newStudentForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("courses", availableCourses);
        model.addAttribute("statuses", StudentStatus.values());
        return "students/form";
    }
    
    // Salvar novo estudante
    @PostMapping
    public String saveStudent(@Valid @ModelAttribute Student student, 
                             BindingResult result, 
                             Model model,
                             RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("courses", availableCourses);
            model.addAttribute("statuses", StudentStatus.values());
            return "students/form";
        }
        
        try {
            studentService.save(student);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Estudante cadastrado com sucesso!");
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("courses", availableCourses);
            model.addAttribute("statuses", StudentStatus.values());
            return "students/form";
        }
        
        return "redirect:/students";
    }
    
    // Formulário para editar estudante
    @GetMapping("/{id}/edit")
    public String editStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));
        
        model.addAttribute("student", student);
        model.addAttribute("courses", availableCourses);
        model.addAttribute("statuses", StudentStatus.values());
        return "students/form";
    }
    
    // Atualizar estudante
    @PostMapping("/{id}")
    public String updateStudent(@PathVariable Long id,
                               @Valid @ModelAttribute Student student,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            student.setId(id);
            model.addAttribute("courses", availableCourses);
            model.addAttribute("statuses", StudentStatus.values());
            return "students/form";
        }
        
        try {
            studentService.update(id, student);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Estudante atualizado com sucesso!");
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            student.setId(id);
            model.addAttribute("courses", availableCourses);
            model.addAttribute("statuses", StudentStatus.values());
            return "students/form";
        }
        
        return "redirect:/students";
    }
    
    // Deletar estudante
    @PostMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Estudante excluído com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/students";
    }
    
    // Toggle status do estudante
    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Student student = studentService.toggleStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Status do estudante " + student.getName() + " alterado para " + 
                student.getStatus().getDisplayName());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/students";
    }
    
    // Visualizar detalhes do estudante
    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));
        
        model.addAttribute("student", student);
        return "students/view";
    }
}
