package ua.ats.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Currency {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    public Currency(String name) {
        this.name = name;
    }
}
