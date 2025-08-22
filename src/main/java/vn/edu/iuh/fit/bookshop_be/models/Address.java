package vn.edu.iuh.fit.bookshop_be.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name= "number", nullable = false)
    private String number;

    @Column(name = "street")
    private String street;

    @Column(name = "district" )
    private String district;

    @Column(name = "city")
    private String city;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Getters and setters
    public int getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }



    public Address(Integer id, String number, String street, String district, String city, User user) {
        this.id = id;
        this.number = number;
        this.street = street;
        this.district = district;
        this.city = city;
        this.user = user;
    }

    public Address() {
    }
}
