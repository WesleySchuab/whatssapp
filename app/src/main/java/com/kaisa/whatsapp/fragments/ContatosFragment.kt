package com.kaisa.whatsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaisa.whatsapp.R
import com.kaisa.whatsapp.databinding.FragmentContatosBinding
import com.kaisa.whatsapp.model.Usuario

class ContatosFragment : Fragment() {
    private lateinit var binding: FragmentContatosBinding
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
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_contatos, container, false)
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {
        firestore.collection("usuarios")
            .addSnapshotListener { querySnapshot, erro ->
                val documentos = querySnapshot?.documents
                documentos?.forEach { documento ->
                    val usuario = documento.toObject(Usuario::class.java)
                    if (usuario != null) {
                        Log.i("ContatosFragment", "adicionarListenerContatos: ${usuario.nome} ")
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}