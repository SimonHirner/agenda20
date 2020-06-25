package edu.hm.cs.katz.swt2.agenda.mvc;

import java.util.Calendar;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
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
public class ErrorController extends AbstractController
    implements org.springframework.boot.web.servlet.error.ErrorController {

  /**
   * Erstellt die Error Seite.
   */
  @GetMapping("/error")
  public String handleError(HttpServletRequest request, Model model, Authentication auth) {
    Object errorCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    Integer code = Integer.valueOf(errorCode.toString());
    Calendar calendar = Calendar.getInstance(request.getLocale());
    Date timestamp = calendar.getTime();

    model.addAttribute("status", errorCode);
    model.addAttribute("error", HttpStatus.valueOf(code));
    model.addAttribute("timestamp", timestamp);
    model.addAttribute("message", request.getAttribute("javax.servlet.error.exception"));
    model.addAttribute("path", request.getAttribute("javax.servlet.forward.request_uri"));

    return "error";
  }

  @Override
  public String getErrorPath() {
    return "/error";
  }
}