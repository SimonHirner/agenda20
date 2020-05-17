package edu.hm.cs.katz.swt2.agenda.service;

import static edu.hm.cs.katz.swt2.agenda.common.SecurityHelper.ADMIN_ROLES;
import static edu.hm.cs.katz.swt2.agenda.common.SecurityHelper.STANDARD_ROLES;

import edu.hm.cs.katz.swt2.agenda.persistence.User;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRepository;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserDisplayDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.ValidationException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service-Klasse zur Verwaltung von Anwendern. Wird auch genutzt, um Logins zu validieren.
 * Servicemethoden sind transaktional und rollen alle Änderungen zurück, wenn eine Exception
 * auftritt. Service-Methoden sollten
 * <ul>
 * <li>keine Modell-Objekte herausreichen, um Veränderungen des Modells außerhalb des
 * transaktionalen Kontextes zu verhindern - Schnittstellenobjekte sind die DTOs (Data Transfer
 * Objects).
 * <li>die Berechtigungen überprüfen, d.h. sich nicht darauf verlassen, dass die Zugriffen über die
 * Webcontroller zulässig sind.</li>
 * </ul>
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserDetailsService, UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired
  private UserRepository anwenderRepository;

  @Autowired
  private ModelMapper mapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    LOG.info("Rufe Anwender {} auf.", username);
    
    Optional<User> findeMitspieler =
        anwenderRepository.findById(username);
    if (findeMitspieler.isPresent()) {
      User user = findeMitspieler.get();
      return new org.springframework.security.core.userdetails.User(user.getLogin(),
          user.getPassword(),
          user.isAdministrator() ? ADMIN_ROLES : STANDARD_ROLES);
    } else {
      LOG.debug("Anwender {} konnte nicht gefunden werden.", username);
      throw new UsernameNotFoundException("Anwender konnte nicht gefunden werden.");
    }
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public List<UserDisplayDto> getAllUsers() {
    LOG.info("Rufe alle Anwender auf.");
    
    List<UserDisplayDto> result = new ArrayList<>();
    for (User anwender : anwenderRepository.findAllByAdministratorFalseOrderByLoginAsc()) {
      result.add(mapper.map(anwender, UserDisplayDto.class));
    }
    return result;
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public List<UserDisplayDto> findeAdmins() {
    LOG.info("Rufe alle Admins auf.");

    // Das Mapping auf DTOs geht eleganter, ist dann aber schwer verständlich.
    List<UserDisplayDto> result = new ArrayList<>();
    for (User anwender : anwenderRepository.findByAdministrator(true)) {
      result.add(mapper.map(anwender, UserDisplayDto.class));
    }
    return result;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public UserDisplayDto getUserInfo(String login) {
    LOG.debug("Lese Daten für Anwender {}.", login);
    User anwender = anwenderRepository.getOne(login);
    return new UserDisplayDto(anwender.getLogin());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public void legeAn(String login, String password, boolean isAdministrator) {
    LOG.info("Erstelle Anwender {}.", login);
    LOG.debug("Erstelle Anwender mit isAdministrator {}.", isAdministrator);
    
    // Validierung von Login
    if (anwenderRepository.existsById(login)) {
      LOG.debug("Anwender {} exisitiert bereits und kann nicht angelegt werden.", login);
      throw new ValidationException("Der Benutzername ist bereits vergeben.");
    }
    
    if (login.length() < 1) {
      LOG.debug("Benutzername ist leer, Anwender kann nicht angelegt werden.");
      throw new ValidationException("Bitte gib einen Benutzernamen an.");
    }
    
    if (login.length() < 4) {
      LOG.debug("Benutzername {} ist zu kurz, Anwender kann nicht angelegt werden.", login);
      throw new ValidationException("Der Benutzername muss mindestens vier Zeichen lang sein.");
    }
    
    if (login.length() > 20) {
      LOG.debug("Benutzername {} ist zu lang, Anwender kann nicht angelegt werden.", login);
      throw new ValidationException("Der Benutzername darf maximal zwanzig Zeichen lang sein.");
    }
    
    for (char c : login.toCharArray()) {
      if (!Character.isLetter(c)) {
        LOG.debug("Benutzername {} ist nicht gültig, Anwender kann nicht angelegt werden.", login);
        throw new ValidationException("Der Benutzername darf nur aus Kleinbuchstaben bestehen.");
      }
    }
    
    if (!login.toLowerCase().equals(login)) {
      LOG.debug("Benutzername {} ist nicht gültig, Anwender kann nicht angelegt werden.", login);
      throw new ValidationException("Der Benutzername darf nur aus Kleinbuchstaben bestehen.");
    }
    
    // Validierung von Passwort
    if (password.length() < 1) {
      LOG.debug("Passwort ist leer, Anwender kann nicht angelegt werden.");
      throw new ValidationException("Bitte gib ein Passwort an.");
    }
    
    if (password.length() < 8) {
      LOG.debug("Passwort ist zu kurz, Anwender kann nicht angelegt werden.");
      throw new ValidationException("Das Passwort muss mindestens acht Zeichen lang sein.");
    }
    
    if (password.length() > 20) {
      LOG.debug("Passwort ist zu lang, Anwender kann nicht angelegt werden.");
      throw new ValidationException("Das Passwort darf maximal zwanzig Zeichen lang sein.");
    }
    
    if (password.contains(" ") || password.contains("\t")) {
      LOG.debug("Passwort ist nicht gültig, Anwender kann nicht angelegt werden.");
      throw new ValidationException("Das Passwort darf keine Leerzeichen oder Leerräume "
          + "enthalten.");
    }
    
    if (!password.matches(".*\\d.*")) {
      LOG.debug("Passwort ist nicht gültig, Anwender kann nicht angelegt werden.");
      throw new ValidationException("Das Passwort muss mindestens eine Zahl enthalten.");
    }
    
    if (!password.matches(".*[A-ZÄÖÜẞ].*")) {
      LOG.debug("Passwort ist nicht gültig, Anwender kann nicht angelegt werden.");
      throw new ValidationException("Das Passwort muss mindestens einen Großbuchstaben enthalten.");
    }
    
    if (!password.matches(".*[a-zäöüß].*")) {
      LOG.debug("Passwort ist nicht gültig, Anwender kann nicht angelegt werden.");
      throw new ValidationException("Das Passwort muss mindestens einen Kleinbuchstaben "
          + "enthalten.");
    }
    
    if (!password.matches(".*[#§$%&@€µ,.\\-;:_'´`\"!?°^+*/\\\\=~<>|()\\[\\]{}].*")) {
      LOG.debug("Passwort ist nicht gültig, Anwender kann nicht angelegt werden.");
      throw new ValidationException("Das Passwort muss mindestens ein Sonderzeichen enhalten.");
    }
    
    // Passwörter müssen Hashverfahren benennen.
    // Wir hashen nicht (noop), d.h. wir haben die
    // Passwörter im Klartext in der Datenbank (böse)
    User anwender = new User(login, "{noop}" + password, isAdministrator);
    anwenderRepository.save(anwender);
  }
}
