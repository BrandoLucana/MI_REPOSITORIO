from flask import Flask, render_template, request, jsonify, redirect, url_for, flash, session
import mysql.connector
from datetime import datetime
import os
import logging
import re
from werkzeug.utils import secure_filename

# Configuración básica
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

app = Flask(__name__, static_folder='static', template_folder='templates')
app.secret_key = os.environ.get('FLASK_SECRET_KEY', 'tu_clave_secreta_aqui')

# Configuración de uploads
UPLOAD_FOLDER = 'static/uploads'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'pdf'}
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

def get_db_connection():
    try:
        conn = mysql.connector.connect(
            host="database4pr.cqq3cpmmrkby.us-east-1.rds.amazonaws.com",
            user="admin",
            password="73144231soto",
            database="pasion_por_el_voley",
            connect_timeout=5
        )
        logger.info("Conexión a la base de datos establecida")
        return conn
    except mysql.connector.Error as err:
        logger.error(f"Error de conexión a la base de datos: {err}")
        return None

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def validate_usuario(data):
    """Valida los campos del usuario según las nuevas reglas de la BD."""
    errors = []
    
    # Validación de nombre y apellidos
    if not re.match(r'^[A-Za-záéíóúÁÉÍÓÚñÑ]+( [A-Za-záéíóúÁÉÍÓÚñÑ]+)*$', data.get('nombre', '')) or len(data.get('nombre', '')) > 30:
        errors.append("Nombre inválido: solo letras y espacios simples, máximo 30 caracteres.")
    
    if not re.match(r'^[A-Za-záéíóúÁÉÍÓÚñÑ]+( [A-Za-záéíóúÁÉÍÓÚñÑ]+)*$', data.get('apellidos', '')) or len(data.get('apellidos', '')) > 30:
        errors.append("Apellidos inválidos: solo letras y espacios simples, máximo 30 caracteres.")
    
    edad = int(data.get('edad', 0))
    email = data.get('email', '').strip().lower()
    telefono = data.get('telefono', '').strip().lower()
    
    # Validación de email según edad
    if edad >= 18:
        if not email:
            errors.append("Correo electrónico obligatorio para mayores de edad.")
        elif email == 'ninguno':
            errors.append("No puede usar 'ninguno' como correo para mayores de edad.")
        elif not re.match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', email):
            errors.append("Correo electrónico inválido.")
    else:
        if email and email != 'ninguno' and not re.match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', email):
            errors.append("Correo electrónico inválido. Para menores sin correo, escriba 'ninguno' o déjelo vacío.")
        elif not email:
            data['email'] = 'ninguno'  # Asignamos automáticamente si es menor y no tiene email
    
    # Validación de teléfono según edad
    if edad >= 18:
        if not telefono:
            errors.append("Teléfono obligatorio para mayores de edad.")
        elif telefono == 'ninguno':
            errors.append("No puede usar 'ninguno' como teléfono para mayores de edad.")
        elif not re.match(r'^[0-9]{9}$', telefono):
            errors.append("Teléfono inválido: debe contener exactamente 9 dígitos numéricos.")
    else:
        if telefono and telefono != 'ninguno' and not re.match(r'^[0-9]{9}$', telefono):
            errors.append("Teléfono inválido. Debe contener 9 dígitos o escribir 'ninguno'.")
        elif not telefono:
            data['telefono'] = 'ninguno'  # Asignamos automáticamente si es menor y no tiene teléfono
    
    # Validación de documento
    if data.get('tipo_documento') == 'DNI' and not re.match(r'^[0-9]{8}$', data.get('numero_documento', '')):
        errors.append("DNI debe contener exactamente 8 dígitos numéricos.")
    if data.get('tipo_documento') == 'CE' and not re.match(r'^[A-Za-z0-9]{8,12}$', data.get('numero_documento', '')):
        errors.append("Carnet de Extranjería debe contener entre 8 y 12 caracteres alfanuméricos.")
    
    return errors

