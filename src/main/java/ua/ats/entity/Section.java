package ua.ats.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    public Section(String name) {
        this.name = name;
    }
}
