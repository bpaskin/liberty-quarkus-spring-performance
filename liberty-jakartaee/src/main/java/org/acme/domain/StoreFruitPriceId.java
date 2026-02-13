package org.acme.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class StoreFruitPriceId implements Serializable {
  
  @Column(nullable = false)
  private Long storeId;
  
  @Column(nullable = false)
  private Long fruitId;
  
  // Default constructor required by JPA
  public StoreFruitPriceId() {
  }
  
  public StoreFruitPriceId(Long storeId, Long fruitId) {
    this.storeId = storeId;
    this.fruitId = fruitId;
  }
  
  public StoreFruitPriceId(Store store, Fruit fruit) {
    this.storeId = (store != null) ? store.getId() : null;
    this.fruitId = (fruit != null) ? fruit.getId() : null;
  }
  
  public Long getStoreId() {
    return storeId;
  }
  
  public void setStoreId(Long storeId) {
    this.storeId = storeId;
  }
  
  public Long getFruitId() {
    return fruitId;
  }
  
  public void setFruitId(Long fruitId) {
    this.fruitId = fruitId;
  }
  
  // For compatibility with record-style access
  public Long storeId() {
    return storeId;
  }
  
  public Long fruitId() {
    return fruitId;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StoreFruitPriceId that = (StoreFruitPriceId) o;
    return Objects.equals(storeId, that.storeId) &&
           Objects.equals(fruitId, that.fruitId);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(storeId, fruitId);
  }
  
  @Override
  public String toString() {
    return "StoreFruitPriceId{" +
           "storeId=" + storeId +
           ", fruitId=" + fruitId +
           '}';
  }
}
