package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.config.ErrorMessageException;
import com.example.employeedirectoryproject.dto.SaveEmployeeDTO;
import com.example.employeedirectoryproject.mapper.EmployeeMapper;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Role;
import com.example.employeedirectoryproject.repository.DepartmentRepository;
import com.example.employeedirectoryproject.repository.EmployeeRepository;
import com.example.employeedirectoryproject.repository.PositionRepository;
import com.example.employeedirectoryproject.service.EmailSenderService;
import com.example.employeedirectoryproject.service.EmployeeService;
import com.example.employeedirectoryproject.service.serviceImpl.EmployeeServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class AdminController {
    private EmployeeRepository employeeRepository;
    private PositionRepository positionRepository;
    private DepartmentRepository departmentRepository;
    private EmployeeService employeeService;
    private EmailSenderService emailSenderService;

    @Autowired
    public AdminController(PositionRepository positionRepository,
                           DepartmentRepository departmentRepository,
                           EmployeeService employeeService,
                           EmailSenderService emailSenderService,
                           EmployeeRepository employeeRepository) {
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
        this.employeeService = employeeService;
        this.emailSenderService = emailSenderService;
        this.employeeRepository = employeeRepository;
    }

    @RequestMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/add_new_employee")
    public String addNewEmployeeForm(Model model) {
        model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("saveEmployeeDto", new SaveEmployeeDTO());
        return "add_new_employee";
    }

    @PostMapping("/add_new_employee")
    public String addNewEmployee(@Valid @ModelAttribute("saveEmployeeDto") SaveEmployeeDTO saveEmployeeDto,
                                 BindingResult result,
                                 Model model) throws MessagingException{
        try {
            if (!Objects.isNull(employeeService.findEmployeeByEmail(saveEmployeeDto.getEmail()))) {
                result.rejectValue("email", null, "Employee email is already existed.");
            }
            if (result.hasErrors()) {
                model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
                model.addAttribute("positions", positionRepository.findAll());
                model.addAttribute("departments", departmentRepository.findAll());
                model.addAttribute("saveEmployeeDto", new SaveEmployeeDTO());
                return "add_new_employee";
            }
            employeeService.saveEmployee(saveEmployeeDto);
        } catch (ErrorMessageException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("positions", positionRepository.findAll());
            model.addAttribute("departments", departmentRepository.findAll());
            model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
            return "add_new_employee";
        }
        return "redirect:/list_employees";
    }

    @GetMapping("/access_denied")
    public String accessDenied() {
        return "access_denied";
    }

    @GetMapping("/update_employee/{id}")
    public String updateEmployeeForm(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        SaveEmployeeDTO saveEmployeeDto = EmployeeMapper.EMPLOYEE_MAPPER.mapToSaveEmployeeDto(employee);
        model.addAttribute("employee", employee);
        model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
        model.addAttribute("saveEmployeeDto", saveEmployeeDto);
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        return "update_employee";
    }

    @PostMapping("/update_employee/{id}")
    public String updateEmployee(@Valid @ModelAttribute("saveEmployeeDto") SaveEmployeeDTO saveEmployeeDto,
                                 @PathVariable("id") Long id){
        employeeService.updateEmployee(saveEmployeeDto, id);
        return "redirect:/list_employees";
    }

    @GetMapping("/delete_employee/{id}")
    public String deleteEmployee(@PathVariable("id") Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        for (Role role: employee.getRoles()) {
            employeeService.deleteEmployeeRole(id, role.getRoleId());
        }
        employeeService.deleteEmployee(id);
        return "redirect:/list_employees";
    }

    @GetMapping("/list_employees/page/{pageNo}")
    public String getListEmployees(HttpServletRequest request,
                                   @PathVariable(value = "pageNo") int pageNo,
                                   @RequestParam("sortField") String sortField,
                                   @RequestParam("sortDirection") String sortDirection,
                                   Model model) {
        String searchText = request.getParameter("searchText");
        Page<Employee> page = employeeService.findPaginated(pageNo, 3, sortField, sortDirection, searchText);
        model.addAttribute("employees", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
        return "list_employees";
    }

    @GetMapping("/list_employees")
    public String defaultListEmployees(HttpServletRequest request, Model model) {
        return getListEmployees(request,1, "employeeCode", "asc", model);
    }

    @GetMapping("/employee_export_excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=list_employees_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        EmployeeServiceImpl excelExporter = new EmployeeServiceImpl(employeeService.listEmployees());
        excelExporter.export(response);
    }

    @GetMapping("/reset_password")
    public String resetPasswordForm() {
        return "reset_password";
    }

    @PostMapping("/reset_password")
    public String forgotPassword(HttpServletRequest request, Model model) throws MessagingException {
        try{
            String email = request.getParameter("email");
            employeeService.resetPassword(email);
            return "redirect:/home";
        } catch (ErrorMessageException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "reset_password";
        }
    }

    @GetMapping(value = "/employee_export_word", produces = "application/vnd.openxmlformats-" + "officedocument.wordprocessingml.document")
    public ResponseEntity<InputStreamResource> exportToWord() throws IOException{
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        ByteArrayInputStream inputStream = employeeService.generateWord();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename = list_employees_"+currentDateTime+ ".docx");
        InputStreamResource streamResource = new InputStreamResource(inputStream);
        return ResponseEntity.ok().headers(headers).body(streamResource);
    }

    @GetMapping("/employee_export_pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException{
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=list_employees_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);
        EmployeeServiceImpl exporter = new EmployeeServiceImpl(employeeService.listEmployees());
        exporter.export(response);
    }

}
