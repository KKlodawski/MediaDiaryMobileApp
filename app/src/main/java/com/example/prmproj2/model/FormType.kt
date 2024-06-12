package com.example.prmproj2.model

import java.io.Serializable

sealed class FormType : Serializable{
    data object New : FormType()
    data class Edit(val id: Int): FormType()
}