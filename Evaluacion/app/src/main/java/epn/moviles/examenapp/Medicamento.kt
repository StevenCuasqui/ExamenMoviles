package epn.moviles.examenapp

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Medicamento(var gramosAIngerir: Double,
               var nombre: String,
               var composicion: String,
               var usadoPara: String,
                  var fechaCaducidad: String,
                  var numeroPastillas: Int,
                  public var pacienteId: Int) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(gramosAIngerir)
        parcel.writeString(nombre)
        parcel.writeString(composicion)
        parcel.writeString(usadoPara)
        parcel.writeSerializable(fechaCaducidad)
        parcel.writeInt(numeroPastillas)
        parcel.writeInt(pacienteId)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "${nombre}"
    }

    companion object CREATOR : Parcelable.Creator<Medicamento> {
        override fun createFromParcel(parcel: Parcel): Medicamento {
            return Medicamento(parcel)
        }

        override fun newArray(size: Int): Array<Medicamento?> {
            return arrayOfNulls(size)
        }
    }
}