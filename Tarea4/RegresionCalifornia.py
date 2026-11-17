import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from sklearn.linear_model import LinearRegression
from sklearn.metrics import mean_squared_error, r2_score
from sklearn.model_selection import train_test_split

#Cargamos el dataset
df = pd.read_csv('housing.csv')

# Visualizamos las primeras filas del dataset
print('Primeras filas del dataset:')
print(df.head())

#Seleccionamos las variables (features y target)
#Usamos las variables 'median_income' para predecir 'median_house_value' en la regresión lineal simple
X = df[['median_income']]
y = df['median_house_value']

#Se divide los datos en conjuntos de entrenamiento y prueba (80% / 20%)
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42
)

#Se crea y entrena el modelo de regresion lineal
model = LinearRegression()
model.fit(X_train, y_train)

#Predicciones del conjunto de prueba
y_pred = model.predict(X_test)

#Resultados del modelo
print('\nResultados del Modelo:')
print(f"Coeficiente (Pendiente): {model.coef_[0]:.2f}")
print(f"Intercepción: {model.intercept_:.2f}")
print(f"Error Cuadrático Medio (MSE): {mean_squared_error(y_test, y_pred):.2f}")
print(f"Coeficiente de Determinación (R^2): {r2_score(y_test, y_pred):.4f}")

#Generamos la grafica con los resultados
plt.figure(figsize=(10, 6))
plt.scatter(
    X_test.sample(500, random_state=42),
    y_test.sample(500, random_state=42),
    color='blue',
    alpha=0.5,
    label='Datos reales (muestra)',
)
plt.plot(
    X_test,
    y_pred,
    color='red',
    linewidth=2,
    label='Línea de Regresión Lineal',
)
plt.title('Regresión Lineal: Ingreso Mediano vs Valor de la Vivienda')
plt.xlabel('Ingreso Mediano (median_income)')
plt.ylabel('Valor Mediano de la Vivienda (median_house_value)')
plt.legend()
plt.grid(True)
plt.show()