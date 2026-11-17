from sklearn.linear_model import Perceptron
import numpy as np

# Datos de entrada (compuerta lógica AND)
# X tiene 2 características
X = np.array([[0, 0], [0, 1], [1, 0], [1, 1]])

# Etiquetas de salida esperadas
y = np.array([0, 0, 0, 1])

# Crear y entrenar el Perceptrón
clf = Perceptron(max_iter=10, eta0=0.1, random_state=42)
clf.fit(X, y)

# Hacer predicciones
predicciones = clf.predict(X)
print("Entradas:\n", X)
print("Predicciones:", predicciones)