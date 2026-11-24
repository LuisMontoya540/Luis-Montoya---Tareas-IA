import os
import struct
import zipfile
import numpy as np
import tensorflow as tf
from tensorflow.keras import layers, models

ZIP_PATH = "archive.zip"
EXTRACT_DIR = "mnist_extracted"
MODEL_PATH = "mnist_cnn_model.keras"


def extract_zip_if_needed(zip_path: str, extract_dir: str) -> None:
    if not os.path.exists(extract_dir):
        if not os.path.exists(zip_path):
            raise FileNotFoundError(f"No se encontró el archivo '{zip_path}' en el directorio actual.")
        with zipfile.ZipFile(zip_path, "r") as zf:
            zf.extractall(extract_dir)
        print(f"Archivo '{zip_path}' extraído en '{extract_dir}'.")


def find_idx_file(root_dir: str, target_name: str) -> str:
    """Se Busca el archivo IDX dentro del directorio extraído, se evita la carpeta __MACOSX."""
    for dirpath, _, filenames in os.walk(root_dir):
        if "__MACOSX" in dirpath:
            continue
        if target_name in filenames:
            return os.path.join(dirpath, target_name)
    raise FileNotFoundError(f"No se encontró '{target_name}' dentro de '{root_dir}'.")


def load_idx_images(filepath: str) -> np.ndarray:
    with open(filepath, "rb") as f:
        magic, num_images, rows, cols = struct.unpack(">IIII", f.read(16))
        if magic != 2051:
            raise ValueError(f"Magic number inválido en imágenes ({magic}) para {filepath}")
        data = np.frombuffer(f.read(), dtype=np.uint8)
        return data.reshape(num_images, rows, cols)


def load_idx_labels(filepath: str) -> np.ndarray:
    with open(filepath, "rb") as f:
        magic, num_labels = struct.unpack(">II", f.read(8))
        if magic != 2049:
            raise ValueError(f"Magic number inválido en etiquetas ({magic}) para {filepath}")
        return np.frombuffer(f.read(), dtype=np.uint8)


def build_cnn_model(input_shape=(28, 28, 1), num_classes=10) -> tf.keras.Model:
    model = models.Sequential([
        layers.Input(shape=input_shape),
        layers.Conv2D(32, kernel_size=(3, 3), activation="relu"),
        layers.MaxPooling2D(pool_size=(2, 2)),
        layers.Conv2D(64, kernel_size=(3, 3), activation="relu"),
        layers.MaxPooling2D(pool_size=(2, 2)),
        layers.Flatten(),
        layers.Dropout(0.3),
        layers.Dense(128, activation="relu"),
        layers.Dense(num_classes, activation="softmax"),
    ])
    model.compile(
        optimizer="adam",
        loss="sparse_categorical_crossentropy",
        metrics=["accuracy"],
    )
    return model


def main():
    extract_zip_if_needed(ZIP_PATH, EXTRACT_DIR)

    train_img_path = find_idx_file(EXTRACT_DIR, "train-images.idx3-ubyte")
    train_lbl_path = find_idx_file(EXTRACT_DIR, "train-labels.idx1-ubyte")
    test_img_path = find_idx_file(EXTRACT_DIR, "t10k-images.idx3-ubyte")
    test_lbl_path = find_idx_file(EXTRACT_DIR, "t10k-labels.idx1-ubyte")

    x_train = load_idx_images(train_img_path).astype("float32") / 255.0
    y_train = load_idx_labels(train_lbl_path)
    x_test = load_idx_images(test_img_path).astype("float32") / 255.0
    y_test = load_idx_labels(test_lbl_path)

    x_train = np.expand_dims(x_train, axis=-1)
    x_test = np.expand_dims(x_test, axis=-1)

    model = build_cnn_model(input_shape=x_train.shape[1:], num_classes=10)
    model.summary()

    print("\nModelo en Entrenamiento.")
    model.fit(
        x_train,
        y_train,
        batch_size=128,
        epochs=6,
        validation_split=0.1,
        verbose=1,
    )

    test_loss, test_acc = model.evaluate(x_test, y_test, verbose=0)
    print(f"\nPrecisión en conjunto de prueba (10,000 imágenes): {test_acc * 100:.2f}%")

    model.save(MODEL_PATH)
    print(f"Modelo guardado exitosamente en '{MODEL_PATH}'.")


if __name__ == "__main__":
    main()