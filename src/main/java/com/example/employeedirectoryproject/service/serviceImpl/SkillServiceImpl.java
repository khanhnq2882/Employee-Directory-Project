package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.dto.SkillDto;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Skill;
import com.example.employeedirectoryproject.repository.EmployeeRepository;
import com.example.employeedirectoryproject.repository.SkillRepository;
import com.example.employeedirectoryproject.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkillServiceImpl implements SkillService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Override
    public void addSkill(SkillDto skillDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentEmployee = employeeRepository.findByEmail(authentication.getName());
        Skill skill = Skill.builder()
                .skillName(skillDto.getSkillName())
                .level(skillDto.getLevel())
                .description(skillDto.getDescription())
                .employees(Arrays.asList(currentEmployee))
                .build();
        currentEmployee.getSkills().add(skill);
        skillRepository.save(skill);
    }
}
