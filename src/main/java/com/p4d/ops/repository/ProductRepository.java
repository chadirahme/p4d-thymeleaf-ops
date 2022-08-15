package com.p4d.ops.repository;

import com.p4d.ops.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository  extends JpaRepository<Product, Integer> {

    List<Product> findByProductType(String productType);


}
