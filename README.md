# 🛒 Shared Shopping

Una aplicación Android colaborativa y moderna diseñada para gestionar listas de la compra en tiempo real.
Este proyecto implementa una arquitectura basada en eventos para sincronizar productos, categorías y usuarios al instante, facilitando la organización doméstica o grupal mediante el ecosistema de Firebase.

## 📱 Galería

*(Espacio reservado para capturas de pantalla)*

## ✨ Características Destacadas

* **Sincronización en Tiempo Real:** Actualización instantánea de productos y estados (completado/pendiente) en todos los dispositivos conectados usando **Firestore Snapshots**.
* **Colaboración Inteligente:** Permite compartir listas con otros usuarios mediante su correo electrónico. El sistema resuelve el UID del usuario y actualiza los permisos automáticamente.
* **Categorización Automática:** Sistema inteligente que detecta productos comunes (ej: "leche", "yogur", "pan", "huevo") para asignarles categoría e icono automáticamente al escribirlos.
* **Gestión de Usuarios:** Integración con **Google Sign-In** para autenticación rápida y visualización de avatares de los colaboradores mediante Glide.

## 🛠️ Stack Tecnológico

El proyecto sigue las mejores prácticas de desarrollo nativo en Android:

* **Lenguaje:** 100% **Kotlin**.
* **Arquitectura:** Repository Pattern para separar la lógica de datos de la interfaz de usuario.
* **Backend as a Service (BaaS):**
    * **Firebase Firestore:** Base de datos NoSQL para persistencia de listas, productos y metadatos de usuarios.
    * **Firebase Auth:** Gestión de sesiones segura mediante proveedor de Google.
* **Librerías Clave:**
    * **Glide:** Para la carga y transformación eficiente de imágenes de perfil (circular crop).
    * **Material Design components:** Uso de `ShapeableImageView`, `FloatingActionButton` y `Chips`.

## ⚙️ Funcionalidad y Lógica

* **Creación de Listas:** Los usuarios pueden crear múltiples listas que se asocian a su UID como propietarios.
* **Interacción UI:**
    * **Click corto:** Navegar a la lista o marcar producto como completado.
    * **Long Click:** Lógica para eliminar listas o productos con diálogos de confirmación de seguridad.
* **Logica de Producto:** Al añadir un ítem, el sistema consulta un mapa interno (`autoMap`) para asignar iconos y categorías (ej: "huevo" → "Frutas y verduras" 🥚) si existen coincidencias.

## 👤 Autor

**Eloy Pedrosa**
[eloypedrosa.cat](http://eloypedrosa.cat)
