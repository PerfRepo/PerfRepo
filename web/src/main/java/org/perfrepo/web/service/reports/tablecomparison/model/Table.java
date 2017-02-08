package org.perfrepo.web.service.reports.tablecomparison.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: document this
 *
 * @author Jakub Markos (jmarkos@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class Table {

   private List<ComparisonItem> items = new ArrayList<>(); // table headers
   private List<Row> rows = new ArrayList<>();

   public List<ComparisonItem> getItems() {
      return items;
   }

   public void setItems(List<ComparisonItem> items) {
      this.items = items;
   }

   public List<Row> getRows() {
      return rows;
   }

   public void setRows(List<Row> rows) {
      this.rows = rows;
   }

   public static class Row {
      private String metricName;
      private List<Cell> cells = new ArrayList<>();

      public String getMetricName() {
         return metricName;
      }

      public void setMetricName(String metricName) {
         this.metricName = metricName;
      }

      public List<Cell> getCells() {
         return cells;
      }

      public void setCells(ArrayList<Cell> cells) {
         this.cells = cells;
      }

   }

   public static class Cell {
      private double value;
      private String diffAgainstBaseline;
      private boolean baseline;
      private CellStyle cellStyle = CellStyle.NEUTRAL;

      public double getValue() {
         return value;
      }

      public void setValue(double value) {
         this.value = value;
      }

      public String getDiffAgainstBaseline() {
         return diffAgainstBaseline;
      }

      public void setDiffAgainstBaseline(String diffAgainstBaseline) {
         this.diffAgainstBaseline = diffAgainstBaseline;
      }

      public boolean isBaseline() {
         return baseline;
      }

      public void setBaseline(boolean baseline) {
         this.baseline = baseline;
      }

      public CellStyle getCellStyle() {
         return cellStyle;
      }

      public void setCellStyle(CellStyle cellStyle) {
         this.cellStyle = cellStyle;
      }
   }

   public enum CellStyle {
      BAD,
      GOOD,
      NEUTRAL
   }

}
