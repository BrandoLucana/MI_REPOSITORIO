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

// Mostrar un paso específico con animación
function showStep(step) {
    // Ocultar todos los pasos con animación
    steps.forEach(s => {
        s.style.opacity = '0';
        s.style.transform = 'translateX(20px)';
        setTimeout(() => {
            s.classList.remove('active');
        }, 300);
    });
    
    // Ocultar todos los indicadores de progreso
    progressSteps.forEach(s => s.classList.remove('active'));
    
    // Mostrar el paso actual con animación
    setTimeout(() => {
        const stepElement = document.querySelector(`.voley-form-step[data-step="${step}"]`);
        stepElement.classList.add('active');
        setTimeout(() => {
            stepElement.style.opacity = '1';
            stepElement.style.transform = 'translateX(0)';
        }, 50);
        
        // Activar el indicador de progreso correspondiente
        document.querySelector(`.voley-step[data-step="${step}"]`).classList.add('active');
        currentStep = step;
        
        // Desplazarse al inicio del formulario para mejor experiencia
        form.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 300);
    
    console.log(`Mostrando paso ${step}`);
}

// Validar campos requeridos en un paso
function validateStep(step) {
    const stepElement = document.querySelector(`.voley-form-step[data-step="${step}"]`);
    const inputs = stepElement.querySelectorAll('[required]');
    let isValid = true;
    
    for (let input of inputs) {
        // Ignorar validación de email si es menor de edad (no es requerido)
        if (input.id === 'email' && parseInt(document.getElementById('edad').value) < 18) {
            continue;
        }
        
        if (!input.value || (input.type === 'checkbox' && !input.checked)) {
            input.classList.add('error');
            if (isValid) {
                input.focus();
                const fieldName = input.name.replace(/_/g, ' ').replace('apoderado', 'del apoderado');
                showFeedback(`Por favor, complete el campo ${fieldName}.`, 'error');
                isValid = false;
            }
        } else {
            input.classList.remove('error');
        }
    }
    
    // Validaciones específicas por paso
    if (isValid && step === 1) {
        // Validar nombres y apellidos
        const nombreInput = document.querySelector('input[name="nombre"]');
        const apellidosInput = document.querySelector('input[name="apellidos"]');
        
        if (!validateName(nombreInput)) {
            nombreInput.focus();
            isValid = false;
        }
        
        if (!validateName(apellidosInput)) {
            apellidosInput.focus();
            isValid = false;
        }
        
        // Validar documento
        const docInput = document.getElementById('numero_documento');
        if (!validateDocument(docInput)) {
            docInput.focus();
            isValid = false;
        }
        
        // Validar email
        const emailInput = document.getElementById('email');
        if (!validateEmail(emailInput)) {
            emailInput.focus();
            isValid = false;
        }
    }
    
    if (isValid && step === 2) {
        // Validar nombre de apoderado
        const apoderadoInput = document.querySelector('input[name="nombre_apoderado"]');
        if (apoderadoInput && !validateName(apoderadoInput)) {
            apoderadoInput.focus();
            isValid = false;
        }
        
        // Validar teléfono de apoderado
        if (!validatePhone()) {
            document.getElementById('telefono_apoderado').focus();
            isValid = false;
        }
        
        // Validar documento de apoderado
        const docApoderadoInput = document.getElementById('numero_documento_apoderado');
        if (docApoderadoInput && !validateDocument(docApoderadoInput, true)) {
            docApoderadoInput.focus();
            isValid = false;
        }
    }
    
    if (isValid && step === 4) {
        // Validar código de operación
        if (!validateOperationCode()) {
            document.getElementById('codigo_operacion').focus();
            isValid = false;
        }
    }
    
    return isValid;
}

// Mostrar retroalimentación
function showFeedback(message, type) {
    feedbackDiv.textContent = message;
    feedbackDiv.className = `p-4 rounded mb-4 ${type === 'error' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`;
    feedbackDiv.classList.remove('hidden');
    
    // Animación de entrada
    feedbackDiv.style.opacity = '0';
    feedbackDiv.style.transform = 'translateY(-10px)';
    setTimeout(() => {
        feedbackDiv.style.opacity = '1';
        feedbackDiv.style.transform = 'translateY(0)';
    }, 10);
    
    setTimeout(() => {
        // Animación de salida
        feedbackDiv.style.opacity = '0';
        feedbackDiv.style.transform = 'translateY(-10px)';
        setTimeout(() => {
            feedbackDiv.classList.add('hidden');
        }, 300);
    }, 5000);
    
    console.log(`Feedback mostrado: ${message} (${type})`);
}

// Calcular edad y manejar paso de apoderado
function calculateAge() {
    const birthDateInput = document.getElementById('fecha_nacimiento');
    const ageInput = document.getElementById('edad');
    const emailInput = document.getElementById('email');
    const telefonoInput = document.getElementById('telefono');
    const apoderadoStep = document.getElementById('apoderado-step');
    const apoderadoForm = document.getElementById('apoderado-form');
    const apoderadoInputs = apoderadoForm.querySelectorAll('input, select');
    const emailInfo = document.getElementById('email-info');
    const telefonoInfo = document.getElementById('telefono-info');
    const progressStep2 = document.querySelector('.voley-step[data-step="2"]');

    if (birthDateInput.value) {
        const birthDate = new Date(birthDateInput.value);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        
        ageInput.value = age;

        // Manejar visibilidad del paso de apoderado y barra de progreso
        if (age < 18) {
            // Mostrar paso de apoderado
            apoderadoStep.style.display = 'block';
            setTimeout(() => {
                apoderadoStep.style.opacity = '1';
                apoderadoStep.style.height = 'auto';
                apoderadoStep.style.overflow = 'visible';
            }, 10);
            
            // Mostrar paso 2 en la barra de progreso
            progressStep2.style.display = 'flex';
            
            // Configurar campos como requeridos
            apoderadoInputs.forEach(input => {
                input.required = true;
            });
            
            // Configurar email como opcional con nota
            emailInput.type = 'text';
            emailInput.removeAttribute('required');
            emailInput.setAttribute('data-email-optional', 'true');
            emailInfo.textContent = 'En caso sea menor de edad, escriba "ninguno" si no tiene correo.';
            emailInfo.classList.remove('hidden');
            
            // Configurar teléfono como opcional con nota
            telefonoInput.removeAttribute('required');
            telefonoInfo.textContent = 'En caso sea menor de edad, escriba "ninguno" si no tiene teléfono/celular.';
            telefonoInfo.classList.remove('hidden');
            
            if (!emailInput.value.trim()) {
                emailInput.placeholder = 'ninguno (opcional)';
            }
            if (!telefonoInput.value.trim()) {
                telefonoInput.placeholder = 'ninguno (opcional)';
            }
        } else {
            // Ocultar paso de apoderado
            apoderadoStep.style.opacity = '0';
            apoderadoStep.style.height = '0';
            apoderadoStep.style.overflow = 'hidden';
            setTimeout(() => {
                apoderadoStep.style.display = 'none';
            }, 300);
            
            // Ocultar paso 2 en la barra de progreso
            progressStep2.style.display = 'none';
            
            // Configurar campos como no requeridos
            apoderadoInputs.forEach(input => {
                input.required = false;
                input.value = '';
            });
            
            // Configurar email como requerido
            emailInput.type = 'email';
            emailInput.setAttribute('required', '');
            emailInput.removeAttribute('data-email-optional');
            emailInfo.classList.add('hidden');
            emailInput.placeholder = 'ejemplo@dominio.com (obligatorio)';
            
            // Configurar teléfono como requerido
            telefonoInput.setAttribute('required', '');
            telefonoInfo.classList.add('hidden');
            telefonoInput.placeholder = '912345678 (obligatorio)';
            
            if (emailInput.value.toLowerCase() === 'ninguno') {
                emailInput.value = '';
            }
            if (telefonoInput.value.toLowerCase() === 'ninguno') {
                telefonoInput.value = '';
            }
        }
        console.log(`Edad calculada: ${age}, apoderado ${age < 18 ? 'requerido' : 'no requerido'}`);
    }
}

// Validación de teléfono
function validatePhone() {
    const phoneInput = document.getElementById('telefono');
    const age = parseInt(document.getElementById('edad').value) || 0;
    const value = phoneInput.value.trim().toLowerCase();
    
    if (age >= 18) {
        if (!value || value === 'ninguno') {
            phoneInput.classList.add('error');
            showFeedback('El teléfono es obligatorio para mayores de edad.', 'error');
            return false;
        }
        if (!/^[0-9]{9}$/.test(value)) {
            phoneInput.classList.add('error');
            showFeedback('El teléfono debe contener exactamente 9 dígitos numéricos.', 'error');
            return false;
        }
    } else {
        if (value && value !== 'ninguno' && !/^[0-9]{9}$/.test(value)) {
            phoneInput.classList.add('error');
            showFeedback('Ingrese 9 dígitos o escriba "ninguno" si no tiene teléfono.', 'error');
            return false;
        }
    }
    
    phoneInput.classList.remove('error');
    return true;
}

document.getElementById('telefono').addEventListener('input', function() {
    const value = this.value.toLowerCase();
    
    // Si el usuario comienza a escribir "ninguno", permitirlo
    if (value.startsWith('n')) {
        // No hacer nada, permitir que escriba "ninguno"
    } 
    // Si no, permitir solo números y máximo 9 caracteres
    else {
        this.value = this.value.replace(/[^0-9]/g, '');
        if (this.value.length > 9) {
            this.value = this.value.substring(0, 9);
        }
    }
});

// Validación de nombres y apellidos
function validateName(input) {
    const value = input.value.trim();
    // Eliminar espacios múltiples y dejar solo uno
    const normalizedValue = value.replace(/\s+/g, ' ');
    
    // Validar que solo contenga letras y espacios
    if (!/^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$/.test(normalizedValue)) {
        input.classList.add('error');
        showFeedback('Solo se permiten letras y espacios en los nombres.', 'error');
        return false;
    }
    
    // Validar longitud máxima
    if (normalizedValue.length > 30) {
        input.classList.add('error');
        showFeedback('El nombre no puede exceder los 30 caracteres.', 'error');
        return false;
    }
    
    input.value = normalizedValue;
    input.classList.remove('error');
    return true;
}

// Validación de documentos
function validateDocument(input, isApoderado = false) {
    const value = input.value.trim();
    const tipoDocumento = isApoderado 
        ? document.getElementById('tipo_documento_apoderado').value 
        : document.getElementById('tipo_documento').value;
    
    if (tipoDocumento === 'DNI') {
        // Validar DNI: exactamente 8 dígitos
        if (!/^[0-9]{8}$/.test(value)) {
            input.classList.add('error');
            showFeedback('El DNI debe contener exactamente 8 dígitos numéricos.', 'error');
            return false;
        }
    } else {
        // Validar Carnet de Extranjería: 8-12 caracteres alfanuméricos
        if (!/^[A-Za-z0-9]{8,12}$/.test(value)) {
            input.classList.add('error');
            showFeedback('El Carnet de Extranjería debe tener entre 8 y 12 caracteres alfanuméricos (sin espacios ni símbolos).', 'error');
            return false;
        }
    }
    
    input.classList.remove('error');
    return true;
}

// Restringir entrada de documentos según tipo
function restrictDocumentInput(input, isApoderado = false) {
    const tipoDocumento = isApoderado 
        ? document.getElementById('tipo_documento_apoderado').value 
        : document.getElementById('tipo_documento').value;
    
    if (tipoDocumento === 'DNI') {
        // Solo permitir números y máximo 8 caracteres
        input.value = input.value.replace(/[^0-9]/g, '');
        if (input.value.length > 8) {
            input.value = input.value.substring(0, 8);
        }
    } else {
        // Permitir letras y números, máximo 12 caracteres
        input.value = input.value.replace(/[^A-Za-z0-9]/g, '');
        if (input.value.length > 12) {
            input.value = this.value.substring(0, 12);
        }
    }
}

// Manejo de medio de pago
function handlePaymentMethod() {
    const paymentMethod = document.getElementById('medio_pago').value;
    const codeGroup = document.getElementById('codigo_operacion_group');
    const codeInput = document.getElementById('codigo_operacion');
    
    if (paymentMethod === 'Efectivo') {
        codeGroup.style.opacity = '0';
        codeGroup.style.height = '0';
        codeGroup.style.overflow = 'hidden';
        setTimeout(() => {
            codeGroup.style.display = 'none';
        }, 300);
        codeInput.removeAttribute('required');
        codeInput.value = '';
    } else {
        codeGroup.style.display = 'block';
        setTimeout(() => {
            codeGroup.style.opacity = '1';
            codeGroup.style.height = 'auto';
            codeGroup.style.overflow = 'visible';
        }, 10);
        codeInput.setAttribute('required', '');
        
        // Cambiar el patrón según el método de pago
        if (paymentMethod === 'Yape') {
            codeInput.setAttribute('pattern', '^[0-9]{8,12}$');
            codeInput.setAttribute('title', 'Ingrese 8 a 12 dígitos numéricos');
        } else { // Transferencia
            codeInput.setAttribute('pattern', '^[A-Za-z0-9]{12}$');
            codeInput.setAttribute('title', 'Ingrese exactamente 12 caracteres alfanuméricos');
        }
    }
    console.log(`Medio de pago seleccionado: ${paymentMethod}`);
}

// Validación de código de operación
function validateOperationCode() {
    const paymentMethod = document.getElementById('medio_pago').value;
    const codeInput = document.getElementById('codigo_operacion');
    
    // Si es efectivo, no se necesita validación
    if (paymentMethod === 'Efectivo') {
        codeInput.value = ''; // Limpiar el campo si es efectivo
        return true;
    }
    
    const value = codeInput.value.trim();
    
    if (paymentMethod === 'Yape') {
        // Validación para Yape: 8-12 dígitos numéricos
        if (!/^[0-9]{8,12}$/.test(value)) {
            codeInput.classList.add('error');
            showFeedback('El código de Yape debe contener entre 8 y 12 dígitos numéricos (sin espacios ni caracteres especiales).', 'error');
            return false;
        }
    } else if (paymentMethod === 'Transferencia') {
        // Validación para Transferencia: exactamente 12 caracteres alfanuméricos
        if (!/^[A-Za-z0-9]{12}$/.test(value)) {
            codeInput.classList.add('error');
            showFeedback('El código de transferencia debe contener exactamente 12 caracteres alfanuméricos (sin espacios ni caracteres especiales).', 'error');
            return false;
        }
    }
    
    // Si pasa todas las validaciones
    codeInput.classList.remove('error');
    return true;
}

// Restringir entrada del código de operación
document.getElementById('codigo_operacion').addEventListener('input', function() {
    const paymentMethod = document.getElementById('medio_pago').value;
    
    if (paymentMethod === 'Yape') {
        // Solo permitir números, máximo 12 caracteres
        this.value = this.value.replace(/[^0-9]/g, '');
        if (this.value.length > 12) {
            this.value = this.value.substring(0, 12);
        }
    } else if (paymentMethod === 'Transferencia') {
        // Solo permitir letras y números, exactamente 12 caracteres
        this.value = this.value.replace(/[^A-Za-z0-9]/g, '');
        if (this.value.length > 12) {
            this.value = this.value.substring(0, 12);
        }
    }
});

// Validación de email
function validateEmail(emailInput) {
    const email = emailInput.value.trim().toLowerCase();
    const age = parseInt(document.getElementById('edad').value) || 0;
    const isOptional = emailInput.hasAttribute('data-email-optional');
    
    // Para mayores de edad o cuando no es opcional
    if (!isOptional) {
        if (!email) {
            emailInput.classList.add('error');
            showFeedback('El correo electrónico es obligatorio.', 'error');
            return false;
        }
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            emailInput.classList.add('error');
            showFeedback('Por favor ingrese un correo electrónico válido.', 'error');
            return false;
        }
    } 
    // Para menores de edad (cuando es opcional)
    else {
        if (email && email !== 'ninguno' && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            emailInput.classList.add('error');
            showFeedback('Ingrese un correo válido o escriba "ninguno" si no tiene.', 'error');
            return false;
        }
    }
    
    emailInput.classList.remove('error');
    return true;
}

