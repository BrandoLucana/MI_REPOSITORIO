from flask import Flask, render_template, request, redirect, url_for, jsonify, flash
import mysql.connector
from datetime import datetime

app = Flask(__name__)
app.secret_key = 'tu_clave_secreta_aqui'

# Configuración de la base de datos
def get_db_connection():
    return mysql.connector.connect(
        host="localhost",
        user="root",
        password="contra123",
        database="pasion_por_el_voley"
    )

# Rutas principales
@app.route('/')
def index():
    return render_template('index.html')

@app.route('/formulario')
def formulario():
    return render_template('formulario_unificado.html')

@app.route('/formulario/<int:id>')
def editar_formulario(id):
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        
        # Obtener datos del usuario
        cursor.execute("""
            SELECT u.*, a.*, d.*
            FROM usuarios u
            LEFT JOIN apoderados a ON u.id = a.usuario_id
            LEFT JOIN datos_deportivos d ON u.id = d.usuario_id
            WHERE u.id = %s
        """, (id,))
        
        usuario = cursor.fetchone()
        
        if not usuario:
            flash('Usuario no encontrado', 'danger')
            return redirect(url_for('lista_usuarios'))
        
        # Convertir valores para el formulario
        usuario['practico_deporte'] = 'si' if usuario['practico_deporte'] else 'no'
        usuario['seguro_medico'] = True if usuario['seguro_medico'] else False
        
        return render_template('formulario_unificado.html', usuario=usuario, edicion=True)
        
    except Exception as e:
        flash(f'Error al cargar usuario para edición: {str(e)}', 'danger')
        return redirect(url_for('lista_usuarios'))
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

@app.route('/lista_usuarios')
def lista_usuarios():
    return render_template('lista_usuarios.html')

@app.route('/registrar_usuario', methods=['POST'])
def registrar_usuario():
    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        
        # Determinar si es una edición
        es_edicion = 'usuario_id' in request.form
        usuario_id = request.form.get('usuario_id')
        
        # Procesar datos del formulario
        datos_personales = {
            'nombre': request.form['nombre'],
            'apellidos': request.form['apellidos'],
            'fecha_nacimiento': request.form['fecha_nacimiento'],
            'edad': int(request.form['edad']),
            'email': request.form['email'],
            'direccion': request.form.get('direccion', ''),
            'acepto_terminos': True
        }
        
        if es_edicion:
            # Actualizar usuario existente
            datos_personales['id'] = usuario_id
            cursor.execute("""
                UPDATE usuarios 
                SET nombre = %(nombre)s, apellidos = %(apellidos)s, 
                    fecha_nacimiento = %(fecha_nacimiento)s, edad = %(edad)s,
                    email = %(email)s, direccion = %(direccion)s
                WHERE id = %(id)s
            """, datos_personales)
        else:
            # Insertar nuevo usuario
            datos_personales['fecha_registro'] = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            cursor.execute("""
                INSERT INTO usuarios (nombre, apellidos, fecha_nacimiento, edad, email, direccion, acepto_terminos, fecha_registro)
                VALUES (%(nombre)s, %(apellidos)s, %(fecha_nacimiento)s, %(edad)s, %(email)s, %(direccion)s, %(acepto_terminos)s, %(fecha_registro)s)
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
        
        if es_edicion:
            cursor.execute("""
                UPDATE apoderados 
                SET nombre_apoderado = %(nombre_apoderado)s, 
                    telefono_apoderado = %(telefono_apoderado)s,
                    correo_apoderado = %(correo_apoderado)s,
                    centro_salud = %(centro_salud)s,
                    tipo_documento = %(tipo_documento)s,
                    numero_documento = %(numero_documento)s
                WHERE usuario_id = %(usuario_id)s
            """, datos_apoderado)
        else:
            cursor.execute("""
                INSERT INTO apoderados (usuario_id, nombre_apoderado, telefono_apoderado, correo_apoderado, 
                                      centro_salud, tipo_documento, numero_documento)
                VALUES (%(usuario_id)s, %(nombre_apoderado)s, %(telefono_apoderado)s, %(correo_apoderado)s, 
                       %(centro_salud)s, %(tipo_documento)s, %(numero_documento)s)
            """, datos_apoderado)
        
        # Datos deportivos
        datos_deportivos = {
            'usuario_id': usuario_id,
            'nivel_actual': request.form.get('nivel_actual', 'Principiante'),
            'practico_deporte': 1 if request.form.get('practico_deporte') == 'si' else 0,
            'posicion': request.form.get('posicion', ''),
            'seguro_medico': 1 if request.form.get('seguro_medico') else 0
        }
        
        if es_edicion:
            cursor.execute("""
                UPDATE datos_deportivos 
                SET nivel_actual = %(nivel_actual)s, 
                    practico_deporte = %(practico_deporte)s,
                    posicion = %(posicion)s,
                    seguro_medico = %(seguro_medico)s
                WHERE usuario_id = %(usuario_id)s
            """, datos_deportivos)
        else:
            cursor.execute("""
                INSERT INTO datos_deportivos (usuario_id, nivel_actual, practico_deporte, posicion, seguro_medico)
                VALUES (%(usuario_id)s, %(nivel_actual)s, %(practico_deporte)s, %(posicion)s, %(seguro_medico)s)
            """, datos_deportivos)
        
        conn.commit()
        flash('Usuario ' + ('actualizado' if es_edicion else 'registrado') + ' exitosamente!', 'success')
        return redirect(url_for('lista_usuarios'))
        
    except Exception as e:
        if conn:
            conn.rollback()
        flash(f'Error al {"editar" if es_edicion else "registrar"} usuario: {str(e)}', 'danger')
        return redirect(url_for('formulario'))
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
        flash('Usuario eliminado exitosamente!', 'success')
        return jsonify({'success': True})
        
    except Exception as e:
        if conn:
            conn.rollback()
        return jsonify({'success': False, 'error': str(e)}), 500
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# Nuevas rutas añadidas
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

if __name__ == '__main__':
    app.run(debug=True)