package org.perfrepo.web.controller.reports.tablecomparison;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer object for communication between table comparison report controller and table comparison report service. Contains all
 * information about a single group of comparisons. Multiple groups form the whole report.
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

   public void addComparison(Comparison comparison) {
      comparisons.add(comparison);
   }

   public void removeComparison(Comparison comparison) {
      comparisons.remove(comparison);
   }

}
