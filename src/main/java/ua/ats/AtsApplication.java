package ua.ats;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ua.ats.dao.ProductRepository;
import ua.ats.entity.Product;
import ua.ats.logic.Calculation;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
//@Configuration
//@EnableAutoConfiguration

//@EnableJpaRepositories
public class AtsApplication {

    public static void main(String[] args) {
        //SpringApplication.run(AtsApplication.class, args);

        ConfigurableApplicationContext context = SpringApplication.run(AtsApplication.class);
        ProductRepository repository = context.getBean(ProductRepository.class);

        System.out.println();
        repository.findByArticul("SY.C50.G07").forEach(System.out::println);
        System.out.println();


        Calculation calculation = new Calculation();
        System.out.println();
        System.out.println();
        calculation.getSheet();
    }
}

