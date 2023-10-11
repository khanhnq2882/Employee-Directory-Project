package com.example.employeedirectoryproject.controller;

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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
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

    @GetMapping("/save_employee")
    public String addNewEmployeeForm(Model model) {
        model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("saveEmployeeDto", new SaveEmployeeDTO());
        return "add_new_employee";
    }

    @PostMapping("/save_employee")
    public String addNewEmployee(@Valid @ModelAttribute("saveEmployeeDto") SaveEmployeeDTO saveEmployeeDto,
                                 BindingResult result,
                                 Model model) throws MessagingException{
        Employee existingEmployee = employeeService.findEmployeeByEmail(saveEmployeeDto.getEmail());
        if (existingEmployee != null) {
            result.rejectValue("email", null, "Employee email is already existed.");
        }
        if (result.hasErrors()) {
            model.addAttribute("saveEmployeeDto", saveEmployeeDto);
            return "add_new_employee";
        }
        employeeService.saveEmployee(saveEmployeeDto);
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
        int pageSize = 3;
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);
        Page<Employee> page = employeeRepository.findAll(pageable);
//        Page<Employee> page = new PageImpl<>(employeeService.getAllEmployees(pageable));
//        List<Employee> employees;
//        if (Objects.isNull(searchText)) {
//            employees = employeeService.getAllEmployees(pageable);
//        } else {
//            employees = employeeService.searchEmployees(searchText);
//        }
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

    @GetMapping("/export_excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=list_employees_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<Employee> listUsers = employeeService.getAllEmployees();
        EmployeeServiceImpl excelExporter = new EmployeeServiceImpl(listUsers);
        excelExporter.export(response);
    }


}
