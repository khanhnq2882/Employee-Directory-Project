package com.example.employeedirectoryproject.dto;

import lombok.Data;
import java.util.Map;

@Data
public class EmailDTO {
    private String to;
    private String from;
    private String subject;
    private String text;
    private String template;
    private Map<String, Object> properties;
}
