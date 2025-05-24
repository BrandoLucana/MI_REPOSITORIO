document.addEventListener('DOMContentLoaded', function() {
    // Elementos del DOM
    const form = document.getElementById('registroForm');
    const steps = document.querySelectorAll('.voley-form-step');
    const progressSteps = document.querySelectorAll('.voley-step');
    const nextButtons = document.querySelectorAll('.voley-next-btn');
    const prevButtons = document.querySelectorAll('.voley-prev-btn');
    
    let currentStep = 0;

    // Función para mostrar el paso actual
    function showStep(stepIndex) {
        steps.forEach((step, index) => {
            step.classList.toggle('active', index === stepIndex);
        });
        
        progressSteps.forEach((step, index) => {
            step.classList.toggle('active', index <= stepIndex);
        });
    }

    // Validar el paso actual antes de avanzar
    function validateStep(stepIndex) {
        const currentStepFields = steps[stepIndex].querySelectorAll('[required]');
        let isValid = true;
        
        currentStepFields.forEach(field => {
            if (!field.value.trim()) {
                field.classList.add('error');
                showFieldError(field, 'Este campo es obligatorio');
                isValid = false;
            } else {
                field.classList.remove('error');
                clearFieldError(field);
            }
        });
        
        return isValid;
    }

    // Mostrar mensaje de error en un campo
    function showFieldError(field, message) {
        clearFieldError(field);
        const errorElement = document.createElement('div');
        errorElement.className = 'error-message';
        errorElement.textContent = message;
        field.parentNode.insertBefore(errorElement, field.nextSibling);
    }

    // Limpiar mensaje de error de un campo
    function clearFieldError(field) {
        const existingError = field.nextElementSibling;
        if (existingError && existingError.classList.contains('error-message')) {
            existingError.remove();
        }
    }

    // Mostrar modal de éxito
    function showSuccessModal() {
        // Crear el modal
        const modal = document.createElement('div');
        modal.className = 'voley-modal';
        modal.innerHTML = `
            <div class="voley-modal-content">
                <div class="voley-modal-icon">
                    <i class="fas fa-check-circle"></i>
                </div>
                <h3>¡Registro Exitoso!</h3>
                <p>El formulario se ha enviado correctamente.</p>
                <button class="voley-modal-button">Aceptar</button>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        // Configurar el botón para redirigir
        const button = modal.querySelector('.voley-modal-button');
        button.addEventListener('click', function() {
            window.location.href = '/'; // Redirigir a la página de inicio
        });
        
        // También redirigir automáticamente después de 3 segundos
        setTimeout(() => {
            window.location.href = '/';
        }, 3000);
    }

    // Event listeners para los botones Siguiente
    nextButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            
            if (validateStep(currentStep)) {
                currentStep++;
                showStep(currentStep);
                form.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        });
    });

    // Event listeners para los botones Anterior
    prevButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            currentStep--;
            showStep(currentStep);
            form.scrollIntoView({ behavior: 'smooth', block: 'start' });
        });
    });

    // Validación en tiempo real para campos requeridos
    form.querySelectorAll('[required]').forEach(field => {
        field.addEventListener('input', function() {
            if (this.value.trim()) {
                this.classList.remove('error');
                clearFieldError(this);
            }
        });
    });

    // Calcular edad automáticamente
    const fechaNacimiento = form.querySelector('[name="fecha_nacimiento"]');
    const edadInput = form.querySelector('[name="edad"]');
    
    if (fechaNacimiento && edadInput) {
        fechaNacimiento.addEventListener('change', function() {
            const birthDate = new Date(this.value);
            if (isNaN(birthDate.getTime())) return;
            
            const today = new Date();
            let age = today.getFullYear() - birthDate.getFullYear();
            const monthDiff = today.getMonth() - birthDate.getMonth();
            
            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }
            
            edadInput.value = age >= 0 ? age : '';
        });
    }

    // Manejo del envío del formulario
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        // Validar todos los pasos antes de enviar
        let formIsValid = true;
        steps.forEach((step, index) => {
            if (!validateStep(index)) {
                if (formIsValid) {
                    currentStep = index;
                    showStep(currentStep);
                    formIsValid = false;
                }
            }
        });
        
        if (!formIsValid) {
            return;
        }
        
        try {
            const formData = new FormData(form);
            const response = await fetch(form.action, {
                method: 'POST',
                body: formData
            });
            
            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor');
            }
            
            const result = await response.json();
            
            if (result.éxito) {
                showSuccessModal();
            } else {
                // Manejar errores del servidor
                if (result.error === 'dato_duplicado') {
                    const inputField = form.querySelector(`[name="${result.campo}"]`);
                    if (inputField) {
                        // Ir al paso que contiene el campo con error
                        const errorStep = inputField.closest('.voley-form-step');
                        if (errorStep) {
                            const stepIndex = Array.from(steps).indexOf(errorStep);
                            currentStep = stepIndex;
                            showStep(currentStep);
                        }
                        
                        inputField.classList.add('error');
                        inputField.focus();
                        showFieldError(inputField, result.mensaje);
                    }
                } else {
                    showNotification('Error: ' + (result.mensaje || 'Error al procesar el registro'), 'error');
                }
            }
        } catch (error) {
            console.error('Error:', error);
            // Solo mostrar error si realmente hay un problema de conexión
            if (error.message.includes('Failed to fetch')) {
                showNotification('Error de conexión con el servidor', 'error');
            }
        }
    });

    // Mostrar el primer paso al cargar la página
    showStep(currentStep);
});

// Función para mostrar notificaciones
function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `voley-notification ${type}`;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.classList.add('fade-out');
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 3000);
}