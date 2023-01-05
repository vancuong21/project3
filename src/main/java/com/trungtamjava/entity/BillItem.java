package com.trungtamjava.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class BillItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Bill bill;
    @ManyToOne
    private Product product;
    private int quantity;
    private double price;
    @CreatedDate
    @Column(updatable = false)
    private Date createdAt;
    @LastModifiedDate
    private Date lastUpdateAt;
}
