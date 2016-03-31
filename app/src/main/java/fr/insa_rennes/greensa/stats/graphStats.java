package fr.insa_rennes.greensa.stats;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private List<View> mChart;

    private ImageView leftArrow = null;
    private ImageView rightArrow = null;

    private int stat; // prend la valeur DISTANCE ou ANGLE
    private int currentChart; // id du graphique affiché

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    public static final int DISTANCE = 0;
    public static final int ANGLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_stats);

        // Utile pour gérer le scrolling horizontal des graphiques
        final GestureDetector gdt = new GestureDetector(new GestureListener());

        ImageButton returnButton = (ImageButton)findViewById(R.id.returnButton);
        ImageButton generalStats = (ImageButton)findViewById(R.id.generalStats);
        ImageButton distance = (ImageButton)findViewById(R.id.distanceStats);
        ImageButton angle = (ImageButton)findViewById(R.id.angleStats);
        Button caract = (Button)findViewById(R.id.caracteristiques);

        leftArrow = (ImageView)findViewById(R.id.left_arrow);
        rightArrow = (ImageView)findViewById(R.id.right_arrow);

        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.graph);

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

        // On touche le conteneur puis on regardera si on scroll (pour changer de graphique)
        chartContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
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

        mChart = new ArrayList<View>();

        // On recupère le choix des statistiques à observer
        Intent intent = getIntent();
        stat = intent.getIntExtra("choix", DISTANCE); // DISTANCE par défaut

        switch(stat){
            case DISTANCE:
                showDistance();
                break;
            case ANGLE:
                showAngle();
                break;
        }

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

        chartView = ChartFactory.getLineChartView(GraphStats.this, mDataset, mRenderer);

        LinearLayout chartLyt = (LinearLayout)findViewById(R.id.graph);
        chartLyt.addView(chartView);*/
    }

    /* On récupère les mouvements des doigts
       Ici la classe sert à verifier si on
       veut scroll pour changer de graphique
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(currentChart < mChart.size()-1) {
                    currentChart++;
                    drawGraphic(currentChart);
                    updateArrowsVisibility();
                    return true; // Right to left
                }
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(currentChart > 0) {
                    currentChart--;
                    drawGraphic(currentChart);
                    updateArrowsVisibility();
                    return true; // Left to right
                }
            }
            return false;
        }
    }

    private void updateArrowsVisibility(){
        if(currentChart == 0){
            leftArrow.setVisibility(View.INVISIBLE);
            rightArrow.setVisibility(View.VISIBLE);
        }
        else if(currentChart == mChart.size()-1){
            rightArrow.setVisibility(View.INVISIBLE);
            leftArrow.setVisibility(View.VISIBLE);
        }
        else{
            leftArrow.setVisibility(View.VISIBLE);
            rightArrow.setVisibility(View.VISIBLE);
        }
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
                // on appelle la méthode pour modifier les graphiques
                updateGraphicDatas(listeClub.getSelectedItem().toString(), listeParcours.getSelectedItem().toString(), listeDate.getSelectedItem().toString());
                // on cache la fenetre
                dialog.dismiss();
            }
        });

        return dialog;
    }

    // Modifie les graphiques suivant les nouvelles valeurs
    public void updateGraphicDatas(String club, String course, String date){

        switch(stat){
            case DISTANCE:
                loadDistance(club, course, date);
                break;
            case ANGLE:
                drawAngle(club, course, date);
                break;
        }
        drawGraphic(currentChart);
    }

    public void drawGraphic(int index){
        if(index >= 0 && index < mChart.size()) {
            LinearLayout chartContainer = (LinearLayout) findViewById(R.id.graph);
            chartContainer.removeAllViews(); // on supprime l'affichage de l'ancien graphique

            updateArrowsVisibility(); // on met à jour la visibilité des fleches
            chartContainer.addView(mChart.get(index));
        }
    }

    public void showDistance(){
        loadDistance("","","");
        currentChart = 0;
        drawGraphic(currentChart);
    }

    public void showAngle(){
/*
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.graph);
        //remove any views before u paint the chart
        chartContainer.removeAllViews();*/
    }

    public void loadDistance(String club, String course, String date){
        String[] shortDistance = {"0", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};
        String[] longDistance = {"60", "70", "80", "100", "120", "140", "160", "180", ">200"};
        int[] shortS = new int[shortDistance.length];
        int[] longS = new int[longDistance.length];

        // Creating an XYSeries for short shots
        XYSeries shortSeries = new XYSeries("Distance");
        for(int i=0;i<shortDistance.length;i++) {
            shortS[i] = (int) (Math.random() * 4000);
            shortSeries.add(i, shortS[i]);
        }
        // Creating an XYSeries for long shots
        XYSeries longSeries = new XYSeries("Distance");
        for(int i=0;i<longDistance.length;i++){
            longS[i] = (int)(Math.random()*4000);
            longSeries.add(i, longS[i]);
        }

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset datasetShort = new XYMultipleSeriesDataset();
        datasetShort.addSeries(shortSeries);

        XYMultipleSeriesDataset datasetLong = new XYMultipleSeriesDataset();
        datasetLong.addSeries(longSeries);

        // Creating XYSeriesRenderer to customize the first chart (shortShot)
        XYSeriesRenderer shortSRenderer = new XYSeriesRenderer();
        shortSRenderer.setColor(Color.CYAN); //color of the graph set to cyan
        shortSRenderer.setFillPoints(true);
        shortSRenderer.setLineWidth(2);
        shortSRenderer.setDisplayChartValues(true);
        shortSRenderer.setDisplayChartValuesDistance(10); //setting chart value distance

        // Creating XYSeriesRenderer to customize the second chart (longShot)
        XYSeriesRenderer longSRenderer = new XYSeriesRenderer();
        longSRenderer.setColor(Color.RED); //color of the graph set to cyan
        longSRenderer.setFillPoints(true);
        longSRenderer.setLineWidth(2);
        longSRenderer.setDisplayChartValues(true);
        longSRenderer.setDisplayChartValuesDistance(10); //setting chart value distance

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle("Distance de tirs courts");
        multiRenderer.setXTitle("Distance");
        multiRenderer.setYTitle("Nombre de tirs");
        //multiRenderer.
        /***
         * Customizing graphs
         */
        multiRenderer.setXLabelsColor(Color.BLACK);
        multiRenderer.setYLabelsColor(0,Color.BLACK);
        //setting text size and color of the title
        multiRenderer.setChartTitleTextSize(50);
        multiRenderer.setLabelsColor(Color.BLACK);
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
        multiRenderer.setXAxisMax(shortDistance.length);
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

        multiRenderer.addSeriesRenderer(shortSRenderer);

        // LONG SHOTS //
        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRendererLong = new XYMultipleSeriesRenderer();
        multiRendererLong.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        multiRendererLong.setXLabels(0);
        multiRendererLong.setChartTitle("Distance de tirs longs");
        multiRendererLong.setXTitle("Distance");
        multiRendererLong.setYTitle("Nombre de tirs");
        //multiRenderer.
        /***
         * Customizing graphs
         */
        multiRendererLong.setXLabelsColor(Color.BLACK);
        multiRendererLong.setYLabelsColor(0,Color.BLACK);
        //setting text size and color of the title
        multiRendererLong.setChartTitleTextSize(50);
        multiRendererLong.setLabelsColor(Color.BLACK);
        //setting text size of the axis title
        multiRendererLong.setAxisTitleTextSize(30);
        //setting text size of the graph lable
        multiRendererLong.setLabelsTextSize(30);
        //setting zoom buttons visiblity
        multiRendererLong.setZoomButtonsVisible(false);
        //setting pan enablity which uses graph to move on both axis
        multiRendererLong.setPanEnabled(false, false);
        //setting click false on graph
        multiRendererLong.setClickEnabled(false);
        //setting zoom to false on both axis
        multiRendererLong.setZoomEnabled(false, false);
        //setting lines to display on y axis
        multiRendererLong.setShowGridY(false);
        //setting lines to display on x axis
        multiRendererLong.setShowGridX(false);
        //setting legend to fit the screen size
        multiRendererLong.setFitLegend(true);
        //setting displaying line on grid
        multiRendererLong.setShowGrid(false);
        //setting zoom to false
        multiRendererLong.setZoomEnabled(false);
        //setting external zoom functions to false
        multiRendererLong.setExternalZoomEnabled(false);
        //setting displaying lines on graph to be formatted(like using graphics)
        multiRendererLong.setAntialiasing(true);
        //setting to in scroll to false
        multiRendererLong.setInScroll(false);
        //setting to set legend height of the graph
        multiRendererLong.setLegendHeight(30);
        //setting x axis label align
        multiRendererLong.setXLabelsAlign(Paint.Align.CENTER);
        //setting y axis label to align
        multiRendererLong.setYLabelsAlign(Paint.Align.LEFT);
        //setting text style
        multiRendererLong.setTextTypeface("sans_serif", Typeface.NORMAL);
        //setting no of values to display in y axis
        multiRendererLong.setYLabels(10);
        // setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.
        // if you use dynamic values then get the max y value and set here
        multiRendererLong.setYAxisMax(4000);
        //setting used to move the graph on xaxiz to .5 to the right
        multiRendererLong.setXAxisMin(-0.5);
        //setting max values to be display in x axis
        multiRendererLong.setXAxisMax(longDistance.length);
        //setting bar size or space between two bars
        multiRendererLong.setBarSpacing(0.5);
        //Setting background color of the graph to transparent
        multiRendererLong.setBackgroundColor(Color.TRANSPARENT);
        //Setting margin color of the graph to transparent
        multiRendererLong.setMarginsColor(getResources().getColor(R.color.transparent));
        multiRendererLong.setApplyBackgroundColor(true);
        //setting the margin size for the graph in the order top, left, bottom, right
        multiRendererLong.setMargins(new int[]{30, 30, 30, 30});
        for(int i=0; i< longDistance.length;i++){
            multiRendererLong.addXTextLabel(i, longDistance[i]);
        }

        multiRendererLong.addSeriesRenderer(longSRenderer);


        mChart.clear();// on vide la liste de graphiques

        mChart.add( ChartFactory.getBarChartView(graphStats.this, datasetShort, multiRenderer, BarChart.Type.DEFAULT) );
        mChart.add( ChartFactory.getBarChartView(graphStats.this, datasetLong, multiRendererLong, BarChart.Type.DEFAULT) );
    }

    public void drawAngle(String club, String course, String date){

    }

}
