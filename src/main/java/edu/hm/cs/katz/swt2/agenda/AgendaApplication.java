package edu.hm.cs.katz.swt2.agenda;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

/**
 * Spring-Boot-Application. Spring-Boot-Anwendungen starten eine Rahmenanwendung, die selbständig
 * nach Programmteilen sucht und diese miteinander verbindet bzw. diese für Anfragen aus dem Web
 * konfiguriert. Daher findet sich in der {@link #main(String[])}-Methode nicht mehr als ein Aufruf
 * einer {@link SpringApplication}.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@SpringBootApplication
public class AgendaApplication {

  public static void main(String[] args) {
    SpringApplication.run(AgendaApplication.class, args);
  }
  
  /**
   * Aktivierung von SSL Traffic und Hinzufügen von HTTP zu HTTPS Umleitung.
   */
  @Bean
  public ServletWebServerFactory servletContainer() {
    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
      @Override
      protected void postProcessContext(Context context) {
        SecurityConstraint securityConstraint = new SecurityConstraint();
        securityConstraint.setUserConstraint("CONFIDENTIAL");
        SecurityCollection collection = new SecurityCollection();
        collection.addPattern("/*");
        securityConstraint.addCollection(collection);
        context.addConstraint(securityConstraint);
      }
    };
    tomcat.addAdditionalTomcatConnectors(getHttpConnector());
    return tomcat;
  }

  /**
   * Umleitung von Anfragen auf Port 8080 zu Port 8443.
   */
  private Connector getHttpConnector() {
    Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
    connector.setScheme("http");
    connector.setPort(8080);
    connector.setSecure(false);
    connector.setRedirectPort(8443);
    return connector;
  }
}
