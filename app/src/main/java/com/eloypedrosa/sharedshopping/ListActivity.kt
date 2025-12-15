package com.eloypedrosa.sharedshopping

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.bumptech.glide.Glide // Importa Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
class ListActivity : AppCompatActivity() {
    private val repo = FirestoreRepository()
    private lateinit var catAdapter: CategoryAdapter
    private lateinit var prodAdapter: ProductAdapter
    private var listId: String = ""
    private var listName: String = ""
    private val autoMap = mapOf(
        "leche" to Pair("Lácteos", "milk"),
        "yogur" to Pair("Lácteos", "yogurt"),
        "pan" to Pair("Panadería", "bread"),
        "huevo" to Pair("Frutas y verduras", "egg")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Recogemos datos del intent
        listId = intent.getStringExtra("listId") ?: ""
        listName = intent.getStringExtra("listName") ?: ""

        // Asignamos el nombre al nuevo TextView del header
        findViewById<TextView>(R.id.tvListHeaderTitle).text = listName

        // --- LÓGICA DEL MENÚ DESPLEGABLE ---
        val btnMenu = findViewById<ImageButton>(R.id.btnMenuOptions)
        btnMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }

        val fabAddProduct = findViewById<FloatingActionButton>(R.id.fabAddProduct)
        fabAddProduct.setOnClickListener {
            showAddProductDialog()
        }

        title = listName

        val rvCats = findViewById<RecyclerView>(R.id.rvCategories)
        rvCats.layoutManager = LinearLayoutManager(this)
        catAdapter = CategoryAdapter { /* category click -> filter or show products */ }
        rvCats.adapter = catAdapter

        val rvProds = findViewById<RecyclerView>(R.id.rvProducts)
        rvProds.layoutManager = LinearLayoutManager(this)

        // Actualizamos el adapter con la lógica de borrado
        prodAdapter = ProductAdapter(
            mutableListOf(),
            onToggleCompleted = { p, done ->
                repo.toggleProductCompleted(listId, p.id, done)
            },
            onLongClick = { product ->
                showDeleteProductDialog(product)
            }
        )

        rvProds.adapter = prodAdapter

        // listeners
        repo.listenCategories(listId) { cats ->
            runOnUiThread {
                catAdapter.submitList(cats)

                val layout = findViewById<LinearLayout>(R.id.layoutCategories)
                layout.removeAllViews()

                for (c in cats) {
                    val chip = TextView(this)
                    chip.text = c.name
                    chip.setPadding(30, 12, 30, 12)
                    chip.setBackgroundResource(R.drawable.bg_chip)
                    chip.setTextColor(Color.BLACK)

                    layout.addView(chip)
                }
            }
        }
        repo.listenProducts(listId) { prods -> runOnUiThread { prodAdapter.updateList(prods) } }


