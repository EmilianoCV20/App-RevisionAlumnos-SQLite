package teclag.c20130792.proyectofinalandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Actividades_materia extends AppCompatActivity {

    BaseDatosHelper baseDatosHelper;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private ArrayList<String> actividad;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividades_materia);
        baseDatosHelper = new BaseDatosHelper(this);

        name = findViewById(R.id.txtMateriaX);
        Intent intent = getIntent();
        String getName = intent.getStringExtra("name");
        name.setText(getName);

        listView = findViewById(R.id.lstvActividad);
        Button btnAgregar = findViewById( R.id.btnAgregar2 );
        Button btnEliminar = findViewById( R.id.button4 );

        actividad = new ArrayList<String>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, actividad);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        cargarDatosDesdeBaseDeDatos();

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Agregar el dato ingresado por el usuario a la lista
                EditText editText = new EditText(Actividades_materia.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(Actividades_materia.this);
                builder.setTitle( "Nombre de ACTIVIDAD: " )
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nuevoDato = editText.getText().toString();
                                actividad.add(nuevoDato);
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
        Cursor cursor = baseDatosHelper.getDatosporAct(name.getText().toString());

        if (cursor != null && cursor.moveToFirst()) {
            HashSet<String> actividadesUnicas = new HashSet<>();

            do {
                String actividadx = cursor.getString(cursor.getColumnIndex(BaseDatosHelper.COL2));
                actividadesUnicas.add(actividadx);
            } while (cursor.moveToNext());

            cursor.close();

            List<String> listaOrdenada = new ArrayList<>(actividadesUnicas);
            Collections.sort(listaOrdenada);

            actividad.clear();
            actividad.addAll(listaOrdenada);
        }

        adapter.notifyDataSetChanged();
    }


    private void eliminarElementosSeleccionados() {
        SparseBooleanArray seleccionados = listView.getCheckedItemPositions();
        for (int i = seleccionados.size() - 1; i >= 0; i--) {
            int posicion = seleccionados.keyAt(i);
            String actividadx = actividad.get(posicion);
            if (seleccionados.valueAt(i)) {
                actividad.remove(posicion);
            }
        }
        adapter.notifyDataSetChanged();
    }


    public void btnActividadClick(View view) {
        SparseBooleanArray seleccionados = listView.getCheckedItemPositions();


        if (seleccionados.size() > 0) {
            for (int i = 0; i < seleccionados.size(); i++) {
                int posicion = seleccionados.keyAt(i);

                if (seleccionados.valueAt(i)) {
                    String nombreSeleccionado = actividad.get(posicion);
                    String Mat = name.getText().toString();

                    Intent intent = new Intent(Actividades_materia.this, Revision_actividad.class);
                    intent.putExtra("name", nombreSeleccionado);
                    intent.putExtra("materia", Mat);
                    startActivity(intent);
                }
            }
        } else {
            Toast.makeText(this, "Por favor, selecciona una materia", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnSalirClick1(View v) {
        Actividades_materia.this.finish();
    }
}