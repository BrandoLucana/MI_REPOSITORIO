// Elementos del DOM
const steps = document.querySelectorAll('.voley-form-step');
const progressSteps = document.querySelectorAll('.voley-step');
const nextButtons = document.querySelectorAll('.voley-next-btn');
const prevButtons = document.querySelectorAll('.voley-prev-btn');
const form = document.getElementById('registroForm');
const feedbackDiv = document.getElementById('form-feedback');
const loader = document.getElementById('loader');
const successModal = document.getElementById('success-modal');
const modalOkBtn = document.getElementById('modal-ok-btn');
const emailInfo = document.getElementById('email-info');
let currentStep = 1;

// Mostrar un paso específico
function showStep(step) {
    steps.forEach(s => s.classList.remove('active'));
    progressSteps.forEach(s => s.classList.remove('active'));
    document.querySelector(`.voley-form-step[data-step="${step}"]`).classList.add('active');
    document.querySelector(`.voley-step[data-step="${step}"]`).classList.add('active');
    currentStep = step;
    console.log(`Mostrando paso ${step}`);
}

// Validar campos requeridos en un paso
function validateStep(step) {
    const stepElement = document.querySelector(`.voley-form-step[data-step="${step}"]`);
    const inputs = stepElement.querySelectorAll('[required]');
    for (let input of inputs) {
        if (!input.value || (input.type === 'checkbox' && !input.checked)) {
            input.classList.add('error');
            input.focus();
            const fieldName = input.name.replace(/_/g, ' ').replace('apoderado', 'del apoderado');
            showFeedback(`Por favor, complete el campo ${fieldName}.`, 'error');
            return false;
        }
        input.classList.remove('error');
    }
    return true;
}

// Mostrar retroalimentación
function showFeedback(message, type) {
    feedbackDiv.textContent = message;
    feedbackDiv.className = `p-4 rounded ${type === 'error' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`;
    feedbackDiv.classList.remove('hidden');
    setTimeout(() => {
        feedbackDiv.classList.add('hidden');
    }, 5000);
    console.log(`Feedback mostrado: ${message} (${type})`);
}

// Calcular edad y manejar paso de apoderado
function calculateAge() {
    const birthDateInput = document.getElementById('fecha_nacimiento');
    const ageInput = document.getElementById('edad');
    const emailInput = document.getElementById('email');
    const apoderadoStep = document.getElementById('apoderado-step');
    const apoderadoForm = document.getElementById('apoderado-form');
    const apoderadoInputs = apoderadoForm.querySelectorAll('input, select');

    if (birthDateInput.value) {
        const birthDate = new Date(birthDateInput.value);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        ageInput.value = age;

        // Manejar visibilidad del paso de apoderado y requisitos del correo
        if (age < 18) {
            apoderadoStep.style.display = 'block';
            apoderadoInputs.forEach(input => {
                input.required = true;
            });
            emailInput.removeAttribute('required');
            emailInfo.classList.remove('hidden');
            if (!emailInput.value) {
                emailInput.value = 'ninguno';
            }
        } else {
            apoderadoStep.style.display = 'none';
            apoderadoInputs.forEach(input => {
                input.required = false;
                input.value = ''; // Limpiar campos de apoderado
            });
            emailInput.setAttribute('required', '');
            emailInfo.classList.add('hidden');
            if (emailInput.value === 'ninguno') {
                emailInput.value = '';
            }
        }
        console.log(`Edad calculada: ${age}, apoderado ${age < 18 ? 'requerido' : 'no requerido'}`);
    }
}

// Validación del teléfono (solo al enviar el formulario, no en cada tecla)
function validatePhone() {
    const phoneInput = document.getElementById('telefono_apoderado');
    if (phoneInput.value && !/^[0-9]{9}$/.test(phoneInput.value)) {
        phoneInput.classList.add('error');
        showFeedback('El teléfono debe contener exactamente 9 dígitos numéricos.', 'error');
        return false;
    }
    phoneInput.classList.remove('error');
    return true;
}

// Establecer fecha de pago actual
function setCurrentDate() {
    const dateInput = document.getElementById('fecha_pago');
    const today = new Date().toISOString().split('T')[0];
    dateInput.value = today;
    console.log(`Fecha de pago establecida: ${today}`);
}

