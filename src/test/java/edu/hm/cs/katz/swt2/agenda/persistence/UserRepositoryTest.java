package edu.hm.cs.katz.swt2.agenda.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {
  @Autowired
  UserRepository userRepository;
  
  @Test
  public void userRepositoryDeliversUsersOrdered() {
    User userA = new User("tttttttt", "Ttttttttt", "#Tiffy2020", false);
    userRepository.save(userA);
    
    User userB = new User("xxxxxxxx", "Xxxxxxxxx", "#Tiffy2020", false);
    userRepository.save(userB);
    
    User userC = new User("uuuuuuuu", "Uuuuuuuuu", "#Tiffy2020", false);
    userRepository.save(userC);
    
    List<User> users = (List<User>) userRepository.findAllByAdministratorFalseOrderByLoginAsc();
    
    assertEquals(3, users.size());
    assertEquals(userA, users.get(0));
    assertEquals(userC, users.get(1));
    assertEquals(userB, users.get(2));
  }
  
  @Test
  public void userRepositoryDeliversAdmins() {
    User admin = new User("tttttttt", "Ttttttttt", "#Tiffy2020", true);
    userRepository.save(admin);
    
    User user = new User("xxxxxxxx", "Xxxxxxxxx", "#Tiffy2020", false);
    userRepository.save(user);
    
    List<User> admins = (List<User>) userRepository.findByAdministrator(true);
    
    assertEquals(1, admins.size());
    assertEquals(admin, admins.get(0));
  }
  
}