// Verificar si el email ya está registrado
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
        showFeedback('No se pudo verificar el email. Por favor confirme que no esté registrado.', 'warning');
        return true; // Permitir continuar a pesar del error
    }
}

// Validar DNI único
document.getElementById('numero_documento').addEventListener('blur', function() {
    const tipo_documento = document.getElementById('tipo_documento').value;
    const numero_documento = this.value.trim();
    
    if (!numero_documento) return;
    
    fetch('/verificar_documento', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            tipo_documento: tipo_documento,
            numero_documento: numero_documento
        })
    })
    .then(response => response.json())
    .then(data => {
        if (!data.disponible) {
            let mensaje = 'Este documento ya está registrado. ';
            if (data.es_usuario) mensaje += 'Como usuario principal. ';
            if (data.es_apoderado) mensaje += 'Como apoderado.';
            
            Swal.fire({
                title: 'Documento ya registrado',
                text: mensaje,
                icon: 'error',
                confirmButtonText: 'Entendido'
            });
            
            document.getElementById('numero_documento').value = '';
            document.getElementById('numero_documento').focus();
        }
    });
});

// Función para mostrar el modal de éxito
function showSuccessModal() {
    const successModal = document.getElementById('success-modal');
    successModal.classList.remove('hidden');
    
    // Redirige automáticamente después de 5 segundos
    setTimeout(() => {
        window.location.href = '/';
    }, 5000);
}

