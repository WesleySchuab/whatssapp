package com.kaisa.whatsapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaisa.whatsapp.databinding.ActivityPerfilBinding
import com.kaisa.whatsapp.utils.exibirMensagem
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }
    private var permissaoCamera = false
    private var permissaoGaleria = false

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val Gerenciadorgaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            binding.imagePerfil.setImageURI(uri)
            uploadImageStorage(uri)
        } else {
            exibirMensagem("Nenhuma imagem selecionada")
        }
    }

    private fun uploadImageStorage(uri: Uri) {

        val idUsuario = firebaseAuth.currentUser?.uid
        if (idUsuario != null) {
            // fotos / usuarios / idUsuario
            storage
                .getReference("fotos")
                .child("usuarios")
                .child(idUsuario)
                .child("perfil.jpg")
                .putFile(uri)
                .addOnSuccessListener { task ->
                    exibirMensagem("sucesso ao fazer upload da imagem")
                    task.metadata?.reference?.downloadUrl?.addOnSuccessListener { url ->
                        exibirMensagem("sucesso ao pegar a url da imagem")
                        val dados = mapOf(
                            "foto" to url.toString()
                        )
                        atualizarDadosPerfil(idUsuario, dados)
                    }
                }.addOnFailureListener {
                    exibirMensagem("erro ao fazer upload da imagem")
                }
        }else{
            exibirMensagem("id do usuário nulo")
        }

    }

    private fun atualizarDadosPerfil(idUsuario: String, dados: Map<String, String>){
        // Atualizar os dados do usuário no Firestore
        firestore
            .collection("usuarios")
            .document(idUsuario)
            .update(dados)
            .addOnSuccessListener {
                exibirMensagem("Dados atualizados com sucesso")
            }.addOnFailureListener {
                exibirMensagem("Erro ao atualizar dados")
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        inicialisarToolbar()
        solicitarPermissoes()
        inicialiarEventosClique()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        recuperarDadosIniciaisUsuario()
    }

    private fun recuperarDadosIniciaisUsuario() {
        val idUsuario = firebaseAuth.currentUser?.uid
        if (idUsuario != null) {
            firestore
                .collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val dados = documentSnapshot.data
                    if (dados != null) {
                        val nome = dados["nome"] as String
                        val foto = dados["foto"] as String
                        binding.editNomePerfil.setText(nome)
                        if (foto.isNotEmpty()) {
                            Picasso.get()
                                .load(foto)
                                .into(binding.imagePerfil)
                        }
                    }
                }

        }
    }
        private fun inicialiarEventosClique() {
        binding.fabSelecionar.setOnClickListener {
            if (permissaoGaleria) {
                Gerenciadorgaleria.launch("image/*")
            } else {
                solicitarPermissoes()
            }
        }
        binding.btnAtualizarPerfil.setOnClickListener {
            val nomeUsuario = binding.editNomePerfil.text.toString()
            if (nomeUsuario.isNotEmpty()) {
               val idUsuario = firebaseAuth.currentUser?.uid
                if (idUsuario != null) {
                    val dados = mapOf(
                        "nome" to nomeUsuario
                    )
                    atualizarDadosPerfil(idUsuario, dados)
                    binding.editNomePerfil.setText("")
                }else{
                    exibirMensagem("usuario invalido")
                }
            }else{
                exibirMensagem("Digite seu nome")
                binding.textIputNomePerfil.error = "Digite seu nome"
            }
        }
    }

    private fun solicitarPermissoes() {
        // verificar se o usuario ja tem
        permissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        //exibirMensagem("permissao camera"+permissaoCamera)

        permissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
        exibirMensagem("permissao galeria" + permissaoGaleria)


        if (!(permissaoGaleria || permissaoCamera)) {
            exibirMensagem("forneça as permissoes para prosseguir")
            // Solicitar Multiplas Permissoes
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissoes ->
                // Verificar se as permissões foram concedidas
                permissaoCamera = permissoes[Manifest.permission.CAMERA] ?: false
                permissaoGaleria = permissoes[Manifest.permission.READ_MEDIA_IMAGES] ?: false

                // Aqui você pode adicionar lógica para lidar com o resultado das permissões
                /*if (permissaoCamera && permissaoGaleria) {
                    // Todas as permissões foram concedidas
                    exibirMensagem("Todas as permissões foram concedidas")
                } else {
                    // Alguma permissão foi negada
                    exibirMensagem("Você não concedeu as permissões")
                }*/
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