package ua.ats.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.ats.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findByArticul(String articul);
    List<Product> findByName(String name);
    Product findProductByName(String name);
    Product findProductByArticul(String articul);
}
