from utils.baseModel import BaseTable

from utils.database import Base, db_session as Session
from sqlalchemy import Column, Integer, CHAR


class UsuarioProfissao(BaseTable, Base):
    __tablename__ = 'usuarios_profissoes'
    id_usuario = Column(Integer, primary_key =True)
    id_profissao = Column(Integer, primary_key = True)
    situacao = Column(CHAR)
    Situacoes = {"A": "Ativo", "I": "Inativo", "S": "Suspenso"}

    def set(self, args):
        keys = [x.name for x in self.__table__.columns._all_columns]
        kw = dict(zip(keys, args))
        for key, value in kw.items():
            self.__setattr__(key, value)

    @property
    def Ativo(self):
        return self.situacao == "A"
    
    @property
    def Situacao(self):
        return self.Situacoes.get(self.situacao, self.situacao)


class UsuariosProfissoes():
    def busca_profissoes(self, id_usuario: int, somente_ativos: bool = True) -> list:
        dr = UsuarioProfissao.query.filter(UsuarioProfissao.id_usuario == id_usuario)
        return [(x.id_profissao, x.Situacao) for x in dr if (somente_ativos != True or x.Ativo)]

    def busca_profissionais(self, id_profissao: int, id_municipio: int = None, somente_ativos: bool = True) -> list:
        dr = UsuarioProfissao.query.filter(UsuarioProfissao.id_profissao == id_profissao)
        return [(x.id_usuario, x.Situacao) for x in dr if (x.Ativo or not somente_ativos)]

    def cadastra(self, id_usuario: int, id_profissao: int) -> bool:
        reg = self.obtem(id_usuario, id_profissao)
        if reg is not None:
            return "Profissão já cadastrada para o usuário"
        reg = UsuarioProfissao(id_usuario, id_profissao, "A")
        Session.add(reg)
        Session.commit()

    def obtem(self, id_usuario: int, id_profissao: int) -> UsuarioProfissao:
        dr = UsuarioProfissao.query.filter(UsuarioProfissao.id_usuario == id_usuario, UsuarioProfissao.id_profissao == id_profissao)
        if dr.count() == 1:
            return dr.first()

    def altera(self, id_usuario: int, id_profissao: int, situacao: str) -> bool:
        reg = self.obtem(id_usuario, id_profissao)
        if situacao != reg.situacao:
            reg.situacao = situacao
            Session.commit()
            return True
        return False

    def apaga(self, id_usuario: int, id_profissao: int) -> bool:
        reg = self.obtem(id_usuario, id_profissao)
        if reg:
            Session.delete(reg)
            Session.commit()
            return True
        return False


if __name__ == "__main__":
    profissoes = UsuariosProfissoes()
    print ("Usuario 4")
    print("busca todos", profissoes.busca_profissoes(4, False))
    print("busca ativos", profissoes.busca_profissoes(4))
    print (profissoes.cadastra(4, 1))
    print("cadastra 1", profissoes.busca_profissoes(4))
    print (profissoes.cadastra(4, 2))
    print (profissoes.cadastra(4, 2))
    print("cadastra 2", profissoes.busca_profissoes(4))
    profissoes.altera(4,1, "S")
    print("altera 1", profissoes.busca_profissoes(4, False))
    print("busca ativos", profissoes.busca_profissoes(4))
    profissoes.apaga(4,1)
    print("apaga 1", profissoes.busca_profissoes(4, False))
    profissoes.apaga(4,2)
    print("apaga 1", profissoes.busca_profissoes(4, False))
