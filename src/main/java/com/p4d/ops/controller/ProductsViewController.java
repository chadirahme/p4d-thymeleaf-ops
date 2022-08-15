package com.p4d.ops.controller;

import com.p4d.ops.models.Product;
import com.p4d.ops.models.SearchModel;
import com.p4d.ops.repository.ProductRepository;
import com.p4d.ops.service.AwsS3Service;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/productsView")
public class ProductsViewController {

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

    @GetMapping
    public String showDefaultProductsList(Model model,SearchModel searchModel) {
        model.addAttribute("products", new ArrayList<>());
        if(model.getAttribute("productType")!=null)
            searchModel.setProductType(model.getAttribute("productType").toString());
       // model.addAttribute("searchModel", new SearchModel());
        return "productsList";
    }

    @PostMapping
    public String searchProduct(Model model,SearchModel searchModel,RedirectAttributes attributes,
                                @RequestParam(value="action", required=true) String action) {
        switch(action) {
            case "Add Product":
                attributes.addFlashAttribute("productType",searchModel.getProductType());
                return "redirect:/productsView/addProduct";
               // return "add-product";
               // break;
            default:
                model.addAttribute("products", productRepository.findByProductType(searchModel.getProductType()));
                attributes.addFlashAttribute("productType",searchModel.getProductType());
                return "productsList";
                //break;
        }
       // model.addAttribute("products", productRepository.findByProductType(searchModel.getProductType()));
      //  attributes.addFlashAttribute("productType",searchModel.getProductType());
       // return "productsList";
        //return "redirect:productsView";
    }

    @GetMapping("/addProduct")
    public String addProduct(Product product,Model model) {
        if(model.getAttribute("productType")!=null)
            product.setProductType(model.getAttribute("productType").toString());
        else
        product.setProductType("home");
        return "add-product";
    }

    @PostMapping("/addProduct")
    public String addStudent(@Valid Product product, BindingResult result, Model model,RedirectAttributes attributes,
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
            attributes.addFlashAttribute("productType",product.getProductType());
            productRepository.save(product);
        }
        catch (DataException ex){
            String errorMessage = ex.getMessage();
            // logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            attributes.addFlashAttribute("message", errorMessage);
            // return "product";
            return "add-product";
        }
        catch (Exception ex) {
            String errorMessage = ex.getMessage();
            // logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            attributes.addFlashAttribute("message", errorMessage);
            // return "product";
            return "add-product";
        }
        attributes.addFlashAttribute("message", "Product added successfully!");
        return "redirect:/productsView";
    }

    @GetMapping("/{ProductId}/delete")
    //@PostMapping(value = {"/deleteProduct/{contactId}/delete"})
    public String deleteProductById(
            Model model, @PathVariable Integer ProductId, RedirectAttributes attributes) {
        try {
            Product product = productRepository.findById(ProductId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + ProductId));

            productRepository.delete(product);
            attributes.addFlashAttribute("message", "Product deleted successfully!");
            attributes.addFlashAttribute("productType",product.getProductType());
            return "redirect:/productsView";
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            // logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            return "product";
        }
    }

    @GetMapping("/{productId}")
    public String editProduct(@PathVariable("productId") Integer productId, Model model) throws SQLException {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + productId));

        Product product = productRepository.findById(productId).orElse(null);
        if(product==null){
            throw new SQLException("Product not found, id="+productId);
        }

        // productRepository.delete(product);
        //model.addAttribute("products", productRepository.findAll());
        model.addAttribute("allowDelete", false);
        model.addAttribute("product", product);
        return "edit-product";
        // return "productsList";
        //return "redirect:productsView";
    }

    @PostMapping("/{productId}")
    public String saveProduct(@PathVariable Integer productId,@ModelAttribute("product") @Valid Product product,
                              BindingResult result, RedirectAttributes attributes,
                              @RequestParam("file") MultipartFile file) throws Exception {
        //try {
                if (result.hasErrors()) {
               product.setProductId(productId);
                return "edit-product";
            }
            Product productDB = productRepository.findById(productId).orElse(null);
            if(productDB==null){
                throw new SQLException("Product not found, id="+productId);
            }
                   // .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + productId));
            product.setProductId(productId);
            // normalize the file path
            if(file!=null && file.getSize()>0) {
                awsS3Service.putObject(file,product.getProductType());
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                product.setProductImage(fileName);
            }else {
                product.setProductImage(productDB.getProductImage());
            }

            productRepository.save(product);
            attributes.addFlashAttribute("productType",product.getProductType());
            attributes.addFlashAttribute("message", "Product updated successfully!");
            return "redirect:/productsView";
       // }
//        catch (Exception ex) {
//            String errorMessage = ex.getMessage();
//            // logger.error(errorMessage);
//            attributes.addFlashAttribute("message", errorMessage);
//            return "redirect:/productsView";
//        }
    }

}
