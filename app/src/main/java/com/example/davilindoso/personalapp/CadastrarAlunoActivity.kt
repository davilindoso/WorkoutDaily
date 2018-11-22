package com.example.davilindoso.personalapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CadastrarAlunoActivity : AppCompatActivity() {
    private lateinit var txtName: EditText
    private lateinit var txtIdade: EditText
    private lateinit var txtAltura: EditText
    private lateinit var txtPeso: EditText
    private lateinit var txtCpf: EditText
    private lateinit var txtTelefone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_aluno)
        inicializarComponentes()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        dbReference = database.reference.child("user")

    }

    fun inicializarComponentes() {
        txtName = findViewById(R.id.txtName)
        txtIdade = findViewById(R.id.txtIdade)
        txtAltura = findViewById(R.id.txtAltura)
        txtPeso = findViewById(R.id.txtPeso)
        txtCpf = findViewById(R.id.txtCpf)
        txtTelefone = findViewById(R.id.txtTelefone)
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtSenha)
        progressBar = findViewById(R.id.progressBar)
    }

    fun register(view: View) {
        cadastrarNovoAluno()
    }

    fun cadastrarNovoAluno() {
        val name: String = txtName.text.toString()
        val idade: String = txtIdade.text.toString()
        val altura: String = txtAltura.text.toString()
        val peso: String = txtPeso.text.toString()
        val cpf: String = txtCpf.text.toString()
        val telefone: String = txtTelefone.text.toString()
        val email: String = txtEmail.text.toString()
        val password: String = txtPassword.text.toString()

        var credenciais: Boolean = !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
        var informacoesPessoais: Boolean =
            !TextUtils.isEmpty(name) && !TextUtils.isEmpty(idade) && !TextUtils.isEmpty(altura)
                    && !TextUtils.isEmpty(peso) && !TextUtils.isEmpty(cpf) && !TextUtils.isEmpty(telefone)
        if (credenciais && informacoesPessoais) {
            progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    val user: FirebaseUser? = auth.currentUser
                    if (task.isComplete) {
                        val userDB = dbReference.child(user?.uid.toString())
                        userDB.child("name").setValue(name)
                        userDB.child("idade").setValue(idade)
                        userDB.child("altura").setValue(altura)
                        userDB.child("peso").setValue(peso)
                        userDB.child("cpf").setValue(cpf)
                        userDB.child("telefone").setValue(telefone)
                        userDB.child("email").setValue(email)
                        userDB.child("password").setValue(password)

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user!!.updateProfile(profileUpdates)
                    }

                }
        }
    }
}