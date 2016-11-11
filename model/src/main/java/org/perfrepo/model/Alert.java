package org.perfrepo.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents alert condition defined on test
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@javax.persistence.Entity
@Table(name = "alert")
@NamedQueries({
    @NamedQuery(name = Alert.GET_BY_TEST_AND_METRIC, query = "SELECT distinct alert from Alert alert join alert.test test join alert.metric metric where test.id = :testId and metric.id = :metricId")
})
public class Alert implements Entity<Alert>, Comparable<Alert> {

   public static final String GET_BY_TEST_AND_METRIC = "Alert.getByTestAndMetric";

   @Id
   @SequenceGenerator(name = "ALERT_ID_GENERATOR", sequenceName = "ALERT_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ALERT_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   @NotNull(message = "{page.alert.nameRequired}")
   @Size(max = 500)
   private String name;

   @Column(name = "description")
   @Size(max = 2097)
   private String description;

   @Column(name = "condition")
   @NotNull(message = "{page.alert.conditionRequired}")
   @Size(max = 2097)
   private String condition;

   @Column(name = "links")
   private String links;

   @ManyToOne(optional = false)
   @JoinColumn(name = "metric_id", referencedColumnName = "id")
   @NotNull(message = "{page.alert.metricRequired}")
   private Metric metric;

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
       name = "alert_tag",
       joinColumns = {@JoinColumn(name = "alert_id", nullable = false, updatable = false)},
       inverseJoinColumns = {@JoinColumn(name = "tag_id", nullable = false, updatable = false)}
   )
   private Set<Tag> tags = new HashSet<>();

   @ManyToOne(optional = false)
   @JoinColumn(name = "test_id", referencedColumnName = "id")
   @NotNull
   private Test test;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getCondition() {
      return condition;
   }

   public void setCondition(String condition) {
      this.condition = condition;
   }

   public String getLinks() {
      return links;
   }

   public void setLinks(String links) {
      this.links = links;
   }

   public Test getTest() {
      return test;
   }

   public void setTest(Test test) {
      this.test = test;
   }

   public Metric getMetric() {
      return metric;
   }

   public void setMetric(Metric metric) {
      this.metric = metric;
   }

   public Set<Tag> getTags() {
      return tags;
   }

   public void setTags(Set<Tag> tags) {
      this.tags = tags;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Alert)) return false;

      Alert alert = (Alert) o;

      if (getName() != null ? !getName().equals(alert.getName()) : alert.getName() != null) return false;
      return getCondition() != null ? getCondition().equals(alert.getCondition()) : alert.getCondition() == null;
   }

   @Override
   public int hashCode() {
      int result = getName() != null ? getName().hashCode() : 0;
      result = 31 * result + (getCondition() != null ? getCondition().hashCode() : 0);
      return result;
   }

   @Override
   public int compareTo(Alert o) {
      return this.name.compareTo(o.name);
   }

   @Override
   public String toString() {
      return "Alert{" +
              "condition='" + condition + '\'' +
              ", id=" + id +
              ", links='" + links + '\'' +
              ", name='" + name + '\'' +
              '}';
   }
}
