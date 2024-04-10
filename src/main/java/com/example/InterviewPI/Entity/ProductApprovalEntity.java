package com.example.InterviewPI.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductApprovalEntity {

    int id;
    String productName;
    int price;
    Date postedDate;
    boolean status;
    int productApprovalId;
}
