package ua.ats.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Measure {
    private int id;
    private String name;
    private List<Product> products;

    @Id
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "measure")
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measure measure = (Measure) o;
        return id == measure.id &&
                Objects.equals(name, measure.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }
}
