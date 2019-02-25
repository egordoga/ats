package ua.ats.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Integer ident;
    private String name;
    private String articul;
    private Byte color;
    private Byte bicolor;
    private Byte bicolorWhiteOut;
    private Byte bicolorWhiteIn;
    private BigDecimal price;
    private BigDecimal cost;
    private BigDecimal weight;
    private BigDecimal perimeter;

    @ManyToOne()
    @JoinColumn(name = "CURRENCY_ID")
    private Currency currency;

    @ManyToOne()
    @JoinColumn(name = "GROUP_ID")
    private Groupp groupp;

    @ManyToOne()
    @JoinColumn(name = "MEASURE_ID")
    private Measure measure;

    @ManyToOne()
    @JoinColumn(name = "SECTION_ID")
    private Section section;


    @Transient
    private Integer columnNumberExel;
    @Transient
    private BigDecimal sum;
    @Transient
    private BigDecimal colorSum;
    @Transient
    private BigDecimal quantity;
    @Transient
    private BigDecimal cena;
    @Transient
    private BigDecimal discountSum;
    @Transient
    private BigDecimal previousCena;
    @Transient
    private BigDecimal colored;
    @Transient
    private BigDecimal discount;

    public Product(Integer ident, String name, String articul, Byte color, Byte bicolor, Byte bicolorWhiteOut,
                   Byte bicolorWhiteIn, BigDecimal price, BigDecimal cost, BigDecimal weight, BigDecimal perimeter,
                   Currency currency, Groupp groupp, Measure measure, Section sectionn) {
        this.ident = ident;
        this.name = name;
        this.articul = articul;
        this.color = color;
        this.bicolor = bicolor;
        this.bicolorWhiteOut = bicolorWhiteOut;
        this.bicolorWhiteIn = bicolorWhiteIn;
        this.price = price;
        this.cost = cost;
        this.weight = weight;
        this.perimeter = perimeter;
        this.currency = currency;
        this.groupp = groupp;
        this.measure = measure;
        this.section = sectionn;
    }
}
