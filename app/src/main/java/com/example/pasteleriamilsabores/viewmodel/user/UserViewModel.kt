package com.example.pasteleriamilsabores.viewmodel.user



import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.pasteleriamilsabores.model.UserLocalModel

class UserViewModel(context: Context) : ViewModel() {

    private val model = UserLocalModel(context)

    fun guardar(nombre: String, correo: String, telefono: String) {
        model.guardarUsuario(nombre, correo, telefono)
    }

    fun leer(): Map<String, String> {
        return model.obtenerUsuario()
    }
}
