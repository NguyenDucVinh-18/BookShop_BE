package vn.edu.iuh.fit.bookshop_be.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer id;


    @Column(name = "ProductName", nullable = false)
    private String productName;

    @Lob
    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "Price", nullable = false)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "package_dimensions")
    private String packageDimensions;

    @Column(name = "weight_grams")
    private Integer weightGrams;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "product_type", nullable = false)
    private String productType;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductReview> productReviews;


    //Sách
    @Column(name = "publisher_name")
    private String publisherName;

    @ElementCollection
    @CollectionTable(name = "product_authors", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "author_name")
    private Set<String> authorNames = new HashSet<>();

    @Column(name = "publication_year")
    private String publicationYear;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "cover_type")
    private String coverType;

    //Sách giáo khoa
    @Column(name = "grade_level")
    private String gradeLevel;

    //Truyện
    @Column(name = "age_rating")
    private String ageRating;

    @Column(name = "genres")
    private String genres;



    // Bút, Ba lô
    @Column(name = "Color")
    private String color;

    @Column(name = "material")
    private String material;

    @Column(name = "manufacturing_location")
    private String manufacturingLocation;


    public Product() {}

    public Product(Integer id, Category category, String productName, String description, BigDecimal price, Integer stockQuantity, String packageDimensions, Integer weightGrams, String publisherName, Set<String> authorNames, String publicationYear, Integer pageCount, String isbn, String coverType, String gradeLevel, String ageRating, String genres, String color, String material, String manufacturingLocation, List<String> imageUrls, String productType, List<CartItem> cartItems, List<OrderItem> orderItems) {
        this.id = id;
        this.category = category;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.packageDimensions = packageDimensions;
        this.weightGrams = weightGrams;
        this.publisherName = publisherName;
        this.authorNames = authorNames;
        this.publicationYear = publicationYear;
        this.pageCount = pageCount;
        this.isbn = isbn;
        this.coverType = coverType;
        this.gradeLevel = gradeLevel;
        this.ageRating = ageRating;
        this.genres = genres;
        this.color = color;
        this.material = material;
        this.manufacturingLocation = manufacturingLocation;
        this.imageUrls = imageUrls;
        this.productType = productType;
        this.cartItems = cartItems;
        this.orderItems = orderItems;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getPackageDimensions() {
        return packageDimensions;
    }

    public void setPackageDimensions(String packageDimensions) {
        this.packageDimensions = packageDimensions;
    }

    public Integer getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(Integer weightGrams) {
        this.weightGrams = weightGrams;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public Set<String> getAuthorNames() {
        return authorNames;
    }

    public void setAuthorNames(Set<String> authorNames) {
        this.authorNames = authorNames;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCoverType() {
        return coverType;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getManufacturingLocation() {
        return manufacturingLocation;
    }

    public void setManufacturingLocation(String manufacturingLocation) {
        this.manufacturingLocation = manufacturingLocation;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<ProductReview> getProductReviews() {
        return productReviews;
    }

    public void setProductReviews(List<ProductReview> productReviews) {
        this.productReviews = productReviews;
    }
}
