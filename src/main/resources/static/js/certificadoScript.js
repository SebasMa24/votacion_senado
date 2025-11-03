async function descargarPDFExacto() {
      const { jsPDF } = window.jspdf;
      const certificado = document.getElementById('certificado');

      // Convertir el recuadro a imagen con alta resolución
      const canvas = await html2canvas(certificado, {
        scale: 3, // más calidad
        useCORS: true
      });

      const imgData = canvas.toDataURL('image/png');
      const imgWidth = canvas.width;
      const imgHeight = canvas.height;

      // Medidas en mm para PDF (1 px ≈ 0.264583 mm)
      const pdfWidth = imgWidth * 0.264583;
      const pdfHeight = imgHeight * 0.264583;

      // Crear PDF del mismo tamaño del recuadro
      const pdf = new jsPDF({
        orientation: pdfWidth > pdfHeight ? 'l' : 'p',
        unit: 'mm',
        format: [pdfWidth, pdfHeight]
      });

      // Agregar la imagen sin márgenes
      pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight);

      // Descargar
      pdf.save('Certificado_Electoral.pdf');
    }