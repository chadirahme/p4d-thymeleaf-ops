package com.p4d.ops.controller;

import com.p4d.ops.models.Product;
import com.p4d.ops.models.SearchModel;
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
import java.util.ArrayList;
import java.util.List;

//@Controller
//@RequestMapping("/productsView/")
public class ProductViewController {

    @Autowired
    AwsS3Service awsS3Service;

    @Autowired
    ProductRepository productRepository;
    @ModelAttribute("productsList")
    public List<String> getProductsList() {
        List<String> options = new ArrayList<String>();
        options.add("Home");
        options.add("Cakes");
        options.add("Muffins");
        return options;
    }

    @GetMapping("/productsView")
    public String showProductsView(Model model) {
//        List<String> options = new ArrayList<String>();
//        options.add("Home");
//        options.add("Cakes");
//        options.add("Muffins");
//        model.addAttribute("options", options);
//
//        model.addAttribute("nameOfProduct" , options.get(1));
        model.addAttribute("products", new ArrayList<>());
        model.addAttribute("searchModel", new SearchModel());
        return "productsList";
    }

    @PostMapping("/searchProduct")
    public String searchProduct(Model model,SearchModel searchModel) {
        model.addAttribute("products", productRepository.findByProductType(searchModel.getProductType()));
        return "productsList";
        //return "redirect:productsView";
    }


    @GetMapping("/addProduct")
    public String addProduct(Product product) {
        product.setProductType("home");
        return "add-product";
    }

    @PostMapping("/addProduct")
    public String addStudent(@Valid Product product, BindingResult result, Model model,
                             @RequestParam("file") MultipartFile file) {
        if (result.hasErrors()) {
            return "add-product";
        }
        try {
            if (file != null && file.getSize() > 0) {
                awsS3Service.putObject(file,product.getProductType());
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                product.setProductImage(fileName);
            }
            productRepository.save(product);
        }catch (Exception ex) {
            String errorMessage = ex.getMessage();
            // logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
           // return "product";
            return "add-product";
        }
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

//    public String saveProduct(Model model,BindingResult result, @PathVariable Integer contactId,
//                              @ModelAttribute("product") Product product,
//                              @RequestParam("file") MultipartFile file)
    @PostMapping(value = {"/saveProduct/{contactId}/edit"})
    public String saveProduct(@Valid Product product, BindingResult result, Model model,
                              @PathVariable Integer contactId,
                             @RequestParam("file") MultipartFile file)
    {
        try {
            if (result.hasErrors()) {
                //return "redirect:/editProduct/"+contactId;
                model.addAttribute("allowDelete", false);
                model.addAttribute("product", product);
                //return "product";
                return "add-product";
            }
            Product productDB = productRepository.findById(contactId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + contactId));
            product.setProductId(contactId);
            // normalize the file path
            if(file!=null && file.getSize()>0) {
                awsS3Service.putObject(file,product.getProductType());
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                product.setProductImage(fileName);
            }else {
                product.setProductImage(productDB.getProductImage());
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
