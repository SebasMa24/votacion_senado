// Configuración global de Chart.js
Chart.defaults.global.responsive = true;
Chart.defaults.global.maintainAspectRatio = false;
Chart.defaults.global.legend.position = "bottom";
Chart.defaults.global.defaultFontSize = 12;

const partyColors = {
  "Partido Liberal": "#36A2EB",
  "Partido Conservador": "#4BC0C0",
  "Centro Democrático": "#FF6384",
  "Partido Verde": "#FFCE56",
  "Comunes": "#9966FF",
};

// Gráfica de Votos por Partido
const ctxPartido = document.getElementById("partidoChart").getContext("2d");
new Chart(ctxPartido, {
  type: "pie",
  data: {
    labels: [
      "Partido Liberal",
      "Partido Conservador",
      "Centro Democrático",
      "Partido Verde",
      "Comunes",
    ],
    datasets: [
      {
        data: [1200000, 900000, 700000, 400000, 200000],
        backgroundColor: [
          partyColors["Partido Liberal"],
          partyColors["Partido Conservador"],
          partyColors["Centro Democrático"],
          partyColors["Partido Verde"],
          partyColors["Comunes"]
        ],
        borderColor: "white",
        borderWidth: 2,
      },
    ],
  },
  options: {
    responsive: true,
    maintainAspectRatio: false,
    legend: {
      position: "bottom",
      labels: {
        boxWidth: 12,
        fontSize: 11,
      },
    },
  },
});

// Gráfica de Votos por Candidato
const ctxCandidato = document.getElementById("candidatoChart").getContext("2d");
new Chart(ctxCandidato, {
  type: "bar",
  data: {
    labels: ["Juan Pérez", "Ana Gómez", "Carlos Torres"],
    datasets: [
      {
        label: "Votos",
        data: [400000, 350000, 300000],
        backgroundColor: "rgba(54, 162, 235, 0.7)",
      },
    ],
  },
  options: {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: function (value) {
            return value.toLocaleString();
          },
        },
      },
      x: {
        ticks: {
          autoSkip: false,
          maxRotation: 45,
          minRotation: 45,
        },
      },
    },
    legend: {
      display: false,
    },
  },
});

// Gráfica de Curules
var ctx = document.getElementById("curulesChart").getContext("2d");
var curulesChart = new Chart(ctx, {
  type: "doughnut",
  data: {
    labels: [
      "Partido Liberal",
      "Partido Conservador",
      "Centro Democrático",
      "Partido Verde",
      "Comunes",
    ],
    datasets: [
      {
        data: [35, 25, 20, 15, 13],
        backgroundColor: [
          partyColors["Partido Liberal"],
          partyColors["Partido Conservador"],
          partyColors["Centro Democrático"],
          partyColors["Partido Verde"],
          partyColors["Comunes"]
        ],
        borderWidth: 2,
        borderColor: "white",
      },
    ],
  },
  options: {
    responsive: true,
    maintainAspectRatio: false,
    cutoutPercentage: 60, // Controla el tamaño del agujero central
    rotation: -Math.PI, // Comienza desde la parte superior (12 en punto)
    circumference: Math.PI, // Solo media circunferencia (180 grados)
    legend: {
      position: "bottom",
      labels: {
        boxWidth: 15,
        fontSize: 11,
        padding: 15,
        usePointStyle: true,
      },
    },
    tooltips: {
      callbacks: {
        label: function (tooltipItem, data) {
          var label = data.labels[tooltipItem.index] || "";
          var value = data.datasets[0].data[tooltipItem.index] || 0;
          return label + ": " + value + " curules";
        },
      },
    },
    animation: {
      animateRotate: true,
      animateScale: true,
    },
  },
});
