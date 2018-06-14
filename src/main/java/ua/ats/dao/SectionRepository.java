package ua.ats.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.ats.entity.Section;

@Repository
public interface SectionRepository extends CrudRepository<Section, Long> {
    Section findSectionByName(String name);
}
