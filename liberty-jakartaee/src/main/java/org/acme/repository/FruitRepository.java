package org.acme.repository;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.acme.domain.Fruit;

@ApplicationScoped
public class FruitRepository {
	
	@PersistenceContext(unitName = "fruitsPU")
	private EntityManager entityManager;
	
	// No-arg constructor required for CDI proxy
	public FruitRepository() {
	}
	
	public List<Fruit> listAll() {
		TypedQuery<Fruit> query = entityManager.createQuery("SELECT f FROM Fruit f", Fruit.class);
		return query.getResultList();
	}
	
	public Optional<Fruit> findByName(String name) {
		TypedQuery<Fruit> query = entityManager.createQuery(
			"SELECT f FROM Fruit f WHERE f.name = :name", Fruit.class);
		query.setParameter("name", name);
		return query.getResultList().stream().findFirst();
	}
	
	public void persist(Fruit fruit) {
		entityManager.persist(fruit);
	}
	
	public Fruit merge(Fruit fruit) {
		return entityManager.merge(fruit);
	}
	
	public void remove(Fruit fruit) {
		entityManager.remove(entityManager.contains(fruit) ? fruit : entityManager.merge(fruit));
	}
	
	public Fruit findById(Long id) {
		return entityManager.find(Fruit.class, id);
	}
}
