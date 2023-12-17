package com.example.sautamaq.dto;

import com.example.sautamaq.model.Recipe;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructionDto {
    private Long id;
    private String name;
    private int step;
}
