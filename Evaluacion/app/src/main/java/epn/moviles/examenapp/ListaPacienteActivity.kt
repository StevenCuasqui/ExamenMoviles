package epn.moviles.examenapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.*
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import epn.moviles.examenapp.ListaPacienteActivity.PacienteAdapter.MyViewHolder
import kotlinx.android.synthetic.main.activity_lista_paciente.*
import kotlinx.android.synthetic.main.paciente_item.view.*
import org.json.JSONArray


class ListaPacienteActivity : AppCompatActivity() {

//    val nombresPacientes: ArrayList<String> = ArrayList()
//    val apellidosPacientes: ArrayList<String> = ArrayList()
    val listGeneral = listadePacientes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_paciente)
        cargarDatos()



    }
    fun cargarDatos(){
        listGeneral.nombres.clear()
        listGeneral.fechas.clear()
        listGeneral.fechas.clear()
        listGeneral.ids.clear()
        val url = "http://192.168.100.8:1337/Paciente"
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
                        Log.i("http", "Datos: ${aux}")
                        Log.i("Tipo", "${aux::class.simpleName}")
//
                    }
                }

                var resp= result.get().array()
                for (i in 0 until aux.length()) {
                    listGeneral.nombres.add(resp.getJSONObject(i).getString("nombres"))
                    listGeneral.apellidos.add(resp.getJSONObject(i).getString("apellidos"))
                    listGeneral.fechas.add(resp.getJSONObject(i).getString("fechaNacimiento"))
                    listGeneral.ids.add(resp.getJSONObject(i).getInt("id"))
                    listGeneral.seguros.add(resp.getJSONObject(i).getBoolean("tieneSeguro"))
                }
                paciente_recyclerView.layoutManager = LinearLayoutManager(this)
                paciente_recyclerView.itemAnimator = DefaultItemAnimator()
                paciente_recyclerView.adapter = PacienteAdapter(listGeneral, this,paciente_recyclerView)
                registerForContextMenu(paciente_recyclerView)
                PacienteAdapter(listGeneral, this,paciente_recyclerView).notifyDataSetChanged()
                Log.i("http", "DatosB: ${listGeneral.nombres.size}")

            }
    }

    override fun onPause(){
        super.onPause()
        listGeneral.nombres.clear()
        listGeneral.fechas.clear()
        listGeneral.fechas.clear()
        listGeneral.ids.clear()
        listGeneral.seguros.clear()
        }




    class PacienteAdapter(private val listaPacientes : listadePacientes,
                          private val contexto: ListaPacienteActivity,
                          private val recyclerView: RecyclerView) : RecyclerView.Adapter<MyViewHolder>() {
        val urlGen = "http://192.168.100.8:1337/Paciente"
        inner class MyViewHolder (view: View) : RecyclerView.ViewHolder(view) {

            val PacienteNombreIndividual = view.text_paciente_individual
            val PacienteApellidoIndividual = view.text_paciente_apellido
            val PacienteFechaIndividual = view.text_fechanacimiento


            val layoutRef = view.findViewById(R.id.relative_layout) as RelativeLayout
            init{

                val layout = view.findViewById(R.id.relative_layout) as RelativeLayout

                layout
                    .setOnClickListener {
                        val nombreActual = it.findViewById(R.id.text_paciente_individual) as TextView

                        Log.i("recycler-view",
                            "El nombre actual es: ${nombreActual.text}")

                    }

            }



        }


        override fun getItemCount(): Int {
            return listaPacientes.nombres.size
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.paciente_item,
                    parent,
                    false
                )
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val arregloLenght = listaPacientes.nombres[position]
            var identificador = listadePacientes.ids.get(position)
            holder.PacienteNombreIndividual?.text =listadePacientes.nombres.get(position)
            holder.PacienteApellidoIndividual?.text = listadePacientes.apellidos.get(position)
            holder.PacienteFechaIndividual?.text =listadePacientes.fechas.get(position).toString()


            holder.layoutRef.setOnClickListener {
                var popUp = PopupMenu(contexto,holder.PacienteNombreIndividual)
                popUp.inflate(R.menu.option_menu)

                popUp.setOnMenuItemClickListener {
                    item -> when (item.itemId){
                    R.id.item_menu_editar->{
                        Log.i("Menu","${identificador}")
                        var nombre = listadePacientes.nombres.get(position)
                        var apellido = listadePacientes.apellidos.get(position)
                        var fechaNac = listadePacientes.fechas.get(position)
                        var seguroTie = listadePacientes.seguros.get(position)
                        var identi = listadePacientes.ids.get(position)

                        var pacienteEditar = Paciente(nombre,
                            apellido,
                            fechaNac,
                            seguroTie)
                        contexto.limp()
                        val intentEditar = Intent(contexto,
                            EPacienteActivity::class.java
                        )
                        intentEditar.putExtra("paciente",pacienteEditar)
//                        intentEditar.putExtra("nombre", nombre)
//                        intentEditar.putExtra("apellido", apellido)
//                        intentEditar.putExtra("fechaNac", fechaNac)
//                        intentEditar.putExtra("seguro", seguroTie)

                        intentEditar.putExtra("id",identi)
                        Log.i("contenido","${pacienteEditar}")
                        startActivity(contexto,intentEditar,null)

                        //Log.i("contenido","${pacienteEditar}")
                        true
                    }
                    R.id.item_menu_eliminar->{
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
                    R.id.item_menu_listar_medicamentos->{
                        val intentMedicamentos = Intent(contexto,ListaMedicamentosActivity::class.java)
                        intentMedicamentos.putExtra("idPaciente",identificador)
                        startActivity(contexto,intentMedicamentos,null)
                        true
                    }
                    R.id.item_menu_compartir->{
//
                        val correo = listadePacientes.nombres.get(position)+"@epn.edu.ec"
                        val subject = "Correo de ejemplo"
                        val texto = "Mi cumple: "+listadePacientes.fechas.get(position)
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

//    fun llenarListView() {
//        val adapter = ArrayAdapter<String>(
//            this,
//            android.R.layout.simple_list_item_1
//        )
//
//        list_view_context_menu.adapter = adapter
//
//        // Registrar para que sirvan los menus contextuales
//
//        registerForContextMenu(list_view_context_menu)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.option_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle item selection
//        when (item.getItemId()) {
//            R.id.abrir -> {
//                Log.i("menu", "Abrir")
//                return true
//            }
//            R.id.nuevo -> {
//                Log.i("menu", "Nuevo")
//                return true
//            }
//            R.id.editar -> {
//                Log.i("menu", "Editar")
//                return true
//            }
//            R.id.eliminar -> {
//                Log.i("menu", "Eliminar")
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun onCreateContextMenu(menu: ContextMenu?,
//        v: View?,
//        menuInfo:ContextMenu.ContextMenuInfo?) {
//        super.onCreateContextMenu(menu, v, menuInfo)
//
//        val inflater = menuInflater
//        inflater.inflate(R.menu.option_menu,menu)
//
//
//    }
fun limp(){
    listadePacientes.nombres.clear()
    listadePacientes.seguros.clear()
    listadePacientes.ids.clear()
    listadePacientes.fechas.clear()
    listadePacientes.apellidos.clear()
}
    companion object listadePacientes{
        val ids = mutableListOf<Int>()
        val nombres = mutableListOf<String>()
        val apellidos = mutableListOf<String>()
        val fechas= mutableListOf<String>()
        val seguros = mutableListOf<Boolean>()

        fun añadirNombre(name: String){
            nombres.add(name)
        }

        fun añadirApellido(apellido: String){
            apellidos.add(apellido)
        }

        fun añadirNacimiento(fecha: String){
            fechas.add(fecha)
        }

        fun añadirId(id: Int){
            ids.add(id)
        }

    }



}
