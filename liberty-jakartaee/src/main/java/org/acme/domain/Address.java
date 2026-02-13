package org.acme.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public class Address {
  
  @Column(nullable = false)
  @NotBlank(message = "Address is mandatory")
  private String address;
  
  @Column(nullable = false)
  @NotBlank(message = "City is mandatory")
  private String city;
  
  @Column(nullable = false)
  @NotBlank(message = "Country is mandatory")
  private String country;
  
  // Default constructor required by JPA
  public Address() {
  }
  
  public Address(String address, String city, String country) {
    this.address = address;
    this.city = city;
    this.country = country;
  }
  
  public String getAddress() {
    return address;
  }
  
  public void setAddress(String address) {
    this.address = address;
  }
  
  public String getCity() {
    return city;
  }
  
  public void setCity(String city) {
    this.city = city;
  }
  
  public String getCountry() {
    return country;
  }
  
  public void setCountry(String country) {
    this.country = country;
  }
  
  // For compatibility with record-style access
  public String address() {
    return address;
  }
  
  public String city() {
    return city;
  }
  
  public String country() {
    return country;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Address address1 = (Address) o;
    return Objects.equals(address, address1.address) &&
           Objects.equals(city, address1.city) &&
           Objects.equals(country, address1.country);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(address, city, country);
  }
  
  @Override
  public String toString() {
    return "Address{" +
           "address='" + address + '\'' +
           ", city='" + city + '\'' +
           ", country='" + country + '\'' +
           '}';
  }
}
