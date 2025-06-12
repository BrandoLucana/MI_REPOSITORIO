from flask import Flask, render_template, request, jsonify, redirect, url_for, flash
import mysql.connector
from datetime import datetime
import logging

# Configuración básica de logging para ver mensajes de depuración en la consola
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

# Inicialización de la aplicación Flask
app = Flask(__name__)
# Clave secreta para proteger las sesiones de Flask y los mensajes flash.
# ¡IMPORTANTE! Cambia 'tu_clave_secreta_aqui' por una cadena de caracteres compleja y única en producción.
app.secret_key = 'tu_clave_secreta_aqui'

# Configuración y función para obtener la conexión a la base de datos
# Incluye manejo de errores para una conexión fallida
def get_db_connection():
    try:
        conn = mysql.connector.connect(
            host="database4pr.cqq3cpmmrkby.us-east-1.rds.amazonaws.com",
            user="admin",
            password="73144231soto",
            database="pasion_por_el_voley",
            connect_timeout=5  # Tiempo de espera para la conexión en segundos
        )
        logger.info("Conexión a la base de datos establecida correctamente")
        return conn
    except mysql.connector.Error as err:
        logger.error(f"Error de conexión a la base de datos: {err}")
        return None

# Ruta para probar la conexión a la base de datos
# Útil para diagnosticar problemas de conexión
@app.route('/test_db')
def test_db():
    try:
        conn = get_db_connection()
        if conn is None:
            return jsonify({"status": "error", "message": "No se pudo conectar a la base de datos"})
        
        cursor = conn.cursor()
        cursor.execute("SHOW TABLES")
        tables = cursor.fetchall()  # Obtiene los nombres de las tablas en la base de datos
        cursor.close()
        conn.close()
        
        return jsonify({
            "status": "success",
            "tables": tables,
            "message": "Conexión exitosa a la base de datos"
        })
    except Exception as e:
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500


# --- Rutas principales de la aplicación web ---

# Ruta para la página de inicio
@app.route('/')
def index():
    return render_template('index.html')

# Ruta para el formulario de registro unificado de usuarios
@app.route('/formulario')
def formulario():
    return render_template('formulario_unificado.html')

# Ruta para mostrar la lista de usuarios registrados
@app.route('/lista_usuarios')
def lista_usuarios():
    return render_template('lista_usuarios.html')

