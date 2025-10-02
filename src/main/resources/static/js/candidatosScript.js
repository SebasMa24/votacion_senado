const btnMasiva = document.getElementById("btnMasiva");
const btnIndividual = document.getElementById("btnIndividual");
const seccionMasiva = document.getElementById("seccionMasiva");
const seccionIndividual = document.getElementById("seccionIndividual");

btnMasiva.addEventListener("click", () => {
  btnMasiva.disabled = true;
  btnIndividual.disabled = false;
  seccionMasiva.classList.remove("hidden");
  seccionIndividual.classList.add("hidden");
  btnMasiva.classList.remove("bg-blue-600");
  btnMasiva.classList.add("bg-blue-200", "text-white");
  btnIndividual.classList.remove("bg-blue-200");
  btnIndividual.classList.add("bg-blue-600");
});

btnIndividual.addEventListener("click", () => {
  btnIndividual.disabled = true;
  btnMasiva.disabled = false;
  seccionMasiva.classList.add("hidden");
  seccionIndividual.classList.remove("hidden");
  btnIndividual.classList.remove("bg-blue-600");
  btnIndividual.classList.add("bg-blue-200", "text-white");
  btnMasiva.classList.remove("bg-blue-200");
  btnMasiva.classList.add("bg-blue-600");
});
