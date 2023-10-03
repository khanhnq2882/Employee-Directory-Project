package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.config.exception.NotExistEmployeeException;
import com.example.employeedirectoryproject.config.exception.NotMatchPasswordException;
import com.example.employeedirectoryproject.config.exception.WrongPasswordException;
import com.example.employeedirectoryproject.dto.*;
import com.example.employeedirectoryproject.mapper.CertificationMapper;
import com.example.employeedirectoryproject.mapper.EmployeeMapper;
import com.example.employeedirectoryproject.mapper.ExperienceMapper;
import com.example.employeedirectoryproject.mapper.SkillMapper;
import com.example.employeedirectoryproject.model.*;
import com.example.employeedirectoryproject.repository.*;
import com.example.employeedirectoryproject.service.EmailSenderService;
import com.example.employeedirectoryproject.service.EmployeeService;
import com.example.employeedirectoryproject.util.TbConstants;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private SkillRepository skillRepository;
    private ExperienceRepository experienceRepository;
    private CertificationRepository certificationRepository;
    private EmailSenderService emailSenderService;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Employee> employeeList;
    public EmployeeServiceImpl(List<Employee> employeeList) {
        this.employeeList = employeeList;
        workbook = new XSSFWorkbook();
    }

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder,
                               SkillRepository skillRepository,
                               ExperienceRepository experienceRepository,
                               CertificationRepository certificationRepository,
                               EmailSenderService emailSenderService) {
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.skillRepository = skillRepository;
        this.experienceRepository = experienceRepository;
        this.certificationRepository = certificationRepository;
        this.emailSenderService = emailSenderService;
    }

    @Override
    public void saveEmployee(SaveEmployeeDTO saveEmployeeDto) throws MessagingException{
        Role role = roleRepository.findByName(TbConstants.Roles.EMPLOYEE);
        if (Objects.isNull(role)) {
            role = roleRepository.save(new Role(TbConstants.Roles.EMPLOYEE));
        }
        String employeeCode = "";
        List<Employee> employees = employeeRepository.findAll();
        if (employees.size() == 0) {
            employeeCode = "NV1";
        } else {
            Long nextId = Collections.max(employees.stream().map(employee -> employee.getEmployeeId()).collect(Collectors.toList())) + 1;
            employeeCode = "NV" + nextId;
        }
        Employee employee = Employee.builder()
                .employeeCode(employeeCode)
                .firstName(saveEmployeeDto.getFirstName())
                .lastName(saveEmployeeDto.getLastName())
                .email(saveEmployeeDto.getEmail())
                .password(randomPassword())
                .dateOfBirth(saveEmployeeDto.getDateOfBirth())
                .phoneNumber(saveEmployeeDto.getPhoneNumber())
                .address(saveEmployeeDto.getAddress())
                .gender(saveEmployeeDto.getGender())
                .startWork(saveEmployeeDto.getStartWork())
                .endWork(saveEmployeeDto.getEndWork())
                .coefficientsSalary(saveEmployeeDto.getCoefficientsSalary())
                .department(saveEmployeeDto.getDepartment())
                .position(saveEmployeeDto.getPosition())
                .status(saveEmployeeDto.getStatus())
                .createdBy(getCurrentEmployee().getFirstName()+" "+getCurrentEmployee().getLastName())
                .updatedBy(getCurrentEmployee().getFirstName()+" "+getCurrentEmployee().getLastName())
                .roles(Arrays.asList(role))
                .build();
        saveEmployeeDto.setPassword(employee.getPassword());
        sendEmail(saveEmployeeDto);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeRepository.save(employee);
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (bCryptPasswordEncoder.matches(changePasswordDTO.getOldPassword(), getCurrentEmployee().getPassword())) {
            if (changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
                getCurrentEmployee().setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
                employeeRepository.save(getCurrentEmployee());
            } else {
                throw new NotMatchPasswordException("New password and confirm password is not match. Try again!");
            }
        } else {
            throw new WrongPasswordException("Old password is not correct. Try again!");
        }
    }

    @Override
    public Employee findEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.getAllEmployees();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    @Override
    public void updateEmployee(SaveEmployeeDTO saveEmployeeDto, Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        EmployeeMapper.EMPLOYEE_MAPPER.mapToUpdateEmployee(employee, saveEmployeeDto);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployeeRole(Long employeeId, Long roleId) {
        employeeRepository.deleteEmployeeRole(employeeId, roleId);
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public Employee getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentEmployee = employeeRepository.findByEmail(authentication.getName());
        return currentEmployee;
    }

    @Override
    public List<Employee> searchEmployees(String search) {
        return employeeRepository.search(search);
    }

    @Override
    public void addSkill(SkillDTO skillDto) {
        Skill skill = SkillMapper.SKILL_MAPPER.mapToSkill(skillDto);
        skill.setEmployees(Arrays.asList(getCurrentEmployee()));
        skill.setCreatedBy(getCurrentEmployee().getFirstName()+" "+getCurrentEmployee().getLastName());
        skill.setUpdatedBy(getCurrentEmployee().getFirstName()+" "+getCurrentEmployee().getLastName());
        getCurrentEmployee().getSkills().add(skill);
        skillRepository.save(skill);
    }

    @Override
    public void addExperience(ExperienceDTO experienceDto) {
        Experience experience = ExperienceMapper.EXPERIENCE_MAPPER.mapToExperience(experienceDto);
        experience.setEmployee(getCurrentEmployee());
        experience.setCreatedBy(getCurrentEmployee().getFirstName()+" "+getCurrentEmployee().getLastName());
        experience.setUpdatedBy(getCurrentEmployee().getFirstName()+" "+getCurrentEmployee().getLastName());
        experienceRepository.save(experience);
    }

    @Override
    public void addCertification(CertificationDTO certificationDto) {
        Certification certification = CertificationMapper.CERTIFICATION_MAPPER.mapToCertification(certificationDto);
        certification.setEmployee(getCurrentEmployee());
        certification.setCreatedBy(getCurrentEmployee().getFirstName()+" "+getCurrentEmployee().getLastName());
        certification.setUpdatedBy(getCurrentEmployee().getFirstName()+" "+getCurrentEmployee().getLastName());
        certificationRepository.save(certification);
    }

    @Override
    public void sendMailToAdmin(String email) throws MessagingException {
        EmailDTO emailDto = new EmailDTO();
        Employee employee = employeeRepository.findByEmail(email);
        if (!Objects.isNull(employee)) {
            emailDto.setFrom(email);
            emailDto.setTo("quockhanhnguyen2882@gmail.com");
            emailDto.setSubject("Employee "+employee.getFirstName()+" "+employee.getLastName()+" ("+email+") wants to reset old password.");
            Map<String, Object> properties = new HashMap<>();
            properties.put("email", email);
            properties.put("fullName", employee.getFirstName()+" "+employee.getLastName());
            emailDto.setProperties(properties);
            emailDto.setTemplate("email_reset_password.html");
            emailSenderService.sendHtmlMessage(emailDto);
        } else {
            throw new NotExistEmployeeException("Not exist employee information that have email is "+email+" . Try again!");
        }
    }

    @Override
    public List<Skill> getEmployeeSkills(Long employeeId) {
        return skillRepository.getEmployeeSkills(employeeId);
    }

    public String randomPassword() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, 8)
                .map(i -> random.nextInt(chars.length()))
                .mapToObj(randomIndex -> String.valueOf(chars.charAt(randomIndex)))
                .collect(Collectors.joining());
    }

    public void sendEmail(SaveEmployeeDTO saveEmployeeDTO) throws MessagingException {
        EmailDTO emailDto = new EmailDTO();
        emailDto.setFrom("quockhanhnguyen2882@gmail.com");
        emailDto.setTo(saveEmployeeDTO.getPersonalEmail());
        emailDto.setSubject("Company send information for new employee "+saveEmployeeDTO.getFirstName()+" "+saveEmployeeDTO.getLastName());
        Map<String, Object> properties = new HashMap<>();
        properties.put("email", saveEmployeeDTO.getEmail());
        properties.put("password", saveEmployeeDTO.getPassword());
        properties.put("fullName", saveEmployeeDTO.getFirstName()+" "+saveEmployeeDTO.getLastName());
        emailDto.setProperties(properties);
        emailDto.setTemplate("email_send_account.html");
        emailSenderService.sendHtmlMessage(emailDto);
    }

    public void createCell(Row row, int columnCount, Object value, CellStyle cellStyle) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else {
            CreationHelper creationHelper = workbook.getCreationHelper();
            short format = creationHelper.createDataFormat().getFormat("yyyy-MM-dd");
            cellStyle.setDataFormat(format);
            cell.setCellValue((Date) value);
        }
        cell.setCellStyle(cellStyle);
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("List Employees");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        createCell(row, 0, "Employee Code", style);
        createCell(row, 1, "Full Name", style);
        createCell(row, 2, "Email", style);
        createCell(row, 3, "Department", style);
        createCell(row, 4, "Position", style);
        createCell(row, 5, "Start Work", style);
        createCell(row, 6, "End Work", style);
        createCell(row, 7, "Status", style);
    }

    private void writeDataLines() {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        for (Employee employee: employeeList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, employee.getEmployeeCode(), style);
            createCell(row, columnCount++, employee.getFirstName()+" "+employee.getLastName(), style);
            createCell(row, columnCount++, employee.getEmail(), style);
            createCell(row, columnCount++, employee.getDepartment().getDepartmentName(), style);
            createCell(row, columnCount++, employee.getPosition().getPositionName(), style);
            createCell(row, columnCount++, employee.getStartWork(), style);
            createCell(row, columnCount++, employee.getEndWork(), style);
            if (employee.getStatus() == true) {
                createCell(row, columnCount++, "Active", style);
            } else {
                createCell(row, columnCount++, "Inactive", style);
            }
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }


}
