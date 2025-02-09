package com.kaisa.whatsapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kaisa.whatsapp.databinding.ActivityPerfilBinding
import com.kaisa.whatsapp.utils.exibirMensagem

class PerfilActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }
    private var permissaoCamera = false
    private var permissaoGaleria = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        inicialisarToolbar()
        solicitarPermissoes()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun solicitarPermissoes() {
        // verificar se o usuario ja tem
        permissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        permissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

       /* val listaPermissoesNegadas = mutableListOf<String>()
        if (!permissaoCamera) {
            listaPermissoesNegadas.add(Manifest.permission.CAMERA)
        }
        if (!permissaoGaleria) {
            listaPermissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)
        }*/
        var permissao = true
        if (!permissaoCamera) {
            permissao = false
            exibirMensagem("Você não concedeu as permissões de Câ" +
                    "mera")
        }
        if (!permissaoGaleria) {
            permissao = false
        }

        if (permissao) {
            exibirMensagem("Você não concedeu as permissões")
            // Solicitar Multiplas Permissoes
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissoes ->
                // Verificar se as permissões foram concedidas
                permissaoCamera = permissoes[Manifest.permission.CAMERA] ?: false
                permissaoGaleria = permissoes[Manifest.permission.READ_MEDIA_IMAGES] ?: false

                // Aqui você pode adicionar lógica para lidar com o resultado das permissões
                if (permissaoCamera && permissaoGaleria) {
                    // Todas as permissões foram concedidas
                } else {
                    // Alguma permissão foi negada
                }
            }

// Lista de permissões que você deseja solicitar
            val listaPermissoes = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            )

// Solicitar as permissões
            gerenciadorPermissoes.launch(listaPermissoes)
        }
    }



private fun inicialisarToolbar() {
    val toolbar = binding.IncludeToolbarperfil.tbPrincipal
    setSupportActionBar(toolbar)
    supportActionBar?.apply {
        title = "Editar Perfil"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
}