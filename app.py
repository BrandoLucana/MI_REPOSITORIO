from flask import Flask, render_template, request, jsonify, redirect, url_for, flash, session
import mysql.connector
from datetime import datetime
import os
import logging
from werkzeug.utils import secure_filename
import uuid
import re


# Configuración básica
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

app = Flask(__name__, static_folder='static', template_folder='templates')
app.secret_key = 'tu_clave_secreta_aqui'

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

def registrar_auditoria(usuario_id, accion, descripcion):
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
    session['form_token'] = str(uuid.uuid4())  # Generar token único para el formulario
    return render_template('formulario_unificado.html', form_token=session['form_token'])

@app.route('/lista_usuarios')
def lista_usuarios():
    return render_template('lista_usuarios.html')

#verificar email#
@app.route('/verificar_email', methods=['POST'])
def verificar_email():
    try:
        data = request.get_json()
        email = data.get('email', '').strip()
        
        if not email:
            return jsonify({'disponible': False}), 400

        conn = get_db_connection()
        if not conn:
            return jsonify({'disponible': False, 'error': 'Database connection failed'}), 500

        cursor = conn.cursor()
        cursor.execute("SELECT id FROM usuarios WHERE email = %s", (email,))
        existe = cursor.fetchone() is not None

        return jsonify({'disponible': not existe})
        
    except Exception as e:
        logger.error(f"Error al verificar email: {str(e)}")
        return jsonify({'disponible': False, 'error': str(e)}), 500
    finally:
        if 'cursor' in locals(): cursor.close()
        if 'conn' in locals(): conn.close()

