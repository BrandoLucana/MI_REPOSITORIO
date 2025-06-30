from flask import Flask, render_template, request, jsonify, redirect, url_for, flash, session
from functools import wraps
import mysql.connector
from datetime import datetime
from werkzeug.security import generate_password_hash, check_password_hash
import logging
import os
from dotenv import load_dotenv

load_dotenv()  # Cargar variables del archivo .env


# Configuración de logging
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

app = Flask(__name__, static_folder='static', template_folder='templates')
app.secret_key = '981837328rds'  # Usa una clave segura (puedes generar una con secrets.token_hex(16))

# Configuración de la base de datos
def get_db_connection():
    try:
        conn = mysql.connector.connect(
    host=os.getenv("DB_HOST"),
    user=os.getenv("DB_USER"),
    password=os.getenv("DB_PASSWORD"),
    database=os.getenv("DB_NAME"),
    connect_timeout=5
)

        logger.info("Conexión a la base de datos establecida correctamente")
        return conn
    except mysql.connector.Error as err:
        logger.error(f"Error de conexión a la base de datos: {err}")
        return None

# Decorador para proteger rutas
def login_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'trainer' not in session:
            flash('Por favor, inicia sesión como entrenador.', 'error')
            return redirect(url_for('index'))
        return f(*args, **kwargs)
    return decorated_function

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

@app.route('/formulario_unificado')
def formulario():
    return render_template('formulario_unificado.html')

@app.route('/lista_usuarios')
@login_required
def lista_usuarios():
    return render_template('lista-usuarios.html')

@app.route('/registrar_usuario', methods=['POST'])
def registrar_usuario():
    conn = None
    cursor = None
    try:
        if request.is_json:
            data = request.get_json()
        else:
            data = request.form

        logger.debug(f"Datos recibidos: {data}")

        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        
        if 'usuario_id' in data:
            return jsonify({
                "éxito": False,
                "mensaje": "La edición de usuarios no está permitida por políticas de privacidad"
            }), 403
        
        campos_requeridos = {
            'nombre': 'Nombre',
            'apellidos': 'Apellidos',
            'fecha_nacimiento': 'Fecha de nacimiento',
            'edad': 'Edad',
            'email': 'Email',
            'nombre_apoderado': 'Nombre del apoderado',
            'telefono_apoderado': 'Teléfono del apoderado',
            'centro_salud': 'Centro de salud',
            'tipo_documento': 'Tipo de documento',
            'numero_documento': 'Número de documento'
        }
        
        campos_faltantes = []
        for campo, nombre_campo in campos_requeridos.items():
            if campo not in data or not data[campo]:
                campos_faltantes.append(nombre_campo)
        
        if campos_faltantes:
            return jsonify({
                "éxito": False,
                "mensaje": f"Los siguientes campos son requeridos: {', '.join(campos_faltantes)}",
                "campos_faltantes": campos_faltantes
            }), 400
        
        datos_personales = {
            'nombre': data['nombre'],
            'apellidos': data['apellidos'],
            'fecha_nacimiento': data['fecha_nacimiento'],
            'edad': int(data['edad']),
            'email': data['email'],
            'direccion': data.get('direccion', ''),
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
        
        datos_apoderado = {
            'usuario_id': usuario_id,
            'nombre_apoderado': data['nombre_apoderado'],
            'telefono_apoderado': data['telefono_apoderado'],
            'correo_apoderado': data.get('correo_apoderado', ''),
            'centro_salud': data['centro_salud'],
            'tipo_documento': data['tipo_documento'],
            'numero_documento': data['numero_documento']
        }
        
        cursor.execute("""
            INSERT INTO apoderados (usuario_id, nombre_apoderado, telefono_apoderado, 
                                  correo_apoderado, centro_salud, tipo_documento, numero_documento)
            VALUES (%(usuario_id)s, %(nombre_apoderado)s, %(telefono_apoderado)s, 
                   %(correo_apoderado)s, %(centro_salud)s, %(tipo_documento)s, %(numero_documento)s)
        """, datos_apoderado)
        
        datos_deportivos = {
            'usuario_id': usuario_id,
            'nivel_actual': data.get('nivel_actual', 'Principiante'),
            'practico_deporte': 1 if data.get('practico_deporte') == 'si' else 0,
            'posicion': data.get('posicion', ''),
            'seguro_medico': 1 if data.get('seguro_medico') else 0
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
            "mensaje": f'Error al registrar usuario: {str(e)}'
        }), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/obtener_usuarios')
