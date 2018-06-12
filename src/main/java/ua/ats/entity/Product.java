package ua.ats.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Product {
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
    private Currency currency;
    private Groupp groupp;
    private Measure measure;
    private Section section;


    private Integer columnNumberExel;
    private BigDecimal sum;
    private BigDecimal colorSum;
    private BigDecimal quantity;
    private BigDecimal cena;



    public Product() {
    }

   /* public Product(Integer ident, String name, String articul, Byte color, BigDecimal price, Measure measure, Section section, Currency currency) {
        this.ident = ident;
        this.name = name;
        this.articul = articul;
        this.color = color;
        this.price = price;
        this.measure = measure;
        this.sectionn = section;
        this.currency = currency;
    }*/


    public Product(Integer ident, String name, String articul, Byte color, Byte bicolor, Byte bicolorWhiteOut,
                   Byte bicolorWhiteIn, BigDecimal price, BigDecimal cost, BigDecimal weight, BigDecimal perimeter, Currency currency, Groupp groupp, Measure measure, Section sectionn) {
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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "IDENT")
    public Integer getIdent() {
        return ident;
    }

    public void setIdent(Integer ident) {
        this.ident = ident;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "ARTICUL")
    public String getArticul() {
        return articul;
    }

    public void setArticul(String articul) {
        this.articul = articul;
    }

    @Basic
    @Column(name = "COLOR")
    public Byte getColor() {
        return color;
    }

    public void setColor(Byte color) {
        this.color = color;
    }

    @Basic
    @Column(name = "BICOLOR")
    public Byte getBicolor() {
        return bicolor;
    }

    public void setBicolor(Byte bicolor) {
        this.bicolor = bicolor;
    }

    @Basic
    @Column(name = "BICOLOR_WHITE_OUT")
    public Byte getBicolorWhiteOut() {
        return bicolorWhiteOut;
    }

    public void setBicolorWhiteOut(Byte bicolorWhite) {
        this.bicolorWhiteOut = bicolorWhite;
    }

    @Basic
    @Column(name = "BICOLOR_WHITE_IN")
    public Byte getBicolorWhiteIn() {
        return bicolorWhiteIn;
    }

    public void setBicolorWhiteIn(Byte bicolorWhiteIn) {
        this.bicolorWhiteIn = bicolorWhiteIn;
    }

    @Basic
    @Column(name = "PRICE")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Basic
    @Column(name = "COST")
    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Basic
    @Column(name = "WEIGHT")
    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    @Basic
    @Column(name = "SQUARE")
    public BigDecimal getPerimeter() {
        return perimeter;
    }

    public void setPerimeter(BigDecimal square) {
        this.perimeter = square;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CURRENCY_ID")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }


    @Transient
    public Integer getColumnNumberExel() {
        return columnNumberExel;
    }

    public void setColumnNumberExel(Integer columnNumberExel) {
        this.columnNumberExel = columnNumberExel;
    }

    @Transient
    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    @Transient
    public BigDecimal getColorSum() {
        return colorSum;
    }

    public void setColorSum(BigDecimal colorSum) {
        this.colorSum = colorSum;
    }

    @Transient
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Transient
    public BigDecimal getCena() {
        return cena;
    }

    public void setCena(BigDecimal cena) {
        this.cena = cena;
    }



    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "GROUP_ID")
    public Groupp getGroupp() {
        return groupp;
    }

    public void setGroupp(Groupp groupp) {
        this.groupp = groupp;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "MEASURE_ID")
    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SECTION_ID")
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id &&
                Objects.equals(ident, product.ident) &&
                Objects.equals(name, product.name) &&
                Objects.equals(articul, product.articul) &&
                Objects.equals(color, product.color) &&
                Objects.equals(bicolor, product.bicolor) &&
                Objects.equals(bicolorWhiteOut, product.bicolorWhiteOut) &&
                Objects.equals(price, product.price) &&
                Objects.equals(cost, product.cost) &&
                Objects.equals(weight, product.weight) &&
                Objects.equals(perimeter, product.perimeter) &&
                Objects.equals(currency, product.currency) &&
                Objects.equals(groupp, product.groupp) &&
                Objects.equals(measure, product.measure) &&
                Objects.equals(section, product.section);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, ident, name, articul, color, bicolor, bicolorWhiteOut, price, cost, weight, perimeter, currency, groupp, measure, section);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", articul='" + articul + '\'' +
                ", price=" + price +
                ", cost=" + cost +
                ", sum=" + sum +
                ", quantity=" + quantity +
                ", square=" + perimeter +
                ", cena=" + cena +
                ", colorSum=" + colorSum +
                '}';
    }
}
