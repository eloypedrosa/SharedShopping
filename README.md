# 🚢 Battleship Online (Hundir la Flota)

**Una implementación moderna y multijugador del clásico juego de estrategia naval, desarrollada nativamente en Android.**

Este proyecto demuestra el uso de arquitecturas reactivas para gestionar partidas en tiempo real, sincronización de estados y persistencia de datos en la nube.

---

## 📱 Galería

<img width="200" alt="Screenshot_20251216_140116" src="https://github.com/user-attachments/assets/a29e7553-d86d-4601-b829-f782481325ac" />
<img width="200" alt="Screenshot_20251216_140136" src="https://github.com/user-attachments/assets/eecec909-bd83-4b95-9214-037455655517" />
<img width="200" alt="Screenshot_20251216_135941" src="https://github.com/user-attachments/assets/acb08e85-47cf-4aa4-b9db-0f2b42003d30" />
<img width="200" alt="Screenshot_20251216_140016" src="https://github.com/user-attachments/assets/77dc383c-5c72-4573-8d97-3d22a0feaa9a" />

---

## ✨ Características Destacadas

* **⚡ Multijugador en Tiempo Real:** Sincronización instantánea de disparos y turnos utilizando **Kotlin Flows** y **Firestore**.
* **🤝 Matchmaking Automático:** Sistema inteligente que busca partidas en espera (`WAITING`) o crea una nueva sala automáticamente si no hay rivales.
* **🏆 Leaderboard Global:** Ranking de los mejores jugadores ordenados por puntuación total y victorias.
* **🔐 Acceso Rápido:** Implementación de Login Anónimo para empezar a jugar sin registros tediosos.

## 🛠️ Stack Tecnológico

El proyecto sigue las mejores prácticas de desarrollo moderno en Android:

* **Lenguaje:** 100% [Kotlin](https://kotlinlang.org/).
* **Arquitectura:** MVVM + Repository Pattern.
* **Concurrencia:** Coroutines & StateFlow.
* **Backend as a Service (BaaS):**
    * **Firebase Firestore:** Base de datos NoSQL para el estado del juego (Tableros, Celdas, Turnos).
    * **Firebase Auth:** Gestión de sesiones de usuario.

## 🎮 Reglas del Juego

1.  **Setup:** Posiciona tu flota (Cruiser, Submarine, Destroyer) en el tablero de 6x6.
2.  **Batalla:** El sistema decide aleatoriamente quién comienza el turno.
3.  **Mecánica:**
    * **AGUA:** Turno pasa al rival.
    * **TOCADO:** +1 Punto.
    * **HUNDIDO:** +2 Puntos extra.
4.  **Victoria:** Gana +10 puntos al eliminar toda la flota enemiga.

---

### 👤 Autor

**[Tu Nombre]**
* [LinkedIn](Tu LinkedIn)
* [Portfolio](Tu Web)

⭐️ **Si te ha gustado este proyecto, ¡no olvides darle una estrella!**
