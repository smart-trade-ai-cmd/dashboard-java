package com.amichaimarcus.investment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.amichaimarcus.investment.model.Alert;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUserId(Long userId);
    // ישמש אותנו לסריקה מהירה של התראות פעילות שעדיין לא קפצו
    List<Alert> findByIsTriggeredFalse();
}