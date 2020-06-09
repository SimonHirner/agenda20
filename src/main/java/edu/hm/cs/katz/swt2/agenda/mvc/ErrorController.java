package edu.hm.cs.katz.swt2.agenda.mvc;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller-Klasse für die Error-Page.
 *
 * @author Fabian Rittmeier (mailto: rittmeie@hm.edu)
 */
@Controller
public class ErrorController extends AbstractController {
    
    
  /**
   * Erstellt die Error Seite.
   */
  @GetMapping("/error")
  public String getErrorView(Model model, Authentication auth) {
    return "error";
  }
}
