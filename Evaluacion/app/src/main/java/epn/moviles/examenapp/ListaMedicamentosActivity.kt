package epn.moviles.examenapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_lista_medicamentos.*
import kotlinx.android.synthetic.main.medicamento_item.view.*
import org.json.JSONArray
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ListaMedicamentosActivity : AppCompatActivity() {

    var miPacientito: Paciente? = null

    var identificaPaciente: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_medicamentos)
        val identifiPaciente = intent.getIntExtra("id",1)
        identificaPaciente = identifiPaciente

        button_CrearMedicamento.setOnClickListener {  iraCrearMedicamento()}
        val miPaciente = intent.getParcelableExtra<Paciente>("paciente")
        miPacientito =miPaciente

        llenarPacienteCampos()
        cargarDatos()
    }

    override fun onResume(){
        super.onResume()
        cargarDatos()
    }

    fun iraCrearMedicamento(){
        val intent = Intent(this,CrearMedicamenteActivity::class.java)
        intent.putExtra("idPaciente",identificaPaciente)
        startActivity(intent)
    }

    fun llenarPacienteCampos(){
        text_Apellidos_Paciente.setText(miPacientito?.apellidos.toString())
        text_fecha_Paciente.setText(miPacientito?.fechaNacimiento.toString())
        text_nombre_paciente.setText(miPacientito?.nombres.toString())
    }


    val listGeneral = listadeMedicamentos

    fun cargarDatos(){
        listGeneral.idMed.clear()
        listGeneral.gramosAIngerir.clear()
        listGeneral.nombre.clear()
        listGeneral.composicion.clear()
        listGeneral.usadoPara.clear()
        listGeneral.fechaCaducidad.clear()
        listGeneral.numeroPastillas.clear()
        listGeneral.pacienteId.clear()
        Log.i("http", "Direccion: ${identificaPaciente}")
        val url = "http://172.31.105.210:1337/Medicamento/?pacienteId=${identificaPaciente}"
        var aux = JSONArray()


        url
            .httpGet()
            .responseJson{ request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        val exepcion = result.getException()
                        Log.i("http", "Error: ${exepcion}")

                    }
                    is Result.Success -> {
                        val data = result.get()
                        aux = data.array()
                        Log.i("http", "Consulta: ${request}")
                        Log.i("http", "Datos: ${aux}")
                        Log.i("Tipo", "${aux::class.simpleName}")
//

                    }
                }

                var resp= result.get().array()
                for (i in 0 until aux.length()) {
                    listGeneral.idMed.add(resp.getJSONObject(i).getInt("id"))
                    listGeneral.nombre.add(resp.getJSONObject(i).getString("nombre"))
                    listGeneral.gramosAIngerir.add(resp.getJSONObject(i).getDouble("gramosAIngerir"))
                    listGeneral.composicion.add(resp.getJSONObject(i).getString("composicion"))
                    listGeneral.usadoPara.add(resp.getJSONObject(i).getString("usadoPara"))
                    listGeneral.fechaCaducidad.add(resp.getJSONObject(i).getString("fechaCaducidad"))
                    listGeneral.numeroPastillas.add(resp.getJSONObject(i).getInt("numeroPastillas"))
                    listGeneral.pacienteId.add(resp.getJSONObject(i).getInt("pacienteId"))

                }

                medicamento_recyclerView.layoutManager = LinearLayoutManager(this)
                medicamento_recyclerView.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator?

                medicamento_recyclerView.adapter = MedicamentoAdapter(listGeneral, this,medicamento_recyclerView)
                registerForContextMenu(medicamento_recyclerView)
                MedicamentoAdapter(listGeneral, this,medicamento_recyclerView).notifyDataSetChanged()
                Log.i("http", "DatosB: ${listGeneral.nombre.size}")

            }
    }

    override fun onPause(){
        super.onPause()
        listGeneral.idMed.clear()
        listGeneral.gramosAIngerir.clear()
        listGeneral.nombre.clear()
        listGeneral.composicion.clear()
        listGeneral.usadoPara.clear()
        listGeneral.fechaCaducidad.clear()
        listGeneral.numeroPastillas.clear()
        listGeneral.pacienteId.clear()
    }




    class MedicamentoAdapter(private val listaMedicamentos : listadeMedicamentos,
                             private val contexto: ListaMedicamentosActivity,
                             private val recyclerView: RecyclerView
    ) : RecyclerView.Adapter<MedicamentoAdapter.MyViewHolder>() {
        val urlGen = "http://172.31.105.210:1337/Medicamento"
        inner class MyViewHolder (view: View) : RecyclerView.ViewHolder(view) {
            val MedicamentoNombreInd = view.text_nombre_medicamento
            val MedicamentoGramosInd = view.text_gramosAIngerir
            val MedicamentoComposicionInd = view.text_composicion
            val MedicamentoUsadoParaInd = view.text_usadoPara
            val MedicamentoFechaCaduInd = view.text_fechaCaducidad
            val MedicamentoNPastillasInd = view.text_numeroPastillas

            val layoutRefe = view.findViewById(R.id.relative_medicamento_layout) as RelativeLayout
            init{

                val layout = view.findViewById(R.id.relative_medicamento_layout) as RelativeLayout

                layout
                    .setOnClickListener {
                        val nombreActual = it.findViewById(R.id.text_nombre_medicamento) as TextView

                        Log.i("recycler-view",
                            "El nombre actual es: ${nombreActual.text}")

                    }

            }



        }


        override fun getItemCount(): Int {
            return listaMedicamentos.nombre.size
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.medicamento_item,
                    parent,
                    false
                )
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val arregloLenght = listaMedicamentos.nombre[position]
            var identificador = listaMedicamentos.idMed.get(position)

            holder.MedicamentoNombreInd?.text =listadeMedicamentos.nombre.get(position)
            holder.MedicamentoGramosInd?.text =listadeMedicamentos.gramosAIngerir.get(position).toString()
            holder.MedicamentoComposicionInd?.text =listadeMedicamentos.composicion.get(position)
            holder.MedicamentoUsadoParaInd?.text =listadeMedicamentos.usadoPara.get(position)
            holder.MedicamentoFechaCaduInd?.text =listadeMedicamentos.fechaCaducidad.get(position)
            holder.MedicamentoNPastillasInd?.text =listadeMedicamentos.numeroPastillas.get(position).toString()

            holder.layoutRefe.setOnClickListener {
                var popUp = PopupMenu(contexto,holder.MedicamentoNombreInd)
                popUp.inflate(R.menu.option_menu)

                popUp.setOnMenuItemClickListener {
                        item -> when (item.itemId){
                    R.id.medi_menu_editar->{
                        Log.i("Menu","${identificador}")
                        var nombreMed = listadeMedicamentos.nombre.get(position)
                        var gramosaingerir = listadeMedicamentos.gramosAIngerir.get(position)
                        var composicionMed = listadeMedicamentos.composicion.get(position)
                        var usadoParaMed = listadeMedicamentos.usadoPara.get(position)
                        var fechacaduMed = listadeMedicamentos.fechaCaducidad.get(position)
                        var numeroPastillas = listadeMedicamentos.numeroPastillas.get(position)
                        var MedIdPaciente = listadeMedicamentos.idMed.get(position)


                        var medicamentoEditar = Medicamento(gramosaingerir,
                            nombreMed,
                            composicionMed,
                            usadoParaMed,
                            fechacaduMed,
                            numeroPastillas,
                            MedIdPaciente
                            )
                        contexto.limp()
                        val intentEditar = Intent(contexto,
                            CrearMedicamenteActivity::class.java
                        )
                        intentEditar.putExtra("medicamento",medicamentoEditar)
//                        intentEditar.putExtra("nombre", nombre)
//                        intentEditar.putExtra("apellido", apellido)
//                        intentEditar.putExtra("fechaNac", fechaNac)
//                        intentEditar.putExtra("seguro", seguroTie)

                        intentEditar.putExtra("idPaciente",MedIdPaciente)
                        Log.i("contenido","${medicamentoEditar}")
                        startActivity(contexto,intentEditar,null)

                        //Log.i("contenido","${pacienteEditar}")
                        true
                    }
                    R.id.medi_menu_eliminar->{
                        val parametros = listOf("id" to identificador)
                        urlGen
                            .httpDelete(parametros)
                            .responseString { request, response, result ->
                                when (result) {
                                    is Result.Failure -> {
                                        val error = result.getException()
                                        Log.i("Eliminar", error.toString())


                                    }
                                    is Result.Success -> run {
                                        val data = result.get()
                                        Log.i("eliminar", data)
                                        contexto.cargarDatos()

                                    }
                                }
                            }
                        true
                    }
                    R.id.medi_menu_compartir->{
//
                        val correo = listadeMedicamentos.nombre.get(position)+"@epn.edu.ec"
                        val subject = "Correo de ejemplo"
                        val texto = "Mi cumple: "+listadeMedicamentos.fechaCaducidad.get(position)
                        val intent = Intent(Intent.ACTION_SEND)

                        intent.type = "text/html"

                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(correo))
                        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                        intent.putExtra(Intent.EXTRA_TEXT, texto)

                        startActivity(contexto,intent,null)

                        true
                    }
                    else -> false
                }
                }
                popUp.show()
            }

        }


    }

    fun limp(){
        listadeMedicamentos.gramosAIngerir.clear()
        listadeMedicamentos.nombre.clear()
        listadeMedicamentos.composicion.clear()
        listadeMedicamentos.usadoPara.clear()
        listadeMedicamentos.fechaCaducidad.clear()
        listadeMedicamentos.numeroPastillas.clear()
        listadeMedicamentos.pacienteId.clear()
    }
    companion object listadeMedicamentos{
        val idMed = mutableListOf<Int>()
        val gramosAIngerir = mutableListOf<Double>()
        val nombre = mutableListOf<String>()
        val composicion = mutableListOf<String>()
        val usadoPara = mutableListOf<String>()
        val fechaCaducidad = mutableListOf<String>()
        val numeroPastillas = mutableListOf<Int>()
        val pacienteId = mutableListOf<Int>()


//        fun añadirIdMed(ids: Int){
//            this.idMed.add(ids)
//        }
        fun añadirgramosAIngerir(gramos: Double){
            this.gramosAIngerir.add(gramos)
        }

        fun añadirNombre(nombre: String){
            this.nombre.add(nombre)
        }

        fun añadirComposicion(composicion: String){
            this.composicion.add(composicion)
        }

        fun añadirUso(uso:String){
            this.usadoPara.add(uso)
        }

        fun añadirFechaCaducidad(fecha: String){
            this.fechaCaducidad.add(fecha)
        }
        fun añadirNumeroPastillas(numeroPastillas: Int){
            this.numeroPastillas.add(numeroPastillas)
        }
         fun añadirPacienteId(idPaciente: Int){
             this.pacienteId.add(idPaciente)
         }


    }
}
