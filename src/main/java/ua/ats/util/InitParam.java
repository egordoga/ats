package ua.ats.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class InitParam {

    public static BigDecimal costAlumWhite;
    public static BigDecimal costAlum;
    public static BigDecimal rateEur;
    public static BigDecimal rateUsd;
    public static BigDecimal crossRate;
    public static BigDecimal color;
    public static BigDecimal color9006;
    public static BigDecimal colorFurn;
    public static BigDecimal bicolor;
    public static BigDecimal bicolorWithWhite;
    public static BigDecimal dekor;

    private static final Path PROP = Paths.get("e://alumotr/properties.txt");

    public static void initParam() {
        try {
            List<String> list = Files.readAllLines(PROP);

            costAlumWhite = new BigDecimal(list.get(1));
            costAlum = new BigDecimal(list.get(3));
            rateUsd = new BigDecimal(list.get(5));
            rateEur = new BigDecimal(list.get(7));
            color = new BigDecimal(list.get(9));
            color9006 = new BigDecimal(list.get(11));
            bicolorWithWhite = new BigDecimal(list.get(13));
            bicolor = new BigDecimal(list.get(15));
            dekor = new BigDecimal(list.get(17));
            colorFurn = new BigDecimal(list.get(19));

            initCross();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initCross() {
        crossRate = rateEur.divide(rateUsd, 3, BigDecimal.ROUND_HALF_UP);
    }

}