@login_required
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
@login_required
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

# Rutas de autenticación para entrenadores
@app.route('/login', methods=['POST'])
def login():
    email = request.form.get('email')
    password = request.form.get('password')
    
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn is None:
            flash('Error de conexión a la base de datos.', 'error')
            return redirect(url_for('index'))
        
        cursor = conn.cursor(dictionary=True)
        cursor.execute("SELECT id, nombre, email, password FROM entrenadores WHERE email = %s", (email,))
        trainer = cursor.fetchone()
        
        if trainer and check_password_hash(trainer['password'], password):
            session['trainer'] = {
                'id': trainer['id'],
                'nombre': trainer['nombre'],
                'email': trainer['email']
            }
            flash('Inicio de sesión exitoso.', 'success')
            return redirect(url_for('index'))
        else:
            flash('Correo o contraseña incorrectos.', 'error')
            return redirect(url_for('index'))
            
    except Exception as e:
        logger.error(f"Error en login: {str(e)}")
        flash('Error al intentar iniciar sesión.', 'error')
        return redirect(url_for('index'))
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/register-trainer', methods=['POST'])
def register_trainer():
    nombre = request.form.get('nombre')
    email = request.form.get('email')
    password = request.form.get('password')
    
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        if conn is None:
            flash('Error de conexión a la base de datos.', 'error')
            return redirect(url_for('index'))
        
        cursor = conn.cursor()
        
        # Verificar si el email ya existe
        cursor.execute("SELECT id FROM entrenadores WHERE email = %s", (email,))
        if cursor.fetchone():
            flash('El correo ya está registrado.', 'error')
            return redirect(url_for('index'))
        
        # Hashear la contraseña
        hashed_password = generate_password_hash(password, method='pbkdf2:sha256')
        
        # Insertar nuevo entrenador
        cursor.execute("""
            INSERT INTO entrenadores (nombre, email, password, fecha_registro)
            VALUES (%s, %s, %s, %s)
        """, (nombre, email, hashed_password, datetime.now()))
        
        conn.commit()
        flash('Entrenador registrado exitosamente. Inicia sesión.', 'success')
        return redirect(url_for('index'))
        
    except mysql.connector.IntegrityError as e:
        conn.rollback()
        flash('El correo ya está registrado.', 'error')
        return redirect(url_for('index'))
    except Exception as e:
        conn.rollback()
        logger.error(f"Error en registro de entrenador: {str(e)}")
        flash('Error al registrar entrenador.', 'error')
        return redirect(url_for('index'))
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/logout', methods=['POST'])
def logout():
    session.pop('trainer', None)
    flash('Sesión cerrada.', 'success')
    return redirect(url_for('index'))

# Otras rutas de la página
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
        
        return redirect(url_for('torneos'))
    
    return render_template('inscripcion-torneo.html')

@app.route('/registro-torneo', methods=['GET', 'POST'])
@login_required
def registro_torneo():
    if request.method == 'POST':
        nombre_torneo = request.form['nombre_torneo']
        fecha = request.form['fecha']
        lugar = request.form['lugar']
        categoria = request.form['categoria']

        try:
            conn = get_db_connection()
            cursor = conn.cursor()

            cursor.execute("""
                INSERT INTO torneos (nombre_torneo, fecha, lugar, categoria)
                VALUES (%s, %s, %s, %s)
            """, (nombre_torneo, fecha, lugar, categoria))

            conn.commit()
            cursor.close()
            conn.close()

            flash('Torneo registrado exitosamente!', 'success')
            return redirect(url_for('registro_torneo'))

        except Exception as e:
            flash(f'Error al registrar torneo: {str(e)}', 'error')
            return redirect(url_for('registro_torneo'))

    return render_template('registro_torneo.html')

# Eliminar la ruta antigua de login-entrenador
# @app.route('/login-entrenador', methods=['POST']) ya no es necesaria

@app.route('/mas')
def mas():
    return render_template('mas.html')

@app.route('/galeria')
def galeria():
    return render_template('galeria.html')

@app.route('/eventos')
def eventos():
    return render_template('eventos.html')

@app.route('/noticias')
def noticias():
    return render_template('noticias.html')

@app.route('/inscripciones')
def inscripciones():
    return render_template('inscripciones.html')

@app.route('/nosotros')
def nosotros():
    return render_template('nosotros.html')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
