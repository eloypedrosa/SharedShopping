package com.eloypedrosa.sharedshopping

import com.google.firebase.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    // Create users mapping at sign-in
    fun ensureUserDocument(onComplete: (Boolean) -> Unit) {
        val u = auth.currentUser ?: return onComplete(false)
        val doc = firestore.collection("users").document(u.uid)
        val data = mapOf("email" to u.email, "displayName" to u.displayName)
        doc.set(data, SetOptions.merge()).addOnSuccessListener { onComplete(true) }.addOnFailureListener { onComplete(false) }
    }

    // Create list
    fun createList(name: String, cb: (String?) -> Unit) {
        val uid = auth.currentUser!!.uid
        val docRef = firestore.collection("shoppingLists").document()
        val list = ShoppingList(id = docRef.id, name = name, ownerUid = uid, ownerEmail = auth.currentUser?.email ?: "", createdAt = Timestamp.now())
        docRef.set(list).addOnSuccessListener { cb(docRef.id) }.addOnFailureListener { cb(null) }
    }

    fun getListsForUser(onComplete: (List<ShoppingList>) -> Unit) {
        val uid = auth.currentUser!!.uid
        val col = firestore.collection("shoppingLists")
        // owner
        col.whereEqualTo("ownerUid", uid).get().addOnSuccessListener { own ->
            col.whereArrayContains("sharedWith", uid).get().addOnSuccessListener { shared ->
                val lists = mutableListOf<ShoppingList>()
                own.documents.forEach { lists.add(it.toObject(ShoppingList::class.java)!!.apply { id = it.id }) }
                shared.documents.forEach { val s = it.toObject(ShoppingList::class.java)!!.apply { id = it.id }; if (!lists.any{it.id==s.id}) lists.add(s) }
                onComplete(lists)
            }
        }
    }

    fun addCategory(listId: String, name: String, cb: (Boolean)->Unit) {
        val col = firestore.collection("shoppingLists").document(listId).collection("categories")
        val doc = col.document()
        val cat = Category(id = doc.id, name = name, order = System.currentTimeMillis())
        doc.set(cat).addOnSuccessListener { cb(true) }.addOnFailureListener { cb(false) }
    }

    fun addProduct(listId: String, product: Product, cb: (Boolean)->Unit) {
        val doc = firestore.collection("shoppingLists").document(listId).collection("products").document()
        val p = product.copy(id = doc.id, createdByUid = auth.currentUser?.uid, createdAt = Timestamp.now())
        doc.set(p).addOnSuccessListener { cb(true) }.addOnFailureListener { cb(false) }
    }

    fun toggleProductCompleted(listId: String, productId: String, completed: Boolean) {
        firestore.collection("shoppingLists").document(listId).collection("products").document(productId)
            .update("completed", completed)
    }

    fun shareListWith(listId: String, userUid: String, cb: (Boolean)->Unit) {
        firestore.collection("shoppingLists").document(listId).update("sharedWith", FieldValue.arrayUnion(userUid))
            .addOnSuccessListener { cb(true) }.addOnFailureListener { cb(false) }
    }

    fun listenCategories(listId: String, onChange: (List<Category>)->Unit) =
        firestore.collection("shoppingLists").document(listId).collection("categories").orderBy("order")
            .addSnapshotListener { snaps, _ ->
                val cats = snaps?.documents?.map {
                    it.toObject(Category::class.java)!!.apply { id = it.id }
                } ?: emptyList()
                onChange(cats)
            }

    fun listenProducts(listId: String, onChange: (List<Product>)->Unit) =
        firestore.collection("shoppingLists").document(listId).collection("products")
            .addSnapshotListener { snaps, _ ->
                val prods = snaps?.documents?.map {
                    it.toObject(Product::class.java)!!.apply { id = it.id }
                } ?: emptyList()
                onChange(prods)
            }

    fun getUsersByIds(uids: List<String>, onComplete: (List<UserData>) -> Unit) {
        if (uids.isEmpty()) {
            onComplete(emptyList())
            return
        }
        // Firestore "whereIn" soporta máximo 10 elementos.
        // Si esperas más, tendrás que hacer lotes, pero para este ejemplo simple:
        firestore.collection("users")
            .whereIn(com.google.firebase.firestore.FieldPath.documentId(), uids.take(10))
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.map { doc ->
                    // Asumimos que guardas photoUrl en el documento del usuario
                    UserData(
                        uid = doc.id,
                        photoUrl = doc.getString("photoUrl"),
                        displayName = doc.getString("displayName")
                    )
                }
                onComplete(users)
            }
            .addOnFailureListener { onComplete(emptyList()) }
    }

    // Clase de datos simple (puedes ponerla en models.kt)
    data class UserData(val uid: String, val photoUrl: String?, val displayName: String?)

    // helper: resolve email -> uid using users collection
    fun resolveUidByEmail(email: String, cb: (String?)->Unit) {
        firestore.collection("users").whereEqualTo("email", email).get().addOnSuccessListener { snaps ->
            val doc = snaps.documents.firstOrNull()
            cb(doc?.id)
        }.addOnFailureListener { cb(null) }
    }
    fun listenListDetails(listId: String, onUpdate: (ShoppingList?) -> Unit) {
        firestore.collection("shoppingLists").document(listId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val list = snapshot.toObject(ShoppingList::class.java)?.apply { id = snapshot.id }
                    onUpdate(list)
                } else {
                    onUpdate(null)
                }
            }
    }
    fun deleteList(listId: String, cb: (Boolean) -> Unit) {
        firestore.collection("shoppingLists").document(listId)
            .delete()
            .addOnSuccessListener { cb(true) }
            .addOnFailureListener { cb(false) }
    }

    fun deleteProduct(listId: String, productId: String, cb: (Boolean) -> Unit) {
        firestore.collection("shoppingLists").document(listId)
            .collection("products").document(productId)
            .delete()
            .addOnSuccessListener { cb(true) }
            .addOnFailureListener { cb(false) }
    }
}
