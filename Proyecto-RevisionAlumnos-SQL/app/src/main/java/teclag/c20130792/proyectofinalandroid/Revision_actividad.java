package teclag.c20130792.proyectofinalandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Revision_actividad extends AppCompatActivity {

    BaseDatosHelper baseDatosHelper;
    private ListView lstvAlumnos;
    private ArrayList<String> seleccionados;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private ArrayList<String> alumnos;
    private static final int REQUEST_CODE_PERMISSION = 123;
    private static final int REQUEST_CODE_FILE_PICKER = 456;

    TextView name, mat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision_actividad);
        baseDatosHelper = new BaseDatosHelper(this);


        name = findViewById(R.id.txtActividad);
        Intent intent = getIntent();
        String getName = intent.getStringExtra("name");
        name.setText(getName);

        mat = findViewById(R.id.txtMateriaR);
        Intent intent2 = getIntent();
        String getName2 = intent2.getStringExtra("materia");
        mat.setText(getName2);

        listView = findViewById(R.id.lstvAlumnos);
        Button btnAgregar = findViewById( R.id.btnAgregarAlu );
        Button btnEliminar = findViewById( R.id.btnElimAlu );
        Button btnCargar = findViewById ( R.id.btnCargar );
        Button btnGuardar = findViewById( R.id.btnGuardar );
        Button btnLimpiar = findViewById( R.id.btnLimpiar );

        alumnos = new ArrayList<String>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, alumnos);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Agregar el dato ingresado por el usuario a la lista
                EditText editText = new EditText(Revision_actividad.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(Revision_actividad.this);
                builder.setTitle( "Ingrese nombre del alumno: " )
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nuevoDato = editText.getText().toString();
                                alumnos.add(nuevoDato);
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

        btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Revision_actividad.this);
                builder.setTitle( "¡AVISO!" )
                        .setMessage("Esta aplicacion solo acepta archivos tipo: '.txt' \n" +
                                "------------------------------------------\n" +
                        "EL formato del archivo debe ser: \n" +
                                "" +
                                "Pedro Solorio 20130873\n" +
                                "Liliana Benitez 19021689\n" +
                                "Raúl Alvarez 20130001")
                        .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cargarDatosDesdeArchivo();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuardarElementos();
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarLista();
            }
        });

        cargarDatosDesdeBaseDeDatos();
    }

    private void cargarDatosDesdeBaseDeDatos() {
        Cursor cursor = baseDatosHelper.getDatosPorMatYAct(mat.getText().toString(), name.getText().toString());

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String alumno = cursor.getString(cursor.getColumnIndex(BaseDatosHelper.COL3)); // Obtener el nombre del alumno
                int estado = cursor.getInt(cursor.getColumnIndex(BaseDatosHelper.COL4)); // Obtener el estado del alumno

                alumnos.add(alumno); // Agregar el alumno a la lista
                int index = alumnos.indexOf(alumno); // Obtener el índice del alumno en la lista
                listView.setItemChecked(index, estado == 1); // Marcar el checkbox según el estado guardado en la base de datos
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter.notifyDataSetChanged(); // Notificar al adaptador sobre los cambios en los datos
    }


    private void eliminarElementosSeleccionados() {
        SparseBooleanArray seleccionados = listView.getCheckedItemPositions();
        for (int i = seleccionados.size() - 1; i >= 0; i--) {
            int posicion = seleccionados.keyAt(i);
            String alumno = alumnos.get(posicion);
            if (seleccionados.valueAt(i)) {
                alumnos.remove(posicion);
                baseDatosHelper.deleteNombre(mat.getText().toString(), name.getText().toString(), alumno);
            }
        }
        adapter.notifyDataSetChanged();
        listView.clearChoices();
    }

    private void eliminarLista() {
        SparseBooleanArray seleccionados = listView.getCheckedItemPositions();
        for (int i = seleccionados.size() - 1; i >= 0; i--) {
            int posicion = seleccionados.keyAt(i);

            String alumno = alumnos.get(posicion);

            alumnos.remove(posicion);

            baseDatosHelper.deleteNombre(mat.getText().toString(), name.getText().toString(), alumno);
        }
        adapter.notifyDataSetChanged();
        listView.clearChoices();
    }

    public void GuardarElementos() {
        SparseBooleanArray seleccionados = listView.getCheckedItemPositions();

        // Iterar sobre todos los alumnos y guardar su estado en la base de datos
        for (int i = 0; i < alumnos.size(); i++) {
            String alumno = alumnos.get(i);
            int estado = (seleccionados.get(i)) ? 1 : 0; // Verificar si el alumno está seleccionado
            baseDatosHelper.addDatos(mat.getText().toString(), name.getText().toString(), alumno, estado);
            baseDatosHelper.updateTabla(mat.getText().toString(), name.getText().toString(), alumno, estado);
        }

        // Iterar nuevamente para guardar los alumnos no seleccionados
        for (int i = 0; i < alumnos.size(); i++) {
            if (!seleccionados.get(i)) {
                String alumno = alumnos.get(i);
                // Guardar el nombre del alumno y marcarlo como no seleccionado (0)
                baseDatosHelper.addDatos(mat.getText().toString(), name.getText().toString(), alumno, 0);
                baseDatosHelper.updateTabla(mat.getText().toString(), name.getText().toString(), alumno, 0);
            }
        }

        // Notificar cambios a la base de datos (puedes ajustar según tu lógica)
        adapter.notifyDataSetChanged();
    }

    public void btnSalirClick2( View v ) {
        Revision_actividad.this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, cargar datos desde el archivo
                cargarDatosDesdeArchivo();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cargarDatosDesdeArchivo() {
        // Aquí puedes abrir un diálogo para que el usuario seleccione el archivo y luego leer los datos.
        // Puedes usar un Intent para abrir un explorador de archivos.

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Permitir cualquier tipo de archivo
        intent.addCategory ( Intent.CATEGORY_DEFAULT );

        try {
            startActivityForResult( Intent.createChooser ( intent, "Selecciona una opcion" ),
                    REQUEST_CODE_FILE_PICKER);
        }
        catch (ActivityNotFoundException e ) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FILE_PICKER && resultCode == RESULT_OK) {
            // Aquí obtén la URI del archivo seleccionado
            Uri uri = data.getData();
            // Luego, puedes leer los datos del archivo utilizando la URI
            // y actualizar tu lista de alumnos y el adaptador
            leerDatosDesdeUri(uri);
        }
    }

    private void leerDatosDesdeUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Agregar cada línea como un alumno
                alumnos.add(line);
            }
            adapter.notifyDataSetChanged();
            // Cierra los recursos abiertos
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al leer el archivo", Toast.LENGTH_SHORT).show();
        }
    }

}

