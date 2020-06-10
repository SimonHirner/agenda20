package edu.hm.cs.katz.swt2.agenda.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

@DataJpaTest
public class TopicRepositoryTest {
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  TopicRepository topicRepository;
  
  private static final String UUID_BASE = "12345678901234567890123456789012345";
  
  @Test
  public void topicRepositoryDeliversTopicsOdererd() {
    User user = new User("tiffy", "Tiffy", "#Tiffy2020", false);
    userRepository.save(user);
    
    Topic topicA = new Topic(UUID_BASE + "1", "Ttttttttttt", "Beschreibung", "Beschreibung", user);
    topicRepository.save(topicA);
    
    Topic topicB = new Topic(UUID_BASE + "2", "Xxxxxxxxxxxxx", "Beschreibung", "Beschreibung",
        user);
    topicRepository.save(topicB);
    
    Topic topicC = new Topic(UUID_BASE + "3", "Uuuuuuuuuuuuu", "Beschreibung", "Beschreibung",
        user);
    topicRepository.save(topicC);
    
    List<Topic> topics = (List<Topic>) topicRepository.findAllByCreator(user,
        Sort.by(Sort.Order.asc("title").ignoreCase()));
    
    assertEquals(3, topics.size());
    assertEquals(topicA, topics.get(0));
    assertEquals(topicC, topics.get(1));
    assertEquals(topicB, topics.get(2));
    
    
  }
  
}
