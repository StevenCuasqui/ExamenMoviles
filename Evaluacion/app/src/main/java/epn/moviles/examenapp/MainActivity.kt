package epn.moviles.examenapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_crear_paciente.setOnClickListener { irACrearPaciente() }
        button_lista_paciente.setOnClickListener { irPantallaListaPacientes() }
    }



    fun irPantallaListaPacientes(){
        val intentIrAListaPacientes = Intent(this , ListaPacienteActivity::class.java)
        this.startActivity(intentIrAListaPacientes)
    }

    fun irACrearPaciente(){
        val intentIrACrearPaciente = Intent(this , CrearPacienteActivity::class.java)
        this.startActivity(intentIrACrearPaciente)
    }
}
