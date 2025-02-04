package com.kaisa.whatsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.kaisa.whatsapp.databinding.ActivityLoginBinding
import com.kaisa.whatsapp.utils.exibirMensagem

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)

    }
    private lateinit var email: String
    private lateinit var senha: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        inicializarEventosClique()
        firebaseAuth.signOut()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editnome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun inicializarEventosClique() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this, CadastroActivity::class.java)
            )
        }
        binding.btnLogar.setOnClickListener {
            if (validarCampos()) {
                logarUsuario()

            }
        }
    }


    private fun verificarUsuarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if (usuarioAtual != null) {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }


    private fun logarUsuario() {
        firebaseAuth.signInWithEmailAndPassword(email, senha).addOnSuccessListener {
            exibirMensagem("Login realizado com sucesso")
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }.addOnFailureListener { erro ->
            try {
                throw erro

            } catch (erroUsuarioInvalido: FirebaseAuthInvalidCredentialsException) {
                erroUsuarioInvalido.printStackTrace()
                exibirMensagem("Senha incorreta")
            } catch (erroSenhaErrada: FirebaseAuthInvalidCredentialsException) {
                erroSenhaErrada.printStackTrace()
                exibirMensagem("Senha incorreta")
            }

        }

    }

    private fun validarCampos(): Boolean {
        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()
        var retorno = true
        if (email.isNotEmpty()) {
            binding.editLoginEmail.error = null
            if (senha.isNotEmpty()) {
                binding.editLoginSenha.error = null
                retorno = true
            } else {
                binding.editLoginSenha.error = "Digite sua senha"
            }
        } else {
            binding.editLoginEmail.error = "Digite seu e-mail"
            retorno = false
        }
        return retorno


    }
}