// Manejo de código de operación según medio de pago
function handlePaymentMethod() {
    const paymentMethod = document.getElementById('medio_pago');
    const codeGroup = document.getElementById('codigo_operacion_group');
    const codeInput = document.getElementById('codigo_operacion');

    codeGroup.style.display = 'block';
    codeInput.required = true;
    console.log(`Medio de pago seleccionado: ${paymentMethod.value}`);
}

// Mostrar modal de éxito
function showSuccessModal() {
    successModal.classList.add('show');
}

// Verificar si el email ya está registrado
async function verificarEmail(email) {
    try {
        const response = await fetch('/verificar_email', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `email=${encodeURIComponent(email)}`
        });
        const data = await response.json();
        return data.disponible;
    } catch (error) {
        console.error('Error al verificar email:', error);
        return false;
    }
}

// Navegación: Botones "Siguiente"
nextButtons.forEach(button => {
    button.addEventListener('click', () => {
        if (validateStep(currentStep)) {
            const age = parseInt(document.getElementById('edad').value);
            if (currentStep === 1 && age >= 18) {
                showStep(3); // Saltar apoderado si es mayor de edad
            } else if (currentStep < 4) {
                showStep(currentStep + 1);
            }
        }
    });
});

// Navegación: Botones "Anterior"
prevButtons.forEach(button => {
    button.addEventListener('click', () => {
        const age = parseInt(document.getElementById('edad').value);
        if (currentStep === 3 && age >= 18) {
            showStep(1); // Saltar apoderado si es mayor de edad
        } else if (currentStep > 1) {
            showStep(currentStep - 1);
        }
    });
});

// Enviar formulario
form.addEventListener('submit', async (e) => {
    e.preventDefault();
    console.log('Formulario enviado, iniciando validación y envío...');
    
    const submitBtn = form.querySelector('button[type="submit"]');
    submitBtn.disabled = true; // Deshabilitar botón para evitar envíos múltiples

    // Validar todos los pasos visibles
    const age = parseInt(document.getElementById('edad').value);
    const stepsToValidate = age < 18 ? [1, 2, 3, 4] : [1, 3, 4];
    for (let step of stepsToValidate) {
        if (!validateStep(step)) {
            showStep(step);
            loader.style.display = 'none';
            submitBtn.disabled = false;
            return;
        }
    }

    // Validar teléfono si es requerido
    if (age < 18 && !validatePhone()) {
        showStep(2);
        document.getElementById('telefono_apoderado').focus();
        loader.style.display = 'none';
        submitBtn.disabled = false;
        return;
    }

    // Verificar disponibilidad del email (solo si no es "ninguno")
    const email = document.getElementById('email').value;
    if (email !== 'ninguno' && !await verificarEmail(email)) {
        showFeedback('El correo electrónico ya está registrado. Por favor usa otro.', 'error');
        loader.style.display = 'none';
        submitBtn.disabled = false;
        return;
    }

    loader.style.display = 'flex';
    const formData = new FormData(form);

    // Depuración: Mostrar datos enviados
    for (let [key, value] of formData.entries()) {
        console.log(`Enviando ${key}: ${value}`);
    }

    try {
        const response = await fetch(form.action, {
            method: 'POST',
            body: formData
        });
        const data = await response.json();
        console.log('Respuesta del servidor:', data);

        loader.style.display = 'none';

        if (data.éxito) {
            showSuccessModal();
            modalOkBtn.addEventListener('click', () => {
                window.location.href = data.redireccion || '/'; // Redirigir al index
            });
        } else {
            let mensajeError = data.mensaje;
            if (data.mensaje.includes('Duplicate entry') && data.mensaje.includes('email')) {
                mensajeError = "El correo electrónico ya está registrado. Por favor usa otro.";
            }
            showFeedback(mensajeError, 'error');
            if (data.campo) {
                const input = document.querySelector(`[name="${data.campo}"]`);
                if (input) {
                    const step = input.closest('.voley-form-step').dataset.step;
                    showStep(parseInt(step));
                    input.focus();
                }
            }
        }
    } catch (error) {
        loader.style.display = 'none';
        showFeedback('Error al enviar el formulario: ' + error.message, 'error');
        console.error('Error en fetch:', error);
    } finally {
        submitBtn.disabled = false; // Rehabilitar botón al finalizar
    }
});

