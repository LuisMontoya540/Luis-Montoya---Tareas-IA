import tkinter as tk
import numpy as np
from PIL import Image, ImageDraw
import tensorflow as tf

MODEL_PATH = "mnist_cnn_model.keras"
CANVAS_SIZE = 280
BRUSH_RADIUS = 12


class DigitRecognizerApp:
    def __init__(self, root: tk.Tk, model: tf.keras.Model):
        self.root = root
        self.model = model
        self.root.title("Clasificador MNIST en Tiempo Real (0 - 9)")
        self.root.resizable(False, False)

        #Imagen interna en escala de grises
        self.pil_image = Image.new("L", (CANVAS_SIZE, CANVAS_SIZE), color=0)
        self.draw = ImageDraw.Draw(self.pil_image)

        self.last_x = None
        self.last_y = None

        #Lienzo visual
        self.canvas = tk.Canvas(
            root,
            width=CANVAS_SIZE,
            height=CANVAS_SIZE,
            bg="black",
            highlightthickness=2,
            highlightbackground="#444",
        )
        self.canvas.grid(row=0, column=0, columnspan=2, padx=12, pady=12)

        self.canvas.bind("<ButtonPress-1>", self.on_button_press)
        self.canvas.bind("<B1-Motion>", self.on_paint)
        self.canvas.bind("<ButtonRelease-1>", self.on_button_release)

        #Etiquetas de resultado
        self.pred_label = tk.Label(
            root,
            text="Dibuja un dígito (0–9)",
            font=("Helvetica", 20, "bold"),
            pady=6,
        )
        self.pred_label.grid(row=1, column=0, columnspan=2)

        self.conf_label = tk.Label(
            root,
            text="Confianza: --%",
            font=("Helvetica", 12),
            fg="#555",
        )
        self.conf_label.grid(row=2, column=0, columnspan=2, pady=(0, 8))

        #Botón limpiar
        self.clear_btn = tk.Button(
            root,
            text="Limpiar lienzo",
            font=("Helvetica", 11),
            command=self.clear_canvas,
            width=16,
        )
        self.clear_btn.grid(row=3, column=0, columnspan=2, pady=(0, 14))

    def on_button_press(self, event):
        self.last_x = event.x
        self.last_y = event.y
        self.paint_point(event.x, event.y)

    def on_paint(self, event):
        if self.last_x is not None and self.last_y is not None:
            self.canvas.create_line(
                self.last_x,
                self.last_y,
                event.x,
                event.y,
                fill="white",
                width=BRUSH_RADIUS * 2,
                capstyle=tk.ROUND,
                smooth=True,
            )
            self.draw.line(
                [self.last_x, self.last_y, event.x, event.y],
                fill=255,
                width=BRUSH_RADIUS * 2,
                joint="curve",
            )
        self.last_x = event.x
        self.last_y = event.y
        self.predict_digit()

    def on_button_release(self, _event):
        self.last_x = None
        self.last_y = None
        self.predict_digit()

    def paint_point(self, x: int, y: int):
        r = BRUSH_RADIUS
        self.canvas.create_oval(x - r, y - r, x + r, y + r, fill="white", outline="white")
        self.draw.ellipse([x - r, y - r, x + r, y + r], fill=255, outline=255)

    def clear_canvas(self):
        self.canvas.delete("all")
        self.pil_image = Image.new("L", (CANVAS_SIZE, CANVAS_SIZE), color=0)
        self.draw = ImageDraw.Draw(self.pil_image)
        self.pred_label.config(text="Dibuja un dígito (0–9)")
        self.conf_label.config(text="Confianza: --%")

    def preprocess_image(self) -> np.ndarray:
        resized = self.pil_image.resize((28, 28), Image.Resampling.LANCZOS)
        arr = np.asarray(resized, dtype="float32") / 255.0
        return arr.reshape(1, 28, 28, 1)

    def predict_digit(self):
        if np.max(np.asarray(self.pil_image)) == 0:
            return
        input_tensor = self.preprocess_image()
        probs = self.model.predict(input_tensor, verbose=0)[0]
        digit = int(np.argmax(probs))
        confidence = float(probs[digit]) * 100.0

        self.pred_label.config(text=f"Predicción: {digit}")
        self.conf_label.config(text=f"Confianza: {confidence:.2f}%")


def main():
    model = tf.keras.models.load_model(MODEL_PATH)
    root = tk.Tk()
    DigitRecognizerApp(root, model)
    root.mainloop()


if __name__ == "__main__":
    main()