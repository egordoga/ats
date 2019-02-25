package ua.ats.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Groupp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "groupp", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    public Groupp(String name) {
        this.name = name;
    }
}
