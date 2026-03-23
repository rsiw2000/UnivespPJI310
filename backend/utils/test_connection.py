from sqlalchemy import create_engine
from sqlalchemy.pool import NullPool
import os


# Fetch variables
USER = os.getenv("user")
PASSWORD = os.getenv("password")
HOST = os.getenv("host")
PORT = os.getenv("port")
DBNAME = os.getenv("dbname")

# Construct the SQLAlchemy connection string
#DATABASE_URL = f"postgresql+psycopg2://{USER}:{PASSWORD}@{HOST}:{PORT}/{DBNAME}?sslmode=require"
#DATABASE_URL = "postgresql+psycopg2://postgres.mjegocvpgzfpikvusdlj:iTrPpDVSxOsOIPD7@aws-1-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require"
DATABASE_URL = 'postgresql+psycopg2://postgres:adm99Beyond@localhost/PI1'   # , connect_args={'options': '-csearch_path={}'.format('v0')}

# Create the SQLAlchemy engine
engine = create_engine(DATABASE_URL, poolclass=NullPool)
# If using Transaction Pooler or Session Pooler, we want to ensure we disable SQLAlchemy client side pooling -
# https://docs.sqlalchemy.org/en/20/core/pooling.html#switching-pool-implementations
# engine = create_engine(DATABASE_URL, poolclass=NullPool)

# Test the connection
try:
    with engine.connect() as connection:
        print("Connection successful!")
except Exception as e:
    print(f"Failed to connect: {e}")