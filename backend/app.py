from flask import Flask, render_template, request, make_response, redirect, jsonify
from flask_cors import CORS
from datetime import datetime
import logging
import json

from model.usuarios import Usuarios
from model.estados import Estados
from model.municipios import Municipios
from utils.utils import Utils
from model.profissoes import Profissoes
from model.usuariosProfissoes import UsuariosProfissoes
from model.servicosPrestados import ServicosPrestados

from utils.database import db_session

#pip install -r requirements.txt

app = Flask(__name__)

# Configuração simplificada e mais permissiva do CORS
CORS(app, 
     resources={r"/*": {"origins": "*"}},
     allow_headers=["Content-Type", "Authorization", "X-Username"],
     methods=["GET", "POST", "PUT", "DELETE", "OPTIONS"],
     expose_headers=["Content-Type"],
     send_wildcard=True,
     always_send=True
)

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

# Handler adicional para garantir CORS em todas as respostas
@app.after_request
def after_request(response):
    origin = request.headers.get('Origin')
    if origin:
        response.headers['Access-Control-Allow-Origin'] = origin
    else:
        response.headers['Access-Control-Allow-Origin'] = '*'
    response.headers['Access-Control-Allow-Headers'] = 'Content-Type,Authorization,X-Username'
    response.headers['Access-Control-Allow-Methods'] = 'GET,POST,PUT,DELETE,OPTIONS'
    response.headers['Access-Control-Max-Age'] = '3600'
    return response


