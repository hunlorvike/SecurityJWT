package project.vegist.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String productName;

    private String description;

    private Double price;

    @Column(name = "sale_price")
    private Double salePrice;

    private String SKU;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "wishlist_count")
    private Integer wishlistCount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @ManyToOne
    @JoinColumn(name = "label_id")
    private Label label;

    private Integer discount;

    @Column(name = "iframe_video")
    private String iframeVideo;

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


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductReview> productReviews;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductUnit> productUnits;


    // other fields, getters, setters, and constructors
}

