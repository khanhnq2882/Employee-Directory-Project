package com.example.employeedirectoryproject.mapper;

import com.example.employeedirectoryproject.dto.SkillDTO;
import com.example.employeedirectoryproject.model.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SkillMapper {
    SkillMapper SKILL_MAPPER = Mappers.getMapper(SkillMapper.class);
    Skill mapToSkill(SkillDTO skillDTO);
    SkillDTO mapToSkillDto(Skill skill);
}
