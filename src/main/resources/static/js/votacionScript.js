document.addEventListener("DOMContentLoaded", function () {
  const confirmarBtn = document.getElementById("confirmarBtn");

  if (confirmarBtn) {
    confirmarBtn.addEventListener("click", function (event) {
      console.log("Botón Confirmar Voto presionado");

      const seleccionado = document.querySelector('input[name="voto"]:checked');
      if (seleccionado) {
        console.log("Voto confirmado para: ", seleccionado.value);
        window.location.href = "/certificado";
      } else {
        event.preventDefault();
        alert(
          "Por favor, seleccione un candidato o la opción de voto en blanco antes de confirmar."
        );
      }
    });
  } else {
    console.error("No se encontró el botón confirmarBtn");
  }
  const csrfToken = document
    .querySelector('meta[name="_csrf"]')
    .getAttribute("content");
  const csrfHeader = document
    .querySelector('meta[name="_csrf_header"]')
    .getAttribute("content");

  let tiempoRestante = 300; // por ejemplo, 5 minutos
  const contador = document.getElementById("contador");

  if (contador) {
    const intervalo = setInterval(() => {
      const minutos = Math.floor(tiempoRestante / 60);
      const segundos = tiempoRestante % 60;
      contador.textContent = `${minutos.toString().padStart(2, "0")}:${segundos
        .toString()
        .padStart(2, "0")}`;

      if (tiempoRestante <= 0) {
        clearInterval(intervalo);
        contador.textContent = "00:00";
        alert("El tiempo de votación ha terminado.");

        // Logout automático usando fetch
        fetch("/logout", {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
          body: `_csrf=${csrfToken}`, // enviar el token CSRF
        }).then(() => {
          window.location.href = "/";
        });
      }

      tiempoRestante--;
    }, 1000);
  } else {
    console.warn("No se encontró el elemento contador");
  }
});
