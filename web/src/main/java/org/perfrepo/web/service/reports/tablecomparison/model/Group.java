package org.perfrepo.web.service.reports.tablecomparison.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: document this
 *
 * @author Jakub Markos (jmarkos@redhat.com)
 */
public class Group {

   private String name;
   private String description;
   private int threshold = 5;
   private List<Comparison> comparisons = new ArrayList<>();

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

   public int getThreshold() {
      return threshold;
   }

   public void setThreshold(int threshold) {
      this.threshold = threshold;
   }

   public List<Comparison> getComparisons() {
      return comparisons;
   }

   public void setComparisons(List<Comparison> comparisons) {
      this.comparisons = comparisons;
   }

}
