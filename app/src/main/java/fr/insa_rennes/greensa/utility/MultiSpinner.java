package fr.insa_rennes.greensa.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

/**
 * Cette classe cree un spinner a choix multiple dans une boite de dialogue
 */
public class MultiSpinner extends Spinner implements
        DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {

    /**
     * Liste des elements du spinner
     */
    private List<String> items;

    /**
     * Tableau indiquant si l'item i est selectionne
     */
    private boolean[] selected;

    /**
     * Texte affiche dans le spinner par defaut
     */
    private String defaultText;

    private MultiSpinnerListener listener;

    /**
     * Constructeur de MultiSpinner
     * @param context Contexte actuel
     */
    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    /**
     * Methode appelee lorsqu'un element de la liste est modifie
     * Cette methode est appelee tout seule
     *
     * @param dialog Boite de dialogue
     * @param which Element modifie
     * @param isChecked Valeur de la modification
     */
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (isChecked)
            selected[which] = true;
        else
            selected[which] = false;
    }

    /**
     * Methode appelee lorsqu'on ferme la boite de dialogue
     *
     * @param dialog Boite de dialogue
     */
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[] { defaultText });
        setAdapter(adapter);
        listener.onItemsSelected(selected);
    }

    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(
                items.toArray(new CharSequence[items.size()]), selected, this);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    /**
     * Methode permettant de deselectionner tous les elements
     */
    public void unselectAll(){
        for(int i=0;i<selected.length;i++)
            selected[i] = false;
    }

    /**
     * Methode verifiant si un element est selectionne
     * @param id Id de l'element
     * @return TRUE si l'element est selectionne, FALSE sinon
     */
    public boolean isSelected(int id){
        return selected[id];
    }

    /**
     * Methode forçant la selection d'un element
     * @param id Id de l'element a selectionner
     */
    public void setItemSelected(int id){
        if(id < selected.length)
            selected[id] = true;
    }

    /**
     * Methode pour ajouter des elements dans la liste
     * @param items Liste des elements a afficher
     * @param allText Texte a afficher par defaut dans le spinner
     * @param listener Listener MutiSpinnerListener pour recuperer les items selectionnes
     */
    public void setItems(List<String> items, String allText,
                         MultiSpinnerListener listener) {
        this.items = items;
        this.defaultText = allText;
        this.listener = listener;

        // all selected by default
        selected = new boolean[items.size()];
        for (int i = 0; i < selected.length; i++)
            selected[i] = false;

        // all text on the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, new String[] { allText });
        setAdapter(adapter);
    }

    /**
     * Interface MultiSpinnerListener
     * Cette interface permet de traiter les elements sectionnes lorsqu'on ferme la boite de dialogue
     */
    public interface MultiSpinnerListener {
        public void onItemsSelected(boolean[] selected);
    }
}