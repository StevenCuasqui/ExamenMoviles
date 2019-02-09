package epn.moviles.examenapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_crear_medicamente.*

class CrearMedicamenteActivity : AppCompatActivity() {

    var idPacienteMedicamento: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_medicamente)
        this.idPacienteMedicamento = intent.getIntExtra("idPaciente",1)
            button_guardarMed.setOnClickListener {
                crearMedicamento()
            this.finish()}
        button_cancelarMed.setOnClickListener { this.finish() }
    }


    fun crearMedicamento(){
        val url = "http://172.31.105.210:1337/Medicamento"
        val MedicamentoNombreInd = texto_nombreMed.text
        val MedicamentoGramosInd = texto_gramosMed.text
        val MedicamentoComposicionInd = texto_compoMed.text
        val MedicamentoUsadoParaInd = texto_usoMed.text
        val MedicamentoFechaCaduInd = texto_caducidadMed.text
        val MedicamentoNPastillasInd = texto_pastillasMed.text

        val parametros = listOf(
                "nombre" to MedicamentoNombreInd,
            "gramosAIngerir" to MedicamentoGramosInd,
            "composicion" to MedicamentoComposicionInd,
            "usadoPara" to MedicamentoUsadoParaInd,
            "fechaCaducidad" to MedicamentoFechaCaduInd,
        "numeroPastillas" to MedicamentoNPastillasInd,
        "pacienteId" to idPacienteMedicamento
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



    }
}
