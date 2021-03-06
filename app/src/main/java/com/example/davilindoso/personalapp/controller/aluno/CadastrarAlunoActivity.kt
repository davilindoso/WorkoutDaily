package com.example.davilindoso.personalapp.controller.aluno

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.davilindoso.personalapp.controller.auth.LoginActivity
import com.example.davilindoso.personalapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

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
    private lateinit var uidProfessor: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_aluno)
        setTitle(R.string.cadastrar_aluno)
        inicializarComponentes()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    private fun inicializarComponentes() {
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

    private fun cadastrarNovoAluno() {
        val name: String = txtName.text.toString()
        val idade: String = txtIdade.text.toString()
        val altura: String = txtAltura.text.toString()
        val peso: String = txtPeso.text.toString()
        val cpf: String = txtCpf.text.toString()
        val telefone: String = txtTelefone.text.toString()
        val email: String = txtEmail.text.toString()
        val password: String = txtPassword.text.toString()

        val credenciais: Boolean = !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
        val informacoesPessoais: Boolean =
            !TextUtils.isEmpty(name) && !TextUtils.isEmpty(idade) && !TextUtils.isEmpty(altura)
                    && !TextUtils.isEmpty(peso) && !TextUtils.isEmpty(cpf) && !TextUtils.isEmpty(telefone)
        if (credenciais && informacoesPessoais) {
            progressBar.visibility = View.VISIBLE

            var user: FirebaseUser? = auth.currentUser
            uidProfessor = user!!.uid
            dbReference = database.reference.child("user").child(user.uid).child("alunos")

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isComplete) {
                        user = auth.currentUser
                        verifyEmail(user)

                        val userDB = dbReference.child(user?.uid.toString())

                        userDB.child("name").setValue(name)
                        userDB.child("idade").setValue(idade)
                        userDB.child("altura").setValue(altura)
                        userDB.child("peso").setValue(peso)
                        userDB.child("telefone").setValue(telefone)
                        userDB.child("cpf").setValue(cpf)
                        userDB.child("email").setValue(email)
                        userDB.child("perfilProfessor").setValue(false.toString())
                        userDB.child("idProfessor").setValue(uidProfessor)

                        //val dbReferenceAlunos = database.reference.child("alunos")
                        //val alunoDB = dbReferenceAlunos.child(user?.uid.toString())
                        //alunoDB.child("name").setValue(name)
                        //alunoDB.child("idade").setValue(idade)
                        //alunoDB.child("altura").setValue(altura)
                        // alunoDB.child("peso").setValue(peso)
                        //alunoDB.child("telefone").setValue(telefone)
                        //alunoDB.child("cpf").setValue(cpf)
                        //alunoDB.child("email").setValue(email)
                        //alunoDB.child("perfilProfessor").setValue(false)
                        //alunoDB.child("idProfessor").setValue(uidProfessor)
                        auth.signOut()
                        Toast.makeText(
                            this,
                            "Cadastro do aluno efetuado com sucesso. Necesário se reconectar",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                }
        }
    }

    private fun verifyEmail(user: FirebaseUser?) {
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this) { task ->

                if (task.isComplete) {
                    Toast.makeText(this, "Email enviado", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Erro ao enviar email", Toast.LENGTH_LONG).show()
                }
            }
    }

}