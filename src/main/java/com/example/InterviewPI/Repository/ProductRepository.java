package com.example.InterviewPI.Repository;

import com.example.InterviewPI.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    // Custom query to fetch active products
    @Query("SELECT p FROM Product p WHERE p.status = true order by p.postedDate desc")
    List<Product> findActiveProducts();

    @Query("SELECT p FROM Product p " +
            "WHERE (:productName IS NULL OR p.productName LIKE %:productName%) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:minPostedDate IS NULL OR p.postedDate >= :minPostedDate) " +
            "AND (:maxPostedDate IS NULL OR p.postedDate <= :maxPostedDate)")
    List<Product> searchProducts( @Param("productName") String productName,
                                  @Param("minPrice") int minPrice,
                                  @Param("maxPrice") int maxPrice,
                                  @Param("minPostedDate") Date minPostedDate,
                                  @Param("maxPostedDate") Date maxPostedDate);
}
