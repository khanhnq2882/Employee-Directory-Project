package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.dto.SaveProjectDTO;
import com.example.employeedirectoryproject.mapper.ProjectMapper;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Project;
import com.example.employeedirectoryproject.repository.EmployeeRepository;
import com.example.employeedirectoryproject.repository.ProjectRepository;
import com.example.employeedirectoryproject.service.EmployeeService;
import com.example.employeedirectoryproject.service.ProjectService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private ProjectRepository projectRepository;
    private EmployeeRepository employeeRepository;
    private EmployeeService employeeService;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Project> projectList;

    public ProjectServiceImpl(List<Project> projectList) {
        this.projectList = projectList;
        workbook = new XSSFWorkbook();
    }

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository,
                              EmployeeRepository employeeRepository,
                              EmployeeService employeeService) {
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
    }

    @Override
    public void addNewProject(SaveProjectDTO saveProjectDTO) {
        Project project = ProjectMapper.PROJECT_MAPPER.mapToProject(saveProjectDTO);
        project.setCreatedBy(employeeService.getCurrentEmployee().getFirstName()+" "+employeeService.getCurrentEmployee().getLastName());
        for (Employee employee: saveProjectDTO.getEmployees()) {
            employee.getProjects().add(project);
        }
        projectRepository.save(project);
    }

    @Override
    public List<Project> getListProjects() {
        return projectRepository.getListProjects();
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Override
    public void updateProject(SaveProjectDTO saveProjectDTO, Long id) {
        Project project = projectRepository.findById(id).orElse(null);
        ProjectMapper.PROJECT_MAPPER.mapToUpdateProject(project, saveProjectDTO);
        project.setUpdatedBy(employeeService.getCurrentEmployee().getFirstName()+" "+employeeService.getCurrentEmployee().getLastName());
        project.getEmployees().stream().forEach(employee -> {
            if (employee.getProjects().size() == 0) {
                employee.setProjects(Arrays.asList(project));
            } else {
                employee.getProjects().stream().forEach(p -> {
                    if (p.getProjectId().equals(project.getProjectId())) {
                        projectRepository.deleteProjectMembers(project.getProjectId(), employee.getEmployeeId());
                    }
                    employee.getProjects().add(project);
                });
            }
        });
        projectRepository.save(project);
    }

    @Override
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id).orElse(null);
        for(Employee employee: project.getEmployees()) {
            projectRepository.deleteProjectMembers(id, employee.getEmployeeId());
        }
        projectRepository.deleteById(id);
    }

    @Override
    public Page<Project> getAllProjects(Pageable pageable) {
        return projectRepository.getAllProjects(pageable);
    }

    @Override
    public Page<Project> searchProjects(String searchText, Pageable pageable) {
        return projectRepository.searchProjects(searchText, pageable);
    }

    @Override
    public Page<Project> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection, String searchText) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);
        Page<Project> page;
        if (Objects.isNull(searchText)) {
            page = getAllProjects(pageable);
        } else {
            page = searchProjects(searchText, pageable);
        }
        return page;
    }

    @Override
    public List<Project> getProjectsByEmployee(Long employeeId) {
        return projectRepository.getProjectsByEmployee(employeeId);
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
        sheet = workbook.createSheet("List Projects");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        createCell(row, 0, "Project Name", style);
        createCell(row, 1, "Language", style);
        createCell(row, 2, "Framework", style);
        createCell(row, 3, "Members", style);
        createCell(row, 4, "Start Date", style);
        createCell(row, 5, "End Date", style);
        createCell(row, 6, "Status", style);
    }

    private void writeDataLines() {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Project project: projectList) {
            List<String> emailList = project.getEmployees().stream().map(employee -> employee.getEmail()).collect(Collectors.toList());
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, project.getProjectName(), style);
            createCell(row, columnCount++, project.getLanguage(), style);
            createCell(row, columnCount++, project.getFramework(), style);
            createCell(row, columnCount++, emailList.stream().map(Object::toString).collect(Collectors.joining(", ")), style);
            createCell(row, columnCount++, project.getStartDate(), style);
            createCell(row, columnCount++, project.getEndDate(), style);
            createCell(row, columnCount++, project.getStatus().getStatusName(), style);
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
        return null;
    }
}
