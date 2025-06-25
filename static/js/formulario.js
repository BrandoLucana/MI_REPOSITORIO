document.addEventListener("DOMContentLoaded", function () {
  // Elementos del DOM
  const steps = document.querySelectorAll(".voley-form-step");
  const progressSteps = document.querySelectorAll(".voley-step");
  const nextButtons = document.querySelectorAll(".voley-next-btn");
  const prevButtons = document.querySelectorAll(".voley-prev-btn");
  const form = document.getElementById("registroForm");
  const feedbackDiv = document.getElementById("form-feedback");
  const loader = document.getElementById("loader");
  const successModal = document.getElementById("success-modal");
  const modalOkBtn = document.getElementById("modal-ok-btn");
  const emailInfo = document.getElementById("email-info");
  let currentStep = 1;

  function showStep(step) {
    steps.forEach((s) => s.classList.remove("active"));
    progressSteps.forEach((s) => s.classList.remove("active"));
    document
      .querySelector(`.voley-form-step[data-step="${step}"]`)
      .classList.add("active");
    document
      .querySelector(`.voley-step[data-step="${step}"]`)
      .classList.add("active");
    currentStep = step;
    console.log(`Mostrando paso ${step}`);
  }

  function validateStep(step) {
    const stepElement = document.querySelector(
      `.voley-form-step[data-step="${step}"]`
    );
    const inputs = stepElement.querySelectorAll("[required]");
    for (let input of inputs) {
      if (!input.value || (input.type === "checkbox" && !input.checked)) {
        input.classList.add("error");
        input.focus();
        const fieldName = input.name
          .replace(/_/g, " ")
          .replace("apoderado", "del apoderado");
        showFeedback(`Por favor, complete el campo ${fieldName}.`, "error");
        return false;
      }
      input.classList.remove("error");
    }
    return true;
  }

  function showFeedback(message, type) {
    feedbackDiv.textContent = message;
    feedbackDiv.className = `p-4 rounded mb-4 ${
      type === "error"
        ? "bg-red-100 text-red-700"
        : "bg-green-100 text-green-700"
    }`;
    feedbackDiv.classList.remove("hidden");
    setTimeout(() => {
      feedbackDiv.classList.add("hidden");
    }, 5000);
    console.log(`Feedback mostrado: ${message} (${type})`);
  }

  function calculateAge() {
    const birthDateInput = document.getElementById("fecha_nacimiento");
    const ageInput = document.getElementById("edad");
    const emailInput = document.getElementById("email");
    const apoderadoStep = document.getElementById("apoderado-step");
    const apoderadoForm = document.getElementById("apoderado-form");
    const apoderadoInputs = apoderadoForm.querySelectorAll("input, select");

    if (birthDateInput.value) {
      const birthDate = new Date(birthDateInput.value);
      const today = new Date();
      let age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();
      if (
        monthDiff < 0 ||
        (monthDiff === 0 && today.getDate() < birthDate.getDate())
      ) {
        age--;
      }
      ageInput.value = age;

      if (age < 18) {
        apoderadoStep.style.display = "block";
        apoderadoInputs.forEach((input) => {
          input.required = true;
        });
        emailInput.removeAttribute("required");
        emailInfo.classList.remove("hidden");
        if (!emailInput.value) {
          emailInput.value = "ninguno";
        }
      } else {
        apoderadoStep.style.display = "none";
        apoderadoInputs.forEach((input) => {
          input.required = false;
          input.value = "";
        });
        emailInput.setAttribute("required", "");
        emailInfo.classList.add("hidden");
        if (emailInput.value === "ninguno") {
          emailInput.value = "";
        }
      }
      console.log(
        `Edad calculada: ${age}, apoderado ${
          age < 18 ? "requerido" : "no requerido"
        }`
      );
    }
  }

  function validatePhone() {
    const phoneInput = document.getElementById("telefono_apoderado");
    if (phoneInput.value && !/^[0-9]{9}$/.test(phoneInput.value)) {
      phoneInput.classList.add("error");
      showFeedback(
        "El teléfono debe contener exactamente 9 dígitos numéricos.",
        "error"
      );
      return false;
    }
    phoneInput.classList.remove("error");
    return true;
  }

  function setCurrentDate() {
    const dateInput = document.getElementById("fecha_pago");
    const today = new Date().toISOString().split("T")[0];
    dateInput.value = today;
    console.log(`Fecha de pago establecida: ${today}`);
  }

  function handlePaymentMethod() {
    const paymentMethod = document.getElementById("medio_pago");
    const codeGroup = document.getElementById("codigo_operacion_group");
    const codeInput = document.getElementById("codigo_operacion");

    if (paymentMethod.value === "Efectivo") {
      codeGroup.style.display = "none";
      codeInput.removeAttribute("required");
      codeInput.value = "";
    } else {
      codeGroup.style.display = "block";
      codeInput.setAttribute("required", "");
    }
    console.log(`Medio de pago seleccionado: ${paymentMethod.value}`);
  }

  async function verificarEmail(email) {
    if (email === "ninguno" || email === "") {
      return true;
    }
    try {
      const response = await fetch("/verificar_email", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email }),
      });
      if (!response.ok) throw new Error("Error en la respuesta del servidor");
      const data = await response.json();
      return data.disponible;
    } catch (error) {
      console.error("Error al verificar email:", error);
      showFeedback(
        "No se pudo verificar el email. Por favor confirme que no esté registrado.",
        "warning"
      );
      return true;
    }
  }

  nextButtons.forEach((button) => {
    button.addEventListener("click", () => {
      if (validateStep(currentStep)) {
        const age = parseInt(document.getElementById("edad").value);
        if (currentStep === 1 && age >= 18) {
          showStep(3);
        } else if (currentStep < 4) {
          showStep(currentStep + 1);
        }
      }
    });
  });

  prevButtons.forEach((button) => {
    button.addEventListener("click", () => {
      const age = parseInt(document.getElementById("edad").value);
      if (currentStep === 3 && age >= 18) {
        showStep(1);
      } else if (currentStep > 1) {
        showStep(currentStep - 1);
      }
    });
  });

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    console.log("Formulario enviado, iniciando validación y envío...");

    const submitBtn = document.getElementById("submitButton");
    submitBtn.disabled = true;
    loader.style.display = "flex";
    feedbackDiv.classList.add("hidden");

    const age = parseInt(document.getElementById("edad").value);
    const stepsToValidate = age < 18 ? [1, 2, 3, 4] : [1, 3, 4];
    for (let step of stepsToValidate) {
      if (!validateStep(step)) {
        showStep(step);
        loader.style.display = "none";
        submitBtn.disabled = false;
        return;
      }
    }

    if (age < 18 && !validatePhone()) {
      showStep(2);
      document.getElementById("telefono_apoderado").focus();
      loader.style.display = "none";
      submitBtn.disabled = false;
      return;
    }

    const email = document.getElementById("email").value;
    if (email !== "ninguno" && email !== "") {
      const emailDisponible = await verificarEmail(email);
      if (!emailDisponible) {
        showFeedback(
          "El correo electrónico ya está registrado. Por favor usa otro.",
          "error"
        );
        document.getElementById("email").focus();
        loader.style.display = "none";
        submitBtn.disabled = false;
        return;
      }
    }