// Inicialización
document.getElementById('fecha_nacimiento').addEventListener('input', calculateAge);
document.getElementById('telefono_apoderado').addEventListener('input', () => {
    // No validar en cada tecla, solo marcar como válido si está completo
    const phoneInput = document.getElementById('telefono_apoderado');
    if (phoneInput.value.length === 9 && /^[0-9]{9}$/.test(phoneInput.value)) {
        phoneInput.classList.remove('error');
    }
});
document.getElementById('medio_pago').addEventListener('change', handlePaymentMethod);
window.addEventListener('load', () => {
    calculateAge();
    setCurrentDate();
    handlePaymentMethod();
    console.log('Formulario inicializado');
});

/*ACTUALIZACION DEL CORREO MENSAJES*/
document.addEventListener("DOMContentLoaded", function () {
    const fechaNacimiento = document.getElementById("fecha_nacimiento");
    const edadInput = document.getElementById("edad");
    const emailInput = document.getElementById("email");
    const emailInfo = document.getElementById("email-info");

    // Función para calcular la edad en base a la fecha
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

    // Verificar si debe mostrarse u ocultarse el mensaje según la edad
    function actualizarMensajeEmail() {
        const edad = parseInt(edadInput.value, 10);
        if (!isNaN(edad) && edad < 18) {
            emailInfo.textContent = "En caso de no tener, escriba 'ninguno'.";
        } else {
            emailInfo.textContent = "";
            emailInfo.classList.add("hidden");
        }
    }

    // Cuando cambia la fecha de nacimiento, actualiza edad y mensaje
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

    // Mostrar mensaje al enfocar email si es menor de 18
    emailInput.addEventListener("focus", function () {
        const edad = parseInt(edadInput.value, 10);
        if (!isNaN(edad) && edad < 18) {
            emailInfo.classList.remove("hidden");
        }
    });

    // Ocultar mensaje al salir del campo
    emailInput.addEventListener("blur", function () {
        emailInfo.classList.add("hidden");
    });
});