        loadSharedUsers()
    }
    private fun loadSharedUsers() {
        repo.listenListDetails(listId) { list ->
            if (list != null) {
                runOnUiThread {
                    // Actualizar título por si cambió
                    findViewById<TextView>(R.id.tvListHeaderTitle).text = list.name

                    // Cargar avatares
                    if (list.sharedWith.isNotEmpty()) {
                        repo.getUsersByIds(list.sharedWith) { users ->
                            displayUserAvatars(users)
                        }
                    }
                }
            }
        }
    }

    private fun displayUserAvatars(users: List<FirestoreRepository.UserData>) {
        val container = findViewById<LinearLayout>(R.id.llSharedProfiles)
        container.removeAllViews() // Limpiar anteriores

        for (user in users) {
            val iv = ShapeableImageView(this)
            val size = (32 * resources.displayMetrics.density).toInt() // 32dp
            val margin = (4 * resources.displayMetrics.density).toInt() // 4dp

            val params = LinearLayout.LayoutParams(size, size)
            params.marginEnd = margin
            iv.layoutParams = params

            // Hacerlo circular
            iv.shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCornerSizes(ShapeAppearanceModel.PILL)
                .build()

            // Color de fondo por si no hay imagen
            iv.setBackgroundColor(Color.LTGRAY)
            iv.scaleType = ImageView.ScaleType.CENTER_CROP

            // Cargar imagen con Glide
            if (!user.photoUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(user.photoUrl)
                    .circleCrop()
                    .into(iv)
            } else {
                iv.setImageResource(android.R.drawable.sym_def_app_icon)
            }

            container.addView(iv)
        }
    }
    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_list_actions, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_add_category -> {
                    showAddCategoryDialog()
                    true
                }
                R.id.action_share -> {
                    showShareDialog()
                    true
                }
                R.id.action_delete_list -> {
                    confirmDeleteList()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun confirmDeleteList() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar lista")
            .setMessage("¿Seguro que quieres borrar esta lista y salir?")
            .setPositiveButton("Eliminar") { _, _ ->
                repo.deleteList(listId) { success ->
                    if (success) finish() // Cerramos la actividad y volvemos al Main
                    else Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null).show()
    }
    private fun showDeleteProductDialog(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage("¿Borrar '${product.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                repo.deleteProduct(listId, product.id) { success ->
                    if (!success) {
                        runOnUiThread { Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show() }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    private fun showAddCategoryDialog() {
        val et = EditText(this)
        AlertDialog.Builder(this).setTitle("Añadir categoría").setView(et)
            .setPositiveButton("Añadir") { _, _ ->
                val name = et.text.toString().trim()
                if (name.isNotEmpty()) repo.addCategory(listId, name) { success -> if (!success) Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show() }
            }
            .setNegativeButton("Cancelar", null).show()
    }
    private fun showAddProductDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null)
        val etName = view.findViewById<EditText>(R.id.etProductName)
        val etQty = view.findViewById<EditText>(R.id.etProductQty)
        val spCategory = view.findViewById<Spinner>(R.id.spCategory)

        // fill spinner with categories snapshot
        val cats = (catAdapter.currentList())
        val names = cats.map { it.name }
        spCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, names)

        AlertDialog.Builder(this).setTitle("Añadir producto").setView(view)
            .setPositiveButton("Añadir") { _, _ ->
                var name = etName.text.toString().trim()
                if (name.isEmpty()) { Toast.makeText(this,"Nombre obligatorio",Toast.LENGTH_SHORT).show(); return@setPositiveButton }
                val lc = name.lowercase()
                var chosenCategoryId = if (cats.isNotEmpty()) cats[0].id else ""
                var chosenIcon: String? = null
                for ((k,v) in autoMap) {
                    if (lc.contains(k)) {
                        val match = cats.firstOrNull { it.name.equals(v.first, ignoreCase = true) }
                        if (match != null) chosenCategoryId = match.id
                        chosenIcon = v.second
                        break
                    }
                }
                if (chosenIcon == null && cats.isNotEmpty()) {
                    val idx = spCategory.selectedItemPosition
                    if (idx >= 0) chosenCategoryId = cats[idx].id
                }
                val product = Product(name = name, categoryId = chosenCategoryId, quantity = etQty.text.toString(), icon = chosenIcon)
                repo.addProduct(listId, product) { success -> if (!success) runOnUiThread { Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show() } }
            }.setNegativeButton("Cancel", null).show()
    }

    private fun showShareDialog() {
        val et = EditText(this)
        et.hint = "Email del colaborador"
        AlertDialog.Builder(this).setTitle("Compartir lista").setView(et)
            .setPositiveButton("Compartir") { _, _ ->
                val email = et.text.toString().trim()
                if (email.isNotEmpty()) {
                    repo.resolveUidByEmail(email) { uid ->
                        runOnUiThread {
                            if (uid != null) {
                                repo.shareListWith(listId, uid) { ok ->
                                    if (!ok) Toast.makeText(this,"Error compartiendo", Toast.LENGTH_SHORT).show()
                                    else Toast.makeText(this,"¡Lista compartida!", Toast.LENGTH_SHORT).show()
                                }
                            } else Toast.makeText(this,"No se ha encontrado usuario con este email", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.setNegativeButton("Cancelar", null).show()
    }}
