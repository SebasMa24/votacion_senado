document.getElementById("confirmarBtn").addEventListener("click", function (event) {
    console.log("Botón Confirmar Voto presionado");
    // Verifica si algún radio con name="voto" está seleccionado
    const seleccionado = document.querySelector('input[name="voto"]:checked');
    if (seleccionado) {
      // Redirige a la página de certificado
      window.location.href = "/certificado";
    } else {
      // Previene la acción y muestra un aviso
      alert("Por favor, seleccione un candidato o la opción de voto en blanco antes de confirmar.");
    }
  });