# Ruta para manejar el registro de nuevos usuarios (POST)
@app.route('/registrar_usuario', methods=['POST'])
def registrar_usuario():
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True) # El cursor en formato de diccionario facilita el acceso a los datos
        
        # Validar que no sea un intento de edición (según la política de privacidad)
        if 'usuario_id' in request.form:
            return jsonify({
                "éxito": False,
                "mensaje": "La edición de usuarios no está permitida por políticas de privacidad"
            }), 403
        
        # Lista de campos requeridos del formulario para validación
        campos_requeridos = ['nombre', 'apellidos', 'fecha_nacimiento', 'edad', 'email', 
                           'nombre_apoderado', 'telefono_apoderado', 'centro_salud', 
                           'tipo_documento', 'numero_documento']
        
        # Verificar que todos los campos requeridos estén presentes
        for campo in campos_requeridos:
            if not request.form.get(campo):
                return jsonify({
                    "éxito": False,
                    "mensaje": f"El campo {campo.replace('_', ' ')} es requerido",
                    "campo": campo
                }), 400
        
        # Recopilar datos personales del formulario
        datos_personales = {
            'nombre': request.form['nombre'],
            'apellidos': request.form['apellidos'],
            'fecha_nacimiento': request.form['fecha_nacimiento'],
            'edad': int(request.form['edad']),
            'email': request.form['email'],
            'direccion': request.form.get('direccion', ''), # 'direccion' es opcional
            'acepto_terminos': True, # Asumimos que la aceptación de términos es por defecto al enviar
            'fecha_registro': datetime.now().strftime('%Y-%m-%d %H:%M:%S') # Fecha actual de registro
        }
        
        # Insertar nuevo usuario en la tabla 'usuarios'
        cursor.execute("""
            INSERT INTO usuarios (nombre, apellidos, fecha_nacimiento, edad, email, 
                                 direccion, acepto_terminos, fecha_registro)
            VALUES (%(nombre)s, %(apellidos)s, %(fecha_nacimiento)s, %(edad)s, 
                   %(email)s, %(direccion)s, %(acepto_terminos)s, %(fecha_registro)s)
        """, datos_personales)
        
        usuario_id = cursor.lastrowid # Obtener el ID del usuario recién insertado
        
        # Recopilar datos del apoderado del formulario
        datos_apoderado = {
            'usuario_id': usuario_id, # Usar el ID del usuario como clave foránea
            'nombre_apoderado': request.form['nombre_apoderado'],
            'telefono_apoderado': request.form['telefono_apoderado'],
            'correo_apoderado': request.form.get('correo_apoderado', ''), # 'correo_apoderado' es opcional
            'centro_salud': request.form['centro_salud'],
            'tipo_documento': request.form['tipo_documento'],
            'numero_documento': request.form['numero_documento']
        }
        
        # Insertar datos del apoderado en la tabla 'apoderados'
        cursor.execute("""
            INSERT INTO apoderados (usuario_id, nombre_apoderado, telefono_apoderado, 
                                  correo_apoderado, centro_salud, tipo_documento, numero_documento)
            VALUES (%(usuario_id)s, %(nombre_apoderado)s, %(telefono_apoderado)s, 
                   %(correo_apoderado)s, %(centro_salud)s, %(tipo_documento)s, %(numero_documento)s)
        """, datos_apoderado)
        
        # Recopilar datos deportivos del formulario
        datos_deportivos = {
            'usuario_id': usuario_id, # Usar el ID del usuario como clave foránea
            'nivel_actual': request.form.get('nivel_actual', 'Principiante'), # Valor por defecto si no se proporciona
            'practico_deporte': 1 if request.form.get('practico_deporte') == 'si' else 0, # Convertir booleano a entero
            'posicion': request.form.get('posicion', ''), # 'posicion' es opcional
            'seguro_medico': 1 if request.form.get('seguro_medico') else 0 # Convertir booleano a entero
        }
        
        # Insertar datos deportivos en la tabla 'datos_deportivos'
        cursor.execute("""
            INSERT INTO datos_deportivos (usuario_id, nivel_actual, practico_deporte, posicion, seguro_medico)
            VALUES (%(usuario_id)s, %(nivel_actual)s, %(practico_deporte)s, %(posicion)s, %(seguro_medico)s)
        """, datos_deportivos)
        
        conn.commit() # Confirmar los cambios en la base de datos
        
        # Devolver una respuesta JSON indicando el éxito y una URL de redirección
        return jsonify({
            "éxito": True,
            "mensaje": "Usuario registrado exitosamente!",
            "redireccion": url_for('lista_usuarios') # Redirigir a la lista de usuarios
        })
        
    except mysql.connector.IntegrityError as e:
        # Manejar errores de integridad (ej. claves duplicadas)
        if conn:
            conn.rollback() # Revertir la transacción si hay un error
        
        if "Duplicate entry" in str(e):
            campo = "email" if "email" in str(e) else "numero_documento" # Identificar el campo duplicado
            return jsonify({
                "éxito": False,
                "mensaje": f"El {campo.replace('_', ' ')} ya está registrado",
                "campo": campo
            }), 400
        
        return jsonify({
            "éxito": False,
            "mensaje": f"Error de base de datos: {str(e)}"
        }), 500
        
    except Exception as e:
        # Manejar cualquier otro tipo de error inesperado
        if conn:
            conn.rollback() # Revertir la transacción
        return jsonify({
            "éxito": False,
            "mensaje": f'Error al registrar usuario: {str(e)}'
        }), 500
    finally:
        # Asegurarse de cerrar el cursor y la conexión a la base de datos
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Ruta para obtener la lista de usuarios (para mostrar en 'lista_usuarios.html')
@app.route('/obtener_usuarios')
def obtener_usuarios():
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True) # Cursor en formato de diccionario
        
        # Consulta SQL para unir datos de usuarios, apoderados y datos deportivos
        query = """
        SELECT u.id, u.nombre, u.apellidos, u.edad, u.email, u.fecha_registro,
               a.nombre_apoderado, a.telefono_apoderado,
               d.nivel_actual, d.posicion
        FROM usuarios u
        LEFT JOIN apoderados a ON u.id = a.usuario_id
        LEFT JOIN datos_deportivos d ON u.id = d.usuario_id
        WHERE 1=1
        """
        
        params = []
        # Parámetros de búsqueda opcionales (nombre y nivel)
        nombre = request.args.get('nombre')
        nivel = request.args.get('nivel')
        
        if nombre:
            query += " AND (u.nombre LIKE %s OR u.apellidos LIKE %s)"
            params.extend([f"%{nombre}%", f"%{nombre}%"])
            
        if nivel:
            query += " AND d.nivel_actual = %s"
            params.append(nivel)
        
        cursor.execute(query, params)
        usuarios = cursor.fetchall() # Obtener todos los resultados
        
        return jsonify(usuarios) # Devolver los usuarios en formato JSON
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Ruta para eliminar un usuario por su ID (POST)
@app.route('/eliminar_usuario/<int:id>', methods=['POST'])
def eliminar_usuario(id):
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        
        # Eliminar registros en el orden correcto para evitar errores de clave foránea
        # (tablas dependientes primero, luego la principal)
        cursor.execute("DELETE FROM datos_deportivos WHERE usuario_id = %s", (id,))
        cursor.execute("DELETE FROM apoderados WHERE usuario_id = %s", (id,))
        cursor.execute("DELETE FROM usuarios WHERE id = %s", (id,))
        
        conn.commit() # Confirmar los cambios
        return jsonify({
            "éxito": True,
            "mensaje": "Usuario eliminado exitosamente!"
        })
        
    except Exception as e:
        if conn:
            conn.rollback() # Revertir si hay un error
        return jsonify({
            "éxito": False,
            "mensaje": f"Error al eliminar usuario: {str(e)}"
        }), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Ruta para la página de horarios
