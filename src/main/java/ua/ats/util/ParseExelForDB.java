package ua.ats.util;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ua.ats.AtsApplication;
import ua.ats.dao.ProductRepository;
import ua.ats.entity.Product;

import java.io.FileInputStream;
import java.io.IOException;

public class ParseExelForDB {

    //private ConfigurableApplicationContext context = SpringApplication.run(AtsApplication.class);
    //private ProductRepository repository = context.getBean(ProductRepository.class);

    public void parseExel(ProductRepository repository) {
        XSSFWorkbook book = null;
        try {
            book = new XSSFWorkbook(new FileInputStream("d://1111.xlsx"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert book != null;
        XSSFSheet sheet = book.getSheetAt(0);


        System.out.println("Start parsing");
        for (int i = 1; i < 16; i++) {

            Row row = sheet.getRow(i);

            Integer ident = null;
            if (row.getCell(1) != null) {
                ident = (int) row.getCell(1).getNumericCellValue();
            }

            String name = null;
            if (row.getCell(2) != null) {
                name = row.getCell(2).getStringCellValue();
            }

            String unitStr = null;
            int unit = 0;
            if (row.getCell(3) != null) {
                unitStr = row.getCell(3).getStringCellValue();

                switch (unitStr) {
                    case "м/п.":
                        unit = 1;
                        break;
                    case "шт.":
                        unit = 2;
                        break;
                    case "компл.":
                        unit = 3;
                        break;
                }
            }

            String currStr = null;
            int curr = 0;
            if (row.getCell(8) != null) {
                currStr = row.getCell(8).getStringCellValue();

                switch (currStr) {
                    case "USD":
                        curr = 1;
                        break;
                    case "EUR":
                        curr = 2;
                        break;
                    case "грн":
                        curr = 3;
                        break;
                }
            }

            String articul = null;
            if (row.getCell(4) != null) {
                if (row.getCell(4).getCellTypeEnum() == CellType.STRING) {
                    articul = row.getCell(4).getStringCellValue();
                }
                if (row.getCell(4).getCellTypeEnum() == CellType.NUMERIC) {
                    articul = String.valueOf((int) row.getCell(4).getNumericCellValue());
                }
            }

            String colorStr = null;
            byte color = 0;
            if (row.getCell(5) != null) {
                colorStr = row.getCell(5).getStringCellValue();
            }
            if ("RAL9016".equals(colorStr)) {
                color = 1;
            }

            Integer price = null;
            if (row.getCell(6) != null) {
                price = (int) (row.getCell(6).getNumericCellValue() * 100);
            }
            String sectionStr = row.getCell(9).getStringCellValue();
            int section = 0;
            switch (sectionStr) {
                case "профиль":
                    section = 1;
                    break;
                case "комплектующие":
                    section = 2;
                    break;
                case "уплотнители":
                    section = 3;
                    break;
                case "фурнитура":
                    section = 4;
                    break;
                case "материалы для монтажа":
                    section = 5;
                    break;
            }
           // repository.save(new Product(ident, name, articul, color, price, unit, section, curr));
        }
        System.out.println("END");
    }
}
