package ua.ats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Service;
import ua.ats.dao.ProductRepository;
import ua.ats.entity.Product;

@Service
//@EntityScan(basePackages = {"ua.ats.entity"})
public class ProductService {

    //@Qualifier("productRepository")
    @Autowired
    private ProductRepository productRepository;

    public Product findProductByArticul(String articul) {
        return productRepository.findProductByArticul(articul);
    }
}
