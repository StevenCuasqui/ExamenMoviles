package epn.moviles.examenapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_crear_paciente.*

class CrearPacienteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_paciente)

        button_cancelar_crear_paciente.setOnClickListener { this.finish() }

        val url = "http://192.168.100.8:1337/Paciente"

        val parametros = listOf(
            "nombres" to "Steven Andres",
            "apellidos" to "Cuasqui Ponce",
            "fechaNacimiento" to "1997-01-22",
            "tieneSeguro" to false
        )

        url
            .httpPost(parametros)
            .responseString { request, response, result ->

                when (result) {
                    is Result.Failure -> {
                        val exepcion = result.getException()
                        Log.i("http", "Error: ${exepcion}")
                        Log.i("http", "Error: ${response}")

                    }
                    is Result.Success -> {
                        val data = result.get()
                        Log.i("http", "Datos: ${data}") // Llega como String

                        Log.i("Tipo","${data::class.simpleName}" )
//                        val usuarioClase: PacienteHttp? = Klaxon()
//                            .parse<PacienteHttp>(usuarioString)

                        //Log.i("http", "Datos: ${usuarioClase?.nombres}")

                    }
                }
            }

    }

    class PacienteHttp(var nombres: String,
                      var apellidos: String,
                       var fechaNacimiento: String,
                       var tieneSeguro: Boolean,
                      var createdAt: Long? = null,
                      var updatedAt: Long? = null,
                      var id: Int? = null) {} //Enviar esta clase a tronsformacion para ser Json, enviar ese Json al Server
}
