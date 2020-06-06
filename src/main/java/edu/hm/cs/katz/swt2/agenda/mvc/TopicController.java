package edu.hm.cs.katz.swt2.agenda.mvc;

import edu.hm.cs.katz.swt2.agenda.common.StatusEnum;
import edu.hm.cs.katz.swt2.agenda.persistence.Registration;
import edu.hm.cs.katz.swt2.agenda.service.TaskService;
import edu.hm.cs.katz.swt2.agenda.service.TopicService;
import edu.hm.cs.katz.swt2.agenda.service.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.ValidationException;
import java.util.List;


/**
 * Controller-Klasse für alle Interaktionen, die die Anzeige und Verwaltung von Topics betrifft.
 * Controller reagieren auf Aufrufe von URLs. Sie benennen ein View-Template (Thymeleaf-Vorlage) und
 * stellen Daten zusammen, die darin dargestellt werden. Dafür verwenden Sie Methoden der
 * Service-Schicht.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Controller
public class TopicController extends AbstractController {

  @Autowired
  private TopicService topicService;

  @Autowired
  private TaskService taskService;

  /**
   * Erstellt die Übersicht über alle Topics des Anwenders, d.h. selbst erzeugte und abonnierte.
   */
  @GetMapping("/topics")
  public String getTopicListView(Model model, Authentication auth) {
    model.addAttribute("managedTopics", topicService.getManagedTopics(auth.getName()));
    model.addAttribute("topics", topicService.getSubscriptions(auth.getName()));
    return "topic-listview";
  }

  /**
   * Erstellt das Formular zum Erstellen eines Topics.
   */
  @GetMapping("/topics/create")
  public String getTopicCreationView(Model model, Authentication auth) {
    model.addAttribute("newTopic", new SubscriberTopicDto(null, null, "", "", ""));
    return "topic-creation";
  }

  /**
   * Nimmt den Formularinhalt vom Formular zum Erstellen eines Topics entgegen und legt einen
   * entsprechendes Topic an. Kommt es dabei zu einer Exception, wird das Erzeugungsformular wieder
   * angezeigt und eine Fehlermeldung eingeblendet. Andernfalls wird auf die Übersicht der Topics
   * weitergeleitet und das Anlegen in einer Einblendung bestätigt.
   */
  @PostMapping("/topics")
  public String handleTopicCreation(Model model, Authentication auth,
      @ModelAttribute("newTopic") SubscriberTopicDto topic, RedirectAttributes redirectAttributes) {
    try {
      topicService.createTopic(topic.getTitle(), topic.getShortDescription(),
          topic.getLongDescription(), auth.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics/create";
    }
    redirectAttributes.addFlashAttribute("success", "Topic " + topic.getTitle() + " angelegt.");
    return "redirect:/topics";
  }

  /**
   * Erzeugt Anzeige eines Topics mit Informationen für den Ersteller.
   */
  @GetMapping("/topics/{uuid}/manage")
  public String createTopicManagementView(Model model, Authentication auth,
      @PathVariable("uuid") String uuid) {
    OwnerTopicDto topic = topicService.getManagedTopic(uuid, auth.getName());
    model.addAttribute("topic", topic);
    model.addAttribute("tasks", taskService.getManagedTasks(uuid, auth.getName()));
    return "topic-management";
  }

  /**
   * Verarbeitet die Aktualisierung eines Topics.
   */
  @PostMapping("/topics/{uuid}/manage")
  public String handleUpdate(@ModelAttribute("topic") OwnerTopicDto topic, Authentication auth,
      @PathVariable("uuid") String uuid,
      @RequestHeader(value = "referer", required = true) String referer) {
    topicService.updateTopic(uuid, auth.getName(), topic.getShortDescription(),
        topic.getLongDescription());
    return "redirect:" + referer;
  }
  
  /**
   * Erzeugt Anzeige für die Nachfrage beim Abonnieren eines Topics.
   */
  @GetMapping("/topics/{uuid}/register")
  public String getTaskRegistrationView(Model model, Authentication auth,
      @PathVariable("uuid") String uuid) {
    SubscriberTopicDto topic = topicService.getTopic(uuid, auth.getName());
    model.addAttribute("topic", topic);
    return "topic-registration";
  }

  /**
   * Nimmt das Abonnement (d.h. die Bestätigung auf die Nachfrage) entgegen und erstellt ein
   * Abonnement.
   */
  @PostMapping("/topics/{uuid}/register")
  public String handleTaskRegistration(Model model, Authentication auth,
      @PathVariable("uuid") String uuid) {
    topicService.subscribe(uuid, auth.getName());
    return "redirect:/topics/" + uuid;
  }
  
  /**
   * Nimmt das Abonnement (d.h. die Bestätigung auf die Nachfrage) entgegen und erstellt ein
   * Abonnement.
   */
  @PostMapping("/topics/{uuid}/unsubscribe")
  public String handleTopicUnsubscription(Model model, Authentication auth,
                                       @PathVariable("uuid") String uuid, RedirectAttributes redirectAttributes) {

    try {
      taskService.resetAllTasks(uuid, auth.getName());
      topicService.unsubscribe(uuid, auth.getName());
    } catch (Exception e) {
  
    }
    return "redirect:/topics/";
  }
  
  /**
   * Erzeugt Anzeige eines Topics mit Informationen für den Ersteller.
   */
  @GetMapping("/topics/{uuid}/unsubscription")
  public String createUnsubscriptionView(Model model, Authentication auth,
                                          @PathVariable("uuid") String uuid) {
    OwnerTopicDto topic = topicService.getManagedTopic(uuid, auth.getName());
    model.addAttribute("topic", topic);
    model.addAttribute("tasks", taskService.getManagedTasks(uuid, auth.getName()));
    return "topic-unsubscription";
  }
  
  /**
   * Erstellt Übersicht eines Topics für einen Abonennten.
   */
  @GetMapping("/topics/{uuid}")
  public String createTopicView(Model model, Authentication auth,
      @PathVariable("uuid") String uuid) {
    SubscriberTopicDto topic = topicService.getTopic(uuid, auth.getName());
    List<SubscriberTaskDto> openTasks = taskService.getTasksForTopicForStatus(uuid, auth.getName(), StatusEnum.OFFEN);
    openTasks.addAll(taskService.getTasksForTopicForStatus(uuid, auth.getName(), StatusEnum.NEU));
    model.addAttribute("topic", topic);
    model.addAttribute("openTasks", openTasks);
    model.addAttribute("finishedTasks", taskService.getTasksForTopicForStatus(uuid, auth.getName(), StatusEnum.FERTIG));
    return "topic";
  }
  
  /**
   * Verarbeitet die Löschung eines Topics.
   */
  @PostMapping("/topics/{uuid}/delete")
  public String handleDeletion(Authentication auth, @PathVariable("uuid") String uuid,
      RedirectAttributes redirectAttributes) {
    OwnerTopicDto topic = topicService.getManagedTopic(uuid, auth.getName());
    
    try {
      topicService.deleteTopic(uuid, auth.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics";
    }
    redirectAttributes.addFlashAttribute("success", "Topic " + topic.getTitle() + " gelöscht.");
    return "redirect:/topics";
  }
  
  /**
   * Erstellt Übersicht über die Abonnenten eines Topics.
   */
  @GetMapping("/topics/{uuid}/subscribers")
  public String createSubscriberView(Model model, Authentication auth,
      @PathVariable("uuid") String uuid) {
    OwnerTopicDto topic = topicService.getManagedTopic(uuid, auth.getName());
    List<UserDisplayDto> subscribers = topic.getSubscribers();
    model.addAttribute("subscribers", subscribers);
    model.addAttribute("topic", topic);
    return "subscriber-listview";
    
  }
  
  /**
   * Verarbeitet die Markierung eines Tasks als "Done".
   */
  @PostMapping("topics/{uuid}/{id}/check")
  public String handleTaskChecking(Model model, Authentication auth, @PathVariable("id") Long id) {
    taskService.checkTask(id, auth.getName());
    return "redirect:/topics/{uuid}";
  }
  
  /**
   * Verarbeitet das Zurücksetzen des Status eines Tasks.
   */
  @PostMapping("topics/{uuid}/{id}/reset")
  public String handleTaskReset(Model model, Authentication auth, @PathVariable("id") Long id) {
    taskService.resetTask(id, auth.getName());
    return "redirect:/topics/{uuid}";
  }
}
