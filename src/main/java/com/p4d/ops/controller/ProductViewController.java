package com.p4d.ops.controller;

import com.p4d.ops.models.Product;
import com.p4d.ops.repository.ProductRepository;
import com.p4d.ops.service.AwsS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Controller
//@RequestMapping("/productsView/")
public class ProductViewController {

    @Autowired
    AwsS3Service awsS3Service;

    @Autowired
    ProductRepository productRepository;

    @GetMapping("/productsView")
    public String showUpdateForm(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "productsList";
    }

    @GetMapping("/addProduct")
    public String addProduct(Product product) {
        product.setProductType("home");
        return "add-product";
    }

    @PostMapping("/addProduct")
    public String addStudent(@Valid Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-product";
        }
            productRepository.save(product);
       // studentRepository.save(student);
        return "redirect:productsView";
    }

    @GetMapping("deleteProduct/{id}")
    public String deleteStudent(@PathVariable("id") Integer id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

       // productRepository.delete(product);
        //model.addAttribute("products", productRepository.findAll());
        model.addAttribute("allowDelete", true);
        model.addAttribute("product", product);
        return "product";
       // return "productsList";
        //return "redirect:productsView";
    }

    @PostMapping(value = {"/deleteProduct/{contactId}/delete"})
    public String deleteContactById(
            Model model, @PathVariable Integer contactId) {
        try {
            Product product = productRepository.findById(contactId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + contactId));

            productRepository.delete(product);
            return "redirect:/productsView";
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
           // logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            return "product";
        }
    }

    @GetMapping("editProduct/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        // productRepository.delete(product);
        //model.addAttribute("products", productRepository.findAll());
        model.addAttribute("allowDelete", false);
        model.addAttribute("product", product);
        return "product";
        // return "productsList";
        //return "redirect:productsView";
    }

    @PostMapping(value = {"/saveProduct/{contactId}/edit"})
    public String saveProduct(Model model, @PathVariable Integer contactId,
                              @ModelAttribute("product") Product product,
                              @RequestParam("file") MultipartFile file) {
        try {
            Product productDB = productRepository.findById(contactId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + contactId));
            product.setProductId(contactId);
            // normalize the file path
            if(file!=null && file.getSize()>0) {
                awsS3Service.putObject(file);
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                product.setProductImage(fileName);
            }

            //productDB.setProductName(product.getProductName());
            productRepository.save(product);

            return "redirect:/productsView";
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            // logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            return "product";
        }
    }

}
