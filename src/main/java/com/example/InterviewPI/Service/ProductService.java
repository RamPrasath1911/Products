package com.example.InterviewPI.Service;

import com.example.InterviewPI.Entity.Product;
import com.example.InterviewPI.Entity.ProductApprovalEntity;
import com.example.InterviewPI.Entity.ProductApprovalQueue;
import com.example.InterviewPI.Repository.ProductApprovalRespository;
import com.example.InterviewPI.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {
    //API to List Active Products   /api/products
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductApprovalRespository productApprovalRespository;
    public ResponseEntity<List<Product>> getProducts(){
        try {
            List<Product> productList = productRepository.findActiveProducts();
            return ResponseEntity.status(200).body(productList);
        }
        catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace(); // You might want to use a logging framework like Log4j or SLF4J

            // Return an appropriate error response
            return ResponseEntity.status(500).body(null); // Internal Server Error
        }
    }

    //API to search products /api/products/search

    public ResponseEntity<List<Product>> searchProducts(String productName, int minPrice, int maxPrice, Date minPostDate, Date maxPostDate){
        try {
            List<Product> productList = productRepository.searchProducts(productName, minPrice, maxPrice, minPostDate, maxPostDate);
            return ResponseEntity.status(200).body(productList);
        }
        catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace(); // You might want to use a logging framework like Log4j or SLF4J

            // Return an appropriate error response
            return ResponseEntity.status(500).body(null); // Internal Server Error
        }
    }
    //API to create a Product  /api/products
    //Price should not exceed $10k, if it is more than $5K then add it to approval queue

    public ResponseEntity<String> createProduct(Product product){
        try {
            Product product1 = product;
            if (product.getPrice() > 10000) {
                return ResponseEntity.status(400).body("Product greater than 10K price is not accepted");
            }
            if (product.getPrice() > 5000) {
                product1.setStatus(false);
                Product savedProduct = productRepository.save(product1);
                ProductApprovalQueue productApprovalQueue = new ProductApprovalQueue();
                productApprovalQueue.setProductId(savedProduct.getId());
                productApprovalQueue.setAction("Update");
                productApprovalQueue.setProductname(savedProduct.getProductName());
                productApprovalQueue.setPrice(savedProduct.getPrice());
                productApprovalQueue.setRequestedDate(new Date());
                productApprovalRespository.save(productApprovalQueue);
                return ResponseEntity.status(201).body("Product has been sent for approval");
            }
            product1.setStatus(true);
            productRepository.save(product1);
            return ResponseEntity.status(201).body("Product has been created");
        }
        catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace(); // You might want to use a logging framework like Log4j or SLF4J

            // Return an appropriate error response
            return ResponseEntity.status(500).body("An unexpected error occurred while creating the product");
        }
    }

    //API to update a Product  /api/products/{productid}
    public ResponseEntity<String> updateProduct(int productId, Product product){
        try {
            Product product1 = productRepository.getById(productId);
            ProductApprovalQueue productApprovalQueue = productApprovalRespository.findbyProductId(productId);

            int newPrice = product.getPrice();
            int currentPrice = product1.getPrice();
            if (newPrice > (currentPrice / 2) || (!product1.isStatus())) {
                productApprovalQueue.setPrice(newPrice);
                productApprovalQueue.setProductname(product.getProductName());
                productApprovalQueue.setRequestedDate(new Date());
                productApprovalQueue.setAction("Update");
                productApprovalRespository.save(productApprovalQueue);
                return ResponseEntity.status(201).body("Product has been updated and sent for approval");
            }

            product1.setProductName(product.getProductName());
            product1.setPrice(product.getPrice());
            product1.setPostedDate(new Date());
            product1.setStatus(true);
            productRepository.save(product1);
            return ResponseEntity.status(201).body("Product has been updated");
        }
        catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace(); // You might want to use a logging framework like Log4j or SLF4J

            // Return an appropriate error response
            return ResponseEntity.status(500).body("An unexpected error occurred while updating the product");
        }
    }

    //method to delete product and send to approval queue
    public ResponseEntity<String> deleteProduct(int productid){
        try {
            ProductApprovalQueue productApprovalQueue = productApprovalRespository.findbyProductId(productid);
            productApprovalQueue.setAction("Delete");
            productApprovalQueue.setRequestedDate(new Date());
            productApprovalRespository.save(productApprovalQueue);
            return ResponseEntity.status(201).body("Product deletion has been sent for approval");
        }
        catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace(); // You might want to use a logging framework like Log4j or SLF4J

            // Return an appropriate error response
            return ResponseEntity.status(500).body("An unexpected error occurred while updating the product");
        }
        }

    //method to get products from approval queue
    public ResponseEntity<List<ProductApprovalEntity>> getApprovalQueueProducts(){
        try {
            List<ProductApprovalEntity> productList = productApprovalRespository.findAllProducts();
            return ResponseEntity.status(200).body(productList);
        }
        catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace(); // You might want to use a logging framework like Log4j or SLF4J

            // Return an appropriate error response
            return ResponseEntity.status(500).body(null); // Internal Server Error
        }
        }

    //method to approve a product
    public ResponseEntity<String> approveProduct(int approvalId){
        try {

            ProductApprovalQueue productApprovalQueue = productApprovalRespository.getById(approvalId);
            Product product = productRepository.getById(productApprovalQueue.getProductId());

            if (Objects.equals(productApprovalQueue.getAction(), "Delete")) {
                productRepository.delete(product);
            } else {
                product.setPrice(productApprovalQueue.getPrice());
                product.setProductName(productApprovalQueue.getProductname());
                product.setPostedDate(productApprovalQueue.getRequestedDate());
                product.setStatus(true);
                productRepository.save(product);
            }
            productApprovalRespository.delete(productApprovalQueue);
            return ResponseEntity.status(201).body("Selected Approval Request has been approved");
        }
        catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace(); // You might want to use a logging framework like Log4j or SLF4J

            // Return an appropriate error response
            return ResponseEntity.status(500).body("An unexpected error occurred while updating the product");
        }
        }
    //method to reject a product

    public ResponseEntity<String> rejectProduct(int approvalId){
    try {
        ProductApprovalQueue productApprovalQueue = productApprovalRespository.getById(approvalId);
        productApprovalRespository.delete(productApprovalQueue);
        return ResponseEntity.status(201).body("Selected Approval Request has been rejected");
    }
    catch (Exception e) {
        // Log the exception for debugging purposes
        e.printStackTrace(); // You might want to use a logging framework like Log4j or SLF4J

        // Return an appropriate error response
        return ResponseEntity.status(500).body("An unexpected error occurred while updating the product");
    }

    }


}
