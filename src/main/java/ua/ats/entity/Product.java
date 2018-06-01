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
    private Byte bicolorWhite;
    private BigDecimal price;
    private BigDecimal cost;
    private BigDecimal weight;
    private BigDecimal square;

    @ManyToOne
    @JoinColumn(name = "CURRENCY_ID")
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "GROUP_ID")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "MEASURE_ID")
    private Measure measure;

    @ManyToOne
    @JoinColumn(name = "SECTION_ID")
    private Section section;


    private Integer columnNumberExel;
    private BigDecimal sum;
    private BigDecimal quantity;
    private BigDecimal cena;



    public Product() {
    }

    public Product(Integer ident, String name, String articul, Byte color, BigDecimal price, Measure measure, Section section, Currency currency) {
        this.ident = ident;
        this.name = name;
        this.articul = articul;
        this.color = color;
        this.price = price;
        this.measure = measure;
        this.section = section;
        this.currency = currency;
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
    @Column(name = "BICOLOR_WHITE")
    public Byte getBicolorWhite() {
        return bicolorWhite;
    }

    public void setBicolorWhite(Byte bicolorWhite) {
        this.bicolorWhite = bicolorWhite;
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
    public BigDecimal getSquare() {
        return square;
    }

    public void setSquare(BigDecimal square) {
        this.square = square;
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

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

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
                Objects.equals(bicolorWhite, product.bicolorWhite) &&
                Objects.equals(price, product.price) &&
                Objects.equals(cost, product.cost) &&
                Objects.equals(weight, product.weight) &&
                Objects.equals(square, product.square) &&
                Objects.equals(currency, product.currency) &&
                Objects.equals(group, product.group) &&
                Objects.equals(measure, product.measure) &&
                Objects.equals(section, product.section);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, ident, name, articul, color, bicolor, bicolorWhite, price, cost, weight, square, currency, group, measure, section);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", ident=" + ident +
                ", name='" + name + '\'' +
                ", articul='" + articul + '\'' +
                ", color=" + color +
                ", bicolor=" + bicolor +
                ", bicolorWhite=" + bicolorWhite +
                ", price=" + price +
                ", cost=" + cost +
                ", weight=" + weight +
                ", square=" + square +
                ", currencyId=" + currency +
                ", groupId=" + group +
                ", measureId=" + measure +
                ", sectionId=" + section +
                '}';
    }
}
