package com.example.employeedirectoryproject.mapper;

import com.example.employeedirectoryproject.dto.CertificationDTO;
import com.example.employeedirectoryproject.model.Certification;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CertificationMapper {
    CertificationMapper CERTIFICATION_MAPPER = Mappers.getMapper(CertificationMapper.class);
    Certification mapToCertification(CertificationDTO certificationDTO);
    CertificationDTO mapToCertificationDto(Certification certification);

}
