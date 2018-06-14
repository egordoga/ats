package ua.ats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.ats.entity.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Currency findCurrencyByName(String name);
}
