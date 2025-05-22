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

    // Validación en tiempo real para campos únicos
    function setupUniqueValidation() {
        const uniqueFields = form.querySelectorAll('[data-unique]');
        
        uniqueFields.forEach(field => {
            field.addEventListener('blur', async function() {
                const fieldName = this.name;
                const fieldValue = this.value.trim();
                
                if (fieldValue) {
                    try {
                        const response = await fetch(`/verificar_campo?campo=${fieldName}&valor=${encodeURIComponent(fieldValue)}`);
                        const result = await response.json();
                        
                        if (!result.disponible) {
                            this.classList.add('error');
                            const errorMsg = this.getAttribute(`data-error-${fieldName}`) || 'Este valor ya está registrado';
                            showFieldError(this, errorMsg);
                        } else {
                            this.classList.remove('error');
                            clearFieldError(this);
                        }
                    } catch (error) {
                        console.error('Error al verificar campo único:', error);
                    }
                }
            });
        });
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
            alert('Por favor complete todos los campos requeridos correctamente');
            return;
        }
        
        try {
            const formData = new FormData(form);
            const response = await fetch(form.action, {
                method: 'POST',
                body: formData
            });
            
            const result = await response.json();
            
            if (result.éxito) {
                // Mostrar mensaje de éxito
                alert('Registro exitoso!');
                // Redirigir o reiniciar el formulario
                window.location.href = '/registro-exitoso';
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
                    alert(result.mensaje || 'Error al procesar el registro');
                }
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error de conexión con el servidor');
        }
    });

    // Configurar validación para campos únicos
    setupUniqueValidation();
    
    // Mostrar el primer paso al cargar la página
    showStep(currentStep);
});