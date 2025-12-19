package org.example.fasthost.entity.dto;

import lombok.Data;



@Data
public class TelegramAuthDto {


    private Long id;

    private String username;

    private String firstName;

    private String lastName;
}