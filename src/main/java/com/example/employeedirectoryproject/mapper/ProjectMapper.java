package com.example.employeedirectoryproject.mapper;

import com.example.employeedirectoryproject.dto.SaveProjectDTO;
import com.example.employeedirectoryproject.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProjectMapper {
    ProjectMapper PROJECT_MAPPER = Mappers.getMapper(ProjectMapper.class);
    Project mapToProject(SaveProjectDTO saveProjectDTO);
    SaveProjectDTO mapToSaveProjectDTO(Project project);
    void mapToUpdateProject(@MappingTarget Project project, SaveProjectDTO saveProjectDTO);
}
