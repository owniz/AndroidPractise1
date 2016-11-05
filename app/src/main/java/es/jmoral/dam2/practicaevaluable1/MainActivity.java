package es.jmoral.dam2.practicaevaluable1;

/**
 * Esta clase es la actividad principal que recibe una serie de datos desde la clase SecondActivity
 * que mostrará por pantalla, si esta clase tiene ya algunos datos recibidos anteriormente desde la
 * actividad secundaria los volverá a mandar. Además somos capaces de acceder a la segunda actividad
 * desde está a través de un botón.
 */

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // constantes definidas para usar como claves
    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_EDAD = "edad";
    public static final String KEY_TELEFONO = "telefono";
    public static final String KEY_LATITUD = "latitud";
    public static final String KEY_LONGITUD = "longitud";

    // constante para el requestCode
    public static final int REQUEST_CODE = 1234;

    // declaramos los TextViews que estarán en la actividad principal
    private TextView tvNombre;
    private TextView tvEdad;
    private TextView tvTelefono;
    private TextView tvLatitud;
    private TextView tvLongitud;

    // declaramos los botones de la actividad principal
    private Button botonEditar;
    private Button botonMaps;

    // variables para almacenar los datos recibidos del GPS desde la actividad secundaria
    private double latitud;
    private double longitud;

    /* sobrescribimos el método onCreate para definir qué elementos aparecerán en pantalla
     * a la hora de crear la actividad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inicialiamos los TextView que aparecerán en pantalla
        tvNombre = (TextView) findViewById(R.id.textViewNombreDevuelto);
        tvEdad = (TextView) findViewById(R.id.textViewEdadDevuelto);
        tvTelefono = (TextView) findViewById(R.id.textViewTelefonoDevuelto);
        tvLatitud = (TextView) findViewById(R.id.textViewLatitudDevuelto);
        tvLongitud = (TextView) findViewById(R.id.textViewLongitudDevuelto);

        // inicialiamos los botones que aparecerán en pantalla
        botonEditar = (Button) findViewById(R.id.buttonEditar);
        botonMaps = (Button) findViewById(R.id.buttonMaps);

        // añadimos los listeners a los botones
        botonEditar.setOnClickListener(this);
        botonMaps.setOnClickListener(this);
    }

    // definimos qué hará cada botón una vez pulsado sobrescribiendo el método onClick
    @Override
    public void onClick(View v) {

        // Si pulsamos el boton Editar
        if(R.id.buttonEditar == v.getId()) {

            // declaramos e inicializamos un Intent y un Bundle para enviar los datos a la actividad secundaria
            Intent intent = new Intent(this, SecondActivity.class);
            Bundle b = new Bundle();

            /* comprobamos si los campos tienen valor para enviarlos a la segunda actividad,
             * utilizamos clave/valor para que las actividades conozcan a qué dato nos estamos refiriendo
             */
            if(!tvNombre.getText().toString().isEmpty())
                b.putString(KEY_NOMBRE, tvNombre.getText().toString());
            if(!tvEdad.getText().toString().isEmpty())
                b.putInt(KEY_EDAD, Integer.valueOf(tvEdad.getText().toString()));
            if(!tvTelefono.getText().toString().isEmpty())
                b.putInt(KEY_TELEFONO, Integer.valueOf(tvTelefono.getText().toString()));

            // insertamoos el contenedor (Bundle) que contiene los datos al intent
            intent.putExtras(b);

            // llamamos a la segunda actividad
            startActivityForResult(intent, REQUEST_CODE);
        }

        // Si pulsamos el boton para acceder a Google Maps
        if(R.id.buttonMaps == v.getId()) {

            // definimos un Uri que pueda recibirlo Google Maps
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitud, longitud);
            Intent intentMaps = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

            // hacemos la llamada
            startActivity(intentMaps);
        }
    }

    // sobrescribimos el método onActivityResult que recibe los datos de la actividad secundaria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // comprobamos si hemos recibido los datos correctamente
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            // para cada clave comprobamos que el intent contiene ese extra para mostrarlo
            if(data.hasExtra(KEY_NOMBRE))
                tvNombre.setText(data.getExtras().getString(KEY_NOMBRE));
            else
                // si el usuario borra el dato en la segunda actividad mostramos un String vacío
                tvNombre.setText("");
            if(data.hasExtra(KEY_EDAD))
                tvEdad.setText(String.valueOf(data.getIntExtra(KEY_EDAD, -1)));
            else
                tvEdad.setText("");
            if(data.hasExtra(KEY_TELEFONO))
                tvTelefono.setText(String.valueOf(data.getIntExtra(KEY_TELEFONO, -1)));
            else
                tvTelefono.setText("");
            if(data.hasExtra(KEY_LATITUD)) {
                tvLatitud.setText(String.valueOf(data.getDoubleExtra(KEY_LATITUD, -1d)));
                latitud = data.getDoubleExtra(KEY_LATITUD, -1d);
            } else {
                tvLatitud.setText("");
            }
            if(data.hasExtra(KEY_LONGITUD)) {
                tvLongitud.setText(String.valueOf(data.getDoubleExtra(KEY_LONGITUD, -1d)));
                longitud = data.getDoubleExtra(KEY_LONGITUD, -1d);
            } else {
                tvLongitud.setText("");
            }

            // hacemos Enabled el boton si recibimos una latitud y una longitud
            botonMaps.setEnabled(data.hasExtra(KEY_LATITUD) && data.hasExtra(KEY_LONGITUD));

            // si el usuario apreta el boton back devolveremos un Toast avisándole
        } else if(requestCode == REQUEST_CODE && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, R.string.usuarioBack, Toast.LENGTH_SHORT).show();
        }
    }
}