@app.route('/horarios')
def horarios():
    return render_template('horarios.html')

# Ruta para la página 'Dónde Estamos'
@app.route('/donde_estamos')
def donde_estamos():
    return render_template('donde_estamos.html')

# Ruta para la página de contacto
@app.route('/contactanos')
def contactanos():
    return render_template('contactanos.html')

# Ruta para la página de torneos (muestra información sobre torneos disponibles)
@app.route('/torneos')
def torneos():
    # Asumo que esta plantilla se llama 'torneos.html' como en tu HTML,
    # aunque antes lo llamabas 'torneo.html'. Asegúrate de que el nombre sea el correcto.
    return render_template('torneo.html')

# Ruta para el formulario de inscripción a torneos (GET para mostrar, POST para procesar)
@app.route('/inscripcion-torneo', methods=['GET', 'POST'])
def inscripcion_torneo():
    if request.method == 'POST':
        # Recoger datos del formulario de inscripción a torneo
        nombre_equipo = request.form['nombre_equipo']
        categoria = request.form['categoria']
        delegado = request.form['delegado']
        telefono = request.form['telefono']
        email = request.form['email']
        
        conn = None
        cursor = None
        try:
            conn = get_db_connection()
            if conn is None:
                flash('No se pudo conectar a la base de datos para la inscripción.', 'error')
                return redirect(url_for('inscripcion_torneo'))

            cursor = conn.cursor()

            # Insertar los datos de la inscripción del equipo en la tabla 'inscripciones_equipos_torneo'
            # ¡IMPORTANTE! Asegúrate de que esta tabla exista en tu base de datos con las columnas adecuadas.
            # Ejemplo de CREATE TABLE para esta tabla:
            # CREATE TABLE IF NOT EXISTS inscripciones_equipos_torneo (
            #     id INT AUTO_INCREMENT PRIMARY KEY,
            #     nombre_equipo VARCHAR(255) NOT NULL,
            #     categoria VARCHAR(50) NOT NULL,
            #     delegado VARCHAR(255) NOT NULL,
            #     telefono VARCHAR(20),
            #     email VARCHAR(100),
            #     fecha_inscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            # );
            sql = """
            INSERT INTO inscripciones_equipos_torneo (nombre_equipo, categoria, delegado, telefono, email, fecha_inscripcion)
            VALUES (%s, %s, %s, %s, %s, NOW())
            """
            cursor.execute(sql, (nombre_equipo, categoria, delegado, telefono, email))
            conn.commit()
            
            flash('¡Inscripción al torneo realizada con éxito!', 'success')
            return redirect(url_for('torneos'))  # Redirigir a la página de torneos o a una página de confirmación

        except mysql.connector.Error as err:
            logger.error(f"Error al guardar inscripción de torneo: {err}")
            if conn:
                conn.rollback() # Revertir transacción si hay un error de DB
            flash(f'Error al procesar tu inscripción: {err}', 'error')
            # Renderiza el formulario de nuevo, manteniendo los datos para que el usuario no los pierda
            return render_template('inscripcion-torneo.html', 
                                    nombre_equipo=nombre_equipo, categoria=categoria, 
                                    delegado=delegado, telefono=telefono, email=email)
        except Exception as e:
            logger.error(f"Error inesperado en inscripción de torneo: {e}")
            if conn:
                conn.rollback() # Revertir transacción si hay un error inesperado
            flash(f'Ocurrió un error inesperado. Por favor, inténtalo de nuevo.', 'error')
            # Renderiza el formulario de nuevo, manteniendo los datos
            return render_template('inscripcion-torneo.html', 
                                    nombre_equipo=nombre_equipo, categoria=categoria, 
                                    delegado=delegado, telefono=telefono, email=email)
        finally:
            # Cerrar cursor y conexión en cualquier caso
            if cursor:
                cursor.close()
            if conn:
                conn.close()
    
    # Si la solicitud es GET, simplemente muestra el formulario de inscripción al torneo
    return render_template('inscripcion-torneo.html')


