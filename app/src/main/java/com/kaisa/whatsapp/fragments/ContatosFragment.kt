package com.kaisa.whatsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.kaisa.whatsapp.R
import com.kaisa.whatsapp.databinding.FragmentContatosBinding
import com.kaisa.whatsapp.model.Usuario

class ContatosFragment : Fragment() {
    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContatosBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {
        eventoSnapshot = firestore.collection("usuarios")
            .addSnapshotListener { querySnapshot, erro ->
                val listaContatos = mutableListOf<Usuario>()
                val documentos = querySnapshot?.documents
                documentos?.forEach { documento ->
                    val usuario = documento.toObject(Usuario::class.java)
                    if (usuario != null) {
                        val idUsuarioLogado = firebaseAuth.currentUser?.uid
                        if (idUsuarioLogado != null) {
                            if (idUsuarioLogado != usuario.id) {
                                listaContatos.add(usuario)
                            }
                        }
                     }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }

}