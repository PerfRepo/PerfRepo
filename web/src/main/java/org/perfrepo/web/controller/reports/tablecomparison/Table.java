package org.perfrepo.web.controller.reports.tablecomparison;

import java.util.ArrayList;

public class Table {

   private ArrayList<ComparisonItem> items = new ArrayList<>(); // table headers
   private ArrayList<Row> rows = new ArrayList<>();

   public ArrayList<ComparisonItem> getItems() {
      return items;
   }

   public void setItems(ArrayList<ComparisonItem> items) {
      this.items = items;
   }

   public void addItem(ComparisonItem item) {
      items.add(item);
   }

   public ArrayList<Row> getRows() {
      return rows;
   }

   public void setRows(ArrayList<Row> rows) {
      this.rows = rows;
   }

   public void addRow(Row row) {
      rows.add(row);
   }

   public static class Row {
      private String metricName;
      private ArrayList<Cell> cells;

      public String getMetricName() {
         return metricName;
      }

      public void setMetricName(String metricName) {
         this.metricName = metricName;
      }

      public ArrayList<Cell> getCells() {
         return cells;
      }

      public void setCells(ArrayList<Cell> cells) {
         this.cells = cells;
      }

      public void addCell(Cell cell) {
         if (cells == null) {
            cells = new ArrayList<>();
         }

         cells.add(cell);
      }
   }

   public static class Cell {
      private String value;
      private String diffAgainstBaseline;
      private boolean baseline;
      private CellStyle cellStyle;

      public String getValue() {
         return value;
      }

      public void setValue(String value) {
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
