import cv2
import json
import numpy as np
import tensorflow as tf

# 1. Cargar modelo entrenado y etiquetas
modelo = tf.keras.models.load_model("modelo_facial.keras")
with open("clases.json", "r") as f:
    clases = json.load(f)

# Calentar el modelo para evitar lag inicial
modelo(np.zeros((1, 224, 224, 3), dtype="float32"), training=False)

# 2. Detector de rostros de OpenCV
face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

# 3. Flujo de cámara web
cap = cv2.VideoCapture(0)
print("Cámara iniciada. Presiona 'q' para salir...")

contador_frames = 0
rostros_actuales = []
predicciones_actuales = []

while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break

    frame = cv2.flip(frame, 1)
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

    # Procesar cada 2 frames para máxima fluidez en video
    if contador_frames % 2 == 0:
        rostros_actuales = face_cascade.detectMultiScale(gray, scaleFactor=1.15, minNeighbors=5, minSize=(80, 80))
        predicciones_actuales = []

        for (x, y, ancho, alto) in rostros_actuales:
            # Ajustar ligeramente el margen interior para coincidir con el recorte de entrenamiento
            margen_x = int(ancho * 0.08)
            margen_y = int(alto * 0.08)
            rostro = rgb_frame[y+margen_y:y+alto-margen_y, x+margen_x:x+ancho-margen_x]
            
            if rostro.size == 0:
                rostro = rgb_frame[y:y+alto, x:x+ancho]

            rostro_resized = cv2.resize(rostro, (224, 224))
            rostro_norm = rostro_resized.astype("float32") / 255.0
            rostro_input = np.expand_dims(rostro_norm, axis=0)

            # Llamada directa al modelo (mucho más rápida que .predict)
            preds = modelo(rostro_input, training=False).numpy()[0]
            indice_max = np.argmax(preds)
            confianza = preds[indice_max] * 100
            etiqueta = clases[str(indice_max)]
            predicciones_actuales.append((etiqueta, confianza))

    # Dibujar resultados en pantalla
    for i, (x, y, ancho, alto) in enumerate(rostros_actuales):
        if i < len(predicciones_actuales):
            etiqueta, confianza = predicciones_actuales[i]
            if confianza < 45:
                texto = f"Desconocido ({confianza:.1f}%)"
                color = (0, 0, 255)
            else:
                texto = f"{etiqueta}: {confianza:.1f}%"
                color = (0, 255, 0)

            cv2.rectangle(frame, (x, y), (x + ancho, y + alto), color, 2)
            cv2.rectangle(frame, (x, y - 30), (x + ancho, y), color, -1)
            cv2.putText(frame, texto, (x + 5, y - 8), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 0), 2)

    contador_frames += 1
    cv2.imshow("Reconocimiento Facial CNN - En Vivo", frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()