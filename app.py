from flask import Flask, render_template, request, jsonify, redirect, url_for, flash
import mysql.connector
from datetime import datetime
import logging

# Configuración de logging
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

# Inicialización de Flask
app = Flask(__name__, static_folder='static')
app.secret_key = 'tu_clave_secreta_aqui'  # Cambiar en producción

# Conexión a la base de datos
def get_db_connection():
    try:
        conn = mysql.connector.connect(
            host="database4pr.cqq3cpmmrkby.us-east-1.rds.amazonaws.com",
            user="admin",
            password="73144231soto",
            database="pasion_por_el_voley",
            connect_timeout=5
        )
        logger.info("Conexión a la base de datos establecida correctamente")
        return conn
    except mysql.connector.Error as err:
        logger.error(f"Error de conexión a la base de datos: {err}")
        return None

# Ruta para probar la conexión a la base de datos
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
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500

# Rutas principales
@app.route('/')
def index():
    return render_template('index.html')

@app.route('/formulario')
def formulario():
    return render_template('formulario_unificado.html')

@app.route('/lista_usuarios')
def lista_usuarios():
    return render_template('lista_usuarios.html')

@app.route('/registrar_usuario', methods=['POST'])
def registrar_usuario():
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn is None:
            return jsonify({
                "éxito": False,
                "mensaje": "No se pudo conectar a la base de datos"
            }), 500
        
        cursor = conn.cursor(dictionary=True)
        
        if 'usuario_id' in request.form:
            return jsonify({
                "éxito": False,
                "mensaje": "La edición de usuarios no está permitida por políticas de privacidad"
            }), 403
        
        # Campos requeridos
        campos_requeridos = ['nombre', 'apellidos', 'fecha_nacimiento', 'edad', 'email']
        if int(request.form.get('edad', 0)) < 18:
            campos_requeridos.extend(['nombre_apoderado', 'telefono_apoderado', 'centro_salud', 'tipo_documento', 'numero_documento'])
        
        for campo in campos_requeridos:
            if not request.form.get(campo):
                return jsonify({
                    "éxito": False,
                    "mensaje": f"El campo {campo.replace('_', ' ')} es requerido",
                    "campo": campo
                }), 400
        
        # Datos personales
        datos_personales = {
            'nombre': request.form['nombre'],
            'apellidos': request.form['apellidos'],
            'fecha_nacimiento': request.form['fecha_nacimiento'],
            'edad': int(request.form['edad']),
            'email': request.form['email'],
            'direccion': request.form.get('direccion', ''),
            'acepto_terminos': True,
            'fecha_registro': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        }
        
        cursor.execute("""
            INSERT INTO usuarios (nombre, apellidos, fecha_nacimiento, edad, email, 
                                 direccion, acepto_terminos, fecha_registro)
            VALUES (%(nombre)s, %(apellidos)s, %(fecha_nacimiento)s, %(edad)s, 
                   %(email)s, %(direccion)s, %(acepto_terminos)s, %(fecha_registro)s)
        """, datos_personales)
        
        usuario_id = cursor.lastrowid
        
        # Datos del apoderado
        datos_apoderado = {
            'usuario_id': usuario_id,
            'nombre_apoderado': request.form['nombre_apoderado'],
            'telefono_apoderado': request.form['telefono_apoderado'],
            'correo_apoderado': request.form.get('correo_apoderado', ''),
            'centro_salud': request.form['centro_salud'],
            'tipo_documento': request.form['tipo_documento'],
            'numero_documento': request.form['numero_documento']
        }
        
        cursor.execute("""
            INSERT INTO apoderados (usuario_id, nombre_apoderado, telefono_apoderado, 
                                  correo_apoderado, centro_salud, tipo_documento, numero_documento)
            VALUES (%(usuario_id)s, %(nombre_apoderado)s, %(telefono_apoderado)s, 
                   %(correo_apoderado)s, %(centro_salud)s, %(tipo_documento)s, %(numero_documento)s)
        """, datos_apoderado)
        
        # Datos deportivos
        datos_deportivos = {
            'usuario_id': usuario_id,
            'nivel_actual': request.form.get('nivel_actual', 'Principiante'),
            'practico_deporte': 1 if request.form.get('practico_deporte') == 'si' else 0,
            'posicion': request.form.get('posicion', ''),
            'seguro_medico': 1 if request.form.get('seguro_medico') == 'si' else 0
        }
        
        cursor.execute("""
            INSERT INTO datos_deportivos (usuario_id, nivel_actual, practico_deporte, posicion, seguro_medico)
            VALUES (%(usuario_id)s, %(nivel_actual)s, %(practico_deporte)s, %(posicion)s, %(seguro_medico)s)
        """, datos_deportivos)
        
        conn.commit()
        
        return jsonify({
            "éxito": True,
            "mensaje": "Usuario registrado exitosamente!",
            "redireccion": url_for('lista_usuarios')
        })
        
    except mysql.connector.IntegrityError as e:
        if conn:
            conn.rollback()
        if "Duplicate entry" in str(e):
            campo = "email" if "email" in str(e) else "numero_documento"
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
        if conn:
            conn.rollback()
        logger.error(f"Error al registrar usuario: {str(e)}")
        return jsonify({
            "éxito": False,
            "mensaje": f"Error al registrar usuario: {str(e)}"
        }), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Otras rutas (abreviadas para claridad)
