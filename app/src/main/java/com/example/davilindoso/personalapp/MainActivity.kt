package com.example.davilindoso.personalapp

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.davilindoso.personalapp.controller.aluno.AlunosFragment
import com.example.davilindoso.personalapp.controller.exercicio.ExerciciosFragment
import com.example.davilindoso.personalapp.model.vo.Aluno
import com.example.davilindoso.personalapp.model.vo.Professor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private var nav_user: TextView? = null
    private var menuSelecionado: Int? = null

    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var ficha: String
    private lateinit var uidProfessor: String
    private lateinit var view_aluno: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.workout_daily)
        setSupportActionBar(toolbar)
        database = FirebaseDatabase.getInstance()
        recuperarDadosUsuario()
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun recuperarDadosUsuario() {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        var displayName = user!!.displayName
        val emailUser = user!!.email
        var profileUri = user!!.photoUrl
        user!!.providerData.forEach {
            if (displayName == null && it.displayName != null) {
                displayName = it.displayName
            }
            if (profileUri == null && it.photoUrl != null) {
                profileUri = it.photoUrl
            }
        }
        if (displayName != null) {
            val navigationView: NavigationView? = findViewById(R.id.nav_view)
            val hView: View = navigationView!!.getHeaderView(0)
            nav_user = null
            nav_user = hView.findViewById(R.id.userId)
            nav_user!!.setText(displayName)
        } else {
            Toast.makeText(this, "Não foi possível recuperar nome do Usuário", Toast.LENGTH_LONG).show()
        }

        if (emailUser != null) {

            val navigationView: NavigationView? = findViewById(R.id.nav_view)
            val hView: View = navigationView!!.getHeaderView(0)
            nav_user = null
            nav_user = hView.findViewById(R.id.userEmail)
            nav_user!!.setText(emailUser)
        } else {
            Toast.makeText(this, "Não foi possível recuperar email do Usuário", Toast.LENGTH_LONG).show()
        }
        recuperarDadosComplementaresUsuario()
    }

    private fun recuperarDadosComplementaresUsuario() {
        dbReference = database.reference.child("user")
        view_aluno = findViewById(R.id.view_aluno)
        consultarIndicadorProfessorFirebase()

    }

    private fun consultarIndicadorProfessorFirebase() {
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (a in snapshot.children) {
                        val professor = a.children
                        val professores = professor.iterator()
                        while (professores.hasNext()) {
                            val chaveUsuario = professores.next()
                            val filhos = chaveUsuario.children.iterator()
                            while (filhos.hasNext()) {
                                val aluno = filhos.next()
                                aluno.value.toString().split(",").forEach() {
                                    if (it.contains(user!!.uid)) {
                                        view_aluno.visibility = View.INVISIBLE
                                    } else if (it.contains("idProfessor")) {
                                        uidProfessor = it.replace("=", "").replace("idProfessor", "").replace("{", "")
                                            .replace("}", "").trim()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        })
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            voltar()
        }
    }

    fun voltar() {
        when (menuSelecionado) {
            AlunosFragment().id -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
            ExerciciosFragment().id -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
            else -> Toast.makeText(this, "Deslize novamente implementar", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_alunos -> {
                loadAlunosFragment(fragAluno = AlunosFragment())
                menuSelecionado = AlunosFragment().id
                setTitle(R.string.alunos)
            }
            R.id.nav_exercicios -> {
                loadExerciciosFragment(fragExercicio = ExerciciosFragment())
                menuSelecionado = ExerciciosFragment().id
                setTitle(R.string.exercicios)
            }
            R.id.nav_info -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadAlunosFragment(fragAluno: AlunosFragment) {
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.frameLayout, fragAluno)
        fm.commit()
    }

    private fun loadExerciciosFragment(fragExercicio: ExerciciosFragment) {
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.frameLayout, fragExercicio)
        fm.commit()
    }

    fun exibirTreino(view: View) {
        dbReference =
            database.reference.child("user").child(uidProfessor).child("alunos").child(user!!.uid)
                .child("treino")
        ficha = ""
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (a in snapshot.children) {
                        val treino = a.value.toString()
                        val treinoDiario: TextView = findViewById(R.id.treinoDiario)
                        treinoDiario.text = treino
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        })


    }
}
