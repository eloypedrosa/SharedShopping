package com.eloypedrosa.sharedshopping

import com.google.firebase.Timestamp

data class ShoppingList(
    var id: String = "",
    var name: String = "",
    var ownerUid: String = "",
    var ownerEmail: String = "",
    var sharedWith: List<String> = emptyList(),
    var createdAt: Timestamp? = null
)

data class Category(
    var id: String = "",
    var name: String = "",
    var order: Long = 0
)

data class Product(
    var id: String = "",
    var name: String = "",
    var categoryId: String = "",
    var quantity: String? = null,
    var notes: String? = null,
    var completed: Boolean = false,
    var tags: List<String> = emptyList(),
    var icon: String? = null,
    var createdByUid: String? = null,
    var createdAt: Timestamp? = null
)
