package epn.moviles.examenapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonJson
import com.beust.klaxon.json
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_crear_paciente.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.ref.ReferenceQueue

class CrearPacienteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_paciente)

        button_cancelar_crear_paciente.setOnClickListener { this.finish() }

        button_post_paciente.setOnClickListener {  crearPaciente()}

    }


    fun crearPaciente(){
        val url = "http://192.168.100.8:1337/Paciente"
        val nombres = texto_nombre.text
        val apellidos = texto_apellido.text
        val fechaNacimiento = texto_fecha.text
        var tieneSeguro:Boolean
        if (checkBox_seguro.isChecked()){
            tieneSeguro = true
        }else{
            tieneSeguro = false
        }


        val parametros = listOf(
            "nombres" to nombres,
            "apellidos" to apellidos,
            "fechaNacimiento" to fechaNacimiento,
            "tieneSeguro" to tieneSeguro
        )
            val paciente = Paciente
//        val paciente = PacienteHttp("","","",true,null,null,null)
//        val paciente2 = PacienteHttp("","","",false,null,null,null)
//        val req = listOf<PacienteHttp>(paciente,paciente2)

        //val req = url.httpPost().httpHeaders["Content-Type"] = "application/json"
        url.httpPost().headers["Content-Type"] = "application/json"
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
        limpiarCampos()
    }

    fun limpiarCampos(){

        val nombres = texto_nombre.text.clear()
        val apellidos = texto_apellido.text.clear()
        val fechaNacimiento = texto_fecha.text.clear()
        checkBox_seguro.isChecked =false

    }

    class PacienteHttp(var nombres: String,
                      var apellidos: String,
                       var fechaNacimiento: String,
                       var tieneSeguro: Boolean,
                      var createdAt: Long? = null,
                      var updatedAt: Long? = null,
                      var id: Int? = null) {} //Enviar esta clase a tronsformacion para ser Json, enviar ese Json al Server
}