// Establecer fecha de pago actual
function setCurrentDate() {
    const dateInput = document.getElementById('fecha_pago');
    const today = new Date().toISOString().split('T')[0];
    dateInput.value = today;
    console.log(`Fecha de pago establecida: ${today}`);
}

// Navegación: Botones "Siguiente"
nextButtons.forEach(button => {
    button.addEventListener('click', (e) => {
        e.preventDefault();
        if (validateStep(currentStep)) {
            const age = parseInt(document.getElementById('edad').value) || 0;
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
    button.addEventListener('click', (e) => {
        e.preventDefault();
        const age = parseInt(document.getElementById('edad').value) || 0;
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
    submitBtn.disabled = true;
    loader.style.display = 'flex';

    // Validar todos los pasos visibles
    const age = parseInt(document.getElementById('edad').value) || 0;
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

    // Verificar disponibilidad del email (solo si no es "ninguno" o vacío)
    const email = document.getElementById('email').value.trim().toLowerCase();
    if (email && email !== 'ninguno') {
        const emailDisponible = await verificarEmail(email);
        if (!emailDisponible) {
            showFeedback('El correo electrónico ya está registrado. Por favor usa otro.', 'error');
            document.getElementById('email').focus();
            loader.style.display = 'none';
            submitBtn.disabled = false;
            return;
        }
    }

    try {
        const formData = new FormData(form);
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
                window.location.href = data.redireccion || '/';
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
        submitBtn.disabled = false;
    }
});

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    // Configurar formulario para deshabilitar validación HTML5
    form.noValidate = true;
    
    // Mostrar todos los pasos en la barra de progreso inicialmente
    progressSteps.forEach(step => {
        step.style.display = 'flex';
    });
    
    // Configurar eventos para campos de nombres
    document.querySelectorAll('input[name="nombre"], input[name="apellidos"], input[name="nombre_apoderado"]').forEach(input => {
        input.addEventListener('input', function() {
            // Prevenir espacios al inicio
            if (this.value.startsWith(' ')) {
                this.value = this.value.trim();
            }
            // Restringir a solo letras y espacios
            this.value = this.value.replace(/[^A-Za-zÁÉÍÓÚáéíóúñÑ ]/g, '');
        });
    });

    // Configurar eventos para documentos
    document.getElementById('numero_documento').addEventListener('input', function() {
        restrictDocumentInput(this);
    });
    document.getElementById('numero_documento_apoderado').addEventListener('input', function() {
        restrictDocumentInput(this, true);
    });

    // Configurar eventos principales
    document.getElementById('fecha_nacimiento').addEventListener('change', calculateAge);
    document.getElementById('telefono_apoderado').addEventListener('input', function() {
        // Solo permitir números y máximo 9 caracteres
        this.value = this.value.replace(/[^0-9]/g, '');
        if (this.value.length > 9) {
            this.value = this.value.substring(0, 9);
        }
    });
    document.getElementById('medio_pago').addEventListener('change', handlePaymentMethod);
    
    // Configurar fecha y validaciones iniciales
    setCurrentDate();
    calculateAge();
    handlePaymentMethod();
    
    console.log('Formulario inicializado correctamente');
});