# Ruta para el registro de nuevos torneos (solo accesible por entrenadores)
@app.route('/registro-torneo', methods=['GET', 'POST'])
def registro_torneo():
    conn = None
    cursor = None
    if request.method == 'POST':
        nombre_torneo = request.form.get('nombre', '').strip()
        fecha = request.form.get('fecha', '').strip()
        lugar = request.form.get('lugar', '').strip()
        nivel = request.form.get('nivel', '').strip()
        descripcion = request.form.get('descripcion', '').strip()
        estado = request.form.get('estado', '').strip()

        # Validación básica de campos
        if not all([nombre_torneo, fecha, lugar, nivel, estado]):
            flash('Todos los campos son obligatorios excepto la descripción.', 'error')
            return redirect(url_for('registro_torneo'))

        try:
            conn = get_db_connection()
            if conn is None:
                flash('No se pudo conectar a la base de datos para registrar el torneo.', 'error')
                logger.error('Conexión a la base de datos fallida.')
                return redirect(url_for('registro_torneo'))

            cursor = conn.cursor()

            sql = """
                INSERT INTO registro_torneo (nombre, fecha, lugar, nivel, descripcion, estado)
                VALUES (%s, %s, %s, %s, %s, %s)
            """
            cursor.execute(sql, (nombre_torneo, fecha, lugar, nivel, descripcion, estado))

            conn.commit()
            logger.info(f'Torneo "{nombre_torneo}" registrado exitosamente.')
            flash('Torneo registrado exitosamente!', 'success')
            return redirect(url_for('registro_torneo'))

        except mysql.connector.Error as e:
            logger.error(f'Error al registrar torneo: {str(e)}')
            if conn:
                conn.rollback()
            flash(f'Error al registrar torneo: {str(e)}', 'error')
            return redirect(url_for('registro_torneo'))
        except Exception as e:
            logger.error(f'Error inesperado al registrar torneo: {str(e)}')
            if conn:
                conn.rollback()
            flash(f'Error inesperado al registrar torneo: {str(e)}', 'error')
            return redirect(url_for('registro_torneo'))
        finally:
            if cursor:
                cursor.close()
            if conn:
                conn.close()
    
    elif request.method == 'GET':
        torneos_existentes = []
        try:
            conn = get_db_connection()
            if conn:
                cursor = conn.cursor(dictionary=True)
                cursor.execute("SELECT id, nombre, fecha, lugar, nivel, descripcion, estado FROM registro_torneo")
                torneos_existentes = cursor.fetchall()
                logger.info(f'Cargados {len(torneos_existentes)} torneos existentes.')
            else:
                flash('No se pudo conectar a la base de datos para cargar los torneos.', 'error')
                logger.error('Conexión fallida al cargar torneos.')
        except mysql.connector.Error as e:
            logger.error(f'Error al cargar torneos: {str(e)}')
            flash(f'Error al cargar torneos: {str(e)}', 'error')
        except Exception as e:
            logger.error(f'Error inesperado al cargar torneos: {str(e)}')
            flash(f'Error inesperado al cargar torneos: {str(e)}', 'error')
        finally:
            if cursor:
                cursor.close()
            if conn:
                conn.close()
        
        return render_template('registro_torneo.html', torneos=torneos_existentes)