@app.route('/registrar_usuario', methods=['POST'])
def registrar_usuario():
    logger.info(f"Recibida solicitud POST /registrar_usuario: {request.form}")
    
    # Verificar token para evitar duplicados
    form_token = request.form.get('form_token')
    if form_token != session.get('form_token'):
        logger.warning("Solicitud duplicada o inválida detectada")
        return jsonify({"éxito": False, "mensaje": "Solicitud duplicada o inválida"}), 400
    session.pop('form_token', None)
    
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn is None:
            return jsonify({"éxito": False, "mensaje": "No se pudo conectar a la base de datos"}), 500
        
        cursor = conn.cursor(dictionary=True)
        
        # Validación de campos requeridos
        campos_requeridos = ['nombre', 'apellidos', 'fecha_nacimiento', 'edad', 'tipo_documento', 'numero_documento']
        datos_form = {}
        for campo in campos_requeridos:
            valor = request.form.get(campo)
            if not valor:
                return jsonify({
                    "éxito": False,
                    "mensaje": f"El campo {campo.replace('_', ' ')} es requerido",
                    "campo": campo
                }), 400
            datos_form[campo] = valor

        # Procesar archivo de comprobante
        if 'comprobante_pago' not in request.files:
            return jsonify({"éxito": False, "mensaje": "El comprobante de pago es requerido"}), 400
            
        file = request.files['comprobante_pago']
        if file.filename == '':
            return jsonify({"éxito": False, "mensaje": "No se seleccionó ningún archivo"}), 400
            
        if not allowed_file(file.filename):
            return jsonify({"éxito": False, "mensaje": "Tipo de archivo no permitido"}), 400

        os.makedirs(UPLOAD_FOLDER, exist_ok=True)
        filename = secure_filename(file.filename)
        filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        file.save(filepath)

        # Validar código de operación según el medio de pago
        medio_pago = request.form.get('medio_pago', 'Efectivo')
        codigo_operacion = request.form.get('codigo_operacion', '').strip()

        if medio_pago == 'Efectivo':
            if codigo_operacion:
                    return jsonify({
                        "éxito": False,
                        "mensaje": "No debe ingresar código de operación si paga en efectivo.",
                        "campo": "codigo_operacion"
                    }), 400
            codigo_operacion = None  # asegurar que vaya como NULL a la BD

        elif medio_pago == 'Yape':
            if not re.fullmatch(r'[0-9]{8,12}', codigo_operacion):
                    return jsonify({
                        "éxito": False,
                        "mensaje": "El código de operación para Yape debe tener entre 8 y 12 dígitos numéricos.",
                        "campo": "codigo_operacion"
                    }), 400

        elif medio_pago == 'Transferencia':
            if not re.fullmatch(r'[A-Za-z0-9]{8,20}', codigo_operacion):
                    return jsonify({
                        "éxito": False,
                        "mensaje": "El código de operación para Transferencia debe tener entre 8 y 20 caracteres alfanuméricos.",
                        "campo": "codigo_operacion"
                    }), 400

            else:
                return jsonify({
                    "éxito": False,
                    "mensaje": "Medio de pago no válido.",
                    "campo": "medio_pago"
                }), 400


        # Insertar usuario
        datos_usuario = {
            'nombre': datos_form['nombre'],
            'apellidos': datos_form['apellidos'],
            'fecha_nacimiento': datos_form['fecha_nacimiento'],
            'edad': int(datos_form['edad']),
            'email': request.form.get('email', ''),
            'direccion': request.form.get('direccion', ''),
            'tipo_documento': datos_form['tipo_documento'],
            'numero_documento': datos_form['numero_documento'],
            'acepto_terminos': True if request.form.get('terminos_condiciones') else False
        }
        
        cursor.execute("""
            INSERT INTO usuarios 
            (nombre, apellidos, fecha_nacimiento, edad, email, direccion, 
             tipo_documento, numero_documento, acepto_terminos)
            VALUES 
            (%(nombre)s, %(apellidos)s, %(fecha_nacimiento)s, %(edad)s, 
             %(email)s, %(direccion)s, %(tipo_documento)s, %(numero_documento)s, 
             %(acepto_terminos)s)
        """, datos_usuario)
        
        usuario_id = cursor.lastrowid

        # Insertar apoderado si es menor de edad
        if datos_usuario['edad'] < 18:
            datos_apoderado = {
                'usuario_id': usuario_id,
                'nombre_apoderado': request.form['nombre_apoderado'],
                'telefono_apoderado': request.form['telefono_apoderado'],
                'correo_apoderado': request.form.get('correo_apoderado', ''),
                'centro_salud': request.form['centro_salud'],
                'tipo_documento_apoderado': request.form['tipo_documento_apoderado'],
                'numero_documento_apoderado': request.form['numero_documento_apoderado']
            }
            if not all(datos_apoderado.values()):
                return jsonify({
                    "éxito": False,
                    "mensaje": "Todos los campos del apoderado son requeridos para menores de edad",
                    "campo": "apoderado"
                }), 400
            
            cursor.execute("""
                INSERT INTO apoderados 
                (usuario_id, nombre_apoderado, telefono_apoderado, 
                 correo_apoderado, centro_salud, tipo_documento_apoderado, numero_documento_apoderado)
                VALUES 
                (%(usuario_id)s, %(nombre_apoderado)s, %(telefono_apoderado)s, 
                 %(correo_apoderado)s, %(centro_salud)s, %(tipo_documento_apoderado)s, %(numero_documento_apoderado)s)
            """, datos_apoderado)

        # Insertar datos deportivos
        datos_deportivos = {
            'usuario_id': usuario_id,
            'nivel_actual': request.form.get('nivel_actual', 'Principiante'),
            'practico_deporte': request.form.get('practico_deporte', 'no'),
            'posicion': request.form.get('posicion', ''),
            'centro_seguro': request.form.get('centro_seguro', 'Ninguno')
        }
        
        cursor.execute("""
            INSERT INTO datos_deportivos 
            (usuario_id, nivel_actual, practico_deporte, posicion, centro_seguro)
            VALUES 
            (%(usuario_id)s, %(nivel_actual)s, %(practico_deporte)s, %(posicion)s, %(centro_seguro)s)
        """, datos_deportivos)

        # Insertar pago
        datos_pago = {
            'usuario_id': usuario_id,
            'paquete': request.form.get('paquete', 'Infantil'),
            'fecha_pago': datetime.now().date(),
            'medio_pago': medio_pago,
            'codigo_operacion': codigo_operacion,
            'comprobante_ruta': filepath,
            'monto': float(request.form.get('monto', 25.00)),
            'estado': 'Pendiente'
        }
        
        cursor.execute("""
            INSERT INTO pagos 
            (usuario_id, paquete, fecha_pago, medio_pago, 
             codigo_operacion, comprobante_ruta, monto, estado)
            VALUES 
            (%(usuario_id)s, %(paquete)s, %(fecha_pago)s, %(medio_pago)s, 
             %(codigo_operacion)s, %(comprobante_ruta)s, %(monto)s, %(estado)s)
        """, datos_pago)
        
        conn.commit()
        
        return jsonify({
            "éxito": True,
            "mensaje": "Usuario registrado exitosamente!",
            "redireccion": url_for('index')
        })
        
    except mysql.connector.Error as e:
        if conn:
            conn.rollback()
        if "Duplicate entry" not in str(e):
            logger.error(f"Error al registrar usuario: {str(e)}")
        if e.errno == 1054:
            return jsonify({
                "éxito": False,
                "mensaje": "Error en la estructura de la base de datos",
                "campo": None
            }), 500
        elif e.errno == 3819:
            return jsonify({
                "éxito": False,
                "mensaje": "El código de operación no es válido. Revisa el formato según el medio de pago.",
                "campo": "codigo_operacion"
            }), 400
        elif "Duplicate entry" in str(e):
            logger.info("Intento de duplicado detectado. Ignorando registro adicional.")
            return jsonify({
                "éxito": True,
                "mensaje": "Ya estabas registrado, redireccionando...",
                "redireccion": url_for('index')
            })
        return jsonify({
            "éxito": False,
            "mensaje": f"Error de base de datos: {str(e)}",
            "campo": None
        }), 500
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
               a.nombre_apoderado, a.telefono_apoderado,
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
        
        registrar_auditoria(id, "Eliminación", "Usuario eliminado del sistema")
        
        cursor.execute("DELETE FROM pagos WHERE usuario_id = %s", (id,))
        cursor.execute("DELETE FROM datos_deportivos WHERE usuario_id = %s", (id,))
        cursor.execute("DELETE FROM apoderados WHERE usuario_id = %s", (id,))
        cursor.execute("DELETE FROM usuarios WHERE id = %s", (id,))
        conn.commit()
        
        return jsonify({
            "éxito": True,
            "mensaje": "Usuario eliminado exitosamente!"
        })
    except Exception as e:
        if conn:
            conn.rollback()
        logger.error(f"Error al eliminar usuario: {str(e)}")
        return jsonify({
            "éxito": False,
            "mensaje": f"Error al eliminar usuario: {str(e)}"
        }), 500
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
                flash('No se pudo conectar a la base de datos', 'error')
                return render_template('inscripcion-torneo.html', 
                                    nombre_equipo=nombre_equipo, categoria=categoria, 
                                    delegado=delegado, telefono=telefono, email=email)
            cursor = conn.cursor()
            sql = """
            INSERT INTO inscripciones_equipos_torneo 
            (nombre_equipo, categoria, delegado, telefono, email, fecha_inscripcion)
            VALUES (%s, %s, %s, %s, %s, NOW())
            """
            cursor.execute(sql, (nombre_equipo, categoria, delegado, telefono, email))
            conn.commit()
            
            registrar_auditoria(
                usuario_id=None,
                accion="Inscripción torneo",
                descripcion=f"Equipo {nombre_equipo} inscrito en categoría {categoria}"
            )
            
            flash('¡Inscripción al torneo realizada con éxito!', 'success')
            return redirect(url_for('torneos'))
        except Exception as e:
            if conn:
                conn.rollback()
            logger.error(f"Error al inscribir equipo: {str(e)}")
            flash(f'Error al procesar tu inscripción: {str(e)}', 'error')
            return render_template('inscripcion-torneo.html', 
                                nombre_equipo=nombre_equipo, categoria=categoria, 
                                delegado=delegado, telefono=telefono, email=email)
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
        flash(f"Error al cargar datos del panel: {str(e)}", "error")
        return render_template('panel_admin.html')
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/panel-entrenador', methods=['GET', 'POST'])
def registro_torneo():
    if not session.get('entrenador_logged_in'):
        return redirect(url_for('login_entrenador'))
    
    conn = None
    cursor = None
    if request.method == 'POST':
        torneo_id = request.form.get('torneo_id', '').strip()
        nombre_torneo = request.form.get('nombre', '').strip()
        fecha = request.form.get('fecha', '').strip()
        lugar = request.form.get('lugar', '').strip()
        nivel = request.form.get('nivel', '').strip()
        descripcion = request.form.get('descripcion', '').strip()
        estado = request.form.get('estado', '').strip()
        if not all([nombre_torneo, fecha, lugar, nivel, estado]):
            return jsonify({
                'éxito': False,
                'mensaje': 'Todos los campos son obligatorios excepto la descripción.'
            }), 400
        try:
            conn = get_db_connection()
            if conn is None:
                return jsonify({
                    'éxito': False,
                    'mensaje': 'No se pudo conectar a la base de datos.'
                }), 500
            cursor = conn.cursor()
            if torneo_id:
                sql = """
                    UPDATE registro_torneo
                    SET nombre=%s, fecha=%s, lugar=%s, nivel=%s, descripcion=%s, estado=%s
                    WHERE id=%s
                """
                cursor.execute(sql, (nombre_torneo, fecha, lugar, nivel, descripcion, estado, torneo_id))
                if cursor.rowcount == 0:
                    return jsonify({
                        'éxito': False,
                        'mensaje': 'No se encontró el torneo con el ID proporcionado.'
                    }), 404
                logger.info(f'Torneo ID {torneo_id} modificado exitosamente.')
                response = {
                    'éxito': True,
                    'mensaje': 'Torneo modificado exitosamente!'
                }
            else:
                sql = """
                    INSERT INTO registro_torneo (nombre, fecha, lugar, nivel, descripcion, estado)
                    VALUES (%s, %s, %s, %s, %s, %s)
                """
                cursor.execute(sql, (nombre_torneo, fecha, lugar, nivel, descripcion, estado))
                logger.info(f'Torneo "{nombre_torneo}" registrado exitosamente.')
                response = {
                    'éxito': True,
                    'mensaje': 'Torneo registrado exitosamente!'
                }
            conn.commit()
            return jsonify(response)
        except mysql.connector.Error as e:
            logger.error(f'Error al procesar torneo: {str(e)}')
            if conn:
                conn.rollback()
            return jsonify({
                'éxito': False,
                'mensaje': f'Error al procesar torneo: {str(e)}'
            }), 500
        except Exception as e:
            logger.error(f'Error inesperado al procesar torneo: {str(e)}')
            if conn:
                conn.rollback()
            return jsonify({
                'éxito': False,
                'mensaje': f'Error inesperado al procesar torneo: {str(e)}'
            }), 500
        finally:
            if cursor:
                cursor.close()
            if conn:
                conn.close()
    elif request.method == 'GET':
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
                logger.info(f'Cargados {len(torneos_existentes)} torneos existentes.')
            else:
                flash('No se pudo conectar a la base de datos para cargar los torneos.', 'error')
                logger.error('Conexión fallida al cargar torneos.')
        except Exception as e:
            logger.error(f'Error al cargar torneos: {str(e)}')
            flash(f'Error al cargar torneos: {str(e)}', 'error')
        finally:
            if cursor:
                cursor.close()
            if conn:
                conn.close()
        return render_template('registro_torneo.html', torneos=torneos_existentes, equipos=equipos)

