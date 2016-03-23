package fr.insa_rennes.greensa;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
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
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

public class graphStats extends Activity {

    private Dialog dialog;
    private Spinner listeClub, listeParcours, listeDate;
    private XYMultipleSeriesRenderer mRenderer;
    private XYSeriesRenderer renderer;
    private XYSeries mCurrentSeries;
    private XYMultipleSeriesDataset mDataset;
    private GraphicalView chartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_stats);

        ImageButton returnButton = (ImageButton)findViewById(R.id.returnButton);
        ImageButton generalStats = (ImageButton)findViewById(R.id.generalStats);
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

        LinearLayout chartLyt = (LinearLayout)findViewById(R.id.chart);
        chartLyt.addView(chartView);
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
                updateGraphic();
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
}
