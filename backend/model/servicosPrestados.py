from utils.baseModel import BaseTable
from statistics import mean as avg

from utils.database import Base, db_session as Session
from sqlalchemy import Column, Integer, String, DateTime, CHAR

class ServicoPrestado(BaseTable, Base):
    __tablename__ = 'servicos_prestados'
    id_servicos_prestados = Column(Integer, primary_key =True)
    id_usuario_cliente = Column(Integer)
    id_usuario_prestador = Column(Integer)
    id_profissao = Column(Integer)
    avaliacao = Column(CHAR)
    observacao = Column(String)
    data_avaliacao = Column(DateTime)
    def set(self, args):
        if len(args) >= 2:
            self.id_usuario_cliente = args[0]
            self.id_usuario_prestador = args[1]
            self.id_profissao = args[2]
            self.avaliacao = args[3]
            self.observacao = args[4]
            self.data_avaliacao = args[5]


class ServicosPrestados():
    def busca_prestador(self, id_usuario: int, id_profissao: int = None) -> list:
        dr = ServicoPrestado.query.filter(ServicoPrestado.id_usuario_prestador == id_usuario)
        return [(x.data_avaliacao, x.id_usuario_cliente, x.id_usuario_prestador, x.id_profissao, x.avaliacao, x.observacao)
                     for x in dr if (id_profissao is None or x.id_profissao == id_profissao)]

    def busca_cliente(self, id_usuario: int, id_profissao: int = None) -> list:
        dr = ServicoPrestado.query.filter(ServicoPrestado.id_usuario_cliente == id_usuario)
        return [(x.data_avaliacao, x.id_usuario_cliente, x.id_usuario_prestador, x.id_profissao, x.avaliacao, x.observacao) 
                    for x in dr if (id_profissao is None or x.id_profissao == id_profissao)]

    def cadastra(self, id_usuario_cliente: int, id_usuario_prestador: int, id_profissao: int, avaliacao: int, observacao: str, data_avaliacao: str) -> bool:
        servico = ServicoPrestado(id_usuario_cliente, id_usuario_prestador, id_profissao, avaliacao, observacao, data_avaliacao)
        Session.add(servico)
        Session.commit()
        return True

    def avaliacoes_prestador(self, id_usuario_prestador: int, profissoes: list) -> list:
        return {id_profissao: self.avaliacao_prestador(id_usuario_prestador, id_profissao) for id_profissao in profissoes}

    def avaliacao_prestador(self, id_usuario_prestador: int, id_profissao: int = None) -> int:
        dr = ServicoPrestado.query.filter(ServicoPrestado.id_usuario_prestador == id_usuario_prestador)
        pontos = [int(x.avaliacao) for x in dr 
                      if x.id_usuario_prestador == id_usuario_prestador and (id_profissao is None or x.id_profissao == id_profissao)]
        return round(avg(pontos)) if pontos else 0


if __name__ == "__main__":
    servicos = ServicosPrestados()
    #print ("avaliacao Prestador 50 (não existe) ->", servicos.avaliacao_prestador(50))
    print("---------------------------------------------")
    print("Servicos Prestador 4")
    for item in servicos.busca_prestador(4):
        print(f"{item[0]:%Y-%m-%d}", item[1:])
    print("Servicos Prestador 4 profissao 10")
    for item in servicos.busca_prestador(4, 10):
        print(f"{item[0]:%Y-%m-%d}", item[1:])
    print("Servicos Cliente 2")
    for item in servicos.busca_cliente(2):
        print(f"{item[0]:%Y-%m-%d}", item[1:])
    print ("avaliacao Prestador 4 ->", servicos.avaliacao_prestador(4))
    print("Cadastra Prestador 4 Cliente 2")
    reg = {"id_usuario_cliente":2,"id_usuario_prestador":4,"id_profissao":15,"avaliacao":3,"observacao":"servico prestado  NOVO - medio","data_avaliacao":"2025-05-17"}
    print(reg)
    servicos.cadastra(**reg)
    print("Servicos Cliente 2")
    for item in servicos.busca_cliente(2):
        print(f"{item[0]:%Y-%m-%d}", item[1:])
    print("Servicos Prestador 4")
    for item in servicos.busca_prestador(4):
        print(f"{item[0]:%Y-%m-%d}", item[1:])
    print ("avaliacao Prestador 4 ->", servicos.avaliacao_prestador(4))
    print("---------------------------------------------")
    print("Servicos Prestador 5")
    for item in servicos.busca_prestador(5):
        print(f"{item[0]:%Y-%m-%d}", item[1:])
    print("Servicos Cliente 4")
    for item in servicos.busca_cliente(4):
        print(f"{item[0]:%Y-%m-%d}", item[1:])
    print ("avaliacao Prestador 5 ->", servicos.avaliacao_prestador(5))
    print("Cadastra Prestador 5 Cliente 4")
    reg = (4,5,2,4,"servico prestado  NOVO - bom","2025-05-16")
    print(reg)
    servicos.cadastra(*reg)
    print("Servicos Prestador 5")
    for item in servicos.busca_prestador(5):
        print(f"{item[0]:%Y-%m-%d}", item[1:])
    print("Servicos Cliente 4")
    for item in servicos.busca_cliente(4):
        print(f"{item[0]:%Y-%m-%d}", item[1:])
    print ("avaliacao Prestador 5 ->", servicos.avaliacao_prestador(5))
    print ("avaliacao Prestador 4 profissao 16 -> ", servicos.avaliacao_prestador(4, 16))
    print ("avaliacao Prestador 6 profissao 16 -> ", servicos.avaliacao_prestador(6, 16))
