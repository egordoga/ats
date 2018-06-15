package ua.ats.util;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import ua.ats.AtsApplication;
import ua.ats.dao.*;
import ua.ats.entity.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Component
public class ParseExelForDB {

    //private ConfigurableApplicationContext context = SpringApplication.run(AtsApplication.class);
    //private ProductRepository repository = context.getBean(ProductRepository.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private GrouppRepository grouppRepository;

    @Autowired
    private MeasureRepository measureRepository;

    @Autowired
    private SectionRepository sectionRepository;


    public void parseExel() {
        XSSFWorkbook book = null;
        try {
            book = new XSSFWorkbook(new FileInputStream("d://111.xlsx"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert book != null;
        XSSFSheet sheet = book.getSheetAt(0);


        System.out.println("Start parsing");



        for (int i = 1; i < 32; i++) {

            System.out.println(i);

            Row row = sheet.getRow(i);

            Integer ident = null;
            if (row.getCell(1) != null) {
                ident = (int) row.getCell(1).getNumericCellValue();
            }

            String name = null;
            if (row.getCell(2) != null) {
                name = row.getCell(2).getStringCellValue();
            }

            Measure measure = null;
            if (row.getCell(3) != null) {
                String mesaureStr = row.getCell(3).getStringCellValue();
                measure = measureRepository.findMeasureByName(mesaureStr);
                if (measure == null) {
                    measure = new Measure(mesaureStr);
                    measureRepository.save(measure);
                }


               /* switch (unitStr) {
                    case "м/п.":
                        unit = 1;
                        break;
                    case "шт.":
                        unit = 2;
                        break;
                    case "компл.":
                        unit = 3;
                        break;
                }*/
            }

            Currency currency = null;
            if (row.getCell(8) != null) {
                String currStr = row.getCell(8).getStringCellValue();
                currency = currencyRepository.findCurrencyByName(currStr);

                if (currency == null) {
                    currency = new Currency(currStr);
                    currencyRepository.save(currency);
                }
                System.out.println(currency.toString());


               /* switch (currStr) {
                    case "USD":
                        curr = 1;
                        break;
                    case "EUR":
                        curr = 2;
                        break;
                    case "грн":
                        curr = 3;
                        break;
                }*/
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
            if ("RAL".equals(colorStr.substring(0,3))) {
                color = 1;
            }

            BigDecimal price = null;
            if (row.getCell(6) != null) {
                price = new BigDecimal(String.valueOf(row.getCell(6).getNumericCellValue()));
            }


            Section section = null;
            if (row.getCell(9) != null) {
                String sectionStr = row.getCell(9).getStringCellValue();
                section = sectionRepository.findSectionByName(sectionStr);
                if (section ==null) {
                    section = new Section(sectionStr);
                    sectionRepository.save(section);
                }
            }
            /*int section = 0;
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
            }*/
            productRepository.save(new Product(ident, name, articul, color,null,null,null,
                    price,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, currency,
                    null, measure, section));

            System.out.println("BB " + i);
        }
        System.out.println("END");
    }
}
