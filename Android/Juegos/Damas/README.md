# ⚪ Juego de las Damas ⚫

Este repositorio contiene una implementación del clásico juego de las damas.

## 📋 Preparación del Juego

La posición inicial estándar del tablero y las fichas es la siguiente:

### 1. El Tablero
* Se juega en un tablero de **8x8 casillas** con colores alternos (claros y oscuros).
* El tablero debe orientarse de modo que la casilla de la esquina inferior derecha de cada jugador sea de **color claro**.

### 2. Distribución de las Fichas
* Cada jugador comienza con **12 fichas**.
* Las fichas se colocan únicamente en las **casillas oscuras** de las tres filas más cercanas a cada jugador.
* Esto dejará las dos filas centrales vacías al inicio de la partida.

---

## 🕹️ Reglas Básicas

### Movimiento Estándar
* Las fichas siempre se mueven en **diagonal** hacia adelante, una casilla a la vez.
* Solo pueden ocupar casillas oscuras que estén vacías.

### Captura de Fichas
* Si una ficha contraria está en una casilla adyacente y la casilla inmediatamente detrás de ella está vacía, puedes **saltar sobre ella** para capturarla.
* La ficha capturada se retira del tablero.
* Si tras realizar un salto, la misma ficha puede volver a saltar sobre otra pieza enemiga, debe hacerlo en el mismo turno.

### Coronación (La Dama)
* Cuando una ficha llega a la última fila del lado del oponente, se convierte en **Dama** (generalmente se coloca otra ficha encima para identificarla).
* A diferencia de las fichas comunes, la Dama puede moverse y capturar tanto hacia adelante como hacia atrás.

---

## 🏆 Objetivo del Juego
El juego termina cuando un jugador:
1.  Captura todas las fichas del oponente.
2.  Bloquea todas las piezas del oponente, dejándolo sin movimientos legales posibles.

---

## 🎥 Tutorial en Vídeo

En el siguiente enlace se dispone de una explicación visual detallada sobre los movimientos y estrategias:

👉 [**Ver Tutorial de Damas en YouTube**](https://www.youtube.com/watch?v=r-7R2sCW3Ro)
