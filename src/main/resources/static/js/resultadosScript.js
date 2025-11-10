// Obtener datos desde el HTML
const dataDiv = document.getElementById("participacionData");
const habilitados = parseInt(dataDiv.dataset.habilitados);
const votantes = parseInt(dataDiv.dataset.votantes);
const abstencion = habilitados - votantes;

// Crear gráfico
const ctx = document.getElementById("participacionChart").getContext("2d");
const participacionChart = new Chart(ctx, {
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

const canvas = document.getElementById("votosNacionalChart");

// Obtener datos desde el HTML
const nombres = canvas.dataset.nombres.split(",");          // ["Partido A", "Partido B", ...]
const votos = canvas.dataset.votos.split(",").map(v => parseInt(v)); // [120, 150, ...]
const votosBlanco = parseInt(canvas.dataset.votosBlanco);

// Añadir votos en blanco
nombres.push("Votos en Blanco");
votos.push(votosBlanco);

// Crear gráfico
new Chart(ctx, {
    type: "bar",
    data: {
        labels: nombres,
        datasets: [{
            label: "Votos",
            data: votos,
            backgroundColor: "#34D399"
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: { display: false }
        }
    }
});