def validate_apoderado(data):
    """Valida los campos del apoderado."""
    errors = []
    if not re.match(r'^[A-Za-záéíóúÁÉÍÓÚñÑ]+( [A-Za-záéíóúÁÉÍÓÚñÑ]+)*$', data.get('nombre_apoderado', '')) or len(data.get('nombre_apoderado', '')) > 30:
        errors.append("Nombre del apoderado inválido: solo letras y espacios simples, máximo 30 caracteres.")
    if not re.match(r'^[0-9]{9}$', data.get('telefono_apoderado', '')):
        errors.append("Teléfono del apoderado debe contener exactamente 9 dígitos numéricos.")
    correo = data.get('correo_apoderado', '').strip()
    if correo and not re.match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', correo):
        errors.append("Correo del apoderado inválido.")
    if data.get('tipo_documento_apoderado') == 'DNI' and not re.match(r'^[0-9]{8}$', data.get('numero_documento_apoderado', '')):
        errors.append("DNI del apoderado debe contener exactamente 8 dígitos numéricos.")
    if data.get('tipo_documento_apoderado') == 'CE' and not re.match(r'^[A-Za-z0-9]{8,12}$', data.get('numero_documento_apoderado', '')):
        errors.append("Carnet de Extranjería del apoderado debe contener entre 8 y 12 caracteres alfanuméricos.")
    return errors

def validate_pago(data):
    """Valida los campos de pago."""
    errors = []
    medio_pago = data.get('medio_pago', '')
    codigo_operacion = data.get('codigo_operacion', '').strip()
    if medio_pago == 'Efectivo' and codigo_operacion:
        errors.append("El código de operación debe estar vacío para pagos en efectivo.")
    if medio_pago == 'Yape' and not re.match(r'^[0-9]{8,12}$', codigo_operacion):
        errors.append("Código de operación para Yape debe contener 8-12 dígitos numéricos.")
    if medio_pago == 'Transferencia' and not re.match(r'^[A-Za-z0-9]{8,20}$', codigo_operacion):
        errors.append("Código de operación para Transferencia debe contener 8-20 caracteres alfanuméricos.")
    try:
        monto = float(data.get('monto', 25.00))
        if monto <= 0 or monto > 9999.99:
            errors.append("Monto inválido: debe estar entre 0.01 y 9999.99.")
    except ValueError:
        errors.append("Monto inválido: debe ser un número decimal.")
    return errors

def registrar_auditoria(usuario_id, accion, descripcion):
    """Registra una acción en la tabla de auditoría."""
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn is None:
            logger.error("No se pudo conectar a la base de datos para registrar auditoría")
            return
        cursor = conn.cursor()
        cursor.execute("""
            INSERT INTO auditoria_usuarios (usuario_id, accion, descripcion, fecha)
            VALUES (%s, %s, %s, NOW())
        """, (usuario_id, accion, descripcion))
        conn.commit()
    except Exception as e:
        logger.error(f"Error al registrar auditoría: {str(e)}")
        if conn:
            conn.rollback()
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/formulario')
def formulario():
    return render_template('formulario_unificado.html')

