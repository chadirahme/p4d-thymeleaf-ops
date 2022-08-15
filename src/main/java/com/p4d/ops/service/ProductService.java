package com.p4d.ops.service;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.transaction.Transactional;

import com.p4d.ops.models.Product;
import com.p4d.ops.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProductService {

//    @Value("${uploads}")
//    private String uploads;
//    private Path root = Paths.get(uploads);

    @Autowired
    private ProductRepository repo;

    public List<Product> listAll() {
        return repo.findAll();
    }

    public List<Product> listAllProductType(String type) {
        return repo.findByProductType(type);
    }


    public void save(Product product) {
        repo.save(product);
    }

    public Product get(Integer id) {
        return repo.findById(id).get();
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}
