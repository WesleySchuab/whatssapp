package com.kaisa.whatsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kaisa.whatsapp.databinding.ActivityCadastroBinding

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }
    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String
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

    private fun inicializarEventosClique() {
        binding.btnCadastrar.setOnClickListener {
            if (validarCampos()) {
                startActivity(
                    Intent(this, LoginActivity::class.java)
                )
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

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        title = "Faça seu cadastro"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}