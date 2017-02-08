package org.perfrepo.web.service.reports.tablecomparison.model;

import java.util.ArrayList;
import java.util.List;

public class Comparison {

   private String name;
   private String description;
   private ChooseOption chooseOption = ChooseOption.SET_OF_TAGS;
   private SelectOption selectOption = SelectOption.LAST;
   private List<ComparisonItem> comparisonItems = new ArrayList<>();

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

   public enum ChooseOption {
      EXECUTION_ID, SET_OF_TAGS
   }

   public enum SelectOption {
      LAST, BEST, AVERAGE
   }
}
