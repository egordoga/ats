package ua.ats.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Currency {
    private int id;
    private String name;

    @OneToMany(mappedBy = "currency")
    List<Product> products = new ArrayList<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return id == currency.id &&
                Objects.equals(name, currency.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }
}
