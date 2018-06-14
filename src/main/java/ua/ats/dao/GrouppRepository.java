package ua.ats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.ats.entity.Groupp;

@Repository
public interface GrouppRepository extends JpaRepository<Groupp, Long> {
    Groupp findGrouppByName(String name);
}
