package edu.hm.cs.katz.swt2.agenda.initialization;

import edu.hm.cs.katz.swt2.agenda.common.SecurityHelper;
import edu.hm.cs.katz.swt2.agenda.service.TaskService;
import edu.hm.cs.katz.swt2.agenda.service.TopicService;
import edu.hm.cs.katz.swt2.agenda.service.UserService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Initialisierung von Demo-Daten. Diese Komponente erstellt beim Systemstart Anwender, Topics,
 * Abonnements usw., damit man die Anwendung mit allen Features vorführen kann.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Component
@Profile("demo")
public class DemoData {

  private static final String LOGIN_FINE = "fine";

  private static final String LOGIN_ERNIE = "ernie";

  private static final String LOGIN_BERT = "bert";

  private static final Logger LOG = LoggerFactory.getLogger(DemoData.class);

  @Autowired
  UserService anwenderService;

  @Autowired
  TopicService topicService;

  @Autowired
  TaskService taskService;

  /**
   * Erstellt die Demo-Daten.
   */
  @PostConstruct
  @SuppressWarnings("unused")
  public void addData() {
    SecurityHelper.escalate(); // admin rights
    LOG.debug("Erzeuge Demo-Daten.");

    anwenderService.legeAn(LOGIN_FINE, "Fine", "#Fine2020", false);
    anwenderService.legeAn(LOGIN_ERNIE, "Ernie", "#Ernie2020", false);
    anwenderService.legeAn(LOGIN_BERT, "Bert", "#Bert2020", false);

    String htmlKursUuid = topicService.createTopic("HTML für Anfänger",
        "Dieser Kurs behandelt die Grundlagen von HTML.",
        "Die Hypertext Markup Language ist eine textbasierte Auszeichnungssprache zur"
        + " Strukturierung elektronischer Dokumente wie Texte mit Hyperlinks,"
        + " Bildern und anderen Inhalten.", LOGIN_FINE);
    topicService.subscribe(htmlKursUuid, LOGIN_ERNIE);
    topicService.subscribe(htmlKursUuid, LOGIN_BERT);
    Long linkErstellenTask = taskService.createTask(htmlKursUuid, "Link erstellen", LOGIN_FINE);
    taskService.checkTask(linkErstellenTask, LOGIN_ERNIE);
    taskService.createTask(htmlKursUuid, "Leeres HTML-Template erstellen", LOGIN_FINE);
       
    String cssKursUuid = topicService.createTopic("CSS für Fortgeschrittene",
        "Dieser Kurs richtet sich an Fortgeschrittene und behandelt CSS.",
        "Cascading Style Sheets, kurz CSS genannt, ist eine Stylesheet-Sprache für"
        + " elektronische Dokumente und zusammen mit HTML und DOM eine der Kernsprachen"
        + " des World Wide Webs.", LOGIN_FINE);
    String erniesKursUuid = topicService.createTopic("Ernies Backkurs",
        "Lernen Sie Backen mit Ernie.",
        "Hier lernen Sie innerhalb kürzester Zeit das Backen wie ein Konditor."
        + " Wir werden uns gemeinsam viele verschiedene Rezepte anschauen.", LOGIN_ERNIE);
    taskService.createTask(erniesKursUuid, "Googlehupf backen", LOGIN_ERNIE);
    Long affenMuffinTask =
        taskService.createTask(erniesKursUuid, "Affenmuffins backen", LOGIN_ERNIE);
    topicService.subscribe(erniesKursUuid, LOGIN_BERT);
    taskService.checkTask(affenMuffinTask, LOGIN_BERT);
  }

}