@app.route('/verificar_email', methods=['POST'])
def verificar_email():
    email = request.form.get('email', '').strip().lower()
    if email == 'ninguno' or not email:
        return jsonify({'disponible': True})
    
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn is None:
            return jsonify({'disponible': False, 'error': 'Error de conexión'})
        
        cursor = conn.cursor(dictionary=True)
        cursor.execute("SELECT id FROM usuarios WHERE email = %s", (email,))
        resultado = cursor.fetchone()
        
        return jsonify({'disponible': resultado is None})
        
    except Exception as e:
        logger.error(f"Error al verificar email: {str(e)}")
        return jsonify({'disponible': False, 'error': str(e)})
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/registrar_usuario', methods=['POST'])
def registrar_usuario():
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn is None:
            return jsonify({"éxito": False, "mensaje": "No se pudo conectar a la base de datos"}), 500
        
        cursor = conn.cursor(dictionary=True)
        datos_usuario = {
            'nombre': request.form.get('nombre', '').strip(),
            'apellidos': request.form.get('apellidos', '').strip(),
            'fecha_nacimiento': request.form.get('fecha_nacimiento', ''),
            'edad': int(request.form.get('edad', '0')),
            'telefono': request.form.get('telefono', '').strip().lower() or 'ninguno',
            'email': request.form.get('email', '').strip().lower(),
            'direccion': request.form.get('direccion', '').strip(),
            'tipo_documento': request.form.get('tipo_documento', 'DNI'),
            'numero_documento': request.form.get('numero_documento', '').strip(),
            'acepto_terminos': request.form.get('acepto_terminos') == 'on'
        }

        # Validación de usuario
        usuario_errors = validate_usuario(datos_usuario)
        if usuario_errors:
            return jsonify({"éxito": False, "mensaje": usuario_errors[0], "campo": usuario_errors[0].split(':')[0].lower()}), 400

        # Validación de apoderado (si es menor de edad)
        datos_apoderado = {}
        if datos_usuario['edad'] < 18:
            datos_apoderado = {
                'nombre_apoderado': request.form.get('nombre_apoderado', '').strip(),
                'telefono_apoderado': request.form.get('telefono_apoderado', '').strip(),
                'correo_apoderado': request.form.get('correo_apoderado', '').strip(),
                'centro_salud': request.form.get('centro_salud', ''),
                'tipo_documento_apoderado': request.form.get('tipo_documento_apoderado', ''),
                'numero_documento_apoderado': request.form.get('numero_documento_apoderado', '').strip()
            }
            apoderado_errors = validate_apoderado(datos_apoderado)
            if apoderado_errors:
                return jsonify({"éxito": False, "mensaje": apoderado_errors[0], "campo": apoderado_errors[0].split(':')[0].lower()}), 400

        # Validación de datos deportivos
        datos_deportivos = {
            'nivel_actual': request.form.get('nivel_actual', 'Principiante'),
            'practico_deporte': request.form.get('practico_deporte', 'no'),
            'posicion': request.form.get('posicion', 'No definido'),
            'centro_seguro': request.form.get('centro_seguro', 'Ninguno')
        }

        # Validación de pago
        if 'comprobante_pago' not in request.files:
            return jsonify({"éxito": False, "mensaje": "El comprobante de pago es requerido"}), 400
        
        file = request.files['comprobante_pago']
        if file.filename == '':
            return jsonify({"éxito": False, "mensaje": "No se seleccionó ningún archivo"}), 400
        
        if not allowed_file(file.filename):
            return jsonify({"éxito": False, "mensaje": "Tipo de archivo no permitido (solo PNG, JPG, JPEG, PDF)"}), 400

        datos_pago = {
            'paquete': request.form.get('paquete', 'Infantil'),
            'fecha_pago': datetime.now().date(),
            'medio_pago': request.form.get('medio_pago', 'Efectivo'),
            'codigo_operacion': request.form.get('codigo_operacion', '').strip() or None,
            'monto': request.form.get('monto', '25.00'),
            'estado': 'Pendiente'
        }

        pago_errors = validate_pago(datos_pago)
        if pago_errors:
            return jsonify({"éxito": False, "mensaje": pago_errors[0], "campo": pago_errors[0].split(':')[0].lower()}), 400

        # Guardar el comprobante de pago
        os.makedirs(UPLOAD_FOLDER, exist_ok=True)
        filename = secure_filename(f"{datetime.now().strftime('%Y%m%d%H%M%S')}_{file.filename}")
        filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        file.save(filepath)
        datos_pago['comprobante_ruta'] = filepath

        # Verificar disponibilidad del email (solo si no es "ninguno" o vacío)
        if datos_usuario['email'] and datos_usuario['email'] != 'ninguno':
            cursor.execute("SELECT id FROM usuarios WHERE email = %s", (datos_usuario['email'],))
            if cursor.fetchone():
                return jsonify({"éxito": False, "mensaje": "El correo electrónico ya está registrado", "campo": "email"}), 400

        # Insertar usuario en la BD
        cursor.execute("""
            INSERT INTO usuarios 
            (nombre, apellidos, fecha_nacimiento, edad, telefono, email, direccion, 
             tipo_documento, numero_documento, acepto_terminos)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """, (
            datos_usuario['nombre'], 
            datos_usuario['apellidos'], 
            datos_usuario['fecha_nacimiento'],
            datos_usuario['edad'], 
            datos_usuario['telefono'],
            datos_usuario['email'],
            datos_usuario['direccion'],
            datos_usuario['tipo_documento'], 
            datos_usuario['numero_documento'], 
            datos_usuario['acepto_terminos']
        ))
        usuario_id = cursor.lastrowid

        # Insertar apoderado si es menor de edad
        if datos_usuario['edad'] < 18:
            cursor.execute("""
                INSERT INTO apoderados 
                (usuario_id, nombre_apoderado, telefono_apoderado, correo_apoderado, 
                 centro_salud, tipo_documento_apoderado, numero_documento_apoderado)
                VALUES (%s, %s, %s, %s, %s, %s, %s)
            """, (
                usuario_id, 
                datos_apoderado['nombre_apoderado'], 
                datos_apoderado['telefono_apoderado'],
                datos_apoderado['correo_apoderado'] or None, 
                datos_apoderado['centro_salud'],
                datos_apoderado['tipo_documento_apoderado'], 
                datos_apoderado['numero_documento_apoderado']
            ))

        # Insertar datos deportivos
        cursor.execute("""
            INSERT INTO datos_deportivos 
            (usuario_id, nivel_actual, practico_deporte, posicion, centro_seguro)
            VALUES (%s, %s, %s, %s, %s)
        """, (
            usuario_id, 
            datos_deportivos['nivel_actual'], 
            datos_deportivos['practico_deporte'],
            datos_deportivos['posicion'], 
            datos_deportivos['centro_seguro']
        ))

        # Insertar pago
        cursor.execute("""
            INSERT INTO pagos 
            (usuario_id, paquete, fecha_pago, medio_pago, codigo_operacion, 
             comprobante_ruta, monto, estado)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
        """, (
            usuario_id, 
            datos_pago['paquete'], 
            datos_pago['fecha_pago'], 
            datos_pago['medio_pago'],
            datos_pago['codigo_operacion'], 
            datos_pago['comprobante_ruta'], 
            datos_pago['monto'],
            datos_pago['estado']
        ))

        conn.commit()
        registrar_auditoria(usuario_id, "Registro", f"Usuario {datos_usuario['nombre']} registrado")
        
        return jsonify({
            "éxito": True,
            "mensaje": "Usuario registrado exitosamente!",
            "redireccion": url_for('index')
        })

    except mysql.connector.Error as e:
        if conn:
            conn.rollback()
        logger.error(f"Error al registrar usuario: {str(e)}")
        if e.errno == 1062:
            return jsonify({"éxito": False, "mensaje": "El número de documento ya está registrado", "campo": "numero_documento"}), 400
        return jsonify({"éxito": False, "mensaje": f"Error de base de datos: {str(e)}", "campo": None}), 500
    except Exception as e:
        if conn:
            conn.rollback()
        logger.error(f"Error inesperado al registrar usuario: {str(e)}")
        return jsonify({"éxito": False, "mensaje": f"Error inesperado: {str(e)}", "campo": None}), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
            
