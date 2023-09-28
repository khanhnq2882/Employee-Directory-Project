package com.example.employeedirectoryproject.mapper;

import com.example.employeedirectoryproject.dto.SaveEmployeeDTO;
import com.example.employeedirectoryproject.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeMapper {
    EmployeeMapper EMPLOYEE_MAPPER = Mappers.getMapper(EmployeeMapper.class);
    Employee mapToEmployee(SaveEmployeeDTO saveEmployeeDTO);
    SaveEmployeeDTO mapToSaveEmployeeDto(Employee employee);
    void mapToUpdateEmployee(@MappingTarget Employee employee, SaveEmployeeDTO saveEmployeeDTO);
}
