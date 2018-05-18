package ua.ats.entity;

import javax.persistence.*;
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
    private Integer price;
    private Integer cost;
    private Integer weight;
    private Integer square;
    private Integer currencyId;
    private Integer groupId;
    private Integer measureId;
    private Integer sectionId;

    public Product() {
    }

    public Product(Integer ident, String name, String articul, Byte color, Integer price, Integer measureId, Integer sectionId, Integer currencyId) {
        this.ident = ident;
        this.name = name;
        this.articul = articul;
        this.color = color;
        this.price = price;
        this.measureId = measureId;
        this.sectionId = sectionId;
        this.currencyId = currencyId;
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
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Basic
    @Column(name = "COST")
    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    @Basic
    @Column(name = "WEIGHT")
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Basic
    @Column(name = "SQUARE")
    public Integer getSquare() {
        return square;
    }

    public void setSquare(Integer square) {
        this.square = square;
    }

    @Basic
    @Column(name = "CURRENCY_ID")
    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    @Basic
    @Column(name = "GROUP_ID")
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Basic
    @Column(name = "MEASURE_ID")
    public Integer getMeasureId() {
        return measureId;
    }

    public void setMeasureId(Integer measureId) {
        this.measureId = measureId;
    }

    @Basic
    @Column(name = "SECTION_ID")
    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
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
                Objects.equals(currencyId, product.currencyId) &&
                Objects.equals(groupId, product.groupId) &&
                Objects.equals(measureId, product.measureId) &&
                Objects.equals(sectionId, product.sectionId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, ident, name, articul, color, bicolor, bicolorWhite, price, cost, weight, square, currencyId, groupId, measureId, sectionId);
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
                ", currencyId=" + currencyId +
                ", groupId=" + groupId +
                ", measureId=" + measureId +
                ", sectionId=" + sectionId +
                '}';
    }
}
