package epn.moviles.examenapp

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Paciente( var nombres: String,
              var apellidos: String,
              var fechaNacimiento: Date,
              var tieneSeguro: Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readSerializable() as Date,
        parcel.readSerializable() as Boolean) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombres)
        parcel.writeString(apellidos)
        parcel.writeSerializable(fechaNacimiento)
        parcel.writeSerializable(tieneSeguro)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "${nombres}"
    }

    companion object CREATOR : Parcelable.Creator<Paciente> {
        override fun createFromParcel(parcel: Parcel): Paciente {
            return Paciente(parcel)
        }

        override fun newArray(size: Int): Array<Paciente?> {
            return arrayOfNulls(size)
        }
    }
}