package teclag.c20130792.proyectofinalandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    BaseDatosHelper baseDatosHelper;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private ArrayList<String> materia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseDatosHelper = new BaseDatosHelper(this);

        listView = findViewById(R.id.lstvMaterias);
        Button btnAgregar = findViewById( R.id.btnAgregar );
        Button btnEliminar = findViewById( R.id.button10 );

        materia = new ArrayList<String>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, materia);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        cargarDatosDesdeBaseDeDatos();

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Agregar el dato ingresado por el usuario a la lista
                EditText editText = new EditText(MainActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle( "Nombre de MATERIA: " )
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nuevoDato = editText.getText().toString();
                                materia.add(nuevoDato);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });


        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarElementosSeleccionados();
            }
        });
    }

    private void cargarDatosDesdeBaseDeDatos() {
        Cursor cursor = baseDatosHelper.getDatosPorMat();

        if (cursor != null && cursor.moveToFirst()) {
            HashSet<String> materiasUnicas = new HashSet<>(); // Utilizamos un HashSet para evitar duplicados

            do {
                String materiax = cursor.getString(cursor.getColumnIndex(BaseDatosHelper.COL1));
                materiasUnicas.add(materiax); // Agregamos la materia al HashSet
            } while (cursor.moveToNext());

            cursor.close();

            List<String> listaOrdenada = new ArrayList<>(materiasUnicas);
            Collections.sort(listaOrdenada); // Ordenar la lista alfabéticamente

            materia.clear(); // Limpiamos la lista actual antes de agregar las materias ordenadas
            materia.addAll(listaOrdenada); // Agregamos las materias ordenadas al ArrayList
        }

        adapter.notifyDataSetChanged();
    }



    private void eliminarElementosSeleccionados() {
        SparseBooleanArray seleccionados = listView.getCheckedItemPositions();
        for (int i = seleccionados.size() - 1; i >= 0; i--) {
            int posicion = seleccionados.keyAt(i);
            String materiax = materia.get(posicion);
            if (seleccionados.valueAt(i)) {
                materia.remove(posicion);
            }
        }
        adapter.notifyDataSetChanged();
    }


    public void btnMateriaClick(View view) {
        SparseBooleanArray seleccionados = listView.getCheckedItemPositions();

        if (seleccionados.size() > 0) {
            for (int i = 0; i < seleccionados.size(); i++) {
                int posicion = seleccionados.keyAt(i);

                if (seleccionados.valueAt(i)) {
                    String nombreSeleccionado = materia.get(posicion);

                    Intent intent = new Intent(MainActivity.this, Actividades_materia.class);
                    intent.putExtra("name", nombreSeleccionado);
                    startActivity(intent);
                }
            }
        } else {
            Toast.makeText(this, "Por favor, selecciona una materia", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnSalirClick(View v) {
        finish();
    }

    public void btnAcercadeClick ( View v ) {
        Intent intent  = new Intent ( this, AcercaDe.class );
        startActivity ( intent );
        finish();
    }
}