package edu.hm.cs.katz.swt2.agenda.mvc;

import edu.hm.cs.katz.swt2.agenda.service.TopicService;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller-Klasse für alle Interaktionen mit der Suchfunktion.
 *
 * @author Fabian Rittmeier(mailto: f.rittmeier@hm.edu)
 */

@Controller
public class SearchController extends AbstractController {

    @Autowired
    private TopicService topicService;

    /**
     * Nimmt einen Registrierschlüssel für ein Topic entgegen oder
     * erstellt eine Liste mit Topics für einen eingegebenen Suchbegriff.
     */
    @PostMapping("/query")
    public String handleSearchQuery(Model model, Authentication auth, @ModelAttribute("search") Search search) {
        String s = search.getSearch();
        String uuid = "";
        try {
            uuid = topicService.getTopicUuid(s, auth.getName());
        } catch (ValidationException e) {
            model.addAttribute("publicQuery", topicService.getPublicTopics(auth.getName(), s));
            model.addAttribute("subscribedQuery", topicService.getSubscriptions(auth.getName(), s));
            return "result-listview";
        }
        return "redirect:/topics/" + uuid + "/register";
    }

}
