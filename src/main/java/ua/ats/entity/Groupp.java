package ua.ats.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Groupp {
    private int id;
    private String name;

    private List<Product> products = new ArrayList<>();

    public Groupp() {
    }

    public Groupp(String name) {
        this.name = name;
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "groupp", cascade = CascadeType.ALL)
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
        Groupp groupp = (Groupp) o;
        return id == groupp.id &&
                Objects.equals(name, groupp.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }
}
