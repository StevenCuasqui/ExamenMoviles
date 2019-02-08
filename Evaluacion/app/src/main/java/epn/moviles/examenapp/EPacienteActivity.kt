package epn.moviles.examenapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_epaciente.*
import org.json.JSONArray

class EPacienteActivity : AppCompatActivity() {

    var miPacientito: Paciente? = null
    var iden:Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_epaciente)

        button_cancelarE.setOnClickListener { this.finish() }

        button_guardarE.setOnClickListener {
            guardarPaciente()
            this.finish()
            }

        val miPaciente = intent.getParcelableExtra<Paciente>("paciente")
        val id = intent.getIntExtra("id",-1)
        miPacientito =miPaciente
        iden = id
        llenarCampos()

    }

    override fun onPause(){
        super.onPause()
        miPacientito = null
    }

    fun llenarCampos(){
        texto_nombreE.setText(miPacientito?.nombres)
        texto_apellidoE.setText(miPacientito?.apellidos)
        texto_fechaE.setText(miPacientito?.fechaNacimiento)
        val seguroTo =miPacientito?.tieneSeguro
        if(seguroTo == true){
            checkBox_E.isChecked = true
        }else{
            checkBox_E.isChecked = false
        }


    }

    fun guardarPaciente(){
        val nuevoNombre = texto_nombreE.text
        val nuevoApellido = texto_apellidoE.text
        val nuevaFecha = texto_fechaE.text
        val nuevoSeguro:Boolean
        if(checkBox_E.isChecked){
            nuevoSeguro = true
        }else{
            nuevoSeguro = false
        }


        val url = "http://172.31.105.210:1337/Paciente/${iden}"
        var aux = JSONArray()


        url
            .httpPut(listOf("nombres" to nuevoNombre,"apellidos" to nuevoApellido,
            "fechaNacimiento" to nuevaFecha,"tieneSeguro" to nuevoSeguro))
            .responseString{ request, response, result ->

                when (result) {
                    is Result.Failure -> {
                        val exepcion = result.getException()
                        Log.i("http", "Error: ${exepcion}")

                    }
                    is Result.Success -> {
                        val data = result.get()

                        Log.i("http", "Datos: ${aux}")
                        Log.i("Tipo", "${aux::class.simpleName}")
//
                    }
                }
            }
    }

}
