package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    @Email(message = "Поле email заполненно некорректно. Проверьте формат.")
    @NotNull(groups = {NewUser.class})
    @Email(groups = {NewUser.class})
    private String email;
    @NotBlank(groups = {NewUser.class})
    private String name;

    public interface NewUser {
    }
}