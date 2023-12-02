package project.vegist.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "product_categories")
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Boolean status;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "seo_title")
    private String seoTitle;

    @Column(name = "meta_keys")
    private String metaKeys;

    @Column(name = "meta_desc")
    private String metaDesc;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;



    // other fields, getters, setters, and constructors
}

