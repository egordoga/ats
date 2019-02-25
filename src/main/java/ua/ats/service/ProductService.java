package ua.ats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.ats.dao.ProductRepository;
import ua.ats.entity.Product;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product findProductByArticul(String articul) {
        return productRepository.findProductByArticul(articul);
    }
}
