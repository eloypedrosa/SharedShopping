package com.eloypedrosa.sharedshopping

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth

class MainActivity : AppCompatActivity() {
    private val repo = FirestoreRepository()
    private val auth = Firebase.auth
    private lateinit var adapter: ListsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.rvLists)
        rv.layoutManager = LinearLayoutManager(this)

        // Actualizamos la inicialización del Adapter
        adapter = ListsAdapter(
            onClick = { list ->
                val i = Intent(this, ListActivity::class.java)
                i.putExtra("listId", list.id)
                i.putExtra("listName", list.name)
                startActivity(i)
            },
            onLongClick = { list ->
                showDeleteListDialog(list)
            }
        )
        rv.adapter = adapter

        findViewById<Button>(R.id.fabCreateList).setOnClickListener { showCreateListDialog() }
    }

    // Nueva función para confirmar borrado
    private fun showDeleteListDialog(list: ShoppingList) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar lista")
            .setMessage("¿Estás seguro de que quieres borrar '${list.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                repo.deleteList(list.id) { success ->
                    if (success) {
                        Toast.makeText(this, "Lista eliminada", Toast.LENGTH_SHORT).show()
                        repo.getListsForUser { lists -> runOnUiThread { adapter.submitList(lists) } }
                    } else {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    override fun onResume() {
        super.onResume()
        repo.getListsForUser { lists -> runOnUiThread { adapter.submitList(lists) } }
    }

    private fun showCreateListDialog() {
        val et = EditText(this)
        AlertDialog.Builder(this).setTitle("Crear lista").setView(et)
            .setPositiveButton("Crear") { _, _ ->
                val name = et.text.toString().trim()
                if (name.isNotEmpty()) {
                    repo.createList(name) { id ->
                        if (id != null) repo.getListsForUser { lists -> runOnUiThread { adapter.submitList(lists) } }
                        else Toast.makeText(this,"Error al crear",Toast.LENGTH_SHORT).show()
                    }
                }
            }.setNegativeButton("Cancel", null).show()
    }
}
