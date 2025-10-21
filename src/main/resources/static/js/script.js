const dropzone = document.getElementById("dropzone");
const fileInput = document.getElementById("fileInput");
const fileName = document.getElementById("fileName");
const form = document.getElementById("uploadForm");
const loading = document.getElementById('loading');

// Abrir explorador al hacer clic
dropzone.addEventListener("click", () => fileInput.click());

// Cuando selecciona archivo
fileInput.addEventListener("change", () => {
  if (fileInput.files.length > 0) {
    fileName.textContent = fileInput.files[0].name;
    fileName.classList.remove("hidden");
  }
});

// Drag over
dropzone.addEventListener("dragover", (e) => {
  e.preventDefault();
  dropzone.classList.add("bg-green-50", "border-green-500");
});

// Drag leave
dropzone.addEventListener("dragleave", () => {
  dropzone.classList.remove("bg-green-50", "border-green-500");
});

// Drop
dropzone.addEventListener("drop", (e) => {
  e.preventDefault();
  dropzone.classList.remove("bg-green-50", "border-green-500");

  if (e.dataTransfer.files.length > 0) {
    fileInput.files = e.dataTransfer.files;
    fileName.textContent = e.dataTransfer.files[0].name;
    fileName.classList.remove("hidden");
  }
});

form.addEventListener("submit", (e) => {
  const fileInput = document.getElementById("fileInput");
  if (!fileInput.files.length) {
    e.preventDefault();
    alert("Debes seleccionar un archivo CSV antes de enviar");
  }
});

form.addEventListener('submit', () => {
    loading.classList.remove('hidden');
});