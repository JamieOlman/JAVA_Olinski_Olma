package JAVA_Olinski_Olma.repository;

import JAVA_Olinski_Olma.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
}