var previousAdded;
var previousHighlightedPointX;
var previousHighlightedPointSeries;
var previousHighlightedPointChart;

var highlightedPointsX;
var highlightedPointsSeries;
var highlightedPointsChart;
var highlightedPointsCounter;

metricHistoryClear();

function metricHistoryRefresh() {
    for (var i = 0; i < highlightedPointsCounter; i++) {
        var chart = RichFaces.component(highlightedPointsChart[i]);

        if (typeof(chart.highlight) === "function") {
            chart.highlight(highlightedPointsSeries[i], highlightedPointsX[i]);
        }
    }

    var chart = RichFaces.component(previousHighlightedPointChart);
    if (typeof(chart.highlight) === "function") {
        chart.highlight(previousHighlightedPointSeries, previousHighlightedPointX);
    }
}

function metricHistoryPointClick(e) {
    if (e.data.x < 0 || e.data.seriesIndex < 0 || e.data.seriesIndex == null) {
        return;
    }

    var currentChartName = e.currentTarget.id.substr(0, e.currentTarget.id.length - 5); // remove 'Chart' from the end
    var chart = RichFaces.component(currentChartName);

    if (!previousAdded && previousHighlightedPointChart != null) {
        var previousChart = RichFaces.component(previousHighlightedPointChart);
        if (typeof(previousChart.unhighlight) === "function") {
            previousChart.unhighlight(previousHighlightedPointSeries, previousHighlightedPointX);
        }
    }

    previousAdded = false;
    previousHighlightedPointX = e.data.x;
    previousHighlightedPointSeries = e.data.seriesIndex;
    previousHighlightedPointChart = currentChartName;

    //no need to check because if I clicked on something, it definitely exists :)
    chart.highlight(e.data.seriesIndex, e.data.x);
}

function metricHistoryPermanentlyHighlight() {
    previousAdded = true;

    highlightedPointsX[highlightedPointsCounter] = previousHighlightedPointX;
    highlightedPointsSeries[highlightedPointsCounter] = previousHighlightedPointSeries;
    highlightedPointsChart[highlightedPointsCounter] = previousHighlightedPointChart;

    highlightedPointsCounter++;
}

function metricHistoryClear() {
    previousAdded = false;
    previousHighlightedPointX = -1;
    previousHighlightedPointSeries = -1;
    previousHighlightedPointChart = null;

    highlightedPointsX = new Array();
    highlightedPointsSeries = new Array();
    highlightedPointsChart = new Array();
    highlightedPointsCounter = 0;
}

$(document).ready(function () {
    $("#clearCompare").click(metricHistoryClear());
});
