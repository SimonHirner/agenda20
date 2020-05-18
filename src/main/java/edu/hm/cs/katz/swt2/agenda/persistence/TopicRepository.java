package edu.hm.cs.katz.swt2.agenda.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository zum Zugriff auf gespeicherte Topics. Repostory-Interfaces erben eine unglaubliche
 * Menge hilfreicher Methoden. Weitere Methoden kann man einfach durch Benennung definierern. Spring
 * Data ergänzt die Implementierungen zur Laufzeit.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, String> {

  /**
   * Findet alle Topics zu einem gegebenen Anwender.
   * 
   * @param creator Anwender
   * @return
   */
  List<Topic> findByCreator(User creator);

  /**
   * Zähle alle Topics, die von einem bestimmten User erstellt wurden.
   * 
   * @param user Creator
   * @return
   */
  int countByCreator(User user);

}