class APIRoutes():
    Usuarios = Usuarios()
    Estados = Estados()
    Municipios = Municipios()
    Profissoes = Profissoes()
    UsuariosProfissoes = UsuariosProfissoes()
    ServicosPrestados = ServicosPrestados()
    Avaliacoes = ["Nenhuma", "Péssimo", "Ruim", "Regular", "Bom", "Ótimo"]
    UsuarioSituacoesValidas = ["A", "U"]

    @staticmethod
    @app.route("/api/auth/login", methods=["POST"])
    def login():
        data = request.get_json()
        username = data.get("username")
        password = data.get("password")
        
        if APIRoutes.Usuarios.valida_senha(username, password):
            user = APIRoutes.Usuarios.get(username)
            token = Utils.hash_token(username)
            return jsonify({
                "success": True,
                "token": token,
                "user": {
                    "username": username,
                    "name": user.nome,
                    "isAdmin": APIRoutes.Usuarios.is_admin(username)
                }
            })
        return jsonify({"success": False, "message": "Usuário não encontrado ou senha inválida!"}), 401

    @staticmethod
    @app.route("/api/auth/logout", methods=["POST"])
    def logout():
        return jsonify({"success": True, "message": "Logout realizado com sucesso"})

    @staticmethod
    @app.route("/api/user/register", methods=["POST"])
    def register():
        data = request.get_json()
        nome = data.get("nome")
        estado = data.get("estado")
        cidade = Utils.safe_int(data.get("cidade"))
        bairro = data.get("bairro")
        telefone = data.get("telefone")
        email = data.get("email")
        cpf_cnpj = Utils.sanitize_doc(data.get("cpfCnpj"))
        password = data.get("password")
        
        # Validações
        campos_validar = [nome, estado, cidade, email, telefone, bairro, cpf_cnpj, password]
        for field in campos_validar:
            if not field:
                return jsonify({"success": False, "message": "Dados inválidos!"}), 400
        
        nome_cidade = APIRoutes.Municipios.get_nome(cidade)
        if not nome_cidade:
            return jsonify({"success": False, "message": "Município inválido!"}), 400
            
        if len(password) < 8:
            return jsonify({"success": False, "message": "Senha muito pequena!"}), 400
            
        if APIRoutes.Usuarios.get(email):
            return jsonify({"success": False, "message": "Usuario já cadastrado!"}), 400
            
        APIRoutes.Usuarios.cadastra(nome, telefone, cpf_cnpj, password, email, cidade, estado, bairro)
        return jsonify({"success": True, "message": "Cadastro criado com sucesso!"})

    @staticmethod
    @app.route("/api/user/profile", methods=["GET"])
    def get_profile():
        # Implementar autenticação por token aqui
        username = request.headers.get('X-Username')  # Simplificado
        if not username:
            return jsonify({"success": False, "message": "Não autorizado"}), 401
            
        usuario = APIRoutes.Usuarios.get(username)
        if not usuario:
            return jsonify({"success": False, "message": "Usuário não encontrado"}), 404
            
        return jsonify({
            "success": True,
            "user": {
                "nome": usuario.nome,
                "email": usuario.email,
                "telefone": usuario.telefone,
                "estado": usuario.id_estado,
                "cidade": usuario.id_municipio,
                "bairro": usuario.bairro,
                "cpfCnpj": usuario.cpf_cnpj
            }
        })

    @staticmethod
    @app.route("/api/user/profile", methods=["PUT"])
    def update_profile():
        username = request.headers.get('X-Username')
        if not username:
            return jsonify({"success": False, "message": "Não autorizado"}), 401
            
        data = request.get_json()
        nome = data.get("nome")
        telefone = data.get("telefone")
        cidade = Utils.safe_int(data.get("cidade"))
        estado = data.get("estado")
        bairro = data.get("bairro")
        
        if APIRoutes.Usuarios.altera(username, nome, telefone, cidade, estado, bairro):
            return jsonify({"success": True, "message": "Cadastro alterado com sucesso!"})
        return jsonify({"success": False, "message": "Erro ao alterar os dados!"}), 400

    @staticmethod
    @app.route("/api/locations/states", methods=["GET"])
    def get_states():
        estados = [{"uf": x.UF, "nome": x.Nome} for x in APIRoutes.Estados]
        return jsonify({"success": True, "data": estados})

    @staticmethod
    @app.route("/api/locations/cities/<uf>", methods=["GET"])
    def get_cities(uf):
        cidades = APIRoutes.Municipios.get_municipios_uf(uf)
        lista = [{"id": x.Id, "nome": x.Nome} for x in cidades]
        return jsonify({"success": True, "data": lista})

    @staticmethod
    @app.route("/api/professions", methods=["GET"])
    def get_professions():
        lista = [{"id": x.id_profissao, "nome": x.nome} for x in APIRoutes.Profissoes.lista()]
        return jsonify({"success": True, "data": lista})

    @staticmethod
    @app.route("/api/professions", methods=["POST"])
    def create_profession():
        data = request.get_json()
        nome = data.get('nome')
        msg = APIRoutes.Profissoes.cadastra(nome)
        if msg is None:
            return jsonify({"success": True, "message": "Profissão cadastrada com sucesso!"})
        return jsonify({"success": False, "message": msg}), 400

    @staticmethod
    @app.route("/api/user/professions", methods=["GET"])
    def get_user_professions():
        username = request.headers.get('X-Username')
        if not username:
            return jsonify({"success": False, "message": "Não autorizado"}), 401
            
        usuario = APIRoutes.Usuarios.get(username)
        profissoes = APIRoutes.UsuariosProfissoes.busca_profissoes(usuario.id_usuario)
        avaliacoes = APIRoutes.ServicosPrestados.avaliacoes_prestador(usuario.id_usuario, [x[0] for x in profissoes])
        
        lista = [{
            "id": x[0], 
            "nome": APIRoutes.Profissoes.get_nome(x[0]), 
            "situacao": x[1], 
            "avaliacao": APIRoutes.Avaliacoes[avaliacoes.get(x[0], 0)]
        } for x in profissoes]
        
        return jsonify({"success": True, "data": lista})

    @staticmethod
    @app.route("/api/user/professions", methods=["POST"])
    def add_user_profession():
        username = request.headers.get('X-Username')
        if not username:
            return jsonify({"success": False, "message": "Não autorizado"}), 401
            
        usuario = APIRoutes.Usuarios.get(username)
        data = request.get_json()
        id_profissao = Utils.safe_int(data.get('id'))
        
        if (not id_profissao) or (not APIRoutes.Profissoes.get_nome(id_profissao)):
            return jsonify({"success": False, "message": "Profissão não localizada!"}), 400
            
        msg = APIRoutes.UsuariosProfissoes.cadastra(usuario.id_usuario, id_profissao)
        if msg is None:
            return jsonify({"success": True, "message": "Profissão adicionada com sucesso!"})
        return jsonify({"success": False, "message": msg}), 400

    @staticmethod
    @app.route("/api/search/professionals", methods=["GET"])
    def search_professionals():
        id_profissao = Utils.safe_int(request.args.get("idProfissao"), 0)
        id_cidade = Utils.safe_int(request.args.get("idMunicipio"))
        
        profissionais = APIRoutes.UsuariosProfissoes.busca_profissionais(id_profissao, id_cidade)
        usuarios = {}
        
        for idUsuario, situacao in profissionais:
            usuario = APIRoutes.Usuarios.get_por_id(idUsuario)
            if usuario and usuario.situacao in APIRoutes.UsuarioSituacoesValidas and (id_cidade is None or usuario.IdMunicipio == id_cidade):
                usuarios[idUsuario] = usuario
                
        lista = [{
            "id": x[0], 
            "idProfissao": id_profissao, 
            "contato": usuarios[x[0]].telefone, 
            "nome": usuarios[x[0]].nome, 
            "nota": APIRoutes.Avaliacoes[APIRoutes.ServicosPrestados.avaliacao_prestador(x[0], id_profissao)]
        } for x in profissionais if x[0] in usuarios]
        
        return jsonify({"success": True, "data": lista})

    @staticmethod
    @app.route("/api/reviews", methods=["POST"])
    def create_review():
        username = request.headers.get('X-Username')
        if not username:
            return jsonify({"success": False, "message": "Não autorizado"}), 401
            
        usuario = APIRoutes.Usuarios.get(username)
        data = request.get_json()
        id_profissao = Utils.safe_int(data.get('idProfissao'))
        id_prestador = Utils.safe_int(data.get('idPrestador'))
        avaliacao = Utils.safe_int(data.get('avaliacao'))
        comentario = data.get('comentario')
        data_avaliacao = datetime.now().strftime("%Y-%m-%d")
        
        if APIRoutes.ServicosPrestados.cadastra(usuario.id_usuario, id_prestador, id_profissao, avaliacao, comentario, data_avaliacao):
            return jsonify({"success": True, "message": "Avaliação enviada com sucesso!"})
        return jsonify({"success": False, "message": "Ocorreu um erro gravando a avaliação!"}), 400

    @app.teardown_appcontext
    def shutdown_session(exception=None):
        db_session.remove()


if __name__ == "__main__":
    logger.info("Starting API server")
    app.run(port=5050, debug=True)