@app.route('/obtener_usuarios')
def obtener_usuarios():
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn is None:
            return jsonify({'error': 'No se pudo conectar a la base de datos'}), 500
        cursor = conn.cursor(dictionary=True)
        query = """
        SELECT u.id, u.nombre, u.apellidos, u.edad, u.email, u.fecha_registro,
               a.nombre_apoderado, a.telefono_apoderado, a.centro_salud,
               d.nivel_actual, d.posicion, d.centro_seguro,
               p.paquete, p.estado AS estado_pago
        FROM usuarios u
        LEFT JOIN apoderados a ON u.id = a.usuario_id
        LEFT JOIN datos_deportivos d ON u.id = d.usuario_id
        LEFT JOIN pagos p ON u.id = p.usuario_id
        """
        cursor.execute(query)
        usuarios = cursor.fetchall()
        return jsonify(usuarios)
    except Exception as e:
        logger.error(f"Error al obtener usuarios: {str(e)}")
        return jsonify({'error': str(e)}), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/eliminar_usuario/<int:id>', methods=['POST'])
def eliminar_usuario(id):
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn is None:
            return jsonify({'éxito': False, 'mensaje': 'No se pudo conectar a la base de datos'}), 500
        cursor = conn.cursor()
        cursor.execute("SELECT nombre FROM usuarios WHERE id = %s", (id,))
        usuario = cursor.fetchone()
        if not usuario:
            return jsonify({'éxito': False, 'mensaje': 'Usuario no encontrado'}), 404
        registrar_auditoria(id, "Eliminación", f"Usuario {usuario[0]} eliminado del sistema")
        cursor.execute("DELETE FROM usuarios WHERE id = %s", (id,))  # Cascades to apoderados, datos_deportivos, pagos
        conn.commit()
        return jsonify({"éxito": True, "mensaje": "Usuario eliminado exitosamente!"})
    except Exception as e:
        if conn:
            conn.rollback()
        logger.error(f"Error al eliminar usuario: {str(e)}")
        return jsonify({"éxito": False, "mensaje": f"Error al eliminar usuario: {str(e)}"}), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/torneos')
