package edu.hm.cs.katz.swt2.agenda.persistence;

import java.util.Collection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository zum Zugriff auf gespeicherte Tasks. Repostory-Interfaces erben eine unglaubliche Menge
 * hilfreicher Methoden. Weitere Methoden kann man einfach durch Benennung definierern. Spring Data
 * ergänzt die Implementierungen zur Laufzeit.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
  /**
   * Ermittelt alle Tasks, die einem bestimmten Topic zugeordnet sind und soritert sie entsprechend.
   * 
   * @param topic Topic, nachdem gefiltert werden soll
   * @param sort Gewünschte Sortierung
   * @return
   */
  Collection<Task> findAllByTopic(Topic topic, Sort sort);
  
  /**
   * Ermittelt alle Tasks, die einem von mehreren Topics zugeordnet sind und sortiert sie
   * entsprechend.
   * 
   * 
   * @param topics Topics, nach denen gefiltert werden soll
   * @param sort Gewünschte Sortierung
   * @return
   */
  Collection<Task> findAllByTopicIn(Collection<Topic> topics, Sort sort);
}
