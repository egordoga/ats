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
public class ParseExcelForDB {

    private static final String FILE_MATERIAL = "d://Fornax.xlsx";
    private static final int MATERIAL_LENGTH = 492;
    private static final int IDENT_SELL = 1;
    private static final int MATERIAL_NAME_SELL = 2;
    private static final int MEASURE_SELL = 3;
    private static final int ARTICUL_SELL = 4;
    private static final int COLOR_SELL = 5;
    private static final int PRICE_SELL = 6;
    private static final int CURRENCY_SELL = 8;
    private static final int SECTION_SELL = 9;
    private static final int GROUP_SELL = 10;
    private static final int WEIGHT_SELL = 18;
    private static final int PERIMETER_SELL = 19;
    private static final int BICOLOR_SELL = 20;
    private static final int BICOLOR_WHITE_IN_SELL = 21;
    private static final int BICOLOR_WHITE_OUT_SELL = 22;

    private final ProductRepository productRepository;
    private final CurrencyRepository currencyRepository;
    private final GrouppRepository grouppRepository;
    private final MeasureRepository measureRepository;
    private final SectionRepository sectionRepository;

    @Autowired
    public ParseExcelForDB(ProductRepository productRepository, CurrencyRepository currencyRepository,
                           GrouppRepository grouppRepository, MeasureRepository measureRepository,
                           SectionRepository sectionRepository) {
        this.productRepository = productRepository;
        this.currencyRepository = currencyRepository;
        this.grouppRepository = grouppRepository;
        this.measureRepository = measureRepository;
        this.sectionRepository = sectionRepository;
    }


    public void parseExcel() {
        XSSFWorkbook book = null;
        try {
            book = new XSSFWorkbook(new FileInputStream(FILE_MATERIAL));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert book != null;
        XSSFSheet sheet = book.getSheetAt(0);

        for (int i = 1; i < MATERIAL_LENGTH; i++) {
            Row row = sheet.getRow(i);
            Integer ident = null;
            if (row.getCell(IDENT_SELL) != null) {
                ident = (int) row.getCell(IDENT_SELL).getNumericCellValue();
            }

            String name = null;
            if (row.getCell(MATERIAL_NAME_SELL) != null) {
                name = row.getCell(MATERIAL_NAME_SELL).getStringCellValue();
            }

            Measure measure = null;
            if (row.getCell(MEASURE_SELL) != null) {
                String measureStr = row.getCell(MEASURE_SELL).getStringCellValue();
                measure = measureRepository.findMeasureByName(measureStr);
                if (measure == null) {
                    measure = new Measure(measureStr);
                    measureRepository.save(measure);
                }
            }

            String articul = null;
            if (row.getCell(ARTICUL_SELL) != null) {
                if (row.getCell(ARTICUL_SELL).getCellTypeEnum() == CellType.STRING) {
                    articul = row.getCell(ARTICUL_SELL).getStringCellValue();
                }
                if (row.getCell(ARTICUL_SELL).getCellTypeEnum() == CellType.NUMERIC) {
                    articul = String.valueOf((long) row.getCell(ARTICUL_SELL).getNumericCellValue());
                }
            }

            String colorStr;
            byte color = 0;
            if (row.getCell(COLOR_SELL) != null) {
                colorStr = row.getCell(COLOR_SELL).getStringCellValue().substring(0, 3);
                if ("RAL".equals(colorStr) || "SIL".equals(colorStr)) {
                    color = 1;
                }
            }

            BigDecimal price = null;
            if (row.getCell(PRICE_SELL) != null) {
                price = new BigDecimal(String.valueOf(row.getCell(PRICE_SELL).getNumericCellValue()));
            }

            Currency currency = null;
            if (row.getCell(CURRENCY_SELL) != null) {
                String currStr = row.getCell(CURRENCY_SELL).getStringCellValue();
                currency = currencyRepository.findCurrencyByName(currStr);

                if (currency == null) {
                    currency = new Currency(currStr);
                    currencyRepository.save(currency);
                }

            }

            Section section = null;
            if (row.getCell(SECTION_SELL) != null) {
                String sectionStr = row.getCell(SECTION_SELL).getStringCellValue();
                section = sectionRepository.findSectionByName(sectionStr);
                if (section == null) {
                    section = new Section(sectionStr);
                    sectionRepository.save(section);
                }
            }

            Groupp groupp = null;
            if (row.getCell(GROUP_SELL) != null) {
                String groupStr = row.getCell(GROUP_SELL).getStringCellValue();
                groupp = grouppRepository.findGrouppByName(groupStr);
                if (groupp == null) {
                    groupp = new Groupp(groupStr);
                    grouppRepository.save(groupp);
                }
            }

            BigDecimal weight = null;
            if (row.getCell(WEIGHT_SELL) != null) {
                weight = new BigDecimal(row.getCell(WEIGHT_SELL).getNumericCellValue());
            }

            BigDecimal perimeter = null;
            if (row.getCell(PERIMETER_SELL) != null) {
                perimeter = new BigDecimal(row.getCell(PERIMETER_SELL).getNumericCellValue());
            }

            Byte biColor = 0;
            if (row.getCell(BICOLOR_SELL) != null) {
                if ((int) row.getCell(BICOLOR_SELL).getNumericCellValue() == 1) {
                    biColor = 1;
                }
            }

            Byte biWhiteIn = 0;
            if (row.getCell(BICOLOR_WHITE_IN_SELL) != null) {
                if ((int) row.getCell(BICOLOR_WHITE_IN_SELL).getNumericCellValue() == 1) {
                    biWhiteIn = 1;
                }
            }

            Byte biWhiteOut = 0;
            if (row.getCell(BICOLOR_WHITE_OUT_SELL) != null) {
                if (row.getCell(BICOLOR_WHITE_OUT_SELL).getCellTypeEnum() == CellType.NUMERIC) {
                    if ((int) row.getCell(BICOLOR_WHITE_OUT_SELL).getNumericCellValue() == 1) {
                        biWhiteOut = 1;
                    }
                } else {
                    if (row.getCell(BICOLOR_WHITE_OUT_SELL).getStringCellValue().equals("1")) {
                        biWhiteOut = 1;
                    }
                }
            }
            productRepository.save(new Product(ident, name, articul, color, biColor, biWhiteOut, biWhiteIn,
                    price, BigDecimal.ZERO, weight, perimeter, currency,
                    groupp, measure, section));
        }
    }
}
