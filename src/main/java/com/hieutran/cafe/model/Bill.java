package com.hieutran.cafe.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity
@DynamicUpdate
@DynamicInsert
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String uuid;

    private String name;

    private String email;

    @Column(name = "contactnumber")
    private String contactNumber;

    @Column(name = "paymentmethod")
    private String paymentMethod;

    private Integer total;

    @Column(name = "productdetail", columnDefinition = "json")
    private String productDetail;

    @Column(name = "createby")
    private String createby;
}