def torneos():
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn is None:
            flash('No se pudo conectar a la base de datos', 'error')
            return render_template('torneo.html', torneos=[])
        cursor = conn.cursor(dictionary=True)
        cursor.execute("SELECT * FROM registro_torneo ORDER BY fecha DESC")
        torneos = cursor.fetchall()
        return render_template('torneo.html', torneos=torneos)
    except Exception as e:
        logger.error(f"Error al cargar torneos: {str(e)}")
        flash(f"Error al cargar torneos: {str(e)}", "error")
        return render_template('torneo.html', torneos=[])
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/inscripcion-torneo', methods=['GET', 'POST'])
def inscripcion_torneo():
    if request.method == 'POST':
        datos = {
            'nombre_equipo': request.form.get('nombre_equipo', '').strip(),
            'categoria': request.form.get('categoria', '').strip(),
            'delegado': request.form.get('delegado', '').strip(),
            'telefono': request.form.get('telefono', '').strip(),
            'email': request.form.get('email', '').strip()
        }
        if not all([datos['nombre_equipo'], datos['categoria'], datos['delegado']]):
            flash('Los campos nombre del equipo, categoría y delegado son obligatorios', 'error')
            return render_template('inscripcion-torneo.html', **datos)
        if datos['telefono'] and not re.match(r'^[0-9]{9}$', datos['telefono']):
            flash('Teléfono inválido: debe contener 9 dígitos numéricos', 'error')
            return render_template('inscripcion-torneo.html', **datos)
        if datos['email'] and not re.match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', datos['email']):
            flash('Correo electrónico inválido', 'error')
            return render_template('inscripcion-torneo.html', **datos)
        conn = None
        cursor = None
        try:
            conn = get_db_connection()
            if conn is None:
                flash('No se pudo conectar a la base de datos', 'error')
                return render_template('inscripcion-torneo.html', **datos)
            cursor = conn.cursor()
            cursor.execute("""
                INSERT INTO inscripciones_equipos_torneo 
                (nombre_equipo, categoria, delegado, telefono, email, fecha_inscripcion)
                VALUES (%s, %s, %s, %s, %s, NOW())
            """, (
                datos['nombre_equipo'], datos['categoria'], datos['delegado'],
                datos['telefono'] or None, datos['email'] or None
            ))
            conn.commit()
            registrar_auditoria(None, "Inscripción torneo", f"Equipo {datos['nombre_equipo']} inscrito en categoría {datos['categoria']}")
            flash('¡Inscripción al torneo realizada con éxito!', 'success')
            return redirect(url_for('torneos'))
        except Exception as e:
            if conn:
                conn.rollback()
            logger.error(f"Error al inscribir equipo: {str(e)}")
            flash(f'Error al procesar tu inscripción: {str(e)}', 'error')
            return render_template('inscripcion-torneo.html', **datos)
        finally:
            if cursor:
                cursor.close()
            if conn:
                conn.close()
    return render_template('inscripcion-torneo.html')