@app.route('/obtener_usuarios')
def obtener_usuarios():
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
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
        nombre = request.args.get('nombre')
        nivel = request.args.get('nivel')
        if nombre:
            query += " AND (u.nombre LIKE %s OR u.apellidos LIKE %s)"
            params.extend([f"%{nombre}%", f"%{nombre}%"])
        if nivel:
            query += " AND d.nivel_actual = %s"
            params.append(nivel)
        cursor.execute(query, params)
        usuarios = cursor.fetchall()
        return jsonify(usuarios)
    except Exception as e:
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
        cursor = conn.cursor()
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
        return jsonify({
            "éxito": False,
            "mensaje": f"Error al eliminar usuario: {str(e)}"
        }), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/horarios')
def horarios():
    return render_template('horarios.html')

@app.route('/donde_estamos')
def donde_estamos():
    return render_template('donde_estamos.html')

@app.route('/contactanos')
def contactanos():
    return render_template('contactanos.html')

@app.route('/torneos')
def torneos():
    return render_template('torneo.html')

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
                flash('No se pudo conectar a la base de datos para la inscripción.', 'error')
                return redirect(url_for('inscripcion_torneo'))
            cursor = conn.cursor()
            sql = """
            INSERT INTO inscripciones_equipos_torneo (nombre_equipo, categoria, delegado, telefono, email, fecha_inscripcion)
            VALUES (%s, %s, %s, %s, %s, NOW())
            """
            cursor.execute(sql, (nombre_equipo, categoria, delegado, telefono, email))
            conn.commit()
            flash('¡Inscripción al torneo realizada con éxito!', 'success')
            return redirect(url_for('torneos'))
        except mysql.connector.Error as err:
            logger.error(f"Error al guardar inscripción de torneo: {err}")
            if conn:
                conn.rollback()
            flash(f'Error al procesar tu inscripción: {err}', 'error')
            return render_template('inscripcion-torneo.html', 
                                  nombre_equipo=nombre_equipo, categoria=categoria, 
                                  delegado=delegado, telefono=telefono, email=email)
        except Exception as e:
            logger.error(f"Error inesperado en inscripción de torneo: {e}")
            if conn:
                conn.rollback()
            flash(f'Ocurrió un error inesperado. Por favor, inténtalo de nuevo.', 'error')
            return render_template('inscripcion-torneo.html', 
                                  nombre_equipo=nombre_equipo, categoria=categoria, 
                                  delegado=delegado, telefono=telefono, email=email)
        finally:
            if cursor:
                cursor.close()
            if conn:
                conn.close()
    return render_template('inscripcion-torneo.html')

@app.route('/panel-entrenador', methods=['GET', 'POST'])
def registro_torneo():
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
            'mensaje': 'Torneo eliminado exitosamente!'
        })
    except mysql.connector.Error as e:
        logger.error(f'Error al eliminar torneo: {str(e)}')
        if conn:
            conn.rollback()
        return jsonify({
            'éxito': False,
            'mensaje': f'Error al eliminar torneo: {str(e)}'
        }), 500
    except Exception as e:
        logger.error(f'Error inesperado al eliminar torneo: {str(e)}')
        if conn:
            conn.rollback()
        return jsonify({
            'éxito': False,
            'mensaje': f'Error inesperado al eliminar torneo: {str(e)}'
        }), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/login-entrenador', methods=['POST'])
def login_entrenador():
    usuario = request.form['usuario']
    contrasena = request.form['contrasena']
    if usuario == "admin" and contrasena == "1234":
        flash("¡Bienvenido, Entrenador!", "success")
        return redirect(url_for('registro_torneo'))
    else:
        flash("Usuario o contraseña incorrectos", "error")
        return redirect(url_for('index'))

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

@app.route('/usuarios-panel')
def panel_usuarios():
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
    return render_template('torneos_panel.html', torneos=torneos)

@app.route('/estadisticas-panel')
def panel_estadisticas():
    return render_template('estadisticas.html')

@app.route('/formulario-panel')
def panel_formulario():
    return render_template('formulario_unificado.html')

# Ruta para equipos inscritos
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
    except mysql.connector.Error as e:
        logger.error(f'Error al cargar equipos: {str(e)}')
        flash(f'Error al cargar equipos: {str(e)}', 'error')
    except Exception as e:
        logger.error(f'Error inesperado al cargar equipos: {str(e)}')
        flash(f'Error inesperado al cargar equipos: {str(e)}', 'error')
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
    return render_template('inscripcion-equipos.html', equipos=equipos)

# Ejecutar la aplicación Flask en modo depuración si es el script principal
if __name__ == '__main__':
    app.run(debug=True)