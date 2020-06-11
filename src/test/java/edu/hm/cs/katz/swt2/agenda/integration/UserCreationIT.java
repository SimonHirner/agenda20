package edu.hm.cs.katz.swt2.agenda.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.hm.cs.katz.swt2.agenda.service.UserService;
import edu.hm.cs.katz.swt2.agenda.service.UserServiceImpl;
import javax.transaction.Transactional;
import javax.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("none ")
@Transactional
class UserCreationIT {
  
  @Autowired
  UserService userService = new UserServiceImpl();
  
  @Test
  @WithUserDetails("admin")
  void createdUserContainsAllInformation() {
    userService.legeAn("diddi", "Dieter", "#Diddi0815", false);
    var createdUser = userService.getUserInfo("diddi");
    
    assertEquals("diddi", createdUser.getLogin());
    assertEquals("Dieter", createdUser.getName());
    assertEquals("Dieter", createdUser.getDisplayName());
  }
  
  @Test
  @WithUserDetails("admin")
  void cannotCreateExistingUser() {
    userService.legeAn("diddi", "Dieter", "#Diddi0815", false);
    assertThrows(ValidationException.class, () -> {
      userService.legeAn("diddi", "Dieter", "#Diddi0815", false);
    });
  }

}