@app.route('/panel-admin')
def panel_admin():
    if not session.get('admin_logged_in'):
        return redirect(url_for('login_admin'))
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn is None:
            flash('No se pudo conectar a la base de datos', 'error')
            return render_template('panel_admin.html')
        cursor = conn.cursor(dictionary=True)
        cursor.execute("SELECT COUNT(*) AS total FROM usuarios")
        total_usuarios = cursor.fetchone()['total']
        cursor.execute("SELECT COUNT(*) AS total FROM pagos WHERE estado = 'Pendiente'")
        pagos_pendientes = cursor.fetchone()['total']
        cursor.execute("SELECT COUNT(*) AS total FROM inscripciones_equipos_torneo")
        total_equipos = cursor.fetchone()['total']
        cursor.execute("SELECT * FROM auditoria_usuarios ORDER BY fecha DESC LIMIT 10")
        auditoria = cursor.fetchall()
        return render_template('panel_admin.html', 
                             total_usuarios=total_usuarios,
                             pagos_pendientes=pagos_pendientes,
                             total_equipos=total_equipos,
                             auditoria=auditoria)
    except Exception as e:
        logger.error(f"Error al cargar datos del panel: {str(e)}")
        flash(f"Error al cargar datos del panel: {value(e)}", "error")
        return render_template('panel_admin.html')
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/panel-entrenador', methods=['GET', 'POST'])
def registro_torneo():
    if not session.get('entrenador'):
        return redirect(url_for('login_entrenador'))
    conn = None
    cursor = None
    if request.method == 'POST':
        datos = {
            'torneo_id': request.form.get('torneo_id', '').strip(),
            'nombre': request.form.get('nombre', '').strip(),
            'fecha': request.form.get('fecha', '').strip(),
            'lugar': request.form.get('lugar', '').strip(),
            'nivel': request.form.get('nivel', '').strip(),
            'descripcion': request.form.get('descripcion', '').strip(),
            'estado': request.form.get('estado', '').strip()
        }
        if not all([datos['nombre'], datos['fecha'], datos['lugar'], datos['nivel'], datos['estado']]):
            return jsonify({"éxito": False, "mensaje": "Todos los campos son obligatorios excepto descripción"}), 400
        try:
            conn = get_db_connection()
            if conn is None:
                return jsonify({"éxito": False, "mensaje": "No se pudo conectar a la base de datos"}), 500
            cursor = conn.cursor()
            if datos['torneo_id']:
                cursor.execute("""
                    UPDATE registro_torneo
                    SET nombre=%s, fecha=%s, lugar=%s, nivel=%s, descripcion=%s, estado=%s
                    WHERE id=%s
                """, (
                    datos['nombre'], datos['fecha'], datos['lugar'], datos['nivel'],
                    datos['descripcion'] or None, datos['estado'], datos['torneo_id']
                ))
                if cursor.rowcount == 0:
                    return jsonify({"éxito": False, "mensaje": "No se encontró el torneo con el ID proporcionado"}), 404
                logger.info(f"Torneo ID {datos['torneo_id']} modificado exitosamente")
            else:
                cursor.execute("""
                    INSERT INTO registro_torneo (nombre, fecha, lugar, nivel, descripcion, estado)
                    VALUES (%s, %s, %s, %s, %s, %s)
                """, (
                    datos['nombre'], datos['fecha'], datos['lugar'], datos['nivel'],
                    datos['descripcion'] or None, datos['estado']
                ))
                logger.info(f"Torneo {datos['nombre']} registrado exitosamente")
            conn.commit()
            return jsonify({"éxito": True, "mensaje": "Torneo procesado exitosamente!"})
        except mysql.connector.Error as e:
            if conn:
                conn.rollback()
            logger.error(f"Error al procesar torneo: {str(e)}")
            return jsonify({"éxito": False, "mensaje": f"Error al procesar torneo: {str(e)}"}), 500
        finally:
            if cursor:
                cursor.close()
            if conn:
                conn.close()
    torneos_existentes = []
    equipos = []
    try:
        conn = get_db_connection()
        if conn:
            cursor = conn.cursor(dictionary=True)
            cursor.execute("SELECT id, nombre, fecha, lugar, nivel, descripcion, estado FROM registro_torneo")
            torneos_existentes = cursor.fetchall()
            cursor.execute("SELECT * FROM inscripciones_equipos_torneo ORDER BY fecha_inscripcion DESC")
            equipos = cursor.fetchall()
        return render_template('registro_torneo.html', torneos=torneos_existentes, equipos=equipos)
    except Exception as e:
        logger.error(f"Error al cargar torneos: {str(e)}")
        flash(f"Error al cargar torneos: {str(e)}", "error")
        return render_template('registro_torneo.html', torneos=[], equipos=[])
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/eliminar-torneo/<int:id>', methods=['POST'])
def eliminar_torneo(id):
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn is None:
            return jsonify({"éxito": False, "mensaje": "No se pudo conectar a la base de datos"}), 500
        cursor = conn.cursor()
        cursor.execute("DELETE FROM registro_torneo WHERE id = %s", (id,))
        if cursor.rowcount == 0:
            return jsonify({"éxito": False, "mensaje": "Torneo no encontrado"}), 404
        conn.commit()
        return jsonify({"éxito": True, "mensaje": "Torneo eliminado exitosamente!"})
    except Exception as e:
        if conn:
            conn.rollback()
        logger.error(f"Error al eliminar torneo: {str(e)}")
        return jsonify({"éxito": False, "mensaje": f"Error al eliminar torneo: {str(e)}"}), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
            
