package ua.ats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.ats.entity.Measure;

@Repository
public interface MeasureRepository extends JpaRepository<Measure, Long> {
    Measure findMeasureByName(String name);
}
