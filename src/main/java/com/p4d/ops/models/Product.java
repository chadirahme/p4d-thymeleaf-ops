package com.p4d.ops.models;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "products")
@Data
public class Product {


    private Integer productId;
    @NotBlank(message = "Name is mandatory")
    private String productName;
    private String productDesc;
    private String productType;
    private String productImage;
    private Integer productOrder;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="productId")
    public Integer getProductId() {
        return productId;
    }
}