@app.route('/verificar_documento', methods=['POST'])
def verificar_documento():
    try:
        data = request.get_json()
        numero_documento = data.get('numero_documento', '').strip()
        tipo_documento = data.get('tipo_documento', 'DNI')
        
        if not numero_documento:
            return jsonify({'disponible': True})
            
        conn = get_db_connection()
        if not conn:
            return jsonify({'disponible': False, 'error': 'Database connection failed'}), 500
            
        cursor = conn.cursor()
        
        # Verificar en usuarios
        cursor.execute("SELECT id FROM usuarios WHERE tipo_documento = %s AND numero_documento = %s", 
                      (tipo_documento, numero_documento))
        existe_usuario = cursor.fetchone() is not None
        
        # Verificar en apoderados (si aplica)
        cursor.execute("""
            SELECT a.id 
            FROM apoderados a
            JOIN usuarios u ON a.usuario_id = u.id
            WHERE a.tipo_documento_apoderado = %s AND a.numero_documento_apoderado = %s
        """, (tipo_documento, numero_documento))
        existe_apoderado = cursor.fetchone() is not None
        
        return jsonify({
            'disponible': not (existe_usuario or existe_apoderado),
            'es_usuario': existe_usuario,
            'es_apoderado': existe_apoderado
        })
        
    except Exception as e:
        logger.error(f"Error al verificar documento: {str(e)}")
        return jsonify({'disponible': False, 'error': str(e)}), 500
    finally:
        if 'cursor' in locals():
            cursor.close()
        if 'conn' in locals():
            conn.close()

@app.route('/login-entrenador', methods=['GET', 'POST'])
def login_entrenador():
    if request.method == 'POST':
        usuario = request.form.get('usuario', '').strip()
        contrasena = request.form.get('contrasena', '').strip()
        if usuario == "admin" and contrasena == "1234":  # Replace with secure authentication
            session['entrenador'] = True
            flash("¡Bienvenido, Entrenador!", "success")
            return redirect(url_for('registro_torneo'))
        flash("Usuario o contraseña incorrectos", "error")
    return render_template('login_entrenador.html')

