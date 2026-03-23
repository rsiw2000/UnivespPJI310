from utils.utils import Utils
from utils.baseModel import BaseTable

from utils.database import Base, db_session as Session
from sqlalchemy import Column, Integer, String


class Profissao(BaseTable, Base):
    __tablename__ = "profissoes"
    id_profissao = Column(Integer, primary_key=True)
    nome = Column(String)
    nome_busca = Column(String)
    def __init__(self, *args, **kwargs):
        super(Profissao, self).__init__(*args, **kwargs)
        if self.nome:
            self.nome_busca = Utils.normalize(self.nome)


class Profissoes():
    def lista(self) -> list:
        return Profissao.query.all()
        
    
    def busca(self, keyword) -> tuple:
        search = "%{}%".format(Utils.normalize(keyword))
        dr = Profissao.query.filter( Profissao.nome_busca.match(search))
        return [(x.id_profissao, x.nome) for x in dr]

    def obtem(self, id_profissao: int) -> Profissao:
        dr = Profissao.query.filter(Profissao.id_profissao == id_profissao)
        if dr.count() == 1:
            return dr.first()

    def get_nome(self, id_profissao: int) -> str:
        reg = self.obtem(id_profissao)
        if reg:
            return reg.nome

    def cadastra(self, nova: str) -> str:
        if not nova or not isinstance(nova, str) or not nova[0].isalpha():
            return "Nome da profissão inválido!"
        if self.busca(nova):
            return f"Profissão '{nova}' já está cadastrada!"
        profissao = Profissao(nova)
        Session.add(profissao)
        Session.commit()
        return profissao.id_profissao

    def apaga(self, id_profissao: int) -> bool:
        reg = self.obtem(id_profissao)
        if reg:
            Session.delete(reg)
            Session.commit()
            return True
        return False


def popula_tabela(profissoes):
    with open("./sql_scripts/popula-tabela-profissoes.sql", "w") as arq:
        for p in profissoes:
            line = f"INSERT INTO v0.profissoes (nome, nome_busca) VALUES ('{p.nome}', '{Utils.normalized_search(p.nome)}');\n"
            arq.write(line)
        arq.flush()


if __name__ == "__main__":
    profissoes = Profissoes()
    print(profissoes.lista())
    print(profissoes.busca("MECANICO"))
    id = profissoes.cadastra("PROFISSAO TESTE MECANICO")
    print(id)
    print(profissoes.busca("MECANICO"))
    print(profissoes.cadastra("PROFISSAO TESTE MECANICO"))
    print(profissoes.apaga(id))
    print(profissoes.busca("MECANICO"))
    print(profissoes.apaga(id))

