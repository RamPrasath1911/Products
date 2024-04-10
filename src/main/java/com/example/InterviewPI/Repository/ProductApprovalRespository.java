package com.example.InterviewPI.Repository;

import com.example.InterviewPI.Entity.Product;
import com.example.InterviewPI.Entity.ProductApprovalEntity;
import com.example.InterviewPI.Entity.ProductApprovalQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductApprovalRespository extends JpaRepository<ProductApprovalQueue,Integer> {

    @Query("SELECT p FROM ProductApprovalQueue p WHERE p.productId = :id order by p.requestedDate desc")
    ProductApprovalQueue findbyProductId(int id);

    @Query("SELECT p, pq.id from ProductApprovalQueue pq left join Product p ON pq.productId=p.id order by pq.requestedDate")
    List<ProductApprovalEntity> findAllProducts();


}
