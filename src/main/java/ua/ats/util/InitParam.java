package ua.ats.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class InitParam {

    public static BigDecimal costAlumWhite;     // = new BigDecimal("3.97");
    public static BigDecimal costAlum;          // = new BigDecimal("3.97");
    public static BigDecimal rateEur;           // = new BigDecimal("1.172");
    public static BigDecimal rateUsd;           // = new BigDecimal("27.1");
    public static BigDecimal color;             // = new BigDecimal("160");
    public static BigDecimal color9006;         // = new BigDecimal("170");
    public static BigDecimal colorFurn;         // = new BigDecimal("50");
    public static BigDecimal bicolor;           // = new BigDecimal("190");
    public static BigDecimal bicolorWithWhite;  // = new BigDecimal("380");
    public static BigDecimal dekor;             // = new BigDecimal("630");


    private static final Path PROP = Paths.get("d://alumotr/properties.txt");


    public static void initParam() {
        try {
            List<String> list = Files.readAllLines(PROP);

            //list.forEach(System.out::println);
            System.out.println(list.get(1));
            System.out.println(list.get(3));

            String s = list.get(1);

            costAlumWhite = new BigDecimal(s);
            costAlum = new BigDecimal(list.get(3));
            rateUsd = new BigDecimal(list.get(5));
            rateEur = new BigDecimal(list.get(7));
            color = new BigDecimal(list.get(9));
            color9006 = new BigDecimal(list.get(11));
            bicolor = new BigDecimal(list.get(13));
            dekor = new BigDecimal(list.get(15));
            colorFurn = new BigDecimal(list.get(17));
//            costAlum = new BigDecimal(list.get(1));
//            costAlum = new BigDecimal(list.get(1));
//            costAlum = new BigDecimal(list.get(1));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
