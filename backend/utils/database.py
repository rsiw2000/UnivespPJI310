from sqlalchemy import create_engine, text
from sqlalchemy.orm import scoped_session, sessionmaker, declarative_base
from sqlalchemy.pool import NullPool

#engine = create_engine(    'postgresql+psycopg2://postgres:240497@127.0.0.1:5432/PI1',    connect_args={'options': '-csearch_path={}'.format('v0')    })
engine = create_engine('postgresql+psycopg2://postgres:adm99Beyond@localhost/PI1',     connect_args={'options': '-csearch_path={}'.format('v0')    })
#engine = create_engine("postgresql+psycopg2://postgres.mjegocvpgzfpikvusdlj:iTrPpDVSxOsOIPD7@aws-1-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require", poolclass=NullPool)
'''engine = create_engine(
    "mysql+mysqldb://fmp:T+7vZ-Pk.J=n4]g@fmp.mysql.pythonanywhere-services.com/fmp$PJI240",
    pool_pre_ping=True,      # Verifica se conexão está ativa antes de usar
    pool_recycle=280,        # Reconecta a cada ~5 minutos (evita timeout MySQL)
    pool_size=5,             # Número de conexões no pool
    max_overflow=10,         # Conexões extras permitidas
    echo=False
)'''


db_session = scoped_session(sessionmaker(autocommit=False,
                                         autoflush=False,
                                         bind=engine
                                         ))
Base = declarative_base()
Base.query = db_session.query_property()

#db_session.execute(text("set session interactive_timeout=30000; set session wait_timeout=30000;"))
db_session.execute(text("SET idle_session_timeout = 30000;"))

'''
from sqlalchemy import Column, Integer, String, DateTime, BigInteger

from datetime import datetime
from baseModel import BaseTable

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
    data_inclusao = Column(DateTime, nullable=True)
    data_alteracao = Column(DateTime, nullable=True)

dr = Usuario.query.filter(Usuario.email == "23212332@aluno.univesp.br")
print (dr)
usuario = dr.first()
print (usuario.nome, usuario.bairro_busca)

id = usuario.id_usuario
usuario.bairro_busca = 'paiolgrandeXX'
db_session.commit()

dr = Usuario.query.filter(Usuario.id_usuario == id)
print (dr)
usuario = dr.first()
print (usuario.nome, usuario.bairro_busca)

user = Usuario( "TESTE3", "TELEFONE3", "22222222222222", "EMAIL3", 3549904, "SP", "bairro1", 
"U", "BAIRRO1", "47da8d45584ead5e9422de2b852cd53da6cb41a20f01383551c54a8c96f16615", datetime.now())
db_session.add(user)
db_session.commit()
'''