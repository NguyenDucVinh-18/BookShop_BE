package vn.edu.iuh.fit.bookshop_be.models;


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
    @Column(name = "ProductID")
    private Integer id;


    @Column(name = "ProductName", nullable = false)
    private String productName;

    @Lob
    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "Price", nullable = false)
    private BigDecimal price;

    @Column(name = "StockQuantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "PackageDimensions")
    private String packageDimensions;

    @Column(name = "WeightGrams")
    private Integer weightGrams;

    @ElementCollection
    @Column(name = "imageUrl")
    @CollectionTable(name = "Product_Images", joinColumns = @JoinColumn(name = "ProductID"))
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "ProductType", nullable = false)
    private String productType;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductReview> productReviews;


    //Sách
    @Column(name = "PublisherName", nullable = false)
    private String publisherName;

    @ElementCollection
    @Column(name = "authorNames")
    @CollectionTable(name = "Product_Authors", joinColumns = @JoinColumn(name = "ProductID"))
    private Set<String> authorNames = new HashSet<>();

    @Column(name = "PublicationYear")
    private String publicationYear;

    @Column(name = "PageCount")
    private Integer pageCount;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "CoverType")
    private String coverType;

        //Sách giáo khoa
        @Column(name = "GradeLevel")
        private String gradeLevel;

        //Truyện
        @Column(name = "AgeRating")
        private String ageRating;

        @Column(name = "Genres")
        private String genres;



    // Bút, Ba lô
    @Column(name = "Color")
    private String color;

    @Column(name = "Material")
    private String material;

    @Column(name = "ManufacturingLocation")
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
}
