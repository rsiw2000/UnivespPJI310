from flask import Flask, render_template, request, make_response, redirect
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

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)


class MainPage():
    Usuarios = Usuarios()
    Estados = Estados()
    Municipios = Municipios()
    Profissoes = Profissoes()
    UsuariosProfissoes = UsuariosProfissoes()
    ServicosPrestados = ServicosPrestados()
    Avaliacoes = ["Nenhuma", "Péssimo", "Ruim", "Regular", "Bom", "Ótimo"]
    UsuarioSituacoesValidas = ["A", "U"]

    # caption, link, islogged, isadmin
    Actions = [
        ("Entrar", "login", False, False),
        ("Sair", "logout", True, False),
        ("Novo Usuário", "cadastro", False, False),
        ("Meu Cadastro", "meu_cadastro", True, False),
        ("Profissões", "profissoes", True, True),
        ("Minhas Profissões", "minhas_profissoes", True, False),
        ("Buscar Profissional", "buscar", True, False),
        ("Esqueci a Senha", "reset", False, False)
    ]

    @staticmethod
    @app.route("/", methods=["GET"])
    def home(message: str = None):
        username = request.cookies.get('username')
        token = request.cookies.get('token')
        isLogged = bool(token and username)
        nome = MainPage.Usuarios.get_nome(username) if isLogged else None
        isAdmin = MainPage.Usuarios.is_admin(username) if isLogged else False
        return MainPage.render_home(nome, isLogged, isAdmin, message)

    @staticmethod
    def render_home(nome=None, isLogged=False, isAdmin=False, message: str = None):
        _actions = [Utils.get_action_if_valid(x, isLogged, isAdmin) for x in MainPage.Actions if Utils.get_action_if_valid(x, isLogged, isAdmin)]
        if message:
            _message = message
        elif isLogged:
            _message = f"Olá {nome}, seja bem vindo. {'ADMIN' if isAdmin else ''}"
        else:
            _message = "Olá, faça login para iniciar ..."
        return render_template("home.html", actions = _actions, welcome_message = _message)
        
    @staticmethod
    @app.route("/login", methods=["GET"])
    def login():
        return render_template("login.html")

    @app.route("/do_login", methods=["POST"])
    def do_login():
        if request.method != "POST":
            return "Not Allowed Method", 405
        username = request.form.get("username")
        password = request.form.get("password")
        if MainPage.Usuarios.valida_senha(username, password):
            response = make_response(redirect("/"))
            response.set_cookie("username", username)
            response.set_cookie("token", Utils.hash_token(username))
            return response
        return render_template("login.html", message = "Usuário não encontrado ou senha inválida!")
    
    @staticmethod
    @app.route("/logout", methods=["GET"])
    def logout():
        username = request.cookies.get("username")
        nome = MainPage.Usuarios.get_nome(username)
        return render_template("logout.html", usuario=nome)

    @staticmethod
    @app.route("/do_logout", methods=["POST"])
    def do_logout():
        response = make_response(redirect("/"))
        if request.form.get("Sim") == "Sim":
            response.delete_cookie("username")
            response.delete_cookie("token")
        return response

    @staticmethod
    @app.route("/cadastro", methods=["GET"])
    def cadastro(message="", form={"email": "", "nome": "", "bairro": "", "cpfCnpj": "", "novo": "S"}):
        uf = form.get("estado","SP")
        estados = [{"Label": x.Nome, "Value": x.UF, "Selected": x.UF == uf} for x in MainPage.Estados]
        cidade = form.get("cidade")
        if cidade is None:
            cidades = [{"Label": "Selecione ...", "Value": "0", "Selected": False}]
        else:
            cidades = [{"Label": x.Nome, "Value": x.Id, "Selected": x.Id == cidade} for x in MainPage.Municipios.get_municipios_uf(uf)]
        title = "Novo cadastro" if form.get("novo") == "S" else "Alterar meu cadastro"
        return render_template("cadastro.html", estados=estados, cidades=cidades, message=message, form=form, title=title)

    @staticmethod
    @app.route("/meu_cadastro", methods=["GET"])
    def meu_cadastro():
        # get dados
        username = request.cookies.get('username')
        usuario = MainPage.Usuarios.get(username)
        form = {"nome": usuario.nome, "estado": usuario.id_estado, "email": usuario.email, "telefone": usuario.telefone, "cidade": usuario.id_municipio, "bairro": usuario.bairro, "cpfCnpj": usuario.cpf_cnpj, "novo": "N", "changePassword": "N"}
        return MainPage.cadastro(form=form)

    @staticmethod
    @app.route("/do_cadastro", methods=["POST"])
    def do_cadastro():
        novo = request.form.get("novo") == "S"
        alt_senha = request.form.get("changePassword") == "S"
        nome = request.form.get("nome")
        estado = request.form.get("estado")
        cidade = Utils.safe_int(request.form.get("cidade"))
        bairro = request.form.get("bairro")
        telefone = request.form.get("telefone")
        email = request.form.get("email")
        cpf_cnpj = Utils.sanitize_doc(request.form.get("cpfCnpj"))
        password = request.form.get("password")
        confirm_password = request.form.get("confirmPassword")
        current_password = request.form.get("currentPassword")
        campos_validar = [nome, estado, cidade, email, telefone, bairro, cpf_cnpj]
        if novo or alt_senha:
            campos_validar.extend([password, confirm_password])
            if alt_senha:
                campos_validar.extend([current_password])
        for field in campos_validar:
            if not field:
                return MainPage.cadastro(message="Dados inválidos!", form=request.form)
        nome_cidade = MainPage.Municipios.get_nome(cidade)
        if not nome_cidade:
            return MainPage.cadastro(message="Município inválido!", form=request.form)
        if alt_senha:
            username = request.cookies.get('username')
            if not MainPage.Usuarios.valida_senha(username, current_password):
                return MainPage.cadastro(message="Senhas atual inválida!", form=request.form)
        if novo or alt_senha:
            if password != confirm_password:
                return MainPage.cadastro(message="Senhas não conferem!", form=request.form)
            if len(password)<8:
                return MainPage.cadastro(message="Senha muito pequena!", form=request.form)
        if novo:
            if MainPage.Usuarios.get(email):
                return MainPage.cadastro(message="Usuario já cadastrado!", form=request.form)
            MainPage.Usuarios.cadastra(nome, telefone, cpf_cnpj, password, email, cidade, estado, bairro)
            return MainPage.home(message="Cadastro criado com sucesso!")
        else:
            # username:str, nome: str, telefone: str, id_municipio: int, id_estado: str, bairro: str = '',
            username = request.cookies.get('username')
            if username and MainPage.Usuarios.altera(username, nome, telefone, cidade, estado, bairro):
                if alt_senha:
                    if not MainPage.Usuarios.altera_senha(username, password):
                        return MainPage.cadastro(message="Erro ao alterar a senha!", form=request.form)
                return MainPage.home(message="Cadastro alterado com sucesso!")
            return MainPage.cadastro(message="Erro ao alterar os dados!", form=request.form)
            
    @staticmethod
    @app.route("/cadastro/municipios")
    def cadastro_municipios():
        params = Utils.query_string_to_dict(request.query_string.decode())
        uf = params.get("uf")
        if uf:
            cidades = MainPage.Municipios.get_municipios_uf(uf)
            lista = [{"id": x.Id, "nome": x.Nome} for x in cidades]
        else:
            lista = []
        result = make_response(lista)
        result.headers.add('Content-Type', 'application/json')
        return result

    @staticmethod
    @app.route("/buscar", methods=["GET"])
    def buscar():
        lista = [{"id": x.id_profissao, "nome": x.nome} for x in MainPage.Profissoes.lista()]
        avaliacoes = [{"id": id, "nome": nome} for id, nome in dict(zip(range(0,len(MainPage.Avaliacoes)),MainPage.Avaliacoes)).items()]
        return render_template("buscar.html", profissoes=lista, avaliacoes=avaliacoes, message="Selecione uma profissão!")

    @staticmethod
    @app.route("/buscar/profissionais", methods=["GET"])
    def buscar_profissionais():
        params = Utils.query_string_to_dict(request.query_string.decode())
        id_profissao = Utils.safe_int(params.get("idProfissao"), 0)
        id_cidade = Utils.safe_int(params.get("idMunicipio"))
        profissionais = MainPage.UsuariosProfissoes.busca_profissionais(id_profissao, id_cidade)
        usuarios = {}
        for idUsuario, situacao in profissionais:
            usuario = MainPage.Usuarios.get_por_id(idUsuario)
            if usuario and usuario.situacao in MainPage.UsuarioSituacoesValidas and (id_cidade is None or usuario.IdMunicipio == id_cidade):
                usuarios[idUsuario] = usuario
        lista = [{"id": x[0], "idProfissao": id_profissao, "contato": usuarios[x[0]].telefone, "nome": usuarios[x[0]].nome, "nota": MainPage.Avaliacoes[MainPage.ServicosPrestados.avaliacao_prestador(x[0], id_profissao)]}
                    for x in profissionais]
        result = make_response(lista)
        result.headers.add('Content-Type', 'application/json')
        return result

    @staticmethod
    @app.route("/buscar/avaliar", methods=["POST"])
    def avaliar():
        username = request.cookies.get('username')
        usuario = MainPage.Usuarios.get(username)
        id_profissao = Utils.safe_int(request.json.get('idProfissao'))
        id_prestador = Utils.safe_int(request.json.get('idPrestador'))
        avaliacao = Utils.safe_int(request.json.get('avaliacao'))
        comentario = request.json.get('comentario')
        data = datetime.now().strftime("%Y-%m-%d")
        if MainPage.ServicosPrestados.cadastra(usuario.id_usuario, id_prestador, id_profissao, avaliacao, comentario, data):
            return make_response({'message':"avaliação enviada com sucesso!"})
        return "Ocorreu um erro gravando a avaliação!", 400
        
    @staticmethod
    @app.route("/profissoes", methods=["GET"])
    def profissoes():
        lista = [{"id": x.id_profissao, "nome": x.nome} for x in MainPage.Profissoes.lista()]
        return render_template("profissoes.html", profissoes=lista, message="")

    @staticmethod
    @app.route("/profissoes/nova", methods=["POST"])
    def nova_profissao():
        dados = json.loads(request.data.decode())
        nova = dados.get('nome')
        msg = MainPage.Profissoes.cadastra(nova)
        if msg is None:
            result = make_response({'message':msg})
            result.headers.add('Content-Type', 'application/json')
            return result
        return msg, 400

    @staticmethod
    @app.route("/minhas_profissoes", methods=["GET"])
    def minhas_profissoes():
        usuario = MainPage.Usuarios.get(request.cookies.get('username'))
        profissoes = MainPage.UsuariosProfissoes.busca_profissoes(usuario.id_usuario)
        avaliacoes = MainPage.ServicosPrestados.avaliacoes_prestador(usuario.id_usuario, [x[0] for x in profissoes])
        lista = [{"id": x[0], "nome": MainPage.Profissoes.get_nome(x[0]), "situacao": x[1], "avaliacao": MainPage.Avaliacoes[avaliacoes.get(x[0], 0)] } 
                 for x in profissoes]
        lista_profissoes = [{"id": x.id_profissao, "nome": x.nome} for x in MainPage.Profissoes.lista()]
        return render_template("minhas_profissoes.html", minhasProfissoes=lista, profissoes=lista_profissoes, message="")

    @staticmethod
    @app.route("/minhas_profissoes/nova", methods=["POST"])
    def minha_nova_profissao():
        usuario = MainPage.Usuarios.get(request.cookies.get('username'))
        dados = json.loads(request.data.decode())
        nova = Utils.safe_int(dados.get('id'))
        if (not nova) or (not MainPage.Profissoes.get_nome(nova)):
            msg = f"Profissao não localizada!"
        else:
            msg = MainPage.UsuariosProfissoes.cadastra(usuario.id_usuario, nova)
        if msg is None:
            result = make_response({'message':msg})
            result.headers.add('Content-Type', 'application/json')
            return result
        return msg, 400

    @staticmethod
    @app.route("/reset", methods=["GET"])
    def reset():
        return render_template("reset.html", message="Não implementado!")

    @app.teardown_appcontext
    def shutdown_session(exception=None):
        db_session.remove()



if __name__ == "__main__":
    logger.info("Starting app")
    #print (hash_token("fulano"))
    #print (hash_password("fulano","678901"))
    app.run(port=5050, debug=True)