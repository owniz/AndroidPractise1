package es.jmoral.dam2.practicaevaluable1;

/**
 * Esta clase es la actividad secundaria en la que podemos insertar los datoa que luego mandaremos
 * a la actividad primaria, además si la actividad primaria tiene ya datos que hayamos introducido
 * esta actividad los recogerá para mostrarlos. También seremos capaces de hacer uso del GPS
 */

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, LocationListener, SeekBar.OnSeekBarChangeListener {

    // declaramos los TextViews que estarán en la actividad secundaria
    private TextView tvEdad;
    private TextView tvLatitud;
    private TextView tvlongitud;

    // declaramos los EditText que estarán en la actividad secundaria
    private EditText etNombre;
    private EditText etTelefono;

    // declaramos los Botones que estarán en la actividad secundaria
    private Button botonAceptar;
    private ToggleButton botonToggle;

    // declaramos esta tabla para poder luego ocultarla cuando el ToggleButton no esté activo
    private TableLayout tablaCoordenadas;

    // declaramos la SeekBar que estará en la actividad secundaria
    private SeekBar seekBarAnyos;

    // variables para almacenar los datos del GPS
    private double latitud;
    private double longitud;

    // variable para acceder a los servicios de localización del dispositivo
    private LocationManager lm;

    /* sobrescribimos el método onCreate para definir qué elementos aparecerán en pantalla
    * a la hora de crear la actividad
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // inicializamos la tabla que se mostrará cuando el ToggleButton este enabled
        tablaCoordenadas = (TableLayout) findViewById(R.id.tablaCoordenadas);

        // inicialiamos los EditText y TextView que aparecerán en pantalla
        etNombre = (EditText) findViewById(R.id.editTextNombre);
        tvEdad = (TextView) findViewById(R.id.textViewMostrarEdad);
        etTelefono = (EditText) findViewById(R.id.editTextTelefono);
        tvLatitud = (TextView) findViewById(R.id.textViewMostrarLatitud);
        tvlongitud = (TextView) findViewById(R.id.textViewMostrarLongitud);

        // inicializamos la variable que accede al servicio de localización
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // inicializamos la SeekBar
        seekBarAnyos = (SeekBar) findViewById(R.id.seekBar);

        // inicialiamos los botones que aparecerán en pantalla
        botonAceptar = (Button) findViewById(R.id.buttonAceptar);
        botonToggle = (ToggleButton) findViewById(R.id.toggleButtonCoordenadas);

        // añadimos los listeners a los botones
        botonToggle.setOnCheckedChangeListener(this);
        botonAceptar.setOnClickListener(this);
        seekBarAnyos.setOnSeekBarChangeListener(this);

        // solicitamos que nos notifique cuando hay cambios en la localización
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        /* si anteoriormente ya hemos mandado un nombre, edad o teléfono a la actividad principal y
         * volvemos a la secundaria para volverlos a editar, éstos aparecerán ya escritos, los
         * recuperamos con getIntent() y los mostramos con setText()
         */
        etNombre.setText(getIntent().getExtras().getString(MainActivity.KEY_NOMBRE));
        etNombre.setSelection(etNombre.getText().length()); // colocamos el cursor a la derecha de la palabra

        // comprobamos que la edad tenga un valor válido para mostrarlo
        if(getIntent().getExtras().getInt(MainActivity.KEY_EDAD) >= 18) {
            seekBarAnyos.setProgress(getIntent().getExtras().getInt(MainActivity.KEY_EDAD) - 18);
            tvEdad.setText(getString(R.string.anyos, String.valueOf(getIntent().getExtras().getInt(MainActivity.KEY_EDAD))));
        }

        // comprobamos que el teléfono tenga un valor para mostrarlo (si no hacemos esta comprobación
        // y accedemos a la actividad secundaria sin tener un teléfono en la principal escribe un 0 ya que el int
        // no tiene definido un valor todavía)
        if (getIntent().hasExtra(MainActivity.KEY_TELEFONO)) {
            etTelefono.setText(String.valueOf(getIntent().getExtras().getInt(MainActivity.KEY_TELEFONO)));
            etTelefono.setSelection(etTelefono.getText().length()); // colocamos el cursor a la derecha de los números
        }
    }

    // definimos qué hará el botón una vez pulsado sobrescribiendo el método onClick
    @Override
    public void onClick(View v) {

        // declaramos e inicializamos el intent que recogerá y mandará los datos a la principal
        Intent intent = new Intent();

        // comprobamos que el botón pulsado es el de aceptar para todos los campos
        if(R.id.buttonAceptar == v.getId()) {

            // comprobamos si está vacío el campo para enviar datos sólo si hemos introducido algún valor para todos los campos
            if (!etNombre.getText().toString().isEmpty())
                intent.putExtra(MainActivity.KEY_NOMBRE, etNombre.getText().toString());
            if (!tvEdad.getText().toString().isEmpty())
                intent.putExtra(MainActivity.KEY_EDAD, seekBarAnyos.getProgress() + 18);
            if (!etTelefono.getText().toString().isEmpty())
                intent.putExtra(MainActivity.KEY_TELEFONO, Integer.valueOf(etTelefono.getText().toString()));
        }

        // comprobamos que el ToggleButton esté activo para poder enviar la latitud y la longitud
        if(botonToggle.isChecked()) {

            // comprobamos que el valor sea distinto de 0 para enviar los datos (si no se realiza esta
            // comprobacion se envian los valores 0 y 0 si el GPS esta desactivado)
            if(latitud != 0)
                intent.putExtra(MainActivity.KEY_LATITUD, latitud);
            if(longitud != 0)
                intent.putExtra(MainActivity.KEY_LONGITUD, longitud);
          }

        // define el resultado que será recuperado por la actividad llamante que lo espera
        setResult(RESULT_OK, intent);

        // termina la actividad
        finish();
    }

    // comprobamos si el ToggleButton esta activo para mostrar o no la tabla donde se muestran las coordenadas
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        tablaCoordenadas.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
    }

    /* este método es llamado cuando ocurre un cambio en la localización, lo sobrescribo para que
     * almacene y muestre las coordenadas cuando cambia la localización
     */
    @Override
    public void onLocationChanged(Location location) {
        latitud = location.getLatitude();
        longitud = location.getLongitude();

        tvLatitud.setText(String.valueOf(latitud));
        tvlongitud.setText(String.valueOf(longitud));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /* método que es llamado cuando cambia la SeekBar, sobrescrito para que muestre la edad que
     * seleccionamos en la SeekBar a través de un TextView
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvEdad.setText(getString(R.string.anyos, String.valueOf(progress + 18)));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
