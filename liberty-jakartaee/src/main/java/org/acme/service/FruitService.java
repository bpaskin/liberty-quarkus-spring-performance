package org.acme.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.acme.dto.FruitDTO;
import org.acme.mapping.FruitMapper;
import org.acme.repository.FruitRepository;

@ApplicationScoped
public class FruitService {
  private FruitRepository fruitRepository;

  // No-arg constructor required for CDI proxy
  public FruitService() {
  }

  @Inject
  public FruitService(FruitRepository fruitRepository) {
    this.fruitRepository = fruitRepository;
  }

  public List<FruitDTO> getAllFruits() {
    return this.fruitRepository.listAll().stream()
        .map(FruitMapper::map)
        .collect(Collectors.toList());
  }

  public Optional<FruitDTO> getFruitByName(String name) {
    return this.fruitRepository.findByName(name)
        .map(FruitMapper::map);
  }

  @Transactional
  public FruitDTO createFruit(FruitDTO fruitDTO) {
    var fruit = FruitMapper.map(fruitDTO);
    this.fruitRepository.persist(fruit);

    return FruitMapper.map(fruit);
  }
}
