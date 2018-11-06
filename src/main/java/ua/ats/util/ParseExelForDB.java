package ua.ats.util;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ats.dao.*;
import ua.ats.entity.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Component
public class ParseExelForDB {

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
            book = new XSSFWorkbook(new FileInputStream("d://Fornax.xlsx"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert book != null;
        XSSFSheet sheet = book.getSheetAt(0);


        System.out.println("Start parsing");


        for (int i = 1; i < 492; i++) {

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
            }

            String articul = null;
            if (row.getCell(4) != null) {
                if (row.getCell(4).getCellTypeEnum() == CellType.STRING) {
                    articul = row.getCell(4).getStringCellValue();
                }
                if (row.getCell(4).getCellTypeEnum() == CellType.NUMERIC) {
                    articul = String.valueOf((long) row.getCell(4).getNumericCellValue());
                }
            }

            String colorStr = null;
            byte color = 0;
            if (row.getCell(5) != null) {
                colorStr = row.getCell(5).getStringCellValue().substring(0, 3);

                if ("RAL".equals(colorStr) || "SIL".equals(colorStr)) {
                    color = 1;
                }

            }

            BigDecimal price = null;
            if (row.getCell(6) != null) {
                price = new BigDecimal(String.valueOf(row.getCell(6).getNumericCellValue()));
            }

            Currency currency = null;
            if (row.getCell(8) != null) {
                String currStr = row.getCell(8).getStringCellValue();
                currency = currencyRepository.findCurrencyByName(currStr);

                if (currency == null) {
                    currency = new Currency(currStr);
                    currencyRepository.save(currency);
                }

            }

            Section section = null;
            if (row.getCell(9) != null) {
                String sectionStr = row.getCell(9).getStringCellValue();
                section = sectionRepository.findSectionByName(sectionStr);
                if (section == null) {
                    section = new Section(sectionStr);
                    sectionRepository.save(section);
                }
            }

            Groupp groupp = null;
            if (row.getCell(10) != null) {
                String groupStr = row.getCell(10).getStringCellValue();
                groupp = grouppRepository.findGrouppByName(groupStr);
                if (groupp == null) {
                    groupp = new Groupp(groupStr);
                    grouppRepository.save(groupp);
                }
            }

            BigDecimal weight = null;
            if (row.getCell(18) != null) {
                weight = new BigDecimal(row.getCell(18).getNumericCellValue());
            }

            BigDecimal perimeter = null;
            if (row.getCell(19) != null) {
                perimeter = new BigDecimal(row.getCell(19).getNumericCellValue());
            }

            Byte bi = 0;
            if (row.getCell(20) != null) {
                if ((int) row.getCell(20).getNumericCellValue() == 1) {
                    bi = 1;
                }
            }

            Byte biWI = 0;
            if (row.getCell(21) != null) {
                if ((int) row.getCell(21).getNumericCellValue() == 1) {
                    biWI = 1;
                }
            }

            Byte biWO = 0;
            if (row.getCell(22) != null) {
                if (row.getCell(22).getCellTypeEnum() == CellType.NUMERIC) {
                    if ((int) row.getCell(22).getNumericCellValue() == 1) {
                        biWO = 1;
                    }
                } else {
                    if (row.getCell(22).getStringCellValue().equals("1")) {
                        biWO = 1;
                    }
                }
            }


            productRepository.save(new Product(ident, name, articul, color, bi, biWO, biWI,
                    price, BigDecimal.ZERO, weight, perimeter, currency,
                    groupp, measure, section));

            System.out.println("BB " + i);
        }
        System.out.println("THE END");
    }
}