@app.route('/login-admin', methods=['GET', 'POST'])
def login_admin():
    if request.method == 'POST':
        usuario = request.form.get('usuario', '').strip()
        contrasena = request.form.get('contrasena', '').strip()
        if usuario == "admin" and contrasena == "admin123":  # Replace with secure authentication
            session['admin_logged_in'] = True
            flash("¡Bienvenido, Administrador!", "success")
            return redirect(url_for('panel_admin'))
        flash("Credenciales incorrectas", "error")
    return render_template('login_admin.html')

@app.route('/logout-admin')
def logout_admin():
    session.pop('admin_logged_in', None)
    flash("Sesión cerrada", "success")
    return redirect(url_for('index'))

@app.route('/logout-entrenador')
def logout_entrenador():
    session.pop('entrenador', None)
    flash("Sesión cerrada", "success")
    return redirect(url_for('index'))

@app.route('/horarios')
def horarios():
    return render_template('horarios.html')

@app.route('/donde_estamos')
def donde_estamos():
    return render_template('donde_estamos.html')

@app.route('/contactanos')
def contactanos():
    return render_template('contactanos.html')

@app.route('/mas')
def mas():
    return render_template('mas.html')

@app.route('/productos')
def productos():
    return render_template('productos.html')

@app.route('/galeria')
def galeria():
    return render_template('galeria.html')

@app.route('/eventos')
def eventos():
    return render_template('eventos.html')

@app.route('/noticias')
def noticias():
    return render_template('noticias.html')

@app.route('/nosotros')
def nosotros():
    return render_template('nosotros.html')

@app.route('/inscripciones')
def inscripciones():
    return render_template('inscripciones.html')

@app.route('/usuarios-panel')
def panel_usuarios():
    return render_template('usuarios.html')

@app.route('/torneos-panel')
def panel_torneos():
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn:
            cursor = conn.cursor(dictionary=True)
            cursor.execute("SELECT id, nombre, fecha, lugar, nivel, descripcion, estado FROM registro_torneo")
            torneos = cursor.fetchall()
            return render_template('torneos_panel.html', torneos=torneos)
        flash('No se pudo conectar a la base de datos', 'error')
        return render_template('torneos_panel.html', torneos=[])
    except Exception as e:
        logger.error(f"Error al cargar torneos: {str(e)}")
        flash(f"Error al cargar torneos: {str(e)}", "error")
        return render_template('torneos_panel.html', torneos=[])
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/estadisticas-panel')
def panel_estadisticas():
    return render_template('estadisticas.html')

@app.route('/formulario-panel')
def panel_formulario():
    return render_template('formulario_unificado.html')

@app.route('/inscripcion-equipos')
def inscripcion_equipos():
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn:
            cursor = conn.cursor(dictionary=True)
            cursor.execute("SELECT id, nombre_equipo, categoria, delegado, telefono, email, fecha_inscripcion FROM inscripciones_equipos_torneo")
            equipos = cursor.fetchall()
            return render_template('inscripcion-equipos.html', equipos=equipos)
        flash('No se pudo conectar a la base de datos', 'error')
        return render_template('inscripcion-equipos.html', equipos=[])
    except Exception as e:
        logger.error(f"Error al cargar equipos: {str(e)}")
        flash(f"Error al cargar equipos: {str(e)}", "error")
        return render_template('inscripcion-equipos.html', equipos=[])
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/test_db')
def test_db():
    try:
        conn = get_db_connection()
        if conn is None:
            return jsonify({"status": "error", "message": "No se pudo conectar a la base de datos"})
        cursor = conn.cursor()
        cursor.execute("SHOW TABLES")
        tables = [row[0] for row in cursor.fetchall()]
        return jsonify({"status": "success", "tables": tables, "message": "Conexión exitosa a la base de datos"})
    except Exception as e:
        logger.error(f"Error al probar conexión a la base de datos: {str(e)}")
        return jsonify({"status": "error", "message": str(e)}), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

if __name__ == '__main__':
    if not os.path.exists(UPLOAD_FOLDER):
        os.makedirs(UPLOAD_FOLDER)
    app.run(debug=True, host='0.0.0.0', port=5000)