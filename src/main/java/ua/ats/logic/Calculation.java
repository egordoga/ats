package ua.ats.logic;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import ua.ats.AtsApplication;
import ua.ats.dao.ProductRepository;
import ua.ats.entity.Product;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
//@ComponentScan
public class Calculation {

    private static final Path DIR = Paths.get("d://alumotr");
    private final static int START_ROW = 11;
    private final static int START_CELL = 7;
    private File file;

    private List<Product> profile = new ArrayList<>();
    private List<Product> accessories = new ArrayList<>();
    private List<Product> sealant = new ArrayList<>();
    private List<Product> furniture = new ArrayList<>();

    private List<String> noNeed = Arrays.asList("Профиль", "Итого по разделу", "Комплектующие",
            "Уплотнители", "Остекление (панели)", "Фурнитура", "Материалы для монтажа");

    @Autowired
    //private ApplicationContext context;
    private ProductRepository repository;



    //ConfigurableApplicationContext context = new AnnotationConfigApplicationContext();
    //ProductRepository repository = context.getBean(ProductRepository.class);

    /*@Autowired
    public Calculation(ProductRepository repository) {
        this.repository = repository;
    }*/



    @PostConstruct
    public void fillLists() {

        int i = START_ROW - 1;
        Row row;
        String name;
        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(DIR, "*.xlsx")) {

            for (Path entry : stream) {
                file = entry.toFile();
            }
            XSSFWorkbook book = new XSSFWorkbook(new FileInputStream(file));
            XSSFSheet sheet = book.getSheetAt(0);

            do {
                i++;
                row = sheet.getRow(i);

                if (row.getCell(START_CELL) != null) {
                    name = row.getCell(START_CELL).getStringCellValue();
                } else {
                    continue;
                }

                if (noNeed.contains(name)) {
                    continue;
                }

                Product product = repository.findProductByName(name);

                switch (product.getSectionId()) {
                    case 1:
                        profile.add(product);
                        break;
                    case 2:
                        accessories.add(product);
                        break;
                    case 3:
                        sealant.add(product);
                        break;
                    case 4:
                        furniture.add(product);
                        break;
                }
            } while ("ИТОГО".equals(row.getCell(START_CELL).getStringCellValue()));
        } catch (IOException e) {
            e.printStackTrace();
        }



        profile.forEach(System.out::println);
        System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
        accessories.forEach(System.out::println);
        System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
        sealant.forEach(System.out::println);
        System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
        furniture.forEach(System.out::println);
    }




        public File getFile() {
            return file;
        }
}