@app.route('/eliminar-torneo/<int:id>', methods=['POST'])
def eliminar_torneo(id):
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn is None:
            return jsonify({
                'éxito': False,
                'mensaje': 'No se pudo conectar a la base de datos.'
            }), 500
        cursor = conn.cursor()
        cursor.execute("DELETE FROM registro_torneo WHERE id = %s", (id,))
        conn.commit()
        return jsonify({
            'éxito': True,
            'mensaje': 'Confirmado, torneo eliminado con éxito.'
        })
    except Exception as e:
        logger.error(f'Error al eliminar torneo: {str(e)}')
        if conn:
            conn.rollback()
        return jsonify({
            "éxito": False,
            "mensaje": f"Error al eliminar torneo: {str(e)}"
        }), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/login-entrenador', methods=['GET', 'POST'])
def login_entrenador():
    if request.method == 'POST':
        usuario = request.form.get('usuario')
        contrasena = request.form.get('contrasena')
        if usuario == "admin" and contrasena == "1234":
            session['entrenador_logged_in'] = True
            flash("¡Bienvenido, Entrenador!", "success")
            return redirect(url_for('registro_torneo'))
        else:
            flash("Usuario o contraseña incorrectos", "error")
            return render_template('login_entrenador.html')
    
    return render_template('login_entrenador.html')

