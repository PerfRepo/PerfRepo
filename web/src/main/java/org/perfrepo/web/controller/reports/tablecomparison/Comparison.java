package org.perfrepo.web.controller.reports.tablecomparison;

import java.util.ArrayList;
import java.util.List;

public class Comparison {

   private String name;
   private String description;
   private ChooseOption chooseOption;
   private SelectOption selectOption;
   private List<ComparisonItem> comparisonItems;

   private Table dataTable;

   public Comparison() {
      name = "New comparison";
      description = "";
      chooseOption = ChooseOption.EXECUTION_ID;
      selectOption = SelectOption.LAST;
      comparisonItems = new ArrayList<>();
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

   public ChooseOption getChooseOption() {
      return chooseOption;
   }

   public void setChooseOption(ChooseOption chooseOption) {
      this.chooseOption = chooseOption;
   }

   public SelectOption getSelectOption() {
      return selectOption;
   }

   public void setSelectOption(SelectOption selectOption) {
      this.selectOption = selectOption;
   }

   public List<ComparisonItem> getComparisonItems() {
      return comparisonItems;
   }

   public void setComparisonItems(List<ComparisonItem> comparisonItems) {
      this.comparisonItems = comparisonItems;
   }

   public void addComparisonItem(ComparisonItem comparisonItem) {
      comparisonItems.add(comparisonItem);
   }

   public void removeComparisonItem(ComparisonItem comparisonItem) {
      comparisonItems.remove(comparisonItem);
   }

   public Table getDataTable() {
      return dataTable;
   }

   public void setDataTable(Table dataTable) {
      this.dataTable = dataTable;
   }

   public void setBaseline(ComparisonItem comparisonItem) {
      getComparisonItems().stream().forEach(comparisonItem1 -> comparisonItem1.setBaseline(false));
      comparisonItem.setBaseline(true);
   }

   public enum ChooseOption {
      EXECUTION_ID, SET_OF_TAGS
   }

   public enum SelectOption {
      LAST, BEST, AVERAGE
   }
}
