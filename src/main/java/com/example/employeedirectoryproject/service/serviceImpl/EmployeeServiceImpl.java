package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.config.ErrorMessageException;
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
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.web.multipart.MultipartFile;

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
        String password = randomPassword();
        if(findByPhoneNumber(saveEmployeeDto.getPhoneNumber()) == null) {
            Employee employee = Employee.builder()
                    .employeeCode(employeeCode)
                    .firstName(saveEmployeeDto.getFirstName())
                    .lastName(saveEmployeeDto.getLastName())
                    .personalEmail(saveEmployeeDto.getPersonalEmail())
                    .email(saveEmployeeDto.getEmail())
                    .password(password)
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
                    .roles(Arrays.asList(role))
                    .build();
            sendEmail(saveEmployeeDto, password);
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            employeeRepository.save(employee);
        } else {
            throw new ErrorMessageException("Phone number is already registered.");
        }
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (bCryptPasswordEncoder.matches(changePasswordDTO.getCurrentPassword(), getCurrentEmployee().getPassword())) {
            if (changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
                getCurrentEmployee().setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
                employeeRepository.save(getCurrentEmployee());
            } else {
                throw new ErrorMessageException("New password and confirm password is not match.");
            }
        } else {
            throw new ErrorMessageException("Current password is not correct.");
        }
    }

    @Override
    public void resetPassword(String email) throws MessagingException {
        EmailDTO emailDto = new EmailDTO();
        Employee employee = employeeRepository.findByEmail(email);
        if (!Objects.isNull(employee)) {
            emailDto.setFrom(getCurrentEmployee().getEmail());
            emailDto.setTo(employee.getPersonalEmail());
            emailDto.setSubject("Your password has been changed");
            employee.setPassword(randomPassword());
            Map<String, Object> properties = new HashMap<>();
            properties.put("password", employee.getPassword());
            properties.put("fullNameAdmin", getCurrentEmployee().getFirstName()+" "+getCurrentEmployee().getLastName());
            properties.put("fullNameEmployee", employee.getFirstName()+" "+employee.getLastName());
            emailDto.setProperties(properties);
            emailDto.setTemplate("email_reset_password.html");
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            emailSenderService.sendHtmlMessage(emailDto);
            employeeRepository.save(employee);
        } else {
            throw new ErrorMessageException("Not exist employee information that have email is "+email+".");
        }
    }

    @Override
    public Employee findEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    @Override
    public Employee findByPhoneNumber(String phoneNumber) {
        return employeeRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public List<Employee> listEmployees() {
        return employeeRepository.listEmployees();
    }

    @Override
    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.getAllEmployees(pageable);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    @Override
    public void updateEmployee(SaveEmployeeDTO saveEmployeeDto, Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        EmployeeMapper.EMPLOYEE_MAPPER.mapToUpdateEmployee(employee, saveEmployeeDto);
        employee.setUpdatedBy(getCurrentEmployee().getFirstName()+" "+getCurrentEmployee().getLastName());
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
    public Page<Employee> searchEmployees(String searchText, Pageable pageable) {
        return employeeRepository.search(searchText, pageable);
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
            emailDto.setTemplate("email_forgot_password.html");
            emailSenderService.sendHtmlMessage(emailDto);
        } else {
            throw new ErrorMessageException("Not exist employee information that have email is "+email+" . Try again!");
        }
    }

    @Override
    public List<Skill> getEmployeeSkills(Long employeeId) {
        return skillRepository.getEmployeeSkills(employeeId);
    }

    @Override
    public List<Certification> getEmployeeCertifications(Long employeeId) {
        return certificationRepository.getEmployeeCertifications(employeeId);
    }

    @Override
    public List<Experience> getEmployeeExperiences(Long employeeId) {
        return experienceRepository.getEmployeeExperiences(employeeId);
    }

    @Override
    public Page<Employee> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection, String searchText) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);
        Page<Employee> page;
        if (Objects.isNull(searchText)) {
            page = getAllEmployees(pageable);
        } else {
            page = searchEmployees(searchText, pageable);
        }
        return page;
    }

    public String randomPassword() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, 8)
                .map(i -> random.nextInt(chars.length()))
                .mapToObj(randomIndex -> String.valueOf(chars.charAt(randomIndex)))
                .collect(Collectors.joining());
    }

    public void sendEmail(SaveEmployeeDTO saveEmployeeDTO, String password) throws MessagingException {
        EmailDTO emailDto = new EmailDTO();
        emailDto.setFrom("quockhanhnguyen2882@gmail.com");
        emailDto.setTo(saveEmployeeDTO.getPersonalEmail());
        emailDto.setSubject("TECH CORP send information for new employee "+saveEmployeeDTO.getFirstName()+" "+saveEmployeeDTO.getLastName());
        Map<String, Object> properties = new HashMap<>();
        properties.put("email", saveEmployeeDTO.getEmail());
        properties.put("password", password);
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
            createCell(row, columnCount++, employee.getStatus().toString().equals("true") ? "Active" : "Inactive", style);
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

    @Override
    public ByteArrayInputStream generateWord() throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = paragraph.createRun();
            run.setBold(true);
            run.setFontSize(30);
            run.setText("List Employees");
            run.setFontFamily("Times New Roman");
            XWPFTable table = document.createTable();
            table.setWidth("100%");
            table.setTableAlignment(TableRowAlign.CENTER);
            XWPFTableRow row = table.getRow(0);
            row.getCell(0).setText("Employee Code");
            row.addNewTableCell().setText("Full Name");
            row.addNewTableCell().setText("Department");
            row.addNewTableCell().setText("Position");
            row.addNewTableCell().setText("Email");
            row.addNewTableCell().setText("Start Work");
            row.addNewTableCell().setText("End Work");
            row.addNewTableCell().setText("Status");
            for(int i=0; i<=row.getTableCells().size()-1; i++) {
                XWPFParagraph p = table.getRow(0).getCell(i).getParagraphs().get(0);
                p.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun r = p.createRun();
                r.setBold(true);
                r.setColor("FF5733");
            }
            for(int i=0; i<=listEmployees().size() - 1; i++) {
                XWPFTableRow r = table.createRow();
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                r.getCell(0).setText(listEmployees().get(i).getEmployeeCode());
                r.getCell(1).setText(listEmployees().get(i).getFirstName() + " " + listEmployees().get(i).getLastName());
                r.getCell(2).setText(listEmployees().get(i).getEmail());
                r.getCell(3).setText(listEmployees().get(i).getDepartment().getDepartmentName());
                r.getCell(4).setText(listEmployees().get(i).getPosition().getPositionName());
                r.getCell(5).setText(df.format(listEmployees().get(i).getStartWork()));
                r.getCell(6).setText(df.format(listEmployees().get(i).getEndWork()));
                r.getCell(7).setText(listEmployees().get(i).getStatus().toString().equals("true") ? "Active" : "Inactive");
            }
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            document.write(b);
            return new ByteArrayInputStream(b.toByteArray());
        }
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);
        cell.setPhrase(new Phrase("Employee Code", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Full Name", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Department", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Position", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Email", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Start Work", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("End Work", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Status", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table) {
        for (Employee employee : listEmployees()) {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            table.addCell(employee.getEmployeeCode());
            table.addCell(employee.getFirstName()+" "+employee.getLastName());
            table.addCell(employee.getDepartment().getDepartmentName());
            table.addCell(employee.getPosition().getPositionName());
            table.addCell(employee.getEmail());
            table.addCell(df.format(employee.getStartWork()));
            table.addCell(df.format(employee.getEndWork()));
            table.addCell(employee.getStatus().toString().equals("true") ? "Active" : "Inactive");
        }
    }

    @Override
    public void exportToPdf(HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);
        Paragraph p = new Paragraph("List Employees", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 3.5f, 3.0f, 3.0f, 1.5f, 3.0f, 3.0f, 1.5f});
        table.setSpacingBefore(10);
        writeTableHeader(table);
        writeTableData(table);
        document.add(table);
        document.close();
    }

    @Override
    public void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }

}
