package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.dto.ExperienceDto;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Experience;
import com.example.employeedirectoryproject.repository.EmployeeRepository;
import com.example.employeedirectoryproject.repository.ExperienceRepository;
import com.example.employeedirectoryproject.service.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ExperienceServiceImpl implements ExperienceService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Override
    public void addExperience(ExperienceDto experienceDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentEmployee = employeeRepository.findByEmail(authentication.getName());
        Experience experience = Experience.builder()
                .companyName(experienceDto.getCompanyName())
                .name(experienceDto.getProjectName())
                .language(experienceDto.getLanguage())
                .framework(experienceDto.getFramework())
                .startWork(experienceDto.getStartWork())
                .endWork(experienceDto.getEndWork())
                .description(experienceDto.getDescription())
                .employee(currentEmployee)
                .build();
        experienceRepository.save(experience);
    }
}
