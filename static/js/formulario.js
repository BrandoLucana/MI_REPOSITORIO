document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('registroForm');
    const steps = document.querySelectorAll('.voley-form-step');
    const progressSteps = document.querySelectorAll('.voley-step');
    const nextButtons = document.querySelectorAll('.voley-next-btn');
    const prevButtons = document.querySelectorAll('.voley-prev-btn');
    
    let currentStep = 0;

    function showStep(stepIndex) {
        steps.forEach((step, index) => {
            step.classList.toggle('active', index === stepIndex);
        });
        
        progressSteps.forEach((step, index) => {
            step.classList.toggle('active', index <= stepIndex);
        });
    }

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

            if (field.type === 'email' && field.value.trim() !== '') {
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailRegex.test(field.value.trim())) {
                    field.classList.add('error');
                    showFieldError(field, 'Ingrese un correo electrónico válido');
                    isValid = false;
                }
            }
        });
        
        return isValid;
    }

    function showFieldError(field, message) {
        clearFieldError(field);
        const errorElement = document.createElement('div');
        errorElement.className = 'error-message';
        errorElement.textContent = message;
        field.parentNode.insertBefore(errorElement, field.nextSibling);
    }

    function clearFieldError(field) {
        const existingError = field.nextElementSibling;
        if (existingError && existingError.classList.contains('error-message')) {
            existingError.remove();
        }
    }

    function showSuccessModal() {
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
        
        const button = modal.querySelector('.voley-modal-button');
        button.addEventListener('click', function() {
            window.location.href = '/';
        });
        
        setTimeout(() => {
            window.location.href = '/';
        }, 3000);
    }

    function showErrorModal(message) {
        const modal = document.createElement('div');
        modal.className = 'voley-modal';
        modal.innerHTML = `
            <div class="voley-modal-content">
                <div class="voley-modal-icon" style="color: var(--voley-red);">
                    <i class="fas fa-exclamation-circle"></i>
                </div>
                <h3 style="color: var(--voley-red);">Error en el Registro</h3>
                <p>${message}</p>
                <button class="voley-modal-button" style="background-color: var(--voley-red);">Entendido</button>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        const button = modal.querySelector('.voley-modal-button');
        button.addEventListener('click', function() {
            modal.remove();
        });
    }

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

    prevButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            currentStep--;
            showStep(currentStep);
            form.scrollIntoView({ behavior: 'smooth', block: 'start' });
        });
    });

    form.querySelectorAll('[required]').forEach(field => {
        field.addEventListener('input', function() {
            if (this.value.trim()) {
                this.classList.remove('error');
                clearFieldError(this);
            }
        });
    });

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

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
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
            showErrorModal('Por favor complete todos los campos requeridos');
            return;
        }
        
        const loader = document.getElementById('loader');
        loader.style.display = 'flex';
        
        try {
            // Crear objeto con los datos del formulario
            const formData = {
                nombre: form.querySelector('[name="nombre"]').value.trim(),
                apellidos: form.querySelector('[name="apellidos"]').value.trim(),
                fecha_nacimiento: form.querySelector('[name="fecha_nacimiento"]').value,
                edad: form.querySelector('[name="edad"]').value,
                email: form.querySelector('[name="email"]').value.trim(),
                direccion: form.querySelector('[name="direccion"]').value.trim(),
                nombre_apoderado: form.querySelector('[name="nombre_apoderado"]').value.trim(),
                telefono_apoderado: form.querySelector('[name="telefono_apoderado"]').value.trim(),
                correo_apoderado: form.querySelector('[name="correo_apoderado"]').value.trim(),
                centro_salud: form.querySelector('[name="centro_salud"]').value.trim(),
                tipo_documento: form.querySelector('[name="tipo_documento"]').value,
                numero_documento: form.querySelector('[name="numero_documento"]').value.trim(),
                nivel_actual: form.querySelector('[name="nivel_actual"]').value,
                practico_deporte: form.querySelector('[name="practico_deporte"]:checked')?.value || 'no',
                posicion: form.querySelector('[name="posicion"]').value.trim(),
                seguro_medico: form.querySelector('[name="seguro_medico"]').checked ? '1' : '0'
            };

            console.log('Datos a enviar:', formData);

            const response = await fetch(form.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensaje || 'Error en el servidor');
            }
            
            const result = await response.json();
            
            if (result.éxito) {
                showSuccessModal();
            } else {
                showErrorModal(result.mensaje || 'Error al procesar el registro');
                
                if (result.campo) {
                    const inputField = form.querySelector(`[name="${result.campo}"]`);
                    if (inputField) {
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
                }
            }
        } catch (error) {
            console.error('Error:', error);
            showErrorModal(error.message || 'Error de conexión con el servidor');
        } finally {
            loader.style.display = 'none';
        }
    });

    showStep(currentStep);
});