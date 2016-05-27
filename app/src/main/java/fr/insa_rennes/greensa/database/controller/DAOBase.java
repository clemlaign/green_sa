package fr.insa_rennes.greensa.database.controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import fr.insa_rennes.greensa.database.DatabaseHandler;

/**
 * "Data Access Object" Base<br/>
 *
 * Cette classe sert a acceder au contenu de la base de donnees<br/>
 */
public abstract class DAOBase {

    /**
     * Version de la base de donnees<br/>
     * Si on met a jour la base de donnees, il faut changer cet attribut (incrementation)<br/>
     * Cela supprime l'ancienne table tir pour recreer la nouvelle
     */
    protected final static int VERSION = 7;

    /**
     * Le nom du fichier qui contiendra la base de donnees
     */
    protected final static String NOM = "database.db";

    /**
     * L'objet SQLiteDatabase qui effectue les operations sur la bdd
     */
    protected SQLiteDatabase mDb = null;

    /**
     * L'objet DatabaseHandler qui permet la creation de la bdd
     */
    protected DatabaseHandler mHandler = null;

    /**
     * Constructeur de la classe DAOBase qui initialise le DatabaseHandler
     *
     * @param pContext Contexte actuel
     */
    public DAOBase(Context pContext) {
        this.mHandler = new DatabaseHandler(pContext, NOM, null, VERSION);
    }

    /**
     * Methode qui ouvre la base de donnees
     *
     * @return L'objet SQLiteDatabase pour effectuer des operations sur la bdd
     */
    public SQLiteDatabase open() {
        // Pas besoin de fermer la derniere base puisque getWritableDatabase s'en charge
        mDb = mHandler.getWritableDatabase();
        return mDb;
    }

    /**
     * Methode pour la fermeture de la bdd
     */
    public void close() {
        mDb.close();
    }

    /**
     * Methode pour recuperer SQLiteDatabase
     *
     * @return L'objet SQLiteDatabase
     */
    public SQLiteDatabase getDb() {
        return mDb;
    }
}