# Ruta para manejar el login de entrenadores (POST)
@app.route('/login-entrenador', methods=['POST'])
def login_entrenador():
    usuario = request.form['usuario']
    contrasena = request.form['contrasena']

    # Validación simple de ejemplo (¡NO USAR EN PRODUCCIÓN! Esto es para fines demostrativos)
    # En un entorno real, usarías una base de datos y un hash seguro para las contraseñas.
    if usuario == "admin" and contrasena == "1234":
        flash("¡Bienvenido, Entrenador!", "success")
        return redirect(url_for('registro_torneo')) # Redirigir a la página de registro de torneos
    else:
        flash("Usuario o contraseña incorrectos", "error")
        # Asumo que '/torneos' es la página desde donde se intenta el login de entrenador
        return redirect(url_for('index')) 

# Rutas para otras páginas de contenido
@app.route("/mas")
def mas():
    return render_template('mas.html')

@app.route("/productos")
def productos():
    return render_template('productos.html')

@app.route("/galeria")
def galeria():
    return render_template('galeria.html')

@app.route("/eventos")
def eventos():
    return render_template('eventos.html')

@app.route("/noticias")
def noticias():
    return render_template('noticias.html')

@app.route("/inscripciones")
def inscripciones():
    return render_template('inscripciones.html')

@app.route("/nosotros")
def nosotros():
    return render_template('nosotros.html')




# --- Nuevas rutas para el panel de entrenador con archivos HTML separados ---


@app.route('/usuarios-panel')
def panel_usuarios():
    return render_template('usuarios.html')

@app.route('/torneos-panel')
def panel_torneos():
    return render_template('torneos.html')

@app.route('/estadisticas-panel')
def panel_estadisticas():
    return render_template('estadisticas.html')

@app.route('/formulario-panel')
def panel_formulario():
    return render_template('formulario_unificado.html')

# Ejecutar la aplicación Flask en modo depuración si es el script principal
if __name__ == '__main__':
    app.run(debug=True) # debug=True recarga el servidor automáticamente y muestra errores detallados