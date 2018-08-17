package ua.ats.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.ats.entity.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    Product findProductByArticul(String articul);
}
