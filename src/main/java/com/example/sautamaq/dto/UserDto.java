package com.example.sautamaq.dto;

import com.example.sautamaq.model.Recipe;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    @JsonProperty(access = WRITE_ONLY)
    private String password;
    private String username;
    private List<Recipe> favs;
    private String role;
    private boolean isActive;
}