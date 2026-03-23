from utils.utils import Utils
from utils.baseModel import BaseTable

from utils.database import Base, db_session as Session
from sqlalchemy import Column, Integer, String, DateTime, BigInteger
from datetime import datetime


class Usuario(BaseTable, Base):
    __tablename__ = 'usuarios'
    id_usuario = Column(Integer, primary_key =True)
    nome = Column(String)
    telefone = Column(String)
    cpf_cnpj = Column(BigInteger, unique=True)
    email = Column(String, unique=True)
    id_municipio = Column(Integer)
    id_estado = Column(String)
    bairro = Column(String)
    situacao = Column(String)
    bairro = Column(String)
    bairro_busca = Column(String)
    senha = Column(String)
    data_inclusao = Column(DateTime)
    data_alteracao = Column(DateTime, nullable=True)

    @property
    def Admin(self) -> bool:
        return self.situacao == "A"
    
    def __repr__(self):
        return f'<User {self.nome!r}>'    
   
class Usuarios():
    def valida_senha(self, username: str, senha: str) -> bool:
        user = self.get(username)
        return bool(user and user.senha == Utils.hash_password(username, senha))

    def is_admin(self, username: str) -> bool:
        user = self.get(username)
        return bool(user and user.Admin)

    def get_nome(self, username: str) -> str:
        user = self.get(username)
        if user:
            return user.nome

    def get_por_id(self, id: int) -> str:
        dr = Usuario.query.filter(Usuario.id_usuario == id)
        if dr.count() == 1:
            return dr.first()

    def get(self, username: str) -> Usuario:
        dr = Usuario.query.filter(Usuario.email == username)
        if dr.count() == 1:
            return dr.first()

    def cadastra(self, nome: str, telefone: str, cpf_cnpj: int, senha: str, email: str, id_municipio: int, id_estado: str, bairro: str = '', admin: bool = False):
        # 'id_usuario', 'Nome', 'Telefone', 'CpfCnpj', 'Email', 'IdMunicipio', 'IdEstado', 'Bairro', 'Situacao', 'BairroBusca', 'Senha'
        user = Usuario(nome, telefone, cpf_cnpj, email, id_municipio, id_estado, bairro, "A" if admin else "U", 
                       Utils.normalized_search(bairro), Utils.hash_password(email, senha), datetime.now())
        Session.add(user)
        Session.commit()
        return True

    def altera(self, username:str, nome: str, telefone: str, id_municipio: int, id_estado: str, bairro: str = '', admin: bool = False):
        user: Usuario = self.get(username)
        if user:
            user.nome = nome
            user.telefone = telefone
            user.id_municipio = id_municipio
            user.id_estado = id_estado
            user.bairro = bairro
            user.bairro_busca = Utils.normalized_search(bairro)
            Session.commit()
            return True

    def set_situacao(self, username: str, situacao: str):
        user: Usuario = self.get(username)
        if user:
            user.situacao = situacao
            Session.commit()
            return True

    def altera_senha(self, email: str, senha: str):
        user: Usuario = self.get(email)
        if user:
            user.senha = Utils.hash_password(email, senha)
            Session.commit()
            return True


def popula_tabela(usuarios):
    base = "INSERT INTO v0.usuarios (nome, telefone, cpf_cnpj, senha, email, id_municipio, id_estado, bairro, situacao, bairro_busca)"
    with open("./sql_scripts/popula-tabela-usuarios.sql", "w") as arq:
        for u in usuarios.values():
            line = f"{base} VALUES ('{u.Nome}', '{str(u.Id)}', {u.Id}, '{u.Senha}', '{u.Email}', {u.Municipio}, '{u.UF}', '{u.Bairro}', '{'A' if u.Admin else 'U'}', '{u.BairroBusca}' );\n"
            arq.write(line)
        arq.flush()


if __name__ == "__main__":
    usuarios = Usuarios()
    print("fulano", usuarios.valida_senha("fulano", "xkx"), usuarios.is_admin("fulano"))
    print("renato", usuarios.valida_senha("23212332@aluno.univesp.br", "23212332"), usuarios.is_admin("23212332@aluno.univesp.br"))
    print("kleber senha inv", usuarios.valida_senha("2216670@aluno.univesp.br", "xkx"), usuarios.is_admin("2216670@aluno.univesp.br"))
    print("Ariadne", usuarios.valida_senha("23221782@aluno.univesp.br", "23221782"), usuarios.is_admin("23221782@aluno.univesp.br"))
