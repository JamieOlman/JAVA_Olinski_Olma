package JAVA_Olinski_Olma.repository;

import JAVA_Olinski_Olma.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Rozszerzenie JpaRepository daje nam gotowe metody do bazy danych
}