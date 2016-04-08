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
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.insa_rennes.greensa.MainActivity;
import fr.insa_rennes.greensa.R;
import fr.insa_rennes.greensa.database.ClubsLoader;
import fr.insa_rennes.greensa.database.CoursesLoader;
import fr.insa_rennes.greensa.database.controller.ShotDAO;
import fr.insa_rennes.greensa.database.model.Club;
import fr.insa_rennes.greensa.database.model.Course;
import fr.insa_rennes.greensa.database.model.Shot;

public class graphStats extends Activity {

    private Dialog dialog;
    private Spinner listeClub, listeParcours, listeDate;
    private List<View> mChart;

    private ImageView leftArrow = null;
    private ImageView rightArrow = null;
    private TextView heading_text = null;
    private ImageView generalStats = null;
    private ImageView distance = null;
    private ImageView angle = null;

    private int stat; // prend la valeur DISTANCE ou ANGLE
    private int currentChart; // id du graphique affiché

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private static final String CLUB_DEFAULT = "Tous";
    private static final String COURSE_DEFAULT = "Tous";
    private static final String DATE_DEFAULT = "Tout afficher";
    private static final String DATE_TODAY = "Aujourd'hui";
    private static final String DATE_WEEK = "Cette semaine";
    private static final String DATE_MONTH = "Ce mois-ci";
    private static final String DATE_YEAR = "Cette année";

