package fr.insa_rennes.greensa.stats;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

import fr.insa_rennes.greensa.MainActivity;
import fr.insa_rennes.greensa.R;

public class graphStats extends Activity {

    private Dialog dialog;
    private Spinner listeClub, listeParcours, listeDate;
    private XYMultipleSeriesRenderer mRenderer;
    private XYSeriesRenderer renderer;
    private XYSeries mCurrentSeries;
    private XYMultipleSeriesDataset mDataset;
    private GraphicalView chartView;
    private View mChart;

    private String[] mMonth = new String[] {
            "Jan", "Feb" , "Mar", "Apr", "May", "Jun",
            "Jul", "Aug" , "Sep", "Oct", "Nov", "Dec"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_stats);

        ImageButton returnButton = (ImageButton)findViewById(R.id.returnButton);
        ImageButton generalStats = (ImageButton)findViewById(R.id.generalStats);
        ImageButton distance = (ImageButton)findViewById(R.id.distanceStats);
        ImageButton angle = (ImageButton)findViewById(R.id.angleStats);
        Button caract = (Button)findViewById(R.id.caracteristiques);

        returnButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(graphStats.this, MainActivity.class);
                startActivity(activity);
            }

        });

        generalStats.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity = new Intent(graphStats.this, Stats.class);
                startActivity(activity);
            }

        });

        // on crée la boite de dialogue pour select. les caracteristiques du graphique
        caract.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showDialog(1);
            }

        });

        distance.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showDistance();
            }

        });

        angle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showAngle();
            }

        });

        Intent intent = getIntent();
        ChoixStat cs = (ChoixStat) intent.getSerializableExtra("choix");
        if(cs == null)
            cs = ChoixStat.Distance;

        ChoixStat d = ChoixStat.Distance;
        ChoixStat a = ChoixStat.Angle;
        if(cs.ordinal() == d.ordinal())
            showDistance();
        else if(cs.ordinal() == a.ordinal())
            showAngle();
