package edu.hm.cs.katz.swt2.agenda.persistence;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository zum Zugriff auf gespeicherte Anwenderdaten. Repostory-Interfaces erben eine
 * unglaubliche Menge hilfreicher Methoden. Weitere Methoden kann man einfach durch Benennung
 * definierern. Spring Data ergänzt die Implementierungen zur Laufzeit.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

  /**
   * Ermittelt alle Anwender, die Administrator sind (oder alle anderen).
   * 
   * @param isAdministrator Flag zum Filtern
   * @return
   */
  List<User> findByAdministrator(boolean isAdministrator);
  
  /**
   * Ermittelt alle Anwender, die keine Administratoren sind, und sortiert sie nach dem Login.
   * 
   * @return
   */
  List<User> findAllByAdministratorFalseOrderByLoginAsc();

}