@app.route('/login-admin', methods=['GET', 'POST'])
def login_admin():
    if request.method == 'POST':
        usuario = request.form.get('usuario')
        contrasena = request.form.get('contrasena')
        if usuario == "admin" and contrasena == "admin123":
            session['admin_logged_in'] = True
            return redirect(url_for('panel_admin'))
        else:
            flash("Credenciales incorrectas", "error")
    
    return render_template('login_admin')

@app.route('/logout-admin')
def logout_admin():
    session.pop('admin_logged_in', None)
    return redirect(url_for('index'))

@app.route('/logout-entrenador')
def logout_entrenador():
    session.pop('entrenador_logged_in', None)
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
    return render_template('nosotros')

@app.route('/inscripciones')
def inscripciones():
    return render_template('inscripciones.html')

@app.route('/usuarios-panel')
def usuarios_panel():
    return render_template('usuarios.html')

@app.route('/torneos-panel')
def panel_torneos():
    conn = None
    cursor = None
    torneos = []
    try:
        conn = get_db_connection()
        if conn:
            cursor = conn.cursor(dictionary=True)
            cursor.execute("SELECT id, nombre, fecha, lugar, nivel, descripcion, estado FROM registro_torneo")
            torneos = cursor.fetchall()
            logger.info(f'Cargados {len(torneos)} torneos para el panel.')
        else:
            flash('No se pudo conectar a la base de datos para cargar los torneos.', 'error')
            logger.error('Conexión fallida al cargar torneos.')
    except Exception as e:
        logger.error(f'Error al cargar torneos: {str(e)}')
        flash(f'Error al cargar torneos: {str(e)}', 'error')
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
    return render_template('torneos_panel.html', torneos=torneos)

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
    equipos = []
    try:
        conn = get_db_connection()
        if conn:
            cursor = conn.cursor(dictionary=True)
            cursor.execute("SELECT id, nombre_equipo, categoria, delegado, telefono, email, fecha_inscripcion FROM inscripciones_equipos_torneo")
            equipos = cursor.fetchall()
            logger.info(f'Cargados {len(equipos)} equipos inscritos para la página.')
        else:
            flash('No se pudo conectar a la base de datos para cargar los equipos.', 'error')
            logger.error('Conexión fallida al cargar equipos.')
    except Exception as e:
        logger.error(f'Error al cargar equipos: {str(e)}')
        flash(f'Error al cargar equipos: {str(e)}', 'error')
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
    return render_template('inscripcion-equipos.html', equipos=equipos)

@app.route('/test_db')
def test_db():
    try:
        conn = get_db_connection()
        if conn is None:
            return jsonify({"status": "error", "message": "No se pudo conectar a la base de datos"})
        
        cursor = conn.cursor()
        cursor.execute("SHOW TABLES")
        tables = cursor.fetchall()
        cursor.close()
        conn.close()
        
        return jsonify({
            "status": "success",
            "tables": tables,
            "message": "Conexión exitosa a la base de datos"
        })
    except Exception as e:
        logger.error(f"Error al probar conexión a la base de datos: {str(e)}")
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500

if __name__ == '__main__':
    if not os.path.exists(UPLOAD_FOLDER):
        os.makedirs(UPLOAD_FOLDER)
    app.run(debug=True)