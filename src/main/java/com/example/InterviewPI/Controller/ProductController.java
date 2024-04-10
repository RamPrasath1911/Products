package com.example.InterviewPI.Controller;

import com.example.InterviewPI.Entity.Product;
import com.example.InterviewPI.Entity.ProductApprovalEntity;
import com.example.InterviewPI.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getActiveProducts(){
        return productService.getProducts();
    }
    @GetMapping("/products/approval-queue")
    public ResponseEntity<List<ProductApprovalEntity>> getApprovalQueueProducts(){
        return productService.getApprovalQueueProducts();
    }
    @PostMapping("/products")
    public ResponseEntity<String> createProduct(@RequestBody Product product){
        return productService.createProduct(product);
    }
    @PostMapping("/products/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable int productId, @RequestBody Product product){
        return productService.updateProduct(productId, product);
    }
    @PutMapping("/products/approval-queue/{approvalid}/approve")
    public ResponseEntity<String> approveProduct(@PathVariable int approvalid){
        return productService.approveProduct(approvalid);
    }
    @PutMapping("/products/approval-queue/{approvalid}/reject")
    public ResponseEntity<String> rejectProduct(@PathVariable int approvalid){
        return productService.rejectProduct(approvalid);
    }
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable int productId){
        return productService.deleteProduct(productId);
    }

}
