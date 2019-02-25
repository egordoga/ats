package ua.ats.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Measure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "measure", cascade = CascadeType.ALL)
    private List<Product> products;

    public Measure(String name) {
        this.name = name;
    }
}