/*
        XYSeries series = new XYSeries("London Temperature hourly");
        int hour;
        for (hour=0;hour<24;hour++) {
            series.add(hour, (int)(Math.random()*30));
        }
        mCurrentSeries = series;

        // Now we create the renderer
        renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(Color.BLACK);
        renderer.setFillPoints(true);

        // Include low and max value
        renderer.setDisplayBoundingPoints(true);
        // we add point markers
        renderer.setPointStyle(PointStyle.CIRCLE);
        //renderer.setPointStrokeWidth(10);
        renderer.setDisplayChartValues(true);
        renderer.setChartValuesTextSize(30);
        renderer.setChartValuesSpacing(20); // ecart entre le marqueur et la valeur
        renderer.setLineWidth(5);

        mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);

        // We want to avoid black border
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        // Disable Pan on two axis
        mRenderer.setPanEnabled(true, false);
        mRenderer.setYAxisMax(30);
        mRenderer.setYAxisMin(0);
        mRenderer.setXAxisMin(0);
        mRenderer.setShowGrid(true); // we show the grid
        mRenderer.setPointSize(5); // epaisseur des points
        mRenderer.setGridColor(Color.BLACK);
        mRenderer.setXLabelsColor(Color.BLACK);
        mRenderer.setYLabelsColor(0, Color.BLACK);
        mRenderer.setAxesColor(Color.BLACK);
        mRenderer.setBackgroundColor(Color.WHITE);
        //mRenderer.setAxisTitleTextSize(20);
        mRenderer.setLabelsTextSize(30);
        mRenderer.setLegendTextSize(20);
        mRenderer.setZoomEnabled(true, false);

        mDataset = new XYMultipleSeriesDataset();
        mDataset.addSeries(mCurrentSeries);

        chartView = ChartFactory.getLineChartView(graphStats.this, mDataset, mRenderer);

        LinearLayout chartLyt = (LinearLayout)findViewById(R.id.graph);
        chartLyt.addView(chartView);*/
    }

    protected Dialog onCreateDialog(int id) {

        dialog=new Dialog(graphStats.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogstats);

        listeClub = (Spinner)dialog.findViewById(R.id.spinnerClub);
        listeParcours = (Spinner)dialog.findViewById(R.id.spinnerParcours);
        listeDate = (Spinner)dialog.findViewById(R.id.spinnerDate);

        List<String> clubs = new ArrayList<String>();
        clubs.add("Tous");
        clubs.add("Element 2");
        clubs.add("Element 3");

        List<String> courses = new ArrayList<String>();
        courses.add("Tous");
        courses.add("9 trous - St-Jacques-de-la-Lande");
        courses.add("18 trous - St-Jacques-de-la-Lande");

        List<String> dates = new ArrayList<String>();
        dates.add("Tout afficher");
        dates.add("Element 5");
        dates.add("Element 6");

        ArrayAdapter<String> adapterClubs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, clubs);
        ArrayAdapter<String> adapterCourses = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, courses);
        ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dates);

        //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
        adapterClubs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listeClub.setAdapter(adapterClubs);
        listeParcours.setAdapter(adapterCourses);
        listeDate.setAdapter(adapterDate);

        Button valider=(Button)dialog.findViewById(R.id.send);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updateGraphic();
                openChart();
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public void updateGraphic(){

        String clubChoisi = listeClub.getSelectedItem().toString();
        String parcoursChoisi = listeParcours.getSelectedItem().toString();
        String dateChoisi = listeDate.getSelectedItem().toString();

        XYSeries series = new XYSeries("London Temperature hourly");
        int hour;
        for (hour=0;hour<24;hour++) {
            series.add(hour, (int)(Math.random()*30));
        }

        //XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        mRenderer.setYAxisMax(100);
        mRenderer.setYAxisMin(0);
        mRenderer.setXAxisMin(0);

        mDataset.clear();
        mDataset.addSeries(series);
        mCurrentSeries = series;
/*
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);

        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        renderer.setDisplayChartValues(true);
        renderer.setDisplayChartValuesDistance(10);

        mRenderer = renderer;
*/
        //chartView = ChartFactory.getLineChartView(graphStats.this, dataset, mRenderer);
        chartView.repaint();
    }

    private void openChart(){
        String[] shortDistance = {"0-10", "10-15", "15-20", "20-25", "25-30", "30-35", "35-40", "40-45", "45-50", "50-55", "55-60"};
        String[] longDistance = {"50-60", "60-70", "70-80", "80-100", "100-120", "120-140", "140-160", "160-180", "180-200", ">200"};
        int[] x = { 0,1,2,3,4,5,6,7, 8, 9, 10, 11 };
        int[] income = { 2000,2500,2700,3000,2800,3500,3700,3800, 0,0,0};
        int[] expense = {2200, 2700, 2900, 2800, 2600, 3000, 3300, 3400, 0, 0, 0, 0 };

        // Creating an XYSeries for Income
        XYSeries shortSeries = new XYSeries("Distance");
        for(int i=0;i<shortDistance.length;i++){
            shortSeries.add(i,income[i]);
        }
        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        // Adding short Series to the dataset
        dataset.addSeries(shortSeries);

        // Creating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.CYAN); //color of the graph set to cyan
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(2);
        incomeRenderer.setDisplayChartValues(true);
        incomeRenderer.setDisplayChartValuesDistance(10); //setting chart value distance

        // Creating XYSeriesRenderer to customize expenseSeries
        /*XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
        expenseRenderer.setColor(Color.GREEN);
        expenseRenderer.setFillPoints(true);
        expenseRenderer.setLineWidth(2);
        expenseRenderer.setDisplayChartValues(true);*/

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle("Distance de tirs courts");
        multiRenderer.setXTitle("Distance");
        multiRenderer.setYTitle("Nombre de tirs");
        /***
         * Customizing graphs
         */
        //setting text size of the title
        multiRenderer.setChartTitleTextSize(30);
        //setting text size of the axis title
        multiRenderer.setAxisTitleTextSize(30);
        //setting text size of the graph lable
        multiRenderer.setLabelsTextSize(30);
        //setting zoom buttons visiblity
        multiRenderer.setZoomButtonsVisible(false);
        //setting pan enablity which uses graph to move on both axis
        multiRenderer.setPanEnabled(false, false);
        //setting click false on graph
        multiRenderer.setClickEnabled(false);
        //setting zoom to false on both axis
        multiRenderer.setZoomEnabled(false, false);
        //setting lines to display on y axis
        multiRenderer.setShowGridY(false);
        //setting lines to display on x axis
        multiRenderer.setShowGridX(false);
        //setting legend to fit the screen size
        multiRenderer.setFitLegend(true);
        //setting displaying line on grid
        multiRenderer.setShowGrid(false);
        //setting zoom to false
        multiRenderer.setZoomEnabled(false);
        //setting external zoom functions to false
        multiRenderer.setExternalZoomEnabled(false);
        //setting displaying lines on graph to be formatted(like using graphics)
        multiRenderer.setAntialiasing(true);
        //setting to in scroll to false
        multiRenderer.setInScroll(false);
        //setting to set legend height of the graph
        multiRenderer.setLegendHeight(30);
        //setting x axis label align
        multiRenderer.setXLabelsAlign(Paint.Align.CENTER);
        //setting y axis label to align
        multiRenderer.setYLabelsAlign(Paint.Align.LEFT);
        //setting text style
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
        //setting no of values to display in y axis
        multiRenderer.setYLabels(10);
        // setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.
        // if you use dynamic values then get the max y value and set here
        multiRenderer.setYAxisMax(4000);
        //setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMin(-0.5);
        //setting max values to be display in x axis
        multiRenderer.setXAxisMax(11);
        //setting bar size or space between two bars
        multiRenderer.setBarSpacing(0.5);
        //Setting background color of the graph to transparent
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
        //Setting margin color of the graph to transparent
        multiRenderer.setMarginsColor(getResources().getColor(R.color.transparent));
        multiRenderer.setApplyBackgroundColor(true);
        //setting the margin size for the graph in the order top, left, bottom, right
        multiRenderer.setMargins(new int[]{30, 30, 30, 30});
        for(int i=0; i< shortDistance.length;i++){
            multiRenderer.addXTextLabel(i, shortDistance[i]);
        }
        // Adding incomeRenderer and expenseRenderer to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same
        multiRenderer.addSeriesRenderer(incomeRenderer);
        //multiRenderer.addSeriesRenderer(expenseRenderer);
        //this part is used to display graph on the xml
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.graph);
        //remove any views before u paint the chart
        //chartContainer.removeAllViews();
        //drawing bar chart
        mChart = ChartFactory.getBarChartView(graphStats.this, dataset, multiRenderer, BarChart.Type.DEFAULT);
        //adding the view to the linearlayout
        chartContainer.addView(mChart);
        Button b = new Button(this);
        chartContainer.addView(b);
    }

    public void showDistance(){
        openChart();
    }

    public void showAngle(){
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.graph);
        //remove any views before u paint the chart
        chartContainer.removeAllViews();
    }
}