    public static final int DISTANCE = 0;
    public static final int ANGLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_stats);

        // Utile pour gérer le scrolling horizontal des graphiques
        final GestureDetector gdt = new GestureDetector(new GestureListener());

        ImageView homeButton = (ImageView)findViewById(R.id.homeButton);
        Button caract = (Button)findViewById(R.id.caracteristiques);

        generalStats = (ImageView)findViewById(R.id.generalStats);
        generalStats.setAlpha(0.5f);
        distance = (ImageView)findViewById(R.id.distanceStats);
        angle = (ImageView)findViewById(R.id.angleStats);

        leftArrow = (ImageView)findViewById(R.id.left_arrow);
        rightArrow = (ImageView)findViewById(R.id.right_arrow);

        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.graph);
        heading_text = (TextView)findViewById(R.id.heading_text);

        homeButton.setOnClickListener(new View.OnClickListener() {

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
            if(mChart.size() > 1) // on affiche la fleche droite que si nb graph > 1
                rightArrow.setVisibility(View.VISIBLE);
            else
                rightArrow.setVisibility(View.INVISIBLE);
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

        if(listeClub == null || listeParcours == null || listeDate == null) {
            listeClub = (Spinner) dialog.findViewById(R.id.spinnerClub);
            listeParcours = (Spinner) dialog.findViewById(R.id.spinnerParcours);
            listeDate = (Spinner) dialog.findViewById(R.id.spinnerDate);
        }

        List<String> clubs = new ArrayList<String>();
        clubs.add(CLUB_DEFAULT);
        for(Club clubTmp : ClubsLoader.getClubs()){
            clubs.add(clubTmp.getName());
        }

        List<String> courses = new ArrayList<String>();
        courses.add(COURSE_DEFAULT);
        for(Course courseTmp : CoursesLoader.getCourses()){
            courses.add(courseTmp.getName());
        }

        List<String> dates = new ArrayList<String>();
        dates.add(DATE_DEFAULT);
        dates.add(DATE_TODAY);
        //dates.add(DATE_WEEK);
        dates.add(DATE_MONTH);
        dates.add(DATE_YEAR);

        //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
        ArrayAdapter<String> adapterClubs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, clubs);
        ArrayAdapter<String> adapterCourses = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, courses);
        ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dates);

        listeClub.setAdapter(adapterClubs);
        listeParcours.setAdapter(adapterCourses);
        listeDate.setAdapter(adapterDate);

        Button valider = (Button)dialog.findViewById(R.id.send);
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
    // sans changer le numero du graphique courant
    public void updateGraphicDatas(String club, String course, String date){

        List<Shot> list = null;
        switch(stat){
            case DISTANCE:
                if(listeClub != null)
                    list = loadShotsFromQuery((String) listeClub.getSelectedItem(), (String) listeParcours.getSelectedItem(), (String) listeDate.getSelectedItem());
                else
                    list = loadShotsFromQuery(CLUB_DEFAULT, COURSE_DEFAULT, DATE_DEFAULT);
                loadDistance(list);
                break;
            case ANGLE:
                if(listeClub != null)
                    list = loadShotsFromQuery((String) listeClub.getSelectedItem(), (String) listeParcours.getSelectedItem(), (String) listeDate.getSelectedItem());
                else
                    list = loadShotsFromQuery(CLUB_DEFAULT, COURSE_DEFAULT, DATE_DEFAULT);
                loadAngle(list);
                break;
        }
        drawGraphic(currentChart);

        int nb = list.size();
        // On affiche un message pour indiqué le nb de tirs enregistrés suivant les nouveaux critères
        if(nb > 1)
            Toast.makeText(getApplicationContext(), Integer.toString(nb)+" tirs enregistrés", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), Integer.toString(nb)+" tir enregistré", Toast.LENGTH_LONG).show();
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
        heading_text.setText("Statistiques - Distance");
        distance.setAlpha(1.0f);
        angle.setAlpha(Stats.ALPHA_LEVEL);
        stat = DISTANCE;

        // On recupère les tirs suivant les caractéristiques
        List<Shot> list = null;
        if(listeClub != null)
            list = loadShotsFromQuery((String) listeClub.getSelectedItem(), (String) listeParcours.getSelectedItem(), (String) listeDate.getSelectedItem());
        else
            list = loadShotsFromQuery(CLUB_DEFAULT, COURSE_DEFAULT, DATE_DEFAULT);

        loadDistance(list);
        currentChart = 0;
        drawGraphic(currentChart);
    }

    public void showAngle(){
        heading_text.setText("Statistiques - Angle");
        angle.setAlpha(1.0f);
        distance.setAlpha(Stats.ALPHA_LEVEL);
        stat = ANGLE;

        // On recupère les tirs suivant les caractéristiques
        List<Shot> list = null;
        if(listeClub != null)
            list = loadShotsFromQuery((String) listeClub.getSelectedItem(), (String) listeParcours.getSelectedItem(), (String) listeDate.getSelectedItem());
        else
            list = loadShotsFromQuery(CLUB_DEFAULT, COURSE_DEFAULT, DATE_DEFAULT);

        loadAngle(list);
        currentChart = 0;
        drawGraphic(currentChart);
    }

    public List<Shot> loadShotsFromQuery(String club, String course, String date){
        int tab_size = 0;
        int id_course = -1;
        String where = "";
        String where_course = "";

        for(Course cTmp : CoursesLoader.getCourses()){
            if(cTmp.getName().equals(course)) {
                id_course = cTmp.getId();
                where_course = "id_course=?";
                where = " WHERE ";
                tab_size++;
                break;
            }
        }

        int id_club = -1;
        String where_club = "";
        for(Club cTmp : ClubsLoader.getClubs()){
            if(cTmp.getName().equals(club)) {
                id_club = cTmp.getId();
                where_club = "id_club=?";
                where = " WHERE ";
                tab_size++;
                break;
            }
        }

        //if(id_club != -1 && id_course != -1)
        String[] tab = null;
        if(tab_size > 0) {
            tab = new String[tab_size];
            if (id_course != -1) {
                tab[0] = Integer.toString(id_course);

                if (id_club != -1) {
                    tab[1] = Integer.toString(id_club);
                    // on ajoute un AND, on a une condition sur course et club
                    where_course = where_course + " AND ";
                }
            } else if (id_club != -1) // pas condition sur course mais sur club
                tab[0] = Integer.toString(id_club);
        }


        String like_clause_date = "";
        SimpleDateFormat formater = null;
        Date aujourdhui = new Date(); // on récupère la date
        // on modifie la requete sql suivant la date que l'on veut
        if(date.equals(DATE_TODAY)){
            formater = new SimpleDateFormat("dd-MM-yyy");
            like_clause_date = "date LIKE '"+formater.format(aujourdhui)+"'";
        }
        else if(date.equals(DATE_MONTH)){
            formater = new SimpleDateFormat("MM-yyyy");
            like_clause_date = "date LIKE '%"+formater.format(aujourdhui)+"'";
        }
        else if(date.equals(DATE_YEAR)){
            formater = new SimpleDateFormat("yyyy");
            like_clause_date = "date LIKE '%"+formater.format(aujourdhui)+"'";
        }

        // on a une condition sur date
        if(!like_clause_date.equals("")) {
            where = " WHERE ";
            if(id_club != -1) // on a une condition sur club
                where_club = where_club + " AND ";
            else if(id_course != -1) // pas de condition sur club mais on a une condition sur course
                where_course = where_course+" AND ";
        }


        List<Shot> listShots = null;
        // On recupère les tirs suivant les infos
        ShotDAO sdao = new ShotDAO(this);
        sdao.open();
        listShots = sdao.selectElements( "SELECT * FROM Shot" + where + where_course + where_club + like_clause_date, tab);
        sdao.close();

        return listShots;
    }

    public void loadDistance(List<Shot> listShot){
        String[] shortDistance = {"5", "10", "15", "20", "25", "30", "35", "40", "45", "50"};
        String[] longDistance = {"60", "80", "100", "120", "140", "160", "180", "200", ">200"};
        int[] shortS = new int[shortDistance.length];
        int[] longS = new int[longDistance.length];

        for(Shot shot : listShot){
            if(shot.getDistance() <= 50){
                // On calcule l'id où on va remplir le tableau
                // id=0 pour dist=[0;5]
                // id=1 pour dist=]5;10] etc..
                int i = (int)(Math.max(0, shot.getDistance() - 0.001)/5);
                shortS[i]++;
            }
            else if(shot.getDistance() <= 200){
                // Idem mais intervalles : ]50;60], ]60;80], ]80;100] ...
                int i = (int)((shot.getDistance() - 0.001)/20) - 2;
                longS[i]++;
            }
            else{
                // sup à 200
                longS[longS.length-1]++;
            }
        }

        // Creating an XYSeries for short shots
        int maxShort=0;
        int maxLong=0;
        XYSeries shortSeries = new XYSeries("Distance");
        for(int i=0;i<shortDistance.length;i++) {
            if(shortS[i] > maxShort)
                maxShort = shortS[i];
            shortSeries.add(i, shortS[i]);
        }
        // Creating an XYSeries for long shots
        XYSeries longSeries = new XYSeries("Distance");
        for(int i=0;i<longDistance.length;i++){
            if(longS[i] > maxLong)
                maxLong = longS[i];
            longSeries.add(i, longS[i]);
        }

        // On fait en sorte que la valeur max en Y soit un multiple de 10
        maxShort = (maxShort/10 + 1) * 10;
        maxLong = (maxLong/10 + 1) * 10;

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset datasetShort = new XYMultipleSeriesDataset();
        datasetShort.addSeries(shortSeries);

        XYMultipleSeriesDataset datasetLong = new XYMultipleSeriesDataset();
        datasetLong.addSeries(longSeries);

        // Creating XYSeriesRenderer to customize the first chart (shortShot)
        XYSeriesRenderer shortSRenderer = new XYSeriesRenderer();
        shortSRenderer.setColor(R.color.chartBar); //color of the graph set to cyan
        shortSRenderer.setFillPoints(true);
        shortSRenderer.setLineWidth(5);
        shortSRenderer.setDisplayChartValues(true);
        shortSRenderer.setDisplayChartValuesDistance(10); //setting chart value distance
        shortSRenderer.setChartValuesTextSize(35);
        shortSRenderer.setShowLegendItem(false); // hide lengend

        // Creating XYSeriesRenderer to customize the second chart (longShot)
        XYSeriesRenderer longSRenderer = new XYSeriesRenderer();
        longSRenderer.setColor(R.color.chartBar); //color of the graph set to cyan
        longSRenderer.setFillPoints(true);
        longSRenderer.setLineWidth(5);
        longSRenderer.setDisplayChartValues(true);
        longSRenderer.setDisplayChartValuesDistance(10); //setting chart value distance
        longSRenderer.setChartValuesTextSize(35);
        longSRenderer.setShowLegendItem(false); // hide lengend

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle("Distance de tirs courts");
        multiRenderer.setXTitle("Distance en mètre");
        multiRenderer.setYTitle("Nombre de tirs");
        //multiRenderer.
        /***
         * Customizing graphs
         */
        multiRenderer.setXLabelsColor(Color.BLACK);
        multiRenderer.setYLabelsColor(0, Color.BLACK);
        //setting text size and color of the title
        multiRenderer.setChartTitleTextSize(50);
        multiRenderer.setLabelsColor(Color.BLACK);
        //setting text size of the axis title
        multiRenderer.setAxisTitleTextSize(30);
        //setting text size of the graph lable
        multiRenderer.setLabelsTextSize(35);
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
        // setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to maxShort.
        // if you use dynamic values then get the max y value and set here
        multiRenderer.setYAxisMax(maxShort);
        multiRenderer.setYAxisMin(0);
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
        multiRenderer.setMargins(new int[]{30, 30, 60, 0});
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
        multiRendererLong.setXTitle("Distance en mètre");
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
        multiRendererLong.setLabelsTextSize(35);
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
        // setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to maxLong.
        // if you use dynamic values then get the max y value and set here
        multiRendererLong.setYAxisMax(maxLong);
        multiRendererLong.setYAxisMin(0);
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
        multiRendererLong.setMargins(new int[]{30, 30, 60, 0});
        for(int i=0; i< longDistance.length;i++){
            multiRendererLong.addXTextLabel(i, longDistance[i]);
        }

        multiRendererLong.addSeriesRenderer(longSRenderer);


        mChart.clear(); // on vide la liste de graphiques

        mChart.add( ChartFactory.getBarChartView(graphStats.this, datasetShort, multiRenderer, BarChart.Type.DEFAULT) );
        mChart.add( ChartFactory.getBarChartView(graphStats.this, datasetLong, multiRendererLong, BarChart.Type.DEFAULT) );
    }

    public void loadAngle(List<Shot> listShot){
        String[] tabAngles = {"- 60-45", "- 45-30", "- 30-15", "- 15-0", "0-15", "15-30", "30-45", "45-60"};
        int[] angleS = new int[tabAngles.length];

        for(Shot shot : listShot){
            if(shot.getAngle() <= 60){
                // On calcule l'id où on va remplir le tableau
                // id=0 pour angle=[-60;-45]
                // id=1 pour dist=]-45;-30] etc..
                int i = (int)(Math.max(0, (shot.getAngle() + 60) - 0.001)/15);
                angleS[i]++;
            }

        }

        // Creating an XYSeries for angles
        int maxAngle=0;
        XYSeries shortSeries = new XYSeries("Angle");
        for(int i=0;i<tabAngles.length;i++) {
            if(angleS[i] > maxAngle)
                maxAngle = angleS[i];
            shortSeries.add(i, angleS[i]);
        }

        // On fait en sorte que la valeur max en Y soit un multiple de 10
        maxAngle = (maxAngle/10 + 1) * 10;

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset datasetAngle = new XYMultipleSeriesDataset();
        datasetAngle.addSeries(shortSeries);

        // Creating XYSeriesRenderer to customize the chart
        XYSeriesRenderer angleSRenderer = new XYSeriesRenderer();
        angleSRenderer.setColor(R.color.chartBar); //color of the graph set to cyan
        angleSRenderer.setFillPoints(true);
        angleSRenderer.setLineWidth(5);
        angleSRenderer.setDisplayChartValues(true);
        angleSRenderer.setDisplayChartValuesDistance(10); //setting chart value distance
        angleSRenderer.setChartValuesTextSize(35);
        angleSRenderer.setShowLegendItem(false); // hide lengend

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle("Angle de déviation");
        multiRenderer.setXTitle("Angle en degré");
        multiRenderer.setYTitle("Nombre de tirs");
        //multiRenderer.
        /***
         * Customizing graphs
         */
        multiRenderer.setXLabelsColor(Color.BLACK);
        multiRenderer.setYLabelsColor(0, Color.BLACK);
        //setting text size and color of the title
        multiRenderer.setChartTitleTextSize(50);
        multiRenderer.setLabelsColor(Color.BLACK);
        //setting text size of the axis title
        multiRenderer.setAxisTitleTextSize(30);
        //setting text size of the graph lable
        multiRenderer.setLabelsTextSize(35);
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
        // setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to maxShort.
        // if you use dynamic values then get the max y value and set here
        multiRenderer.setYAxisMax(maxAngle);
        multiRenderer.setYAxisMin(0);
        //setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMin(-0.5);
        //setting max values to be display in x axis
        multiRenderer.setXAxisMax(tabAngles.length);
        //setting bar size or space between two bars
        multiRenderer.setBarSpacing(0.5);
        //Setting background color of the graph to transparent
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
        //Setting margin color of the graph to transparent
        multiRenderer.setMarginsColor(getResources().getColor(R.color.transparent));
        multiRenderer.setApplyBackgroundColor(true);
        //setting the margin size for the graph in the order top, left, bottom, right
        multiRenderer.setMargins(new int[]{30, 30, 60, 0});
        for(int i=0; i< tabAngles.length;i++){
            multiRenderer.addXTextLabel(i, tabAngles[i]);
        }

        multiRenderer.addSeriesRenderer(angleSRenderer);


        mChart.clear(); // on vide la liste de graphiques

        mChart.add( ChartFactory.getBarChartView(graphStats.this, datasetAngle, multiRenderer, BarChart.Type.DEFAULT) );
    }

}
