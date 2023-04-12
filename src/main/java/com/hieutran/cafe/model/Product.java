package com.hieutran.cafe.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;


@NamedQuery(name = "Product.getAllProduct", query = "select new com.hieutran.cafe.DTO.ProductDTO(p.id, p.name, p.description, p.price, p.status, p.category.id, p.category.name) from Product p")

@Data
@Entity
@DynamicInsert
@DynamicUpdate
public class Product implements Serializable {

    public static final Long serialVersionUid = 123456L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String description;

    private Integer price;

    private String status;
}
