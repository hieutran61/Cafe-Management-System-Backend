package com.hieutran.cafe.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@NamedQuery(name = "User.getAllAdminEmail", query = "SELECT u.email from User u WHERE u.role='admin'")

@Data
@Entity
@DynamicUpdate
@DynamicInsert
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    private String name;

    private String contactNumber;

    private String email;

    private String password;

    private String status;

    private String role;

    
}
