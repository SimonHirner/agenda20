package edu.hm.cs.katz.swt2.agenda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.hm.cs.katz.swt2.agenda.persistence.User;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRepository;
import javax.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Mock
  UserRepository userRepository;
    
  @InjectMocks
  UserService userService = new UserServiceImpl();
    
  @Test
  void createUserSuccess() {
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    userService.legeAn("diddi", "Dieter", "#Diddi0815", false);
    Mockito.verify(userRepository).save(userCaptor.capture());
    assertEquals("diddi", userCaptor.getValue().getLogin());
  }
    
  @Test
  void createUserFailsForExistingLogin() {
    Mockito.when(userRepository.existsById("diddi")).thenReturn(true);
    assertThrows(ValidationException.class, () -> {
      userService.legeAn("diddi", "Dieter", "#Diddi0815", false);
    });
  }
  
  @Test
  void createUserFailsForEmptyLogin() {
    assertThrows(ValidationException.class, () -> {
      userService.legeAn("", "Dieter", "#Diddi0815", false);
    });
  }
  
  @Test
  void createUserFailsForLoginTooShort() {
    assertThrows(ValidationException.class, () -> {
      userService.legeAn("d", "Dieter", "#Diddi0815", false);
    });
  }
  
  @Test
  void createUserFailsForLoginTooLong() {
    assertThrows(ValidationException.class, () -> {
      userService.legeAn("dieterdieterdieterdieterdieterdieter", "Dieter", "#Diddi0815", false);
    });
  }
}