document.addEventListener('DOMContentLoaded', function() {
    // 1. Configuración inicial
    const form = document.getElementById('registroForm');
    const submitBtn = form.querySelector('button[type="submit"]');
    const loader = document.getElementById('loader');
    const successModal = document.getElementById('success-modal');
    
    // Configurar fecha automática
    document.getElementById('fecha_pago').valueAsDate = new Date();

    // 2. Manejar el envío del formulario
    form.addEventListener('submit', function(e) {
        e.preventDefault(); // ¡IMPORTANTE!
        
        console.log("Formulario enviándose..."); // Debug
        submitBtn.disabled = true;
        loader.style.display = 'flex';

        // Crear FormData
        const formData = new FormData(form);
        
        // Debug: Mostrar datos
        for (let [key, value] of formData.entries()) {
            console.log(`${key}: ${value}`);
        }

        // Enviar datos
        fetch(form.action, {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            console.log("Respuesta:", data);
            
            if (data.éxito) {
                successModal.classList.remove('hidden');
                document.getElementById('modal-ok-btn').onclick = function() {
                    window.location.href = data.redireccion || '/';
                };
            } else {
                alert("Error: " + (data.mensaje || "Error desconocido"));
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Error de conexión");
        })
        .finally(() => {
            submitBtn.disabled = false;
            loader.style.display = 'none';
        });
    });

    // 3. Manejar cambios en medio de pago (opcional)
    document.getElementById('medio_pago').addEventListener('change', function() {
        document.getElementById('codigo_operacion_group').style.display = 
            this.value === 'Efectivo' ? 'none' : 'block';
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('registroForm');
    const feedbackDiv = document.getElementById('form-feedback');
    
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const loader = document.getElementById('loader');
        const submitBtn = form.querySelector('button[type="submit"]');
        
        // Mostrar loader
        loader.style.display = 'flex';
        submitBtn.disabled = true;
        feedbackDiv.classList.add('hidden');

        try {
            const formData = new FormData(form);
            const response = await fetch(form.action, {
                method: 'POST',
                body: formData
            });
            
            const data = await response.json();
            
            if (data.éxito) {
                // Mostrar modal de éxito
                document.getElementById('success-modal').classList.remove('hidden');
                document.getElementById('modal-ok-btn').onclick = function() {
                    window.location.href = data.redireccion || '/';
                };
            } else {
                // Mostrar error al usuario
                showFeedback(data.mensaje || 'Error desconocido', 'error');
                
                // Resaltar campo con error
                if (data.campo) {
                    const input = document.querySelector(`[name="${data.campo}"]`);
                    input?.classList.add('border-red-500');
                    input?.scrollIntoView({ behavior: 'smooth' });
                }
            }
        } catch (error) {
            showFeedback('Error de conexión con el servidor', 'error');
            console.error('Error:', error);
        } finally {
            loader.style.display = 'none';
            submitBtn.disabled = false;
        }
    });

    function showFeedback(message, type) {
        feedbackDiv.textContent = message;
        feedbackDiv.className = `p-4 rounded mb-4 ${
            type === 'error' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'
        }`;
        feedbackDiv.classList.remove('hidden');
    }
});
document.addEventListener('DOMContentLoaded', function() {
    // Configuración inicial
    const form = document.getElementById('registroForm');
    const loader = document.getElementById('loader');
    const feedbackDiv = document.getElementById('form-feedback');

    // Manejar envío
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const submitBtn = form.querySelector('button[type="submit"]');
        submitBtn.disabled = true;
        loader.style.display = 'flex';
        feedbackDiv.classList.add('hidden');

        try {
            const response = await fetch(form.action, {
                method: 'POST',
                body: new FormData(form)
            });
            
            const data = await response.json();
            
            if (!response.ok) throw new Error(data.mensaje || 'Error desconocido');

            if (data.éxito) {
                document.getElementById('success-modal').classList.remove('hidden');
            } else {
                feedbackDiv.textContent = data.mensaje.includes('estructura') 
                    ? 'Error del sistema. Contacte al administrador' 
                    : data.mensaje;
                feedbackDiv.className = 'p-4 rounded bg-red-100 text-red-700';
                feedbackDiv.classList.remove('hidden');
            }
        } catch (error) {
            feedbackDiv.textContent = error.message;
            feedbackDiv.className = 'p-4 rounded bg-red-100 text-red-700';
            feedbackDiv.classList.remove('hidden');
        } finally {
            submitBtn.disabled = false;
            loader.style.display = 'none';
        }
    });
});
// Función mejorada para verificar email
async function verificarEmail(email) {
    // No verificar si el email es "ninguno" o está vacío
    if (email === 'ninguno' || email === '') {
        return true;
    }

    try {
        const response = await fetch('/verificar_email', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email: email })
        });
        
        if (!response.ok) {
            throw new Error('Error en la respuesta del servidor');
        }
        
        const data = await response.json();
        return data.disponible;
    } catch (error) {
        console.error('Error al verificar email:', error);
        // En caso de error, permitir el envío pero mostrar advertencia
        showFeedback('No se pudo verificar el email. Por favor confirme que no esté registrado.', 'warning');
        return true; // Permitir continuar a pesar del error
    }
}

// Modificación en el event listener del formulario
form.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const submitBtn = form.querySelector('button[type="submit"]');
    submitBtn.disabled = true;

    // Validar todos los pasos visibles
    const age = parseInt(document.getElementById('edad').value);
    const stepsToValidate = age < 18 ? [1, 2, 3, 4] : [1, 3, 4];
    
    for (let step of stepsToValidate) {
        if (!validateStep(step)) {
            showStep(step);
            loader.style.display = 'none';
            submitBtn.disabled = false;
            return;
        }
    }

    // Validar teléfono si es requerido
    if (age < 18 && !validatePhone()) {
        showStep(2);
        document.getElementById('telefono_apoderado').focus();
        loader.style.display = 'none';
        submitBtn.disabled = false;
        return;
    }

    // Verificar disponibilidad del email (solo si no es "ninguno")
    const email = document.getElementById('email').value;
    if (email !== 'ninguno' && email !== '') {
        const emailDisponible = await verificarEmail(email);
        if (!emailDisponible) {
            showFeedback('El correo electrónico ya está registrado. Por favor usa otro.', 'error');
            document.getElementById('email').focus();
            loader.style.display = 'none';
            submitBtn.disabled = false;
            return;
        }
    }

    // Resto del código de envío del formulario...
    loader.style.display = 'flex';
    const formData = new FormData(form);

    try {
        const response = await fetch(form.action, {
            method: 'POST',
            body: formData
        });
        const data = await response.json();

        if (data.éxito) {
            showSuccessModal();
        } else {
            showFeedback(data.mensaje, 'error');
        }
    } catch (error) {
        showFeedback('Error de conexión', 'error');
    } finally {
        submitBtn.disabled = false;
        loader.style.display = 'none';
    }
});