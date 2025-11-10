// Obtener datos desde el HTML
const dataDiv = document.getElementById("participacionData");
const habilitados = parseInt(dataDiv.dataset.habilitados);
const votantes = parseInt(dataDiv.dataset.votantes);
const abstencion = habilitados - votantes;

// Crear gráfico de participación
const ctxParticipacion = document
  .getElementById("participacionChart")
  .getContext("2d");
const participacionChart = new Chart(ctxParticipacion, {
  type: "doughnut",
  data: {
    labels: ["Votantes", "Abstención"],
    datasets: [
      {
        data: [votantes, abstencion],
        backgroundColor: ["#BBF7D0", "#FEF3C7"],
        borderColor: ["#34D399", "#FBBF24"],
        borderWidth: 1,
      },
    ],
  },
  options: {
    plugins: {
      legend: { position: "bottom" },
      tooltip: {
        callbacks: {
          label: function (context) {
            const value = context.parsed;
            const percentage = ((value / habilitados) * 100).toFixed(2);
            return context.label + ": " + value + " (" + percentage + "%)";
          },
        },
      },
    },
  },
});

// Gráfico de Votos Nacionales
const canvasNacional = document.getElementById("votosNacionalChart");
if (canvasNacional) {
  const nombres = canvasNacional.dataset.nombres.split(",");
  const votos = canvasNacional.dataset.votos.split(",").map((v) => parseInt(v));
  const votosBlanco = parseInt(canvasNacional.dataset.votosBlanco);

  // Añadir votos en blanco
  nombres.push("Votos en Blanco");
  votos.push(votosBlanco);

  // Generar colores pastel automáticamente
  const generarColoresPastel = (numColores) => {
    const colores = [];
    for (let i = 0; i < numColores; i++) {
      const hue = ((i * 360) / numColores) % 360;
      // HSL con alta saturación y luminosidad para efecto pastel
      colores.push(`hsl(${hue}, 80%, 85%)`);
    }
    return colores;
  };

  const colores = generarColoresPastel(nombres.length - 1);
  colores.push("#E5E7EB"); // Gris pastel para votos en blanco

  const ctxNacional = canvasNacional.getContext("2d");
  new Chart(ctxNacional, {
    type: "bar",
    data: {
      labels: nombres,
      datasets: [
        {
          label: "Votos",
          data: votos,
          backgroundColor: colores,
          borderColor: colores.map((color) => color.replace("85%", "70%")), // Borde un poco más oscuro
          borderWidth: 2,
          borderRadius: 4, // Bordes redondeados para mejor look
          borderSkipped: false,
        },
      ],
    },
    options: {
      responsive: true,
      plugins: {
        legend: { display: false },
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: {
            color: "rgba(0, 0, 0, 0.1)",
          },
        },
        x: {
          grid: {
            display: false,
          },
        },
      },
    },
  });
}

// Gráfico de Votos Indígenas
const canvasIndigena = document.getElementById("votosIndigenaChart");
if (canvasIndigena) {
  const nombres = canvasIndigena.dataset.nombres.split(",");
  const votos = canvasIndigena.dataset.votos.split(",").map((v) => parseInt(v));
  const votosBlanco = parseInt(canvasIndigena.dataset.votosBlanco);

  nombres.push("Votos en Blanco");
  votos.push(votosBlanco);

  // Misma función de colores pastel
  const generarColoresPastel = (numColores) => {
    const colores = [];
    for (let i = 0; i < numColores; i++) {
      const hue = ((i * 360) / numColores) % 360;
      colores.push(`hsl(${hue}, 80%, 85%)`);
    }
    return colores;
  };

  const colores = generarColoresPastel(nombres.length - 1);
  colores.push("#E5E7EB"); // Gris pastel para votos en blanco

  const ctxIndigena = canvasIndigena.getContext("2d");
  new Chart(ctxIndigena, {
    type: "bar",
    data: {
      labels: nombres,
      datasets: [
        {
          label: "Votos",
          data: votos,
          backgroundColor: colores,
          borderColor: colores.map((color) => color.replace("85%", "70%")),
          borderWidth: 2,
          borderRadius: 4,
          borderSkipped: false,
        },
      ],
    },
    options: {
      responsive: true,
      plugins: {
        legend: { display: false },
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: {
            color: "rgba(0, 0, 0, 0.1)",
          },
        },
        x: {
          grid: {
            display: false,
          },
        },
      },
    },
  });
}

const canvasCurules = document.getElementById("curulesChart");
if (canvasCurules) {
  // Obtener datos desde los atributos data
  const curulesNacionalesStr = canvasCurules.dataset.curulesNacionales;
  const curulesIndigenasStr = canvasCurules.dataset.curulesIndigenas;

  // Parsear los datos de los mapas
  const parseMapData = (mapStr) => {
    const map = {};
    if (mapStr) {
      const pairs = mapStr.split(",");
      pairs.forEach((pair) => {
        const [key, value] = pair.split("=");
        if (key && value) {
          map[key.trim()] = parseInt(value.trim());
        }
      });
    }
    return map;
  };

  const curulesNacionales = parseMapData(curulesNacionalesStr);
  const curulesIndigenas = parseMapData(curulesIndigenasStr);

  // Preparar datos para la gráfica
  const labels = [];
  const data = [];

  // Procesar curules nacionales
  Object.entries(curulesNacionales).forEach(([partido, curules]) => {
    labels.push(partido);
    data.push(curules);
  });

  // Procesar curules indígenas y sumar si el partido ya existe
  Object.entries(curulesIndigenas).forEach(([partido, curules]) => {
    const existingIndex = labels.indexOf(partido);
    if (existingIndex !== -1) {
      // Sumar a partido existente
      data[existingIndex] += curules;
    } else {
      // Agregar nuevo partido
      labels.push(partido);
      data.push(curules);
    }
  });

  // Agregar curul de oposición (fija)
  const partidoOposicion = "Segunda Fuerza Presidencial";
  labels.push(`${partidoOposicion} (Oposición)`);
  data.push(1); // 1 curul de oposición

  // Generar colores pastel para todos los partidos
  const generarColoresPastel = (numColores) => {
    const colores = [];
    for (let i = 0; i < numColores; i++) {
      const hue = ((i * 360) / numColores) % 360;
      colores.push(`hsl(${hue}, 70%, 75%)`); // Colores un poco más saturados
    }
    return colores;
  };

  const colores = generarColoresPastel(labels.length);
  // El último color será para la oposición

  // Crear la gráfica usando la sintaxis moderna de Chart.js
  const ctxCurules = canvasCurules.getContext("2d");
  new Chart(ctxCurules, {
    type: "doughnut",
    data: {
      labels: labels,
      datasets: [
        {
          data: data,
          backgroundColor: colores,
          borderWidth: 2,
          borderColor: "white",
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      cutout: "60%", // Sintaxis moderna
      rotation: -90, // Grados en lugar de radianes
      circumference: 180, // Media dona (180 grados)
      plugins: {
        legend: {
          position: "bottom",
          labels: {
            boxWidth: 15,
            fontSize: 11,
            padding: 15,
            usePointStyle: true,
          },
        },
        tooltip: {
          callbacks: {
            label: function (context) {
              const label = context.label || "";
              const value = context.parsed || 0;
              return label + ": " + value + " curules";
            },
          },
        },
      },
      animation: {
        animateRotate: true,
        animateScale: true,
      },
    },
  });
}
