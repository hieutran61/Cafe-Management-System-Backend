package com.hieutran.cafe.DTO;


import lombok.Value;

@Value
public class UserDTO {
    private Integer id;
    private String name;
    private String email;
    private String contactNumber;
    private String status;

}