const medioPago = document.getElementById("medio_pago").value;
const codigoOperacion = document.getElementById("codigo_operacion").value.trim();

// Validación para Yape: 8 a 12 dígitos numéricos
if (medioPago === "Yape") {
  if (!/^[0-9]{8,12}$/.test(codigoOperacion)) {
    showFeedback(
      "El código de operación para Yape debe tener entre 8 y 12 dígitos numéricos.",
      "error"
    );
    document.getElementById("codigo_operacion").focus();
    loader.style.display = "none";
    submitBtn.disabled = false;
    return;
  }
}

// Validación para Transferencia: 8 a 20 caracteres alfanuméricos
if (medioPago === "Transferencia") {
  if (!/^[A-Za-z0-9]{8,20}$/.test(codigoOperacion)) {
    showFeedback(
      "El código de operación para Transferencia debe tener entre 8 y 20 caracteres alfanuméricos.",
      "error"
    );
    document.getElementById("codigo_operacion").focus();
    loader.style.display = "none";
    submitBtn.disabled = false;
    return;
  }
}

// Validación para Efectivo: no debe haber código ingresado
if (medioPago === "Efectivo" && codigoOperacion !== "") {
  showFeedback(
    "No debe ingresar código de operación si paga en efectivo.",
    "error"
  );
  document.getElementById("codigo_operacion").focus();
  loader.style.display = "none";
  submitBtn.disabled = false;
  return;
}


    const formData = new FormData(form);
    for (let [key, value] of formData.entries()) {
      console.log(`Enviando ${key}: ${value}`);
    }

    try {
      const response = await fetch(form.action, {
        method: "POST",
        body: formData,
      });
      const data = await response.json();
      console.log("Respuesta del servidor:", data);

      if (data.éxito) {
        successModal.classList.remove("hidden");
        modalOkBtn.onclick = () => {
          window.location.href = data.redireccion || "/";
        };
      } else {
        let mensajeError = data.mensaje;
        if (
          data.mensaje.includes("Duplicate entry") &&
          data.mensaje.includes("email")
        ) {
          mensajeError =
            "El correo electrónico ya está registrado. Por favor usa otro.";
        }
        showFeedback(mensajeError, "error");
        if (data.campo) {
          const input = document.querySelector(`[name="${data.campo}"]`);
          if (input) {
            const step = input.closest(".voley-form-step").dataset.step;
            showStep(parseInt(step));
            input.classList.add("border-red-500");
            input.focus();
          }
        }
      }
    } catch (error) {
      showFeedback(
        "Error de conexión con el servidor: " + error.message,
        "error"
      );
      console.error("Error en fetch:", error);
    } finally {
      loader.style.display = "none";
      submitBtn.disabled = false;
    }
  });

  document
    .getElementById("fecha_nacimiento")
    .addEventListener("input", calculateAge);
  document
    .getElementById("telefono_apoderado")
    .addEventListener("input", () => {
      const phoneInput = document.getElementById("telefono_apoderado");
      if (
        phoneInput.value.length === 9 &&
        /^[0-9]{9}$/.test(phoneInput.value)
      ) {
        phoneInput.classList.remove("error");
      }
    });
  document
    .getElementById("medio_pago")
    .addEventListener("change", handlePaymentMethod);

  window.addEventListener("load", () => {
    calculateAge();
    setCurrentDate();
    handlePaymentMethod();
    console.log("Formulario inicializado");
  });

  const fechaNacimiento = document.getElementById("fecha_nacimiento");
  const edadInput = document.getElementById("edad");
  const emailInput = document.getElementById("email");

  function calcularEdad(fecha) {
    const hoy = new Date();
    const nacimiento = new Date(fecha);
    let edad = hoy.getFullYear() - nacimiento.getFullYear();
    const m = hoy.getMonth() - nacimiento.getMonth();
    if (m < 0 || (m === 0 && hoy.getDate() < nacimiento.getDate())) {
      edad--;
    }
    return edad;
  }

  function actualizarMensajeEmail() {
    const edad = parseInt(edadInput.value, 10);
    if (!isNaN(edad) && edad < 18) {
      emailInfo.textContent = "En caso de no tener, escriba 'ninguno'.";
    } else {
      emailInfo.textContent = "";
      emailInfo.classList.add("hidden");
    }
  }

  fechaNacimiento.addEventListener("change", function () {
    const fecha = fechaNacimiento.value;
    if (fecha) {
      const edad = calcularEdad(fecha);
      edadInput.value = edad;
      actualizarMensajeEmail();
    } else {
      edadInput.value = "";
      emailInfo.textContent = "";
      emailInfo.classList.add("hidden");
    }
  });

  emailInput.addEventListener("focus", function () {
    const edad = parseInt(edadInput.value, 10);
    if (!isNaN(edad) && edad < 18) {
      emailInfo.classList.remove("hidden");
    }
  });

  emailInput.addEventListener("blur", function () {
    emailInfo.classList.add("hidden");
  });
});
