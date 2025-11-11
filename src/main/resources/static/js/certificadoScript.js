async function descargarPDFExacto() {
  const { jsPDF } = window.jspdf;
  const certificado = document.getElementById("certificado");

  // Convertir el recuadro a imagen con alta resolución
  const canvas = await html2canvas(certificado, {
    scale: 3, // más calidad
    useCORS: true,
  });

  const imgData = canvas.toDataURL("image/png");
  const imgWidth = canvas.width;
  const imgHeight = canvas.height;

  // Medidas en mm para PDF (1 px ≈ 0.264583 mm)
  const pdfWidth = imgWidth * 0.264583;
  const pdfHeight = imgHeight * 0.264583;

  // Crear PDF del mismo tamaño del recuadro
  const pdf = new jsPDF({
    orientation: pdfWidth > pdfHeight ? "l" : "p",
    unit: "mm",
    format: [pdfWidth, pdfHeight],
  });

  // Agregar la imagen sin márgenes
  pdf.addImage(imgData, "PNG", 0, 0, pdfWidth, pdfHeight);

  // Descargar
  pdf.save("Certificado_Electoral.pdf");
}

async function enviarPDFPorCorreo() {
  const token = document.querySelector('meta[name="_csrf"]').content;
  const header = document.querySelector('meta[name="_csrf_header"]').content;

  const certificado = document.getElementById("certificado");
  const toEmail = document.getElementById("toEmail").value;

  // 1️⃣ Generar PDF desde el HTML
  const canvas = await html2canvas(certificado, { scale: 1, useCORS: true });
  const imgData = canvas.toDataURL("image/png");

  const { jsPDF } = window.jspdf;
  const pdf = new jsPDF({
    orientation: canvas.width > canvas.height ? "l" : "p",
    unit: "mm",
    format: [canvas.width * 0.264583, canvas.height * 0.264583],
  });
  pdf.addImage(
    imgData,
    "PNG",
    0,
    0,
    canvas.width * 0.264583,
    canvas.height * 0.264583
  );

  const pdfBase64 = pdf.output("datauristring");

  // 2️⃣ Enviar PDF al backend usando FormData
  const formData = new FormData();
  formData.append("toEmail", toEmail);
  formData.append("pdfBase64", pdfBase64);

  const response = await fetch("/certificado/enviar", {
    method: "POST",
    headers: {
        [header]: token
    },
    body: formData,
  });

  // 3️⃣ Actualizar mensaje en la página
  const text = await response.text();
  alert("PDF enviado al correo!");
}
