package teclag.c20130792.proyectofinalandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BaseDatosHelper extends SQLiteOpenHelper {

    private static final String TAG = "BaseDatosHelper";
    private static final String DB_NAME    = "personitasDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "revision_tabla";
    public static final String COL1 = "Materia";
    public static final String COL2 = "Actividad";
    public static final String COL3 = "Nombre";
    public static final String COL4 = "Realizado";

    //----------------------------------------------------------------------------------------------

    public BaseDatosHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION );
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE " + TABLE_NAME + " ( " +
                COL1 + " TEXT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT, " +
                COL4 + " INTEGER DEFAULT 0, " +
                "CONSTRAINT pk_unique PRIMARY KEY (" + COL1 + ", " + COL2 + ", " + COL3 + ")" +
                ")";
        db.execSQL(crearTabla);
    }


    //----------------------------------------------------------------------------------------------

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //----------------------------------------------------------------------------------------------

    public boolean addDatos(String materia, String actividad, String nombre, int realizado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, materia);
        contentValues.put(COL2, actividad);
        contentValues.put(COL3, nombre);
        contentValues.put(COL4, realizado);

        Log.d(TAG, "addDatos: Agregando datos a " + TABLE_NAME + ": Materia = " + materia +
                ", Actividad = " + actividad + ", Nombre = " + nombre + ", Realizado = " + realizado);

        long resultado = db.insert(TABLE_NAME, null, contentValues);

        // si se insertó correctamente, resultado será el ID del nuevo registro insertado (diferente de -1)
        return resultado != -1;
    }

    //----------------------------------------------------------------------------------------------

    public Cursor getDatos() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    //----------------------------------------------------------------------------------------------

    public Cursor getDatosPorMatYAct(String materia, String actividad) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL3 + ", " + COL4 + " FROM " + TABLE_NAME +
                " WHERE " + COL1 + " = '" + materia + "'" +
                " AND " + COL2 + " = '" + actividad + "'";
        return db.rawQuery(query, null);
    }

    public Cursor getDatosPorMat() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME ;
        return db.rawQuery(query, null);
    }

    public Cursor getDatosporAct(String materia) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL2 + " FROM " + TABLE_NAME +
                " WHERE " + COL1 + " = '" + materia + "'";
        return db.rawQuery(query, null);
    }


    //----------------------------------------------------------------------------------------------

    public void updateTabla(String materia, String actividad, String nombre, int nuevoRealizado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL4, nuevoRealizado);

        String whereClause = COL1 + " = ? AND " + COL2 + " = ? AND " + COL3 + " = ?";
        String[] whereArgs = {materia, actividad, nombre};

        int rowsAffected = db.update(TABLE_NAME, contentValues, whereClause, whereArgs);

        Log.d(TAG, "updateTabla: Filas afectadas: " + rowsAffected);
    }

    //----------------------------------------------------------------------------------------------

    public void deleteNombre(String materia, String actividad, String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = COL1 + " = ? AND " + COL2 + " = ? AND " + COL3 + " = ?";
        String[] whereArgs = {materia, actividad, nombre};

        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);

        Log.d(TAG, "deleteNombre: Filas eliminadas: " + rowsDeleted);
    }

    //----------------------------------------------------------------------------------------------
}
























