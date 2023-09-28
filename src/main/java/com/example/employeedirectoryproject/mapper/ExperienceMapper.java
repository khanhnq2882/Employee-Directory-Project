package com.example.employeedirectoryproject.mapper;

import com.example.employeedirectoryproject.dto.ExperienceDTO;
import com.example.employeedirectoryproject.model.Experience;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExperienceMapper {
    ExperienceMapper EXPERIENCE_MAPPER = Mappers.getMapper(ExperienceMapper.class);

    @Mapping(source = "name", target = "projectName")
    @Mapping(source = "startWork", target = "startDate")
    @Mapping(source = "endWork", target = "endDate")
    ExperienceDTO mapToExperienceDto(Experience experience);

    @Mapping(source = "projectName", target = "name")
    @Mapping(source = "startDate", target = "startWork")
    @Mapping(source = "endDate", target = "endWork")
    Experience mapToExperience(ExperienceDTO experienceDTO);
}
