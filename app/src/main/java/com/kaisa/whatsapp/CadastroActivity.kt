package com.kaisa.whatsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.kaisa.whatsapp.databinding.ActivityCadastroBinding
import com.kaisa.whatsapp.model.Usuario
import com.kaisa.whatsapp.utils.exibirMensagem

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }
    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        inicializarToolbar()
        inicializarEventosClique()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editnome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbarCadastro.tbPrincipal
        setSupportActionBar(toolbar)
        title = "Faça seu cadastro"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun inicializarEventosClique() {
        binding.btnCadastrar.setOnClickListener {
            if (validarCampos()) {
                cadastrarUsuario(nome, email, senha)
            }

        }
    }

    private fun validarCampos(): Boolean {
        var retorno = true
        nome = binding.editnome.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editSenha.text.toString()
        if (nome.isNotEmpty()) {
            binding.textInputLayoutNome.error = null

            if (email.isNotEmpty()) {
                binding.textInputLayoutEmail.error = null

                if (senha.isNotEmpty()) {
                    binding.editSenha.error = null
                    retorno = true
                } else {
                    binding.textInputLayoutSenha.error = "Digite sua senha"
                    retorno = false
                }
            } else {
                binding.textInputLayoutEmail.error = "Digite seu e-mail"
                retorno = false
            }
        } else {
            binding.textInputLayoutNome.error = "Digite seu nome"
            retorno = false
        }
        return retorno
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { resultado ->
                if (resultado.isSuccessful) {
                    val id = resultado.result.user?.uid
                    if (id != null) {
                        val usuario  = Usuario(id, nome, email)
                        salvarUsuario(usuario)
                    }
                }

            }.addOnFailureListener { erro ->
                try {
                    throw erro
                }catch(erroSenhaFrca: FirebaseAuthWeakPasswordException) {
                    erroSenhaFrca.printStackTrace()
                    exibirMensagem("Digite uma senha msi forte, com nunmeros e letras")
                }catch(erroUsuarioExistente: FirebaseAuthUserCollisionException) {
                    erroUsuarioExistente.printStackTrace()
                    exibirMensagem("E-mail foi utilizado para cadastro anterior digite um outro e-mail")
                }catch(erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                    erroCredenciaisInvalidas.printStackTrace()
                    exibirMensagem("E-mail inválido digite um outro e-mail")
                }
            }
    }

    private fun salvarUsuario(usuario: Usuario) {
        firestore.collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {
                exibirMensagem("Cadastro realizado com sucesso")
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }.addOnFailureListener{
                exibirMensagem("Erro ao realizar cadastro sucesso")
            }
    }
}