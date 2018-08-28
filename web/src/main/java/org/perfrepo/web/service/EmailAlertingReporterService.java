package org.perfrepo.web.service;

import org.perfrepo.model.Alert;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.user.User;
import org.perfrepo.web.dao.UserDAO;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link AlertingReporterService} that react to the failed alert by sending an email to every
 * subscribed user.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class EmailAlertingReporterService implements AlertingReporterService {

   @Inject
   private ApplicationConfiguration applicationConfiguration;

   @Inject
   private Mailer mailer;

   @Inject
   private UserDAO userDAO;

   private Map<Alert, Map<String, Object>> conditionVariables;

   @Override
   public void reportAlert(List<Alert> alerts, TestExecution testExecution) {
      if (alerts == null || alerts.isEmpty()) {
         return;
      }

      List<User> subscribers = userDAO.findSubscribersForTest(testExecution.getTest().getId());

      String message = composeMessage(alerts, testExecution);
      String subject = composeSubject(testExecution);
      for (User subscriber : subscribers) {
         try {
            mailer.sendEmail(subscriber.getEmail(), subject, message);
         } catch (MessagingException e) {
            e.printStackTrace();  // TODO: handle this properly
         }
      }
   }

   @Override
   public void setConditionVariables(Map<Alert, Map<String, Object>> variables) {
      this.conditionVariables = variables;
   }

   private String composeSubject(TestExecution testExecution) {
      return "PerfRepo - some alerts on test " + testExecution.getTest().getName() + " were triggered.";
   }

   private String composeMessage(List<Alert> alerts, TestExecution testExecution) {
      String urlPath = applicationConfiguration.getUrl();

      StringBuilder message = new StringBuilder();

      message.append("Hello,<br /><br />");
      message.append("after uploading results of test execution of test ");
      message.append("<a href=\"" + urlPath + "/test/" + testExecution.getTest().getId() + "\">" + testExecution.getTest().getName() + "</a>");
      message.append(" some of the alerts conditions failed.<br /><br />");
      message.append("Info about test execution: <br />");

      message.append("Test execution name: <a href=\"" + urlPath + "/exec/" + testExecution.getId() + "\">" + testExecution.getName() + "</a><br />");
      message.append("Execution date: " + testExecution.getStarted());

      message.append("<br /><br />");

      message.append("Alerts that failed: <br />");
      message.append("-----<br />");
      for (Alert alert : alerts) {
         message.append("Alert name: <a href=\"" + urlPath + "/alert/" + alert.getId() + "\">" + alert.getName() + "</a><br />");
         message.append("Description: " + getEvaluatedDescription(alert) + "<br />");
         if (alert.getLinks() != null && !alert.getLinks().isEmpty()) {
            message.append("Links: ");
            for (String link : alert.getLinks().split(" ")) {
               message.append("<a href=\"" + link + "\">" + link + "</a> ");
            }
            message.append("<br />");
         }
         message.append("-----<br />");
      }

      message.append("<br /><br />");

      message.append("Sincerely yours,<br />");
      message.append("PerfRepo Alerting");

      return message.toString();
   }

   /**
    * Helper method. It's possible to use various expressions in the alert's description such as ${expression} using
    * variables from the condition. This method parses the description and evaluates such expressions.
    *
    * @param alert
    * @return evaluated description
    */
   private String getEvaluatedDescription(Alert alert) {
      if (alert.getDescription() == null || alert.getDescription().isEmpty()) {
         return "";
      }
      
      ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

      //regex pattern for ${expression}
      Pattern pattern = Pattern.compile("\\$\\{([^}]*)\\}");
      Matcher matcher = pattern.matcher(alert.getDescription());
      StringBuffer evaluatedDescription = new StringBuffer();

      while (matcher.find()) {
         String expression = matcher.group(1);

         Object result;
         try {
            result = engine.eval(expression, new SimpleBindings(conditionVariables.get(alert)));
         } catch (ScriptException e) {
            throw new IllegalArgumentException("Error occurred while evaluating the description.", e);
         }

         matcher.appendReplacement(evaluatedDescription, result.toString());
      }

      matcher.appendTail(evaluatedDescription);

      return evaluatedDescription.toString();
   }
}
